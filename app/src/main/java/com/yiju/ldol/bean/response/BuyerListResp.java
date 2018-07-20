package com.yiju.ldol.bean.response;

import com.yiju.idol.api.BaseReslut;
import com.yiju.idol.base.entity.IMultiItemType;

import java.io.Serializable;
import java.util.List;

/**
 * Created by d on 2018/6/28.
 */

public class BuyerListResp extends BaseReslut {

    public List<GoodsOrderListBean> goodsOrderList;

    public static class GoodsOrderListBean implements Serializable {

        public String address;//收货人地址
        public String amountStr;//订单总金额
        public String areaCode;//国家区号
        public long createTime;//订单生成时间
        public String createTimeStr;
        public String deliveryFeeStr;//运费
        public String deliveryNo;//快递单号
        public String goodsAmountStr;//商品金额
        public int needExpress;//是否需要物流 0-否 1-是
        public String orderNumber;//订单编号
        public int orderType;//1-普通商品 2-照片 3-视频
        public String phone;//收货人电话
        public String receiverName;//收货人名字
        public int sellerUserId;//卖家userID
        public int status;//订单状态 0-未支付，1-已支付，2-已发货，3-已签收，4-已完成，10-已取消，15-已失效
        public String statusStr;//订单状态
        public int userId;//用户id
        public String shortDescription;//订单商品描述
        public List<OrderSkuListBean> orderSkuList;
    }

    public static class OrderSkuListBean implements Serializable, IMultiItemType {
        public String commentNum;//评论数
        public String downloadNum;//下载数
        public int goodsId;//商品ID根据订单类型 ，可能为普通商品ID,也可能是照片ID,视频ID
        public String picUrl;//图片地址
        public String playUrl;//视频地址
        public String praiseNum;//点赞数
        public String shareNum;//分享数
        public String viewNum;//播放数
        public String priceStr;//单价
        public int quantity;//数量
        public String spec;//规格
        public String title;//商品名

        @Override
        public int getItemType() {
            return IMultiItemType.TYPE_PURCHASING_ORDER_ITEM;
        }
    }
}
