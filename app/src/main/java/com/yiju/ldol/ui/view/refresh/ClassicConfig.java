package com.yiju.ldol.ui.view.refresh;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yiju.idol.R;

import java.text.SimpleDateFormat;
import java.util.Locale;

import me.dkzwm.widget.srl.utils.PixelUtl;

/**
 * Created by dkzwm on 2017/5/31.
 *
 * @author dkzwm
 */
public class ClassicConfig {
    private static final String SP_NAME = "sr_classic_last_update_time";
    private static final SimpleDateFormat sDataFormat = new SimpleDateFormat
            ("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    private ClassicConfig() {
    }

    public static void updateTime(@NonNull Context context, String key, long time) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, 0);
        if (!TextUtils.isEmpty(key)) {
            sharedPreferences.edit().putLong(key, time).apply();
        }
    }

    static void createClassicViews(RelativeLayout layout) {
        TextView textViewTitle = new TextView(layout.getContext());
        textViewTitle.setId(R.id.sr_classic_title);
        textViewTitle.setTextSize(12);
        textViewTitle.setTextColor(0xFFBAC1CB);
        LinearLayout textContainer = new LinearLayout(layout.getContext());
        textContainer.setOrientation(LinearLayout.VERTICAL);
        textContainer.setGravity(Gravity.CENTER_HORIZONTAL);
        textContainer.setId(R.id.sr_classic_text_container);
        LinearLayout.LayoutParams textViewTitleLP = new LinearLayout.LayoutParams(ViewGroup
                .LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textContainer.addView(textViewTitle, textViewTitleLP);
        RelativeLayout.LayoutParams textContainerLP = new RelativeLayout.LayoutParams(ViewGroup
                .LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textContainerLP.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(textContainer, textContainerLP);
        ImageView imageViewArrow = new ImageView(layout.getContext());
        imageViewArrow.setId(R.id.sr_classic_arrow);
        RelativeLayout.LayoutParams imageViewArrowLP = new RelativeLayout.LayoutParams(ViewGroup
                .LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int dp6 = PixelUtl.dp2px(layout.getContext(), 6);
        final int dp15 = PixelUtl.dp2px(layout.getContext(), 15);
        imageViewArrowLP.setMargins(dp6, dp6, dp15, dp6);
        imageViewArrowLP.addRule(RelativeLayout.LEFT_OF, R.id
                .sr_classic_text_container);
        imageViewArrowLP.addRule(RelativeLayout.CENTER_VERTICAL);
        layout.addView(imageViewArrow, imageViewArrowLP);
        ProgressBar progressBar = new ProgressBar(layout.getContext(), null, android.R.attr
                .progressBarStyleSmallInverse);
        progressBar.setId(R.id.sr_classic_progress);
        RelativeLayout.LayoutParams progressBarLP = new RelativeLayout.LayoutParams(ViewGroup
                .LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        progressBar.setIndeterminateDrawable(layout.getResources().getDrawable(R.drawable.progress_refresh));
        progressBarLP.setMargins(dp6, dp6, dp15, dp6);
        progressBarLP.addRule(RelativeLayout.LEFT_OF, R.id
                .sr_classic_text_container);
        progressBarLP.addRule(RelativeLayout.CENTER_VERTICAL);
        layout.addView(progressBar, progressBarLP);
    }
}
