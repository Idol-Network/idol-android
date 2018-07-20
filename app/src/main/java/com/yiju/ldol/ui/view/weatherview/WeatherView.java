package com.yiju.ldol.ui.view.weatherview;


import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import com.plattysoft.leonids.ParticleSystem;
import com.yiju.idol.R;

public class WeatherView extends View {
    private int mRainTime = Constants.rainTime;
    private int mSnowTime = Constants.snowTime;
    private int mFadeOutTime = Constants.fadeOutTime;

    private int mRainParticles = Constants.rainParticles;
    private int mSnowParticles = Constants.snowParticles;

    private int mFps = Constants.fps;
    private int mRainAngle = Constants.rainAngle;
    private int mSnowAngle = Constants.snowAngle;


    private ParticleSystem mParticleSystem;
    private Constants.weatherStatus mCurrentWeather = Constants.weatherStatus.SUN;
    Context mContext;
    Activity mActivity;
    boolean isPlaying = false;

    public WeatherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        if (!isInEditMode()) {
            mActivity = (Activity) getContext();
            initOptions(context, attrs);
        }
    }

    private void initOptions(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WeatherView, 0, 0);
        int startingWeather, lifeTime, fadeOutTime, numParticles, fps, angle;
        try {
            startingWeather = typedArray.getInt(R.styleable.WeatherView_startingWeather, 0);
            lifeTime = typedArray.getInt(R.styleable.WeatherView_lifeTime, -1);
            fadeOutTime = typedArray.getInt(R.styleable.WeatherView_fadeOutTime, -1);
            numParticles = typedArray.getInt(R.styleable.WeatherView_numParticles, -1);
            fps = typedArray.getInt(R.styleable.WeatherView_fps, -1);
            angle = typedArray.getInt(R.styleable.WeatherView_angle, -200);

            setWeather(Constants.weatherStatus.values()[startingWeather])
                    .setLifeTime(lifeTime)
                    .setFadeOutTime(fadeOutTime)
                    .setParticles(numParticles)
                    .setFPS(fps)
                    .setAngle(angle);

        } finally {
            typedArray.recycle();
        }
    }

    public WeatherView setWeather(Constants.weatherStatus status) {
        mCurrentWeather = status;
        return this;
    }

    public WeatherView setAngle(int angle) {
        setSnowAngle(angle);
        return this;
    }

    public WeatherView setLifeTime(int lifeTime) {
        setSnowTime(lifeTime);
        return this;
    }

    public WeatherView setParticles(int numParticles) {
        setSnowParticles(numParticles);
        return this;
    }

    private Bitmap mBitmap;

    public WeatherView setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
        return this;
    }

    public void startAnimation() {
//        stopAnimation();
        mParticleSystem = new ParticleSystem(mActivity, 150, mBitmap, 8000)
                .setSpeedByComponentsRange(0f, 0f, 0.05f, 0.1f)
                .setSpeedModuleAndAngleRange(0f, 0.1f, 65, 115)
                .setRotationSpeed(50)
                .setScaleRange(0.2f, 0.7f)
                .setInitialRotation(-mSnowAngle)
                .setFadeOut(this.mFadeOutTime, new AccelerateInterpolator());


        mParticleSystem.setFPS(getFPS());

        if (mParticleSystem != null) {
            this.post(new Runnable() {
                @Override
                public void run() {
                    int width = getWidth();
                    int height = getHeight();
                    if (width != 0 && height != 0 && !isPlaying) {
                        emitParticles();
                    }
                }
            });
        }
    }

    private void emitParticles() {
        mParticleSystem.emitWithGravity(this, Gravity.TOP, mSnowParticles);
        isPlaying = true;
    }

    public WeatherView cancelAnimation() {
        if (mParticleSystem != null) {
            mParticleSystem.cancel();
            isPlaying = false;
        }
        return this;
    }

    public WeatherView stopAnimation() {
        if (mParticleSystem != null) {
            mParticleSystem.stopEmitting();
            isPlaying = false;
        }
        return this;
    }

    private void setRainTime(int rainTime) {
        this.mRainTime = rainTime >= 0 ? rainTime : Constants.rainTime;
    }

    public WeatherView setFadeOutTime(int fadeOutTime) {
        this.mFadeOutTime = fadeOutTime >= 0 ? fadeOutTime : Constants.fadeOutTime;
        return this;
    }

    public int getFadeOutTime() {
        return mFadeOutTime;
    }

    public int getLifeTime() {
        return (getCurrentWeather() == Constants.weatherStatus.RAIN ? mRainTime : mSnowTime);
    }

    public int getParticles() {
        return (getCurrentWeather() == Constants.weatherStatus.RAIN ? mRainParticles : mSnowParticles);
    }

    public int getAngle() {
        return (getCurrentWeather() == Constants.weatherStatus.RAIN ? mRainAngle : mSnowAngle);
    }

    private void setSnowTime(int snowTime) {
        this.mSnowTime = snowTime >= 0 ? snowTime : Constants.snowTime;
    }

    private void setRainParticles(int rainParticles) {
        this.mRainParticles = rainParticles >= 0 ? rainParticles : Constants.rainParticles;
    }

    public WeatherView setFPS(int fps) {
        this.mFps = (fps > 7 && fps < 100) ? fps : Constants.fps;

        //Must cancel in order to avoid overlapping with particles
        if (mParticleSystem != null) {
            cancelAnimation();
        }
        return this;
    }

    public int getFPS() {
        return mFps;
    }

    private void setRainAngle(int angle) {
        this.mRainAngle = angle > -181 && angle < 181 ? angle : Constants.rainAngle;
    }

    private void setSnowAngle(int angle) {
        this.mSnowAngle = angle > -181 && angle < 181 ? angle : Constants.snowAngle;
    }

    public Constants.weatherStatus getCurrentWeather() {
        return mCurrentWeather;
    }

    private void setSnowParticles(int snowParticles) {
        this.mSnowParticles = snowParticles >= 0 ? snowParticles : Constants.snowParticles;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public WeatherView resetConfiguration() {
        setRainTime(-1);
        setFadeOutTime(-1);
        setSnowTime(-1);
        setRainParticles(-1);
        setSnowParticles(-1);
        setFPS(-1);
        setRainAngle(-200);
        setSnowAngle(-200);
        return this;
    }
}
