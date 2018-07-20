package com.yiju.ldol.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class BackgroundProgress extends View {

    private int mWidth, mHeight;

    public BackgroundProgress(Context context) {
        this(context, null);
    }

    public BackgroundProgress(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BackgroundProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 设置左边进度
     *
     * @param progress
     */
    public void setLeftProgress(float progress) {
        this.leftProgress = progress;
        if (progress > leftMaxProgress) {
            return;
        }
        invalidate();
    }

    /**
     * 设置左边最大值
     *
     * @param maxProgress
     */
    public void setLeftMaxProgress(float maxProgress) {
        this.leftMaxProgress = maxProgress;
    }

    /**
     * 设置右边进度
     *
     * @param progress
     */
    public void setRightProgress(float progress) {
        this.rightProgress = progress;
        if (progress > rightMaxProgress) {
            return;
        }
        invalidate();
    }

    /**
     * 设置右边进度最大值
     *
     * @param maxProgress
     */
    public void setRightMaxProgress(float maxProgress) {
        this.rightMaxProgress = maxProgress;
    }

    private float leftProgress;
    private float leftMaxProgress;

    private float rightProgress;
    private float rightMaxProgress;

    private Paint leftPaint;
    private Paint rightPaint;

    private void init() {
        leftPaint = new Paint();
        leftPaint.setAntiAlias(true);
        leftPaint.setColor(Color.parseColor("#7f28B996"));
        rightPaint = new Paint();
        rightPaint.setAntiAlias(true);
        rightPaint.setColor(Color.parseColor("#7fFD2E42"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float leftP = leftProgress / leftMaxProgress;
        RectF leftRectF = new RectF(mWidth / 2 * (1 - leftP), 0, mWidth / 2, mHeight);
        canvas.drawRect(leftRectF, leftPaint);

        float rightP = rightProgress / rightMaxProgress;
        RectF rightRectf = new RectF(mWidth / 2, 0, mWidth / 2 * (1 + rightP), mHeight);
        canvas.drawRect(rightRectf, rightPaint);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }
}
