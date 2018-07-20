package com.yiju.ldol.bean.response;

import com.yiju.idol.api.BaseReslut;

import java.util.List;

public class KLineResp extends BaseReslut {

    public List<List<Object>> klineData;

    /**
     * [
     * [
     * 1524896700000, 时间
     * 0.12416,       开盘
     * 0.15881,       最高
     * 0.00222,       最低
     * 0.061728,      收盘
     * 4416          成交量
     * ],
     */

}

