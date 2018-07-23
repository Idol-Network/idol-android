package com.yiju.ldol.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yiju.ldol.base.App;
import com.yiju.ldol.base.BaseImmersionActivity;
import com.yiju.ldol.bean.response.UpdateAvatarResp;
import com.yiju.ldol.listener.JPermissionListener;
import com.yiju.ldol.utils.DialogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class PersonImageActivity extends BaseImmersionActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_more)
    ImageView ivMore;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.rl_image_title)
    RelativeLayout rlImageTitle;
    @BindView(R.id.sdv_user_avatar)
    SimpleDraweeView sdvUserAvatar;


    @Override
    public int getLayout() {
        return R.layout.ac_personimage;
    }

    @Override
    public void initView() {
        mImmersionBar.titleBarMarginTop(rlImageTitle);
        sdvUserAvatar.setImageURI(App.getApp().getUser().avatar);
    }

    @Override
    public void initData(Bundle savedInstanceState) {

    }

    @OnClick({R.id.iv_back, R.id.iv_more})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_more:
                DialogUtils.showBottomDialog(PersonImageActivity.this, R.layout.dialog_setting_avatar, new DialogUtils.InitViewsListener() {
                    @Override
                    public void setAction(Dialog dialog, View view) {
                        View.OnClickListener listener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                switch (v.getId()) {
                                    case R.id.tv_take_pic:
                                        requestPermission(new JPermissionListener(PersonImageActivity.this, 1) {
                                            @Override
                                            public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                                                takePhoto();
                                            }
                                        }, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                        break;
                                    case R.id.tv_seleted_pto:
                                        requestPermission(new JPermissionListener(PersonImageActivity.this, 2) {
                                            @Override
                                            public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                                                photoAlbum();
                                            }
                                        }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                        break;
                                    case R.id.tv_cancel:

                                        break;
                                }
                                dialog.dismiss();
                            }
                        };
                        view.findViewById(R.id.tv_take_pic).setOnClickListener(listener);
                        view.findViewById(R.id.tv_seleted_pto).setOnClickListener(listener);
                        view.findViewById(R.id.tv_cancel).setOnClickListener(listener);
                    }
                });
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_SELECT:
                //添加图片返回
                if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
                    ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                    upLoadAvatar(images.get(0).path);
                }
                break;
        }
    }

    /**
     * 上传头像
     *
     * @param path
     */
    private void upLoadAvatar(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        File file = new File(path);
        RequestBody body = RequestBody.create(null, file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("avatar", file.getName(), body);
        APIHelper.getInstance().updateavatar(part, new CallBack<UpdateAvatarResp>() {
            @Override
            public void onSuccess(UpdateAvatarResp data) {
                sdvUserAvatar.setImageURI(Uri.fromFile(new File(path)));
                App.getApp().getUser().avatar = data.filePath;
            }
        });
    }

}
