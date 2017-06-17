package com.ike.sq.taxi.presenters;

import com.ike.sq.taxi.base.presenter.BasePersenter;
import com.ike.sq.taxi.interfaces.IAddMotorManView;
import com.ike.sq.taxi.interfaces.IEstimateView;
import com.ike.sq.taxi.listeners.OnAddMotorManListener;
import com.ike.sq.taxi.listeners.OnEstimateListener;
import com.ike.sq.taxi.model.AddMotorManModel;
import com.ike.sq.taxi.model.EstimateModel;

import java.io.File;
import java.util.Map;

/**
 * Created by T-BayMax on 2017/5/12.
 */

public class EstimatePresenter extends BasePersenter<IEstimateView> implements OnEstimateListener {
    private EstimateModel model;

    public EstimatePresenter() {
        model = new EstimateModel();
    }
    public void motorManPresenter(Map<String,String> params,Map<String, File> files,String fileName){
        model.appraise(params,files,fileName,this);
    }

    @Override
    public void appraiseListeners(String data) {
        mView.appraiseView(data);
    }

    @Override
    public void showError(String errorString) {
        mView.showError(errorString);
    }
}
