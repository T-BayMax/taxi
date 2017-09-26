package com.ike.sq.taxi.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ike.sq.taxi.bean.AroundOrder;
import com.ike.sq.taxi.bean.Code;
import com.ike.sq.taxi.bean.MotorManBean;
import com.ike.sq.taxi.listeners.OnUserMainListener;
import com.ike.sq.taxi.listeners.ReviewDetailsListener;
import com.ike.sq.taxi.network.CoreErrorConstants;
import com.ike.sq.taxi.network.HttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by T-BayMax on 2017/6/17.
 */

public class UserMainModel {
    /**
     * 用户叫车（生成订单）
     *
     * @param formData
     * @param listener
     */
    public void onCreateOrderCallBackModel(Map<String, String> formData, final OnUserMainListener listener) {
        HttpUtils.sendGsonPostRequest("/userFindCar", formData, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                listener.showError(e.toString());
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
                            listener.onCreateOrderCallBack(code.getData().getOrderId());
                            break;
                        case 1043:
                            listener.haveOrder();
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
     * 位置录入
     *
     * @param formData
     * @param listener
     */
    public void positionInput(Map<String, String> formData, final OnUserMainListener listener) {
        HttpUtils.sendGsonPostRequest("/positionInput", formData, new StringCallback() {
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
                            listener.positionInput("成功");
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
     * 正在进行中的订单
     *
     * @param formData
     * @param listener
     */
    public void selectUseOrder(Map<String, String> formData, final OnUserMainListener listener) {
        HttpUtils.sendGsonPostRequest("/selectUseOreder", formData, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                listener.showError(e.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    Gson gson = new Gson();
                    Type type = new TypeToken<Code<List<AroundOrder>>>() {
                    }.getType();
                    Code<List<AroundOrder>> code = gson.fromJson(response, type);
                    switch (code.getCode()) {
                        case 200:
                            listener.selectUseOrder(code.getData());
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
     * 查询四周司机
     *
     * @param formData
     * @param listener
     */
    public void aroundCar(Map<String, String> formData, final OnUserMainListener listener) {
        HttpUtils.sendGsonPostRequest("/aroundCar", formData, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                listener.showError(e.toString());
            }

            @Override
            public void onResponse(String response, int id) {
                try {
                    Gson gson = new Gson();
                    Type type = new TypeToken<Code<List<AroundOrder>>>() {
                    }.getType();
                    Code<List<AroundOrder>> code = gson.fromJson(response, type);
                    switch (code.getCode()) {
                        case 200:
                            listener.aroundCarListener(code.getData());
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
     * 取消订单
     *
     * @param formData
     * @param listener
     */
    public void cancelOrder(Map<String, String> formData, final OnUserMainListener listener) {
        HttpUtils.sendGsonPostRequest("/cancelOrder", formData, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                listener.showError(e.toString());
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
                            listener.cancelOrder("成功");
                            break;
                        default:
                            listener.showError(CoreErrorConstants.errors.get(code.getCode()));
                            break;
                    }
                } catch (Exception e) {
                    listener.showError("取消失败，请重试！");
                }
            }
        });
    }

    /**
     * 用户确认付款
     * @param formData
     * @param listener
     */
    public void userPayOrder(Map<String, String> formData, final OnUserMainListener listener) {
        HttpUtils.sendGsonPostRequest("/userPayOrder", formData, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                listener.showError(e.toString());
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
                            listener.userPayOrderListener("成功");
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
