package com.ike.sq.taxi.presenters;

import com.ike.sq.taxi.base.presenter.BasePersenter;
import com.ike.sq.taxi.bean.AroundOrder;
import com.ike.sq.taxi.interfaces.ICollectionView;
import com.ike.sq.taxi.interfaces.IReviewDetailsView;
import com.ike.sq.taxi.listeners.OnCollectionListener;
import com.ike.sq.taxi.listeners.ReviewDetailsListener;
import com.ike.sq.taxi.model.CollectionModel;
import com.ike.sq.taxi.model.ReviewDetailsModel;

import java.util.List;
import java.util.Map;

/**
 * Created by T-BayMax on 2017/6/3.
 */

public class CollectionPresenters extends BasePersenter<ICollectionView> implements OnCollectionListener {
    private CollectionModel model;

    public CollectionPresenters() {
        model = new CollectionModel();
    }


    public void getReviewDetails(Map<String, String> formData) {
        model.postCollectionModel(formData, this);
    }

    public void driverArriveConfirm(Map<String, String> formData) {
        model.driverArriveAddressConfirm(formData, this);
    }

    @Override
    public void postCollection(AroundOrder data) {
        mView.postCollection(data);
    }

    @Override
    public void showError(String errorString) {
        mView.showError(errorString);
    }

    @Override
    public void driverArriveAddressConfirm(String data) {
        mView.driverArriveAddressConfirm(data);
    }
}
