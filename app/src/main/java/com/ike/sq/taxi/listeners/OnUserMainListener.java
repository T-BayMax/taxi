package com.ike.sq.taxi.listeners;

import com.ike.sq.taxi.bean.AroundOrder;

import java.util.List;

/**
 * Created by T-BayMax on 2017/6/17.
 */

public interface OnUserMainListener {

    void onCreateOrderCallBack(String data);

    void haveOrder();

    void positionInput(String data);

    void cancelOrder(String data);

    void userPayOrderListener(String data);

    void selectUseOrder(List<AroundOrder> data);

    void aroundCarListener(List<AroundOrder> data);

    void showError(String errorString);
}
