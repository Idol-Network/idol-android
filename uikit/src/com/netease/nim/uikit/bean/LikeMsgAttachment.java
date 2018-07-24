package com.netease.nim.uikit.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


/**
 * 点赞消息实体类
 * Created by mushroo,m on 2016/7/13.
 */
public class LikeMsgAttachment extends CustomAttachment implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String GIFT_TYPE = "gift_type";
    private static final String USER_ID = "user_id";
    //    private static final String AVATER = "head_url";
    private static final String LIVE_ID = "live_id";
    //    private static final String LIVE_HITS = "liveHitsStr";
    private static final String DATE = "date";
    private static final String ZAN_NUM = "gift_num";
    private static final String NICKNAME = "nickname";
    private static final String PREFIX_PIC = "prefix_pic";
    private static final String ZAN_INDEX = "zan_index";
    private int userId;
    private int liveId;
    private int zanIndex;
    private String nickName;
    private String prefixPic;//暂时用于标识是否为场控

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPrefixPic() {
        return prefixPic;
    }

    public void setPrefixPic(String prefixPic) {
        this.prefixPic = prefixPic;
    }

    public int getZanIndex() {
        return zanIndex;
    }

    public void setZanIndex(int zanIndex) {
        this.zanIndex = zanIndex;
    }

    //    private String avater;
//    private String liveHits;
    private long date;
    private int gift_num;

    public int getGift_type() {
        return gift_type;
    }

    public void setGift_type(int gift_type) {
        this.gift_type = gift_type;
    }

    private int gift_type;

    @Override
    public String toJson(boolean b) {
        return CustomAttachParser.packData(customType, packData());
    }

    public LikeMsgAttachment() {
        super(CustomAttachmentType.TYPE_LIKE);//消息类型
    }

    public int getLiveId() {
        return liveId;
    }

    public void setLiveId(int liveId) {
        this.liveId = liveId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setGift_num(int zanNum){
        this.gift_num = zanNum;
    }

    public int getUserId() {
        return userId;
    }
    public int getGift_num() {
        return gift_num;
    }

//    public String getAvater() {
//        return avater;
//    }
//
//    public String getLiveHits() {
//        return liveHits;
//    }

    public long getDate() {
        return date;
    }

    // 解析数据
    @Override
    protected void parseData(JSONObject data) {
            customType = data.optInt(CUSTOM_TYPE);//必须解析父类的该字段
            userId = data.optInt(USER_ID);
            liveId = data.optInt(LIVE_ID);
            gift_num = data.optInt(ZAN_NUM);
            gift_type = data.optInt(GIFT_TYPE);
            nickName = data.optString(NICKNAME);
            prefixPic = data.optString(PREFIX_PIC);
            zanIndex = data.optInt(ZAN_INDEX);
//            avater = data.optString(AVATER);
//            liveHits = data.optString(LIVE_HITS);
//            date = data.optLong(DATE);
    }

    // 数据打包
    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        try {
            data.put(USER_ID, userId);
            data.put(LIVE_ID, liveId);
            data.put(ZAN_NUM,gift_num);
            data.put(GIFT_TYPE, gift_type);
            data.put(NICKNAME,nickName);
            data.put(PREFIX_PIC, prefixPic);
            data.put(ZAN_INDEX,zanIndex);
            data.put("version", 1);
//            data.put(date, da);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

}
