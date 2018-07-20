package com.yiju.ldol.ui.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

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
import com.yiju.idol.utils.PreferencesUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by thbpc on 2018/3/28 0028.
 */

public class SplashActivity extends BaseImmersionActivity {
    @BindView(R.id.sdv_launcher_bg)
    SimpleDraweeView sdvLauncherBg;
    @BindView(R.id.iv_bg)
    ImageView ivBg;


    @Override
    public int getLayout() {
        return R.layout.ac_splash;
    }

    @Override
    public void initView() {
        getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
//        sdvPic.setImageResource(R.drawable.phone_register_shape);
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

    private String imageUri;

    @Override
    public void initData(Bundle savedInstanceState) {
//        File file = new File(Constant.SD_PATH + Constant.LAUNCHER_IMAGE_PATH, Constant.LAUNCHER_IMAGE_NAME);
//        if (!file.exists()) {
        APIHelper.getInstance().down("default", new CallBack<LauncherResp>() {
            @Override
            public void onSuccess(LauncherResp data) {
                if (data != null) {
                    imageUri = data.picUrl;
                    PreferencesUtils.putString(App.getContext(), Constant.CONFIG_CLIENT_ID_KEY, data.paypalCientIdTest);
//                    ImageUtils.downLoadPic(data.picUrl, Constant.SD_PATH + Constant.LAUNCHER_IMAGE_PATH, Constant.LAUNCHER_IMAGE_NAME);
                }
            }
        });
//        }
        doLogin();
        //逻辑重构
        sdvLauncherBg.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    if (!TextUtils.isEmpty(imageUri)) {
                        sdvLauncherBg.setImageURI(imageUri);
                        sdvLauncherBg.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (TextUtils.isEmpty(App.getApp().getUserToken())) {
                                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                    finish();
                                } else {
                                    getUserInfo();
                                }
                            }
                        }, 2000);
                        ivBg.setVisibility(View.GONE);
                        ivBg = null;
                    } else {
                        sdvLauncherBg.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (TextUtils.isEmpty(App.getApp().getUserToken())) {
                                    startActivity(new Intent(mContext, LoginActivity.class));
                                    finish();
                                } else {
                                    getUserInfo();
                                }
                            }
                        }, 2000);
                    }
                }
            }
        }, 1000);
    }

    private void getUserInfo() {
        APIHelper.getInstance().getUserInfo(new CallBack<UserInfoResp>() {
            @Override
            public void onSuccess(UserInfoResp data) {
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
                if (data.followerNum == 0) {
                    startActivity(new Intent(SplashActivity.this, AddFollowActivity.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                }
                finish();
            }

            @Override
            public void onFailure(BaseReslut data) {
                super.onFailure(data);
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
}
