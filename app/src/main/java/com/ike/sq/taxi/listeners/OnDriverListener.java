package com.ike.sq.taxi.listeners;

import com.ike.sq.taxi.bean.AroundOrder;

import java.util.List;

/**
 * Created by T-BayMax on 2017/5/26.
 */

public interface OnDriverListener {

    void selectAroundOrder(List<AroundOrder> data);

    void driverRobOrder(String data);

    void driverTakeUser(String data);

    void positionInput(String data);

    void selectUseOrder(List<AroundOrder> data);

    void arrivingDestinationListener(AroundOrder data);//到达目的地

    void showError(String errorString);
}

