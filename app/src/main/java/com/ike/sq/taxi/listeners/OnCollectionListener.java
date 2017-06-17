package com.ike.sq.taxi.listeners;

/**
 * Created by T-BayMax on 2017/6/5.
 */

public interface OnCollectionListener {
    void postCollection(String data);
    void showError(String errorString);
}
