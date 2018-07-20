package com.yiju.ldol.bean.response;

import com.yiju.idol.api.BaseReslut;

import java.io.Serializable;
import java.util.List;

/**
 * 立即购买
 */

public class SettlementResp extends BaseReslut {

    public String amountStr;//总金额
    public int deliveryFee;//运费 单位：分
    public String deliveryFeeStr;//运费
    public int needAddress;//是否需要收货地址 0-否 1-是
    public String orderNumber;//若为图片、视频，则有该值，用于支付
    public PersonShortBean personShort;//影人信息
    public List<CartListBean> cartList;//商品列表
    public String shortDescription;//订单商品描述

    public static class PersonShortBean implements Serializable {
        public String avatar;//头像
        public String name;//影人名称
        public int personId;//影人编号
        public int userId;//影人用户ID
    }

    public static class CartListBean implements Serializable {
        public int goodsId;
        public String id;//购物车Id
        public String picUrl;//图片
        public String playUrl;//视频
        public String priceStr;//单价
        public int quantity;//数量
        public String spec;//规格
        public String title;//商品名称
        public int type;//类型 1-普通商品 2-照片 3-视频
    }
}
