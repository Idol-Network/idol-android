package com.yiju.ldol.utils.im;

import android.content.Context;
import android.os.Environment;

import com.netease.nim.uikit.api.wrapper.MessageRevokeTip;
import com.netease.nim.uikit.api.wrapper.NimUserInfoProvider;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderThumbBase;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.msg.MessageNotifierCustomization;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.yiju.idol.R;
import com.yiju.idol.base.App;
import com.yiju.idol.base.Constant;
import com.yiju.idol.ui.activity.SplashActivity;

/**
 * Created by hzchenkang on 2017/9/26.
 * <p>
 * 云信sdk 自定义的SDK选项设置
 */

public class NimSDKOptionConfig {

    public static SDKOptions getSDKOptions(Context context) {
        SDKOptions options = new SDKOptions();

        // 如果将新消息通知提醒托管给SDK完成，需要添加以下配置。 不添加该项则不会将消息显示在状态栏
//        initStatusBarNotificationConfig(options);

        // 配置 APP 保存图片/语音/文件/log等数据的目录
        options.sdkStorageRootPath = getAppCacheDir(context) + "/messages"; // 可以不设置，那么将采用默认路径

        // 配置数据库加密秘钥
        options.databaseEncryptKey = "NETEASE";

        // 配置是否需要预下载附件缩略图
        options.preloadAttach = true;

        // 配置附件缩略图的尺寸大小
        options.thumbnailSize = MsgViewHolderThumbBase.getImageMaxEdge();

        // 通知栏显示用户昵称和头像
        options.userInfoProvider = new NimUserInfoProvider(App.getApp());

        // 定制通知栏提醒文案（可选，如果不定制将采用SDK默认文案）
        options.messageNotifierCustomization = messageNotifierCustomization;

        // 在线多端同步未读数
        options.sessionReadAck = true;

        // 动图的缩略图直接下载原图
        options.animatedImageThumbnailEnabled = true;

        // 采用异步加载SDK
        options.asyncInitSDK = true;

        // 是否是弱IM场景 默认为 false。
        // 如果您的APP 仅在部分场景按需使用 IM 能力(不需要在应用启动时就做自动登录)，
        // 并不需要保证消息通知、数据的实时性，那么这里可以填 true。弱 IM 场景下，
        // push 进程采用懒启动策略(延迟到用户登录阶段)，
        // 启动后其生命周期将跟随 UI 进程，降低弱 IM 场景的APP的后台功耗开销。
        options.reducedIM = true;

        // 是否检查manifest 配置，调试阶段打开，调试通过之后请关掉
        options.checkManifestConfig = Constant.DEBUG_MODE;

        // 是否启用群消息已读功能，默认关闭
        options.enableTeamMsgAck = true;

        return options;
    }

    /**
     * 配置 APP 保存图片/语音/文件/log等数据的目录
     * 这里示例用SD卡的应用扩展存储目录
     */
    public  static String getAppCacheDir(Context context) {
//        String storageRootPath = null;
//        try {
//            // SD卡应用扩展存储区(APP卸载后，该目录下被清除，用户也可以在设置界面中手动清除)，请根据APP对数据缓存的重要性及生命周期来决定是否采用此缓存目录.
//            // 该存储区在API 19以上不需要写权限，即可配置 <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="18"/>
//            if (context.getExternalCacheDir() != null) {
//                storageRootPath = context.getExternalCacheDir().getCanonicalPath();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        if (TextUtils.isEmpty(storageRootPath)) {
//            // SD卡应用公共存储区(APP卸载后，该目录不会被清除，下载安装APP后，缓存数据依然可以被加载。SDK默认使用此目录)，该存储区域需要写权限!
//            storageRootPath = Environment.getExternalStorageDirectory() + "/" + Constant.DEFAULT_FILE;
////            storageRootPath = Environment.getExternalStorageDirectory() + "/" + DemoCache.getContext().getPackageName();
//        }
//
//        return storageRootPath;
        return Environment.getExternalStorageDirectory() + "/" + Constant.DEFAULT_FILE_NAME;
    }

    private static void initStatusBarNotificationConfig(SDKOptions options) {
        // load 应用的状态栏配置
        StatusBarNotificationConfig config = loadStatusBarNotificationConfig();

        // load 用户的 StatusBarNotificationConfig 设置项
//        StatusBarNotificationConfig userConfig = NimUserPreferences.getStatusConfig();
//        if (userConfig == null) {
//            userConfig = config;
//        } else {
//            // 新增的 UserPreferences 存储项更新，兼容 3.4 及以前版本
//            // 新增 notificationColor 存储，兼容3.6以前版本
//            // APP默认 StatusBarNotificationConfig 配置修改后，使其生效
//            userConfig.notificationEntrance = config.notificationEntrance;
//            userConfig.notificationFolded = config.notificationFolded;
//            userConfig.notificationColor = config.notificationColor;
//        }
//        // 持久化生效
//        NimUserPreferences.setStatusConfig(userConfig);
        // SDK statusBarNotificationConfig 生效
        options.statusBarNotificationConfig = config;
    }

    // 这里开发者可以自定义该应用初始的 StatusBarNotificationConfig
    private static StatusBarNotificationConfig loadStatusBarNotificationConfig() {
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
        // 点击通知需要跳转到的界面
        config.notificationEntrance = SplashActivity.class;
        config.notificationSmallIconId = R.mipmap.notification_logo;
//        config.notificationColor = DemoCache.getContext().getResources().getColor(R.color.color_blue_3a9efb);
        // 通知铃声的uri字符串
//        config.notificationFolded = true;
        // 呼吸灯配置
//        config.ledARGB = Color.GREEN;
//        config.ledOnMs = 1000;
//        config.ledOffMs = 1500;
        // 是否APP ICON显示未读数红点(Android O有效)
        config.showBadge = true;

//        // save cache，留做切换账号备用
//        DemoCache.setNotificationConfig(config);
        return config;
    }

    private static MessageNotifierCustomization messageNotifierCustomization = new MessageNotifierCustomization() {
        @Override
        public String makeNotifyContent(String nick, IMMessage message) {
            return null; // 采用SDK默认文案
        }

        @Override
        public String makeTicker(String nick, IMMessage message) {
            return null; // 采用SDK默认文案
        }

        @Override
        public String makeRevokeMsgTip(String revokeAccount, IMMessage item) {
            return MessageRevokeTip.getRevokeTipContent(item, revokeAccount);
        }
    };
}
