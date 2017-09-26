package com.ike.sq.taxi.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.enums.AimLessMode;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
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
import com.autonavi.tbt.TrafficFacilityInfo;
import com.ike.sq.lib.pushlibrary.listeners.PushListener;
import com.ike.sq.taxi.App;
import com.ike.sq.taxi.MainActivity;
import com.ike.sq.taxi.R;
import com.ike.sq.taxi.adapter.GrabSingleAdapter;
import com.ike.sq.taxi.adapter.PoiListAdapter;
import com.ike.sq.taxi.base.view.BaseMvpActivity;
import com.ike.sq.taxi.bean.AroundOrder;
import com.ike.sq.taxi.bean.DrivingRouteOverlay;
import com.ike.sq.taxi.interfaces.IDriverView;
import com.ike.sq.taxi.network.HttpUtils;
import com.ike.sq.taxi.presenters.DriverPresenter;
import com.ike.sq.taxi.utils.CircleTransform;
import com.ike.sq.taxi.utils.DisplayUtils;
import com.ike.sq.taxi.utils.T;
import com.ike.sq.taxi.utils.TTSController;
import com.ike.sq.taxi.view.CustomerFooter;
import com.qihoo360.replugin.RePlugin;
import com.squareup.picasso.Picasso;
import com.umeng.message.entity.UMessage;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.OnShowRationale;
import permissions.dispatcher.PermissionRequest;
import permissions.dispatcher.RuntimePermissions;

/**
 * 司机主界面
 * Created by T-BayMax on 2017/5/26.
 */

@RuntimePermissions
public class DriverMainActivity extends BaseMvpActivity<IDriverView, DriverPresenter> implements IDriverView,
        AMap.OnMyLocationChangeListener, Inputtips.InputtipsListener, RouteSearch.OnRouteSearchListener, AMapNaviListener {


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
    @BindView(R.id.ll_call)
    LinearLayout llCall;
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

    private boolean isFistLocation = true;
    private LatLonPoint start_latPoint;//开始位置信息

    private LatLonPoint end_latPoint;//结束位置信息
    private LatLng latLng;
    private int page = 1;

    private int status;//记录当前状态 0初始状态，1查看订单，2前往接乘车，3接到乘客，5到达目的地，6确认收款，7评价

    private AlertDialog dialog;
    private AlertDialog haveOrderDialog;
    List<AroundOrder> list;
    private GrabSingleAdapter adapter;

    private float zoom = 18f;
    private int CITY_REQUEST_CODE = 1;
    Map<String, String> currentInfo = new HashMap<>(0);
    private boolean isGrabSingle;
    private AroundOrder order;

    MyLocationStyle myLocationStyle;


    // 是否需要跟随定位
    private boolean isNeedFollow = true;

    // 处理静止后跟随的timer
    private Timer needFollowTimer;

    // 屏幕静止DELAY_TIME之后，再次跟随
    private long DELAY_TIME = 5000;
    private AMapNavi aMapNavi;
    private TTSController ttsManager;

    private String address;
    private String userId = "13824692192";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);
        ButterKnife.bind(this);
        map.onCreate(savedInstanceState);// 此方法必须重写
        iniView();
        iniMapData();
        selectUseOrder();
        App.activityMap.put("MainActivity", DriverMainActivity.this);
       /* App.setOnMsgListener(new App.OnMsgListener() {
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
        });*/
        ClassLoader cl = RePlugin.getHostClassLoader();
        if (cl == null) {
            Toast.makeText(DriverMainActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Class clz = cl.loadClass("com.ike.communityalliance.App");
            Method m = clz.getDeclaredMethod("setPushListener", Context.class, PushListener.class);
            m.invoke(null, DriverMainActivity.this, new PushListener(){

                @Override
                public void onMsg(UMessage msg) {
                    T.showLong(DriverMainActivity.this, msg.title);
                    switch (msg.title) {

                    }
                }

                @Override
                public void onDriverMsg(UMessage uMessage) {

                }
            });
        } catch (Exception e) {
            // 有可能Demo2根本没有这个类，也有可能没有相应方法（通常出现在"插件版本升级"的情况）
            Toast.makeText(DriverMainActivity.this, "", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    LinearLayoutManager layoutManager;

    private void iniView() {

        userId = getIntent().getStringExtra("loginid");
        if (null!=userId){
            App.checkVip = Integer.parseInt(getIntent().getStringExtra("checkVip"));
        }else {
            userId="13824692192";
        }
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
                        //uiSettings();
                        rlGrabSingle.setVisibility(View.VISIBLE);
                        tvDriver.setText("退出");
                        status = 1;
                        //定时器//用于定时刷新列表
                        TimerTask task = new TimerTask() {
                            public void run() {
                                selectOrder();
                            }
                        };
                        timer = new Timer();
                        timer.schedule(task, 0, 10000); // 10s后执行task,经过10s再次执行
                        break;
                    case 1:
                        grabSingle();
                        break;
                    case 2:
                        takeOrder();
                        break;
                    case 3:
                        //driverArriveAddress();
                        reachedDestination();
                        break;
                    case 4:
                        break;
                    case 5:/*
                        Intent intent = new Intent(DriverMainActivity.this, CollectionActivity.class);

                        startActivity(intent);*/
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

                //  page++;
                /*initData();*/
            }
        });
        adapter.setOnItemClickListener(new GrabSingleAdapter.OnItemClickListener() {
            @Override
            public void onGrabSingleClick(GrabSingleAdapter.ViewHolder viewHolder, AroundOrder o) {
                order = o;
                status = 2;
                robOrder();
            }
        });
    }

    /**
     * 退出查看附件的订单
     */
    private void grabSingle() {
        isGrabSingle = false;
        // uiSettings();
        tvDriver.setText("查看附近呼车");
        rlGrabSingle.setVisibility(View.GONE);
        status = 0;
        timer.cancel();
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
        pd = new ProgressDialog(DriverMainActivity.this);
        pd.setMessage("正在抢单，请稍后！");
        pd.show();
        Map<String, String> formData = new HashMap<String, String>(0);
        formData.put("userId", userId);
        formData.put("orderId", null != order.getId() ? order.getId() : order.getOrderId());
        formData.put("key", App.deviceToken);
        presenter.driverRobOrder(formData);
    }

    /**
     * 接到乘客
     */
    private void takeOrder() {
        pd = new ProgressDialog(DriverMainActivity.this);
        pd.setMessage("正在提交数据，请稍后！");
        pd.show();
        Map<String, String> formData = new HashMap<String, String>(0);
        formData.put("userId", userId);
        formData.put("orderId", null != order.getId() ? order.getId() : order.getOrderId());
        presenter.takeUser(formData);
    }

    /**
     * 司机到达目的地结算
     */
    private void driverArriveAddress() {
        Map<String, String> formData = new HashMap<String, String>(0);
        formData.put("userId", userId);
        formData.put("orderId", null != order.getId() ? order.getId() : order.getOrderId());
        formData.put("kilometre", order.getKilometre() + "");
        formData.put("money", order.getMoney() + "");
        presenter.driverArriveAddress(formData);
    }

    private void iniMapData() {
       /* if (ContextCompat.checkSelfPermission(this, Manifest.permission.LOCATION_HARDWARE) != PackageManager.PERMISSION_GRANTED) {
            DriverMainActivityPermissionsDispatcher.locationNeedsWithCheck(this);
        }*/
        if (aMap == null) {
            aMap = map.getMap();
            aMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));
            // 设置定位监听
            aMap.setOnMyLocationChangeListener(this);
            // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            aMap.setMyLocationEnabled(true);

            myLocationStyle = new MyLocationStyle();
            myLocationStyle.strokeWidth(1);
            myLocationStyle.interval(5000);
            myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
            myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
            aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示

            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);
            aMap.setMyLocationStyle(myLocationStyle);
            aMap.setTrafficEnabled(true);// 显示实时交通状况
            //地图模式可选类型：MAP_TYPE_NORMAL,MAP_TYPE_SATELLITE,MAP_TYPE_NIGHT
            aMap.setMapType(AMap.MAP_TYPE_NORMAL);
            mUiSettings = aMap.getUiSettings();
            mUiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
            aMapNavi = AMapNavi.getInstance(this);
            aMapNavi.startAimlessMode(AimLessMode.CAMERA_AND_SPECIALROAD_DETECTED);

            ttsManager = TTSController.getInstance(this);
            ttsManager.init();

            aMapNavi.addAMapNaviListener(this);
            aMapNavi.addAMapNaviListener(ttsManager);

            setMapInteractiveListener();
        }

        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(this);//设置驾车出行路线规划数据回调监听器
    }

    /**
     * 设置导航监听
     */
    private void setMapInteractiveListener() {

        aMap.setOnMapTouchListener(new AMap.OnMapTouchListener() {

            @Override
            public void onTouch(MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 按下屏幕
                        // 如果timer在执行，关掉它
                        clearTimer();
                        // 改变跟随状态
                        isNeedFollow = false;
                        break;

                    case MotionEvent.ACTION_UP:
                        // 离开屏幕
                        startTimerSomeTimeLater();
                        break;

                    default:
                        break;
                }
            }
        });

    }

    /**
     * 取消timer任务
     */
    private void clearTimer() {
        if (needFollowTimer != null) {
            needFollowTimer.cancel();
            needFollowTimer = null;
        }
    }

    /**
     * 如果地图在静止的情况下
     */
    private void startTimerSomeTimeLater() {
        // 首先关闭上一个timer
        clearTimer();
        needFollowTimer = new Timer();
        // 开启一个延时任务，改变跟随状态
        needFollowTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                isNeedFollow = true;
            }
        }, DELAY_TIME);
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
        if (null != userId && null != address && null != latLng) {
            Map<String, String> formData = new HashMap<>(0);
            formData.put("userId", userId);
            formData.put("address", address);
            formData.put("longitude", latLng.longitude + "");
            formData.put("latitude", latLng.latitude + "");
            formData.put("type", "1");//0用户，1司机
            presenter.positionInputPresenter(formData);
        }
    }

    /**
     * 设置地图是否可操作
     */
    private void uiSettings() {
        boolean isSetting = !isGrabSingle;
        if (null == mUiSettings) {
            return;
        }
        mUiSettings.setScrollGesturesEnabled(isSetting);
        mUiSettings.setZoomGesturesEnabled(isSetting);
        mUiSettings.setTiltGesturesEnabled(isSetting);
        mUiSettings.setRotateGesturesEnabled(isSetting);

        mUiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
    }

    /**
     * 已接单
     */
    private void driverOrder() {

        status = 2;
        if (null != timer) {
            timer.cancel();
        }
        tvDriver.setText("接到乘客");
        rlGrabSingle.setVisibility(View.GONE);
        View view = getLayoutInflater().inflate(R.layout.view_call, null);
        ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
        ImageView ivCall = (ImageView) view.findViewById(R.id.iv_call);
        TextView tv_stat_adders = (TextView) view.findViewById(R.id.tv_stat_adders);
        TextView tv_end_adders = (TextView) view.findViewById(R.id.tv_end_adders);
        tv_stat_adders.setText(order.getFromAddress());
        tv_end_adders.setText(order.getDestination());
        Picasso.with(this).load(HttpUtils.IMAGE_RUL + order.getUserPortraitUrl())
                .transform(new CircleTransform()).into(iv_icon);
        ivCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != order.getMobile()) {
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + order.getMobile()));//跳转到拨号界面，同时传递电话号码
                    startActivity(dialIntent);
                }
            }
        });
        llCall.addView(view);
        double starLatitude;
        double starLongitude;
        if (null != order.getFromDegree()) {
            String star[] = order.getFromDegree().split(",");
            starLatitude = Double.parseDouble(star[0]);
            starLongitude = Double.parseDouble(star[1]);
        } else {
            starLatitude = order.getLatitude();
            starLongitude = order.getLongitude();
        }
        driveRoute(latLng.latitude,latLng.longitude,starLatitude,starLongitude);
    }

    private void driveRoute(double starLatitude, double starLongitude, double endLatitude, double endLongitude) {

        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                new LatLonPoint(starLatitude, starLongitude), new LatLonPoint(endLatitude, endLongitude));

        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mode, null,
                null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
        routeSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询

    }

    /**
     * 已上车
     */
    private void haveGotOnBus() {
        tvDriver.setText("到达目的地");
        rlGrabSingle.setVisibility(View.GONE);
        double starLatitude;
        double starLongitude;
        if (null != order.getFromDegree()) {
            String star[] = order.getFromDegree().split(",");
            starLatitude = Double.parseDouble(star[0]);
            starLongitude = Double.parseDouble(star[1]);
        } else {
            starLatitude = order.getLatitude();
            starLongitude = order.getLongitude();
        }
        String end[] = order.getEndDegree().split(",");
        double endLatitude = Double.parseDouble(end[0]);
        double endLongitude = Double.parseDouble(end[1]);

        driveRoute(starLatitude,starLongitude,endLatitude,endLongitude);
        navi(starLatitude,starLongitude,endLatitude,endLongitude);
    }

    protected final List<NaviLatLng> sList = new ArrayList<NaviLatLng>();
    protected final List<NaviLatLng> eList = new ArrayList<NaviLatLng>();
    protected List<NaviLatLng> mWayPointList;


    private void navi(double starLatitude, double starLongitude, double endLatitude, double endLongitude) {
        /**
         * 方法: int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute); 参数:
         *
         * @congestion 躲避拥堵
         * @avoidhightspeed 不走高速
         * @cost 避免收费
         * @hightspeed 高速优先
         * @multipleroute 多路径
         *
         *  说明: 以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
         *  注意: 不走高速与高速优先不能同时为true 高速优先与避免收费不能同时为true
         */
        int strategy = 0;
        sList.add(new NaviLatLng(starLatitude, starLongitude));
        eList.add(new NaviLatLng(endLatitude, endLongitude));
        try {
            //再次强调，最后一个参数为true时代表多路径，否则代表单路径
            strategy = aMapNavi.strategyConvert(true, false, false, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        aMapNavi.calculateDriveRoute(sList, eList, mWayPointList, strategy);
    }

    /**
     * 查询正在进行的订单
     */
    private void selectUseOrder() {
        Map<String, String> formData = new HashMap<String, String>(0);
        formData.put("userId", userId);
        formData.put("type", "2");//2司机
        presenter.selectUseOrder(formData);
    }

    private final static int REACHED_RESULT = 2;

    /**
     * 已到达目的地
     */
    private void reachedDestination() {
        /*tvDriver.setText("发起收款");
        rlGrabSingle.setVisibility(View.GONE);*/
        Intent intent = new Intent(DriverMainActivity.this, CollectionActivity.class);
        intent.putExtra("order", order);
        intent.putExtra("userId", userId);
        startActivityForResult(intent, REACHED_RESULT);
    }

    @OnClick({R.id.lt_main_title_left, R.id.lt_main_title_right, R.id.iv_pull_down, R.id.ll_depart})
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
            case R.id.iv_pull_down:
                if (null != list && list.size() == 10) {
                    page++;
                    selectOrder();
                    timer.cancel();
                }
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
                    Intent intent = new Intent(DriverMainActivity.this, TakeTaxiRecordActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("type", 2);
                    startActivity(intent);
                    mPopupWindow.dismiss();
                }
            });
            tv_my_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(DriverMainActivity.this, FeedForCommentActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("type", 2);
                    startActivity(intent);
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
            // initMarker();


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
                if (isNeedFollow) {
                    changeCamera(
                            CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                    latLng, zoom, 0, 20)));
                    isFistLocation = false;
                }
                positionInput();
                Log.e("amap", "定位信息， code: " + errorCode + " errorInfo: " + errorInfo + " locationType: " + locationType);
            } else {
                Log.e("amap", "定位信息， bundle is null ");

            }

        } else {
            Log.e("amap", "定位失败");
        }
    }


    /**
     * 根据动画按钮状态，调用函数animateCamera或moveCamera来改变可视区域
     */
    private void changeCamera(CameraUpdate update) {

        aMap.animateCamera(update);

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
                        //  aMap.setMyLocationStyle(new MyLocationStyle().myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER));
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
        } else if (resultCode == REACHED_RESULT) {
            if (data.getBooleanExtra("finish", false)) {
                aMap.clear();
                status = 0;
                tvDriver.setText("查看附件的呼车");
                llCall.removeAllViews();
                aMapNavi.stopNavi();
            }
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
                    grabSingle();

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
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                pd.dismiss();
            }
        }.start();
    }

    @Override
    public void showError(String errorString) {
        //
        if (!errorString.equals("")) {
            T.showLong(DriverMainActivity.this, errorString);
        }
        if (null != pd) {
            pd.dismiss();
        }
        if (xRefreshView.mPullRefreshing) {
            xRefreshView.stopRefresh(true);
        }
       /* if (status == 2) {
            showPD(errorString);
            status = 1;
        }*/
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
        pd.dismiss();

        rlGrabSingle.setVisibility(View.GONE);
        showPD(data);
        driverOrder();
    }

    /**
     * 查询正在进行的订单
     *
     * @param data
     */
    @Override
    public void selectUseOrder(List<AroundOrder> data) {
        if (null != data & data.size() > 0) {
            order = data.get(0);
            driverOrder();
            if (order.getStatus() == 1) {
                haveGotOnBus();
                //driveRoute();
                status = 3;
            }
        }
    }

    @Override
    public void driverTakeUser(String data) {
        pd.dismiss();
        T.showLong(DriverMainActivity.this, "已接到乘客");
        haveGotOnBus();
        status = 3;
    }

    @Override
    public void arrivingDestinationView(AroundOrder data) {
        status = 6;
    }

    @Override
    public void positionInputView(String data) {//上传位置成功

    }

    Timer timer;


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
        ttsManager.stopSpeaking();
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
        if (null != aMap) {
            aMap.clear();
        }
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
        if (null != ttsManager) {
            ttsManager.destroy();
        }
        if (null != aMapNavi) {
            aMapNavi.stopAimlessMode();
            aMapNavi.destroy();
        }
    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onInitNaviSuccess() {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onGetNavigationText(int i, String s) {

    }

    @Override
    public void onGetNavigationText(String s) {

    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {

    }


    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onReCalculateRouteForYaw() {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {

    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {

    }


    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

    @Override
    public void onPlayRing(int i) {

    }

    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void locationNeeds() {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        DriverMainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void locationShow(final PermissionRequest request) {

        showRationaleDialog(R.string.app_name,request);
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void locationDenied() {
        T.showLong(DriverMainActivity.this,"你禁止了位置获取，有可能使用不了打车功能！");
    }
    private void showRationaleDialog(@StringRes int messageResId, final PermissionRequest request) {
        final AlertDialog ComfirmDialog = new AlertDialog.Builder(this).create();
        ComfirmDialog.setCancelable(false);
        ComfirmDialog.show();
        Window window = ComfirmDialog.getWindow();
        window.setContentView(R.layout.view_achieve_location);
        TextView tv_comfirm = (TextView) window.findViewById(R.id.tv_comfirm);
        TextView tv_cancel= (TextView) window.findViewById(R.id.tv_cancel);

        tv_comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request.proceed();
                ComfirmDialog.dismiss();
            }
        });
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                request.cancel();
                ComfirmDialog.dismiss();
            }
        });
    }
}
