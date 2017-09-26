package com.ike.sq.taxi.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ike.sq.taxi.bean.AroundOrder;
import com.ike.sq.taxi.bean.Code;
import com.ike.sq.taxi.listeners.OnCollectionListener;
import com.ike.sq.taxi.listeners.ReviewDetailsListener;
import com.ike.sq.taxi.network.CoreErrorConstants;
import com.ike.sq.taxi.network.HttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by T-BayMax on 2017/6/3.
 */

public class CollectionModel {
    /**
     * 司机到达目的地结算
     * @param formData
     * @param listener
     */
    public void postCollectionModel(Map<String, String> formData, final OnCollectionListener listener) {
        HttpUtils.sendGsonPostRequest("/driverArriveAddress", formData, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                listener.showError("系统异常");
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    Gson gson = new Gson();
                    Type type = new TypeToken<Code<AroundOrder>>() {
                    }.getType();
                    Code<AroundOrder> code = gson.fromJson(response, type);
                    switch (code.getCode()) {
                        case 200:
                            listener.postCollection(code.getData());
                            break;
                        default:
                            listener.showError(CoreErrorConstants.errors.get(code.getCode()));
                            break;
                    }
                } catch (Exception e) {
                    listener.showError("系统异常");
                }
            }
        });
    }

    /**
     * 司机到达目的地结算-发送用户确认
     * @param formData
     * @param listener
     */
    public void driverArriveAddressConfirm(Map<String, String> formData, final OnCollectionListener listener) {
        HttpUtils.sendGsonPostRequest("/driverArriveAddressConfirm", formData, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                listener.showError("系统异常");
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    Gson gson = new Gson();
                    Type type = new TypeToken<Code>() {
                    }.getType();
                    Code code = gson.fromJson(response, type);
                    switch (code.getCode()) {
                        case 200:
                            listener.driverArriveAddressConfirm(code.getData().toString());
                            break;
                        default:
                            listener.showError(CoreErrorConstants.errors.get(code.getCode()));
                            break;
                    }
                } catch (Exception e) {
                    listener.showError("系统异常");
                }
            }
        });
    }
}
