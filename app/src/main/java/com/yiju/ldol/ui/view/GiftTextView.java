package com.yiju.ldol.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import java.lang.reflect.Field;

public class GiftTextView extends TextView {

    private Paint mTextPaint;
    private int textColor;

    public GiftTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTextPaint = getPaint();
        setTypeface();
    }

    public GiftTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTextPaint = getPaint();
        setTypeface();
    }

    public GiftTextView(Context context) {
        super(context);
        mTextPaint = getPaint();
        setTypeface();
    }

    public void setTypeface() {
        super.setTypeface(Typeface.createFromAsset(getContext().getAssets(),
                "fonts/huawenxinwei.ttf"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (textColor == 0) {
            //保存默认字体颜色
            textColor = getCurrentTextColor();
        }
        // 描外层
        //super.setTextColor(Color.BLUE); // 不能直接这么设，如此会导致递归
        setTextColorUseReflection(mTextPaint, Color.WHITE);
        mTextPaint.setStrokeWidth(1);  // 描边宽度
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE); //描边种类
        mTextPaint.setFakeBoldText(true); // 外层text采用粗体
        mTextPaint.setShadowLayer(1, 1, 1, getShadowColor()); //字体的阴影效果，可以忽略
        super.onDraw(canvas);

        // 描内层，恢复原先的画笔
        //super.setTextColor(Color.BLUE); // 不能直接这么设，如此会导致递归
        setTextColorUseReflection(mTextPaint, textColor);
        mTextPaint.setStrokeWidth(0);
        mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTextPaint.setFakeBoldText(false);
        mTextPaint.setShadowLayer(0, 0, 0, 0);
        super.onDraw(canvas);
    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
        textColor = color;
        invalidate();
    }

    private void setTextColorUseReflection(Paint m_TextPaint, int color) {
        Field textColorField;
        try {
            textColorField = TextView.class.getDeclaredField("mCurTextColor");
            textColorField.setAccessible(true);
            textColorField.set(this, color);
            textColorField.setAccessible(false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        m_TextPaint.setColor(color);
    }

}
