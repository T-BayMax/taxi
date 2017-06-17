package com.ike.sq.taxi.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ike.sq.taxi.R;
import com.ike.sq.taxi.base.view.BaseMvpActivity;
import com.ike.sq.taxi.interfaces.IEstimateView;
import com.ike.sq.taxi.presenters.EstimatePresenter;
import com.ike.sq.taxi.view.RatingBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 评价
 * Created by T-BayMax on 2017/5/25.
 */

public class EstimateActivity extends BaseMvpActivity<IEstimateView, EstimatePresenter> implements IEstimateView {

    @BindView(R.id.lt_main_title_left)
    TextView ltMainTitleLeft;
    @BindView(R.id.lt_main_title)
    TextView ltMainTitle;
    @BindView(R.id.lt_main_title_right)
    TextView ltMainTitleRight;
    @BindView(R.id.iv_icon)
    ImageView ivIcon;
    @BindView(R.id.rb)
    RatingBar rb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate);
        ButterKnife.bind(this);
        initView();
        initChange();
    }
    private void initView(){
        ltMainTitle.setText("评价");
        ltMainTitleRight.setText("提交");
        ltMainTitleRight.setCompoundDrawables(null,null,null,null);
        rb.setClickable(true);//设置可否点击
        rb.setStar(0f);//设置显示的星星个数
        rb.setStepSize(RatingBar.StepSize.Half);//设置每次点击增加一颗星还是半颗星

    }
    private void initChange(){
        rb.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(float ratingCount) {//点击星星变化后选中的个数
                // rb.setStar(ratingCount);//设置显示的星星个数
            }
        });
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
    public void appraiseView(String data) {

    }

    @Override
    public boolean checkInputInfo() {
        return false;
    }

    @Override
    public EstimatePresenter initPresenter() {
        return new EstimatePresenter();
    }

    @OnClick({R.id.lt_main_title_left, R.id.lt_main_title_right})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.lt_main_title_left:
                EstimateActivity.this.finish();
                break;
            case R.id.lt_main_title_right:
                break;
        }
    }
}
