package com.ike.sq.taxi.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ike.sq.taxi.R;
import com.ike.sq.taxi.base.view.BaseMvpActivity;
import com.ike.sq.taxi.bean.AroundOrder;
import com.ike.sq.taxi.interfaces.ICollectionView;
import com.ike.sq.taxi.network.HttpUtils;
import com.ike.sq.taxi.presenters.CollectionPresenters;
import com.ike.sq.taxi.utils.CircleTransform;
import com.ike.sq.taxi.utils.DateConversionUtils;
import com.ike.sq.taxi.utils.DisplayUtils;
import com.ike.sq.taxi.utils.T;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 结束-收款
 * Created by T-BayMax on 2017/6/5.
 */

public class CollectionActivity extends BaseMvpActivity<ICollectionView, CollectionPresenters> implements ICollectionView {

    @BindView(R.id.lt_main_title_left)
    TextView ltMainTitleLeft;
    @BindView(R.id.lt_main_title)
    TextView ltMainTitle;
    @BindView(R.id.lt_main_title_right)
    TextView ltMainTitleRight;
    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.tv_stat_adders)
    TextView tvStatAdders;
    @BindView(R.id.tv_end_adders)
    TextView tvEndAdders;
    @BindView(R.id.iv_call)
    ImageView ivCall;
    @BindView(R.id.tv_settle_accounts)
    TextView tvSettleAccounts;
    @BindView(R.id.tv_amount)
    TextView tvAmount;
    @BindView(R.id.tv_kilometre_total)
    TextView tvKilometreTotal;
    @BindView(R.id.tv_cost)
    TextView tvCost;
    @BindView(R.id.tv_surpass)
    TextView tvSurpass;
    @BindView(R.id.tv_surpass_cost)
    TextView tvSurpassCost;
    @BindView(R.id.tv_duration)
    TextView tvDuration;
    @BindView(R.id.tv_duration_cost)
    TextView tvDurationCost;

    private AlertDialog comfirmDialog;

    private String headerPath;
    private String userName;
    private String userId;
    private AroundOrder order;

    private  final static int REACHED_RESULT=2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        order = (AroundOrder) intent.getSerializableExtra("order");
        headerPath = order.getUserPortraitUrl();
        userName=order.getUserName();
        userId = intent.getStringExtra("userId");
        Map<String, String> formData = new HashMap<String, String>(0);
        formData.put("userId", userId);
        formData.put("orderId", null!=order.getId()?order.getId():order.getOrderId());
        formData.put("kilometre", order.getKilometre() + "");
        formData.put("money", order.getMoney() + "");
        presenter.getReviewDetails(formData);

        ltMainTitleRight.setVisibility(View.GONE);
    }

    private void initData() {
        if (null!=headerPath) {
            Picasso.with(this).load(HttpUtils.IMAGE_RUL + headerPath)
                    .transform(new CircleTransform()).into(ivIcon);
        }
        tvStatAdders.setText(order.getFromAddress());
        tvEndAdders.setText(order.getDestination());
        tvAmount.setText(order.getMoney() + "");
        tvKilometreTotal.setText("里程\t\t" + order.getKilometre() + "公里");
        String surpass = String.valueOf(order.getKilometre());
        tvSurpass.setText("超里程\t\t0" + (surpass.substring(surpass.indexOf("."), surpass.length())) + "公里");
        tvDuration.setText("时长\t\t" + DateConversionUtils.calculatingTime(order.getStartTime(), order.getEndTime()));
    }


    public void showComfirmDialog() {
        comfirmDialog = new AlertDialog.Builder(this).create();
        comfirmDialog.show();
        Window window = comfirmDialog.getWindow();
        WindowManager.LayoutParams lp = comfirmDialog.getWindow().getAttributes();
        lp.width = DisplayUtils.dp2px(CollectionActivity.this, 300);//定义宽度
        lp.height = DisplayUtils.dp2px(CollectionActivity.this, 200);//定义高度
        comfirmDialog.getWindow().setAttributes(lp);
        LayoutInflater mLayoutInflater = (LayoutInflater) this
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = mLayoutInflater.inflate(
                R.layout.comfirm_dialog_layout, null);
        window.setContentView(view);
        Button btn_comfirm_dialog_comfirm = (Button) window.findViewById(R.id.btn_comfirm_dialog_comfirm);
        //ImageView iv_comfirm_dialog_cancel = (ImageView) window.findViewById(R.id.iv_comfirm_dialog_cancel);
        btn_comfirm_dialog_comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> formData = new HashMap<String, String>(0);
                formData.put("userId", userId);
                formData.put("orderId", order.getOrderId());
                presenter.driverArriveConfirm(formData);
                comfirmDialog.dismiss();
            }
        });
       /* iv_comfirm_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comfirmDialog.dismiss();
            }
        });*/
    }

    @Override
    public CollectionPresenters initPresenter() {
        return new CollectionPresenters();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showError(String errorString) {
        T.showLong(CollectionActivity.this, errorString);
    }

    @Override
    public void postCollection(AroundOrder data) {
        order = data;
        order.setUserName(userName);
        order.setUserPortraitUrl(headerPath);
        initData();
    }

    @Override
    public void driverArriveAddressConfirm(String data) {
        T.showLong(CollectionActivity.this, "确认成功");
        back();
    }

    @OnClick({R.id.lt_main_title_left, R.id.iv_call, R.id.tv_settle_accounts})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lt_main_title_left:
                CollectionActivity.this.finish();
                break;
            case R.id.iv_call:
                break;
            case R.id.tv_settle_accounts:
                showComfirmDialog();
                break;
        }
    }

    /**
     * 确认成功返回
     */
    private void back(){
        Intent it = new Intent();
        it.putExtra("finish",true);
        setResult(REACHED_RESULT, it);
        CollectionActivity.this.finish();
        Intent intent=new Intent(CollectionActivity.this,EstimateActivity.class);
        intent.putExtra("userId", userId);
        intent.putExtra("order", order);
        intent.putExtra("type", "2");
        startActivity(intent);
    }

}
