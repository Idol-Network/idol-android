package com.yiju.ldol.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yiju.ldol.MainActivity;
import com.yiju.ldol.base.App;
import com.yiju.ldol.base.BaseImmersionActivity;
import com.yiju.ldol.bean.response.LoginResp;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by thbpc on 2018/3/16 0016.
 */

public class SetNameAndPwdActivity extends BaseImmersionActivity {

    public static final String CODE = "code";
    public static final String AREA_CODE = "area_code";

    @BindView(R.id.iv_info_back)
    ImageView ivInfoBack;
    @BindView(R.id.tv_info_1)
    TextView tvInfo1;
    @BindView(R.id.tv_info_2)
    TextView tvInfo2;
    @BindView(R.id.et_info_name)
    EditText etInfoName;
    @BindView(R.id.view_info_1)
    View viewInfo1;
    @BindView(R.id.et_info_pwd)
    EditText etInfoPwd;
    @BindView(R.id.view_info_2)
    View viewInfo2;
    @BindView(R.id.bt_info_next)
    Button btInfoNext;
    @BindView(R.id.tv_info_3)
    TextView tvInfo3;
    @BindView(R.id.rl_main)
    RelativeLayout rlMain;
    private String code;
    private String mobile;
    private String areaCode;

    @Override
    public int getLayout() {
        return R.layout.ac_settingpwd;
    }

    @Override
    public void initView() {
        mImmersionBar.titleBar(ivInfoBack);
        addLayoutListener(rlMain, btInfoNext);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        code = getIntent().getStringExtra(CODE);
        mobile = getIntent().getStringExtra(PhoneCodeActivity.MOBILE);
        areaCode = getIntent().getStringExtra(AREA_CODE);
    }


    @OnClick({R.id.bt_info_next, R.id.tv_info_3, R.id.iv_info_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_info_back:
                finish();
                break;
            case R.id.bt_info_next:
                String username = etInfoName.getText().toString().trim();
                if (TextUtils.isEmpty(username)) {
                    showToast(getString(R.string.empty_name));
                    return;
                }
                String pwd = etInfoPwd.getText().toString().trim();
                if (TextUtils.isEmpty(pwd)) {
                    showToast(getString(R.string.empty_pwd));
                    return;
                }
                if (TextUtils.isEmpty(code) || TextUtils.isEmpty(areaCode)) {//邮箱注册
                    APIHelper.getInstance().regByEmail(mobile, pwd, username, new CallBack<LoginResp>() {
                        @Override
                        public void onSuccess(LoginResp data) {
                            EventBus.getDefault().post(IEventType.ON_LOGIN_FINISHED);
                            App.getApp().setUser(data);
                            if (data.user.followerNum == 0) {
                                startActivity(new Intent(mContext, AddFollowActivity.class));
                            } else {
                                startActivity(new Intent(mContext, MainActivity.class));
                            }
                            finish();
                        }
                    });
                } else {
                    APIHelper.getInstance().reg(mobile, pwd, code, username, areaCode, new CallBack<LoginResp>() {
                        @Override
                        public void onSuccess(LoginResp data) {
                            EventBus.getDefault().post(IEventType.ON_LOGIN_FINISHED);
                            App.getApp().setUser(data);
                            startActivity(new Intent(mContext, UpLoadAvatarActivity.class));
                            finish();
                        }
                    });
                }
                break;
            case R.id.tv_info_3:
                Intent intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra(WebViewActivity.URI, "https://m.idol.network/help/agreetment");
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideKeybord(etInfoName);
        rlMain.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
    }
}
