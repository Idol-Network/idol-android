package com.yiju.ldol.bean.response;

import com.yiju.idol.api.BaseReslut;

import java.util.List;

public class PersoncurrencyDetailsResp extends BaseReslut {


    public String avatar;
    public String circulationNum;
    public String createTime;
    public int dateX;
    public String gains;
    public String issueNum;
    public String name;
    public String nowPriceStr;
    public int personCurrencyId;
    public int personId;
    public String priceStr;
    public boolean selectFlag;
    public String shortName;
    public String summary;
    public int userId;
    public List<SummaryListBean> summaryList;

    public static class SummaryListBean {

        public String summaryKey;
        public String summaryValue;
    }
}
