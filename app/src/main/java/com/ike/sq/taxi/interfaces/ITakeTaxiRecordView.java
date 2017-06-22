package com.ike.sq.taxi.interfaces;

import com.ike.sq.taxi.base.view.BaseView;
import com.ike.sq.taxi.bean.AroundOrder;
import com.ike.sq.taxi.bean.CommentsBean;

import java.util.List;

/**
 *Created by T-BayMax on 2017/3/20.
 */

public interface ITakeTaxiRecordView extends BaseView {
    void historicalJourneyView(List<AroundOrder> data);
}
