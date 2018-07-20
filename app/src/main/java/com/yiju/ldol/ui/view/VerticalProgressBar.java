package com.yiju.ldol.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.yiju.idol.R;


/**
 * Created by zhanghengzhen on 2016/6/7.
 * 体力
 */
public class VerticalProgressBar extends ImageView {

    private int maxValue = 10000;
    private int currentValue;

    /**
     * @see <a href="http://developer.android.com/reference/android/graphics/drawable/ClipDrawable.html">ClipDrawable</a>
     */

    public VerticalProgressBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setImageResource(R.drawable.bg_probar_strength);
    }

    /**
     * 图形填充 值从0-10000
     *
     * @param value
     */
    public void setValue(int value) {
        currentValue = value;
        int i = (int) (((float) value / (float) maxValue) * 10000);
        if (i < 0) {
            i = 0;
        } else if (i > 10000) {
            i = 10000;
        }
        setImageLevel(i);
    }

    public int getValue() {
        return currentValue;
    }

    public void setMax(int maxValue) {
        this.maxValue = maxValue;
    }


}