package com.ike.sq.taxi.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ike.sq.taxi.R;
import com.ike.sq.taxi.base.view.BaseMvpActivity;
import com.ike.sq.taxi.bean.AroundOrder;
import com.ike.sq.taxi.interfaces.IEstimateView;
import com.ike.sq.taxi.network.HttpUtils;
import com.ike.sq.taxi.presenters.EstimatePresenter;
import com.ike.sq.taxi.utils.CircleTransform;
import com.ike.sq.taxi.utils.DisplayUtils;
import com.ike.sq.taxi.utils.T;
import com.ike.sq.taxi.view.RatingBar;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

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
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.rb)
    RatingBar rb;
    @BindView(R.id.et_content)
    EditText etContent;

    private String userId;
    private String orderId;
    private String type;
    private AroundOrder order;

    private String starNumber;
    private String content;
    private ProgressDialog pd;

private static final int REACHED_RESULT=2;
    private AlertDialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estimate);
        ButterKnife.bind(this);
        initView();
        initChange();
    }

    private void initView() {
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        order = (AroundOrder) intent.getSerializableExtra("order");
        orderId = null != order.getId() ? order.getId() : order.getOrderId();
        type = intent.getStringExtra("type");
        ltMainTitle.setText("评价");
        ltMainTitleRight.setText("提交");
        ltMainTitleRight.setCompoundDrawables(null, null, null, null);
        rb.setClickable(true);//设置可否点击
        rb.setStar(0f);//设置显示的星星个数
        rb.setStepSize(RatingBar.StepSize.Half);//设置每次点击增加一颗星还是半颗星
        tvName.setText(order.getUserName());
        Picasso.with(EstimateActivity.this).load(HttpUtils.IMAGE_RUL + order.getUserPortraitUrl())
                .transform(new CircleTransform()).into(ivIcon);
    }

    private void initChange() {
        rb.setOnRatingChangeListener(new RatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(float ratingCount) {//点击星星变化后选中的个数
                starNumber = ((int) ratingCount) + "";
            }
        });
    }

    /**
     * 提交评价
     */
    private void userEvaluate() {
        pd = new ProgressDialog(EstimateActivity.this);
        pd.setMessage("正在评价中...");
        pd.show();
        Map<String, String> formData = new HashMap<String, String>(0);
        formData.put("userId", userId);
        formData.put("orderId", orderId);
        formData.put("type", type);
        formData.put("starNumber", starNumber);
        formData.put("content", content);
        presenter.userEvaluate(formData);
    }

    /**
     * 评价成功
     */
    public void showDialog() {
        dialog = new AlertDialog.Builder(this).create();
        dialog.show();
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = DisplayUtils.dp2px(EstimateActivity.this, 300);//定义宽度
        lp.height = DisplayUtils.dp2px(EstimateActivity.this, 260);//定义高度
        dialog.getWindow().setAttributes(lp);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        LayoutInflater mLayoutInflater = (LayoutInflater) this
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = mLayoutInflater.inflate(
                R.layout.view_estimate_complete, null);
        window.setContentView(view);
        TextView tv_record = (TextView) window.findViewById(R.id.tv_record);
        TextView tv_return_home = (TextView) window.findViewById(R.id.tv_return_home);

        tv_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EstimateActivity.this, FeedForCommentActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("type", type);
                startActivity(intent);
                dialog.dismiss();
                Intent it = new Intent();
                it.putExtra("finish", true);
                setResult(REACHED_RESULT, it);
                EstimateActivity.this.finish();
            }
        });
        tv_return_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.putExtra("finish", true);
                setResult(REACHED_RESULT, it);
                EstimateActivity.this.finish();
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
        T.showLong(EstimateActivity.this, errorString);
        if (null != pd)
            pd.dismiss();
    }

    @Override
    public void appraiseView(String data) {
        T.showLong(EstimateActivity.this, data);
        if (null != pd)
            pd.dismiss();
        showDialog();
    }

    @Override
    public boolean checkInputInfo() {
        content = etContent.getText().toString().trim();
        if (null == starNumber) {
            return false;
        }
        if (content.equals("")) {
            return false;
        }
        return true;
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
                if (checkInputInfo()) {
                    userEvaluate();
                }
                break;
        }
    }
}
