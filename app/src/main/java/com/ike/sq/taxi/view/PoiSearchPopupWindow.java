package com.ike.sq.taxi.view;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.PopupWindow;

/**
 * Created by T-BayMax on 2017/5/19.
 */

public class PoiSearchPopupWindow extends PopupWindow {

    public PoiSearchPopupWindow(View contentView, int width, int height, boolean focusable){
        super(contentView,width,height,focusable);
        this.setTouchable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new BitmapDrawable(contentView.getResources(), (Bitmap) null));
    }
}
