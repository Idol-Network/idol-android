package com.yiju.ldol.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.yiju.ldol.base.App;
import com.yiju.ldol.base.BaseImmersionActivity;
import com.yiju.ldol.utils.APKUtils;
import com.yiju.ldol.utils.DataCleanManager;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

public class AppSetiingActivity extends BaseImmersionActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_appsetting_title)
    RelativeLayout rlAppsettingTitle;
    @BindView(R.id.tv_multilingual_settings)
    TextView tvMultilingualSettings;
    @BindView(R.id.tv_message_remind)
    TextView tvMessageRemind;
    @BindView(R.id.tv_cachesize)
    TextView tvCachesize;
    @BindView(R.id.rl_clear_cache)
    RelativeLayout rlClearCache;
    @BindView(R.id.bt_logout)
    Button btLogout;


    private long cachesize;
    private long cachtmp;
    long num = 1000;
    @SuppressLint("HandlerLeak")
    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            tvCachesize.setText(DataCleanManager.getFormatSize(cachesize));
            if (cachesize > 0) {
                cachesize -= num;
                tvCachesize.setText(DataCleanManager.getFormatSize(cachesize));
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        sendEmptyMessage(0);
                    }
                }, 100);
            }
            if (cachesize <= 0) {
                tvCachesize.setText("0Kb");
                if (cachtmp != 0) {
                    showToast(getString(R.string.clear_cache) + DataCleanManager.getFormatSize(cachtmp));
                    cachtmp = 0;
                }
            }
        }
    };

    @Override
    public int getLayout() {
        return R.layout.ac_appsetting;
    }

    @Override
    public void initView() {
        mImmersionBar.titleBarMarginTop(rlAppsettingTitle);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        cachtmp = cachesize = APKUtils.getCacheSize();
        num = cachesize / 10;
        tvCachesize.setText(DataCleanManager.getFormatSize(cachesize));
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(String type) {//接收消息
        switch (type) {
            case IEventType.ON_LANGUAGE_CHANGED://语言变化
                recreate();
                break;
            default:
                break;
        }
    }

    @OnClick({R.id.iv_back, R.id.tv_multilingual_settings, R.id.tv_message_remind, R.id.rl_clear_cache, R.id.bt_logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_multilingual_settings:
                startActivity(new Intent(mContext, MultilingualSettingsActivity.class));
                break;
            case R.id.tv_message_remind:
                startActivity(new Intent(mContext, MessageRemindActivity.class));
                break;
            case R.id.rl_clear_cache:
                mhandler.sendEmptyMessage(0);
                DataCleanManager.clearAllCache(getApplicationContext());
                ImagePipeline imagePipeline = Fresco.getImagePipeline();
                //清空内存缓存（包括Bitmap缓存和未解码图片的缓存）
//                imagePipeline.clearMemoryCaches();
                //清空硬盘缓存，一般在设置界面供用户手动清理
//                imagePipeline.clearDiskCaches();
//                同时清理内存缓存和硬盘缓存
                imagePipeline.clearCaches();
                break;
            case R.id.bt_logout:
                App.getApp().loginOut();
                EventBus.getDefault().post(IEventType.ON_LOGOUT);
                Intent intent = new Intent(mContext, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
