package com.netease.nim.uikit.bean;

import org.json.JSONObject;

import java.io.Serializable;


/**
 * 由服务器发送的只含有custom_type属性的自定义消息
 * Created by zhanghengzhen on 2016/7/5.
 */
public class CustomMsgAttachment extends CustomAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    // 自定义消息附件的类型，根据该字段区分不同的自定义消息

    @Override
    public String toJson(boolean b) {
        return CustomAttachParser.packData(customType, packData());
    }

    public CustomMsgAttachment() {
        super(CustomAttachmentType.TYPE_LIVE_END);//消息类型
    }

    // 解析数据
    @Override
    protected void parseData(JSONObject data) {
        customType = data.optInt(CUSTOM_TYPE);//必须解析父类的该字段
    }

    // 数据打包
    @Override
    protected JSONObject packData() {
        return null;
    }


}
