package com.ike.sq.taxi.utils;

import com.ike.sq.taxi.bean.CarBrandBean;

import java.util.Comparator;

/**
 * Created by T-BayMax on 2017/5/16.
 */

public class PinyinComparator implements Comparator<CarBrandBean> {

    public int compare(CarBrandBean o1, CarBrandBean o2) {
        //这里主要是用来对ListView里面的数据根据ABCDEFG...来排序
        if (o2.getSortLetters().equals("#")) {
            return -1;
        } else if (o1.getSortLetters().equals("#")) {
            return 1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());
        }
    }
}
