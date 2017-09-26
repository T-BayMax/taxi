package com.ike.sq.taxi.bean;

import java.io.Serializable;

/**
 * 出车单
 * Created by T-BayMax on 2017/6/19.
 */

public class AroundOrder implements Serializable {
    private String id;               //订单id
    private String userId;          //
    private String orderId;         //订单id
    private String startTime;         //乘车计算开始时间
    private String endTime;         //结算结束时间
    private int status;//状态

    private String fromAddress;      //开始地址
    private String destination;        //终点地址
    private double kilometre;          //公里数
    private double money;           //价钱
    private String time;           //订单时间
    private String userPortraitUrl;   //用户头像
    private String fromDegree;      //终点地址经纬度 格式：经，维
    private String endDegree;       //结束经纬度
    private double longitude;        //经度
    private double latitude;         //纬度
    private String mobile;          //电话号码
    private String userName;        //
    private String licensePlate;
    private int drivingYear;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public String getEndDegree() {
        return endDegree;
    }

    public void setEndDegree(String endDegree) {
        this.endDegree = endDegree;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public int getDrivingYear() {
        return drivingYear;
    }

    public void setDrivingYear(int drivingYear) {
        this.drivingYear = drivingYear;
    }
}
