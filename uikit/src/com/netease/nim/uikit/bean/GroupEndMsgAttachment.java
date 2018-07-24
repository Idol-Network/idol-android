package com.netease.nim.uikit.bean;

import org.json.JSONObject;

import java.io.Serializable;


/**
 * 组直播结束
 * Created by zhanghengzhen on 2017/3/7.
 */
public class GroupEndMsgAttachment extends CustomAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String LOGIN_INFO = "login_info";
    private static final String HEAD_URL = "head_url";
    private static final String VIEW_NUM = "view_num";
    private static final String APPLAUSE = "applause";
    private static final String STRENGTH = "strength";

    private int loginInfo;//0：无红包 1：有红包
    private String headUrl;//头像
    private String viewNum;//收入
    private String applause;//掌声
    private String strength;//直播时长

    // 自定义消息附件的类型，根据该字段区分不同的自定义消息

    @Override
    public String toJson(boolean b) {
        return CustomAttachParser.packData(customType, packData());
    }

    public GroupEndMsgAttachment() {
        super(CustomAttachmentType.TYPE_GROUP_END);//消息类型
    }

    public int getLoginInfo() {
        return loginInfo;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public String getViewNum() {
        return viewNum;
    }

    public String getApplause() {
        return applause;
    }

    public String getStrength() {
        return strength;
    }

    // 解析数据
    @Override
    protected void parseData(JSONObject data) {
        customType = data.optInt(CUSTOM_TYPE);//必须解析父类的该字段
        loginInfo = data.optInt(LOGIN_INFO);
        headUrl = data.optString(HEAD_URL);
        viewNum = data.optString(VIEW_NUM);
        applause = data.optString(APPLAUSE);
        strength = data.optString(STRENGTH);
    }

    // 数据打包
    @Override
    protected JSONObject packData() {
        return null;
    }


}
