package com.yiju.ldol.bean.response;

import com.yiju.idol.api.BaseReslut;

import java.util.List;

public class GoodsDetailsResp extends BaseReslut {


    public String allStockNum;
    public BaseGoodsBean baseGoods;
    public String priceRange;

    public static class BaseGoodsBean {

        public int deliveryFee;
        public long goodsId;
        public String picUrl;
        public String title;
        public int type;
        public List<DetailsBean> details;
        public List<NormModelsBean> normModels;

        public static class DetailsBean {

            public String detail;
            public int orderNo;
            public int type;
        }

        public static class NormModelsBean {

            public String norm;
            public String price;
            public int skuId;
            public int stockNum;
        }
    }
}
