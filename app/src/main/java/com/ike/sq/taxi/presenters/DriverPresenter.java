package com.ike.sq.taxi.presenters;

import com.ike.sq.taxi.base.presenter.BasePersenter;
import com.ike.sq.taxi.bean.AroundOrder;
import com.ike.sq.taxi.interfaces.IDriverView;
import com.ike.sq.taxi.listeners.OnDriverListener;
import com.ike.sq.taxi.model.DriverModel;

import java.util.List;
import java.util.Map;

/**
 * Created by T-BayMax on 2017/5/26.
 */

public class DriverPresenter extends BasePersenter<IDriverView> implements OnDriverListener {
    private DriverModel model;

    public DriverPresenter() {
        model = new DriverModel();
    }

    public void getAroundOrder(Map<String, String> formData) {
        model.selectAroundOrder(formData, this);
    }
    public void positionInputPresenter(Map<String ,String> formData){
        model.positionInput(formData,this);
    }
    public void driverRobOrder(Map<String, String> formData) {
        model.driverRobOrder(formData, this);
    }
    public void takeUser(Map<String, String> formData){
        model.driverTakeUser(formData,this);
    }
    public void driverArriveAddress(Map<String, String> formData){
        model.arrivingDestination(formData,this);
    }

    @Override
    public void selectAroundOrder(List<AroundOrder> data) {
        mView.selectAroundOrder(data);
    }

    @Override
    public void driverRobOrder(String data) {
        mView.driverRobOrder(data);
    }

    @Override
    public void driverTakeUser(String data) {
        mView.driverTakeUser(data);
    }

    @Override
    public void positionInput(String data) {
        mView.positionInputView(data);
    }

    @Override
    public void arrivingDestinationListener(AroundOrder data) {
        mView.arrivingDestinationView(data);
    }

    @Override
    public void showError(String errorString) {
        mView.showError(errorString);
    }
}
