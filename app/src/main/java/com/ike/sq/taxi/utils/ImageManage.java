package com.ike.sq.taxi.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by T-BayMax on 2017/6/13.
 */

public class ImageManage {
    // 根据路径获得图片并压缩，返回bitmap用于显示
    public static Bitmap getSmallBitmap(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        // Decode bitmap with inSampleSize set
        options.inSampleSize = calculateInSampleSize(options, 800, 480);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    //计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / ((float) height>width?reqHeight:reqWidth));
            final int widthRatio = Math.round((float) width /( (float) height<width?reqHeight:reqWidth));
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }
}
