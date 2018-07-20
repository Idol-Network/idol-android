package com.yiju.ldol.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yiju.idol.R;
import com.yiju.idol.api.APIHelper;
import com.yiju.idol.api.BaseReslut;
import com.yiju.idol.api.CallBack;
import com.yiju.idol.base.App;
import com.yiju.idol.base.BaseImmersionActivity;
import com.yiju.idol.bean.response.CountryListResp;

import butterknife.BindView;
import butterknife.OnClick;

public class BindPhoneActivity extends BaseImmersionActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_bindphone_title)
    RelativeLayout rlBindphoneTitle;
    @BindView(R.id.tv)
    TextView tv;
    @BindView(R.id.tv_city)
    TextView tvCity;
    @BindView(R.id.rl_phonereg_1)
    RelativeLayout rlPhonereg1;
    @BindView(R.id.tv_country_code)
    TextView tvCountryCode;
    @BindView(R.id.et_edit_phone)
    EditText etEditPhone;
    @BindView(R.id.bt_getcode)
    Button btGetcode;
    @BindView(R.id.et_edit_code)
    EditText etEditCode;
    @BindView(R.id.bt_correct)
    Button btCorrect;
    @BindView(R.id.ll_main)
    LinearLayout llMain;
    @BindView(R.id.bt_cancel)
    Button btCancel;
    @BindView(R.id.ll_success)
    LinearLayout llSuccess;


    private int recLen = 60;
    private boolean flag = true;//倒计时开始为flase
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (recLen > 1) {
                btGetcode.setEnabled(false);
                btGetcode.setSelected(false);
                flag = false;
                recLen--;
                btGetcode.setText(recLen + "s");
                handler.postDelayed(this, 1000);
            } else {
                btGetcode.setEnabled(true);
                btGetcode.setSelected(true);
                btGetcode.setText("Get Code");
                flag = true;
                recLen = 60;
            }
        }
    };

    @Override
    public int getLayout() {
        return R.layout.ac_bindphone;
    }

    @Override
    public void initView() {
        mImmersionBar.titleBarMarginTop(rlBindphoneTitle);
        if (TextUtils.isEmpty(App.getApp().getUser().phone)) {
            llMain.setVisibility(View.VISIBLE);
            llSuccess.setVisibility(View.GONE);
        }else {
            llMain.setVisibility(View.GONE);
            llSuccess.setVisibility(View.VISIBLE);
        }
        //获取验证码
        btGetcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = etEditPhone.getText().toString().trim();
                String areaCode = tvCountryCode.getText().toString().substring(1, tvCountryCode.length());
                if (!TextUtils.isEmpty(mobile)) {
                    APIHelper.getInstance().sendbindphonecode(mobile, areaCode, new CallBack() {
                        @Override
                        public void onSuccess(BaseReslut data) {
                            runnable.run();
                        }
                    });
                } else {
                    showToast("mobile is null");
                }
            }
        });
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @OnClick({R.id.bt_correct, R.id.tv_city, R.id.bt_cancel, R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_city:
                Intent intent = new Intent(this, CountryActivity.class);
                startActivityForResult(intent, 234);
                break;
            case R.id.bt_correct:
                String mobile = etEditPhone.getText().toString().trim();
                String code = etEditCode.getText().toString().trim();
                if (TextUtils.isEmpty(mobile)) {
                    showToast(getString(R.string.mobile_is_null));
                    return;
                }
                if (TextUtils.isEmpty(code)) {
                    showToast(getString(R.string.verif_code_isnull));
                    return;
                }
                APIHelper.getInstance().bindmobile(mobile, code, new CallBack() {
                    @Override
                    public void onSuccess(BaseReslut data) {
                        llMain.setVisibility(View.GONE);
                        llSuccess.setVisibility(View.VISIBLE);
                    }
                });
                break;
            case R.id.bt_cancel:
                finish();
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            CountryListResp.CountryListBean bean = (CountryListResp.CountryListBean) data.getSerializableExtra("country");
            tvCountryCode.setText("+" + bean.areaCode);
            tvCity.setText(bean.name);
        }
    }
}
