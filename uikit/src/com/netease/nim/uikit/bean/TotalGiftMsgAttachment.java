package com.netease.nim.uikit.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


/**
 * Created by zhanghengzhen on 2016/6/30.
 */
public class TotalGiftMsgAttachment extends CustomAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String USER_ID = "user_id";
    private static final String NICKNAME = "nickname";
    private static final String GIFT_TYPE = "gift_type";
    private static final String GIFT_NUM = "gift_num";
    private static final String LIVE_ID = "live_id";
    private static final String PREFIX_PIC = "prefix_pic";

    // 自定义消息附件的类型，根据该字段区分不同的自定义消息

    private String userId;
    private String nickname;
    private String prefixPic;//暂时用于标识是否为场控
    private int giftType;
    private int giftNum;
    private int liveId;

    @Override
    public String toJson(boolean b) {
        return CustomAttachParser.packData(customType, packData());
    }

    public TotalGiftMsgAttachment() {
        super(CustomAttachmentType.TYPE_TOTAL_GIFT);//消息类型为总礼物数
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setLiveId(int liveId) {
        this.liveId = liveId;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setGiftType(int giftType) {
        this.giftType = giftType;
    }

    public void setGiftNum(int giftNum) {
        this.giftNum = giftNum;
    }

    public int getLiveId() {
        return liveId;
    }

    public String getUserId() {
        return userId;
    }

    public String getNickname() {
        return nickname;
    }

    public int getGiftType() {
        return giftType;
    }

    public int getGiftNum() {
        return giftNum;
    }

    public String getPrefixPic() {
        return prefixPic;
    }

    public void setPrefixPic(String prefixPic) {
        this.prefixPic = prefixPic;
    }

    // 解析数据
    @Override
    protected void parseData(JSONObject data) {
        customType = data.optInt(CUSTOM_TYPE);//必须解析父类的该字段
        userId = data.optString(USER_ID);
        liveId = data.optInt(LIVE_ID);
        giftType = data.optInt(GIFT_TYPE);
        giftNum = data.optInt(GIFT_NUM);
        nickname = data.optString(NICKNAME);
        prefixPic = data.optString(PREFIX_PIC);
    }

    // 数据打包
    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        try {
            data.put(LIVE_ID, liveId);
            data.put(USER_ID, userId);
            data.put(GIFT_TYPE, giftType);
            data.put(GIFT_NUM, giftNum);
            data.put(NICKNAME, nickname);
            data.put(PREFIX_PIC, prefixPic);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }


}
