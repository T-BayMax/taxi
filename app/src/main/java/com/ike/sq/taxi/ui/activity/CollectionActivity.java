package com.ike.sq.taxi.ui.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ike.sq.taxi.R;
import com.ike.sq.taxi.base.view.BaseMvpActivity;
import com.ike.sq.taxi.interfaces.ICollectionView;
import com.ike.sq.taxi.presenters.CollectionPresenters;
import com.ike.sq.taxi.utils.DisplayUtils;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {

    }


    public void showComfirmDialog() {
        comfirmDialog = new AlertDialog.Builder(this).create();
        comfirmDialog.show();
        Window window = comfirmDialog.getWindow();
        WindowManager.LayoutParams lp = comfirmDialog.getWindow().getAttributes();
        lp.width = DisplayUtils.dp2px(CollectionActivity.this, 300);//定义宽度
        lp.height = DisplayUtils.dp2px(CollectionActivity.this, 200);//定义高度
        comfirmDialog.getWindow().setAttributes(lp);
        window.setContentView(R.layout.comfirm_dialog_layout);
        Button btn_comfirm_dialog_comfirm = (Button) window.findViewById(R.id.btn_comfirm_dialog_comfirm);
        ImageView iv_comfirm_dialog_cancel = (ImageView) window.findViewById(R.id.iv_comfirm_dialog_cancel);
        btn_comfirm_dialog_comfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                comfirmDialog.dismiss();
            }
        });
        iv_comfirm_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comfirmDialog.dismiss();
            }
        });
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

    }

    @Override
    public void postCollection(String data) {

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
}
