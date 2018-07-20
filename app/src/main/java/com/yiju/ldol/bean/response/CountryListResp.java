package com.yiju.ldol.bean.response;

import com.yiju.idol.api.BaseReslut;

import java.io.Serializable;
import java.util.List;

/**
 * Created by thbpc on 2018/3/24 0024.
 */

public class CountryListResp extends BaseReslut {

    public List<CountryListBean> countryList;

    public static class CountryListBean implements Serializable{
        public String areaCode;
        public String currentName;
        public String firstLetter;
        public String name;
    }
}
