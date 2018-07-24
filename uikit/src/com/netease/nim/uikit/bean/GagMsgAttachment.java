package com.netease.nim.uikit.bean;

import org.json.JSONObject;

import java.io.Serializable;


/**
 * 由服务器发送的禁言/解禁自定义消息
 * Created by zhanghengzhen on 2016/7/5.
 */
public class GagMsgAttachment extends CustomAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String USER_ID = "user_id";

    private int userId;

    // 自定义消息附件的类型，根据该字段区分不同的自定义消息

    @Override
    public String toJson(boolean b) {
        return CustomAttachParser.packData(customType, packData());
    }

    public GagMsgAttachment() {
        super(CustomAttachmentType.TYPE_GAG);//消息类型
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // 解析数据
    @Override
    protected void parseData(JSONObject data) {
        customType = data.optInt(CUSTOM_TYPE);//必须解析父类的该字段
        userId = data.optInt(USER_ID);
    }

    // 数据打包
    @Override
    protected JSONObject packData() {
        return null;
    }


}
