package com.ike.sq.taxi.presenters;

import com.ike.sq.taxi.base.presenter.BasePersenter;
import com.ike.sq.taxi.interfaces.IAddMotorManView;
import com.ike.sq.taxi.listeners.OnAddMotorManListener;
import com.ike.sq.taxi.model.AddMotorManModel;

import java.io.File;
import java.util.Map;

/**
 * Created by T-BayMax on 2017/5/12.
 */

public class AddMotorManPresenter extends BasePersenter<IAddMotorManView> implements OnAddMotorManListener {
    private AddMotorManModel model;

    public AddMotorManPresenter() {
        model = new AddMotorManModel();
    }
    public void motorManPresenter(Map<String,String> params,Map<String, File> files,String fileName){
        model.add(params,files,fileName,this);
    }

    @Override
    public void motorManListeners(String data) {
        mView.motorManView(data);
    }

    @Override
    public void showError(String errorString) {
        mView.showError(errorString);
    }
}
