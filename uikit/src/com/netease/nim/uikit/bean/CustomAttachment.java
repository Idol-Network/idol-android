package com.netease.nim.uikit.bean;

import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;

import org.json.JSONObject;

// 先定义一个自定义消息附件的基类，负责解析你的自定义消息的公用字段，比如类型等等。
public abstract class CustomAttachment implements MsgAttachment {

    public static final String CUSTOM_TYPE = "custom_type";

    // 自定义消息附件的类型，根据该字段区分不同的自定义消息
    protected int customType;

    CustomAttachment(int type) {
        this.customType = type;
    }

    // 解析附件内容。
    public void fromJson(JSONObject data) {
        if (data != null) {
            parseData(data);
        }
    }

    /**
     * 消息附件类型
     */
    public int getCustomType() {
        return customType;
    }

    // 实现 MsgAttachment 的接口，封装公用字段，然后调用子类的封装函数。
    @Override
    public String toJson(boolean send) {
        return CustomAttachParser.packData(customType, packData());
    }

    // 子类的解析和封装接口。
    protected abstract void parseData(JSONObject data);

    protected abstract JSONObject packData();
}