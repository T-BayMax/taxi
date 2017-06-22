package com.ike.sq.taxi.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ike.sq.taxi.bean.AroundOrder;
import com.ike.sq.taxi.bean.Code;
import com.ike.sq.taxi.listeners.OnDriverListener;
import com.ike.sq.taxi.network.CoreErrorConstants;
import com.ike.sq.taxi.network.HttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by T-BayMax on 2017/5/26.
 */

public class DriverModel {
    public void selectAroundOrder(Map<String,String> formData, final OnDriverListener listener){
        HttpUtils.sendGsonPostRequest("/selectAroundOrder",formData,new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                listener.showError(e.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                Type type = new TypeToken<Code<List<AroundOrder>>>() {
                }.getType();
                Code<List<AroundOrder>> code = gson.fromJson(response, type);
                switch (code.getCode()) {
                    case 200:
                        listener.selectAroundOrder(code.getData());
                        break;
                    default:
                        listener.showError(CoreErrorConstants.errors.get(code.getCode()));
                        break;
                }
            }
        });
    }

    /**
     * 位置录入
     * @param formData
     * @param listener
     */
    public void positionInput(Map<String,String> formData, final OnDriverListener listener){
        HttpUtils.sendGsonPostRequest("/positionInput",formData,new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                listener.showError(e.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                Type type = new TypeToken<Code>() {
                }.getType();
                Code code = gson.fromJson(response, type);
                switch (code.getCode()) {
                    case 200:
                        listener.positionInput("成功");
                        break;
                    default:
                        listener.showError(CoreErrorConstants.errors.get(code.getCode()));
                        break;
                }
            }
        });
    }

    /**
     * 接单
     * @param formData
     * @param listener
     */
    public void driverRobOrder(Map<String,String> formData, final OnDriverListener listener){
            HttpUtils.sendGsonPostRequest("/driverRobOrder",formData,new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    listener.showError(e.toString());
                }

                @Override
                public void onResponse(String response, int id) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<Code>() {
                    }.getType();
                    Code code = gson.fromJson(response, type);
                    switch (code.getCode()) {
                        case 200:
                            listener.driverRobOrder("抢单成功，请稍后");
                            break;
                        default:
                            listener.showError(CoreErrorConstants.errors.get(code.getCode()));
                            break;
                    }
                }
            });
    }

    /**
     * 接到乘客
     * @param formData
     * @param listener
     */
    public void driverTakeUser(Map<String,String> formData, final OnDriverListener listener){
        HttpUtils.sendGsonPostRequest("/driverTakeUser",formData,new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                listener.showError(e.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                Type type = new TypeToken<Code>() {
                }.getType();
                Code code = gson.fromJson(response, type);
                switch (code.getCode()) {
                    case 200:
                        listener.driverTakeUser("接到乘客");
                        break;
                    default:
                        listener.showError(CoreErrorConstants.errors.get(code.getCode()));
                        break;
                }
            }
        });
    }

    /**
     * 到达目的地
     * @param formData
     * @param listener
     */
    public void arrivingDestination(Map<String,String> formData, final OnDriverListener listener){
        HttpUtils.sendGsonPostRequest("/driverArriveAddress",formData,new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                listener.showError(e.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                Type type = new TypeToken<Code<AroundOrder>>() {
                }.getType();
                Code<AroundOrder> code = gson.fromJson(response, type);
                switch (code.getCode()) {
                    case 200:
                        listener.arrivingDestinationListener(code.getData());
                        break;
                    default:
                        listener.showError(CoreErrorConstants.errors.get(code.getCode()));
                        break;
                }
            }
        });
    }
}

