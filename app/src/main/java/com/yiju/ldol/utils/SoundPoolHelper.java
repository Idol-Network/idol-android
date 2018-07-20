package com.yiju.ldol.utils;

import android.media.AudioManager;
import android.media.SoundPool;
import android.text.TextUtils;

import com.yiju.idol.base.Constant;


/**
 * Created by thbpc on 2016/12/16 0016.
 */

public class SoundPoolHelper {
    private static SoundPool soundPool;

    public static void playSound(String soundName) {
        if (TextUtils.isEmpty(soundName)) {
            return;
        }
        if (soundPool == null) {
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 5);
            soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
                @Override
                public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                    soundPool.play(i, 1, 1, 0, 0, 1);//id，左右音量大小，优先级，loop，播放速率
                }
            });
        }
        String soundPath = Constant.SD_PATH + Constant.ANIMATION_PATH + soundName + ".mp3";
        soundPool.load(soundPath, 1);

    }

    public static void onDestroy() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}