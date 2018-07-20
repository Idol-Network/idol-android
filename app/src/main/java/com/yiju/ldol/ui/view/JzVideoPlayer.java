package com.yiju.ldol.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import cn.jzvd.JZVideoPlayerStandard;

/**
 * Created by thbpc on 2018/3/29 0029.
 */

public class JzVideoPlayer extends JZVideoPlayerStandard {
    public JzVideoPlayer(Context context) {
        super(context);
    }

    public JzVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onStatePause() {
        super.onStatePause();
        if (mStateListener!=null){
            mStateListener.onPause();
        }
    }

    @Override
    public void onStatePlaying() {
        super.onStatePlaying();
        if (mStateListener!=null){
            mStateListener.onStart();
        }
    }


    public void setOnPlayerStateListener(OnPlayerStateListener mStateListener) {
        this.mStateListener = mStateListener;
    }

    OnPlayerStateListener mStateListener;

    public interface OnPlayerStateListener {
        void onStart();

        void onPause();
    }
}
