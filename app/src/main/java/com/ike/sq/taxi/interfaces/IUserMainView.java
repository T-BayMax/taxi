package com.ike.sq.taxi.interfaces;

import com.ike.sq.taxi.base.view.BaseView;
import com.ike.sq.taxi.bean.AroundOrder;

import java.util.List;

/**
 * Created by T-BayMax on 2017/6/17.
 */

public interface IUserMainView extends BaseView {

    void createOrderCallBack(String data);

    void haveOrderCallBack();

    void cancelOrder(String data);

    void userPayOrderView(String data);

    void selectUseOrder(List<AroundOrder> data);

    void aroundCarView(List<AroundOrder> data);

    void positionInputView(String data);
}
