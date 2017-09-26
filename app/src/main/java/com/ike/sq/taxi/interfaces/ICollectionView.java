package com.ike.sq.taxi.interfaces;

import com.ike.sq.taxi.base.view.BaseView;
import com.ike.sq.taxi.bean.AroundOrder;

/**
 * Created by T-BayMax on 2017/6/5.
 */

public interface ICollectionView extends BaseView {
    void postCollection(AroundOrder data);

    /**
     * 确认完成订单
     * @param data
     */
    void driverArriveAddressConfirm(String data);
}
