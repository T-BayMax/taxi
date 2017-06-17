package com.ike.sq.taxi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.ike.sq.taxi.R;
import com.ike.sq.taxi.base.view.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 申请为司机
 * Created by T-BayMax on 2017/6/5.
 */

public class ApplyForDriverActivity extends BaseActivity {
    @BindView(R.id.lt_main_title_left)
    TextView ltMainTitleLeft;
    @BindView(R.id.lt_main_title)
    TextView ltMainTitle;
    @BindView(R.id.lt_main_title_right)
    TextView ltMainTitleRight;
    @BindView(R.id.tv_apply_driver)
    TextView tvApplyDriver;
    @BindView(R.id.tv_progress_audit)
    TextView tvProgressAudit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_for_driver);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.lt_main_title_left, R.id.tv_apply_driver, R.id.tv_progress_audit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lt_main_title_left:
                ApplyForDriverActivity.this.finish();
                break;
            case R.id.tv_apply_driver:
                startActivity(new Intent(ApplyForDriverActivity.this,AddMotormanActivity.class));
                break;
            case R.id.tv_progress_audit:
                startActivity(new Intent(ApplyForDriverActivity.this,ReviewDetailsAcitivy.class));
                break;
        }
    }
}
