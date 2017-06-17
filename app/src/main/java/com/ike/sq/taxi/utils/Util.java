package com.ike.sq.taxi.utils;

import android.text.TextUtils;

/**
 * Created by shixi_tianrui1 on 16-9-28.
 */
public class Util {

    public static String getFirstLetter(String pinyin) {
        if (TextUtils.isEmpty(pinyin)) return null;
        return pinyin.substring(0, 1);
    }
}
