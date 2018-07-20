package com.yiju.ldol.bean.response;

import java.io.Serializable;

/**
 * 收货地址
 */

public class AddressItemBean implements Serializable {
    public String areaCode;//国家区号
    public String address;//地址
    public int addressId;//收货地址ID
    public int cityId;//城市ID
    public String completeAddress;//完整地址
    public int countryId;//国家ID
    public int def;//是否默认 0 -否 1-是
    public String name;//收件人
    public String phone;//联系电话
    public int provinceId;//省份ID
    public int streetId;//县(区)ID
    public String countryStr;//国家
    public String provinceStr;//省份
    public String cityStr;//城市
    public String streetStr;//县(区)

    public int checkedCountry;
    public int checkedProvince;
    public int checkedCity;
    public int checkedStreet;
}
