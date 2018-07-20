package com.yiju.ldol.ui.view.danmaku;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.yiju.idol.R;

import java.util.ArrayList;

/**
 * Created by d on 2016/12/5.
 */

public class DanmakuView extends FrameLayout implements View.OnClickListener {

    private Context mContext;
    private DanmakuItem mDanmaku1;
    private DanmakuItem mDanmaku2;
    private DanmakuItem mDanmaku3;
    private ArrayList<ChatRoomMessage> danmakuList;

    public DanmakuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        danmakuList = new ArrayList<>();
        init();
    }

    private void init() {
        View v = inflate(mContext, R.layout.layout_danmaku, null);
        mDanmaku1 = (DanmakuItem) v.findViewById(R.id.danmaku1);
        mDanmaku2 = (DanmakuItem) v.findViewById(R.id.danmaku2);
        mDanmaku3 = (DanmakuItem) v.findViewById(R.id.danmaku3);
        mDanmaku1.setOnClickListener(this);
        mDanmaku2.setOnClickListener(this);
        mDanmaku3.setOnClickListener(this);
        DanmakuItem.OnAnimCanPlayListener onAnimCanPlayListener = new DanmakuItem.OnAnimCanPlayListener() {
            @Override
            public void onCanPlayNext() {
                //此时不会重叠，可以继续播放弹幕
                showDamaku();
            }

            @Override
            public void onAnimEnd() {
                //动画结束
                showDamaku();
            }
        };
        mDanmaku1.setOnAnimCanPlayListener(onAnimCanPlayListener);
        mDanmaku2.setOnAnimCanPlayListener(onAnimCanPlayListener);
        mDanmaku3.setOnAnimCanPlayListener(onAnimCanPlayListener);
        addView(v);
        //重新设置布局
        v.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
    }

    public interface OnItemClickListener {
        void onClick(ChatRoomMessage message);
    }

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public void addDanmaku(ChatRoomMessage message) {
        danmakuList.add(message);
        showDamaku();
    }

    /**
     * 弹幕动画
     */
    private void showDamaku() {
        if (danmakuList.size() == 0) {
            return;
        }
        if (!mDanmaku1.isAnimating() && mDanmaku2.isCanPlayNext() && mDanmaku3.isCanPlayNext()) {
            mDanmaku1.setData(danmakuList.get(0));
            danmakuList.remove(0);
        } else if (!mDanmaku2.isAnimating() && mDanmaku1.isCanPlayNext() && mDanmaku3.isCanPlayNext()) {
            mDanmaku2.setData(danmakuList.get(0));
            danmakuList.remove(0);
        } else if (!mDanmaku3.isAnimating() && mDanmaku1.isCanPlayNext() && mDanmaku2.isCanPlayNext()) {
            mDanmaku3.setData(danmakuList.get(0));
            danmakuList.remove(0);
        }
    }

    @Override
    public void onClick(View v) {
        if (mListener != null) {
            mListener.onClick((ChatRoomMessage) v.getTag());
        }
    }
}
