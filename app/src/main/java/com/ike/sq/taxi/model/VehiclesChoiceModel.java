package com.ike.sq.taxi.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ike.sq.taxi.bean.CarBrandBean;
import com.ike.sq.taxi.bean.Code;
import com.ike.sq.taxi.bean.JsonResult;
import com.ike.sq.taxi.listeners.OnVehiclesChoice;
import com.ike.sq.taxi.network.HttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;

/**
 * Created by T-BayMax on 2017/5/15.
 */

public class VehiclesChoiceModel {
    public void getBrand(String appkey,final OnVehiclesChoice listener){

        HttpUtils.getRequest("http://apicloud.mob.com/car/brand/query?key="+appkey,new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                listener.showError(e.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    Gson gson = new Gson();
                    Type type = new TypeToken<JsonResult<List<CarBrandBean>>>() {
                    }.getType();
                    JsonResult<List<CarBrandBean>> code = gson.fromJson(response, type);
                    switch (code.getRetCode()) {
                        case 200:
                            listener.brandListener(code.getResult());
                            break;
                        case 0:
                            listener.showError("查询失败");
                            break;
                    }
                }catch (Exception e){
                    listener.showError("系统异常");
                }
            }
        });
    }
}
