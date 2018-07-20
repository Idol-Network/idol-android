package com.yiju.ldol.listener;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.yiju.idol.utils.DensityUtil;
import com.yiju.idol.utils.LogUtils;

/**
 * 实现监听左右滑动的事件，哪个view需要的时候直接setOnTouchListener就可以用了
 *
 * @author LinZhiquan
 */
public class GestureListener extends SimpleOnGestureListener implements OnTouchListener {
    /**
     * 滑动的最短距离
     */
    private int distance = 200;
    private int distance2 = 350;
    /**
     * 滑动的最小速度
     */
    private int velocity = 250;

    private GestureDetector gestureDetector;

    private OnGestureListener listener;

    public GestureListener(Context context, OnGestureListener listener) {
        super();
        gestureDetector = new GestureDetector(context, this);
        this.listener = listener;
        distance = DensityUtil.getScreenWidth() / 4;
        distance2 = DensityUtil.getScreenHeight() / 3;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (listener != null) {
            listener.onSingleTapUp();
        }
        return super.onSingleTapUp(e);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        // TODO Auto-generated method stub
        // e1：第1个ACTION_DOWN MotionEvent
        // e2：最后一个ACTION_MOVE MotionEvent
        // velocityX：X轴上的移动速度（像素/秒）
        // velocityY：Y轴上的移动速度（像素/秒）

        // 向左滑
        if (e1.getX() - e2.getX() > distance
                && Math.abs(velocityX) > velocity) {
            if (listener != null) {
                listener.onLeftFlipped();
            }
        }
        // 向右滑
        else if (e2.getX() - e1.getX() > distance
                && Math.abs(velocityX) > velocity) {
            if (listener != null) {
                listener.onRightFlipped();
            }
        }
        // 向下滑
        else if (e2.getY() - e1.getY() > distance2
                && Math.abs(velocityY) > velocity) {
            LogUtils.i("GestureListener", "向下滑");
            if (listener != null) {
                listener.onDownFlipped();
            }
        }
        // 向上滑
        else if (e1.getY() - e2.getY() > distance2
                && Math.abs(velocityY) > velocity) {
            LogUtils.i("GestureListener", "向上滑");
            if (listener != null) {
                listener.onUpFlipped();
            }
        }
        return false;//不拦截事件
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub
        gestureDetector.onTouchEvent(event);
        return false;//不拦截事件
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getVelocity() {
        return velocity;
    }

    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }

    public GestureDetector getGestureDetector() {
        return gestureDetector;
    }

    public void setGestureDetector(GestureDetector gestureDetector) {
        this.gestureDetector = gestureDetector;
    }


}

