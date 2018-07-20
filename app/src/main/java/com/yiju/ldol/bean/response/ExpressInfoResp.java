package com.yiju.ldol.bean.response;

import com.yiju.idol.api.BaseReslut;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhanghengzhen on 2017/6/17.
 */

public class ExpressInfoResp extends BaseReslut {

    public String deliveryNo;
    public String deliveryStr;
    public String deliveryTelphone;
    public int goodsNum;
    public String logisticsStr;
    public int orderId;
    public String pic;
    public List<DataBean> data;

    public static class DataBean implements Serializable {
        public String context;
        public String time;
        public long deliveryTime;
    }
}
