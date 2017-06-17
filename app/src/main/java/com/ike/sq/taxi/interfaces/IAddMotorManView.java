package com.ike.sq.taxi.interfaces;

import com.ike.sq.taxi.base.view.BaseView;

/**
 * Created by T-BayMax on 2017/5/12.
 */

public interface IAddMotorManView extends BaseView {
    void motorManView(String data);

    boolean checkInputInfo();
}
