package com.yiju.ldol.bean;

import java.io.Serializable;

/**
 * Created by Allan_Zhang on 2018/5/7.
 */

public class SimpleDelegateBean implements Serializable {
    private int personCurrencyId;
    private int personId;
    private String personName;
    private String shortName;//币简称
    private String nowPriceStr;//当前价格字符串
    private int dealDecimals;//交易数量小数点后的位数
    private int priceDecimals;//代币价格小数点后的位数

    private SimpleDelegateBean(Builder builder) {
        this.personCurrencyId = builder.personCurrencyId;
        this.personId = builder.personId;
        this.personName = builder.personName;
        this.shortName = builder.shortName;
        this.nowPriceStr = builder.nowPriceStr;
        this.dealDecimals = builder.dealDecimals;
        this.priceDecimals = builder.priceDecimals;
    }

    public int getPersonCurrencyId() {
        return personCurrencyId;
    }

    public int getPersonId() {
        return personId;
    }

    public String getPersonName() {
        return personName;
    }

    public String getShortName() {
        return shortName;
    }

    public void setNowPriceStr(String nowPriceStr) {
        this.nowPriceStr = nowPriceStr;
    }

    public String getNowPriceStr() {
        return nowPriceStr;
    }

    public int getDealDecimals() {
        return dealDecimals;
    }

    public int getPriceDecimals() {
        return priceDecimals;
    }

    public static class Builder {

        private int personCurrencyId;
        private int personId;
        private String personName;
        private String shortName;//币简称
        private String nowPriceStr;//当前价格字符串
        private int dealDecimals;//交易数量小数点后的位数
        private int priceDecimals;//代币价格小数点后的位数

        public SimpleDelegateBean create() {
            return new SimpleDelegateBean(this);
        }

        public Builder setPersonCurrencyId(int personCurrencyId) {
            this.personCurrencyId = personCurrencyId;
            return this;
        }

        public Builder setPersonId(int personId) {
            this.personId = personId;
            return this;
        }

        public Builder setPersonName(String personName) {
            this.personName = personName;
            return this;
        }

        public Builder setShortName(String shortName) {
            this.shortName = shortName;
            return this;
        }

        public Builder setNowPriceStr(String nowPriceStr) {
            this.nowPriceStr = nowPriceStr;
            return this;
        }

        public Builder setDealDecimals(int dealDecimals) {
            this.dealDecimals = dealDecimals;
            return this;
        }

        public Builder setPriceDecimals(int priceDecimals) {
            this.priceDecimals = priceDecimals;
            return this;
        }
    }
}
