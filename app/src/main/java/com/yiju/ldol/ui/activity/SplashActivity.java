package com.yiju.idol.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.gyf.barlibrary.ImmersionBar;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.yiju.idol.R;
import com.yiju.idol.api.APIHelper;
import com.yiju.idol.api.BaseReslut;
import com.yiju.idol.api.CallBack;
import com.yiju.idol.base.App;
import com.yiju.idol.base.BaseImmersionActivity;
import com.yiju.idol.base.Constant;
import com.yiju.idol.bean.UserBean;
import com.yiju.idol.bean.response.LauncherResp;
import com.yiju.idol.bean.response.LoginResp;
import com.yiju.idol.bean.response.UserInfoResp;
import com.yiju.idol.listener.JPermissionListener;
import com.yiju.idol.utils.ImageUtils;
import com.yiju.idol.utils.PreferencesUtils;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by thbpc on 2018/3/28 0028.
 */

public class SplashActivity extends BaseImmersionActivity {
    @BindView(R.id.sdv_launcher_bg)
    SimpleDraweeView sdvLauncherBg;
    @BindView(R.id.tv_skip_time)
    TextView tvSkipTime;
    @BindView(R.id.bt_open_moreTime)
    Button btOpenMoreTime;

    private int duration;

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (duration > 0) {
                tvSkipTime.setText(String.format(getString(R.string.skip_time), duration));
                duration--;
                mHandler.postDelayed(this, 1000);
            } else {
                tvSkipTime.setText(String.format(getString(R.string.skip_time), 0));
                if (!isFinishing()) {
                    if (TextUtils.isEmpty(App.getApp().getUserToken())) {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        getUserInfo();
                    }
                }
            }
        }
    };

    @Override
    public int getLayout() {
        return R.layout.ac_splash;
    }

    @Override
    public void initView() {
        getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
        mImmersionBar.titleBarMarginTop(tvSkipTime);
    }

    @Override
    public void setBase() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_BEHIND);

        //加载布局
        if (getLayout() != 0) {
            setContentView(getLayout());
            ButterKnife.bind(this);
        }
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.init();
    }


    @Override
    public void initData(Bundle savedInstanceState) {
        doLogin();
        getLauncher();
    }

    private void getLauncher() {
        requestPermission(new JPermissionListener(this, 999) {
            @Override
            public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                if (isFinishing()) {
                    return;
                }
                APIHelper.getInstance().down("default", new CallBack<LauncherResp>() {
                    @Override
                    public void onSuccess(LauncherResp data) {
                        if (data != null && !TextUtils.isEmpty(data.picUrl)) {
                            //存入显示策略
                            App.getApp().setShowType(data.showType);
                            //存入paypal支付的id
                            PreferencesUtils.putString(App.getContext(), Constant.CONFIG_CLIENT_ID_KEY, data.paypalCientIdTest);
                            //截取名字
                            int lastIndex = data.picUrl.lastIndexOf("/");
                            String imageName = data.picUrl.substring(lastIndex + 1, data.picUrl.length());
                            //存入image的名字
                            PreferencesUtils.putString(App.getContext(), Constant.LAUNCH_IMAGE_NAME, imageName);
                            //存入启动页时长
                            PreferencesUtils.putInt(App.getContext(), imageName, data.duration);
                            //判断本次图片是否存在
                            File file = new File(Constant.SD_PATH + Constant.LAUNCHER_IMAGE_PATH, imageName);
                            if (!file.exists()) {
                                ImageUtils.downLoadPic(data.picUrl, Constant.SD_PATH + Constant.LAUNCHER_IMAGE_PATH, imageName);
                            }
                        }
                    }
                });
            }

            @Override
            public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                super.onFailed(requestCode, deniedPermissions);
                if (isFinishing()) {
                    return;
                } else {
                    showLauncher();
                }
            }
        }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        showLauncher();
    }

    private void showLauncher() {
        //获取图片名称
        String imageName = PreferencesUtils.getString(App.getContext(), Constant.LAUNCH_IMAGE_NAME);
        //判断图片是否存在
        File file = new File(Constant.SD_PATH + Constant.LAUNCHER_IMAGE_PATH, imageName);
        //获取启动图展示时长
        duration = PreferencesUtils.getInt(App.getContext(), imageName, 3);
        if (file.exists() && !file.isDirectory()) {
            sdvLauncherBg.setImageURI(Uri.fromFile(file));
        } else {
            sdvLauncherBg.setImageURI(Uri.parse("res://com.yiju.idol/" + R.drawable.launch_bg));
        }
        if (duration == 0) {
            btOpenMoreTime.setVisibility(View.VISIBLE);
        } else {
            mHandler.postDelayed(runnable, 0);
            tvSkipTime.setVisibility(View.VISIBLE);
        }
    }

    private void getUserInfo() {
        APIHelper.getInstance().getUserInfo(new CallBack<UserInfoResp>() {
            @Override
            public void onSuccess(UserInfoResp data) {
                if (isFinishing()) {
                    return;
                }
                LoginResp resp = new LoginResp();
                UserBean user = new UserBean();
                user.birthday = data.birthday;
                user.gender = data.gender;
                user.phone = data.phone;
                user.avatar = data.avatar;
                user.signInNum = data.signInNum;
                user.followerNum = data.followerNum;
                user.nickName = data.nickName;
                user.roomToken = data.roomToken;
                user.sign = data.sign;
                user.token = data.token;
                user.userId = data.userId;
                resp.user = user;
                App.getApp().setUser(resp);
                if (TextUtils.isEmpty(data.phone)) {//邮箱登录，没有绑定手机
                    Intent intent1 = new Intent(mContext, BindPhoneActivity.class);
                    intent1.putExtra(BindPhoneActivity.IS_FROM_OTHER, true);
                    startActivity(intent1);
                } else if (TextUtils.isEmpty(data.avatar)) {
                    startActivity(new Intent(SplashActivity.this, UpLoadAvatarActivity.class));
                } else {
                    if (data.followerNum == 0) {
                        startActivity(new Intent(SplashActivity.this, AddFollowActivity.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    }
                }
                finish();
            }

            @Override
            public void onFailure(BaseReslut data) {
                super.onFailure(data);
                if (isFinishing()) {
                    return;
                }
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private int retryTime = 3;
    private boolean loginSuccess = false;


    private void doLogin() {
        NimUIKit.login(new LoginInfo(String.valueOf(App.getApp().getUserId()), App.getApp().getRoomToken()),
                new RequestCallback<LoginInfo>() {
                    @Override
                    public void onSuccess(LoginInfo param) {
                        loginSuccess = true;
                        NimUIKit.loginSuccess(param.getAccount());
                    }

                    @Override
                    public void onFailed(int code) {
                        if (retryTime > 0) {
                            sdvLauncherBg.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    retryTime--;
                                    doLogin();//重试3次登录
                                }
                            }, 1000);
                        } else {
                            loginSuccess = false;
                        }
                    }

                    @Override
                    public void onException(Throwable exception) {
                        if (retryTime > 0) {
                            sdvLauncherBg.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    retryTime--;
                                    doLogin();//重试3次登录
                                }
                            }, 1000);
                        } else {
                            loginSuccess = false;
                        }
                    }
                });
    }


    @OnClick({R.id.tv_skip_time, R.id.bt_open_moreTime})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_skip_time:
                if (TextUtils.isEmpty(App.getApp().getUserToken())) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                } else {
                    getUserInfo();
                }
                break;
            case R.id.bt_open_moreTime:
                if (TextUtils.isEmpty(App.getApp().getUserToken())) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                } else {
                    getUserInfo();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(runnable);
        mHandler.removeCallbacksAndMessages(null);
    }
}
