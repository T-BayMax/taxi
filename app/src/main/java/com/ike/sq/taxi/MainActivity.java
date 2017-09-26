package com.ike.sq.taxi;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
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
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ike.sq.lib.pushlibrary.listeners.PushListener;
import com.ike.sq.taxi.adapter.PoiListAdapter;
import com.ike.sq.taxi.base.view.BaseMvpActivity;
import com.ike.sq.taxi.bean.AroundOrder;
import com.ike.sq.taxi.bean.Code;
import com.ike.sq.taxi.bean.DrivingRouteOverlay;
import com.ike.sq.taxi.interfaces.IUserMainView;
import com.ike.sq.taxi.network.HttpUtils;
import com.ike.sq.taxi.presenters.UserMainPresenter;
import com.ike.sq.taxi.ui.activity.DriverMainActivity;
import com.ike.sq.taxi.ui.activity.EstimateActivity;
import com.ike.sq.taxi.ui.activity.FeedForCommentActivity;
import com.ike.sq.taxi.ui.activity.SelectCityActivity;
import com.ike.sq.taxi.ui.activity.TakeTaxiRecordActivity;
import com.ike.sq.taxi.utils.AMapUtil;
import com.ike.sq.taxi.utils.CircleTransform;
import com.ike.sq.taxi.utils.DisplayUtils;
import com.ike.sq.taxi.utils.T;
import com.qihoo360.replugin.RePlugin;
import com.squareup.picasso.Picasso;
import com.umeng.message.entity.UMessage;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
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
 * 用户主界面
 * Created by T-BayMax on 2017/5/15.
 */
@RuntimePermissions
public class MainActivity extends BaseMvpActivity<IUserMainView, UserMainPresenter> implements AMap.OnMyLocationChangeListener, TextWatcher
        , Inputtips.InputtipsListener, RouteSearch.OnRouteSearchListener, IUserMainView {

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

    private TextView tvDriver;
    private EditText etStartPosition;
    private EditText etEndPosition;
    private TextView tv_nearby_car;
    private TextView tv_price;
    private TextView tv_confirm_the_taxi;
    private LinearLayout ll_select_address, ll_confirm_the_taxi, ll_after_receiving_order;
    private TextView tv_reminder;
    private ImageView iv_icon;
    private TextView tv_mileage;
    private TextView tv_total_money;
    private TextView tv_evaluate;

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
    private MyLocationStyle myLocationStyle;

    private LatLonPoint start_latPoint;//开始位置信息

    private LatLonPoint end_latPoint;//结束位置信息
    private LatLng latLng;
    private AroundOrder order = new AroundOrder();

    Timer timer;
    /*private boolean isDriver;
    private boolean isSelect;
    private boolean isConfirm;*/
    private int status;//记录当前状态 0初始状态，1选择地址，2确认呼车,3等待接单，4等待司机，5正在前往，6到达目的地
    private String address;
    private float kilometre;//公里数
    private int taxiCost;//大约多少钱
    private String userId = "18878481054";

    private boolean isFistLocation = true;
    private AlertDialog dialog;
    private AlertDialog haveOrderDialog;

    private float zoom = 18f;
    private int CITY_REQUEST_CODE = 1;
    private int REACHED_RESULT = 2;
    Map<String, String> currentInfo = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        map.onCreate(savedInstanceState);// 此方法必须重写
        iniView();
        iniMapData();
        selectUseOrder();
        App.activityMap.put("MainActivity", MainActivity.this);
       /* App.setOnMsgListener(new App.OnMsgListener() {
            @Override
            public void onMsg(final UMessage msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        T.showLong(MainActivity.this, msg.title);
                        switch (msg.title) {
                            case "1":
                                pushData(msg);
                                haveOrder();//司机接单
                                break;
                            case "2":
                                overTaking();//正在前往目的地
                                break;

                            case "3":
                                userPayOrder();
                                tv_money.setText(msg.text);
                                overDestination();//到达目的地
                                break;
                            case "4":
                                // userPayOrder();
                                break;
                            case "5":

                                break;
                            default:
                                break;
                        }
                    }
                });

            }

            @Override
            public void onDriverMsg(UMessage msg) {

            }
        });*/
        ClassLoader cl = RePlugin.getHostClassLoader();
        if (cl == null) {
            Toast.makeText(MainActivity.this, "获取失败", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Class clz = cl.loadClass("com.ike.communityalliance.App");
            Method m = clz.getDeclaredMethod("setPushListener", Context.class, PushListener.class);
            m.invoke(null, MainActivity.this, new PushListener() {

                @Override
                public void onMsg(UMessage msg) {
                    T.showLong(MainActivity.this, msg.title);
                    switch (msg.title) {
                        case "1":
                            pushData(msg);
                            haveOrder();//司机接单
                            break;
                        case "2":
                            overTaking();//正在前往目的地
                            break;

                        case "3":
                            userPayOrder();
                            tv_money.setText(msg.text);
                            overDestination();//到达目的地
                            break;
                        case "4":
                            // userPayOrder();
                            break;
                        case "5":

                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void onDriverMsg(UMessage uMessage) {

                }
            });
        } catch (Exception e) {
            // 有可能Demo2根本没有这个类，也有可能没有相应方法（通常出现在"插件版本升级"的情况）
            Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    /**
     * 解析司机端推送接收到的消息
     *
     * @param msg
     */
    private void pushData(UMessage msg) {
        if (null != msg.text) {
            try {
                Gson gson = new Gson();
                Type type = new TypeToken<AroundOrder>() {
                }.getType();
                JSONObject object = new JSONObject(msg.text);

                AroundOrder data = gson.fromJson(object.get("driverInfo").toString(), type);
                order.setUserName(data.getUserName());
                order.setMobile(data.getMobile());
                order.setLicensePlate(data.getLicensePlate());
                order.setDrivingYear(data.getDrivingYear());
            } catch (Exception e) {

            }
        }
    }

    private void iniView() {
        userId = getIntent().getStringExtra("loginid");//"13824692192";
        App.checkVip = Integer.parseInt(getIntent().getStringExtra("checkVip"));//1;

        View passengerView = LayoutInflater.from(MainActivity.this).inflate(R.layout.view_passenger, null);
        etStartPosition = (EditText) passengerView.findViewById(R.id.et_start_position);
        etEndPosition = (EditText) passengerView.findViewById(R.id.et_end_position);
        tv_nearby_car = (TextView) passengerView.findViewById(R.id.tv_nearby_car);
        tv_price = (TextView) passengerView.findViewById(R.id.tv_price);
        tv_confirm_the_taxi = (TextView) passengerView.findViewById(R.id.tv_confirm_the_taxi);

        ll_select_address = (LinearLayout) passengerView.findViewById(R.id.ll_select_address);
        ll_confirm_the_taxi = (LinearLayout) passengerView.findViewById(R.id.ll_confirm_the_taxi);
        ll_after_receiving_order = (LinearLayout) passengerView.findViewById(R.id.ll_after_receiving_order);
        tv_reminder = (TextView) passengerView.findViewById(R.id.tv_reminder);
        iv_icon = (ImageView) passengerView.findViewById(R.id.iv_icon);
        tv_mileage = (TextView) passengerView.findViewById(R.id.tv_mileage);
        tv_total_money = (TextView) passengerView.findViewById(R.id.tv_total_money);
        tv_evaluate = (TextView) passengerView.findViewById(R.id.tv_evaluate);

        llDepart.addView(passengerView);

        initClick();
    }

    private void initClick() {

        etStartPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initSearchPOIPopupWindow(etStartPosition, "输入开始位置");
            }
        });
        etEndPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initSearchPOIPopupWindow(etEndPosition, "输入结束位置");
            }
        });
        tv_confirm_the_taxi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userFindCar();
                //定时器//用于定时刷新列表
                TimerTask task = new TimerTask() {
                    public void run() {
                        aroundCar();
                    }
                };
                timer = new Timer();
                timer.schedule(task, 10000, 10000); // 1s后执行task,经过1s再次执行
            }
        });
        tv_evaluate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EstimateActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("order", order);
                intent.putExtra("type", "1");
                startActivityForResult(intent, REACHED_RESULT);
            }
        });
    }

    private void iniMapData() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.LOCATION_HARDWARE) != PackageManager.PERMISSION_GRANTED) {
            MainActivityPermissionsDispatcher.locationNeedsWithCheck(this);
        }

        if (aMap == null) {
            aMap = map.getMap();
            aMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));
            // 设置定位监听
            aMap.setOnMyLocationChangeListener(this);
            // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
            aMap.setMyLocationEnabled(true);

            myLocationStyle = new MyLocationStyle();
            myLocationStyle.strokeWidth(1);
            myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
            myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色

            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER);
            myLocationStyle.interval(5000);
            aMap.setMyLocationStyle(myLocationStyle);
            aMap.setTrafficEnabled(true);// 显示实时交通状况
            //地图模式可选类型：MAP_TYPE_NORMAL,MAP_TYPE_SATELLITE,MAP_TYPE_NIGHT
            aMap.setMapType(AMap.MAP_TYPE_NORMAL);
            UiSettings mUiSettings = aMap.getUiSettings();
            mUiSettings.setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
            mUiSettings.setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
            mUiSettings.setCompassEnabled(true);
        }

        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(this);//设置驾车出行路线规划数据回调监听器


    }

    private void iniClick() {
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (status > 1) {
                    return;
                }
                aMap.clear();
                start_latPoint = new LatLonPoint(latLng.latitude, latLng.longitude);

                etStartPosition.setText("地图上选择点");
                MarkerOptions markOptiopns = new MarkerOptions();
                markOptiopns.position(latLng);
                /*TextView textView = new TextView(getApplicationContext());
                textView.setText("从这里出发");
                textView.setGravity(Gravity.CENTER);
                textView.setTextColor(Color.BLACK);
                textView.setBackgroundResource(R.mipmap.custom_info_bubble);*/
                markOptiopns.title("从这里出发");

                markOptiopns.draggable(true);
                markOptiopns.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                        .decodeResource(getResources(),
                                R.mipmap.purple_pin))).draggable(true).period(10);
                Marker marker = aMap.addMarker(markOptiopns);
                marker.showInfoWindow();
                //  initMarker();
            }
        });

    }

    Map<String, MarkerOptions> markers = new HashMap<String, MarkerOptions>(0);

    /**
     * 自定义图标
     */
    private void initMarker(List<AroundOrder> data) {
        // aMap.clear();
        for (int i = 0; i < data.size(); i++) {
            boolean boo = true;
            for (int j = 0; j < markers.size(); j++) {
                MarkerOptions markerOption = markers.get(j);
                if (null != markerOption) {
                    // markerOption.position(new LatLng(order.getLatitude(), order.getLongitude()));
                    markers.get(j).position(new LatLng(order.getLatitude(), order.getLongitude()));
                    // aMap.addMarker(markerOption);
                    boo = false;
                    break;
                }
            }
            if (boo) {
                AroundOrder order = data.get(i);
                MarkerOptions markerOption = new MarkerOptions().icon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.amap_car)))
                        .position(new LatLng(order.getLatitude(), order.getLongitude()))
                        .draggable(true);
                markers.put(order.getUserId(), markerOption);
                aMap.addMarker(markerOption);
            }
        }
        //driveRoute(mDriveRouteResult);
        //连续定位、蓝点不会移动到地图中心点，并且蓝点会跟随设备移动。
        // aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER));
    }

    private void putMarker() {

    }


    @OnClick({R.id.lt_main_title_left, R.id.lt_main_title_right, R.id.ll_depart})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lt_main_title_left:
                keyBack();
                break;
            case R.id.lt_main_title_right:
                initPopupWindow();
                break;
            case R.id.ll_depart:
                break;
        }
    }

    void initPopupWindow() {
        int width = ltMainTitleRight.getWidth();
        int WidthPixels = DisplayUtils.getScreenWidthPixels(MainActivity.this);
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
                    Intent intent = new Intent(MainActivity.this, TakeTaxiRecordActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("type", 1);
                    startActivity(intent);
                    mPopupWindow.dismiss();
                }
            });
            tv_my_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, FeedForCommentActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("type", 1);
                    startActivityForResult(intent, REACHED_RESULT);
                    mPopupWindow.dismiss();
                }
            });
        }
    }

    private PopupWindow SearchPOIPopupWindow;
    private AutoCompleteTextView et_keyword;
    private EditText et_city;

    /**
     * 查找地址
     *
     * @param et
     * @param hint
     */
    private void initSearchPOIPopupWindow(final EditText et, String hint) {
        status = 1;
        // int WidthPixels = DisplayUtils.getScreenWidthPixels(MainActivity.this);
        if (null == SearchPOIPopupWindow || !SearchPOIPopupWindow.isShowing()) {
            LayoutInflater mLayoutInflater = (LayoutInflater) MainActivity.this
                    .getSystemService(LAYOUT_INFLATER_SERVICE);
            View popwindowSearchPOI = mLayoutInflater.inflate(
                    R.layout.popwindow_search_poi, null);
            SearchPOIPopupWindow = new PopupWindow(popwindowSearchPOI, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true);
            SearchPOIPopupWindow.setTouchable(true);
            SearchPOIPopupWindow.setOutsideTouchable(true);
            SearchPOIPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
            SearchPOIPopupWindow.setFocusable(true);
            SearchPOIPopupWindow.showAsDropDown(ltMainTitleLeft, 0, -100);
            et_city = (EditText) popwindowSearchPOI.findViewById(R.id.et_city);
            TextView tv_cancel = (TextView) popwindowSearchPOI.findViewById(R.id.tv_cancel);
            et_keyword = (AutoCompleteTextView) popwindowSearchPOI.findViewById(R.id.et_keyword);
            InputMethodManager m = (InputMethodManager) et_keyword.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            et_keyword.setHint(hint);
            mPoiSearchList = (ListView) popwindowSearchPOI.findViewById(R.id.lv_poi);
            et_keyword.addTextChangedListener(MainActivity.this);
            et_city.setText(city);
            tv_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    status = 0;
                    SearchPOIPopupWindow.dismiss();
                }
            });
            et_city.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, SelectCityActivity.class);
                    startActivityForResult(intent, CITY_REQUEST_CODE);
                }
            });
            mPoiSearchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Tip tip = mpoiadapter.getItem(position);
                    et.setText(tip.getName());
                    switch (et.getId()) {
                        case R.id.et_start_position:
                            start_latPoint = tip.getPoint();
                            break;
                        case R.id.et_end_position:
                            end_latPoint = tip.getPoint();
                            break;
                    }
                    status = 0;
                    searchRouteResult();
                    SearchPOIPopupWindow.dismiss();
                }
            });
        }
    }

    Dialog payOrderDialog;
    TextView tv_money;

    /**
     * 用户确认付款
     */
    private void userPayOrder() {
        payOrderDialog = new Dialog(this, R.style.my_dialog);
        LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.popwindow_pay__fare, null);
        tv_money = (TextView) root.findViewById(R.id.tv_money);
        ImageView iv_close = (ImageView) root.findViewById(R.id.iv_close);
        TextView tv_Settlement = (TextView) root.findViewById(R.id.tv_Settlement);
        tv_Settlement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> formData = new HashMap<String, String>(0);
                formData.put("userId", userId);
                formData.put("orderId", null != order.getId() ? order.getId() : order.getOrderId());
                presenter.userPayOrder(formData);
            }
        });
        tv_money.setText(order.getMoney() + "");
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payOrderDialog.dismiss();
            }
        });
        payOrderDialog.setCancelable(false);
        payOrderDialog.setContentView(root);
        Window dialogWindow = payOrderDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = -20; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
//      lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度
//      lp.alpha = 9f; // 透明度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();
        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        payOrderDialog.show();
    }

    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult() {
        if (start_latPoint == null) {
            T.showShort(MainActivity.this, "起点未设置");
            return;
        }
        if (end_latPoint == null) {
            T.showShort(MainActivity.this, "终点未设置");
            return;
        }
        status = 2;
        ll_select_address.setVisibility(View.GONE);
        ll_confirm_the_taxi.setVisibility(View.VISIBLE);
        ll_after_receiving_order.setVisibility(View.GONE);
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                start_latPoint, end_latPoint);
        // if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mode, null,
                null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
        routeSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        // }
    }

    @Override
    public UserMainPresenter initPresenter() {
        return new UserMainPresenter();
    }


    @Override
    public void onMyLocationChange(Location location) {
        // 定位回调监听
        if (location != null) {
            if (location.getLatitude() > 0) {
                Log.e("amap", "onMyLocationChange 定位成功， lat: " + location.getLatitude() + " lon: " + location.getLongitude());
                Bundle bundle = location.getExtras();
                latLng = new LatLng(location.getLatitude(), location.getLongitude());
                //   initMarker();
                //etStartPosition.setText("我的位置");
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
                    address = currentInfo.get("address");
                    if (status == 3) {
                        positionInput();
                    }
                    if (isFistLocation) {
                        changeCamera(
                                CameraUpdateFactory.newCameraPosition(new CameraPosition(
                                        latLng, zoom, 0, 0)));
                        isFistLocation = false;
                    }
                } else {

                   return;
                }
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
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String newText = s.toString().trim();
        if (!AMapUtil.IsEmptyOrNullString(newText)) {
            InputtipsQuery inputquery = new InputtipsQuery(newText, city);
            inputquery.setCityLimit(true);
            Inputtips inputTips = new Inputtips(MainActivity.this, inputquery);
            inputTips.setInputtipsListener(MainActivity.this);
            inputTips.requestInputtipsAsyn();
        }
    }

    @Override
    public void onGetInputtips(List<Tip> tipList, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {

            mpoiadapter = new PoiListAdapter(this, tipList);
            mPoiSearchList.setAdapter(mpoiadapter);
        } else {
            T.showLong(MainActivity.this, rCode);
        }
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    /**
     * 路线规划回调
     *
     * @param result
     * @param code
     */
    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int code) {
        aMap.clear();// 清理地图上的所有覆盖物

        if (code == AMapException.CODE_AMAP_SUCCESS) {
            driveRoute(result);
        } else {
            T.showLong(this.getApplicationContext(), code);
        }
    }

    private void driveRoute(DriveRouteResult result) {
        if (result != null && result.getPaths() != null) {
            if (result.getPaths().size() > 0) {
                mDriveRouteResult = result;
                for (int i = 0; i < mDriveRouteResult.getPaths().size(); i++) {
                    DrivePath drivePath = mDriveRouteResult.getPaths()
                            .get(i);
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                            MainActivity.this, aMap, drivePath,
                            mDriveRouteResult.getStartPos(),
                            mDriveRouteResult.getTargetPos(), null);
                    drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                    drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(zoom));
                    kilometre = drivePath.getDistance() / 1000;
                    taxiCost = (int) mDriveRouteResult.getTaxiCost();
                    tv_price.setText("约" + taxiCost + "元");
                }
                //aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER));
            } else if (result != null && result.getPaths() == null) {
                T.showShort(MainActivity.this, "对不起，没有搜索到相关数据！");
            }

        } else {
            // T.showShort(MainActivity.this, "对不起，没有搜索到相关数据！");
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
            et_city.setText(city);
        } else if (resultCode == REACHED_RESULT) {
            if (data.getBooleanExtra("finish", false)) {
                back();
                etStartPosition.setText(getString(R.string.str_start_position));
                etEndPosition.setText(getString(R.string.str_end_position));
                aMap.clear();
            }
        }
    }

    ProgressBar progressBar;
    ProgressDialog pd;

    /**
     * 提交订单
     */
    private void userFindCar() {
        pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("正在提交订单，请稍后..");
        pd.setCanceledOnTouchOutside(false);
        pd.setCancelable(false);
        pd.show();
        Map<String, String> formData = new HashMap<String, String>(0);
        formData.put("userId", userId);
        formData.put("fromAddress", etStartPosition.getText().toString());
        formData.put("destination", etEndPosition.getText().toString());
        formData.put("latitude", start_latPoint.getLatitude() + "");
        formData.put("longitude", start_latPoint.getLongitude() + "");
        formData.put("kilometre", kilometre + "");
        formData.put("money", taxiCost + "");
        formData.put("key", App.deviceToken);
        formData.put("fromDegree", start_latPoint.getLatitude() + "," + start_latPoint.getLongitude()); //开始地址经纬度  格式：经度，维度
        formData.put("endDegree", end_latPoint.getLatitude() + "," + end_latPoint.getLongitude()); //终点地址经纬度  格式：经度，维度
        presenter.createOrder(formData);
        positionInput();

    }

    /**
     * 等待接单订单
     */
    private void WaitingOrder() {
        status = 3;
        dialog = new AlertDialog.Builder(this).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = DisplayUtils.dp2px(MainActivity.this, 300);//定义宽度
        lp.height = DisplayUtils.dp2px(MainActivity.this, 160);//定义高度
        dialog.getWindow().setAttributes(lp);
        window.setContentView(R.layout.dialog_waiting_order);
        progressBar = (ProgressBar) window.findViewById(R.id.spin_kit);


        Circle circle = new Circle();
        progressBar.setIndeterminateDrawable(circle);
        ImageView close = (ImageView) window.findViewById(R.id.iv_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("确认")
                        .setMessage("你确定要取消订单吗？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cancelOrder();
                            }
                        })
                        .setNegativeButton("否", null)
                        .show();
                // dialog.dismiss();
            }
        });
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
        formData.put("type", "0");//0用户，1司机
        presenter.positionInputPresenter(formData);
    }

    /**
     * 查看周围司机
     */
    private void aroundCar() {
        Map<String, String> formData = new HashMap<String, String>(0);
        formData.put("userId", userId);
        presenter.aroundCar(formData);
    }

    /**
     * 取消订单
     */
    private void cancelOrder() {
        Map<String, String> formData = new HashMap<String, String>(0);
        formData.put("userId", userId);
        formData.put("orderId", null != order.getId() ? order.getId() : order.getOrderId());
        presenter.cancelOrder(formData);
    }

    /**
     * 查询正在进行的订单
     */
    private void selectUseOrder() {
        Map<String, String> formData = new HashMap<String, String>(0);
        formData.put("userId", userId);
        formData.put("type", "1");//0用户
        presenter.selectUseOrder(formData);
    }

    ImageView iv_driver_icon;
    TextView tv_driver;
    TextView tv_driving_years;
    TextView tv_plate_number;
    TextView tv_phone;

    /**
     * 已接单
     */
    public void haveOrder() {
        if (null != dialog && dialog.isShowing()) {
            dialog.dismiss();
        }
        haveOrderDialog = new AlertDialog.Builder(MainActivity.this).create();
        haveOrderDialog.setCancelable(false);
        haveOrderDialog.show();
        Window window = haveOrderDialog.getWindow();
        window.setContentView(R.layout.dialog_have_order);
        WindowManager.LayoutParams lp = haveOrderDialog.getWindow().getAttributes();
        lp.width = DisplayUtils.dp2px(MainActivity.this, 300);//定义宽度
        lp.height = DisplayUtils.dp2px(MainActivity.this, 200);//定义高度
        haveOrderDialog.getWindow().setAttributes(lp);
        ImageView iv_close = (ImageView) window.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                haveOrderDialog.dismiss();

            }
        });
        iv_driver_icon = (ImageView) window.findViewById(R.id.iv_driver_icon);
        tv_driver = (TextView) window.findViewById(R.id.tv_driver);
        tv_driving_years = (TextView) window.findViewById(R.id.tv_driving_years);
        tv_plate_number = (TextView) window.findViewById(R.id.tv_plate_number);
        tv_phone = (TextView) window.findViewById(R.id.tv_phone);
        if (null != order) {
            Picasso.with(this).load(HttpUtils.IMAGE_RUL + order.getUserPortraitUrl())
                    .transform(new CircleTransform()).into(iv_driver_icon);
            tv_driver.setText(order.getUserName());
            tv_driving_years.setText("驾龄：" + order.getDrivingYear() + "年");
            tv_plate_number.setText("车牌：" + order.getLicensePlate());
            tv_phone.setText("电话：" + order.getMobile());
        }
        afterReceivingOrder();
    }

    /**
     * 司机正在赶往
     */
    private void afterReceivingOrder() {
        status = 4;
        tv_reminder.setText("距离你大约500米 约5分钟到达请耐心等待");
        tv_mileage.setText(kilometre + "");
        tv_total_money.setText(taxiCost + "");
        tv_evaluate.setVisibility(View.GONE);
        ll_select_address.setVisibility(View.GONE);
        ll_confirm_the_taxi.setVisibility(View.GONE);
        ll_after_receiving_order.setVisibility(View.VISIBLE);
    }


    /**
     * 已经上车正在赶往目的地
     */
    private void overTaking() {
        status = 5;
        if (null != haveOrderDialog) {
            haveOrderDialog.dismiss();
        }
        tv_reminder.setText("上车成功，正在前往目的地");
        tv_evaluate.setVisibility(View.GONE);
        ll_select_address.setVisibility(View.GONE);
        ll_confirm_the_taxi.setVisibility(View.GONE);
        ll_after_receiving_order.setVisibility(View.VISIBLE);
    }

    /**
     * 到达目的地
     */
    private void overDestination() {
        status = 6;
        tv_reminder.setText("本次行程已结束，可以给司机");
        tv_evaluate.setVisibility(View.VISIBLE);
        ll_select_address.setVisibility(View.GONE);
        ll_confirm_the_taxi.setVisibility(View.GONE);
        ll_after_receiving_order.setVisibility(View.VISIBLE);
        if (null != timer) {
            timer.cancel();
        }
    }

    private void back() {
        ll_select_address.setVisibility(View.VISIBLE);
        ll_confirm_the_taxi.setVisibility(View.GONE);
        ll_after_receiving_order.setVisibility(View.GONE);
        tv_evaluate.setVisibility(View.GONE);
        status = 0;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            keyBack();
            return true;
        } else {
            return false;
        }
    }

    private void keyBack() {
        switch (status) {
            case 0:
                MainActivity.this.finish();
                break;
            case 1:
                status = 0;
                SearchPOIPopupWindow.dismiss();
                break;
            case 2:
                back();
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                back();
                etStartPosition.setText(getString(R.string.str_start_position));
                etEndPosition.setText(getString(R.string.str_end_position));
                aMap.clear();
                break;
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
        if (!errorString.equals(""))
            T.showLong(MainActivity.this, errorString);
        if (null != pd) {
            pd.dismiss();
        }
    }

    @Override
    public void haveOrderCallBack() {
        if (null != pd) {
            pd.dismiss();
        }
        haveOrder();
    }

    /**
     * 取消订单
     *
     * @param data
     */
    @Override
    public void cancelOrder(String data) {
        T.showLong(MainActivity.this, data);
        dialog.dismiss();
        etStartPosition.setText(getString(R.string.str_start_position));
        etEndPosition.setText(getString(R.string.str_end_position));
        status = 0;
    }

    @Override
    public void userPayOrderView(String data) {//订单完成
        T.showLong(MainActivity.this, data);
        payOrderDialog.dismiss();

        overDestination();
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
            etStartPosition.setText(order.getFromAddress());
            etEndPosition.setText(order.getDestination());

            tv_mileage.setText(order.getKilometre() + "");
            tv_total_money.setText(order.getMoney() + "");
            /*
            start_latPoint=new LatLonPoint();*/
            switch (order.getStatus()) {
                case 1:
                    overTaking();
                    break;
                case 4:
                    haveOrder();
                    break;
                case 5:
                    userPayOrder();
                    overDestination();
                    break;
                default:
                    WaitingOrder();
                    break;
            }
            if (order.getStatus() == 0) {
            } else {
            }
            if (null != timer) {
                timer.cancel();
            }
        }
    }

    /**
     * 四周车辆
     *
     * @param data
     */
    @Override
    public void aroundCarView(List<AroundOrder> data) {
        initMarker(data);
    }

    /**
     * 添加订单
     *
     * @param data
     */
    @Override
    public void createOrderCallBack(String data) {
        T.showLong(MainActivity.this, "呼叫成功，等待司机接单");
        if (null != pd) {
            pd.dismiss();
        }
        order.setId(data);
        WaitingOrder();

    }

    @Override
    public void positionInputView(String data) {
        //T.showLong(MainActivity.this, data);
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
        outState.putInt("status", status);
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


    @NeedsPermission({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void locationNeeds() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnShowRationale({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void locationShow(final PermissionRequest request) {

        showRationaleDialog(R.string.app_name, request);
    }

    @OnPermissionDenied({Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
    void locationDenied() {
        T.showLong(MainActivity.this, "你禁止了位置获取，有可能使用不了打车功能！");
    }

    private void showRationaleDialog(@StringRes int messageResId, final PermissionRequest request) {
        final AlertDialog ComfirmDialog = new AlertDialog.Builder(this).create();
        ComfirmDialog.setCancelable(false);
        ComfirmDialog.show();
        Window window = ComfirmDialog.getWindow();
        window.setContentView(R.layout.view_achieve_location);
        TextView tv_comfirm = (TextView) window.findViewById(R.id.tv_comfirm);
        TextView tv_cancel = (TextView) window.findViewById(R.id.tv_cancel);

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
