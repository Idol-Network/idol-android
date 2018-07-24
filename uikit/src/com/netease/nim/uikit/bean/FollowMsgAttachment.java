package com.netease.nim.uikit.bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


/**
 * Created by zhanghengzhen 2016/7/13.
 */
public class FollowMsgAttachment extends CustomAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String USER_ID = "user_id";
    private static final String LIVE_ID = "live_id";
    private static final String NICKNAME = "nickname";
    private static final String PREFIX_PIC = "prefix_pic";

    private int userId;
    private int liveId;
    private String nickname;
    private String prefixPic;//暂时用于标识是否为场控

    @Override
    public String toJson(boolean b) {
        return CustomAttachParser.packData(customType, packData());
    }

    public FollowMsgAttachment() {
        super(CustomAttachmentType.TYPE_FOLLOW);//消息类型
    }

    public void setLiveId(int liveId) {
        this.liveId = liveId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
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

    // 解析数据
    @Override
    protected void parseData(JSONObject data) {
        customType = data.optInt(CUSTOM_TYPE);//必须解析父类的该字段
        userId = data.optInt(USER_ID);
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
            data.put(NICKNAME, nickname);
            data.put(PREFIX_PIC, prefixPic);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }

}
