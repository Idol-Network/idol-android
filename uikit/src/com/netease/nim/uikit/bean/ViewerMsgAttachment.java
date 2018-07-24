package com.netease.nim.uikit.bean;

import org.json.JSONObject;

import java.io.Serializable;


/**
 * Created by zhanghengzhen on 2016/7/13.
 */
public class ViewerMsgAttachment extends CustomAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    public final static int TYPE_FIRST_ENTER = 20;//榜一
    public final static int TYPE_SECOND_ENTER = 21;//榜二
    public final static int TYPE_THIRD_ENTER = 22;//榜三

    private static final String USER_ID = "user_id";
    private static final String AVATER = "head_url";
    private static final String LIVE_ID = "live_id";
    private static final String NICKNAME = "nickname";
    private static final String LIVE_HITS = "liveHitsStr";
    private static final String DATE = "date";
    private static final String PREFIX_PIC = "prefix_pic";
    private static final String LOGIN_INFO = "login_info";
    private static final String GIFT_NUM = "gift_num";//用于标记是否刷新用户头像列表
    private static final String GIFT_TYPE = "gift_type";//用于标记排行榜前三名
    private static final String LEVEL = "level";//等级

    private int userId;
    private int liveId;
    private String nickname;
    private String avater;
    private String liveHits;
    private long date;
    private String prefixPic;
    private int loginInfo;//登录类型，为3时不显示此用户的进入信息，但显示头像；为2时不显示此用户的进入信息，也不显示头像；为1或者是不存在这个login_info时都显示（跟以前一样）
    private int giftNum;//为1的时候刷新列表，为0的时候不刷新，为2的时候只删除退出用户
    private int giftType;//对应贡献榜用户特效id
    private int level;

    @Override
    public String toJson(boolean b) {
        return CustomAttachParser.packData(customType, packData());
    }

    public ViewerMsgAttachment() {
        super(CustomAttachmentType.TYPE_MEMBER_IN);//消息类型
    }

    public int getLiveId() {
        return liveId;
    }

    public void setLiveId(int liveId) {
        this.liveId = liveId;
    }

    public int getUserId() {
        return userId;
    }

    public void setAvater(String avater) {
        this.avater = avater;
    }

    public String getAvater() {
        return avater;
    }

    public String getLiveHits() {
        return liveHits;
    }

    public long getDate() {
        return date;
    }

    public String getNickname() {
        return nickname;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPrefixPic() {
        return prefixPic;
    }

    public void setPrefixPic(String prefixPic) {
        this.prefixPic = prefixPic;
    }

    public int getLoginInfo() {
        return loginInfo;
    }

    public int getGiftNum() {
        return giftNum;
    }

    public int getGiftType() {
        return giftType;
    }

    public void setGiftType(int giftType) {
        this.giftType = giftType;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    // 解析数据
    @Override
    protected void parseData(JSONObject data) {
        customType = data.optInt(CUSTOM_TYPE);//必须解析父类的该字段
        userId = data.optInt(USER_ID);
        liveId = data.optInt(LIVE_ID);
        nickname = data.optString(NICKNAME);
        avater = data.optString(AVATER);
        liveHits = data.optString(LIVE_HITS);
        date = data.optLong(DATE);
        giftNum = data.optInt(GIFT_NUM);
        giftType = data.optInt(GIFT_TYPE);
        prefixPic = data.optString(PREFIX_PIC);
        level = data.optInt(LEVEL);
        loginInfo = data.optInt(LOGIN_INFO);
    }

    // 数据打包
    @Override
    protected JSONObject packData() {
        return null;
    }


}
