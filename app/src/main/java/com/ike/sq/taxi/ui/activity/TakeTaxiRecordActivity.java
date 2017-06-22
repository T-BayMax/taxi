package com.ike.sq.taxi.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.andview.refreshview.XRefreshView;
import com.ike.sq.taxi.R;
import com.ike.sq.taxi.adapter.FeedForCommentListAdapter;
import com.ike.sq.taxi.adapter.TakeTaxiRecordAdapter;
import com.ike.sq.taxi.base.view.BaseMvpActivity;
import com.ike.sq.taxi.bean.AroundOrder;
import com.ike.sq.taxi.bean.CommentsBean;
import com.ike.sq.taxi.interfaces.IFeedForCommentListView;
import com.ike.sq.taxi.interfaces.ITakeTaxiRecordView;
import com.ike.sq.taxi.presenters.FeedForCommentPresenter;
import com.ike.sq.taxi.presenters.TakeTaxiRecordPresenter;
import com.ike.sq.taxi.utils.T;
import com.ike.sq.taxi.view.CustomerFooter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我的行程
 * Created by T-BayMax on 2017/3/18.
 */

public class TakeTaxiRecordActivity extends BaseMvpActivity<ITakeTaxiRecordView, TakeTaxiRecordPresenter> implements ITakeTaxiRecordView {

    @BindView(R.id.lt_main_title_left)
    TextView ltMainTitleLeft;
    @BindView(R.id.lt_main_title)
    TextView ltMainTitle;
    @BindView(R.id.lt_main_title_right)
    TextView ltMainTitleRight;


    @BindView(R.id.recycler_view_test_rv)
    RecyclerView recyclerView;
    @BindView(R.id.xrefreshview)
    XRefreshView xRefreshView;


    private View headerView;

    private boolean isRefresh = true;
    private boolean isComment = false;

    List<AroundOrder> personList = new ArrayList<AroundOrder>();
    LinearLayoutManager layoutManager;
    private int mLoadCount = 0;

    private int limit = 20;
    private int page = 1;

    TakeTaxiRecordAdapter adapter;

    private String userId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_for_comments);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {
        ltMainTitle.setText("我的行程");
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");

        xRefreshView.setPullLoadEnable(true);
        recyclerView.setHasFixedSize(true);

        adapter = new TakeTaxiRecordAdapter(personList, this);
        // 设置静默加载模式
//		xRefreshView1.setSilenceLoadMore();
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        // 静默加载模式不能设置footerview
        recyclerView.setAdapter(adapter);
        //设置刷新完成以后，headerview固定的时间

        xRefreshView.setPinnedTime(1500);

        xRefreshView.setMoveForHorizontal(true);
        xRefreshView.setPullLoadEnable(true);
        xRefreshView.setAutoLoadMore(false);

        //当需要使用数据不满一屏时不显示点击加载更多的效果时，解注释下面的三行代码
        //并注释掉第四行代码
        CustomerFooter customerFooter = new CustomerFooter(this);
        customerFooter.setRecyclerView(recyclerView);
       adapter.setCustomLoadMoreView(customerFooter);
        //adapter.setCustomLoadMoreView(new XRefreshViewFooter(this));
        xRefreshView.enableReleaseToLoadMore(true);
        xRefreshView.enableRecyclerViewPullUp(true);
        xRefreshView.enablePullUpWhenLoadCompleted(true);


        xRefreshView.setXRefreshViewListener(new XRefreshView.SimpleXRefreshListener() {

            @Override
            public void onRefresh(boolean isPullDown) {
                page = 1;
                initData();
            }

            @Override
            public void onLoadMore(boolean isSilence) {

                page++;
                initData();
            }
        });

    }

    private void initData() {
        isRefresh = true;
        Map<String, String> formData = new HashMap<String, String>(0);
        formData.put("userId", userId);
        formData.put("page",page+"");
        presenter.historicalJourney(formData);
    }

    @OnClick(R.id.lt_main_title_left)
    void leftClick() {
        TakeTaxiRecordActivity.this.finish();
    }


    FeedForCommentListAdapter.AdapterViewHolder viewHolder;
    CommentsBean commentsBean;


    @Override
    public TakeTaxiRecordPresenter initPresenter() {
        return new TakeTaxiRecordPresenter();
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showError(String errorString) {
        if (isRefresh)
            if (page == 1) {
                xRefreshView.stopRefresh(false);
            } else {
                xRefreshView.stopLoadMore(false);
            }
        T.showShort(TakeTaxiRecordActivity.this, errorString);
    }

    @Override
    public void historicalJourneyView(List<AroundOrder> data) {
        if (page == 1) {
            xRefreshView.stopRefresh(true);
        } else {
            xRefreshView.stopLoadMore(true);
        }
        adapter.setData(data, page);
    }

}
