package com.ike.sq.taxi.presenters;

import com.ike.sq.taxi.base.presenter.BasePersenter;
import com.ike.sq.taxi.bean.MotorManBean;
import com.ike.sq.taxi.interfaces.IReviewDetailsView;
import com.ike.sq.taxi.listeners.ReviewDetailsListener;
import com.ike.sq.taxi.model.ReviewDetailsModel;

import java.util.List;
import java.util.Map;

/**
 * Created by T-BayMax on 2017/6/3.
 */

public class ReviewDetailsPresenters extends BasePersenter<IReviewDetailsView> implements ReviewDetailsListener {
    private ReviewDetailsModel model;

    public ReviewDetailsPresenters() {
        model = new ReviewDetailsModel();
    }


    public void getReviewDetails(Map<String,String> formData) {
        model.getReviewDetailsModel(formData, this);
    }

    @Override
    public void getReviewDetailsListener(MotorManBean data) {
        mView.getReviewDetailsViewData(data);
    }

    @Override
    public void showError(String errorString) {
        mView.showError(errorString);
    }
}
