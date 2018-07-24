package com.netease.nim.uikit.bean;

import org.json.JSONObject;

import java.io.Serializable;


/**
 * Created by zhanghengzhen on 2016/7/4.
 */
public class PrepareWinMsgAttachment extends CustomAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    // 自定义消息附件的类型，根据该字段区分不同的自定义消息

    @Override
    public String toJson(boolean b) {
        return CustomAttachParser.packData(customType, packData());
    }

    public PrepareWinMsgAttachment() {
        super(CustomAttachmentType.TYPE_PREPARE_WIN);//消息类型为中奖
    }

    // 解析数据
    @Override
    protected void parseData(JSONObject data) {
        customType = data.optInt(CUSTOM_TYPE);//必须解析父类的该字段
    }

    // 数据打包
    @Override
    protected JSONObject packData() {
        return new JSONObject();
    }


}
