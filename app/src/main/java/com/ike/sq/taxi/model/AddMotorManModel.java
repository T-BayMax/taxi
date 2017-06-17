package com.ike.sq.taxi.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ike.sq.taxi.bean.Code;
import com.ike.sq.taxi.bean.MotorManBean;
import com.ike.sq.taxi.listeners.OnAddMotorManListener;
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

public class AddMotorManModel {
    public void add(Map<String, String> params, Map<String,File> files, String fileName, final OnAddMotorManListener listener) {

        HttpUtils.sendFormatPostRequest("/driverRegister", params,null, files, fileName, new StringCallback() {
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
                        listener.motorManListeners("申请提交成功，请耐心等待审核！");
                        break;
                    default:
                        listener.showError(CoreErrorConstants.errors.get(code.getCode()));
                        break;
                }
            }
        });
    }
}
