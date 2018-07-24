package com.netease.nim.uikit.bean;

import org.json.JSONObject;

import java.io.Serializable;


/**
 * 鸡蛋
 * Created by zhanghengzhen on 2017/3/7.
 */
public class EggMsgAttachment extends CustomAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String USER_ID = "user_id";
    private static final String APPLAUSE = "applause";//主播收到的总鸡蛋数
    private static final String GIFT_TYPE = "gift_type";
    private static final String LOGIN_INFO = "login_info";
    private static final String STRENGTH = "strength";
    private static final String PREFIX_PIC = "prefix_pic";
    private static final String NICKNAME = "nickname";
    private static final String VIEW_NUM = "view_num";
    private static final String LIVE_HITS = "live_hits";

    private int userId;
    private int applause;//鸡蛋数量
    private int loginInfo;//0没有红包 1有红包（弹出红包界面）
    private int strength;//主播剩余体力值
    private int giftType;//100为主播第一次死亡 101为死亡，没有下面等待直播 102为死亡，下面有等待直播（根据101和102判断主播是否发起抢直播界面）。
    private String nickname;
    private String prefixPic;//等级
    private String viewNum;//用于显示收入
    private int liveHits;//为1时开启红包倒计时

    // 自定义消息附件的类型，根据该字段区分不同的自定义消息

    @Override
    public String toJson(boolean b) {
        return CustomAttachParser.packData(customType, packData());
    }

    public EggMsgAttachment() {
        super(CustomAttachmentType.TYPE_EGG);//消息类型
    }

    public int getApplause() {
        return applause;
    }

    public int getGiftType() {
        return giftType;
    }

    public int getLoginInfo() {
        return loginInfo;
    }

    public int getUserId() {
        return userId;
    }

    public int getStrength() {
        return strength;
    }

    public String getNickname() {
        return nickname;
    }

    public String getPrefixPic() {
        return prefixPic;
    }

    public String getViewNum() {
        return viewNum;
    }

    public int getLiveHits() {
        return liveHits;
    }

    // 解析数据
    @Override
    protected void parseData(JSONObject data) {
        customType = data.optInt(CUSTOM_TYPE);//必须解析父类的该字段
        userId = data.optInt(USER_ID);
        applause = data.optInt(APPLAUSE);
        giftType = data.optInt(GIFT_TYPE);
        loginInfo = data.optInt(LOGIN_INFO);
        strength = data.optInt(STRENGTH);
        nickname = data.optString(NICKNAME);
        prefixPic = data.optString(PREFIX_PIC);
        viewNum = data.optString(VIEW_NUM);
        liveHits = data.optInt(LIVE_HITS);
    }

    // 数据打包
    @Override
    protected JSONObject packData() {
        return null;
    }


}
