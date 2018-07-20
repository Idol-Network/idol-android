package com.yiju.ldol.bean.response;

import com.yiju.idol.base.entity.IMultiItemType;

/**
 * Created by Allan_Zhang on 2018/6/28.
 */

public class PurchasingOrderFoot implements IMultiItemType {

    private int status;//订单状态 0-未支付，1-已支付，2-已发货，3-已签收，4-已完成，10-已取消，15-已失效(未支付、已发货只显示确认按钮；已支付、已取消、已失效隐藏按钮一栏；有快递单号时显示查看物流按钮)
    private String statusStr;//订单状态
    private String orderNumber;//订单编号
    private String amountStr;//订单总金额
    private String deliveryFeeStr;//运费
    private String shortDescription;//订单商品描述
    public String deliveryNo;//快递单号

    public PurchasingOrderFoot(int status, String statusStr, String orderNumber, String amountStr, String deliveryFeeStr, String shortDescription, String deliveryNo) {
        this.status = status;
        this.statusStr = statusStr;
        this.orderNumber = orderNumber;
        this.amountStr = amountStr;
        this.deliveryFeeStr = deliveryFeeStr;
        this.shortDescription = shortDescription;
        this.deliveryNo = deliveryNo;
    }

    public int getStatus() {
        return status;
    }

    public String getStatusStr() {
        return statusStr;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getAmountStr() {
        return amountStr;
    }

    public String getDeliveryFeeStr() {
        return deliveryFeeStr;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getDeliveryNo() {
        return deliveryNo;
    }

    @Override
    public int getItemType() {
        return IMultiItemType.TYPE_PURCHASING_ORDER_TOTAL;
    }
}
