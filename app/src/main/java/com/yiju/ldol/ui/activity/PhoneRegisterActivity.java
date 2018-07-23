package com.yiju.ldol.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yiju.ldol.base.BaseImmersionActivity;
import com.yiju.ldol.bean.response.CountryListResp;
import com.yiju.ldol.utils.RegexUtils;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

/**
 * Created by thbpc on 2018/3/16 0016.
 */

public class PhoneRegisterActivity extends BaseImmersionActivity {
    @BindView(R.id.iv_phonereg_back)
    ImageView ivPhoneregBack;
    @BindView(R.id.tv_phonereg_1)
    TextView tvPhonereg1;
    @BindView(R.id.tv_phonereg_city)
    TextView tvPhoneregCity;
    @BindView(R.id.rl_phonereg_1)
    RelativeLayout rlPhonereg1;
    @BindView(R.id.et_phonereg_num)
    EditText etPhoneregNum;
    @BindView(R.id.ll_phonereg_1)
    LinearLayout llPhonereg1;
    @BindView(R.id.bt_phonereg_next)
    Button btPhoneregNext;
    @BindView(R.id.view_phonereg_2)
    View viewPhonereg2;
    @BindView(R.id.tv_phonereg_3)
    TextView tvPhonereg3;
    @BindView(R.id.tv_phonereg_2)
    TextView tvPhonereg2;
    @BindView(R.id.tv_country_code)
    TextView tvCountryCode;
    @BindView(R.id.rl_main)
    RelativeLayout rlMain;
    @BindView(R.id.et_email_regist)
    EditText etEmailRegist;
    @BindView(R.id.tv_switch_regist)
    TextView tvSwitchRegist;
    @BindView(R.id.ll_mobile_regist)
    LinearLayout llMobileRegist;


    @Override
    public int getLayout() {
        return R.layout.ac_phoneregister;
    }


    @Override
    public void initData(Bundle savedInstanceState) {
//        addLayoutListener(rlMain, btPhoneregNext);
    }

    @OnClick({R.id.iv_phonereg_back, R.id.tv_phonereg_city, R.id.bt_phonereg_next, R.id.tv_phonereg_2, R.id.tv_switch_regist})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_phonereg_back:
                finish();
                break;
            case R.id.tv_phonereg_city:
                Intent intent = new Intent(this, CountryActivity.class);
                startActivityForResult(intent, 123);
                break;
            case R.id.bt_phonereg_next:
                if (llMobileRegist.getVisibility() == View.VISIBLE) {//手机号注册
                    String phone = etPhoneregNum.getText().toString().trim();
                    String areaCode = tvCountryCode.getText().toString().subSequence(1, tvCountryCode.length()).toString();
                    if (!TextUtils.isEmpty(phone)) {
                        APIHelper.getInstance().regCode(phone, areaCode, new CallBack<BaseReslut>() {
                            @Override
                            public void onSuccess(BaseReslut data) {
                                showToast(getString(R.string.send_code_success));
                                Intent intent = new Intent(mContext, PhoneCodeActivity.class);
                                intent.putExtra(PhoneCodeActivity.MOBILE, phone);
                                intent.putExtra(SetNameAndPwdActivity.AREA_CODE, areaCode);
                                startActivity(intent);
                            }
                        });
                    } else {
                        showToast(getString(R.string.invalid_mobile_no));
                    }
                } else {//邮箱注册
                    String email = etEmailRegist.getText().toString().trim();
                    if (!RegexUtils.isValidEmail(email)) {
                        showToast(getString(R.string.invalid_email));
                        return;
                    }
                    Intent intent1 = new Intent(mContext, SetNameAndPwdActivity.class);
                    intent1.putExtra(PhoneCodeActivity.MOBILE, email);
                    startActivity(intent1);
                }
                break;
            case R.id.tv_phonereg_2:
                Intent webview = new Intent(mContext, WebViewActivity.class);
                webview.putExtra(WebViewActivity.URI, "https://m.idol.network/help/agreetment");
                startActivity(webview);
                break;
            case R.id.tv_switch_regist:
                int visibility1 = llMobileRegist.getVisibility();
                int visibility2 = etEmailRegist.getVisibility();
                llMobileRegist.setVisibility(visibility2);
                etEmailRegist.setVisibility(visibility1);
                if (visibility1 == View.VISIBLE) {//邮箱注册
                    tvSwitchRegist.setText(getString(R.string.mobile_regist));
                } else {//手机号注册
                    tvSwitchRegist.setText(getString(R.string.email_regist));
                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            CountryListResp.CountryListBean bean = (CountryListResp.CountryListBean) data.getSerializableExtra("country");
            tvCountryCode.setText("+" + bean.areaCode);
            tvPhoneregCity.setText(bean.name);
        }
    }

    @Override
    public void initView() {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        mImmersionBar.titleBarMarginTop(ivPhoneregBack);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        hideKeybord(etPhoneregNum);
//        rlMain.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
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

}
