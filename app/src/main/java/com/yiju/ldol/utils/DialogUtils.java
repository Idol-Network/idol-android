package com.yiju.ldol.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.design.widget.BottomSheetDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.yiju.idol.R;
import com.yiju.idol.listener.OnShareClickListener;

/**
 * Created by thbpc on 2018/3/22 0022.
 */

public class DialogUtils {
    public static void showBottomDialog(Context context, @LayoutRes int res, InitViewsListener listener) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        View view = View.inflate(context, res, null);
        dialog.setContentView(view);
        dialog.show();
        listener.setAction(dialog, view);
    }

    public interface InitViewsListener {
        void setAction(Dialog dialog, View view);
    }

    private static Dialog baseDialog(Activity context, int resource, int anim, boolean Cancelable, boolean outside, int gravity, int width, int height, InitViewsListener listener) {
        Dialog baseDialog = new Dialog(context, R.style.dialog);
        baseDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(context).inflate(resource, null);
        baseDialog.setContentView(view);
        listener.setAction(baseDialog, view);
        baseDialog.setCancelable(Cancelable);//true
        baseDialog.setCanceledOnTouchOutside(outside);//false
        Window window = baseDialog.getWindow();
        window.setWindowAnimations(anim);//R.style.dialog_animation
        window.setGravity(gravity);
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = width > 0 ? DensityUtil.dip2px(width) : width;
        lp.height = height > 0 ? DensityUtil.dip2px(height) : height;
        window.setAttributes(lp);
        if (!context.isFinishing()) {
            baseDialog.show();
        }
        return baseDialog;
    }

    public static Dialog showCenterDialog(Activity context, int resource, int width, int height, InitViewsListener listener) {
        return baseDialog(context, resource, 0, true, true, Gravity.CENTER, width, height, listener);
    }

    public static void showShareDialog(Activity context, OnShareClickListener listener) {
        if (listener == null) {
            return;
        }
        DialogUtils.showBottomDialog(context, R.layout.dialog_sp_share, new DialogUtils.InitViewsListener() {
            @Override
            public void setAction(Dialog dialog, View view) {
                TextView btShareWeixin = view.findViewById(R.id.bt_share_weixin);
                TextView btShareSina = view.findViewById(R.id.bt_share_sina);
                TextView btShareFacebook = view.findViewById(R.id.bt_share_facebook);
                TextView btShareTwitter = view.findViewById(R.id.bt_share_twitter);
                TextView btShareCopy = view.findViewById(R.id.bt_share_copy);
                TextView btCancel = view.findViewById(R.id.bt_cancel);
                View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.bt_share_weixin:
                                listener.weixin();
                                break;
                            case R.id.bt_share_sina:
                                listener.sina();
                                break;
                            case R.id.bt_share_facebook:
                                listener.facebook();
                                break;
                            case R.id.bt_share_twitter:
                                listener.twitter();
                                break;
                            case R.id.bt_share_copy:
                                listener.copy();
                                break;
                        }
                        dialog.dismiss();
                    }
                };
                btShareWeixin.setOnClickListener(onClickListener);
                btShareSina.setOnClickListener(onClickListener);
                btShareFacebook.setOnClickListener(onClickListener);
                btShareTwitter.setOnClickListener(onClickListener);
                btShareCopy.setOnClickListener(onClickListener);
                btCancel.setOnClickListener(onClickListener);
            }
        });
    }
}
