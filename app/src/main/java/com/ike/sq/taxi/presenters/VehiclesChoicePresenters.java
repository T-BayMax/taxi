package com.ike.sq.taxi.presenters;

import com.ike.sq.taxi.base.presenter.BasePersenter;
import com.ike.sq.taxi.bean.CarBrandBean;
import com.ike.sq.taxi.bean.CarSonBean;
import com.ike.sq.taxi.interfaces.IVehiclesChoice;
import com.ike.sq.taxi.listeners.OnVehiclesChoice;
import com.ike.sq.taxi.model.VehiclesChoiceModel;

import java.util.List;

/**
 * Created by T-BayMax on 2017/5/15.
 */

public class VehiclesChoicePresenters extends BasePersenter<IVehiclesChoice> implements OnVehiclesChoice {
    private VehiclesChoiceModel model;

    public VehiclesChoicePresenters() {
        model = new VehiclesChoiceModel();
    }

    public void getBrand(String key) {
        model.getBrand(key, this);
    }

    @Override
    public void brandListener(List<CarBrandBean> data) {
        mView.brandView(data);
    }

    @Override
    public void informationListener(List<CarSonBean> data) {
        mView.informationView(data);
    }

    @Override
    public void showError(String errorString) {
        mView.showError(errorString);
    }
}
