package com.ike.sq.taxi;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Bundle;
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

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
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
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.ike.sq.taxi.adapter.PoiListAdapter;
import com.ike.sq.taxi.base.presenter.BasePersenter;
import com.ike.sq.taxi.base.view.BaseActivity;
import com.ike.sq.taxi.base.view.BaseMvpActivity;
import com.ike.sq.taxi.bean.DrivingRouteOverlay;
import com.ike.sq.taxi.interfaces.IUserMainView;
import com.ike.sq.taxi.listeners.OnUserMainListener;
import com.ike.sq.taxi.presenters.UserMainPresenter;
import com.ike.sq.taxi.ui.activity.SelectCityActivity;
import com.ike.sq.taxi.utils.AMapUtil;
import com.ike.sq.taxi.utils.DisplayUtils;
import com.ike.sq.taxi.utils.T;
import com.umeng.message.entity.UMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 用户主界面
 * Created by T-BayMax on 2017/5/15.
 */
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

    /*private boolean isDriver;
    private boolean isSelect;
    private boolean isConfirm;*/
    private int status;//记录当前状态 0初始状态，1选择地址，2确认呼车,3等待接单，4等待司机，5正在前往，6到达目的地
    private String address;
    private float kilometre;//公里数
    private int taxiCost;//大约多少钱
    private String userId = "13824692192";

    private AlertDialog dialog;
    private AlertDialog haveOrderDialog;

    private float zoom = 18;
    private int CITY_REQUEST_CODE = 1;
    Map<String, String> currentInfo = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        map.onCreate(savedInstanceState);// 此方法必须重写
        iniView();
        iniMapData();
        //iniClick();
        // haveOrder();
        App.activityMap.put("MainActivity", MainActivity.this);
        App.setOnMsgListener(new App.OnMsgListener() {
            @Override
            public void onMsg(final UMessage msg) {
                T.showLong(MainActivity.this, msg.title);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        switch (msg.title) {
                            case "1":
                                haveOrder();//司机接单
                                break;
                            case "2":
                                overTaking();//正在前往目的地
                                break;

                            case "3":
                                overDestination();//到达目的地
                                break;
                            case "4":
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
        });

    }

    private void iniView() {

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
            }
        });
        //}
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
            myLocationStyle = new MyLocationStyle();
            myLocationStyle.strokeWidth(1);
            myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));// 设置圆形的边框颜色
            myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));// 设置圆形的填充颜色
            //myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
            aMap.getUiSettings().setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
            //连续定位、蓝点不会移动到地图中心点，并且蓝点会跟随设备移动。
            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
            myLocationStyle.interval(5000);
            aMap.setMyLocationStyle(myLocationStyle);
            aMap.setTrafficEnabled(true);// 显示实时交通状况
            //地图模式可选类型：MAP_TYPE_NORMAL,MAP_TYPE_SATELLITE,MAP_TYPE_NIGHT
            aMap.setMapType(AMap.MAP_TYPE_NORMAL);

            //aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER));
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

    /**
     * 自定义图标
     */
    private void initMarker() {
       /* locationMarker = aMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 0.5f)

                .icon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.location_marker)))
                .position(latLng));
        locationMarker.showInfoWindow();
*/
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
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
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
                if (status == 3)
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
                                MainActivity.this, aMap, drivePath,
                                mDriveRouteResult.getStartPos(),
                                mDriveRouteResult.getTargetPos(), null);
                        drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                        drivingRouteOverlay.removeFromMap();
                        drivingRouteOverlay.addToMap();
                        drivingRouteOverlay.zoomToSpan();

                        kilometre = drivePath.getDistance() / 1000;
                        taxiCost = (int) mDriveRouteResult.getTaxiCost();
                        tv_price.setText("约" + taxiCost + "元");
                    }
                    aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW_NO_CENTER));
                } else if (result != null && result.getPaths() == null) {
                    T.showShort(MainActivity.this, "对不起，没有搜索到相关数据！");
                }

            } else {
                T.showShort(MainActivity.this, "对不起，没有搜索到相关数据！");
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
            et_city.setText(city);
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
                dialog.dismiss();
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
        afterReceivingOrder();
    }

    /**
     * 司机正在赶往
     */
    private void afterReceivingOrder() {
        status = 4;
        tv_reminder.setText("距离你500米 约5分钟到达请耐心等待");
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
    }

    private void back() {
        ll_select_address.setVisibility(View.VISIBLE);
        ll_confirm_the_taxi.setVisibility(View.GONE);
        ll_after_receiving_order.setVisibility(View.GONE);
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
        T.showLong(MainActivity.this, errorString);
        pd.dismiss();
    }

    @Override
    public void haveOrderCallBack() {
        pd.dismiss();
        //haveOrder();
    }

    @Override
    public void createOrderCallBack(String data) {
        T.showLong(MainActivity.this, data);
        pd.dismiss();
        WaitingOrder();

    }

    @Override
    public void positionInputView(String data) {
        T.showLong(MainActivity.this, data);
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
