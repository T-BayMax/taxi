package com.ike.sq.taxi.interfaces;

import com.ike.sq.taxi.base.view.BaseView;

/**
 * Created by T-BayMax on 2017/6/17.
 */

public interface IUserMainView extends BaseView {

    void createOrderCallBack(String data);

    void positionInputView(String data);
}
