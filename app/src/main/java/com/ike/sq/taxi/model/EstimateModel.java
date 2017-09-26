package com.ike.sq.taxi.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ike.sq.taxi.bean.Code;
import com.ike.sq.taxi.bean.MotorManBean;
import com.ike.sq.taxi.listeners.OnAddMotorManListener;
import com.ike.sq.taxi.listeners.OnEstimateListener;
import com.ike.sq.taxi.network.CoreErrorConstants;
import com.ike.sq.taxi.network.HttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by T-BayMax on 2017/5/12.
 */

public class EstimateModel {
    public void appraise(Map<String, String> params, final OnEstimateListener listener) {

        HttpUtils.sendGsonPostRequest("/userEvaluate", params, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                listener.showError("系统异常");
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    Gson gson = new Gson();
                    Type type = new TypeToken<Code<MotorManBean>>() {
                    }.getType();
                    Code<MotorManBean> code = gson.fromJson(response, type);
                    switch (code.getCode()) {
                        case 200:
                            listener.appraiseListeners("评论提交成功！");
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
