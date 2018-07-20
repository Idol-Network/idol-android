package com.yiju.ldol.ui.view.likeanimation;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;

/**
 * Created by gaoxuan on 2016/8/14.
 */
public class HeartView extends AnimImageView {
    private Paint paint;

    public HeartView(Context context) {
        super(context);
        paint = new Paint();
    }

    public HeartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
    }

    @Override
    public void setColor(int color) {
    }


}
