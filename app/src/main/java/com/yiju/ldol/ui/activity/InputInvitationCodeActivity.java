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

import com.yiju.idol.R;
import com.yiju.idol.api.APIHelper;
import com.yiju.idol.api.BaseReslut;
import com.yiju.idol.api.CallBack;
import com.yiju.idol.base.BaseImmersionActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class InputInvitationCodeActivity extends BaseImmersionActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_skip)
    TextView tvSkip;
    @BindView(R.id.rl_wallet_title)
    RelativeLayout rlWalletTitle;
    @BindView(R.id.tv_1)
    TextView tv1;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.bt_ok)
    Button btOk;
    @BindView(R.id.tv_2)
    TextView tv2;

    @Override
    public int getLayout() {
        return R.layout.ac_inputinvitationcode;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @OnClick({R.id.iv_back, R.id.tv_skip, R.id.bt_ok, R.id.tv_2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_skip:
                startActivity(new Intent(mContext, AddFollowActivity.class));
                break;
            case R.id.bt_ok:
                String invitation_code = etCode.getText().toString().trim();
                if (TextUtils.isEmpty(invitation_code)) {
                    showToast(getString(R.string.invitation_code));
                    return;
                }
                APIHelper.getInstance().inviteCode(invitation_code, new CallBack() {
                    @Override
                    public void onSuccess(BaseReslut data) {
                        startActivity(new Intent(mContext, AddFollowActivity.class));
                    }
                });
                break;
            case R.id.tv_2:
                Intent intent = new Intent(mContext, WebViewActivity.class);
                intent.putExtra(WebViewActivity.URI, "https://m.idol.network/help/agreetment");
                startActivity(intent);
                break;
        }
    }
}
