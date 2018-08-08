package com.yiju.ldol.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.models.User;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yiju.ldol.MainActivity;
import com.yiju.ldol.base.App;
import com.yiju.ldol.base.BaseImmersionActivity;
import com.yiju.ldol.bean.response.LoginResp;
import com.yiju.ldol.bean.response.TestUser;
import com.yiju.ldol.listener.TwitterLoginCallback;
import com.yiju.ldol.utils.TwitterPlaform;

import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by thbpc on 2018/3/16 0016.
 */

public class LoginActivity extends BaseImmersionActivity {

    @BindView(R.id.sdv_login_bg)
    SimpleDraweeView sdvLoginBg;
    @BindView(R.id.iv_login_logo)
    ImageView ivLoginLogo;
    @BindView(R.id.bt_login_phone)
    Button btLoginPhone;
    @BindView(R.id.bt_login_signup)
    Button btLoginSignup;
    @BindView(R.id.tv_login_2)
    TextView tvLogin2;
    @BindView(R.id.ll_login_2)
    LinearLayout llLogin2;
    @BindView(R.id.ll_login_1)
    LinearLayout llLogin1;
    @BindView(R.id.tv_login_1)
    TextView tvLogin1;
    @BindView(R.id.iv_facebook)
    ImageView ivFacebook;
    @BindView(R.id.iv_weixin)
    ImageView ivWeixin;
    @BindView(R.id.iv_sina)
    ImageView ivSina;
    @BindView(R.id.iv_twitter)
    ImageView ivTwitter;


    @Override
    public int getLayout() {
        return R.layout.ac_login_t1;
    }

    @Override
    public void initView() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        mShareAPI.release();
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(String type) {//接收消息
        switch (type) {
            case IEventType.ON_LOGIN_FINISHED:
                finish();
                break;
            default:
                break;
        }
    }

    private UMShareAPI mShareAPI = null;

    @Override
    public void initData(Bundle savedInstanceState) {
        mShareAPI = UMShareAPI.get(getApplicationContext());
        UMShareConfig config = new UMShareConfig();
        config.isNeedAuthOnGetUserInfo(true);
        mShareAPI.setShareConfig(config);//每次都需要授权 默认为token有效期内登录不进行二次授权
    }

    UMAuthListener authListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            showProgressDialog();
        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            disMissDialog();
            Set<String> strings = data.keySet();
//            Log.d("tang", "------------" + platform.getName() + "------------");
//            for (String s : strings) {
//                Log.d("tang", "key:" + s + "\nvalue:" + data.get(s));
//                Log.d("tang", "\n");
//            }
//            Log.d("tang", "------------" + platform.getName() + "------------");
            String nickname = null;
            String avatar = null;
            int sex = 3;
            int tokenType = 0;
            String accessToken = null;
            String openId = null;
            switch (platform) {
                case WEIXIN:
                    break;
                case SINA:
                    break;
                case FACEBOOK:
                    nickname = data.get("name");
                    avatar = data.get("iconurl");
                    tokenType = 3;
                    accessToken = data.get("accessToken");
                    openId = data.get("uid");
//                    setTestUser(nickname, avatar, tokenType, accessToken, openId, sex);
                    break;
//                case TWITTER:
//                    nickname = data.get("name");
//                    accessToken = data.get("accessToken");
//                    break;
            }
            APIHelper.getInstance().thirdReg(nickname, avatar, sex, tokenType, accessToken, openId, new CallBack<LoginResp>() {
                @Override
                public void onSuccess(LoginResp data) {
                    if (data != null && data.user != null) {
                        App.getApp().setUser(data);
                        if (data.threeFirst) {
                            startActivity(new Intent(mContext, InputInvitationCodeActivity.class));
                        } else {
                            if (data.user.followerNum == 0) {
                                startActivity(new Intent(mContext, AddFollowActivity.class));
                            } else {
                                startActivity(new Intent(mContext, MainActivity.class));
                            }
                        }
                        finish();
                    }
                }
            });
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            disMissDialog();
            showToast("Error:" + t.getMessage());
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            disMissDialog();
            showToast(getString(R.string.login_cancel));
        }
    };

    TestUser user;

    private void setTestUser(String nickname, String avatar, int tokenType, String accessToken, String openId, int sex) {
        user = new TestUser();
        user.accessToken = accessToken;
        user.avatar = avatar;
        user.nickname = nickname;
        user.openId = openId;
        user.sex = sex;
        user.tokenType = tokenType;
    }

    @OnClick({R.id.bt_login_phone, R.id.bt_login_signup, R.id.iv_weixin, R.id.iv_sina, R.id.iv_twitter, R.id.iv_facebook, R.id.tv_login_1})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_login_phone:
                startActivity(new Intent(this, PhoneLoginActivity.class));
                break;
            case R.id.bt_login_signup:
                startActivity(new Intent(this, PhoneRegisterActivity.class));
                break;
            case R.id.iv_weixin:
                if (mShareAPI.isInstall(this, SHARE_MEDIA.WEIXIN)) {
                    mShareAPI.getPlatformInfo(this, SHARE_MEDIA.WEIXIN, authListener);
                }
                break;
            case R.id.iv_sina:
                mShareAPI.getPlatformInfo(this, SHARE_MEDIA.SINA, authListener);
                break;
            case R.id.iv_twitter:
                twitterPlaform = new TwitterPlaform(this);
                twitterPlaform.login(new TwitterLoginCallback() {
                    @Override
                    public void Success(User user, String authToken) {
                        setTestUser(user.name, user.profileImageUrl, 5, authToken, user.idStr, 3);
                        APIHelper.getInstance().thirdReg(user.name, user.profileImageUrl, 3, 5, authToken, user.idStr, new CallBack<LoginResp>() {
                            @Override
                            public void onSuccess(LoginResp data) {
                                if (data != null && data.user != null) {
                                    App.getApp().setUser(data);
                                    if (data.threeFirst) {
                                        startActivity(new Intent(mContext, InputInvitationCodeActivity.class));
                                    } else {
                                        if (data.user.followerNum == 0) {
                                            startActivity(new Intent(mContext, AddFollowActivity.class));
                                        } else {
                                            startActivity(new Intent(mContext, MainActivity.class));
                                        }
                                    }
                                    finish();
                                }
                            }
                        });
                    }

                    @Override
                    public void onFailure(String message) {
                        showToast(getString(R.string.login_cancel));
                    }
                });

                break;
            case R.id.iv_facebook:
                if (mShareAPI.isInstall(this, SHARE_MEDIA.FACEBOOK)) {
                    mShareAPI.getPlatformInfo(this, SHARE_MEDIA.FACEBOOK, authListener);
                } else {
                    showToast("FACEBOOK uninstall");
                }
                break;
            case R.id.tv_login_1:
//                if (user != null) {
//                    APIHelper.getInstance().thirdReg(user.nickname, user.avatar, user.sex, user.tokenType, "abcdefghijk", user.openId, new CallBack<LoginResp>() {
//                        @Override
//                        public void onSuccess(LoginResp data) {
//                            if (data != null && data.user != null) {
//                                App.getApp().setUser(data);
//                                if (data.user.followerNum == 0) {
//                                    startActivity(new Intent(mContext, AddFollowActivity.class));
//                                } else {
//                                    startActivity(new Intent(mContext, MainActivity.class));
//                                }
//                                finish();
//                            }
//                        }
//                    });
//                } else {
//                    Log.d("tang", "user==null");
//                }
                Intent intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra(WebViewActivity.URI, "https://m.idol.network/help/agreetment");
                startActivity(intent);
                break;
        }
    }

    TwitterPlaform twitterPlaform;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
        //twitter的回调
        if (requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE) {
            twitterPlaform.getTwitterAuthClient().onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mShareAPI.onSaveInstanceState(outState);
    }
}
