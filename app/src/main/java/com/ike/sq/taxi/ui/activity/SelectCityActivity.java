package com.ike.sq.taxi.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.ike.sq.taxi.R;
import com.ike.sq.taxi.adapter.CityAdapter;
import com.ike.sq.taxi.adapter.SearchedCityAdapter;
import com.ike.sq.taxi.base.view.BaseActivity;
import com.ike.sq.taxi.bean.City;
import com.ike.sq.taxi.db.DBManager;
import com.ike.sq.taxi.view.LetterBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 选择城市
 * Created by T-BayMax on 2017/5/15.
 */
public class SelectCityActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_search_clear)
    ImageView ivSearchClear;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.city_list)
    ListView lvCityList;
    @BindView(R.id.tv_selected)
    TextView tvSelected;
    @BindView(R.id.letterbar)
    LetterBar letterBar;
    @BindView(R.id.searched_city_list)
    ListView searchedCityList;
    private List<City> cities;
    private CityAdapter adapter;
    private SearchedCityAdapter searchedCityAdapter;

    private final static int REQUEST_CONTENT = 1;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        ButterKnife.bind(this);

        etSearch.addTextChangedListener(textWatcher);
        letterBar.setSelectedListener(selectedListener);

        // if (DBManager.copyDB(this)) {
        cities = DBManager.getAllCity();
        adapter = new CityAdapter(this, cities);
        // adapter.setAdapterListener(adapterListener);
        lvCityList.setAdapter(adapter);
        lvCityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SelectCityActivity.this, cities.get(position).getName(), Toast.LENGTH_SHORT).show();
                result(((City) cities.get(position)).getName());
            }
        });

        searchedCityAdapter = new SearchedCityAdapter(this);
        searchedCityList.setAdapter(searchedCityAdapter);
        searchedCityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SelectCityActivity.this, ((City) searchedCityAdapter.getItem(position)).getName(), Toast.LENGTH_SHORT).show();
                result(((City) searchedCityAdapter.getItem(position)).getName());
            }
        });
        adapter.setAdapterListener(new CityAdapter.AdapterListener() {
            @Override
            public void startPosition(String city) {
                Toast.makeText(SelectCityActivity.this, city, Toast.LENGTH_SHORT).show();
                result(city);
            }

            @Override
            public void hotCityClick(String city) {
                if (city.indexOf("定位") != -1) {
                    Toast.makeText(SelectCityActivity.this, "未获取当前位置，请选择城市", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Toast.makeText(SelectCityActivity.this, city, Toast.LENGTH_SHORT).show();
                result(city);

            }
        });
        //初始化定位
        initLocation();
        // }
    }

    /**
     * 选择城市
     *
     * @param city
     */
    private void result(String city) {
        Intent it = new Intent();
        it.putExtra("city", city);
        setResult(REQUEST_CONTENT, it);
        SelectCityActivity.this.finish();
    }


    //声明定位回调监听器


    /**
     * 点击事件
     */
    @OnClick({R.id.iv_back, R.id.iv_search_clear})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_search_clear:
                etSearch.setText("");
                break;
        }
    }


    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            ivSearchClear.setVisibility(View.VISIBLE);
            searchedCityList.setVisibility(View.VISIBLE);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            List<City> cities = DBManager.searchCity(s.toString());
            searchedCityAdapter.setCityList(cities);
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().equals("")) {
                ivSearchClear.setVisibility(View.GONE);
                searchedCityList.setVisibility(View.GONE);
            }
        }
    };

    private LetterBar.SelectedListener selectedListener = new LetterBar.SelectedListener() {
        @Override
        public void onSelected(String letter) {
            tvSelected.setText(letter);
            tvSelected.setVisibility(View.VISIBLE);
            lvCityList.setSelection(adapter.getLetterPosition(letter.toLowerCase()));
        }

        @Override
        public void onCancel() {
            tvSelected.setVisibility(View.GONE);
        }
    };

    private void initLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        //初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //获取最近3s内精度最高的一次定位结果：
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        mLocationOption.setNeedAddress(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
      /*  LatLng latLng1 = new LatLng(23.208844, 113.434757);
        LatLng latLng2 = new LatLng(23.208866592751, 113.43474801476);
        float distance = AMapUtils.calculateLineDistance(latLng1, latLng2);
        LatLng latLng3 = new LatLng(23.2013495214, 113.2669710244);
        LatLng latLng4 = new LatLng(23.1806057803, 113.3912658691);
        float distance2 = AMapUtils.calculateLineDistance(latLng1, latLng3);
        float distance3 = AMapUtils.calculateLineDistance(latLng1, latLng4);*/
    }

    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    adapter.setCurrentPosition(aMapLocation.getCity());
                } else {
                    adapter.setCurrentPosition("定位失败");
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("yue.huang", "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    };

}
