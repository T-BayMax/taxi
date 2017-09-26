package com.ike.sq.taxi.bean;

import com.umeng.message.entity.UMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by T-BayMax on 2017/7/3.
 */

public class PushMessages extends UMessage {
    public PushMessages(JSONObject jsonObject) throws JSONException {
        super(jsonObject);
    }
    private AroundOrder driver;

    public AroundOrder getDriver() {
        return driver;
    }

    public void setDriver(AroundOrder driver) {
        this.driver = driver;
    }
}
