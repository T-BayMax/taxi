package com.ike.sq.taxi.ui.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ike.sq.taxi.R;
import com.ike.sq.taxi.base.view.BaseMvpActivity;
import com.ike.sq.taxi.bean.MotorManBean;
import com.ike.sq.taxi.interfaces.IReviewDetailsView;
import com.ike.sq.taxi.presenters.ReviewDetailsPresenters;
import com.ike.sq.taxi.utils.T;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 审核详情
 * Created by T-BayMax on 2017/6/3.
 */

public class ReviewDetailsAcitivy extends BaseMvpActivity<IReviewDetailsView, ReviewDetailsPresenters> implements IReviewDetailsView {

    @BindView(R.id.lt_main_title_left)
    TextView ltMainTitleLeft;
    @BindView(R.id.lt_main_title)
    TextView ltMainTitle;
    @BindView(R.id.lt_main_title_right)
    TextView ltMainTitleRight;
    @BindView(R.id.iv_present)
    ImageView ivPresent;
    @BindView(R.id.v_present)
    View vPresent;
    @BindView(R.id.iv_audit)
    ImageView ivAudit;
    @BindView(R.id.v_audit)
    View vAudit;
    @BindView(R.id.iv_audit_result)
    ImageView ivAuditResult;
    @BindView(R.id.tv_present)
    TextView tvPresent;
    @BindView(R.id.tv_present_time)
    TextView tvPresentTime;
    @BindView(R.id.tv_audit)
    TextView tvAudit;
    @BindView(R.id.tv_audit_details)
    TextView tvAuditDetails;
    @BindView(R.id.tv_audit_result)
    TextView tvAuditResult;
    @BindView(R.id.tv_audit_result_details)
    TextView tvAuditResultDetails;
    @BindView(R.id.rl_review)
    RelativeLayout rlReview;


    private String userId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_details);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        userId="13824692192";
        ltMainTitle.setText("审核详情");
        ltMainTitleRight.setCompoundDrawables(null, null, null, null);
        ivAudit.setImageResource(R.mipmap.standing_progress);
        tvAudit.setTextColor(ContextCompat.getColor(ReviewDetailsAcitivy.this, R.color.color_10));
        tvAuditDetails.setTextColor(ContextCompat.getColor(ReviewDetailsAcitivy.this, R.color.color_66));
        Map<String,String> formData=new HashMap<>(0);
        formData.put("userId",userId);
        presenter.getReviewDetails(formData);
    }

    @Override
    public ReviewDetailsPresenters initPresenter() {
        return new ReviewDetailsPresenters();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showError(String errorString) {
        T.showLong(ReviewDetailsAcitivy.this,errorString);
    }

    @Override
    public void getReviewDetailsViewData(MotorManBean data) {
        rlReview.setVisibility(View.VISIBLE);
        if (data.getStatus() == 1) {
            tvAuditResultDetails.setText("审核成功，恭喜你成为司机的一员\n" + data.getEndTime());
            vAudit.setBackgroundColor(ContextCompat.getColor(ReviewDetailsAcitivy.this, R.color.color_10));
            tvAuditResult.setTextColor(ContextCompat.getColor(ReviewDetailsAcitivy.this, R.color.color_66));
            ivAuditResult.setImageResource(R.mipmap.standing_progress);
            //auditResult();
        }
        tvPresentTime.setText("你的司机申请表已经成功提交\n" + data.getTime());
    }

   /* private void auditResult() {
            vAudit.setBackgroundColor(ContextCompat.getColor(ReviewDetailsAcitivy.this, R.color.color_10));
            tvAuditResult.setBackgroundColor(ContextCompat.getColor(ReviewDetailsAcitivy.this, R.color.color_66));
            ivAuditResult.setImageResource(R.mipmap.standing_progress);
    }*/

    @OnClick(R.id.lt_main_title_left)
    public void onViewClicked() {
        ReviewDetailsAcitivy.this.finish();
    }
}
