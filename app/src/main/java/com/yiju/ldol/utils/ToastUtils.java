package com.yiju.ldol.utils;

import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yiju.idol.R;
import com.yiju.idol.base.App;


/**
 * Created by Administrator on 2016/3/7.
 */
public class ToastUtils {
    public static Toast toast;
    private static TextView tvMsg;


    public static void showCenterToast(String text) {
        if (toast != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                toast.cancel();
            }
        } else {
//            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            View toastView = LayoutInflater.from(App.getContext()).inflate(R.layout.custom_toast, null);
            tvMsg =toastView.findViewById(R.id.tv_toast_msg);
            toast = new Toast(App.getContext());
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(toastView);
        }
        tvMsg.setText(text);
        toast.show();
    }

    public static void cancle() {
        if (null != toast) {
            toast.cancel();
        }
    }

    public static void showToast(String text) {
        if (toast != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                toast.cancel();
            }
        } else {
            toast = Toast.makeText(App.getContext(), text, Toast.LENGTH_SHORT);
        }
        toast.setText(text);
        toast.show();
    }

}
