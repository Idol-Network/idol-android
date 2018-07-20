package com.yiju.ldol.bean.response;

import com.yiju.idol.base.entity.IMultiItemType;

/**
 * Created by d on 2018/6/28.
 */

public class PurchasingOrderTitle implements IMultiItemType {

    private String orderNumber;//订单编号
    private long createTime;//订单生成时间
    private BuyerListResp.OrderSkuListBean skuBean;//视频/图片内容

    public PurchasingOrderTitle(String orderNumber, long createTime, BuyerListResp.OrderSkuListBean skuBean) {
        this.orderNumber = orderNumber;
        this.createTime = createTime;
        this.skuBean = skuBean;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public long getCreateTime() {
        return createTime;
    }

    public BuyerListResp.OrderSkuListBean getSkuBean() {
        return skuBean;
    }

    @Override
    public int getItemType() {
        return IMultiItemType.TYPE_PURCHASING_ORDER_NUM;
    }
}
