package com.yiju.ldol.ui.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class EnterTextView extends TextView {

    public EnterTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setTypeface();
    }

    public EnterTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface();
    }

    public EnterTextView(Context context) {
        super(context);
        setTypeface();
    }

    public void setTypeface() {
        super.setTypeface(Typeface.createFromAsset(getContext().getAssets(),
                "fonts/helvetica_oblique.ttf"));
    }
}
