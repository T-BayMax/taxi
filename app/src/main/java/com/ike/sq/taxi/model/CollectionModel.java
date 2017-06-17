package com.ike.sq.taxi.model;

import com.ike.sq.taxi.listeners.OnCollectionListener;
import com.ike.sq.taxi.listeners.ReviewDetailsListener;
import com.ike.sq.taxi.network.HttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * Created by T-BayMax on 2017/6/3.
 */

public class CollectionModel {
    public void postCollectionModel(String formData, final OnCollectionListener listener){
        HttpUtils.sendGsonPostRequest("getdata",formData,new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                listener.showError(e.toString());
            }

            @Override
            public void onResponse(String response, int id) {

            }
        });
    }
}
