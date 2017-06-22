package com.ike.sq.taxi.presenters;


import com.ike.sq.taxi.base.presenter.BasePersenter;
import com.ike.sq.taxi.bean.AroundOrder;
import com.ike.sq.taxi.bean.CommentsBean;
import com.ike.sq.taxi.interfaces.IFeedForCommentListView;
import com.ike.sq.taxi.interfaces.ITakeTaxiRecordView;
import com.ike.sq.taxi.listeners.OnFeedForCommentListListener;
import com.ike.sq.taxi.listeners.OnTakeTaxiRecordListener;
import com.ike.sq.taxi.model.FeedForCommentModel;
import com.ike.sq.taxi.model.TakeTaxiRecordModel;

import java.util.List;
import java.util.Map;


/**
 *Created by T-BayMax on 2017/3/20.
 */

public class TakeTaxiRecordPresenter extends BasePersenter<ITakeTaxiRecordView> implements OnTakeTaxiRecordListener {
    private TakeTaxiRecordModel model;

    public TakeTaxiRecordPresenter() {
        model = new TakeTaxiRecordModel();
    }

    public void historicalJourney( Map<String, String> formData ) {
        model.historicalJourneyData(formData, this);
    }


    @Override
    public void historicalJourneyListener(List<AroundOrder> data) {
        mView.historicalJourneyView(data);
    }

    @Override
    public void showError(String errorString) {
        mView.showError(errorString);
    }
}
