package com.ike.sq.taxi.presenters;

import com.ike.sq.taxi.base.presenter.BasePersenter;
import com.ike.sq.taxi.interfaces.ICollectionView;
import com.ike.sq.taxi.interfaces.IReviewDetailsView;
import com.ike.sq.taxi.listeners.OnCollectionListener;
import com.ike.sq.taxi.listeners.ReviewDetailsListener;
import com.ike.sq.taxi.model.CollectionModel;
import com.ike.sq.taxi.model.ReviewDetailsModel;

import java.util.List;

/**
 * Created by T-BayMax on 2017/6/3.
 */

public class CollectionPresenters extends BasePersenter<ICollectionView> implements OnCollectionListener {
    private CollectionModel model;

    public CollectionPresenters() {
        model = new CollectionModel();
    }


    public void getReviewDetails(String formData) {
        model.postCollectionModel(formData, this);
    }

    @Override
    public void postCollection(String data) {
        mView.postCollection(data);
    }

    @Override
    public void showError(String errorString) {
        mView.showError(errorString);
    }
}
