package com.yiju.ldol.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.text.TextUtils;

import com.yiju.idol.BuildConfig;
import com.yiju.idol.base.App;
import com.yiju.idol.base.Constant;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Locale;

/**
 * Created by d on 2016/9/20.
 */
public class APKUtils {

    static {
        System.loadLibrary("Idol");
    }

    /**
     * 获取应用加密值，获取时会进行apk签名验证
     *
     * @param context
     * @return
     */
    public static native String getAuthKey(Context context);

    public static String getChannelVersion() {
        String channel = "";
        try {
            channel = App.getContext().getPackageManager().getApplicationInfo(App.getContext().getPackageName(), PackageManager.GET_META_DATA).metaData.getString("APP_CHANNEL");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (StringUtils.isEmpty(channel)) {
            return "_default";
        } else {
            return channel;
        }
    }

    public static String getApkVersion() {
        PackageInfo pi = null;

        try {
            PackageManager pm = App.getContext().getPackageManager();
            pi = pm.getPackageInfo(App.getContext().getPackageName(),
                    PackageManager.GET_CONFIGURATIONS);
            return pi.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    @SuppressLint("HardwareIds")
    public static String getAndroidId() {
        return Settings.Secure.getString(App.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 安装APK文件
     */
    private void installApk(Context context, String filepath) {
        File apkfile = new File(filepath);
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        context.startActivity(i);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 判断服务是否正在运行
     *
     * @param context
     * @param className 判断的服务名字：包名+类名
     * @return true在运行 false 不在运行
     */
    public static boolean serviceIsRunning(Context context, String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取所有的服务
        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);
        if (services != null && services.size() > 0) {
            for (ActivityManager.RunningServiceInfo service : services) {
                if (className.equals(service.service.getClassName())) {
                    isRunning = true;
                    break;
                }
            }
        }

        return isRunning;
    }

    /**
     * VersionName
     *
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        String version = "";
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            version = info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    /**
     * versionCode
     *
     * @param context
     * @return
     */
    public static int getVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 获取当前进程名
     *
     * @param context
     * @return 进程名
     */
    public static final String getProcessName(Context context) {
        String processName = null;

        // ActivityManager
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));

        while (true) {
            for (ActivityManager.RunningAppProcessInfo info : am.getRunningAppProcesses()) {
                if (info.pid == android.os.Process.myPid()) {
                    processName = info.processName;

                    break;
                }
            }

            // go home
            if (!TextUtils.isEmpty(processName)) {
                return processName;
            }

            // take a rest and again
            try {
                Thread.sleep(100L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * 获取当前系统语言
     *
     * @return
     */
    public static final String getLanguage() {
        Locale locale = App.getContext().getResources().getConfiguration().locale;
        return locale.getLanguage();
    }

    public static long getCacheSize() {
        long cachesize = 0;
        try {
            cachesize = DataCleanManager.getTotalCacheSize(App.getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cachesize;
    }

    /**
     * 设置语言
     *
     * @param language 1 简体中文 2 英语
     */
    public static void setLanguage(Activity context, String language) {
        PreferencesUtils.putStringLanguage(context, Constant.LANGUAGE, language);
    }

    /**
     * 获取应用md5签名
     * @param context
     * @return
     */
    public static String getMd5Sign(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(BuildConfig.APPLICATION_ID, PackageManager.GET_SIGNATURES);
            return getSignValidString(packageInfo.signatures[0].toByteArray());
        } catch (Exception e) {
            return "";
        }
    }

    private static String getSignValidString(byte[] paramArrayOfByte) throws NoSuchAlgorithmException {
        MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
        localMessageDigest.update(paramArrayOfByte);
        return toHexString(localMessageDigest.digest());
    }

    private static String toHexString(byte[] paramArrayOfByte) {
        if (paramArrayOfByte == null) {
            return null;
        }
        StringBuilder localStringBuilder = new StringBuilder(2 * paramArrayOfByte.length);
        for (int i = 0; ; i++) {
            if (i >= paramArrayOfByte.length) {
                return localStringBuilder.toString();
            }
            String str = Integer.toString(0xFF & paramArrayOfByte[i], 16);
            if (str.length() == 1) {
                str = "0" + str;
            }
            localStringBuilder.append(str);
        }
    }
}

