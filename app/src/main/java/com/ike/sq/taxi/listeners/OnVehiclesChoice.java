package com.ike.sq.taxi.listeners;

import com.ike.sq.taxi.bean.CarBrandBean;
import com.ike.sq.taxi.bean.CarSonBean;

import java.util.List;

/**
 * Created by T-BayMax on 2017/5/15.
 */

public interface OnVehiclesChoice {

    void brandListener(List<CarBrandBean> data);

    void informationListener(List<CarSonBean> data);

    void showError(String errorString);
}
