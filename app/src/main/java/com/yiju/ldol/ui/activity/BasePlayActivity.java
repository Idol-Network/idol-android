package com.yiju.ldol.ui.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.netease.nim.uikit.bean.GiftMsgAttachment;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMessage;
import com.yiju.idol.api.APIHelper;
import com.yiju.idol.api.BaseReslut;
import com.yiju.idol.api.CallBack;
import com.yiju.idol.api.Request;
import com.yiju.idol.base.App;
import com.yiju.idol.bean.ItemUserShort;
import com.yiju.idol.bean.response.ApplauseResp;
import com.yiju.idol.bean.response.InPlayNewResp;
import com.yiju.idol.bean.response.UserInfoResp;
import com.yiju.idol.listener.OnShareClickListener;
import com.yiju.idol.nim.GiftMsgUtil;
import com.yiju.idol.ui.adapter.ViewerAdapter;
import com.yiju.idol.ui.dialog.GiftPanelDialogFt;
import com.yiju.idol.ui.view.GiftView;
import com.yiju.idol.ui.view.weatherview.WeatherView;
import com.yiju.idol.utils.DialogUtils;
import com.yiju.idol.utils.NumberUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * created by Allan_Zhang
 */
public abstract class BasePlayActivity extends BaseAVActivity implements View.OnClickListener {

    private static final String PREPAY_ID = "prepay_id";//微信支付id

    protected ArrayList<ChatRoomMessage> mListAnim = new ArrayList<>();//动画列表
    protected ArrayList<ChatRoomMessage> mListBigAnim = new ArrayList<>();//大动画列表
    protected ArrayList<ChatRoomMessage> mListContributionAnim = new ArrayList<>();//榜单用户列表
    protected ArrayList<ItemUserShort> mListMember = new ArrayList<>();//成员列表

    protected RecyclerView mRvViewer;//当前聊天室成员
    protected GiftView gv;
    protected GiftView gv2;
    protected TextView mTvApplauseNum;//掌声数
    protected TextView mTvliveHits;//直播人气
    protected TextView mTvNick;//主播昵称
    //    protected TextView mTvStarId;//星id
    protected SimpleDraweeView mIvVip;
    protected int giftNum;

    protected SimpleDraweeView mSdvHead;//主播头像
    protected SimpleDraweeView mSdvBgPic;

    protected ViewerAdapter mViewerAdapter;//用户头像列表adapter
    protected GiftPanelDialogFt mGiftPanelDialogFt;
    protected View mContentFullScreenGift;//全屏动画布局
    protected Dialog mUserInfoDialog;//用户信息对话框
    protected View mContentBtnFollow;//关注按钮布局
    protected View mProBarWave;//关注按钮动画
    protected WeatherView weatherView;

    protected View mContentFollow;//用户信息对话框关注布局
    protected TextView tvTip;//用户信息对话框的禁言
    protected TextView tv2Black;//用户信息对话框的拉黑

    protected int selectedChargeId;//选中的充值项id
    protected final int RECHARGE_WEIXIN = 1;
    protected final int RECHARGE_ZHIFUBAO = 2;
    protected InPlayNewResp inPlayResp;//当前直播信息
    protected boolean isLoadingMore;//是否正在加载用户数据

    protected boolean isRequringUserData;//记录是否已在请求用户信息，防止多次点击造成多次显示
    protected boolean isRechargeDialogShown;//充值框是否显示

    protected int selectedShareId;//选中的分享方式

    protected String prepayId = "";
    protected int mGiftNum = -1;
    protected int mGiftType;
    private Request mGiftRequest;//送礼的请求

    protected Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 232:
                    mProBarWave.setVisibility(View.VISIBLE);
                    break;
                case 233:
                    mProBarWave.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    private TextView mTvRechargeStrength;//充值dialog中的体力值

    protected boolean hasFollowed;//是否点击过关注主播按钮

    private Timer mFollowTimer;
    private TimerTask mFollowTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            //恢复微信支付id
            prepayId = savedInstanceState.getString(PREPAY_ID);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PREPAY_ID, prepayId);//保存微信支付id
    }

    //在界面 onCreate 里注册消息接收观察者，在 onDestroy 中注销观察者。在收到消息时，判断是否是当前聊天对象的消息，如果是，加入到列表中显示。
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        setOnMsgListener(false);//注销消息监听
        if (getRoomId() != null) {
            exitChatRoom(getRoomId());//退出直播间
        }
        startFollowTimer(false);
        App.getApp().setWatching(false);//正在观看
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//屏幕常亮
        //注册自定义消息解析 已在application中注册
//        IMHelper.getInstance().registerCustomAttachmentParser(new CustomAttachParser()); // 监听的注册，必须在主进程中。
        setOnMsgListener(true);
    }

    /**
     * 取消计时动画任务
     */
    private void resetFollowTimer() {
        if (mFollowTask != null) {
            mFollowTask.cancel();
            mFollowTask = null;
        }
        if (mFollowTimer != null) {
            mFollowTimer.cancel();
            mFollowTimer = null;
        }
    }

    /**
     * 关注按钮动画
     *
     * @param start
     */
    protected void startFollowTimer(boolean start) {
        if (start) {
            //第一次进入直播，过一分钟显示动画
            if (mFollowTimer == null) {
                mFollowTask = new TimerTask() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(232);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                handler.sendEmptyMessage(233);
                            }
                        }, 4800);
                    }
                };
                mFollowTimer = new Timer();
                mFollowTimer.schedule(mFollowTask, 60000, 64800);
            }
        } else {
            resetFollowTimer();
        }
    }

    @Override
    protected void requestPermission() {
        ArrayList<String> permissions = new ArrayList<>();
        //分享 6.0权限
        String[] mPermissionList = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        for (String aMPermissionList : mPermissionList) {
            if (ContextCompat.checkSelfPermission(this, aMPermissionList)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                permissions.add(aMPermissionList);
            }
        }
        if (permissions.size() > 0) {
            String[] list = new String[permissions.size()];
            ActivityCompat.requestPermissions(this, permissions.toArray(list), 100);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }


    @Override
    public void initView() {
        setLisenter();
    }

    public void setLisenter() {
        mSdvHead.setOnClickListener(this);
        //设置用户头像点击监听
        mViewerAdapter.SetOnItemClickListener(new ViewerAdapter.OnItemClickListener() {
            @Override
            public void onClick(int userId) {
                getUserInfo(userId);
            }
        });

        GiftView.OnHeadTouchListener onHeadTouchListener = new GiftView.OnHeadTouchListener() {
            @Override
            public void onHeadTouch(int userId) {
                getUserInfo(userId);
            }
        };
        gv.setOnHeadTouchListener(onHeadTouchListener);
        gv2.setOnHeadTouchListener(onHeadTouchListener);
        gv.setOnAnimFinishListener(new GiftView.OnAnimListener() {
            @Override
            public void onTextAnimFinished() {
                if (mListAnim.size() > 0) {
                    GiftMsgAttachment attachment = getAttachment(gv);
                    if (attachment != null) {
                        gv.updateData(attachment);
                    }
//                    gv.updateData((GiftMsgAttachment) mListAnim.get(0).getAttachment());
//                    mListAnim.remove(0);
                }
            }

            @Override
            public void onAnimEnd() {
                if (mListAnim.size() > 0) {
                    GiftMsgAttachment attachment = getAttachment(gv);
                    if (attachment != null) {
                        gv.updateData(attachment);
                    }
                }
            }

            @Override
            public void onReachLevel(int level, int giftType) {
                onReachAnimLevel(weatherView, level, giftType);
            }
        });
        gv2.setOnAnimFinishListener(new GiftView.OnAnimListener() {
            @Override
            public void onTextAnimFinished() {
                if (mListAnim.size() > 0) {
                    GiftMsgAttachment attachment = getAttachment(gv2);
                    if (attachment != null) {
                        gv2.updateData(attachment);
                    }
                }
            }

            @Override
            public void onAnimEnd() {
                if (mListAnim.size() > 0) {
                    GiftMsgAttachment attachment = getAttachment(gv2);
                    if (attachment != null) {
                        gv2.updateData(attachment);
                    }
                }
            }

            @Override
            public void onReachLevel(int level, int giftType) {
                onReachAnimLevel(weatherView, level, giftType);
            }
        });
    }


    /**
     * 直播不存在
     */
    protected abstract void doLiveNotExist();

//    @Override
//    protected void onResponseError(Request req) {
//        if (isFinishing()) {
//            return;
//        }
//        switch (req.getType()) {
//            case APIService.USERINFO://获取用户资料
//                isRequringUserData = false;
//                break;
//            case APIService.INPLAY:
//                //被禁止观看
//                finish();
//                break;
//            case APIService.ORDER_QUERY://购买失败
//                prepayId = "";
//                break;
//            case APIService.APPLAUSE://送礼失败
//                mGiftNum = -1;
//                if (mGiftPanelDialogFt != null) {
//                    mGiftPanelDialogFt.onResponse(false);
//                }
//                break;
//            case APIService.PAY_TYPE:
//                isRechargeDialogShown = false;
//                break;
//        }
//        super.onResponseError(req);
//    }

    UserInfoResp avaterMingpianData; // 个人名片

//    @Override
//    public void onResponseSuccess(Request req) {
//        if (isFinishing()) {
//            return;
//        }
//        disMissDialog();
//        switch (req.getType()) {
//            case APIService.USERINFO://获取用户资料
//                avaterMingpianData = (UserInfoResp) req.getData();
//                if (avaterMingpianData != null) {
//                    showUserInfoDialog(avaterMingpianData);
//                } else {
//                    isRequringUserData = false;
//                }
//                break;
//            case APIService.ADD_FOLLOW_STAR://关注明星
//                mContentBtnFollow.setVisibility(View.GONE);
//                //关注成功
//                if (mContentFollow != null) {
//                    manageFollowButton(true);
//                }
//                if (!hasFollowed && this instanceof AVActivity && getRoomId() != null && app != null) {
//                    sendMsg(createFollowMsg(getRoomId(), getLiveId(), App.getApp().getUserId(), getNickName(), inPlayResp.userGradePic));
//                    hasFollowed = true;
//                }
//                startFollowTimer(false);//取消关注按钮动画
//                EventBus.getDefault().post("addFollow");
//                break;
//            case APIService.ADD_FOLLOW://关注
//                BaseReslut addData = req.getData();
//                if (addData != null) {
//                    //关注成功
//                    manageFollowButton(true);
//                }
//                break;
//            case APIService.DELETE_FOLLOW_STAR://取消关注明星
//                mContentBtnFollow.setVisibility(View.VISIBLE);
//                //取消关注成功
//                manageFollowButton(false);
//                resetFollowTimer();
//                startFollowTimer(true);
//                EventBus.getDefault().post("deleteFollow");
//                break;
//            case APIService.DELETE_FOLLOW://取消关注
//                BaseReslut delData = req.getData();
//                if (delData != null) {
//                    //取消关注成功
//                    manageFollowButton(false);
//                }
//                break;
//            case APIService.APPLAUSE://送礼成功
//                ApplauseResp applauseResp = (ApplauseResp) req.getData();
//                if (applauseResp != null) {
//                    //保存用户体力值
//                    inPlayResp.userBalance.strength = applauseResp.userGift.strength;
//                    inPlayResp.anchorBalance.totalApplause = applauseResp.userGift.totalApplause;//保存最新的主播掌声数
//                    if (inPlayResp.prize) {
//                        //如有抽奖，保存进度条的值
//                        inPlayResp.userLive.lastStrength = applauseResp.userGift.strengthNum;
//                    }
//                    boolean b = applauseResp.userGift.paymentOk;
//                    if (mGiftPanelDialogFt != null) {
//                        mGiftPanelDialogFt.onResponse(b);
//                        //更新用户体力值
//                        mGiftPanelDialogFt.setStarCoin(applauseResp.userGift.strength);
//                    }
//                    if (!b) {
//                        //体力不足，获取充值列表
//                        getPayTypeList(1);
//                    }
//                }
//                break;
//            case APIService.KICK_OFF://踢人
//                if (mUserInfoDialog != null) {
//                    mUserInfoDialog.dismiss();
//                }
//                showCustomToast(0, getString(R.string.kick_success));
//                break;
//            case APIService.GAG://禁言
//                //按钮状态显示 解禁
//                if (mUserInfoDialog != null) {
//                    mUserInfoDialog.dismiss();
//                }
//                showCustomToast(0, getString(R.string.gag_success));
//                if (tvTip != null) {
//                    tvTip.setText(getString(R.string.delete_gag));
//                }
//                break;
//            case APIService.DELETE_GAG://解禁
//                //按钮状态显示 禁言
//                if (mUserInfoDialog != null) {
//                    mUserInfoDialog.dismiss();
//                }
//                showCustomToast(0, getString(R.string.del_gag_success));
//                if (tvTip != null) {
//                    tvTip.setText(getString(R.string.gag));
//                }
//                break;
//            case APIService.REPORT://举报
//                showToast(getString(R.string.tip_success));
//                break;
//            case APIService.ADD_BLACK://加入黑名单
//                if (tv2Black != null) {
//                    tv2Black.setText(getString(R.string.black_added));
//                }
//                showToast(getString(R.string.black_success));
//                break;
//            case APIService.DEL_BLACK://取消拉黑
//                if (tv2Black != null) {
//                    tv2Black.setText(getString(R.string.add_black));
//                }
//                showToast(getString(R.string.del_black_success));
//                break;
//            case APIService.END_SHOW:
//                //直播中退出观看
//                finish();
//                break;
//            case APIService.RECHARGE:
//                RechargeResp data1 = (RechargeResp) req.getData();
//                if (data1 != null) {
//                    prepayId = data1.result.prepayId;//
//
//                    PayReq request = new PayReq();
//                    request.appId = data1.result.appId;
//                    request.partnerId = data1.result.partnerId;
//                    request.prepayId = data1.result.prepayId;
//                    request.packageValue = data1.result.pack;
//                    request.nonceStr = data1.result.noncestr;
//                    request.timeStamp = data1.result.timestamp;
//                    request.sign = data1.result.sign;
//                    api.sendReq(request);
//                }
//                break;
//            case APIService.ORDER_QUERY://购买成功
//                OrderQueryResp data2 = (OrderQueryResp) req.getData();
//                if (data2 != null) {
//                    //显示充值额度
//                    showCenterToast(String.format(getString(R.string.recharge_success), data2.strength));
//                    //调用接口刷新体力
//                    getOtherInfo();
//                    TalkingDataAppCpa.onPay(String.valueOf(app.getUserID()), String.valueOf(prepayId), data2.strength * 10, "CNY", "weixin");//充值成功
//                    prepayId = "";//将id置空
//                }
//                break;
//            case APIService.OTHER_INFO://刷新体力
//                OtherInfoResp otherInfoResp = (OtherInfoResp) req.getData();
//                if (otherInfoResp != null) {
//                    //保存用户体力值
//                    inPlayResp.userBalance.strength = otherInfoResp.userBalance.strength;
//                    if (mTvRechargeStrength != null) {
//                        mTvRechargeStrength.setText(String.format(getString(R.string.current_strenth_left),
//                                otherInfoResp.userBalance.strength));
//                    }
//                    if (mGiftPanelDialogFt != null) {
//                        mGiftPanelDialogFt.setStarCoin(otherInfoResp.userBalance.strength);
//                    }
//                }
//                break;
//            case APIService.PAY_TYPE://充值列表
//                PayTypeResp payTypeResp = (PayTypeResp) req.getData();
//                if (payTypeResp != null) {
//                    List<PayTypeResp.SystemRechargeSetupsBean> rechargeSetups = payTypeResp.systemRechargeSetups;
//                    showRechargeDialog(rechargeSetups, null);
//                } else {
//                    isRechargeDialogShown = false;
//                }
//                break;
//            case APIService.PAY_TYPE_RECHARGE://点击充值按钮
//                PayTypeResp payTypeResp2 = (PayTypeResp) req.getData();
//                if (payTypeResp2 != null) {
//                    List<PayTypeResp.SystemRechargeSetupsBean> rechargeSetups = payTypeResp2.systemRechargeSetups;
//                    showRechargeDialog(rechargeSetups, getString(R.string.recharge_title));
//                } else {
//                    isRechargeDialogShown = false;
//                }
//                break;
//            default:
//                break;
//        }
//
//    }


    /**
     * 显示用户资料 主播界面按钮为：踢人 禁言；观众界面按钮为：举报 拉黑
     *
     * @param resp 获取用户资料返回的参数
     */
    protected void showUserInfoDialog(final UserInfoResp resp) {
        showTestToast("showUserInfoDialog");
//        //获取用户资料成功，显示用户资料对话框
//        mUserInfoDialog = DialogUtil.showCenterDialog(BasePlayActivity.this, new DialogUtil.CustomizeAction() {
//            @Override
//            public void setCustomizeAction(final Dialog dialog, View view) {
//                SimpleDraweeView sdvHead = (SimpleDraweeView) view.findViewById(R.id.sdf_head);
//                ImageView ivSex = (ImageView) view.findViewById(R.id.iv_sex);
//                TextView tvNick = (TextView) view.findViewById(R.id.tv_nick);
//                TextView tvUserID = (TextView) view.findViewById(R.id.tv_userid);
//                TextView tvFollowed = (TextView) view.findViewById(R.id.tv_followed_num);
//                TextView tvFans = (TextView) view.findViewById(R.id.tv_fans_num);
//                TextView tvPrivateChat = (TextView) view.findViewById(R.id.tv_private_chat);//私信
//                tvTip = (TextView) view.findViewById(R.id.tv_tip);
//                tv2Black = (TextView) view.findViewById(R.id.tv_to_black_list);
//                Button btnMainPage = (Button) view.findViewById(R.id.btn_main_page);//主页
//                mContentFollow = view.findViewById(R.id.content_follow);
//                final TextView TvFollow = (TextView) view.findViewById(R.id.tv_info_follow);//关注
//                switch (resp.user.sex) {
//                    case 0://女
//                        ivSex.setImageResource(R.mipmap.nv);
//                        break;
//                    case 1://男
//                        ivSex.setImageResource(R.mipmap.nan);
//                        break;
//                    case -1://保密
//                        ivSex.setVisibility(View.INVISIBLE);
//                        break;
//                }
//
//                if (inPlayResp.type == 0 && inPlayResp.admin && resp.user.userId != inPlayResp.userLive.userId) {
//                    //如果当前为直播，点击的不是主播，且有管理员身份
//                    tvTip.setText(resp.gag ? R.string.delete_gag : R.string.gag);
//                    tv2Black.setText(R.string.kick);
//                } else {
//                    tvTip.setText(R.string.tip);
//                    tv2Black.setText(resp.black ? getString(R.string.black_added) : getString(R.string.add_black));
//                }
//                DraweeController controller = Fresco.newDraweeControllerBuilder()
//                        .setUri(resp.user.avatar)
//                        .setOldController(sdvHead.getController())
//                        .build();
//                sdvHead.setController(controller);//头像
//                tvNick.setText(resp.user.nickName);//昵称
//                tvUserID.setText(String.format(getString(R.string.userid), resp.user.userId));
//                tvFollowed.setText(String.format(getString(R.string.followed_num), resp.user.followCount));
//                tvFans.setText(String.format(getString(R.string.fans_num), resp.user.fansCount));
//                manageFollowButton(resp.follow);//初始化按钮状态
//                SimpleDraweeView sdvVip = (SimpleDraweeView) view.findViewById(R.id.iv_vip);
//                sdvVip.setImageURI("http://pic1.grtstar.cn/image/static/" + resp.user.level + "_3x.png");
//                if (resp.user.userId == app.getUserID()) {
//                    //调整关注按钮大小
//                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnMainPage.getLayoutParams();
//                    layoutParams.leftMargin = DensityUtil.dip2px( 8);
//                    layoutParams.rightMargin = DensityUtil.dip2px(8);
//                    btnMainPage.setLayoutParams(layoutParams);
//                    mContentFollow.setVisibility(View.GONE);
//                    //如果是自己，隐藏举报、拉黑等按钮
//                    view.findViewById(R.id.layout_ctrl).setVisibility(View.GONE);
//                }
//                View.OnClickListener listener = new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        switch (v.getId()) {
//                            case R.id.tv_tip:
//                                if (inPlayResp.type == 0 && inPlayResp.admin && resp.user.userId != inPlayResp.userLive.userId) {
//                                    //禁言
//                                    showBottomMenu(4, resp.user.userId);
//                                } else {
//                                    //举报
//                                    showBottomMenu(1, resp.user.userId);
//                                }
//                                break;
//                            case R.id.tv_to_black_list:
//                                if (inPlayResp.type == 0 && inPlayResp.admin && resp.user.userId != inPlayResp.userLive.userId) {
//                                    //踢人
//                                    showBottomMenu(3, resp.user.userId);
//                                } else {
//                                    //拉黑
//                                    showBottomMenu(2, resp.user.userId);
//                                }
//                                break;
//                            case R.id.content_follow://关注
//                                if (TvFollow.getText().toString().equals(getString(R.string.followed))) {
//                                    //已关注该用户，请求取消关注接口
//                                    if (resp.user.userId == inPlayResp.anchorBalance.userId) {
//                                        //取消关注明星
//                                        deleteFollow(resp.user.userId, APIService.DELETE_FOLLOW_STAR);
//                                    } else {
//                                        deleteFollow(resp.user.userId, APIService.DELETE_FOLLOW);
//                                    }
//
//                                } else {
//                                    //未关注该用户，请求关注接口
//                                    if (resp.user.userId == inPlayResp.anchorBalance.userId) {
//                                        //关注主播
//                                        int liveid = 0;
//                                        if (inPlayResp != null && inPlayResp.type == 0) {
//                                            //直播过程中关注主播
//                                            liveid = inPlayResp.userLive.liveId;
//                                        }
//                                        addFollow(resp.user.userId, liveid, APIService.ADD_FOLLOW_STAR);
//                                    } else {
//                                        addFollow(resp.user.userId, 0, APIService.ADD_FOLLOW);
//                                    }
//                                }
//                                break;
//                            case R.id.btn_main_page:
//                                //跳转到个人主页
//                                Intent intent = new Intent(mContext, NewUserHomeActivity.class);
//
//                                intent.putExtra(NewUserHomeActivity.USER_ID, resp.user.userId);
//                                startActivityForResult(intent, Constant.HOMEPAGE_INTENT_REQUEST_CODE);
//                                break;
//                            case R.id.tv_private_chat://私信
//                                startPrivateChat(String.valueOf(resp.user.userId));
//                                dialog.dismiss();
//                                break;
//                            default:
//                                break;
//                        }
//
//                    }
//                };
//                btnMainPage.setOnClickListener(listener);
//                mContentFollow.setOnClickListener(listener);
//                tvTip.setOnClickListener(listener);
//                tv2Black.setOnClickListener(listener);
//                tvPrivateChat.setOnClickListener(listener);
//            }
//        }, R.layout.dialog_user_info);
//        mUserInfoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialog) {
//                isRequringUserData = false;
//            }
//        });
    }

    /**
     * 处理用户信息对话框中关注按钮的样式
     *
     * @param followed
     */
    private void manageFollowButton(boolean followed) {
        showTestToast("manageFollowButton");
//        if (mContentFollow == null) {
//            return;
//        }
//        TextView tvFollow = (TextView) mContentFollow.findViewById(R.id.tv_info_follow);
//        if (followed) {
//            mContentFollow.setBackgroundResource(R.drawable.bg_btn_user_info_unfollow);
//            tvFollow.setTextColor(ContextCompat.getColor(this, R.color.cto));
//            tvFollow.setText(getString(R.string.followed));
//            tvFollow.setCompoundDrawables(null, null, null, null);
//        } else {
//            mContentFollow.setBackgroundResource(R.drawable.bg_btn_follow_dialog_user_info);
//            tvFollow.setTextColor(ContextCompat.getColor(this, R.color.white));
//            tvFollow.setText(getString(R.string.add_follow));
//            tvFollow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baisejiahao, 0, 0, 0);
//        }
    }

    /**
     * 显示确定取消菜单
     *
     * @param type   1为举报 2为拉黑 3为踢人 4为禁言
     * @param userId 用户id
     */
    protected void showBottomMenu(final int type, final int userId) {
        showTestToast("showBottomMenu");
//        switch (type) {
//            case 1://举报
//                //举报
//                DialogUtil.showSelectDialog(this, new DialogUtil.CustomizeAction() {
//                    @Override
//                    public void setCustomizeAction(final Dialog dialog, View view) {
//                        View.OnClickListener listener = new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                switch (v.getId()) {
//                                    case R.id.tv_ad:
//                                        tipOff(userId, 1);
//                                        break;
//                                    case R.id.tv_obscene:
//                                        tipOff(userId, 2);
//                                        break;
//                                    case R.id.tv_abuse:
//                                        tipOff(userId, 3);
//                                        break;
//                                    case R.id.tv_counter:
//                                        tipOff(userId, 4);
//                                        break;
//                                    case R.id.tv_other:
//                                        tipOff(userId, 10);
//                                        break;
//                                    case R.id.tv_cancel:
//                                        break;
//                                    default:
//                                        break;
//                                }
//
//                                dialog.dismiss();
//                            }
//                        };
//                        view.findViewById(R.id.tv_ad).setOnClickListener(listener);
//                        view.findViewById(R.id.tv_obscene).setOnClickListener(listener);
//                        view.findViewById(R.id.tv_abuse).setOnClickListener(listener);
//                        view.findViewById(R.id.tv_counter).setOnClickListener(listener);
//                        view.findViewById(R.id.tv_other).setOnClickListener(listener);
//                        view.findViewById(R.id.tv_cancel).setOnClickListener(listener);
//                    }
//                }, true, R.layout.dialog_bottom_menu_tip, WindowManager.LayoutParams.WRAP_CONTENT);
//                break;
//            case 2://拉黑
//                //拉黑
//                DialogUtil.showSelectDialog(this, new DialogUtil.CustomizeAction() {
//                    @Override
//                    public void setCustomizeAction(final Dialog dialog, View view) {
//                        View.OnClickListener listener = new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                switch (v.getId()) {
//                                    case R.id.tv_ok:
//                                        if (tv2Black.getText().toString().equals(getString(R.string.add_black))) {
//                                            //拉黑
//                                            add2Black(userId);
//                                        } else {
//                                            //取消拉黑
//                                            delBlack(userId);
//                                        }
//                                        break;
//                                    case R.id.tv_cancel:
//                                        break;
//                                    default:
//                                        break;
//                                }
//                                dialog.dismiss();
//                            }
//                        };
//                        view.findViewById(R.id.tv_cancel).setOnClickListener(listener);
//                        view.findViewById(R.id.tv_ok).setOnClickListener(listener);
//                    }
//                }, true, R.layout.dialog_bottom_menu_confirm, WindowManager.LayoutParams.WRAP_CONTENT);
//                break;
//            case 3://踢人
//            case 4://禁言
//                DialogUtil.showSelectDialog(this, new DialogUtil.CustomizeAction() {
//                    @Override
//                    public void setCustomizeAction(final Dialog dialog, View view) {
//                        View.OnClickListener listener = new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                switch (v.getId()) {
//                                    case R.id.tv_ok:
//                                        if (type == 3) {
//                                            //踢人
//                                            kickOff(userId, getLiveId());
//                                        } else if (tvTip != null) {
//                                            //禁言
//                                            if (tvTip.getText().toString().equals(getString(R.string.gag))) {
//                                                //禁言
//                                                gag(userId, getLiveId());
//                                            } else {
//                                                //解禁
//                                                deleteGag(userId, getLiveId());
//                                            }
//                                        }
//                                        break;
//                                    case R.id.tv_cancel:
//                                        break;
//                                    default:
//                                        break;
//                                }
//                                dialog.dismiss();
//                            }
//                        };
//                        view.findViewById(R.id.tv_cancel).setOnClickListener(listener);
//                        view.findViewById(R.id.tv_ok).setOnClickListener(listener);
//                    }
//                }, true, R.layout.dialog_bottom_menu_confirm, WindowManager.LayoutParams.WRAP_CONTENT);
//                break;
//            default:
//                break;
//        }

    }

    /**
     * 关注
     *
     * @param userid
     */
    protected void addFollow(int userid, int liveid, String flag) {
        showTestToast("addFollow");
//        Call<RespResult> call = mApiService.addFollow(userid, liveid);
//        startRequest(call, flag);
    }

    /**
     * 取消关注
     *
     * @param userid
     */
    protected void deleteFollow(int userid, String flag) {
        showTestToast("deleteFollow");
//        Call<RespResult> call = mApiService.deleteFollow(userid);
//        startRequest(call, flag);
    }

    /**
     * 举报
     *
     * @param userId 被举报者id
     * @param type   举报原因 1-广告欺骗 2-淫秽色情 3-骚扰谩骂 4-反动政治 10-其他内容
     */
    protected void tipOff(int userId, int type) {
        showTestToast("tipOff");
//        int liveUserId = inPlayResp.userLive.userId;//主播id
//        //status 0为举报主播 1为举报用户
//        Call<RespResult> call = mApiService.report(userId, userId == liveUserId ? 0 : 1, type);
//        startRequest(call, APIService.REPORT);
    }

    /**
     * 加入黑名单
     *
     * @param userid
     */
    protected void add2Black(int userid) {
        showTestToast("add2Black");
//        Call<RespResult> call = mApiService.addBlack(userid);
//        startRequest(call, APIService.ADD_BLACK);
    }

    /**
     * 取消拉黑
     *
     * @param userid
     */
    protected void delBlack(int userid) {
        showTestToast("delBlack");
//        Call<RespResult> call = mApiService.delBlack(userid);
//        startRequest(call, APIService.DEL_BLACK);
    }

    /**
     * 踢人
     *
     * @param userId
     */
    protected void kickOff(int userId, int liveId) {
        showTestToast("kickOff");
//        if (liveId == 0 || userId == 0) {
//            showToast(getString(R.string.do_failed));
//            return;
//        }
//        Call<RespResult> call = mApiService.kickOff(userId, liveId);
//        startRequest(call, APIService.KICK_OFF);
    }

    /**
     * 禁言
     *
     * @param userId
     */
    protected void gag(int userId, int liveId) {
        showTestToast("gag");
//        if (liveId == 0 || userId == 0) {
//            showToast(getString(R.string.do_failed));
//            return;
//        }
//        Call<RespResult> call = mApiService.gag(userId, liveId);
//        startRequest(call, APIService.GAG);
    }

    /**
     * 解禁
     *
     * @param userId
     * @param liveId
     */
    protected void deleteGag(int userId, int liveId) {
        showTestToast("deleteGag");
//        if (liveId == 0 || userId == 0) {
//            showToast(getString(R.string.do_failed));
//            return;
//        }
//        Call<RespResult> call = mApiService.deleteGag(userId, liveId);
//        startRequest(call, APIService.DELETE_GAG);
    }

    protected abstract void sendMsg(final ChatRoomMessage message);

    /**
     * 注册消息监听
     *
     * @param register 注册/注销
     */
    protected abstract void setOnMsgListener(boolean register);

    /**
     * 分享
     */
    protected void share2Friends() {
        DialogUtils.showShareDialog(this, new OnShareClickListener() {
            @Override
            public void weixin() {

            }

            @Override
            public void sina() {

            }

            @Override
            public void facebook() {

            }

            @Override
            public void twitter() {

            }

            @Override
            public void copy() {

            }
        });
//        if (inPlayResp == null || inPlayResp.liveShare == null) {
//            showToast(getString(R.string.can_not_share));
//            return;
//        }
//        DialogUtil.showSelectDialog(BasePlayActivity.this, new DialogUtil.CustomizeAction() {
//            @Override
//            public void setCustomizeAction(final Dialog dialog, View view) {
//
//                View.OnClickListener listener = new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        SHARE_MEDIA type = null;
//                        switch (v.getId()) {
//                            case R.id.tv_share_friends:
//                                type = SHARE_MEDIA.WEIXIN_CIRCLE;
//                                break;
//                            case R.id.tv_share_weixin:
//                                type = SHARE_MEDIA.WEIXIN;
//                                break;
//                            case R.id.tv_share_sina:
//                                type = SHARE_MEDIA.SINA;
//                                break;
//                            case R.id.tv_share_qq:
//                                type = SHARE_MEDIA.QQ;
//                                break;
//                            case R.id.tv_share_qqqzon:
//                                type = SHARE_MEDIA.QZONE;
//                                break;
//                            case R.id.tv_share_cancel:
//                                break;
//                            default:
//                                break;
//                        }
//                        if (type != null) {
//                            shareData(type, inPlayResp.liveShare.title, inPlayResp.liveShare.content,
//                                    inPlayResp.liveShare.picUrl, inPlayResp.liveShare.url, 1, getLiveId());
//                        }
//                        dialog.dismiss();
//                    }
//                };
//                view.findViewById(R.id.tv_share_friends).setOnClickListener(listener);
//                view.findViewById(R.id.tv_share_weixin).setOnClickListener(listener);
//                view.findViewById(R.id.tv_share_sina).setOnClickListener(listener);
//                view.findViewById(R.id.tv_share_qq).setOnClickListener(listener);
//                view.findViewById(R.id.tv_share_qqqzon).setOnClickListener(listener);
//                view.findViewById(R.id.tv_share_cancel).setOnClickListener(listener);
//            }
//        }, true, R.layout.sharereplay, 260);
    }

    /**
     * 清屏
     *
     * @param clear true:清屏 false:显示
     */
    protected abstract void clearScreen(boolean clear);

    /**
     * 开始动画
     */
    @Override
    protected void startAnim() {
        if (isPlayingFullScreenAnim) {
            return;
        }
        if (mListContributionAnim.size() > 0 && !isPlayingFullScreenAnim) {
            //暂停连送
            gv.onPause();
            gv2.onPause();
            //播放用户进入动画
            ChatRoomMessage chatRoomMessage = mListContributionAnim.get(0);
            //播放全屏动画
            playFullScreenGift(mContentFullScreenGift, chatRoomMessage);
            mListContributionAnim.remove(0);
            return;
        }
        if (mListBigAnim.size() > 0 && !isPlayingFullScreenAnim) {
            //暂停连送
            gv.onPause();
            gv2.onPause();
            //如果gv不在播放动画而此时有清屏动画
            ChatRoomMessage chatRoomMessage = mListBigAnim.get(0);
            //播放全屏动画
            playFullScreenGift(mContentFullScreenGift, chatRoomMessage);
            mListBigAnim.remove(0);
            return;
        }

        //若之前有连送未播放完，则继续播放
        boolean isPause = false;
        if (gv.isPaused()) {
            gv.startAnim();
            isPause = true;
        }
        if (gv2.isPaused()) {
            gv2.startAnim();
            isPause = true;
        }
        if (isPause) {
            return;
        }

        if (mListAnim.size() > 0) {
            GiftMsgAttachment attachment = (GiftMsgAttachment) mListAnim.get(0).getAttachment();
            if (!gv.isAnimating()) {
                boolean b = gv2.getAttachment() != null && (!gv2.getAttachment().getUserId().equals(attachment.getUserId())
                        || gv2.getAttachment().getGiftType() != attachment.getGiftType());
                if (!gv2.isAnimating() || b) {
                    gv.setData(attachment);
                    gv.startAnim();
                    mListAnim.remove(0);
                }
            } else if (!gv2.isAnimating()) {
                if (gv.getAttachment() != null &&
                        (GiftMsgUtil.getInstance().isFullScreenGift(gv.getAttachment().getGiftType()))) {
                    //如果gv正在播放清屏动画，则gv2不能播放动画
                    return;
                }
                if (gv.getAttachment() != null && (!gv.getAttachment().getUserId().equals(attachment.getUserId())
                        || gv.getAttachment().getGiftType() != attachment.getGiftType())) {
                    gv2.setData(attachment);
                    gv2.startAnim();
                    mListAnim.remove(0);
                }
            }
        }
    }

    /**
     * 动画分配
     *
     * @param gv
     * @return
     */
    protected synchronized GiftMsgAttachment getAttachment(GiftView gv) {
        if (isPlayingFullScreenAnim) {
            return null;
        }
        GiftView giftView;
        if (gv.equals(this.gv)) {
            if (mListBigAnim.size() > 0) {
                //只允许gv播放清屏动画
                ChatRoomMessage msg = mListBigAnim.get(0);
                GiftMsgAttachment attachment = (GiftMsgAttachment) msg.getAttachment();
                mListBigAnim.remove(msg);
                return attachment;
            } else {
                giftView = this.gv2;
            }
        } else {
            if (this.gv.getAttachment() != null &&
                    (GiftMsgUtil.getInstance().isFullScreenGift(this.gv.getAttachment().getGiftType()))) {
                //如果gv正在播放清屏动画，则不允许gv2播放动画
                return null;
            } else {
                giftView = this.gv;
            }
        }
        for (ChatRoomMessage msg : mListAnim) {
            GiftMsgAttachment attachment = (GiftMsgAttachment) msg.getAttachment();
            if (giftView.getAttachment() == null || (!giftView.getAttachment().getUserId().equals(attachment.getUserId())
                    || giftView.getAttachment().getGiftType() != attachment.getGiftType())) {
                mListAnim.remove(msg);
                return attachment;
            }
        }
        return null;
    }

    @Override
    protected void startViewerEnterAnim() {

    }

    protected void showGiftPanel() {
        if (mGiftPanelDialogFt == null) {
            mGiftPanelDialogFt = new GiftPanelDialogFt();
            Bundle bundle = new Bundle();
            bundle.putInt(GiftPanelDialogFt.TAG_LIVE_TYPE, 0);
            bundle.putString(GiftPanelDialogFt.TAG_STAR_COIN, inPlayResp.userWallet.numStr);
            mGiftPanelDialogFt.setArguments(bundle);
            mGiftPanelDialogFt.setOnPanelListener(new GiftPanelDialogFt.OnPanelListener() {
                @Override
                public void onDismiss() {
                    onGiftPanelShow(false);
                }

                @Override
                public void onGiftSend(int giftType, int giftNum, int amount) {
                    long giftPrice = GiftMsgUtil.getInstance().getGiftPrice(giftType);
                    long totalPrice = giftPrice * (amount - giftNum);
                    if (inPlayResp.userWallet.num - totalPrice < 0) {
                        //体力不足，获取充值列表
                        getPayTypeList(1);
                        return;
                    }
//                    if (mGiftRequest != null && mGiftRequest.getCall().isExecuted()) {
//                        mGiftRequest.getCall().cancel();
//                    }
                    mGiftRequest = APIHelper.getInstance().sendGift(inPlayResp.userLiveItem.liveId,
                            giftType, amount == 0 ? 1 : giftNum, amount, new CallBack<ApplauseResp>() {
                                @Override
                                public void onSuccess(ApplauseResp data) {
                                    if (isFinishing()) {
                                        return;
                                    }
                                    if (data != null) {
                                        //保存用户体力值
                                        inPlayResp.userWallet.num = data.userGift.coinNum;
                                        inPlayResp.userWallet.numStr = data.userGift.coinNumStr;
                                        inPlayResp.userLiveItem.coinNum = data.userGift.liveCoinNum;//保存最新的主播币数
                                        inPlayResp.userLiveItem.coinNumStr = data.userGift.liveCoinNumStr;//保存最新的主播币数字符串
                                        boolean b = data.userGift.paymentOk;
                                        if (mGiftPanelDialogFt != null) {
                                            mGiftPanelDialogFt.onResponse(b);
                                            //更新用户体力值
                                            mGiftPanelDialogFt.setStarCoin(data.userGift.coinNumStr);
                                        }
                                        if (!b) {
                                            //体力不足，获取充值列表
                                            getPayTypeList(1);
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(BaseReslut data) {
                                    if (isFinishing()) {
                                        return;
                                    }
                                    mGiftNum = -1;
                                    if (mGiftPanelDialogFt != null) {
                                        mGiftPanelDialogFt.onResponse(false);
                                    }
                                    super.onFailure(data);
                                }
                            });
                }

                @Override
                public void onCombo(int giftType, int giftNum) {
                    long giftPrice = GiftMsgUtil.getInstance().getGiftPrice(giftType);
                    if (inPlayResp.userWallet.num - giftPrice < 0) {
                        //体力不足，获取充值列表
                        getPayTypeList(1);
                        return;
                    }
                    long coinNum = inPlayResp.userLiveItem.coinNum + giftPrice;//计算主播获得礼物后的币数
                    BigDecimal decimal = new BigDecimal(coinNum);
                    BigDecimal divide = new BigDecimal(NumberUtil.OFFSET);
                    BigDecimal result = decimal.divide(divide, 6);
                    String coinNumStr = result.stripTrailingZeros().toPlainString();
                    //发送消息
                    sendMsg(createGiftMsg(getLiveId(), getRoomId(), String.valueOf(App.getApp().getUserId()),
                            App.getApp().getNickName(), App.getApp().getUser().avatar, giftType, giftNum > 1 ? giftNum : 2,
                            inPlayResp.userLiveItem.coinNum + giftPrice, coinNumStr, inPlayResp.userGradePic));
                }

                @Override
                public void onRechargeClick() {
                    getPayTypeList(0);//充值
                }
            });
        } else {
            //更新金币值
            mGiftPanelDialogFt.setStarCoin(inPlayResp.userWallet.numStr);
        }
        if (!mGiftPanelDialogFt.isAdded()) {
            //避免快速点击出现Fragment already added异常
            mGiftPanelDialogFt.show(getSupportFragmentManager(), null);
            onGiftPanelShow(true);
        }
    }

    /**
     * 当礼物菜单消失时
     *
     * @param isShow 菜单是否显示
     */
    protected abstract void onGiftPanelShow(boolean isShow);

    /**
     * 获取充值列表
     *
     * @param type 0：显示标题为“星币充值”；1:显示标题为“新币不足，请充值”
     */
    protected void getPayTypeList(int type) {
        showTestToast("getPayTypeList");
//        if (isRechargeDialogShown) {
//            return;
//        }
//        isRechargeDialogShown = true;
//        showProgressDialog();
//        Call<PayTypeResp> call = mApiService.getPayTypeList(0);
//        startRequest(call, type == 0 ? APIService.PAY_TYPE_RECHARGE : APIService.PAY_TYPE);
    }

//    /**
//     * 显示充值框
//     *
//     * @param title 标题 为null显示默认标题
//     */
//    protected void showRechargeDialog(final List<PayTypeResp.SystemRechargeSetupsBean> rechargeSetups, final String title) {
//        Dialog d = DialogUtil.showSelectDialog(this, new DialogUtil.CustomizeAction() {
//                    @Override
//                    public void setCustomizeAction(final Dialog dialog, final View view) {
//                        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
//                        TextView tvZhifubao = (TextView) view.findViewById(R.id.charge_zhifubao);
//                        Button btnRecharge = (Button) view.findViewById(R.id.btn_recharge);
//                        mTvRechargeStrength = (TextView) view.findViewById(R.id.tv_current_strength);
//                        if (title != null) {
//                            tvTitle.setText(title);
//                        }
//                        mTvRechargeStrength.setText(String.format(getString(R.string.current_strenth_left),
//                                inPlayResp.userBalance.strength));
//                        tvZhifubao.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                showToast(getString(R.string.zhifubao_not_available));
//                            }
//                        });
//                        RecyclerView rvRecharge = (RecyclerView) view.findViewById(R.id.rv_recharge);
//                        LinearLayoutManager manager = new LinearLayoutManager(BasePlayActivity.this, LinearLayoutManager.HORIZONTAL, false);
//                        rvRecharge.setLayoutManager(manager);
//                        final RechargeAdapter adapter = new RechargeAdapter(BasePlayActivity.this, rechargeSetups);
//                        rvRecharge.setAdapter(adapter);
//                        btnRecharge.setOnClickListener(new View.OnClickListener() {
//                                                           @Override
//                                                           public void onClick(View v) {
//                                                               goReharge(RECHARGE_WEIXIN, adapter.getRechargeId());//充值
//                                                           }
//                                                       }
//
//                        );
//                    }
//                }
//
//                , true, R.layout.dialog_recharge_list, WindowManager.LayoutParams.WRAP_CONTENT);
//        d.setOnDismissListener(new DialogInterface.OnDismissListener()
//
//                               {
//                                   @Override
//                                   public void onDismiss(DialogInterface dialog) {
//                                       //清空选中的充值金额
//                                       mTvRechargeStrength = null;
//                                       isRechargeDialogShown = false;
//                                   }
//                               }
//
//        );
//    }

    /**
     * 粉丝贡献榜
     */
    protected void showContributionList() {
        showTestToast("showContributionList");
//        if (inPlayResp == null || inPlayResp.liveType != 0) {
//            //直播类型为投票时，不跳转到礼物排行榜
//            return;
//        }
//        int userid;
//        if (inPlayResp.type == 2) {
//            //主播id
//            userid = inPlayResp.livePrevue.userId;
//        } else {
//            userid = inPlayResp.userLive.userId;
//        }
//        if (userid != 0) {
//            Intent intent = new Intent(this, GiftListActivity.class);
//            intent.putExtra(GiftListActivity.USER_ID, userid);
//            startActivity(intent);
//        }
    }

    /**
     * 充值
     *
     * @param rechargeType 充值平台类型 0:微信 1：支付宝
     * @param payid        充值金额（元）
     */
    protected void goReharge(int rechargeType, int payid) {
        showTestToast("goReharge");
//        showProgressDialog();
//        Call<RechargeResp> call = mApiService.recharge(rechargeType, payid);
//        startRequest(call, APIService.RECHARGE);
    }

    /**
     * 获取用户资料并弹出dialog
     *
     * @param userid
     */
    protected void getUserInfo(int userid) {
        showTestToast("getUserInfo");
//        if (isRequringUserData) {
//            //防止重复点击
//            return;
//        }
//        isRequringUserData = true;
//        Call<UserInfoResp> call = mApiService.getUserInfo(userid, getLiveId());
//        startRequest(call, APIService.USERINFO);
    }

    protected void orderQuery(String prepayId) {//订单查询
        showTestToast("orderQuery");
//        if (TextUtils.isEmpty(prepayId)) {
//            return;
//        }
//        showProgressDialog();
//        Call<OrderQueryResp> call = mApiService.orderQuery(prepayId);
//        startRequest(call, APIService.ORDER_QUERY);
    }

    protected void getOtherInfo() {//获取体力值
        showTestToast("getOtherInfo");
//        showProgressDialog();
//        Call<OtherInfoResp> call = mApiService.getOtherInfo();
//        startRequest(call, APIService.OTHER_INFO);
    }

    @Override
    protected String getRoomId() {
        if (inPlayResp != null && inPlayResp.userLiveItem != null && inPlayResp.userLiveItem.userLiveBaseExtended != null) {
            return inPlayResp.userLiveItem.userLiveBaseExtended.roomId;
        }
        return null;
    }

    /**
     * 获取直播/预告id
     * type 0为直播，1为回看，2为预告
     *
     * @return
     */
    @Override
    protected int getLiveId() {
        if (inPlayResp != null && inPlayResp.userLiveItem != null) {
            return inPlayResp.userLiveItem.liveId;
        }
        return 0;
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == Constant.HOMEPAGE_INTENT_REQUEST_CODE && resultCode == Constant.HOMEPAGE_INTENT_RESULT_CODE) {
//            boolean isFollow = data.getBooleanExtra("isFollow", false);
//            if (isFollow) {
//                avaterMingpianData.follow = true;
//                if (inPlayResp != null && inPlayResp.type == 2) {
//                    //预告
//                    if (avaterMingpianData.user.userId == inPlayResp.livePrevue.userId) {
//                        //关注主播，隐藏关注主播按钮
//                        mContentBtnFollow.setVisibility(View.GONE);
//                        startFollowTimer(false);
//                    }
//                } else if (inPlayResp != null && (inPlayResp.type == 0 || inPlayResp.type == 1)) {
//                    //直播或回放
//                    if (avaterMingpianData.user.userId == inPlayResp.userLive.userId) {
//                        //关注主播，隐藏关注主播按钮
//                        mContentBtnFollow.setVisibility(View.GONE);
//                        startFollowTimer(false);
//                    }
//                }
//                if (null != mContentFollow) {
//                    manageFollowButton(true);
//                }
//            } else {
//                avaterMingpianData.follow = false;
//                if (inPlayResp != null && inPlayResp.type == 2) {
//                    //预告
//                    if (avaterMingpianData.user.userId == inPlayResp.livePrevue.userId) {
//                        //取消关注主播，显示关注主播按钮
//                        mContentBtnFollow.setVisibility(View.VISIBLE);
//                        resetFollowTimer();
//                        startFollowTimer(true);
//                    }
//                } else if (inPlayResp != null && (inPlayResp.type == 0 || inPlayResp.type == 1)) {
//                    //直播或回放
//                    if (avaterMingpianData.user.userId == inPlayResp.userLive.userId) {
//                        //取消关注主播，显示关注主播按钮
//                        mContentBtnFollow.setVisibility(View.VISIBLE);
//                        resetFollowTimer();
//                        startFollowTimer(true);
//                    }
//                }
//                if (mContentFollow != null) {
//                    manageFollowButton(false);
//                }
//            }
//        }
//    }
}

