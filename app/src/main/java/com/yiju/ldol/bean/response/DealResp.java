package com.yiju.ldol.bean.response;

import com.yiju.idol.api.BaseReslut;

import java.util.List;

public class DealResp extends BaseReslut {

    public List<DealItemsBean> dealItems;

    public static class DealItemsBean {

        public int direction;
        public String num;
        public String price;
        public String time;
    }
}
