package com.yiju.ldol.bean.response;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhanghengzhen on 2018/4/28.
 */

public class PersonCurrencyBean implements Serializable {
    public String name;//影人名称
    public boolean selectFlag;//当前登录用户是否将此明星币加入自选
    public String avatar;//影人头像
    public int dealDecimals;//交易数量小数点后的位数
    public int priceDecimals;//代币价格小数点后的位数
    public String gains;//涨幅
    public String nowPriceStr;//当前价格 ,
    public int personCurrencyId;//明星代币ID
    public int personId;//影人ID ,
    public int userId;//影人的userId
    public String circulationNum;//流通总量
    public String createTime;//发行时间
    public String issueNum;//发行总量
    public String priceStr;//众筹价格
    public String shortName;//代币名称
    public String summary;//简介
    public List<SummaryBean> summaryList;//简介详情key - value形式(包括:发行时间，发行总量，流通总量，众筹价格)

    public static class SummaryBean implements Serializable {
        public String summaryKey;//简介key
        public String summaryValue;//简介value
    }
}
