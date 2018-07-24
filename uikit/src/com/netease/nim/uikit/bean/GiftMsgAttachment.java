package com.netease.nim.uikit.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


/**
 * Created by zhanghengzhen on 2016/5/31.
 */
public class GiftMsgAttachment extends CustomAttachment implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String GIFT_TYPE = "gift_type";
    private static final String GIFT_NUM = "gift_num";
    private static final String USER_ID = "user_id";
    private static final String HEAD_URL = "head_url";
    private static final String NICKNAME = "nickname";
//    private static final String APPLAUSE = "applause";
    private static final String COIN_NUM = "coinNum";
    private static final String COIN_NUM_STR = "coinNumStr";
    private static final String STRENGTH = "strength";
    private static final String LIVE_ID = "live_id";
    private static final String PREFIX_PIC = "prefix_pic";
    private static final String GIFT_AMOUNT = "gift_amount";//连送礼物的数量
    private static final String VIEW_NUM = "view_num";//连送礼物的数量

//    public final static int TYPE_APPLAUSE = 1;
//    public final static int TYPE_LOVE = 2;
//    public final static int TYPE_CHEER = 3;
//    public final static int TYPE_KISS = 4;
//    public final static int TYPE_VOTE = 5;
//    public final static int TYPE_STAR = 6;
//    public final static int TYPE_ROSE = 7;
//    public final static int TYPE_LIGHT_STICK = 8;
//    public final static int TYPE_ICECREAM = 9;
//    public final static int TYPE_BANBANA = 10;
//    public final static int TYPE_KEY = 11;
//    public final static int TYPE_SAPPHIRE = 12;
//    public final static int TYPE_OSCAR = 13;
//    public final static int TYPE_CAKE = 14;
//    public final static int TYPE_CUPID = 15;
//    public final static int TYPE_TRANSFORMARS = 16;
//    public final static int TYPE_CANDY = 17;
//    public final static int TYPE_PUMPKIN = 18;
//    public final static int TYPE_WITCH = 19;
    // 自定义消息附件的类型，根据该字段区分不同的自定义消息

    private String userId;
    private String headUrl;
    private String nickname;
    private int liveid;
    private int giftType;
    private int giftNum;//次数
    private long coinNum;//币值
    private String coinNumStr;//转换好的币值字符串，用于展示
    private int strength;//进度条
    private String prefixPic;//暂时用于标识是否为场控
    private int giftAmount;//连送的数量
    private int viewNum;//用来显示的礼物数量

    @Override
    public String toJson(boolean b) {
        return CustomAttachParser.packData(customType, packData());
    }

    public GiftMsgAttachment() {
        super(CustomAttachmentType.TYPE_GIFT);
    }

    public int getLiveid() {
        return liveid;
    }

    public void setLiveid(int liveid) {
        this.liveid = liveid;
    }

    public int getGiftType() {
        return giftType;
    }

    public void setGiftType(int giftType) {
        this.giftType = giftType;
    }

    public int getGiftNum() {
        return giftNum;
    }

    public void setGiftNum(int giftNum) {
        this.giftNum = giftNum;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public long getCoinNum() {
        return coinNum;
    }

    public void setCoinNum(long coinNum) {
        this.coinNum = coinNum;
    }

    public void setCoinNumStr(String coinNumStr) {
        this.coinNumStr = coinNumStr;
    }

    public String getCoinNumStr() {
        return coinNumStr;
    }

    public void setPrefixPic(String prefixPic) {
        this.prefixPic = prefixPic;
    }

    public String getPrefixPic() {
        return prefixPic;
    }

    public int getGiftAmount() {
        return giftAmount;
    }

    public void setGiftAmount(int giftAmount) {
        this.giftAmount = giftAmount;
    }

    public int getViewNum() {
        return viewNum;
    }

    // 解析数据
    @Override
    protected void parseData(JSONObject data) {
        customType = data.optInt(CUSTOM_TYPE);//必须解析父类的该字段
        giftType = data.optInt(GIFT_TYPE);
        giftNum = data.optInt(GIFT_NUM);
        nickname = data.optString(NICKNAME);
        headUrl = data.optString(HEAD_URL);
        userId = data.optString(USER_ID);
        liveid = data.optInt(LIVE_ID);
        coinNum = data.optLong(COIN_NUM);
        coinNumStr = data.optString(COIN_NUM_STR);
        strength = data.optInt(STRENGTH);
        prefixPic = data.optString(PREFIX_PIC);
        giftAmount = data.optInt(GIFT_AMOUNT);
        viewNum = data.optInt(VIEW_NUM);
    }

    // 数据打包
    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        try {
            data.put(LIVE_ID, liveid);
            data.put(GIFT_TYPE, giftType);
            data.put(GIFT_NUM, giftNum);
            data.put(NICKNAME, nickname);
            data.put(USER_ID, userId);
            data.put(HEAD_URL, headUrl);
            data.put(COIN_NUM, coinNum);
            data.put(COIN_NUM_STR, coinNumStr);
            data.put(STRENGTH, strength);
            data.put(GIFT_AMOUNT, giftAmount);
            data.put(PREFIX_PIC, prefixPic);
            data.put(VIEW_NUM, viewNum);
//            data.put("version", 2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }


}
