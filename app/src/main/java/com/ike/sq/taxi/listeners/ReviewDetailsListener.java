package com.ike.sq.taxi.listeners;

import com.ike.sq.taxi.bean.MotorManBean;

import java.util.List;

/**
 * Created by T-BayMax on 2017/6/3.
 */

public interface ReviewDetailsListener {
    public void getReviewDetailsListener(MotorManBean data);
    public void showError(String errorString);
}
