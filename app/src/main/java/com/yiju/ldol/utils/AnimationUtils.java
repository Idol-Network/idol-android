package com.yiju.ldol.utils;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;

import com.plattysoft.leonids.ParticleSystem;
import com.yiju.idol.R;

/**
 * Created by thbpc on 2018/3/28 0028.
 */

public class AnimationUtils {

    public static final int FADE_OUT_TIME = 2000;

    public static void startAnimation(View view) {
        new ParticleSystem((Activity) view.getContext(), 40, R.drawable.jinbi, 8000)
//                .setSpeedModuleAndAngleRange(0.01f, 0.2f, 45, 135)
                .setRotationSpeed(90)//设置自身旋转速度
                .setAcceleration(0.001f, 90)//设置加速度 和角度
                .setScaleRange(0.5f, 1f)//设置随机缩放大小
                .setFadeOut(FADE_OUT_TIME)//设置淡出时间
                .emitWithGravity(view, Gravity.TOP, 50, 500);
    }
}
