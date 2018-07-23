package com.yiju.ldol.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.yiju.ldol.base.BaseImmersionActivity;
import com.yiju.ldol.bean.response.UpdateAvatarResp;
import com.yiju.ldol.listener.GlideImageLoader;
import com.yiju.ldol.listener.JPermissionListener;
import com.yiju.ldol.utils.DialogUtils;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by thbpc on 2018/3/16 0016.
 */

public class UpLoadAvatarActivity extends BaseImmersionActivity {
    @BindView(R.id.iv_avatar_back)
    ImageView ivAvatarBack;
    @BindView(R.id.sdv_avatar)
    SimpleDraweeView sdvAvatar;
    @BindView(R.id.bt_avatar_next)
    Button btAvatarNext;
    private String mImagePath;

    @Override
    public int getLayout() {
        return R.layout.ac_uploadavatar;
    }

    @Override
    public void initView() {
        mImmersionBar.titleBar(ivAvatarBack);
    }


    @Override
    public void initData(Bundle savedInstanceState) {
        initImagePicker();
    }

    @OnClick({R.id.iv_avatar_back, R.id.sdv_avatar, R.id.bt_avatar_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_avatar_back:
                finish();
                break;
            case R.id.sdv_avatar:
                selectAvatar();
                break;
            case R.id.bt_avatar_next:
                upLoadAvatar(mImagePath);
                break;
        }
    }

    private void selectAvatar() {
        DialogUtils.showBottomDialog(this, R.layout.bottom_img_layout, new DialogUtils.InitViewsListener() {
            @Override
            public void setAction(Dialog dialog, View view) {
                TextView tv_takephoto = view.findViewById(R.id.tv_takephoto);
                TextView tv_photoalbum = view.findViewById(R.id.tv_photoalbum);
                TextView tv_cancel = view.findViewById(R.id.tv_cancel);
                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.tv_takephoto:
                                requestPermission(new JPermissionListener(UpLoadAvatarActivity.this, 1) {
                                    @Override
                                    public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                                        takePhoto();
                                    }
                                }, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                break;
                            case R.id.tv_photoalbum:
                                requestPermission(new JPermissionListener(UpLoadAvatarActivity.this, 2) {
                                    @Override
                                    public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                                        photoAlbum();
                                    }
                                }, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                                break;
                        }
                        dialog.dismiss();
                    }
                };
                tv_takephoto.setOnClickListener(listener);
                tv_photoalbum.setOnClickListener(listener);
                tv_cancel.setOnClickListener(listener);
            }
        });
    }

    /**
     * 拍照
     */
    protected void takePhoto() {
        Intent intent = new Intent(mContext, ImageGridActivity.class);
        intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
        startActivityForResult(intent, REQUEST_CODE_SELECT);
    }

    /**
     * 相册
     */
    protected void photoAlbum() {
        Intent intent1 = new Intent(mContext, ImageGridActivity.class);
        startActivityForResult(intent1, REQUEST_CODE_SELECT);
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
                    mImagePath = images.get(0).path;
                    sdvAvatar.setImageURI(Uri.fromFile(new File(mImagePath)));
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
        if (TextUtils.isEmpty(mImagePath)) {
            selectAvatar();
            return;
        }
        File file = new File(path);
        RequestBody body = RequestBody.create(null, file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("avatar", file.getName(), body);
        APIHelper.getInstance().updateavatar(part, new CallBack<UpdateAvatarResp>() {
            @Override
            public void onSuccess(UpdateAvatarResp data) {
                startActivity(new Intent(mContext, AddFollowActivity.class));
                finish();
            }
        });
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setMultiMode(false);
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setCrop(true);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(5);              //选中数量限制
        imagePicker.setStyle(CropImageView.Style.CIRCLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(500);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(500);                         //保存文件的高度。单位像素
    }
}
