package com.ike.sq.taxi.listeners;

import com.ike.sq.taxi.bean.CommentsBean;

import java.util.List;

/**
 *Created by T-BayMax on 2017/3/20.
 */

public interface OnFeedForCommentListListener {
    void getFeedCommentInfo(List<CommentsBean> data);


    void showError(String errorString);
}
