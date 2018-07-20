package com.yiju.ldol.bean.response;


import com.yiju.idol.api.BaseReslut;

import java.io.Serializable;

/**
 * Created by zhanghengzhen on 2016/6/27.
 */
public class ApplauseResp extends BaseReslut {

    public UserGiftBean userGift;

    public static class UserGiftBean implements Serializable {
        public String affairTime;//当前事务时间
        public long coinNum;//用户的币余额
        public String coinNumStr;//用户的币余额字符串
        public int egg;//用户鸡蛋数量
        public int liveSendRecordId;//送礼记录编号
        public int liveUserId;//主播用户id
        public boolean paymentOk;//是否支付成功。false的时候是余额不足
        public long liveCoinNum;//主播币数
        public String liveCoinNumStr;//主播币数字符串
        public int giftId;//礼物ID
        public int giftNum;//送的数量
        public int num;//数量【最大数量】
        public int roomId;//房间id
    }
}
