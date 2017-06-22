package com.ike.sq.taxi.listeners;

import com.ike.sq.taxi.bean.AroundOrder;
import com.ike.sq.taxi.bean.CommentsBean;

import java.util.List;

/**
 *Created by T-BayMax on 2017/3/20.
 */

public interface OnTakeTaxiRecordListener {
    void historicalJourneyListener(List<AroundOrder> data);


    void showError(String errorString);
}
