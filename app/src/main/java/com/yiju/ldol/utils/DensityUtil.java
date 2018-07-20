package com.yiju.ldol.utils;

import android.content.Context;
import android.util.DisplayMetrics;

import com.yiju.idol.base.App;

public class DensityUtil {

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {
        final float scale = App.getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue) {
        final float scale = App.getContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getScreenWidth() {
        //获取屏幕信息
        //获取屏幕信息
        DisplayMetrics dm = new DisplayMetrics();
        dm = App.getContext().getResources().getDisplayMetrics();
        return dm.widthPixels; // 屏幕高（像素，如：1280px）
    }

    public static int getScreenHeight() {
        //获取屏幕信息
        //获取屏幕信息
        DisplayMetrics dm = new DisplayMetrics();
        dm = App.getContext().getResources().getDisplayMetrics();
        return dm.heightPixels; // 屏幕高（像素，如：1280px）
    }

    public static int getStatusBarHeight(Context context) {
        int resourceId = App.getContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return App.getContext().getResources().getDimensionPixelSize(resourceId);
        } else {
            return 0;
        }
    }

    /**
     * 获取屏幕dpi
     *
     * @param context
     * @return
     */
    public static float getDensity(Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return density;
    }
}  