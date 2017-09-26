package com.ike.sq.taxi.ui.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.ike.sq.taxi.R;
import com.ike.sq.taxi.base.view.BaseMvpActivity;
import com.ike.sq.taxi.interfaces.IAddMotorManView;
import com.ike.sq.taxi.presenters.AddMotorManPresenter;
import com.ike.sq.taxi.utils.AMUtils;
import com.ike.sq.taxi.utils.IDCardUtil;
import com.ike.sq.taxi.utils.ImageManage;
import com.ike.sq.taxi.utils.T;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.finalteam.rxgalleryfinal.RxGalleryFinalApi;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultDisposable;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageRadioResultEvent;
import cn.finalteam.rxgalleryfinal.ui.base.IRadioImageCheckedListener;
import cn.finalteam.rxgalleryfinal.utils.Logger;
import cn.finalteam.rxgalleryfinal.utils.ModelUtils;

/**
 * 添加司机信息
 * Created by T-BayMax on 2017/5/12.
 */

public class AddMotormanActivity extends BaseMvpActivity<IAddMotorManView, AddMotorManPresenter> implements IAddMotorManView {


    @BindView(R.id.lt_main_title_left)
    TextView ltMainTitleLeft;
    @BindView(R.id.lt_main_title)
    TextView ltMainTitle;
    @BindView(R.id.lt_main_title_right)
    TextView ltMainTitleRight;
    @BindView(R.id.rl_city)
    RelativeLayout rlCity;
    @BindView(R.id.et_city)
    EditText etCity;
    @BindView(R.id.iv_city_more)
    ImageView ivCityMore;
    @BindView(R.id.sp_licence_plate)
    Spinner spLicencePlate;
    @BindView(R.id.et_licence_plate)
    EditText etLicencePlate;
    @BindView(R.id.et_vehicle_model)
    EditText etVehicleModel;
    @BindView(R.id.iv_vehicle_model)
    ImageView ivVehicleModel;
    @BindView(R.id.et_owner_name)
    EditText etOwnerName;
    @BindView(R.id.et_record_date)
    EditText etRecordDate;
    @BindView(R.id.et_driver_name)
    EditText etDriverName;
    @BindView(R.id.et_IDCard)
    EditText etIDCard;
    @BindView(R.id.et_date)
    EditText etDate;
    @BindView(R.id.tv_finish)
    TextView tvFinish;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.iv_file)
    ImageView ivFile;

    private String userId;


    private int CITY_REQUEST_CODE = 1;
    private int VEHICLE_REQUEST_CODE = 2;
    private String path;
    private File file;
    private ProgressDialog pd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_motorman);
        ButterKnife.bind(this);
        initView();
        ModelUtils.setDebugModel(true);
    }

    private void initView() {
        userId = getIntent().getStringExtra("userId");
        ltMainTitle.setText("申请表");
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.city_abbreviation,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);// 设置下拉列表样式
        spLicencePlate.setAdapter(adapter);
    }

    @Override
    public AddMotorManPresenter initPresenter() {
        return new AddMotorManPresenter();
    }

    @OnClick({R.id.lt_main_title_left, R.id.rl_city, R.id.rl_vehicle_model, R.id.et_record_date, R.id.et_date, R.id.iv_file, R.id.tv_finish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lt_main_title_left:
                AddMotormanActivity.this.finish();
                break;
            case R.id.rl_city:
                Intent intent = new Intent(AddMotormanActivity.this, SelectCityActivity.class);
                startActivityForResult(intent, CITY_REQUEST_CODE);
                break;
            case R.id.rl_vehicle_model:
                Intent vehicle = new Intent(AddMotormanActivity.this, VehiclesChoiceActivity.class);
                startActivityForResult(vehicle, VEHICLE_REQUEST_CODE);
                break;
            case R.id.et_record_date:
                //隐藏软件盘，防止遮挡
                InputMethodManager immRecord = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (immRecord.isActive()) {
                    immRecord.hideSoftInputFromWindow(getCurrentFocus()
                            .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                TimePickerView pvTimeRecord = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {//选中事件回调
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        etRecordDate.setText(dateFormat.format(date));
                    }
                }).setTitleText("车辆注册时间")
                        .setTitleColor(R.color.color_99)
                        .setSubmitColor(R.color.color_33)
                        .setCancelColor(R.color.color_33).setType(TimePickerView.Type.YEAR_MONTH_DAY)
                        .setLabel("", "", "", "", "", "").build();
                pvTimeRecord.show();
                break;
            case R.id.et_date:
                //隐藏软件盘，防止遮挡
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(getCurrentFocus()
                            .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {//选中事件回调
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        etDate.setText(dateFormat.format(date));
                    }
                }).setTitleText("车辆注册时间")
                        .setTitleColor(R.color.color_99)
                        .setSubmitColor(R.color.color_33)
                        .setCancelColor(R.color.color_33).setType(TimePickerView.Type.YEAR_MONTH_DAY)
                        .setLabel("", "", "", "", "", "").build();
                pvTime.show();
                break;
            case R.id.iv_file:
                checkImage();
                break;
            case R.id.tv_finish:
                getTableData();
                if (checkInputInfo()) {
                    motorManPresenter();
                }

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
        T.showLong(AddMotormanActivity.this, errorString);
    }

    @Override
    public void motorManView(String data) {
        AddMotormanActivity.this.finish();
        T.showLong(AddMotormanActivity.this, data);
    }

    private String city;                 //城市
    private String licensePlate;         //车牌号
    private String carModels;           //车型
    private String carUser;             //车主姓名
    private String registerTime;         //车辆注册日期
    private String fullName;            //司机姓名
    private String mobile;              //司机电话
    private String idcard;               //身份证号码
    private String firstDate;               //初次领取驾照日期

    private void getTableData() {
        city = etCity.getText().toString();
        licensePlate = spLicencePlate.getSelectedItem().toString() + etLicencePlate.getText().toString();
        carModels = etVehicleModel.getText().toString();
        carUser = etOwnerName.getText().toString();
        registerTime = etRecordDate.getText().toString();
        fullName = etDriverName.getText().toString();
        mobile = etPhone.getText().toString();
        idcard = etIDCard.getText().toString();
        firstDate = etDate.getText().toString();

    }

    @Override
    public boolean checkInputInfo() {
        if (city.equals("")) {
            T.showLong(AddMotormanActivity.this, "请选择城市");
            return false;
        }
        if (licensePlate.equals("")) {
            T.showLong(AddMotormanActivity.this, "请输入车牌号");
            return false;
        }
        if (carModels.equals("")) {
            T.showLong(AddMotormanActivity.this, "请选择" + getResources().getString(R.string.str_add_motorman_vehicle_model));
            return false;
        }
        if (carUser.equals("")) {
            T.showLong(AddMotormanActivity.this, "请输入" + getResources().getString(R.string.str_add_motorman_owner_name));
            return false;
        }
        if (registerTime.equals("")) {
            T.showLong(AddMotormanActivity.this, "请选择" + getResources().getString(R.string.str_add_motorman_record_date));
            return false;
        }
        if (fullName.equals("")) {
            T.showLong(AddMotormanActivity.this, "请输入" + getResources().getString(R.string.str_add_motorman_driver_name));
            return false;
        }
        if (idcard.equals("")) {
            T.showLong(AddMotormanActivity.this, "请输入" + getResources().getString(R.string.str_add_motorman_IDCard));
            return false;
        }
        if (mobile.equals("")) {
            T.showLong(AddMotormanActivity.this, "请输入" + getResources().getString(R.string.str_add_motorman_phone));
            return false;
        }
        if (firstDate.equals("")) {
            T.showLong(AddMotormanActivity.this, "请选择" + getResources().getString(R.string.str_add_motorman_date));
            return false;
        }
        if (file == null) {
            T.showLong(AddMotormanActivity.this, "请选择" + getResources().getString(R.string.str_add_motorman_file));
            return false;
        }
        if (!AMUtils.isCarnumberNO(licensePlate)) {
            T.showLong(AddMotormanActivity.this, "请输入正确的车牌号");
            return false;
        }
        if (!AMUtils.isMobile(mobile)) {
            T.showLong(AddMotormanActivity.this, "请输入正确的电话号码");
            return false;
        }
        String info = IDCardUtil.IDCardValidate(idcard);
        if (!info.equals("YES")) {
            T.showLong(AddMotormanActivity.this, info);
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == CITY_REQUEST_CODE) {
            etCity.setText(data.getStringExtra("city"));
        } else if (resultCode == VEHICLE_REQUEST_CODE) {
            etVehicleModel.setText(data.getStringExtra("car"));
        }
    }

    private void checkImage() {
        RxGalleryFinalApi.getInstance(AddMotormanActivity.this).openGalleryRadioImgDefault(
                new RxBusResultDisposable<ImageRadioResultEvent>() {
                    @Override
                    protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) throws Exception {

                    }
                }).onCropImageResult(
                new IRadioImageCheckedListener() {
                    @Override
                    public void cropAfter(Object t) {
                        path = t.toString();
                        file = new File(path);

                        ivFile.setImageBitmap(ImageManage.getSmallBitmap(path));

                    }

                    @Override
                    public boolean isActivityFinish() {
                        Logger.i("返回false不关闭，返回true则为关闭");
                        return true;
                    }
                });
    }

    private void motorManPresenter() {
        pd = new ProgressDialog(AddMotormanActivity.this);

        pd.setMessage("正在提交中...");
        Map<String, String> formData = new HashMap<String, String>(0);
        formData.put("userId", userId);
        formData.put("city", etCity.getText().toString());
        formData.put("licensePlate", spLicencePlate.getSelectedItem().toString() + etLicencePlate.getText().toString());
        formData.put("carModels", etVehicleModel.getText().toString());
        formData.put("carUser", etOwnerName.getText().toString());
        formData.put("registerTime", etRecordDate.getText().toString());
        formData.put("fullName", etDriverName.getText().toString());
        formData.put("mobile", etPhone.getText().toString());
        formData.put("idcard", etIDCard.getText().toString());
        formData.put("firstDate", etDate.getText().toString());
        Map<String, File> formFile = new HashMap<String, File>(0);
        formFile.put(file.getName(), file);

        presenter.motorManPresenter(formData, formFile, "file");
    }

}
