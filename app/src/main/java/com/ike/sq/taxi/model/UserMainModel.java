package com.ike.sq.taxi.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ike.sq.taxi.bean.Code;
import com.ike.sq.taxi.bean.MotorManBean;
import com.ike.sq.taxi.listeners.OnUserMainListener;
import com.ike.sq.taxi.listeners.ReviewDetailsListener;
import com.ike.sq.taxi.network.CoreErrorConstants;
import com.ike.sq.taxi.network.HttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by T-BayMax on 2017/6/17.
 */

public class UserMainModel {
    /**
     * 用户叫车（生成订单）
     * @param formData
     * @param listener
     */
    public void onCreateOrderCallBackModel(Map<String,String> formData, final OnUserMainListener listener){
        HttpUtils.sendGsonPostRequest("/userFindCar",formData,new StringCallback() {
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
                        listener.onCreateOrderCallBack("呼叫成功，等待司机接单");
                        break;
                    case 1043:
                        listener.haveOrder();
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
    public void positionInput(Map<String,String> formData, final OnUserMainListener listener){
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
}
