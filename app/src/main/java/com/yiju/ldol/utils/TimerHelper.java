package com.yiju.ldol.utils;

import android.os.CountDownTimer;

/**
 * Created by thbpc on 2016/12/16 0016.
 */

public class TimerHelper {
    private static CountDownTimer timer;
    public static boolean timerIsStop = true;

    /**
     * @param time  秒
     * @param callBack
     */
    public static void startCountDownTime(long time, final CallBack callBack) {
        timerIsStop = false;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new CountDownTimer(time * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                timerIsStop = true;
                if (callBack != null) {
                    callBack.endTming();
                }
            }

        };
        timer.start();// 开始计时
    }

    public interface CallBack {
        void endTming();
    }
}
