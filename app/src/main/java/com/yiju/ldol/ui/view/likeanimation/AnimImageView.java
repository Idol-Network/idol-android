package com.yiju.ldol.ui.view.likeanimation;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.yiju.idol.R;

import java.util.ArrayList;
import java.util.List;

/**
 */
public abstract class AnimImageView extends ImageView {
    private float value;
    private boolean isAnimEnded;

    public AnimImageView(Context context) {
        super(context);
    }

    public AnimImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        List<Drawable> drawableList = new ArrayList<Drawable>();
        drawableList.add(getResources().getDrawable(R.drawable.zan0001));
        drawableList.add(getResources().getDrawable(R.drawable.zan0002));
        drawableList.add(getResources().getDrawable(R.drawable.zan0003));
        drawableList.add(getResources().getDrawable(R.drawable.zan0004));
        drawableList.add(getResources().getDrawable(R.drawable.zan0005));


        int heartDrawableIndex;
        heartDrawableIndex = (int) (drawableList.size() * Math.random());
        setImageDrawable(drawableList.get(heartDrawableIndex));

    }


    public void setValue(float value) {
        this.value = value;
    }

    public float getValue() {
        return value;
    }

    public void setAnimEnded(boolean isAnimEnded) {
        this.isAnimEnded = isAnimEnded;
    }

    public boolean isAnimEnded() {
        return isAnimEnded;
    }

    public abstract void setColor(int color);
}
