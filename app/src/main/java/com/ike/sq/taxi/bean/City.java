package com.ike.sq.taxi.bean;

/**
 * Created by T-BayMax on 2017/5/15.
 */

public class City {
    private String name;
    private String pinyin;

    public City(){}
    public City(String n, String py){
        name = n;
        pinyin = py;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }
}
