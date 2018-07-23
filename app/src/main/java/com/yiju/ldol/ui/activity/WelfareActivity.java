package com.yiju.ldol.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yiju.ldol.base.BaseImmersionActivity;
import com.yiju.ldol.ui.view.FlowTagLayout;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class WelfareActivity extends BaseImmersionActivity {

    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.sdv_avatar)
    SimpleDraweeView sdvAvatar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_creattime)
    TextView tvCreattime;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.sdv_bigimage)
    SimpleDraweeView sdvBigimage;
    @BindView(R.id.ftl_fans_name)
    FlowTagLayout ftlFansName;
    @BindView(R.id.tv_jointeam)
    TextView tvJointeam;
    @BindView(R.id.bt_share)
    Button btShare;
    @BindView(R.id.tv_day)
    TextView mTvDay;
    @BindView(R.id.tv_hour)
    TextView mTvHour;
    @BindView(R.id.tv_min)
    TextView mTvMin;
    @BindView(R.id.tv_second)
    TextView mTvSecond;

    public static void start(Context context) {
        Intent intent = new Intent(context, WelfareActivity.class);
        context.startActivity(intent);
    }


    @Override
    public int getLayout() {
        return R.layout.ac_welfare;
    }

    @Override
    public void initView() {
        mImmersionBar.titleBarMarginTop(rlTitle);
        sdvAvatar.setImageURI(Constant.ImageTestUrl);
        sdvBigimage.setImageURI(Constant.ImageTestUrl);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        setTime(1614795);//初始化时间显示
        countDown(1614795);//开始倒计时
    }


    /**
     * 使用RxJava实现倒计时
     */
    private void countDown(final int second) {
        Observable.interval(1, TimeUnit.SECONDS)
                .take(second)//计时次数
                .map(integer -> second - 1 - integer)
                .doOnSubscribe(() -> {
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Long>() {

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        setTime(aLong);
                    }
                });
    }

    private void setTime(long seconds) {
        long day = seconds / (24 * 60 * 60);
        long hour = seconds % (24 * 60 * 60) / (60 * 60);
        long min = seconds % (24 * 60 * 60) % (60 * 60) / 60;
        long second = seconds % (24 * 60 * 60) % (60 * 60) % 60;
        String hourStr = hour < 10 ? "0" + hour : String.valueOf(hour);
        String minStr = min < 10 ? "0" + min : String.valueOf(min);
        String secondStr = second < 10 ? "0" + second : String.valueOf(second);
        mTvDay.setText(String.valueOf(day));
        mTvHour.setText(hourStr);
        mTvMin.setText(minStr);
        mTvSecond.setText(secondStr);
    }

    @OnClick({R.id.tv_jointeam, R.id.bt_share, R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_jointeam:
                break;
            case R.id.bt_share:
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
