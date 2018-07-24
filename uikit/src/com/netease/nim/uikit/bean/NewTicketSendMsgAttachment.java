package com.netease.nim.uikit.bean;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;


/**
 * 新的门票/鸡蛋消息 只用于发送
 * Created by zhanghengzhen on 2017/4/18.
 */
public class NewTicketSendMsgAttachment extends CustomAttachment implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String USER_ID = "user_id";
    private static final String LIVE_ID = "live_id";
    private static final String GIFT_NUM = "gift_num";

    private int userId;//送星星的用户
    private int liveId;
    private int giftNum;//连送数量 默认是0

    // 自定义消息附件的类型，根据该字段区分不同的自定义消息

    public NewTicketSendMsgAttachment(int type) {
        super(type);//消息类型
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setLiveId(int liveId) {
        this.liveId = liveId;
    }

    public void setGiftNum(int giftNum) {
        this.giftNum = giftNum;
    }

    // 解析数据
    @Override
    protected void parseData(JSONObject data) {
        customType = data.optInt(CUSTOM_TYPE);//必须解析父类的该字段
        userId = data.optInt(USER_ID);
        liveId = data.optInt(LIVE_ID);
        giftNum = data.optInt(GIFT_NUM);
    }

    // 数据打包
    @Override
    protected JSONObject packData() {
        JSONObject data = new JSONObject();
        try {
            data.put(USER_ID, userId);
            data.put(LIVE_ID, liveId);
            data.put(GIFT_NUM, giftNum);
            data.put("version", 2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return data;
    }


}
