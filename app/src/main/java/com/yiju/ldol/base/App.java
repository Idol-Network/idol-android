package com.yiju.ldol.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.view.CropImageView;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.yiju.idol.bean.UserBean;
import com.yiju.idol.bean.response.LoginResp;
import com.yiju.idol.listener.GlideImageLoader;
import com.yiju.idol.utils.APKUtils;
import com.yiju.idol.utils.PreferencesUtils;
import com.yiju.idol.utils.StringUtils;
import com.yiju.idol.utils.ToastUtils;
import com.yiju.ldol.BuildConfig;


/**
 * Created by sumomogenbi on 16-5-12.
 */
public class App extends Application {
    private static App sInstance;
    private String nickName;


    public static App getApp() {
        return sInstance;
    }

    public static Context getContext() {
        return sInstance.getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        init();
    }

    private void init() {
//        JPushInterface.init(getApplicationContext());  // 初始化 JPush
//        JPushInterface.setDebugMode(Constant.DEBUG_MODE);    // 设置开启日志,发布时请关闭日志
//        PlatformConfig.setQQZone("1105320395", "ry9YWPR4ObSojYvk");
//        PlatformConfig.setQQZone("1105512033", "fMU5HqScG47vEld5");
//        PlatformConfig.setWeixin("wx521aaf688057bcf1", "c5b7f9a6b10aa7bde0c3380df26d77a5");
//        PlatformConfig.setSinaWeibo("930133636", "06eff32a7cb5628415e255bd776ee83d", "http://sns.whalecloud.com/sina2/callback");
//        Config.DEBUG = Constant.DEBUG_MODE;

        initImagePicker();
//        //内存泄漏检测
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            return;
//        }
//        LeakCanary.install(this);
        initUmeng();
    }

    private void initUmeng() {
        //设置LOG开关，默认为false
        UMConfigure.setLogEnabled(BuildConfig.DEBUG);

    }


    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setMultiMode(false);
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setCrop(true);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(5);              //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }


    /**
     * 是否运行在主线程
     *
     * @return
     */
    public boolean inMainProcess() {
        String packageName = getPackageName();
        String processName = APKUtils.getProcessName(this);
        return packageName.equals(processName);
    }

    /**
     * 短时间显示Toast
     * 作用:不重复弹出Toast,如果当前有toast正在显示，则先取消
     *
     * @param info 显示的内容
     */
    public void showToast(String info) {
        ToastUtils.showToast(info);
    }


    /**
     * 退出应用
     */
    public void quit() {
        for (Activity activity : unDestroyActivityList) {
            if (null != activity) {
                activity.finish();
            }
        }
        unDestroyActivityList.clear();
    }

    /**
     * 获取网络验证秘钥
     *
     * @return
     */
    public String getAuthKey() {
        if (Constant.KEY_DES == null) {
            Constant.KEY_DES = APKUtils.getAuthKey(this);
        }
        return Constant.KEY_DES;
    }

    public int getUserID() {
        if (userID == 0) {
            userID = PreferencesUtils.getInt(this, Constant.USER_ID);
        }
        return userID;
    }

    public void setUserID(int id) {
        this.userID = id;
        PreferencesUtils.putInt(this, Constant.USER_ID, id);
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
        PreferencesUtils.putString(this, Constant.USER_NICK, nickName);
    }

    public int getUserId() {
        if (userID == 0) {
            return userID = PreferencesUtils.getInt(this, Constant.USER_ID);
        } else {
            return userID;
        }
    }

    public String getNickName() {
        if (TextUtils.isEmpty(nickName)) {
            return nickName = PreferencesUtils.getString(this, Constant.USER_NICK);
        } else {
            return nickName;
        }
    }


    public void setRoomToken(String roomToken) {
        this.roomToken = roomToken;
        PreferencesUtils.putString(this, Constant.ROOM_TOKEN, roomToken);
    }

    public String getRoomToken() {
        if (!StringUtils.isEmpty(roomToken)) {
            return roomToken;
        } else {
            return roomToken = PreferencesUtils.getString(this, Constant.ROOM_TOKEN);
        }
    }

    public UserBean getUser() {
        return user;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
        PreferencesUtils.putString(this, Constant.USER_TOKEN, userToken);
    }

    public String getUserToken() {//TODO
        if (!StringUtils.isEmpty(userToken)) {
            return userToken;
        } else {
            return userToken = PreferencesUtils.getString(this, Constant.USER_TOKEN);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public void loginOut() {
        this.user = null;
        setRoomToken("");
        setUserToken("");
        setUserID(0);
        setNickName("");
        PreferencesUtils.removeKey(this, Constant.USER_ID);
        PreferencesUtils.removeKey(this, Constant.USER_TOKEN);
        PreferencesUtils.removeKey(this, Constant.ROOM_TOKEN);
        PreferencesUtils.removeKey(this, Constant.USER_NICK);
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_UI_HIDDEN) {
            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            //清空内存缓存（包括Bitmap缓存和未解码图片的缓存）
            imagePipeline.clearMemoryCaches();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        //清空内存缓存（包括Bitmap缓存和未解码图片的缓存）
        imagePipeline.clearMemoryCaches();
    }

    public void setUser(LoginResp data) {
        setUserID(data.user.userId);
        setUserToken(data.user.token);
        setNickName(data.user.nickName);
        setRoomToken(data.user.roomToken);
        this.user = data.user;
    }

    public boolean isLiving() {
        return isLiving;
    }

    public void setIsLiving(boolean isLiving) {
        this.isLiving = isLiving;
    }

    public boolean isWatching() {
        return isWatching;
    }

    public void setWatching(boolean watching) {
        isWatching = watching;
    }
}
