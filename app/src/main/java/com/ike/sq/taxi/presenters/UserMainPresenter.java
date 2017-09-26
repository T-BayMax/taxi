package com.ike.sq.taxi.presenters;

import com.ike.sq.taxi.base.presenter.BasePersenter;
import com.ike.sq.taxi.bean.AroundOrder;
import com.ike.sq.taxi.interfaces.IUserMainView;
import com.ike.sq.taxi.listeners.OnUserMainListener;
import com.ike.sq.taxi.model.UserMainModel;

import java.util.List;
import java.util.Map;

/**
 * Created by T-BayMax on 2017/6/17.
 */

public class UserMainPresenter extends BasePersenter<IUserMainView> implements OnUserMainListener {
    private UserMainModel model;

    public UserMainPresenter() {
        model = new UserMainModel();
    }

    public void createOrder(Map<String, String> formData) {
        model.onCreateOrderCallBackModel(formData, this);
    }

    public void selectUseOrder(Map<String, String> formData) {
        model.selectUseOrder(formData, this);
    }

    public void cancelOrder(Map<String, String> formData) {
        model.cancelOrder(formData, this);
    }

    public void userPayOrder(Map<String, String> formData){
        model.userPayOrder(formData,this);
    }

    public void aroundCar(Map<String, String> formData) {
        model.aroundCar(formData,this);
    }

    public void positionInputPresenter(Map<String, String> formData) {
        model.positionInput(formData, this);
    }

    @Override
    public void onCreateOrderCallBack(String data) {
        mView.createOrderCallBack(data);
    }

    @Override
    public void haveOrder() {
        mView.haveOrderCallBack();
    }

    @Override
    public void positionInput(String data) {
        mView.positionInputView(data);
    }

    @Override
    public void cancelOrder(String data) {
        mView.cancelOrder(data);
    }

    @Override
    public void userPayOrderListener(String data) {
        mView.userPayOrderView(data);
    }

    @Override
    public void selectUseOrder(List<AroundOrder> data) {
        mView.selectUseOrder(data);
    }

    @Override
    public void aroundCarListener(List<AroundOrder> data) {
        mView.aroundCarView(data);
    }

    @Override
    public void showError(String errorString) {
        mView.showError(errorString);
    }
}
