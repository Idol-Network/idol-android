package com.yiju.ldol.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.yiju.idol.R;


/**
 * 仿iphone带进度的进度条，线程安全的View，可直接在线程中更新进度
 *
 * @author xiaanming
 */
public class RoundProgressBar extends View {
    /**
     * 画笔对象的引用
     */
    private Paint paint;

    /**
     * 底层颜色
     */
    private int backColor;

    /**
     * 圆环的颜色
     */
    private int roundColor;

    /**
     * 圆环进度的颜色
     */
    private int roundProgressColor;

    /**
     * 中间进度百分比的字符串的颜色
     */
    private int textColor;

    /**
     * 中间进度百分比的字符串的字体
     */
    private float textSize;

    /**
     * 圆环的宽度
     */
    private float roundWidth;

    /**
     * 最大进度
     */
    private int max;

    /**
     * 当前进度
     */
    private int progress;
    /**
     * 是否显示中间的进度
     */
    private boolean textIsDisplayable;

    /**
     * 进度的风格，实心或者空心
     */
    private int style;

    public static final int STROKE = 0;
    public static final int FILL = 1;

    /**
     * 正在倒计时
     */
    public boolean isCountDown;
    private boolean isFirst = true;//用于标记是显示“连击”还是显示数字“3”

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        paint = new Paint();
        paint.setAntiAlias(true);  //消除锯齿

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.RoundProgressBar);

        //获取自定义属性和默认值
        roundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, Color.RED);
        backColor = mTypedArray.getColor(R.styleable.RoundProgressBar_backColor, Color.BLUE);
        roundProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor, Color.GREEN);
        textColor = mTypedArray.getColor(R.styleable.RoundProgressBar_textColor, Color.GREEN);
        textSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundTextSize, 15);
        roundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 5);
        max = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 30);
        textIsDisplayable = mTypedArray.getBoolean(R.styleable.RoundProgressBar_textIsDisplayable, true);
        style = mTypedArray.getInt(R.styleable.RoundProgressBar_style, 0);

        mTypedArray.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /**
         * 画最外层的大圆环
         */
        int centre = getWidth() / 2; //获取圆心的x坐标
        int radius = (int) (centre - roundWidth / 2); //圆环的半径

        paint.setColor(backColor);  //设置进度的颜色
        paint.setStyle(Paint.Style.FILL); //设置空心
        canvas.drawCircle(centre, centre, radius, paint); //画出圆环

        /**
         * 画圆弧 ，画圆环的进度
         */

        //设置进度是实心还是空心
        paint.setStrokeWidth(roundWidth); //设置圆环的宽度
        paint.setColor(roundProgressColor);  //设置进度的颜色
        RectF oval = new RectF(centre - radius, centre - radius, centre
                + radius, centre + radius);  //用于定义的圆弧的形状和大小的界限

        switch (style) {
            case STROKE: {
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawArc(oval, 0, 360 * progress / max, false, paint);  //根据进度画圆弧
                break;
            }
            case FILL: {
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                if (progress != 0)
                    canvas.drawArc(oval, -90, 360 * progress / max, true, paint);  //根据进度画圆弧
                break;
            }
        }

        paint.setColor(roundColor); //设置圆环的颜色
        paint.setStyle(Paint.Style.STROKE); //设置空心
        paint.setStrokeWidth(roundWidth); //设置圆环的宽度

        canvas.drawCircle(centre, centre, radius, paint); //画出圆环

        /**
         * 画进度百分比
         */
        if (isCountDown) {
            paint.setStyle(Paint.Style.FILL); //设置空心
            paint.setColor(textColor);
            paint.setTextSize(textSize);
            paint.setTypeface(Typeface.DEFAULT); //设置字体
//        int percent = (int) (((float) progress / (float) max) * 100);  //中间的进度百分比，先转换成float在进行除法运算，不然都为0
            float textWidth = paint.measureText("1");   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间

//        if (textIsDisplayable && percent != 0 && style == STROKE) {
            int timeLeft = max - progress;
            if (timeLeft == 20) {
                if (isFirst && finishedListener != null) {
                    finishedListener.onStart();
                }
                isFirst = false;

            }
            if (timeLeft > 20) {
                if (!isFirst)
                    canvas.drawText("3", centre - textWidth / 2, centre + textSize / 5 * 2, paint); //画出进度百分比
            } else if (timeLeft > 10) {
                canvas.drawText("2", centre - textWidth / 2, centre + textSize / 5 * 2, paint);
            } else if (timeLeft > 0) {
                canvas.drawText("1", centre - textWidth / 2, centre + textSize / 5 * 2, paint);
            } else {
                canvas.drawText("0", centre - textWidth / 2, centre + textSize / 5 * 2, paint);
            }
//        }
        }
    }

    private Runnable run = new Runnable() {
        @Override
        public void run() {
            invalidate();
            if (progress >= max) {
                removeCallbacks(this);
                if (finishedListener != null) {
                    finishedListener.onFinished();
                }
                isCountDown = false;
                progress = 0;
                invalidate();
                isFirst = true;
                return;
            }
            postDelayed(this, 100);
            progress++;
        }
    };

    /**
     * 开始倒计时
     */
    public void startCountDown() {
//        if (finishedListener != null) {
//            finishedListener.onStart();
//        }
        isCountDown = true;
        if (progress > 0) {
            //连续点击，隐藏“连击”
            if (finishedListener != null) {
                finishedListener.onStart();
            }
            isFirst = false;
        }
        removeCallbacks(run);
        progress = 0;
        post(run);
    }

    /**
     * 停止倒计时
     */
    public void stopCountDown() {
        removeCallbacks(run);
        if (finishedListener != null) {
            finishedListener.onFinished();
        }
        isCountDown = false;
        progress = 0;
        invalidate();
        isFirst = true;
    }

    public synchronized int getMax() {
        return max;
    }

    /**
     * 设置进度的最大值
     *
     * @param max
     */
    public synchronized void setMax(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("max not less than 0");
        }
        this.max = max;
    }

    /**
     * 获取进度.需要同步
     *
     * @return
     */
    public synchronized int getProgress() {
        return progress;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     * @param progress
     */
    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (progress > max) {
            progress = max;
        }
        if (progress <= max) {
            this.progress = progress;
            postInvalidate();
        }

    }


    public int getCricleColor() {
        return roundColor;
    }

    public void setCricleColor(int cricleColor) {
        this.roundColor = cricleColor;
    }

    public int getCricleProgressColor() {
        return roundProgressColor;
    }

    public void setCricleProgressColor(int cricleProgressColor) {
        this.roundProgressColor = cricleProgressColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public float getRoundWidth() {
        return roundWidth;
    }

    public void setRoundWidth(float roundWidth) {
        this.roundWidth = roundWidth;
    }

    private OnCountDownListener finishedListener;

    public void setOnCountDownListener(OnCountDownListener listener) {
        this.finishedListener = listener;
    }

    public interface OnCountDownListener {
        void onStart();

        void onFinished();
    }

}
