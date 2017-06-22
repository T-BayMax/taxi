package com.ike.sq.taxi.presenters;


import com.ike.sq.taxi.base.presenter.BasePersenter;
import com.ike.sq.taxi.bean.CommentsBean;
import com.ike.sq.taxi.interfaces.IFeedForCommentListView;
import com.ike.sq.taxi.listeners.OnFeedForCommentListListener;
import com.ike.sq.taxi.model.FeedForCommentModel;

import java.util.List;
import java.util.Map;


/**
 *Created by T-BayMax on 2017/3/20.
 */

public class FeedForCommentPresenter extends BasePersenter<IFeedForCommentListView> implements OnFeedForCommentListListener {
    private FeedForCommentModel model;

    public FeedForCommentPresenter() {
        model = new FeedForCommentModel();
    }

    public void FeedCommentInfo( Map<String, String> formData ) {
        model.getFeedCommentInfo(formData, this);
    }


    @Override
    public void getFeedCommentInfo(List<CommentsBean> data) {
        mView.setFeedForCommentListData(data);
    }

    @Override
    public void showError(String errorString) {
        mView.showError(errorString);
    }
}
