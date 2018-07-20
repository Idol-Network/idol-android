package com.yiju.ldol.ui.view.refresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yiju.idol.R;

import me.dkzwm.widget.srl.SmoothRefreshLayout;
import me.dkzwm.widget.srl.extra.IRefreshView;
import me.dkzwm.widget.srl.indicator.IIndicator;
import me.dkzwm.widget.srl.utils.PixelUtl;

/**
 * @author dkzwm
 */
public abstract class AbsClassicRefreshView<T extends IIndicator> extends RelativeLayout
        implements IRefreshView<T> {
    private static final Interpolator sLinearInterpolator = new LinearInterpolator();
    @RefreshViewStyle
    protected int mStyle = STYLE_DEFAULT;
    protected int mDefaultHeightInDP = 64;
    protected RotateAnimation mFlipAnimation;
    protected RotateAnimation mReverseFlipAnimation;
    protected TextView mTitleTextView;
    protected TextView mLastUpdateTextView;
    protected ImageView mArrowImageView;
    protected ProgressBar mProgressBar;
    protected String mLastUpdateTimeKey;
    protected boolean mShouldShowLastUpdate;
    protected long mLastUpdateTime = -1;
    protected int mRotateAniTime = 200;

    public AbsClassicRefreshView(Context context) {
        this(context, null);
    }

    public AbsClassicRefreshView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbsClassicRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            final TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable
                    .AbsClassicRefreshView, 0, 0);
            @RefreshViewStyle
            int style = arr.getInt(R.styleable.AbsClassicRefreshView_pull_style, mStyle);
            mStyle = style;
            arr.recycle();
        }
        mFlipAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF,
                0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mFlipAnimation.setInterpolator(sLinearInterpolator);
        mFlipAnimation.setDuration(mRotateAniTime);
        mFlipAnimation.setFillAfter(true);

        mReverseFlipAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF,
                0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseFlipAnimation.setInterpolator(sLinearInterpolator);
        mReverseFlipAnimation.setDuration(mRotateAniTime);
        mReverseFlipAnimation.setFillAfter(true);
        ClassicConfig.createClassicViews(this);
        mArrowImageView = (ImageView) findViewById(R.id.sr_classic_arrow);
        mTitleTextView = (TextView) findViewById(R.id.sr_classic_title);
        mLastUpdateTextView = (TextView) findViewById(R.id.sr_classic_last_update);
        mProgressBar = (ProgressBar) findViewById(R.id.sr_classic_progress);
        mArrowImageView.clearAnimation();
        mArrowImageView.setVisibility(VISIBLE);
        mProgressBar.setVisibility(INVISIBLE);
    }

    public TextView getLastUpdateTextView() {
        return mLastUpdateTextView;
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mFlipAnimation.cancel();
        mReverseFlipAnimation.cancel();
    }

    @Override
    public int getStyle() {
        return mStyle;
    }

    public void setStyle(@RefreshViewStyle int style) {
        mStyle = style;
        requestLayout();
    }

    public void setDefaultHeightInDP(@IntRange(from = 0) int defaultHeightInDP) {
        mDefaultHeightInDP = defaultHeightInDP;
    }

    @Override
    public int getCustomHeight() {
        return PixelUtl.dp2px(getContext(), mDefaultHeightInDP);
    }

    @NonNull
    @Override
    public View getView() {
        return this;
    }

    @Override
    public void onReset(SmoothRefreshLayout frame) {
        mArrowImageView.clearAnimation();
        mArrowImageView.setVisibility(VISIBLE);
        mProgressBar.setVisibility(INVISIBLE);
        mShouldShowLastUpdate = true;
    }

    @Override
    public void onFingerUp(SmoothRefreshLayout layout, T indicator) {

    }

    @Override
    public void onPureScrollPositionChanged(SmoothRefreshLayout layout, byte status, T indicator) {
        if (indicator.hasJustLeftStartPosition()) {
            mArrowImageView.clearAnimation();
            mArrowImageView.setVisibility(INVISIBLE);
            mProgressBar.setVisibility(INVISIBLE);
            mTitleTextView.setVisibility(GONE);
            mArrowImageView.setVisibility(GONE);
            mLastUpdateTextView.setVisibility(GONE);
            mShouldShowLastUpdate = false;
        }
    }

    public void setRotateAniTime(int time) {
        if (time == mRotateAniTime || time <= 0) {
            return;
        }
        mRotateAniTime = time;
        mFlipAnimation.setDuration(mRotateAniTime);
        mReverseFlipAnimation.setDuration(mRotateAniTime);
    }

    public void setLastUpdateTimeKey(String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        mLastUpdateTimeKey = key;
    }

    public void setTitleTextColor(@ColorInt int color) {
        mTitleTextView.setTextColor(color);
    }

    public void setLastUpdateTextColor(@ColorInt int color) {
        mLastUpdateTextView.setTextColor(color);
    }

}
