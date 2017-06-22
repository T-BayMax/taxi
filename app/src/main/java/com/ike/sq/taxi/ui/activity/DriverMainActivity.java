package com.ike.sq.taxi.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.amap.api.maps.UiSettings;
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
import com.andview.refreshview.XRefreshView;
import com.ike.sq.taxi.App;
import com.ike.sq.taxi.R;
import com.ike.sq.taxi.adapter.GrabSingleAdapter;
import com.ike.sq.taxi.adapter.PoiListAdapter;
import com.ike.sq.taxi.base.view.BaseMvpActivity;
import com.ike.sq.taxi.bean.AroundOrder;
import com.ike.sq.taxi.bean.DrivingRouteOverlay;
import com.ike.sq.taxi.interfaces.IDriverView;
import com.ike.sq.taxi.presenters.DriverPresenter;
import com.ike.sq.taxi.utils.DisplayUtils;
import com.ike.sq.taxi.utils.T;
import com.ike.sq.taxi.view.CustomerFooter;
import com.umeng.message.entity.UMessage;

import java.util.ArrayList;
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
    @BindView(R.id.v_halving)
    View vHalving;
    @BindView(R.id.iv_pull_down)
    ImageView ivPullDown;
    @BindView(R.id.rv_grab_single)
    RecyclerView rvGrabSingle;
    @BindView(R.id.xrefreshview)
    XRefreshView xRefreshView;

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

    private UiSettings mUiSettings;//手势选项

    private LatLonPoint start_latPoint;//开始位置信息

    private LatLonPoint end_latPoint;//结束位置信息
    private LatLng latLng;
    private int page = 1;

    private int status;//记录当前状态 0初始状态，1查看订单，2前往接乘车，3接到乘客，5到达目的地，6确认收款，7评价

    private AlertDialog dialog;
    private AlertDialog haveOrderDialog;
    List<AroundOrder> list;
    private GrabSingleAdapter adapter;

    private float zoom = 18;
    private int CITY_REQUEST_CODE = 1;
    Map<String, String> currentInfo = new HashMap<>();
    private boolean isGrabSingle;
    private AroundOrder order;

    private String address;
    private String userId = "110";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);
        ButterKnife.bind(this);
        map.onCreate(savedInstanceState);// 此方法必须重写
        iniView();
        iniMapData();
        App.activityMap.put("MainActivity", DriverMainActivity.this);
        App.setOnMsgListener(new App.OnMsgListener() {
            @Override
            public void onMsg(UMessage msg) {

            }

            @Override
            public void onDriverMsg(UMessage msg) {
                T.showLong(DriverMainActivity.this, msg.title);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        // haveOrder();
                    }
                });

            }
        });
    }

    LinearLayoutManager layoutManager;

    private void iniView() {
        rvGrabSingle.setHasFixedSize(true);
        // 设置静默加载模式
        layoutManager = new LinearLayoutManager(this);
        rvGrabSingle.setLayoutManager(layoutManager);
        list = new ArrayList<AroundOrder>(0);
        adapter = new GrabSingleAdapter(list, DriverMainActivity.this);
        rvGrabSingle.setAdapter(adapter);

        xRefreshView.setPinnedTime(500);
        xRefreshView.setPullLoadEnable(true);
        xRefreshView.setAutoLoadMore(false);
        xRefreshView.setMoveForHorizontal(true);
        xRefreshView.enableRecyclerViewPullUp(true);
        xRefreshView.enableReleaseToLoadMore(true);
        xRefreshView.enablePullUpWhenLoadCompleted(true);


        View driverView = LayoutInflater.from(DriverMainActivity.this).inflate(R.layout.view_depart_driver, null);
        tvDriver = (TextView) driverView.findViewById(R.id.tv_driver);
        llDepart.addView(driverView);
        tvDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (status) {
                    case 0:
                        isGrabSingle = true;
                        uiSettings();
                        rlGrabSingle.setVisibility(View.VISIBLE);
                        tvDriver.setText("退出");
                        selectOrder();
                        status = 1;
                        break;
                    case 1:
                        isGrabSingle = false;
                        uiSettings();
                        tvDriver.setText("查看附近呼车");
                        rlGrabSingle.setVisibility(View.GONE);
                        status = 0;
                        break;
                    case 2:
                        takeOrder();
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                    case 5:
                        break;
                }

            }
        });
        xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {

            @Override
            public void onRefresh(boolean isPullDown) {
                page = 1;
                selectOrder();
            }

            @Override
            public void onLoadMore(boolean isSilence) {

                page++;
                /*initData();*/
            }
        });
        adapter.setOnItemClickListener(new GrabSingleAdapter.OnItemClickListener() {
            @Override
            public void onGrabSingleClick(GrabSingleAdapter.ViewHolder viewHolder, AroundOrder o) {
                order = o;
                robOrder();
            }
        });
    }

    /**
     * 查看附件订单
     */
    private void selectOrder() {
        Map<String, String> formData = new HashMap<String, String>(0);
        formData.put("userId", userId);
        presenter.getAroundOrder(formData);
    }

    /**
     * 接单
     */
    private void robOrder() {
        showPD("正在抢单，请稍后！");
        Map<String, String> formData = new HashMap<String, String>(0);
        formData.put("userId", userId);
        formData.put("orderId", order.getId());
        presenter.driverRobOrder(formData);
    }

    /**
     * 接到乘客
     */
    private void takeOrder() {
        Map<String, String> formData = new HashMap<String, String>(0);
        formData.put("userId", userId);
        formData.put("orderId", order.getId());
        presenter.takeUser(formData);
    }
    /**
     * 司机到达目的地结算
     */
    private void driverArriveAddress(){
        Map<String, String> formData = new HashMap<String, String>(0);
        formData.put("userId", userId);
        formData.put("orderId", order.getId());
        formData.put("kilometre",order.getKilometre()+"");
        formData.put("money",order.getMoney()+"");
        presenter.driverArriveAddress(formData);
    }


    private void iniMapData() {
        if (aMap == null) {
            aMap = map.getMap();
            mUiSettings = aMap.getUiSettings();
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
            uiSettings();
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

    /**
     * 位置录入
     */
    private void positionInput() {
        Map<String, String> formData = new HashMap<>(0);
        formData.put("userId", userId);
        formData.put("address", address);
        formData.put("longitude", latLng.longitude + "");
        formData.put("latitude", latLng.latitude + "");
        formData.put("type", "1");//0用户，1司机
        presenter.positionInputPresenter(formData);
    }

    /**
     * 设置地图是否可操作
     */
    private void uiSettings() {
        boolean isSetting = !isGrabSingle;
        mUiSettings.setScrollGesturesEnabled(isSetting);
        mUiSettings.setZoomGesturesEnabled(isSetting);
        mUiSettings.setTiltGesturesEnabled(isSetting);
        mUiSettings.setRotateGesturesEnabled(isSetting);
    }

    /**
     * 已接单
     */
    private void driverOrder() {
        tvDriver.setText("接到乘客");
        rlGrabSingle.setVisibility(View.GONE);
    }

    /**
     * 已上车
     */
    private void haveGotOnBus() {
        tvDriver.setText("到达目的地");
        rlGrabSingle.setVisibility(View.GONE);
    } /**
     * 已到达目的地
     */
    private void reachedDestination() {
        tvDriver.setText("发起收款");
        rlGrabSingle.setVisibility(View.GONE);
    }

    @OnClick({R.id.lt_main_title_left, R.id.lt_main_title_right, R.id.ll_depart})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lt_main_title_left:
                if (status == 0) {
                    DriverMainActivity.this.finish();
                } else {

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
                address = currentInfo.get("address");
                city = currentInfo.get("city");
                positionInput();
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

    ProgressDialog pd;

    private void showPD(String data) {
        pd = new ProgressDialog(DriverMainActivity.this);
        pd.setMessage(data);
        pd.show();
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pd.dismiss();
            }
        }.start();
    }

    @Override
    public void showError(String errorString) {
        //showPD(errorString);
        if (xRefreshView.mPullRefreshing) {
            xRefreshView.stopRefresh(true);
        }
    }

    @Override
    public DriverPresenter initPresenter() {
        return new DriverPresenter();
    }

    @Override
    public void selectAroundOrder(List<AroundOrder> data) {//查看周围订单成功
        if (xRefreshView.mPullRefreshing) {
            xRefreshView.stopRefresh(true);
        }
        if (null != data) {
            list = data;
            adapter.setData(list, page);
        }
    }

    @Override
    public void driverRobOrder(String data) {//接单成功
        showPD(data);
        driverOrder();
        status = 2;
    }

    @Override
    public void driverTakeUser(String data) {
        T.showLong(DriverMainActivity.this, "已接到乘客");
        haveGotOnBus();
        status = 3;
    }

    @Override
    public void arrivingDestinationView(AroundOrder data) {
        status = 4;
        reachedDestination();
    }

    @Override
    public void positionInputView(String data) {//上传位置成功

    }

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

}
