package com.netease.nim.uikit.bean;

/**
 * 自定义消息、通知类型
 * Created by zhanghengzhen on 2016/9/1.
 */
public final class CustomAttachmentType {
    //自定义通知类型
    public static final int TYPE_GAG = 1;//禁言
    public static final int TYPE_RELIVE_GAG = 3;//解除禁言
    public static final int TYPE_ADD_ADMIN = 30;//添加管理员
    public static final int TYPE_DEL_ADMIN = 31;//移除管理员
    public static final int TYPE_RECHARGE = 1000;//公众号充值
    public static final int TYPE_SYS_NOTI = 10000;//系统通知 升级
    public static final int TYPE_STAR_NUM_UP = 620;//系统通知 升级
    public static final int TYPE_GRAP = 222;//抢麦通知

    //自定义消息类型
    public static final int TYPE_GIFT = 6;//消息类型为礼物
    public static final int TYPE_WIN = 9;//消息类型为中奖
    public static final int TYPE_PREPARE_WIN = 8;//准备中奖
    public static final int TYPE_LIVE_END = 2;//直播结束，由服务器发送
    public static final int TYPE_KICK = 0;//踢人
    public static final int TYPE_PAUSE_LIVE = 4;//暂停直播
    public static final int TYPE_RESUME_LIVE = 5;//恢复直播
    public static final int TYPE_ADVANCE_LIVE = 10;//提前直播
    public static final int TYPE_PREVIEW_START = 20;//预告开播，此时重新请求资料
    public static final int TYPE_FORCE_EXIT = 22;//强制所有用户退出
    public static final int TYPE_MEMBER_EXIT = 23;//用户退出提醒
    public static final int TYPE_MEMBER_IN = 24;//用户进入提醒
    public static final int TYPE_TOTAL_GIFT = 7;//所有礼物数量
    public static final int TYPE_LIKE = 40;//点赞消息
    public static final int TYPE_FOLLOW = 25;//关注主播消息
    public static final int TYPE_SHARE = 26;//分享直播消息
    public static final int TYPE_NEW_GIFT = 66;//新的礼物类型
    public static final int TYPE_TICKET = 60;//门票消息 Applause为门票数量
    public static final int TYPE_EGG = 61;//鸡蛋消息 Applause为鸡蛋数量 Gift_type:100为主播第一次死亡；101为死亡，没有下面等待直播；102为死亡，下面有等待直播	（根据101和102判断主播是否发起抢直播界面）。Login_info	0没有红包；1有红包（弹出红包界面）
    public static final int TYPE_STAR = 62;//星星消息 userId为送星星的用户
    public static final int TYPE_GROUP_END = 2222;//组直播结束
    public static final int TYPE_GROUP_CONTINUE = 55;//继续组直播
    public static final int TYPE_GET_EGG = 610;//鸡蛋数量增加的
    public static final int TYPE_GET_TICKET = 600;//门票数量增加
    public static final int TYPE_EGG_RED_POCKET = 223;//显示鸡蛋红包 同一时间只显示一个
    public static final int TYPE_NEW_TICKET = 160;//新的门票自定义消息，只用于发送
    public static final int TYPE_NEW_EGG = 161;//新的鸡蛋自定义消息，只用于发送
    public static final int TYPE_GOODS_URL = 1000;//商品链接消息
    public static final int TYPE_GOODS_URL_TIP = 999999;//发送商品链接的提示消息，只对自己展示
    public static final int TYPE_ORDER = 2000;//订单消息
}
