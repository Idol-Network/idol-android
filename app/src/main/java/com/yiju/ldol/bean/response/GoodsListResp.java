package com.yiju.ldol.bean.response;

import com.yiju.idol.api.BaseReslut;

import java.io.Serializable;
import java.util.List;

public class GoodsListResp extends BaseReslut {

    public List<GoodsItemsBean> goodsItems;

    public static class GoodsItemsBean implements Serializable {

        public long goodsId;
        public String picUrl;
        public String price;
        public String title;
    }

}
