package com.netease.nim.uikit.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


/**
 * Created by zhanghengzhen on 2016/6/30.
 */
public class WinMsgAttachment extends CustomAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String USER_ID = "user_id";
    private static final String LOTTERY = "lottery";
    private static final String NICKNAME = "nickname";
    private static final String PREFIX_PIC = "prefix_pic";

    // 自定义消息附件的类型，根据该字段区分不同的自定义消息

    private String userId;
    private boolean lottery;
    private String nickname;
    private String prefixPic;//暂时用于标识是否为场控

    @Override
    public String toJson(boolean b) {
        return CustomAttachParser.packData(customType, packData());
    }

    public WinMsgAttachment() {
        super(CustomAttachmentType.TYPE_WIN);//消息类型为中奖
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isLottery() {
        return lottery;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setLottery(boolean lottery) {
        this.lottery = lottery;
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
        lottery = data.optBoolean(LOTTERY);
        nickname = data.optString(NICKNAME);
        prefixPic = data.optString(PREFIX_PIC);
    }

    // 数据打包
    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        try {
            data.put(USER_ID, userId);
            data.put(LOTTERY, lottery);
            data.put(NICKNAME, nickname);
            data.put(PREFIX_PIC, prefixPic);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }


}
