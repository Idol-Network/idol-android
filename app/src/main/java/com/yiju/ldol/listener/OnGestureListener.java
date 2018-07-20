package com.yiju.ldol.listener;

/**
 * Created by Allan_Zhang on 2016/9/9.
 */
public interface OnGestureListener {

    void onLeftFlipped();

    void onRightFlipped();

    void onUpFlipped();

    void onDownFlipped();

    /**
     * 点击事件
     */
    void onSingleTapUp();
}
