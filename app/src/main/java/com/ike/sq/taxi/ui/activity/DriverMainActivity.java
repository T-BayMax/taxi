package com.ike.sq.taxi.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Tip;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.ike.sq.taxi.R;
import com.ike.sq.taxi.adapter.PoiListAdapter;
import com.ike.sq.taxi.base.view.BaseMvpActivity;
import com.ike.sq.taxi.bean.DrivingRouteOverlay;
import com.ike.sq.taxi.interfaces.IDriverView;
import com.ike.sq.taxi.presenters.DriverPresenter;
import com.ike.sq.taxi.utils.DisplayUtils;
import com.ike.sq.taxi.utils.T;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 司机主界面
 * Created by T-BayMax on 2017/5/26.
 */

public class DriverMainActivity extends BaseMvpActivity<IDriverView, DriverPresenter> implements IDriverView,
        AMap.OnMyLocationChangeListener, Inputtips.InputtipsListener, RouteSearch.OnRouteSearchListener {


    @BindView(R.id.lt_main_title_left)
    TextView ltMainTitleLeft;
    @BindView(R.id.lt_main_title)
    TextView ltMainTitle;
    @BindView(R.id.lt_main_title_right)
    TextView ltMainTitleRight;
    @BindView(R.id.map)
    MapView map;
    @BindView(R.id.ll_depart)
    LinearLayout llDepart;
    @BindView(R.id.rl_grab_single)
    RelativeLayout rlGrabSingle;

    private TextView tvDriver;

    private PopupWindow mPopupWindow;
    private AMap aMap;
    //声明mLocationOption对象
    LocationSource.OnLocationChangedListener mListener;
    AMapLocationClient mlocationClient;
    AMapLocationClientOption mLocationOption;

    private String city;
    private PoiListAdapter mpoiadapter;
    private ListView mPoiSearchList;
    private RouteSearch routeSearch;
    private DriveRouteResult mDriveRouteResult;
    private int mode = RouteSearch.DrivingMultiStrategy;
    private Marker locationMarker;

    private LatLonPoint start_latPoint;//开始位置信息

    private LatLonPoint end_latPoint;//结束位置信息
    private LatLng latLng;

    private int status;//记录当前状态 0初始状态，1选择地址，2确认呼车

    private AlertDialog dialog;
    private AlertDialog haveOrderDialog;

    private float zoom = 18f;
    private int CITY_REQUEST_CODE = 1;
    Map<String, String> currentInfo = new HashMap<>();
    private boolean isGrabSingle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        map.onCreate(savedInstanceState);// 此方法必须重写
        iniView();
        iniMapData();
    }

    private void iniView() {

        View driverView = LayoutInflater.from(DriverMainActivity.this).inflate(R.layout.view_depart_driver, null);
        tvDriver = (TextView) driverView.findViewById(R.id.tv_driver);
        llDepart.addView(driverView);
        tvDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGrabSingle) {
                    isGrabSingle = false;

                    tvDriver.setText("查看附近呼车");
                    rlGrabSingle.setVisibility(View.GONE);
                } else {
                    isGrabSingle = true;
                    rlGrabSingle.setVisibility(View.VISIBLE);
                    tvDriver.setText("退出");
                }
            }
        });
    }

    private void iniMapData() {
        if (aMap == null) {
            aMap = map.getMap();
            aMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));
            // 设置定位监听
            aMap.setOnMyLocationChangeListener(this);
            // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            aMap.setMyLocationEnabled(true);
            // 设置定位的类型为定位模式，有定位、跟随或地图根据面向方向旋转几种
            MyLocationStyle myLocationStyle = new MyLocationStyle();
            myLocationStyle.strokeWidth(1);
            myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
            myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
            aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
            aMap.setMyLocationStyle(myLocationStyle);
            aMap.setTrafficEnabled(true);// 显示实时交通状况
            //地图模式可选类型：MAP_TYPE_NORMAL,MAP_TYPE_SATELLITE,MAP_TYPE_NIGHT
            aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 卫星地图模式
        }

        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(this);//设置驾车出行路线规划数据回调监听器
    }


    private void initMarker() {
        locationMarker = aMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 0.5f)

                .icon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.location_marker)))
                .position(latLng));
        locationMarker.showInfoWindow();

    }


    @OnClick({R.id.lt_main_title_left, R.id.lt_main_title_right, R.id.ll_depart})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lt_main_title_left:
                if (status == 2) {

                } else {
                    DriverMainActivity.this.finish();
                }
                break;
            case R.id.lt_main_title_right:
                initPopupWindow();
                break;
            case R.id.ll_depart:
                break;
        }
    }

    /**
     * 更多
     */
    void initPopupWindow() {
        int width = ltMainTitleRight.getWidth();
        int WidthPixels = DisplayUtils.getScreenWidthPixels(DriverMainActivity.this);
        if (null == mPopupWindow || !mPopupWindow.isShowing()) {
            LayoutInflater mLayoutInflater = (LayoutInflater) this
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
            View popwindow_more = mLayoutInflater.inflate(
                    R.layout.popwindow_more, null);
            mPopupWindow = new PopupWindow(popwindow_more, WidthPixels / 3, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            mPopupWindow.setTouchable(true);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
            mPopupWindow.showAsDropDown(ltMainTitleRight, -width, 30);
            TextView tv_information = (TextView) popwindow_more.findViewById(R.id.tv_information);
            TextView tv_my_share = (TextView) popwindow_more.findViewById(R.id.tv_my_share);

            tv_information.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Intent intent = new Intent(MainActivity.this, CommentMessageActivity.class);
                    startActivity(intent);*/
                    mPopupWindow.dismiss();
                }
            });
            tv_my_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   /* Intent intent = new Intent(MainActivity.this, MinShareActivity.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);*/
                    mPopupWindow.dismiss();
                }
            });
        }
    }
/*
    private void initGrabSingle() {
        if (null == grabSinglePopupWindow || !grabSinglePopupWindow.isShowing()) {
            LayoutInflater mLayoutInflater = (LayoutInflater) this
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
            View popwindow_more = mLayoutInflater.inflate(
                    R.layout.popwindow_grab_single, null);
            grabSinglePopupWindow = new PopupWindow(popwindow_more, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
            grabSinglePopupWindow.setTouchable(true);
            grabSinglePopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
            grabSinglePopupWindow.setOutsideTouchable(true);
            grabSinglePopupWindow.setFocusable(true);
            grabSinglePopupWindow.showAsDropDown(vHalving, 0, 47);
        }
    }*/

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        map.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        map.onDestroy();
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }


    @Override
    public void onMyLocationChange(Location location) {
        // 定位回调监听
        if (location != null) {
            Log.e("amap", "onMyLocationChange 定位成功， lat: " + location.getLatitude() + " lon: " + location.getLongitude());
            Bundle bundle = location.getExtras();
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            initMarker();
           /* locationMarker = aMap.addMarker(new MarkerOptions()
                    .anchor(0.5f, 0.5f)
                    .icon(BitmapDescriptorFactory
                            .fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.purple_pin)))
                    .position(latLng));
            locationMarker.showInfoWindow();*/

            if (bundle != null) {
                int errorCode = bundle.getInt(MyLocationStyle.ERROR_CODE);
                String errorInfo = bundle.getString(MyLocationStyle.ERROR_INFO);
                // 定位类型，可能为GPS WIFI等，具体可以参考官网的定位SDK介绍
                int locationType = bundle.getInt(MyLocationStyle.LOCATION_TYPE);
                start_latPoint = new LatLonPoint(location.getLatitude(), location.getLongitude());
                String[] args = location.toString().split("#");
                for (String arg : args) {
                    String[] data = arg.split("=");
                    if (data.length >= 2)
                        currentInfo.put(data[0], data[1]);
                }
                city = currentInfo.get("city");
                Log.e("amap", "定位信息， code: " + errorCode + " errorInfo: " + errorInfo + " locationType: " + locationType);
            } else {
                Log.e("amap", "定位信息， bundle is null ");

            }

        } else {
            Log.e("amap", "定位失败");
        }
    }


    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {

            mpoiadapter = new PoiListAdapter(this, tipList);
            mPoiSearchList.setAdapter(mpoiadapter);
        } else {
            T.showLong(DriverMainActivity.this, rCode);
        }
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int code) {
        aMap.clear();// 清理地图上的所有覆盖物
        if (code == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mDriveRouteResult = result;
                    for (int i = 0; i < mDriveRouteResult.getPaths().size(); i++) {
                        DrivePath drivePath = mDriveRouteResult.getPaths()
                                .get(i);
                        DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                                DriverMainActivity.this, aMap, drivePath,
                                mDriveRouteResult.getStartPos(),
                                mDriveRouteResult.getTargetPos(), null);
                        drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                        drivingRouteOverlay.removeFromMap();
                        drivingRouteOverlay.addToMap();
                        drivingRouteOverlay.zoomToSpan();
                    }

                } else if (result != null && result.getPaths() == null) {
                    T.showShort(DriverMainActivity.this, "对不起，没有搜索到相关数据！");
                }

            } else {
                T.showShort(DriverMainActivity.this, "对不起，没有搜索到相关数据！");
            }
        } else {
            T.showLong(this.getApplicationContext(), code);
        }
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == CITY_REQUEST_CODE) {
            city = data.getStringExtra("city");
            //et_city.setText(city);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            switch (status) {
                case 0:
                    DriverMainActivity.this.finish();
                    break;
                case 1:
                    status = 0;

                    break;
                case 2:

                    break;
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showError(String errorString) {

    }

    @Override
    public DriverPresenter initPresenter() {
        return new DriverPresenter();
    }
}
