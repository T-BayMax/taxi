package com.ike.sq.taxi.presenters;

import com.ike.sq.taxi.base.presenter.BasePersenter;
import com.ike.sq.taxi.interfaces.IDriverView;
import com.ike.sq.taxi.listeners.OnDriverListener;
import com.ike.sq.taxi.model.DriverModel;

/**
 * Created by T-BayMax on 2017/5/26.
 */

public class DriverPresenter extends BasePersenter<IDriverView> implements OnDriverListener {
    private DriverModel model;
}
