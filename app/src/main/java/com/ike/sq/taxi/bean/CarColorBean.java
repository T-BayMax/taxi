package com.ike.sq.taxi.bean;

/**
 * 车辆颜色
 * Created by T-BayMax on 2017/5/16.
 */

public class CarColorBean {
    private int colorImage;
    private String colorName;

    public CarColorBean(int colorImage, String colorName) {
        this.colorImage = colorImage;
        this.colorName = colorName;
    }

    public int getColorImage() {
        return colorImage;
    }

    public void setColorImage(int colorImage) {
        this.colorImage = colorImage;
    }

    public String getColorName() {
        return colorName;
    }

    public void setColorName(String colorName) {
        this.colorName = colorName;
    }
}
