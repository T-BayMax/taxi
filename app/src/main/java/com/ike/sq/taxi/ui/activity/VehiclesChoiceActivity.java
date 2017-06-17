package com.ike.sq.taxi.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ike.sq.taxi.R;
import com.ike.sq.taxi.adapter.CarColorAdapter;
import com.ike.sq.taxi.adapter.CarSonAdapter;
import com.ike.sq.taxi.adapter.HeaderCarBrandAdapter;
import com.ike.sq.taxi.adapter.HotAdapter;
import com.ike.sq.taxi.adapter.SortAdapter;
import com.ike.sq.taxi.base.view.BaseMvpActivity;
import com.ike.sq.taxi.bean.CarBrandBean;
import com.ike.sq.taxi.bean.CarColorBean;
import com.ike.sq.taxi.bean.CarSonBean;
import com.ike.sq.taxi.bean.City;
import com.ike.sq.taxi.db.DBManager;
import com.ike.sq.taxi.interfaces.IVehiclesChoice;
import com.ike.sq.taxi.presenters.VehiclesChoicePresenters;
import com.ike.sq.taxi.utils.CharacterParser;
import com.ike.sq.taxi.utils.DisplayUtils;
import com.ike.sq.taxi.utils.PinyinComparator;
import com.ike.sq.taxi.utils.T;
import com.ike.sq.taxi.view.LetterBar;
import com.ike.sq.taxi.view.WrapHeightGridView;
import com.zhy.autolayout.attr.AutoAttr;
import com.zhy.autolayout.utils.AutoUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 选择车型，颜色
 * Created by T-BayMax on 2017/5/15.
 */

public class VehiclesChoiceActivity extends BaseMvpActivity<IVehiclesChoice, VehiclesChoicePresenters> implements IVehiclesChoice {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.iv_search_clear)
    ImageView ivSearchClear;
    @BindView(R.id.city_list)
    ListView cityList;
    @BindView(R.id.letterbar)
    LetterBar letterbar;
    @BindView(R.id.tv_selected)
    TextView tvSelected;
    @BindView(R.id.searched_city_list)
    ListView searchedCityList;
    @BindView(R.id.activity_main)
    LinearLayout activityMain;

    //显示字母的TextView
    private TextView dialog;
    private SortAdapter adapter;
    private HeaderCarBrandAdapter headerAdapter;
    //  private ClearEditText mClearEditText;

    //汉字转换成拼音的类
    private CharacterParser characterParser;
    private List<CarBrandBean> SourceDateList;
    private List<CarBrandBean> hotSourceDateList;
    private List<String> hotCar;
    private List<CarColorBean> carColorList;

    //根据拼音来排列ListView里面的数据类
    private PinyinComparator pinyinComparator;


    private PopupWindow mPopupWindow;
    private PopupWindow colorPopupWindow;
    private StringBuilder car;
    private final static int REQUEST_CONTENT=2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_city);
        ButterKnife.bind(this);
        initView();
        presenter.getBrand("1dd376c76ac19");
    }

    private void initView() {
        etSearch.setHint("输入你的车型");
        letterbar.letters = new String[]{"热门", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};
//实例化汉字转拼音类
        characterParser = CharacterParser.getInstance();

        pinyinComparator = new PinyinComparator();

        cityList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //这里要利用adapter.getItem(position)来获取当前position所对应的对象
                CarBrandBean carBrandBean = ((CarBrandBean) adapter.getItem(position - 1));
                car = new StringBuilder();
                T.showLong(getApplication(), carBrandBean.getName());
                initPopupWindow(carBrandBean.getSon());
                car.append(carBrandBean.getName());
                view.setBackgroundColor(ContextCompat.getColor(VehiclesChoiceActivity.this, R.color.colorAccent));
            }
        });
        SourceDateList = new ArrayList<>(0);

        // 根据a-z进行排序源数据
        Collections.sort(SourceDateList, pinyinComparator);
        letterbar.setSelectedListener(selectedListener);
        etSearch.addTextChangedListener(textWatcher);
        View view = LayoutInflater.from(VehiclesChoiceActivity.this).inflate(R.layout.layout_item2, null);
        WrapHeightGridView gridView = (WrapHeightGridView) view.findViewById(R.id.hot_list);
        TextView tv_hot = (TextView) view.findViewById(R.id.tv_hot);
        tv_hot.setText("热门车型");
        hotCar = new ArrayList<>(0);
        hotCar.add("大众");
        hotCar.add("丰田");
        hotCar.add("北京现代");
        hotCar.add("东风日产");
        hotCar.add("雪佛兰");
        hotSourceDateList = new ArrayList<>(0);
        headerAdapter = new HeaderCarBrandAdapter(VehiclesChoiceActivity.this, hotSourceDateList);
        gridView.setAdapter(headerAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CarBrandBean carBrandBean = ((CarBrandBean) headerAdapter.getItem(position));
                car = new StringBuilder();
                car.append(carBrandBean.getName());
                initPopupWindow(carBrandBean.getSon());
                view.setBackgroundColor(ContextCompat.getColor(VehiclesChoiceActivity.this, R.color.colorAccent));
            }
        });
        cityList.addHeaderView(view);
        adapter = new SortAdapter(this, SourceDateList);
        cityList.setAdapter(adapter);
    }


    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showError(String errorString) {
        T.showLong(VehiclesChoiceActivity.this, errorString);
    }

    @Override
    public void brandView(List<CarBrandBean> data) {
        SourceDateList = filledData(data);
        adapter.updateListView(SourceDateList);
        adapter.notifyDataSetChanged();
        headerAdapter.notifyDataSetChanged();
    }

    @Override
    public void informationView(List<CarSonBean> data) {

    }

    @Override
    public VehiclesChoicePresenters initPresenter() {
        return new VehiclesChoicePresenters();
    }

    @OnClick({R.id.iv_back, R.id.iv_search_clear})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                VehiclesChoiceActivity.this.finish();
                break;
            case R.id.iv_search_clear:
                break;
        }
    }

    private LetterBar.SelectedListener selectedListener = new LetterBar.SelectedListener() {
        @Override
        public void onSelected(String letter) {
            //该字母首次出现的位置
            int position = adapter.getPositionForSection(letter.charAt(0));
            if (position != -1) {
                cityList.setSelection(position);
            }
        }

        @Override
        public void onCancel() {
            tvSelected.setVisibility(View.GONE);
        }
    };

    /**
     * 为ListView填充数据
     *
     * @param date
     * @return
     */
    private List<CarBrandBean> filledData(List<CarBrandBean> date) {
        List<CarBrandBean> mSortList = new ArrayList<CarBrandBean>();

        for (int i = 0; i < date.size(); i++) {
            CarBrandBean model = date.get(i);
            //汉字转换成拼音
            String pinyin = characterParser.getSelling(model.getName());
            String sortString = pinyin.substring(0, 1).toUpperCase();
            // 正则表达式，判断首字母是否是英文字母
            if (sortString.matches("[A-Z]")) {
                model.setSortLetters(sortString.toUpperCase());
            } else {
                model.setSortLetters("#");
            }
            for (int j = 0; j < hotCar.size(); j++) {
                if (hotCar.get(j).equals(model.getName())) {
                    CarBrandBean carModel = model;
                    carModel.setSortLetters("热门");
                    hotSourceDateList.add(carModel);
                }
            }

            mSortList.add(model);
        }
        return mSortList;

    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
            filterData(s.toString());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        List<CarBrandBean> filterDateList = new ArrayList<CarBrandBean>();

        if (TextUtils.isEmpty(filterStr)) {
            filterDateList = SourceDateList;
        } else {
            filterDateList.clear();
            for (CarBrandBean sortModel : SourceDateList) {
                String name = sortModel.getName();
                if (name.toUpperCase().indexOf(
                        filterStr.toString().toUpperCase()) != -1
                        || characterParser.getSelling(name).toUpperCase()
                        .startsWith(filterStr.toString().toUpperCase())) {
                    filterDateList.add(sortModel);
                }
            }
        }

        // 根据a-z进行排序
        Collections.sort(filterDateList, pinyinComparator);
        adapter.updateListView(filterDateList);
    }

    /**
     * 车型弹窗
     */
    private void initPopupWindow(List<CarSonBean> list) {
        int width = 280;
        int margin = 360;
        if (null == mPopupWindow || !mPopupWindow.isShowing()) {
            LayoutInflater mLayoutInflater = (LayoutInflater) this
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = mLayoutInflater.inflate(
                    R.layout.view_car_son_list, null);
            ListView listView = (ListView) view.findViewById(R.id.lv_car_son);
            AutoUtils.autoSize(view, AutoAttr.BASE_HEIGHT);

            mPopupWindow = new PopupWindow(view, width, ViewGroup.LayoutParams.MATCH_PARENT, true);
            mPopupWindow.setTouchable(true);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
            mPopupWindow.showAsDropDown(ivSearchClear, -margin, 31);

            final CarSonAdapter carSonAdapter = new CarSonAdapter(VehiclesChoiceActivity.this, list);
            listView.setAdapter(carSonAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    initCarColor();
                    view.setBackgroundColor(ContextCompat.getColor(VehiclesChoiceActivity.this, R.color.colorAccent));
                    CarSonBean carSonBean = (CarSonBean) carSonAdapter.getItem(position);
                    car.append("  " + carSonBean.getType());
                }
            });
        }
    }

    private void initCarColor() {
        carColorList = new ArrayList<>(0);
        carColorList.add(new CarColorBean(R.mipmap.car_color_black, "黑"));
        carColorList.add(new CarColorBean(R.mipmap.car_color_white, "白"));
        carColorList.add(new CarColorBean(R.mipmap.car_color_silver, "银"));
        carColorList.add(new CarColorBean(R.mipmap.car_color_red, "红"));
        carColorList.add(new CarColorBean(R.mipmap.car_color_orange, "橙"));
        carColorList.add(new CarColorBean(R.mipmap.car_color_yellow, "黄"));
        carColorList.add(new CarColorBean(R.mipmap.car_color_green, "绿"));
        carColorList.add(new CarColorBean(R.mipmap.car_color_blue, "蓝"));
        carColorList.add(new CarColorBean(R.mipmap.car_color_indigo, "靛"));
        carColorList.add(new CarColorBean(R.mipmap.car_color_violet, "紫"));
        carColorList.add(new CarColorBean(R.mipmap.car_color_brown, "棕"));
        carColorList.add(new CarColorBean(R.mipmap.car_color_pink, "粉"));
        initCarColorPopupWindow();
    }

    /**
     * 颜色弹窗
     */
    private void initCarColorPopupWindow() {
        int width = 200;
        int margin = 100;
        if (null == colorPopupWindow || !colorPopupWindow.isShowing()) {
            LayoutInflater mLayoutInflater = (LayoutInflater) this
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
            View view = mLayoutInflater.inflate(
                    R.layout.view_car_color_list, null);
            ListView listView = (ListView) view.findViewById(R.id.lv_car_color);
            AutoUtils.autoSize(view, AutoAttr.BASE_HEIGHT);

            colorPopupWindow = new PopupWindow(view, width, ViewGroup.LayoutParams.MATCH_PARENT, true);
            colorPopupWindow.setTouchable(true);
            colorPopupWindow.setOutsideTouchable(true);
            colorPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
            colorPopupWindow.showAsDropDown(ivSearchClear, -margin, 31);

            CarColorAdapter carSonAdapter = new CarColorAdapter(VehiclesChoiceActivity.this, carColorList);
            listView.setAdapter(carSonAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    CarColorBean carColorBean=carColorList.get(position);
                    car.append("  "+carColorBean.getColorName());
                    Intent it = new Intent();
                    it.putExtra("car", car.toString());
                    setResult(REQUEST_CONTENT, it);
                    mPopupWindow.dismiss();
                    colorPopupWindow.dismiss();
                    VehiclesChoiceActivity.this.finish();
                }
            });
        }
    }
}
