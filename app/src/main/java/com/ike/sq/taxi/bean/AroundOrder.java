package com.ike.sq.taxi.bean;

import java.io.Serializable;

/**
 * 出车单
 * Created by T-BayMax on 2017/6/19.
 */

public class AroundOrder implements Serializable {
    private String id;               //订单id
    private String fromAddress;      //开始地址
    private String destination;        //终点地址
    private double kilometre;          //公里数
    private double money;           //价钱
    private String time;           //订单时间
    private String userPortraitUrl;   //用户头像
    private String fromDegree;      //终点地址经纬度 格式：经，维
    private String longitude;        //经度
    private String latitude;         //纬度


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public double getKilometre() {
        return kilometre;
    }

    public void setKilometre(double kilometre) {
        this.kilometre = kilometre;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUserPortraitUrl() {
        return userPortraitUrl;
    }

    public void setUserPortraitUrl(String userPortraitUrl) {
        this.userPortraitUrl = userPortraitUrl;
    }

    public String getFromDegree() {
        return fromDegree;
    }

    public void setFromDegree(String fromDegree) {
        this.fromDegree = fromDegree;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
