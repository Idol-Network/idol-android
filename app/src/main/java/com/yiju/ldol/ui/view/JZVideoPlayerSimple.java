package com.yiju.ldol.ui.view;

import android.content.Context;
import android.util.AttributeSet;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yiju.idol.R;

import cn.jzvd.JZMediaManager;
import cn.jzvd.JZVideoPlayer;

/**
 * Created by Allan_Zhang on 2018/3/21.
 */

public class JZVideoPlayerSimple extends JZVideoPlayer {

    private OnCompletionListener listener;
    public SimpleDraweeView mSdvThumb;

    public JZVideoPlayerSimple(Context context) {
        super(context);
    }

    public JZVideoPlayerSimple(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getLayoutId() {
        return R.layout.jz_layout_simple;
    }

    @Override
    public void init(Context context) {
        super.init(context);
        mSdvThumb = findViewById(R.id.sdv_thumb);
    }

    @Override
    public void onPrepared() {
        super.onPrepared();
        try {
            //不播放声音
            JZMediaManager.instance().jzMediaInterface.setVolume(0f, 0f);
        } catch (IllegalStateException e) {
            //DO nothing
        }
    }


    @Override
    public void onStatePrepared() {
        super.onStatePrepared();
        mSdvThumb.setVisibility(GONE);
        if (listener != null) {
            listener.onStatePrepared();
        }
    }

    @Override
    public void onStateError() {
        super.onStateError();
        mSdvThumb.setVisibility(VISIBLE);
        if (listener != null) {
            listener.onStateError();
        }
    }

    @Override
    public void onStateAutoComplete() {
        super.onStateAutoComplete();
//        mSdvThumb.setVisibility(VISIBLE);
        if (listener != null) {
            listener.onStateAutoComplete();
        }
    }

    public void setOnCompletionListener(OnCompletionListener listener) {
        this.listener = listener;
    }

    public interface OnCompletionListener {
        void onStateAutoComplete();

        void onStatePrepared();

        void onStateError();
    }
}
