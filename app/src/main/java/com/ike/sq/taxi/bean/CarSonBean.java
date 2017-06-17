package com.ike.sq.taxi.bean;

import java.io.Serializable;

/**
 * 车型信息
 * Created by T-BayMax on 2017/5/15.
 */

public class CarSonBean implements Serializable {
    private String  car;
    private String  type;

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
