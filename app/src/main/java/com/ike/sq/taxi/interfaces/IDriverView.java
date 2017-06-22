package com.ike.sq.taxi.interfaces;

import com.ike.sq.taxi.base.view.BaseView;
import com.ike.sq.taxi.bean.AroundOrder;

import java.util.List;

/**
 * Created by T-BayMax on 2017/5/26.
 */

public interface IDriverView extends BaseView {
    void selectAroundOrder(List<AroundOrder> data);

    void driverRobOrder(String data);

    void driverTakeUser(String data);

    /**
     * 到达目的地
     * @param data
     */
    void arrivingDestinationView(AroundOrder data);

    void positionInputView(String data);
}
