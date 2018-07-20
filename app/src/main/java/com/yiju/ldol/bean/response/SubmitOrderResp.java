package com.yiju.ldol.bean.response;

import com.yiju.idol.api.BaseReslut;

/**
 * 提交订单
 */

public class SubmitOrderResp extends BaseReslut {
    public String orderNumber;//订单编号
    public String amountStr;//总金额
    public String shortDescription;//订单商品描述
}
