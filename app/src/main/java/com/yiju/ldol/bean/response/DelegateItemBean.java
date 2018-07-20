package com.yiju.ldol.bean.response;

import java.io.Serializable;

/**
 * 当前交易bean
 * Created by zhanghengzhen on 2018/5/4.
 */

public class DelegateItemBean implements Serializable {
    public int direction;//方向 1-买入 2-卖出
    public String num;//数量
    public int orderId;//订单Id
    public String price;//价格
    public int status;//状态 1-交易中 2-交易完成(不可撤销)
    public String time;//时间
    public String tradeNum;//成交量
}
