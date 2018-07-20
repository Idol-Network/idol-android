package com.yiju.ldol.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yiju.idol.R;
import com.yiju.idol.api.APIHelper;
import com.yiju.idol.api.BaseReslut;
import com.yiju.idol.api.CallBack;
import com.yiju.idol.base.BaseImmersionActivity;
import com.yiju.idol.utils.APKUtils;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by thbpc on 2018/3/16 0016.
 */

public class PhoneCodeActivity extends BaseImmersionActivity {

    public static final String MOBILE = "mobile";

    @BindView(R.id.iv_code_back)
    ImageView ivCodeBack;
    @BindView(R.id.et_code_num)
    EditText etCodeNum;
    @BindView(R.id.tv_code_tip)
    TextView tvCodeTip;
    @BindView(R.id.tv_phone_num)
    TextView tvPhoneNum;
    @BindView(R.id.bt_code_next)
    Button btCodeNext;
    @BindView(R.id.rl_main)
    RelativeLayout rlMain;

    private String mobile;
    private String areaCode;

    @Override
    public int getLayout() {
        return R.layout.ac_phonecode;
    }

    @Override
    public void initView() {
        mImmersionBar.titleBar(ivCodeBack);
        addLayoutListener(rlMain, btCodeNext);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        mobile = getIntent().getStringExtra(MOBILE);
        areaCode = getIntent().getStringExtra(SetNameAndPwdActivity.AREA_CODE);
        String content = String.format(getResources().getString(R.string.phone_num_details), areaCode + " " + mobile);
        SpannableString gotoMsgListStr = new SpannableString(content);
        gotoMsgListStr.setSpan(new ClickableSpan() {
                                   @Override
                                   public void onClick(View widget) {
                                       APIHelper.getInstance().regCode(mobile, areaCode, new CallBack<BaseReslut>() {
                                           @Override
                                           public void onSuccess(BaseReslut data) {
                                               showToast(getString(R.string.send_code_success));
                                           }
                                       });
                                   }

                                   @Override
                                   public void updateDrawState(TextPaint ds) {
                                       super.updateDrawState(ds);
                                       ds.setColor(getResources().getColor(R.color.c8C5FFF)); //设置颜色
                                   }
                               },
                APKUtils.getLanguage().equals("en") ? content.indexOf("Request") : content.indexOf("Request"),
                APKUtils.getLanguage().equals("en") ? content.indexOf("one") + 3 : content.indexOf("one") + 3,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvPhoneNum.append(gotoMsgListStr);
        tvPhoneNum.setMovementMethod(LinkMovementMethod.getInstance());  //很重要，点击无效就是由于没有设置这个引起
    }

    @OnClick({R.id.iv_code_back, R.id.bt_code_next, R.id.tv_phonereg_1})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_code_back:
                finish();
                break;
            case R.id.bt_code_next://
                tvCodeTip.setVisibility(View.INVISIBLE);
                String code = etCodeNum.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    tvCodeTip.setVisibility(View.VISIBLE);
                    return;
                } else {
                    APIHelper.getInstance().verifyCode(mobile, code, new CallBack<BaseReslut>() {
                        @Override
                        public void onSuccess(BaseReslut data) {
                            Intent intent = new Intent(mContext, SetNameAndPwdActivity.class);
                            intent.putExtra(SetNameAndPwdActivity.CODE, code);
                            intent.putExtra(MOBILE, mobile);
                            intent.putExtra(SetNameAndPwdActivity.AREA_CODE, areaCode);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void onFailure(BaseReslut data) {
                            tvCodeTip.setVisibility(View.VISIBLE);
                        }
                    });
                }
                break;
            case R.id.tv_login_1:
                Intent intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra(WebViewActivity.URI, "https://m.idol.network/help/agreetment");
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideKeybord(etCodeNum);
        rlMain.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
    }
}
