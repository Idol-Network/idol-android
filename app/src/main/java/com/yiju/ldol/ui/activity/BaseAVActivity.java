package com.yiju.ldol.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import com.google.gson.Gson;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.bean.CustomAttachment;
import com.netease.nim.uikit.bean.CustomAttachmentType;
import com.netease.nim.uikit.bean.EggMsgAttachment;
import com.netease.nim.uikit.bean.FollowMsgAttachment;
import com.netease.nim.uikit.bean.GiftMsgAttachment;
import com.netease.nim.uikit.bean.LikeMsgAttachment;
import com.netease.nim.uikit.bean.ShareMsgAttachment;
import com.netease.nim.uikit.bean.TicketMsgAttachment;
import com.netease.nim.uikit.bean.TotalGiftMsgAttachment;
import com.netease.nim.uikit.bean.ViewerMsgAttachment;
import com.netease.nim.uikit.bean.WinMsgAttachment;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.chatroom.ChatRoomMessageBuilder;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessageExtension;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.yiju.idol.R;
import com.yiju.idol.api.LiveApiService;
import com.yiju.idol.base.App;
import com.yiju.idol.base.BaseImmersionActivity;
import com.yiju.idol.base.Constant;
import com.yiju.idol.bean.ItemUserShort;
import com.yiju.idol.bean.LiveGiftBean;
import com.yiju.idol.bean.UserBean;
import com.yiju.idol.bean.response.LiveGiftResp;
import com.yiju.idol.bean.response.OutResp;
import com.yiju.idol.nim.CustomNotificationContent;
import com.yiju.idol.nim.GiftMsgUtil;
import com.yiju.idol.services.DownloadGiftResUtil;
import com.yiju.idol.ui.view.VerticalProgressBar;
import com.yiju.idol.ui.view.flashview.FlashDataParser;
import com.yiju.idol.ui.view.flashview.FlashView;
import com.yiju.idol.ui.view.weatherview.WeatherView;
import com.yiju.idol.utils.DensityUtil;
import com.yiju.idol.utils.DesUtil;
import com.yiju.idol.utils.IMHelper;
import com.yiju.idol.utils.ImageUtils;
import com.yiju.idol.utils.LogUtils;
import com.yiju.idol.utils.SoundPoolHelper;
import com.yiju.idol.utils.TimerHelper;
import com.yiju.idol.utils.ZanBitmapUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Allan_Zhang on 2016/6/15.
 */
public abstract class BaseAVActivity extends BaseImmersionActivity implements View.OnClickListener {

    private static final boolean SHOW_TOAST = true;

    public static final int REQUEST_CODE = 1;
    public static final int EGG_COUNT = 300;//倒计时5分钟

    private final static int REQUEST_GOODS = 10;
    private static final int REQUEST_OPEN_MSG = 13;//打开消息中心或私聊

    private static final String PACKAGE_URL_SCHEME = "package:"; // 方案
    protected long lastUserInTime;//用于记录用户进入、退出的时间
    protected int joinChatRoomTimes;//加入聊天室重试次数
    private int mIMRetryTimes;//IM登录重试次数
    protected ArrayList<ItemUserShort> users2RemoveList = new ArrayList<>();//重复待移除的用户
    protected GiftMsgUtil giftMsgUtil = GiftMsgUtil.getInstance();
    protected boolean isFullScreen;
    /**
     * 加入了聊天室
     */
    protected boolean isJoinedChatRoom;
    /**
     * 是否需要刷新消息列表
     */
    protected boolean isNeedRefreshChat;
    /**
     * 软键盘是否弹出
     */
    protected boolean isKeyboardShown;

    protected final int DELAY = 10;//连续点击的临界点

    protected boolean isPlayingFullScreenAnim;//是否正在播放全屏动画
    protected boolean isPlayingViewerEnterAnim;//是否正在播放跑道动画

    private FlashView flashView;

    private boolean isOnStop;//Activity#onStop时，不播放动画，直接调用结束
    protected boolean isStoreShown;//防止商城多次点击

    @Override
    public void setBase() {
        super.setBase();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        initMsgState();//刷新消息图标状态
        isOnStop = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isOnStop = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SoundPoolHelper.onDestroy();
    }

    protected void showTestToast(String str) {
        if (!SHOW_TOAST) {
            return;
        }
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission();
        }
    }

    /**
     * 更新信息状态 在onResume 及登录im成功时时调用来刷新消息图标状态
     */
    protected void initMsgState() {
        if (!isIMLogin()) {
            //未登录不读取消息状态
            return;
        }
        int unreadCount = IMHelper.getInstance().getUnreadCount();
        initUnreadMsgState(unreadCount > 0);
    }

//    /**
//     * 显示/隐藏商城
//     *
//     * @param contentGoods
//     * @param adapter
//     * @param show
//     */
//    protected void showStore(final View contentGoods, GoodsAdapter adapter, boolean show) {
//        if (show) {
//            int liveId = getLiveId();
//            if (liveId != 0) {
//                Call<GoodsListResp> call = mApiService.getGoodsList(liveId);
//                startRequest(call, APIService.GOODS_LIST);
//            }
//        } else {
//            adapter.hideDot();
//            contentGoods.setVisibility(View.GONE);
//        }
//    }

//    /**
//     * 跳转到购买或详情界面
//     *
//     * @param currentGoodsItem
//     * @param isForDetail
//     */
//    protected void go2BuyShopOrMoreDetail(GoodsListResp.GoodsItemsBean currentGoodsItem, boolean isForDetail) {
//        Intent intent = new Intent(this, BuyGoodsActivity.class);
//        intent.putExtra(Constant.SHOP_BEAN, currentGoodsItem);
//        intent.putExtra("isForDetail", isForDetail);
//        intent.putExtra(BuyGoodsActivity.PLAYER_BEAN, initPlayerData());
//        startActivityForResult(intent, REQUEST_CODE);
//    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SETTINGS && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermission();
        } else if (requestCode == REQUEST_GOODS) {
            isStoreShown = false;
        }
    }

    /**
     * 权限请求
     */
    protected abstract void requestPermission();

    protected String getAccount() {
        return String.valueOf(App.getApp().getUserId());
    }

    protected String getRoomToken() {
        return App.getApp().getRoomToken();
    }

    // 显示缺失权限提示
    protected void showMissingPermissionDialog(int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(BaseAVActivity.this);
        builder.setTitle(R.string.help);
        builder.setMessage(R.string.string_help_text);

        // 拒绝, 退出应用
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        builder.setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startAppSettings();
            }
        });

        builder.setCancelable(false);

        builder.show();
    }

//    /**
//     * 私信
//     *
//     * @param userId2Chat
//     */
//    protected void startPrivateChat(String userId2Chat) {
//        if (TextUtils.isEmpty(NimUIKit.getAccount())) {
//            //nim未登录不能聊天
//            return;
//        }
//        //直播中禁止拍照
//        IMHelper.getInstance().startPrivateChat(this, userId2Chat, true, App.getApp().isLiving(), true, false);
//    }

    /**
     * 显示消息中心
     */
    protected void showMsgCenter() {
        showTestToast("MsgCenter");
//        if (TextUtils.isEmpty(NimUIKit.getAccount())) {
//            //nim未登录不能聊天
//            return;
//        }
//        Intent intent = new Intent(BaseAVActivity.this, MsgCenterActivity.class);
//        if (this instanceof BaseLiveActivity) {
//            //直播中禁止拍照
//            intent.putExtra(Extras.EXTRA_DISABLE_CAMERA, true);
//        }
//        intent.putExtra(MsgCenterActivity.IS_MINI_MODE, true);
//        intent.putExtra(Extras.EXTRA_IS_FULL_SCREEN, isFullScreen);
//        startActivityForResult(intent, REQUEST_OPEN_MSG);
//        overridePendingTransition(R.anim.zoom_in, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                showMissingPermissionDialog(requestCode);
                return;
            }
        }
    }

    /**
     * 开始直播/观看时调用
     *
     * @param watchType 1：观众端 2：主播端
     */
    protected void onShowStart(int watchType) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://sdkoptedge.chinanetcenter.com")
                //增加返回值为String的支持
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        LiveApiService apiService = retrofit.create(LiveApiService.class);
        switch (watchType) {
            case 1:
                Call<String> callWatch = apiService.onWatchStart();
                callWatch.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        LogUtils.d("onShowStart#onWatchStart", "" + response);
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                    }
                });
                break;
            case 2:
                Call<String> callLive = apiService.onLiveStart();
                callLive.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        LogUtils.d("onShowStart#onLiveStart", "" + response);
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                    }
                });
                break;
            default:
                break;
        }
    }

    /**
     * 离开聊天室
     *
     * @param roomId
     */
    protected void exitChatRoom(String roomId) {
        IMHelper.getInstance().exitChatRoom(roomId);
    }

    /**
     * 创建文本消息
     *
     * @param roomId
     * @param content
     * @param adminTag  用于标识显示的场控图片
     * @param isBarrage 是否为弹幕
     * @return
     */
    protected ChatRoomMessage createTextMsg(String roomId, String content, String adminTag, boolean isBarrage) {
        // 创建文本消息
        final ChatRoomMessage message = ChatRoomMessageBuilder.createChatRoomTextMessage(
                roomId, // 聊天室id
                content // 文本内容
        );
        HashMap<String, Object> extensionMap = new HashMap<>();
        extensionMap.put("prefix_pic", adminTag);
        extensionMap.put("liveid", getLiveId());
        extensionMap.put("barrage", isBarrage ? "1" : "0");
        message.setRemoteExtension(extensionMap);
        return message;
    }

    /**
     * 创建关注消息
     *
     * @param roomId
     * @param userId
     * @param nickname
     * @return
     */
    protected ChatRoomMessage createFollowMsg(String roomId, int liveId, int userId, String nickname, String adminTag) {
        FollowMsgAttachment attachment = new FollowMsgAttachment();
        attachment.setLiveId(liveId);
        attachment.setUserId(userId);
        attachment.setNickname(nickname);
        attachment.setPrefixPic(adminTag);
        // 创建礼物消息
        final ChatRoomMessage message = ChatRoomMessageBuilder.createChatRoomCustomMessage(
                roomId, // 聊天室id
                attachment // 文本内容
        );

        return message;
    }

    /**
     * 创建礼物消息
     *
     * @param roomId
     * @param userId
     * @param nickname
     * @param headUrl
     * @param giftType
     * @param giftNum
     * @return
     */
    protected ChatRoomMessage createGiftMsg(int liveId, String roomId, String userId, String nickname, String headUrl,
                                            int giftType, int giftNum, long coinNum, String coinNumStr, String prefixPic) {
        GiftMsgAttachment attachment = new GiftMsgAttachment();
        attachment.setLiveid(liveId);
        attachment.setUserId(userId);
        attachment.setNickname(nickname);
        attachment.setHeadUrl(headUrl);
        attachment.setGiftNum(giftNum);
        attachment.setGiftType(giftType);
        attachment.setCoinNum(coinNum);
        attachment.setCoinNumStr(coinNumStr);
//        attachment.setApplause(applause);
        attachment.setPrefixPic(prefixPic);
        // 创建礼物消息
        final ChatRoomMessage message = ChatRoomMessageBuilder.createChatRoomCustomMessage(
                roomId, // 聊天室id
                attachment // 文本内容
        );
        return message;
    }

    /**
     * 创建点赞的消息
     *
     * @return
     */
    protected ChatRoomMessage createLikeMsg(int liveId, int userId, String roomId, int zanNum, boolean isFirst, String nickName, String adminTag, int zanIndex) {
        LikeMsgAttachment attachment = new LikeMsgAttachment();
        attachment.setLiveId(liveId);
        attachment.setUserId(userId);
        attachment.setGift_num(zanNum);
        if (isFirst) {
            attachment.setGift_type(zanIndex);
        } else {
            attachment.setGift_type(0);
        }
        attachment.setNickName(nickName);
        attachment.setPrefixPic(adminTag);
        attachment.setZanIndex(zanIndex);
        // 创建点赞消息
        final ChatRoomMessage message = ChatRoomMessageBuilder.createChatRoomCustomMessage(
                roomId, // 聊天室id
                attachment // 点赞内容
        );
        return message;
    }

    /**
     * 创建分享消息
     *
     * @param roomId
     * @param userId
     * @param nickname
     * @return
     */
    protected ChatRoomMessage createShareMsg(String roomId, int liveId, int userId, String nickname, String adminTag) {
        ShareMsgAttachment attachment = new ShareMsgAttachment();
        attachment.setLiveId(liveId);
        attachment.setUserId(userId);
        attachment.setNickname(nickname);
        attachment.setPrefixPic(adminTag);
        // 创建礼物消息
        final ChatRoomMessage message = ChatRoomMessageBuilder.createChatRoomCustomMessage(
                roomId, // 聊天室id
                attachment // 文本内容
        );
        return message;
    }

    /**
     * 添加消息到消息记录
     *
     * @param list 消息容器
     * @param msg  需要添加的消息
     */
    protected void addMsg2List(ArrayList<ChatRoomMessage> list, ChatRoomMessage msg) {
        list.add(msg);
        isNeedRefreshChat = true;
        if (list.size() > 50) {//最多保留50条消息
            list.remove(0);
        }
    }

    protected String getNickName() {
        return App.getApp().getNickName();
    }

    protected abstract String getRoomId();

    int i = 0;

    protected void refreshProgress(final VerticalProgressBar bar, int value) {
        bar.setValue(value);
    }

    /**
     * 刷新消息
     */
    protected void refreshChat(List<ChatRoomMessage> msgList, MsgAdapter adapter, ListView lv) {
        //刷新聊天记录
        adapter.notifyDataSetChanged();
        isNeedRefreshChat = false;
    }

    /**
     * 播放全屏礼物动画
     *
     * @param contentGift 播放动画的布局
     * @param giftMsg     礼物消息
     */
    protected void playFullScreenGift(final View contentGift, final ChatRoomMessage giftMsg) {
        if (isPlayingFullScreenAnim || contentGift == null) {
            return;
        }
        int giftType;
        isPlayingFullScreenAnim = true;
        if (isOnStop) {
            //若当前为Activity#onStop 则不播放动画
            isPlayingFullScreenAnim = false;
            return;
        }
        final View fullscreenTitle = contentGift.findViewById(R.id.layout_title_fullscreen_gift);
        final View contributionTitle = contentGift.findViewById(R.id.layout_title_top_contribution);
        flashView = (FlashView) contentGift.findViewById(R.id.flashView);
        CustomAttachment msgAttachment = (CustomAttachment) giftMsg.getAttachment();
        final int customType = msgAttachment.getCustomType();
        if (customType == CustomAttachmentType.TYPE_MEMBER_IN) {
            ViewerMsgAttachment viewerMsgAttachment = (ViewerMsgAttachment) msgAttachment;
            fullscreenTitle.setVisibility(View.GONE);
            contributionTitle.setVisibility(View.VISIBLE);
            //播放榜单用户进入动画
            SimpleDraweeView sdvHead = (SimpleDraweeView) contributionTitle.findViewById(R.id.sdv_contribution_head);
            TextView tvName = (TextView) contributionTitle.findViewById(R.id.tv_contribution_name);
            ImageView ivBg = (ImageView) contributionTitle.findViewById(R.id.iv_contribution_bg);
            ImageView ivCap = (ImageView) contributionTitle.findViewById(R.id.iv_contribution_cap);
            sdvHead.setImageURI(viewerMsgAttachment.getAvater());//头像
            giftType = viewerMsgAttachment.getGiftType();
            switch (giftType) {
                case ViewerMsgAttachment.TYPE_FIRST_ENTER:
                    ivBg.setImageResource(R.drawable.caitiaobang1);//背景彩条
                    ivCap.setImageResource(R.drawable.huangguanbang1);//皇冠
                    break;
                case ViewerMsgAttachment.TYPE_SECOND_ENTER:
                    ivBg.setImageResource(R.drawable.caitiaobang2);
                    ivCap.setImageResource(R.drawable.huangguanbang2);
                    break;
                case ViewerMsgAttachment.TYPE_THIRD_ENTER:
                    ivBg.setImageResource(R.drawable.caitiaobang3);
                    ivCap.setImageResource(R.drawable.huangguanbang3);
                    break;
            }
            //显示用户名
            String nickname = viewerMsgAttachment.getNickname();
            if (nickname != null) {
                tvName.setText(nickname);
            }
            tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onNickClick(giftMsg);
                }
            });
            ViewAnimator.animate(contributionTitle)
                    .translationX(0f, 0f)
                    .alpha(0f, 1f)
                    .duration(300)
                    .start();
        } else {
            fullscreenTitle.setVisibility(View.VISIBLE);
            contributionTitle.setVisibility(View.GONE);
            GiftMsgAttachment giftMsgAttachment = (GiftMsgAttachment) msgAttachment;
            giftType = giftMsgAttachment.getGiftType();
            //播放全屏动画
            TextView tvName = (TextView) fullscreenTitle.findViewById(R.id.tv_name);
            TextView tvGiftName = (TextView) fullscreenTitle.findViewById(R.id.tv_gift_name);
            //显示用户名
            String nickname = giftMsgAttachment.getNickname();
            if (nickname != null) {
                tvName.setText(nickname);
            }
            //用户名点击显示资料
            tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onNickClick(giftMsg);
                }
            });
            //设置礼物名称
            String giftName = GiftMsgUtil.getInstance().getGiftName(giftType);
            tvGiftName.setText(String.format(getString(R.string.send_a_gift), giftName));
            ViewAnimator.animate(fullscreenTitle)
                    .alpha(0f, 1f)
                    .duration(300)
                    .start();
        }
        LiveGiftBean data = GiftMsgUtil.getInstance().getLiveGiftBean(giftType);
        String[] split = new String[0];
        if (data != null) {
            split = data.identity.split(",");//礼物名，宽，高，版本
        }
        contentGift.setVisibility(View.VISIBLE);
        String giftName = split[0];
        if (isFullScreen) {//0上下居中，1为居底，2为居顶
            switch (data.position) {
                case 0:
                    flashView.reload(giftName, "flashAnims", (float) (DensityUtil.getScreenHeight() - Integer.valueOf(split[2])) / 2);
                    break;
                case 1:
                    flashView.reload(giftName, "flashAnims", (float) (DensityUtil.getScreenHeight() - Integer.valueOf(split[2])));
                    break;
                case 2:
                    flashView.reload(giftName, "flashAnims", -(float) (DensityUtil.getScreenHeight() - Integer.valueOf(split[2])));
                    break;
            }
        } else {
            flashView.reload(giftName, "flashAnims", 0f);
        }

        flashView.play(giftName, FlashDataParser.FlashLoopTimeOnce);
        SoundPoolHelper.playSound(giftName);
        flashView.setEventCallback(new FlashDataParser.IFlashViewEventCallback() {
            @Override
            public void onEvent(FlashDataParser.FlashViewEvent e, FlashDataParser.FlashViewEventData data) {
                switch (e) {
                    case STOP:
                        LogUtils.d("tang", "STOP");
                    case ERROR:
                        LogUtils.d("tang", "ERROR");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (customType == CustomAttachmentType.TYPE_MEMBER_IN) {
                                    ViewAnimator.animate(contributionTitle)
                                            .translationX(0, -DensityUtil.getScreenWidth() / 2
                                                    - contributionTitle.getWidth() / 2)
                                            .alpha(1f, 0f)
                                            .duration(600)
                                            .onStop(new AnimationListener.Stop() {
                                                @Override
                                                public void onStop() {
                                                    contentGift.setVisibility(View.GONE);
                                                    //重新唤醒动画调用方法
                                                    isPlayingFullScreenAnim = false;
                                                    startAnim();
                                                }
                                            })
                                            .start();
                                } else {
                                    ViewAnimator.animate(fullscreenTitle)
                                            .alpha(1f, 0f)
                                            .duration(300)
                                            .onStop(new AnimationListener.Stop() {
                                                @Override
                                                public void onStop() {
                                                    contentGift.setVisibility(View.GONE);
                                                    //重新唤醒动画调用方法
                                                    isPlayingFullScreenAnim = false;
                                                    startAnim();
                                                }
                                            })
                                            .start();
                                }
                            }
                        });
                        break;
                }
            }
        });
    }

    protected void playViewerEnterAnim(final View contentLayout, final ChatRoomMessage message) {
        if (isPlayingViewerEnterAnim || contentLayout == null) {
            return;
        }
        isPlayingViewerEnterAnim = true;
        TextView tvNick = (TextView) contentLayout.findViewById(R.id.tv_nick_enter);
        TextView tvLevel = (TextView) contentLayout.findViewById(R.id.tv_level_enter);
        final View ivLight = contentLayout.findViewById(R.id.iv_light);
        ViewerMsgAttachment attachment = (ViewerMsgAttachment) message.getAttachment();
        String nickName = attachment.getNickname();
        String level = attachment.getLevel() + " ";
        float textWidth = 0;
        if (!TextUtils.isEmpty(nickName)) {
            tvNick.setText(nickName);
            //获取名字长度
            Paint paint = new Paint();
            paint.setTextSize(tvNick.getTextSize());
            textWidth = paint.measureText(nickName);
        }
        tvLevel.setText(level);
        tvNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNickClick(message);
            }
        });
        int screenWidth = DensityUtil.getScreenWidth();
        //根据名字长度计算光斑动画距离
        float animWidth = textWidth + DensityUtil.dip2px(112);//光斑动画距离
        ViewAnimator.animate(contentLayout)
                .onStart(new AnimationListener.Start() {
                    @Override
                    public void onStart() {
                        contentLayout.setVisibility(View.VISIBLE);
                    }
                })
                .translationX(screenWidth, 0f)
                .duration(500)
                .thenAnimate(ivLight)
                .onStart(new AnimationListener.Start() {
                    @Override
                    public void onStart() {
                        ivLight.setVisibility(View.VISIBLE);
                    }
                })
                .onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        ivLight.setVisibility(View.GONE);
                    }
                })
                .duration(2000)
                .fadeIn()
                .alpha(0.8f, 1, 1, 0.5f)
                .translationX(0, animWidth)
                .thenAnimate(contentLayout)
                .translationX(0, -contentLayout.getWidth())
                .duration(500)
                .onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        contentLayout.setVisibility(View.INVISIBLE);
                        isPlayingViewerEnterAnim = false;
                        startViewerEnterAnim();//重新检测并唤起动画
                    }
                })
                .start();
    }

    /**
     * 播放全屏特效
     *
     * @param level
     * @param giftType
     */
    protected void onReachAnimLevel(WeatherView view, int level, int giftType) {//TODO
        String giftName = GiftMsgUtil.getInstance().getGiftUrlStr(giftType);
        if (!TextUtils.isEmpty(giftName)) {
            Bitmap bitmap = ImageUtils.readBitmap(Constant.GIFTIMAGE_PATH + "/" + giftName + ".png");
            startFullScreenAnimation(view, bitmap, 20);
        }
    }

    /**
     * @param view
     * @param bitmap
     * @param particles 8/20
     */
    private void startFullScreenAnimation(final WeatherView view, Bitmap bitmap, int particles) {
        view.cancelAnimation()
                .setBitmap(bitmap)
                .setFadeOutTime(1000)
                .setLifeTime(8000)
                .setParticles(particles)
                .startAnimation();

        TimerHelper.startCountDownTime(6, new TimerHelper.CallBack() {
            @Override
            public void endTming() {
                view.stopAnimation();
            }
        });
    }

    /**
     * 检查本地礼物是否是最新礼物
     *
     * @param data
     */
    protected void checkGiftListIsNewest(final LiveGiftResp data) {
        if (!DownloadGiftResUtil.mGiftIsDownload) {
            new DownloadGiftResUtil(getApplicationContext(), data);
        }
    }

    /**
     * 解析gson
     *
     * @param content
     * @return
     */
    protected CustomNotificationContent getGsonBean(String content) {
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        try {
            Gson mgson = new Gson();
            CustomNotificationContent notificationContent = mgson.fromJson(
                    content, CustomNotificationContent.class);
            return notificationContent;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 显示自定义Toast
     *
     * @param toastType    0为默认黑色背景 1为设为场控用的toast
     * @param toastMessage
     */
    protected void showCustomToast(int toastType, String toastMessage) {
        View view = null;
        switch (toastType) {
            case 0:
                view = getLayoutInflater().inflate(R.layout.toast_black_custom, null);
                TextView tvMsg = (TextView) view.findViewById(R.id.tv_toast_msg);
                tvMsg.setText(toastMessage);
                break;
            case 1:
                view = getLayoutInflater().inflate(R.layout.toast_admin_add, null);
                break;
            default:
                break;
        }
        if (view != null) {
            Toast toast = new Toast(this);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(view);
            toast.show();
        }

    }

    /**
     * 调用唤醒动画
     */
    protected abstract void startAnim();

    /**
     * 唤醒用户进入的跑道动画
     */
    protected abstract void startViewerEnterAnim();

    /**
     * IM登录
     *
     * @param account 用户id
     * @param token   token
     */
    protected void doLogin(String account, String token) {
        LoginInfo info = new LoginInfo(account, token);
        RequestCallback<LoginInfo> callback = new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo loginInfo) {
                NimUIKit.setAccount(String.valueOf(App.getApp().getUserId()));
                // 可以在此保存LoginInfo到本地，下次启动APP做自动登录用
                joinChatRoom(getRoomId());
            }

            @Override
            public void onFailed(int i) {
                if (mIMRetryTimes < 3) {
                    //登录失败重试
                    doLogin(String.valueOf(App.getApp().getUserId()), App.getApp().getRoomToken());
                    mIMRetryTimes++;
                }
            }

            @Override
            public void onException(Throwable throwable) {
            }
        };
        IMHelper.getInstance().login(info, callback);
    }

    /**
     * 加入聊天室
     *
     * @param roomId 聊天室id
     */
    protected void joinChatRoom(final String roomId) {
        RequestCallback<EnterChatRoomResultData> cb = new RequestCallback<EnterChatRoomResultData>() {
            @Override
            public void onSuccess(EnterChatRoomResultData enterChatRoomResultData) {
                //TODO 加入聊天室成功
                isJoinedChatRoom = true;
                //加入聊天室成功回调
                onJoinChatRoomSuccess();
                //初始化消息图标状态
                initMsgState();
            }

            @Override
            public void onFailed(int i) {
                if (joinChatRoomTimes < 3) {
                    //重试5次
                    joinChatRoomTimes++;
                    joinChatRoom(roomId);
                }
            }

            @Override
            public void onException(Throwable throwable) {

            }
        };
        UserBean user = App.getApp().getUser();
        if (user == null || user.nickName == null || roomId == null) {
            return;
        }
        IMHelper.getInstance().joinChatRoom(getLiveId(), roomId, user.nickName, user.avatar, cb);
    }

    /**
     * 解密url
     *
     * @param rmtpUrl
     * @return
     */
    protected String getVedioSource(String rmtpUrl) {
        return DesUtil.decrypt(rmtpUrl);
    }

    /**
     * 获取直播/预告id
     *
     * @return
     */
    protected abstract int getLiveId();

    class MsgAdapter extends BaseAdapter {

        private List<ChatRoomMessage> list;

        MsgAdapter(List<ChatRoomMessage> list) {
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = getLayoutInflater().inflate(R.layout.item_msg, null);
                holder.tvMsg = (TextView) convertView.findViewById(R.id.tv_msg);
                holder.ivTag = (SimpleDraweeView) convertView.findViewById(R.id.iv_tag);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final ChatRoomMessage msg = list.get(position);

            MsgTypeEnum msgType = msg.getMsgType();
            SpannableStringBuilder spBuilder = null;
            String nickName;
            StringBuilder contentText = new StringBuilder();
            switch (msgType) {
                case tip://系统提醒
                    contentText.append(msg.getSessionId());
                    holder.tvMsg.setTextColor(getResources().getColor(R.color.color_msg_blue));
                    break;
                case text://文本消息
                    //设置消息颜色
                    holder.tvMsg.setTextColor(getResources().getColor(R.color.white));
                    //获取消息扩展字段
                    ChatRoomMessageExtension extension = msg.getChatRoomMessageExtension();
                    if (extension != null) {
                        nickName = extension.getSenderNick();
                    } else {
                        //自己发送的消息
                        nickName = getNickName();
                    }
                    contentText.append(nickName).append("：").append(msg.getContent());
                    spBuilder = new SpannableStringBuilder(contentText);
                    spBuilder.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            onNickClick(msg);
                        }

                        @Override
                        public void updateDrawState(TextPaint ds) {
                            ds.setColor(getResources().getColor(R.color.color_msg_yellow));
                            ds.setUnderlineText(false);
                        }
                    }, 0, nickName.length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                case custom://自定义消息：如礼物消息
                    CustomAttachment customAttachment = (CustomAttachment) msg.getAttachment();
                    int customType = customAttachment.getCustomType();
                    switch (customType) {
                        case CustomAttachmentType.TYPE_GIFT://礼物
                            //设置消息颜色
                            holder.tvMsg.setTextColor(getResources().getColor(R.color.color_msg_red));
                            GiftMsgAttachment giftMsgAttachment = (GiftMsgAttachment) customAttachment;
                            int giftType = giftMsgAttachment.getGiftType();
                            int viewNum = giftMsgAttachment.getViewNum();
                            int giftNum = viewNum > 0 ? viewNum : 1;//显示的数量
                            nickName = giftMsgAttachment.getNickname();
                            String giftName = GiftMsgUtil.getInstance().getGiftName(giftType);
                            contentText.append(nickName).append(String.format(getString(R.string.send_a_gift_msg), giftNum, giftName));
                            if (!TextUtils.isEmpty(nickName)) {
                                spBuilder = new SpannableStringBuilder(contentText);
                                spBuilder.setSpan(new ClickableSpan() {
                                    @Override
                                    public void onClick(View widget) {
                                        onNickClick(msg);
                                    }

                                    @Override
                                    public void updateDrawState(TextPaint ds) {
                                        ds.setColor(getResources().getColor(R.color.color_msg_yellow));//设置可点击文字颜色
                                        ds.setUnderlineText(false);//去除下划线颜色
                                    }
                                }, 0, nickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            break;
                        case CustomAttachmentType.TYPE_TOTAL_GIFT://送礼总数
                            //设置消息颜色
                            holder.tvMsg.setTextColor(getResources().getColor(R.color.color_msg_red));
                            TotalGiftMsgAttachment totalAttachment = (TotalGiftMsgAttachment) customAttachment;
                            int totalGiftType = totalAttachment.getGiftType();
                            int totalGiftNum = totalAttachment.getGiftNum();
                            nickName = totalAttachment.getNickname();
                            String totalGiftName = GiftMsgUtil.getInstance().getGiftName(totalGiftType);
                            contentText.append(nickName).append(String.format(getString(R.string.send_a_gift_msg), totalGiftNum, totalGiftName));
                            if (!TextUtils.isEmpty(nickName)) {
                                spBuilder = new SpannableStringBuilder(contentText);
                                spBuilder.setSpan(new ClickableSpan() {
                                    @Override
                                    public void onClick(View widget) {
                                        onNickClick(msg);
                                    }

                                    @Override
                                    public void updateDrawState(TextPaint ds) {
                                        ds.setColor(getResources().getColor(R.color.color_msg_yellow));
                                        ds.setUnderlineText(false);
                                    }
                                }, 0, nickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            break;
                        case CustomAttachmentType.TYPE_MEMBER_IN://用户进入直播间
                            //设置消息颜色
                            holder.tvMsg.setTextColor(getResources().getColor(R.color.color_msg_green));
                            ViewerMsgAttachment viewerMsgAttachment = (ViewerMsgAttachment) msg.getAttachment();
                            nickName = viewerMsgAttachment.getNickname();
                            contentText.append(nickName).append(getString(R.string.user_entered));
                            if (!TextUtils.isEmpty(nickName)) {
                                spBuilder = new SpannableStringBuilder(contentText);
                                spBuilder.setSpan(new ClickableSpan() {
                                    @Override
                                    public void onClick(View widget) {
                                        onNickClick(msg);
                                    }

                                    @Override
                                    public void updateDrawState(TextPaint ds) {
                                        ds.setColor(getResources().getColor(R.color.color_msg_yellow));
                                        ds.setUnderlineText(false);
                                    }
                                }, 0, nickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            break;
                        case CustomAttachmentType.TYPE_FOLLOW://关注主播
                            //设置消息颜色
                            holder.tvMsg.setTextColor(getResources().getColor(R.color.color_msg_purple));
                            FollowMsgAttachment followMsgAttachment = (FollowMsgAttachment) msg.getAttachment();
                            nickName = followMsgAttachment.getNickname();
                            contentText.append(nickName).append(getString(R.string.followed_star));
                            if (!TextUtils.isEmpty(nickName)) {
                                spBuilder = new SpannableStringBuilder(contentText);
                                spBuilder.setSpan(new ClickableSpan() {
                                    @Override
                                    public void onClick(View widget) {
                                        onNickClick(msg);
                                    }

                                    @Override
                                    public void updateDrawState(TextPaint ds) {
                                        ds.setColor(getResources().getColor(R.color.color_msg_yellow));
                                        ds.setUnderlineText(false);
                                    }
                                }, 0, nickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            break;
                        case CustomAttachmentType.TYPE_SHARE://分享直播
                            //设置消息颜色
                            holder.tvMsg.setTextColor(getResources().getColor(R.color.color_msg_purple));
                            ShareMsgAttachment shareMsgAttachment = (ShareMsgAttachment) msg.getAttachment();
                            nickName = shareMsgAttachment.getNickname();
                            contentText.append(nickName).append(getString(R.string.shared_live));
                            if (!TextUtils.isEmpty(nickName)) {
                                spBuilder = new SpannableStringBuilder(contentText);
                                spBuilder.setSpan(new ClickableSpan() {
                                    @Override
                                    public void onClick(View widget) {
                                        onNickClick(msg);
                                    }

                                    @Override
                                    public void updateDrawState(TextPaint ds) {
                                        ds.setColor(getResources().getColor(R.color.color_msg_yellow));
                                        ds.setUnderlineText(false);
                                    }
                                }, 0, nickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            break;

                        case CustomAttachmentType.TYPE_LIKE: //刷新点亮
                            //设置消息颜色
                            holder.tvMsg.setTextColor(getResources().getColor(R.color.color_msg_green));
                            //获取消息扩展字段
                            LikeMsgAttachment likeMsgAttachment = (LikeMsgAttachment) msg.getAttachment();
                            nickName = likeMsgAttachment.getNickName();
                            contentText.append(nickName).append("：").append("我点亮了 赞");
                            spBuilder = new SpannableStringBuilder(contentText);
                            if (!TextUtils.isEmpty(nickName)) {
                                spBuilder.setSpan(new ClickableSpan() {
                                    @Override
                                    public void onClick(View widget) {
                                        onNickClick(msg);
                                    }

                                    @Override
                                    public void updateDrawState(TextPaint ds) {
                                        ds.setColor(getResources().getColor(R.color.color_msg_yellow));
                                        ds.setUnderlineText(false);
                                    }
                                }, 0, nickName.length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            int zanIndex = likeMsgAttachment.getGift_type();
                            int drawableRes = ZanBitmapUtils.getZanIndexBitmap(zanIndex);
                            //获取Drawable资源
                            Drawable d = getResources().getDrawable(drawableRes);
                            int px = DensityUtil.dip2px(17);
                            int offset = DensityUtil.dip2px(3.5f);
                            d.setBounds(0, -offset, px + offset, px);
                            //创建ImageSpan
                            ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BOTTOM);
                            //用ImageSpan替换文本
                            spBuilder.setSpan(span, contentText.length() - 1, contentText.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

                            break;

                        case CustomAttachmentType.TYPE_WIN://奖品消息
                            //设置消息颜色
                            holder.tvMsg.setTextColor(getResources().getColor(R.color.color_msg_orange));
                            WinMsgAttachment winMsgAttachment = (WinMsgAttachment) customAttachment;
                            nickName = winMsgAttachment.getNickname();
                            contentText.append(nickName).append(getString(R.string.get_a_gift));
                            if (!TextUtils.isEmpty(nickName)) {
                                spBuilder = new SpannableStringBuilder(contentText);
                                spBuilder.setSpan(new ClickableSpan() {
                                    @Override
                                    public void onClick(View widget) {
                                        onNickClick(msg);
                                    }

                                    @Override
                                    public void updateDrawState(TextPaint ds) {
                                        ds.setColor(getResources().getColor(R.color.color_msg_yellow));
                                        ds.setUnderlineText(false);
                                    }
                                }, 0, nickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            break;
                        case CustomAttachmentType.TYPE_TICKET://门票
                            //设置消息颜色
                            holder.tvMsg.setTextColor(getResources().getColor(R.color.color_msg_showroom_gift));
                            TicketMsgAttachment ticketMsgAttachment = (TicketMsgAttachment) customAttachment;
                            nickName = ticketMsgAttachment.getNickname();
                            contentText.append(nickName).append(getString(R.string.send_a_ticket));
                            if (!TextUtils.isEmpty(nickName)) {
                                spBuilder = new SpannableStringBuilder(contentText);
                                spBuilder.setSpan(new ClickableSpan() {
                                    @Override
                                    public void onClick(View widget) {
                                        onNickClick(msg);
                                    }

                                    @Override
                                    public void updateDrawState(TextPaint ds) {
                                        ds.setColor(getResources().getColor(R.color.color_msg_yellow));
                                        ds.setUnderlineText(false);
                                    }
                                }, 0, nickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            break;
                        case CustomAttachmentType.TYPE_EGG://鸡蛋
                            //设置消息颜色
                            holder.tvMsg.setTextColor(getResources().getColor(R.color.color_msg_showroom_gift));
                            EggMsgAttachment eggMsgAttachment = (EggMsgAttachment) customAttachment;
                            nickName = eggMsgAttachment.getNickname();
                            contentText.append(nickName).append(getString(R.string.send_an_egg));
                            if (!TextUtils.isEmpty(nickName)) {
                                spBuilder = new SpannableStringBuilder(contentText);
                                spBuilder.setSpan(new ClickableSpan() {
                                    @Override
                                    public void onClick(View widget) {
                                        onNickClick(msg);
                                    }

                                    @Override
                                    public void updateDrawState(TextPaint ds) {
                                        ds.setColor(getResources().getColor(R.color.color_msg_yellow));
                                        ds.setUnderlineText(false);
                                    }
                                }, 0, nickName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                            }
                            break;
                    }
                    break;
                default:
                    break;
            }
            if (spBuilder != null) {
                holder.tvMsg.setMovementMethod(LinkMovementMethod.getInstance());
            }
            holder.tvMsg.setText(spBuilder != null ? spBuilder : contentText);
            return convertView;
        }
    }


    class ViewHolder {
        SimpleDraweeView ivTag;
        TextView tvMsg;
    }

    /**
     * 拼接图片uri
     *
     * @param tag
     * @return
     */
    protected String getPrefixPicUri(String tag) {
        return "http://pic1.grtstar.cn/image/static/" + tag + "_3x.png";
    }

    /**
     * 键盘是否显示
     *
     * @return
     */
    protected boolean isKeybordShown() {
        return isKeyboardShown;
    }

    /**
     * 点击用户名弹出用户信息对话框
     */
    protected abstract void onNickClick(ChatRoomMessage msg);

    /**
     * 体力值进度条有更新
     *
     * @param coinNum 币值
     * @param  coinNumStr 转换好的币值字符串
     */
    protected abstract void onStrengthUpdate(long coinNum, String coinNumStr);

    /**
     * 直播结束
     */
    protected abstract void onLiveEnd(OutResp resp);

    /**
     * 加入聊天室成功
     */
    protected abstract void onJoinChatRoomSuccess();

    /**
     * 设置短信图标状态
     *
     * @param hasUnreadMsg 是否有未读消息
     */
    protected abstract void initUnreadMsgState(boolean hasUnreadMsg);

    protected void downloadGiftImages(final List<LiveGiftBean> liveGifts) {
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < liveGifts.size(); i++) {
                    File file = new File(Constant.GIFTIMAGE_PATH, liveGifts.get(i).picUrl + ".png");
                    if (!file.exists()) {
                        ImageUtils.downLoadPic(giftMsgUtil.getIcon1Url(liveGifts.get(i).giftId), Constant.GIFTIMAGE_PATH, liveGifts.get(i).picUrl);
                    }
                }
            }
        }.start();
    }
}
