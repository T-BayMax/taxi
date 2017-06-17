package com.ike.sq.taxi.listeners;

/**
 * Created by T-BayMax on 2017/6/17.
 */

public interface OnUserMainListener {

    void onCreateOrderCallBack(String data);

    void positionInput(String data);

    void showError(String errorString);
}
