package com.yiju.ldol.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.yiju.idol.R;


/**
 * 带百分比的进度条
 * Created by Allan_Zhang on 2018/5/11.
 */

public class TextProgressBar extends ProgressBar {

    private int mMeasuredWidth;
    private int strictWidth;
    private float textSize;
    private Bitmap mThumb;
    private Drawable drawable;
    private TextPaint mPaint;
    private float mOffset;
    private StaticLayout layout;
    private int textTop;

    public TextProgressBar(Context context) {
        super(context);
    }

    public TextProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TextProgressBar);
        drawable = ta.getDrawable(R.styleable.TextProgressBar_thumb);
        textSize = ta.getDimensionPixelSize(R.styleable.TextProgressBar_textSize, 12);
        ta.recycle();
        mPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mPaint.density = context.getResources().getDisplayMetrics().density;
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(textSize);
    }

    public TextProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawThumb(canvas);
        drawText(canvas);
    }

    private void drawThumb(Canvas canvas) {
        mThumb = getBitmapDraw(drawable, true);
        strictWidth = mMeasuredWidth - mThumb.getWidth();
        int progress = getProgress();
        mOffset = (float) progress / (float) 100 * strictWidth;
        canvas.drawBitmap(mThumb, mOffset, 0, mPaint);
    }

    private void drawText(Canvas canvas) {
        float measureText = mPaint.measureText(getProgress() + "%");
        float marginLeft = mOffset - measureText / 2 + mThumb.getWidth() / 2;
        canvas.drawText(getProgress() + "%", marginLeft, textTop, mPaint);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMeasuredWidth = getMeasuredWidth();
        layout = new StaticLayout("", mPaint, mMeasuredWidth - getPaddingLeft() - getPaddingRight(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0f, true);
        textTop = layout.getLineBaseline(0) + getPaddingTop();
    }

    private Bitmap getBitmapDraw(Drawable drawable, boolean isThumb) {
        if (drawable == null) {
            return null;
        }
        int width;
        int height;
        width = drawable.getIntrinsicWidth();
        height = drawable.getIntrinsicHeight();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
