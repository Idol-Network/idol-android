package com.netease.nim.uikit.bean;

import org.json.JSONObject;

import java.io.Serializable;


/**
 * 由服务器发送的添加/移除管理员自定义消息
 * Created by Allan_Zhang on 2016/9/21.
 */
public class AdminMsgAttachment extends CustomAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String USER_ID = "user_id";
    private static final String LIVE_ID = "live_id";
    private static final String PREFIX_PIC = "prefix_pic";

    private int userId;
    private int liveId;
    private String prefixPic;

    // 自定义消息附件的类型，根据该字段区分不同的自定义消息

    @Override
    public String toJson(boolean b) {
        return CustomAttachParser.packData(customType, packData());
    }

    public AdminMsgAttachment() {
        super(CustomAttachmentType.TYPE_ADD_ADMIN);//消息类型
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getLiveId() {
        return liveId;
    }

    public String getPrefixPic() {
        return prefixPic;
    }

    // 解析数据
    @Override
    protected void parseData(JSONObject data) {
        /**JSONObject.optXXX方法会在对应的key中的值不存在的时候返回默认值，而JSONObject.getXXX方法会抛出异常*/
        customType = data.optInt(CUSTOM_TYPE);//必须解析父类的该字段
        userId = data.optInt(USER_ID);
        liveId = data.optInt(LIVE_ID);
        prefixPic = data.optString(PREFIX_PIC);
    }

    // 数据打包
    @Override
    protected JSONObject packData() {
        return null;
    }


}
