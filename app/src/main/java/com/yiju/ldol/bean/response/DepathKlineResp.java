package com.yiju.ldol.bean.response;

import com.yiju.idol.api.BaseReslut;

import java.util.List;

public class DepathKlineResp extends BaseReslut {

    public String nowPrice;
    public int type;
    public List<BuyDepthItemsBean> buyDepthItems;
    public List<SellDepthItemsBean> sellDepthItems;

    public static class BuyDepthItemsBean {

        public int direction;
        public String price;
        public String time;
        public long totalNum;
        public String totalNumStr;
        public long tradeNum;
        public String tradeNumStr;
    }

    public static class SellDepthItemsBean {

        public int direction;
        public String price;
        public String time;
        public long totalNum;
        public String totalNumStr;
        public long tradeNum;
        public String tradeNumStr;
    }
}
