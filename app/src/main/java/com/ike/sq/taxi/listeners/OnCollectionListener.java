package com.ike.sq.taxi.listeners;

import com.ike.sq.taxi.bean.AroundOrder;

/**
 * Created by T-BayMax on 2017/6/5.
 */

public interface OnCollectionListener {
    void postCollection(AroundOrder data);

    void showError(String errorString);

    void driverArriveAddressConfirm(String data);
}
