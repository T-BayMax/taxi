package com.ike.sq.taxi.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 车型
 * Created by T-BayMax on 2017/5/15.
 */

public class CarBrandBean implements Serializable {
    private String  name;
    private String sortLetters;  //显示数据拼音的首字母
    private List<CarSonBean> son;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }

    public List<CarSonBean> getSon() {
        return son;
    }

    public void setSon(List<CarSonBean> son) {
        this.son = son;
    }
}
