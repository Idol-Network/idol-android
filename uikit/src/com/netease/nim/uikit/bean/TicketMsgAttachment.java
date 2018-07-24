package com.netease.nim.uikit.bean;

import org.json.JSONObject;

import java.io.Serializable;


/**
 * 门票
 * Created by zhanghengzhen on 2017/3/7.
 */
public class TicketMsgAttachment extends CustomAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String USER_ID = "user_id";
    private static final String APPLAUSE = "applause";//主播收到的总门票数
    private static final String STRENGTH = "strength";
    private static final String PREFIX_PIC = "prefix_pic";
    private static final String NICKNAME = "nickname";
    private static final String VIEW_NUM = "view_num";

    private int userId;
    private int applause;//门票数量
    private int strength;//主播剩余体力值
    private String nickname;
    private String prefixPic;//暂时用于标识是否为场控
    private String viewNum;//用于显示收入

    // 自定义消息附件的类型，根据该字段区分不同的自定义消息

    @Override
    public String toJson(boolean b) {
        return CustomAttachParser.packData(customType, packData());
    }

    public TicketMsgAttachment() {
        super(CustomAttachmentType.TYPE_TICKET);//消息类型
    }

    public int getUserId() {
        return userId;
    }

    public int getApplause() {
        return applause;
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

    // 解析数据
    @Override
    protected void parseData(JSONObject data) {
        customType = data.optInt(CUSTOM_TYPE);//必须解析父类的该字段
        userId = data.optInt(USER_ID);
        applause = data.optInt(APPLAUSE);
        strength = data.optInt(STRENGTH);
        nickname = data.optString(NICKNAME);
        prefixPic = data.optString(PREFIX_PIC);
        viewNum = data.optString(VIEW_NUM);
    }

    // 数据打包
    @Override
    protected JSONObject packData() {
        return null;
    }


}
