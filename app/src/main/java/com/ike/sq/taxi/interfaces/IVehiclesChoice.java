package com.ike.sq.taxi.interfaces;

import com.ike.sq.taxi.base.view.BaseView;
import com.ike.sq.taxi.bean.CarBrandBean;
import com.ike.sq.taxi.bean.CarSonBean;

import java.util.List;

/**
 * Created by T-BayMax on 2017/5/15.
 */

public interface IVehiclesChoice extends BaseView {
    void brandView(List<CarBrandBean> data);
    void informationView(List<CarSonBean> data);
}
