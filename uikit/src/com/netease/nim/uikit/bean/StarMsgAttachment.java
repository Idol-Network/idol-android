package com.netease.nim.uikit.bean;

import org.json.JSONObject;

import java.io.Serializable;


/**
 * 星星
 * Created by zhanghengzhen on 2017/3/7.
 */
public class StarMsgAttachment extends CustomAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String USER_ID = "user_id";
    private static final String APPLAUSE = "applause";

    private int userId;//送星星的用户
    private int applause;//主播总共增加的直播时间（秒）

    // 自定义消息附件的类型，根据该字段区分不同的自定义消息

    @Override
    public String toJson(boolean b) {
        return CustomAttachParser.packData(customType, packData());
    }

    public StarMsgAttachment() {
        super(CustomAttachmentType.TYPE_STAR);//消息类型
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getApplause() {
        return applause;
    }

    // 解析数据
    @Override
    protected void parseData(JSONObject data) {
        customType = data.optInt(CUSTOM_TYPE);//必须解析父类的该字段
        userId = data.optInt(USER_ID);
        applause = data.optInt(APPLAUSE);
    }

    // 数据打包
    @Override
    protected JSONObject packData() {
        return null;
    }


}
