package com.ike.sq.taxi.interfaces;

import com.ike.sq.taxi.base.view.BaseView;
import com.ike.sq.taxi.bean.MotorManBean;

import java.util.List;

/**
 * Created by T-BayMax on 2017/6/3.
 */

public interface IReviewDetailsView extends BaseView {
    public void getReviewDetailsViewData(MotorManBean formData);
}
