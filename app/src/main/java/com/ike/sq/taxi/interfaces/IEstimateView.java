package com.ike.sq.taxi.interfaces;

import com.ike.sq.taxi.base.view.BaseView;

/**
 * Created by T-BayMax on 2017/5/12.
 */

public interface IEstimateView extends BaseView {
    void appraiseView(String data);

    boolean checkInputInfo();
}
