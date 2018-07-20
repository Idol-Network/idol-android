package com.yiju.ldol.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 2017/1/20
 */
public class TextProgressView extends View {

    private float[] radii1;
    private float[] radii2;

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        if (progress > maxProgress) {
            return;
        }
        invalidate();
    }

    public void setMaxProgress(float maxProgress) {
        this.maxProgress = maxProgress;
    }

    private float progress;
    private float maxProgress;
    private int mWidth, mHeight;

    public TextProgressView(Context context) {
        this(context, null);
    }

    public TextProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {
        backgroundPaint = new Paint();
        backgroundPaint.setAntiAlias(true);
        backgroundPaint.setColor(Color.parseColor("#7fDE3D41"));
        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setColor(Color.parseColor("#DE3D41"));
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(30);
        textPaint.setColor(Color.WHITE);
        radii1 = new float[]{
                10f, 10f,
                0f, 0f,
                0f, 0f,
                10f, 10f};
        radii2 = new float[]{
                0f, 0f,
                10f, 10f,
                10f, 10f,
                0f, 0f};
    }

    private Paint progressPaint;
    private Paint backgroundPaint;
    private Paint textPaint;

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float p = progress / maxProgress;

        /**绘制左边进度**/
        Path leftPath = new Path();
        RectF leftrectF = new RectF(
                0,
                mHeight / 2 - dipToPx(2),
                (mWidth - dipToPx(40)) * p,
                mHeight / 2 + dipToPx(2));

        leftPath.addRoundRect(leftrectF, radii1, Path.Direction.CW);
        canvas.drawPath(leftPath, progressPaint);

        /**绘制进度文字背景**/
        RectF progressrectF = new RectF(
                (mWidth - dipToPx(40)) * p,
                0,
                leftrectF.right + dipToPx(40),
                mHeight);
        canvas.drawRoundRect(
                progressrectF,
                dipToPx(10),
                dipToPx(10),
                progressPaint);

        /**绘制右边进度**/
        Path rightPath = new Path();
        RectF rightrectF = new RectF(
                progressrectF.right,
                mHeight / 2 - dipToPx(2),
                mWidth,
                mHeight / 2 + dipToPx(2));
        rightPath.addRoundRect(rightrectF, radii2, Path.Direction.CW);
        canvas.drawPath(rightPath, backgroundPaint);

        /**绘制进度文字**/
        String text = (int) progress + "%";
        Rect rect = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), rect);
        canvas.drawText(
                text,
                progressrectF.left + progressrectF.width() / 2 - rect.width() / 2,
                mHeight - rect.height() / 2,
                textPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
    }


    private int dipToPx(int dip) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }
}
