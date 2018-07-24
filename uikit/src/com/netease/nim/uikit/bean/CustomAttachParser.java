package com.netease.nim.uikit.bean;

import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachmentParser;

import org.json.JSONException;
import org.json.JSONObject;

public class CustomAttachParser implements MsgAttachmentParser {

    private static final String CUSTOM_DATA = "custom_data";

    // 根据解析到的消息类型，确定附件对象类型
    @Override
    public MsgAttachment parse(String json) {
        CustomAttachment attachment = null;
        try {
            JSONObject object = new JSONObject(json);
            JSONObject data = object.getJSONObject(CUSTOM_DATA);
            int type = data.getInt(CustomAttachment.CUSTOM_TYPE);
            switch (type) {
                case CustomAttachmentType.TYPE_GIFT://礼物
                    attachment = new GiftMsgAttachment();
                    break;
                case CustomAttachmentType.TYPE_PREPARE_WIN://开始抽奖
                    attachment = new PrepareWinMsgAttachment();
                    break;
                case CustomAttachmentType.TYPE_WIN://抽奖结果
                    attachment = new WinMsgAttachment();
                    break;
                case CustomAttachmentType.TYPE_GAG://禁言
                case CustomAttachmentType.TYPE_RELIVE_GAG://解除禁言
                case CustomAttachmentType.TYPE_KICK://被踢
                    attachment = new GagMsgAttachment();
                    break;
                case CustomAttachmentType.TYPE_LIVE_END://直播结束
                case CustomAttachmentType.TYPE_PAUSE_LIVE://暂停直播
                case CustomAttachmentType.TYPE_RESUME_LIVE://恢复直播
                case CustomAttachmentType.TYPE_ADVANCE_LIVE://提前直播
                case CustomAttachmentType.TYPE_FORCE_EXIT://强制退出
                case CustomAttachmentType.TYPE_PREVIEW_START://预告转变为直播
                case CustomAttachmentType.TYPE_GROUP_CONTINUE://继续组直播
                case CustomAttachmentType.TYPE_STAR_NUM_UP://星星数+1
                case CustomAttachmentType.TYPE_GET_EGG://鸡蛋数+1
                case CustomAttachmentType.TYPE_GET_TICKET://门票数+1
                case CustomAttachmentType.TYPE_EGG_RED_POCKET://鸡蛋红包
                    attachment = new CustomMsgAttachment();
                    break;
                case CustomAttachmentType.TYPE_MEMBER_IN://用户进入
                case CustomAttachmentType.TYPE_MEMBER_EXIT://用户离开
                    attachment = new ViewerMsgAttachment();
                    break;
                case CustomAttachmentType.TYPE_TOTAL_GIFT://总礼物数
                    attachment = new TotalGiftMsgAttachment();
                    break;
                case CustomAttachmentType.TYPE_LIKE: //点赞消息
                    attachment = new LikeMsgAttachment();
                    break;
                case CustomAttachmentType.TYPE_FOLLOW://关注主播
                    attachment = new FollowMsgAttachment();
                    break;
                case CustomAttachmentType.TYPE_SHARE://分享直播
                    attachment = new ShareMsgAttachment();
                    break;
                case CustomAttachmentType.TYPE_ADD_ADMIN://添加管理员
                case CustomAttachmentType.TYPE_DEL_ADMIN://移除管理员
                    attachment = new AdminMsgAttachment();
                    break;
                case CustomAttachmentType.TYPE_STAR://收到星星
                    attachment = new StarMsgAttachment();
                    break;
                case CustomAttachmentType.TYPE_EGG://收到鸡蛋
                    attachment = new EggMsgAttachment();
                    break;
                case CustomAttachmentType.TYPE_TICKET://收到门票
                    attachment = new TicketMsgAttachment();
                    break;
                case CustomAttachmentType.TYPE_GROUP_END://组直播结束
                    attachment = new GroupEndMsgAttachment();
                    break;
                case CustomAttachmentType.TYPE_GOODS_URL://商品链接消息
                    attachment = new GoodsUrlMsgAttachment();
                    break;
                case CustomAttachmentType.TYPE_GOODS_URL_TIP://自定义的发送宝贝链接的消息 只在咨询时对买家自己展示
                    attachment = new GoodsTipMsgAttachment();
                    break;
                case CustomAttachmentType.TYPE_ORDER://订单消息
                    attachment = new OrderMsgAttachment();
                    break;
                default:
                    break;
            }

            if (attachment != null) {
                attachment.fromJson(data);
            }
        } catch (Exception e) {
            LogUtil.e("CustomAttachParser",e.getMessage());
        }

        return attachment;
    }

    public static String packData(int type, JSONObject data) {
        JSONObject object = new JSONObject();
        if (data != null) {
            try {
                data.put(CustomAttachment.CUSTOM_TYPE, type);
                object.put(CUSTOM_DATA, data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return object.toString();
    }
}