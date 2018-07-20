package com.yiju.ldol.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.yiju.idol.R;
import com.yiju.idol.utils.DensityUtil;

import java.util.ArrayList;

/**
 * Created by zejian
 * Time  16/1/8 下午3:19
 * Email shinezejian@163.com
 * Description:自定义表情底部指示器
 */
public class GiftPagerIndicatorView extends LinearLayout {

    private Context mContext;
    private ArrayList<View> mImageViews;//所有指示器集合
    private int size = 5;
    private int marginSize = 6;
    private int pointSize;//指示器的大小
    private int marginLeft;//间距

    public GiftPagerIndicatorView(Context context) {
        this(context, null);
    }

    public GiftPagerIndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GiftPagerIndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        pointSize = DensityUtil.dip2px(size);
        marginLeft = DensityUtil.dip2px(marginSize);
    }

    /**
     * 初始化指示器
     *
     * @param count 指示器的数量
     */
    public void initIndicator(int count) {
        mImageViews = new ArrayList<>();
        this.removeAllViews();
        LayoutParams lp;
        for (int i = 0; i < count; i++) {
            View v = new View(mContext);
            lp = new LayoutParams(pointSize, pointSize);
            if (i != 0)
                lp.leftMargin = marginLeft;
            v.setLayoutParams(lp);
            if (i == 0) {
                v.setBackgroundResource(R.drawable.shape_bg_indicator_point_select);
            } else {
                v.setBackgroundResource(R.drawable.shape_bg_indicator_point_nomal);
            }
            mImageViews.add(v);
            this.addView(v);
        }
    }

    /**
     * 页面移动时切换指示器
     */
    public void playByStartPointToNext(int startPosition, int nextPosition) {
        if (startPosition < 0 || nextPosition < 0 || nextPosition == startPosition) {
            startPosition = nextPosition = 0;
        }
        final View ViewStrat = mImageViews.get(startPosition);
        final View ViewNext = mImageViews.get(nextPosition);
        ViewNext.setBackgroundResource(R.drawable.shape_bg_indicator_point_select);
        ViewStrat.setBackgroundResource(R.drawable.shape_bg_indicator_point_nomal);
    }

}
