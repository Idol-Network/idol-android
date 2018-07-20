package com.yiju.ldol.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.bean.CustomAttachParser;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.constant.MemberQueryType;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachmentParser;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.uinfo.UserService;
import com.netease.nimlib.sdk.uinfo.constant.UserInfoFieldEnum;
import com.netease.nimlib.sdk.util.NIMUtil;
import com.yiju.idol.base.App;
import com.yiju.idol.utils.im.NimSDKOptionConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Allan_Zhang on 2016/5/26.
 */
public class IMHelper {

    private static final IMHelper helper = new IMHelper();
    private Context mContext;

    private IMHelper() {
    }

    public static IMHelper getInstance() {
        return helper;
    }

    public void init(Context context) {
        mContext = context;
        // SDK初始化（启动后台服务，若已经存在用户登录信息， SDK 将完成自动登录）
        NIMClient.init(context, getLoginInfo(), NimSDKOptionConfig.getSDKOptions(context));
        // ... your codes
        if (NIMUtil.isMainProcess(context)) {
            // 注意：以下操作必须在主进程中进行
            // 1、UI相关初始化操作
            // 2、相关Service调用
            initUI();
        }
    }

    public void initUI() {
        NimUIKit.init(mContext);
        // 注册自定义消息附件解析器
        registerCustomAttachmentParser(new CustomAttachParser());
//        NIMClient.getService(MsgService.class).registerCustomAttachmentParser(new CustomAttachParser());
        //注册各种扩展消息类型的显示ViewHolder
//        registerViewHolders();
    }
//
//    /**
//     * 注册各种扩展消息类型的显示ViewHolder
//     */
//    private static void registerViewHolders() {
//        NimUIKit.registerMsgItemViewHolder(GoodsTipMsgAttachment.class, GoodsTipHolder.class);
//        NimUIKit.registerMsgItemViewHolder(GoodsUrlMsgAttachment.class, GoodsMsgHolder.class);
//        NimUIKit.registerMsgItemViewHolder(OrderMsgAttachment.class, OrderMsgHolder.class);
//    }

    private LoginInfo getLoginInfo() {
        String account = String.valueOf(App.getApp().getUserId());
        String token = App.getApp().getRoomToken();
        Log.d("tang", account + "==" + token);
        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            return new LoginInfo(account, token);
        } else {
            return null;
        }
    }

    /**
     * IM登录
     *
     * @param info
     * @param cb
     */
    public void login(LoginInfo info, RequestCallback<LoginInfo> cb) {
        NIMClient.getService(AuthService.class).login(info)
                .setCallback(cb);
    }

    /**
     * 注销IM
     */
    public void logout() {
        NIMClient.getService(AuthService.class).logout();
    }

    // 如果已经存在用户登录信息，返回LoginInfo，否则返回null即可
    private LoginInfo loginInfo() {
        return null;
    }

    /**
     * 注册普通消息监听
     *
     * @param register 注册/注销
     */
    public void setOnMsgListener(Observer<List<IMMessage>> obsever, boolean register) {

        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(obsever, register);
    }

    /**
     * 注册自定义通知接收
     *
     * @param observer
     * @param register
     */
    public void setOnCustomNotificationListener(Observer<CustomNotification> observer, boolean register) {
        // 如果有自定义通知是作用于全局的，不依赖某个特定的 Activity，那么这段代码应该在 Application 的 onCreate 中就调用
        NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(observer, register);
    }

    /**
     * 发送私聊
     */
    public void sendPrivateMsg(IMMessage message, RequestCallback<Void> cb) {
        // 发送消息。如果需要关心发送结果，可设置回调函数。发送完成时，会收到回调。如果失败，会有具体的错误码。
        NIMClient.getService(MsgService.class).sendMessage(message, false).setCallback(cb);//发送失败不重发
    }

    /**
     * 加入聊天室
     *
     * @param liveid   预告id
     * @param roomId   聊天室id
     * @param nickname 用户昵称
     * @param head     头像地址
     * @param cb       回调
     */
    public void joinChatRoom(int liveid, String roomId, String nickname, String head, RequestCallback<EnterChatRoomResultData> cb) {
        EnterChatRoomData data = new EnterChatRoomData(roomId);
        data.setNick(nickname);
        data.setAvatar(head);

        HashMap<String, Object> map = new HashMap<>();
        map.put("liveid", liveid);
//        map.put("nickname", nickname);
//        map.put("head", head);
        data.setExtension(map);
//        HashMap<String, Object> notifyMap = new HashMap<>();
//        notifyMap.put("userid", userid);
//        notifyMap.put("nickname", nickname);
//        notifyMap.put("head", head);
//        data.setNotifyExtension(notifyMap);
        NIMClient.getService(ChatRoomService.class).enterChatRoom(data)
                .setCallback(cb);
    }

    /**
     * 发送聊天室消息
     *
     * @param message
     * @param cb
     */
    public void sendChatRoomMsg(ChatRoomMessage message, RequestCallback<Void> cb) {
        // 发送消息。如果需要关心发送结果，可设置回调函数。发送完成时，会收到回调。如果失败，会有具体的错误码。
        NIMClient.getService(ChatRoomService.class).sendMessage(message, false).setCallback(cb);
    }

    /**
     * 聊天室消息监听
     *
     * @param observer
     * @param register
     */
    public void setOnChatRoomMsgListener(Observer<List<ChatRoomMessage>> observer, boolean register) {
        NIMClient.getService(ChatRoomServiceObserver.class).observeReceiveMessage(observer, register);
    }

    /**
     * 退出聊天室
     *
     * @param roomId 聊天室id
     */
    public void exitChatRoom(String roomId) {
        NIMClient.getService(ChatRoomService.class).exitChatRoom(roomId);
    }

    /**
     * 注册自定义消息解析
     *
     * @param parser
     */
    public void registerCustomAttachmentParser(MsgAttachmentParser parser) {
        NIMClient.getService(MsgService.class).registerCustomAttachmentParser(parser); // 监听的注册，必须在主进程中。
    }

    /**
     * @param roomId 聊天室id
     * @param time   成员加入时间，会查询该时间之前的用户。固定成员列表用updateTime, 游客列表用进入enterTime， 填0会使用当前服务器最新时间开始查询，即第一页，单位毫秒
     * @param limit  获取的人数
     * @param cb     回调
     */
    public void fetchRoomMembers(String roomId, long time, int limit, RequestCallbackWrapper<List<ChatRoomMember>> cb) {
        NIMClient.getService(ChatRoomService.class)
                .fetchRoomMembers(String.valueOf(roomId), MemberQueryType.GUEST, time, limit)
                .setCallback(cb);
    }

    public void updateUserInfo(String nickname, String headUrl) {
        Map<UserInfoFieldEnum, Object> fields = new HashMap<>(2);
        fields.put(UserInfoFieldEnum.Name, nickname);
        fields.put(UserInfoFieldEnum.AVATAR, headUrl);
        NIMClient.getService(UserService.class).updateUserInfo(fields)
                .setCallback(new RequestCallbackWrapper<Void>() {
                    @Override
                    public void onResult(int i, Void aVoid, Throwable throwable) {

                    }
                });
    }

    /**
     * 删除单条消息记录
     *
     * @param msg
     */
    public void deleteMessageHistory(IMMessage msg) {
        // 删除单条消息，不显示在聊天记录中
        NIMClient.getService(MsgService.class).deleteChattingHistory(msg);
    }

//    /**
//     * 开始私聊
//     *
//     * @param context
//     * @param userId2Chat   聊天对象的id
//     * @param isMiniMode    是否为小窗口模式（半屏）
//     * @param disableCamera 是否禁止使用相机 默认传false 允许使用；正在直播时，禁用相机
//     * @param disableRecord 是否禁用录音 正在直播或观看直播时，为true，禁用录音
//     * @param isFullScreen  是否是横屏模式
//     */
//    public void startPrivateChat(Context context, String userId2Chat, boolean isMiniMode, boolean disableCamera, boolean disableRecord, boolean isFullScreen) {
//        if (isMiniMode) {
//            SessionCustomization sessionCustomization = new SessionCustomization();
//            sessionCustomization.backgroundColor = ContextCompat.getColor(context, R.color.Ffto);
//            MiniP2PMessageActivity.start(context, userId2Chat, sessionCustomization, null, disableCamera, disableRecord, isFullScreen);
//        } else {
//            SessionCustomization sessionCustomization = new SessionCustomization();
//            sessionCustomization.backgroundColor = ContextCompat.getColor(context, R.color.Ffto);
//            NormalP2PMessageActivity.start(context, userId2Chat, sessionCustomization, null, disableCamera, disableRecord);
//        }
//    }
//
//    /**
//     * anchor为进入聊天界面后要发送的信息 其他参数说明同上
//     *
//     * @param context
//     * @param userId2Chat
//     * @param bundle
//     */
//    public void startPrivateChat(Context context, String userId2Chat, Bundle bundle) {
//        boolean isMiniMode = bundle.getBoolean(Extras.EXTRA_IS_MINI_MODE);
//        SessionCustomization sessionCustomization = new SessionCustomization();
//        sessionCustomization.backgroundColor = ContextCompat.getColor(context, R.color.Ffto);
//        if (isMiniMode) {
//            MiniP2PMessageActivity.start(context, userId2Chat, sessionCustomization, bundle);
//        } else {
//            NormalP2PMessageActivity.start(context, userId2Chat, sessionCustomization, bundle);
//        }
//    }

    /**
     * 是否显示通知栏通知
     *
     * @param show 在不需要显示通知的界面 onResume时，传入false; onPause中传入true
     */
    public void showNotification(boolean show) {
        if (show) {
            // 退出聊天界面或离开最近联系人列表界面，建议放在onPause中
            NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);
        } else {
            // 进入最近联系人列表界面，建议放在onResume中
            NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_ALL, SessionTypeEnum.None);
        }
    }

    /**
     * 获取未读消息数
     *
     * @return
     */
    public int getUnreadCount() {
        return NIMClient.getService(MsgService.class).getTotalUnreadCount();
    }

    /**
     * 清空本地所有消息记录
     *
     * @param clearRecent 若为true，将同时清空最近联系人列表数据
     */
    public void clearMsgDatabase(boolean clearRecent) {
        NIMClient.getService(MsgService.class).clearMsgDatabase(clearRecent);
    }
}
