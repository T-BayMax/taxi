package com.ike.sq.taxi.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ike.sq.taxi.bean.Code;
import com.ike.sq.taxi.bean.MotorManBean;
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

public class ReviewDetailsModel {
    public void getReviewDetailsModel(Map<String,String> formData, final ReviewDetailsListener listener){
        HttpUtils.sendGsonPostRequest("/selectDriverRegister",formData,new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                listener.showError(e.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson = new Gson();
                Type type = new TypeToken<Code<MotorManBean>>() {
                }.getType();
                Code<MotorManBean> code = gson.fromJson(response, type);
                switch (code.getCode()) {
                    case 200:
                        listener.getReviewDetailsListener(code.getData());
                        break;
                    default:
                        listener.showError(CoreErrorConstants.errors.get(code.getCode()));
                        break;
                }
            }
        });
    }
}
