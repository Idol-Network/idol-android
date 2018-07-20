package com.yiju.ldol.bean.response;

import com.yiju.idol.api.BaseReslut;

import java.io.Serializable;
import java.util.List;

/**
 * 深度
 * Created by zhanghengzhen on 2018/5/5.
 */

public class DepthResp extends BaseReslut {
    public String nowPrice;//实时价格
    public int type;//判断涨跌 0-涨 1-跌
    public List<DepthItemBean> buyDepthItems;//买入数据
    public List<DepthItemBean> sellDepthItems;//卖出数据

    public static class DepthItemBean implements Serializable {
        public int direction;//方向 1-买入 2-卖出
        public String tradeNumStr;
        public String price;//价格
        public String time;
        public long totalNum;//总数数量
        public String totalNumStr;//总数数量(字符串)
        public long tradeNum;//交易数量
    }
}
