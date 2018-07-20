package com.yiju.ldol.bean;

public class DateBean {
    private int[] solar;//阳历年、月、日
    private int type;//0:上月，1:当月，2:下月
    private boolean isToday;//是否为今天
    private boolean signed;//是否签到

    public int[] getSolar() {
        return solar;
    }

    public void setIsToday(boolean isToday) {
        this.isToday = isToday;
    }

    public boolean isToday() {
        return isToday;
    }

    public boolean isSigned() {
        return signed;
    }

    public void setSigned(boolean signed) {
        this.signed = signed;
    }

    public void setSolar(int year, int month, int day) {
        this.solar = new int[]{year, month, day};
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DateBean) {
            DateBean dateNext = (DateBean) obj;
            int[] solar = dateNext.getSolar();
            if (solar != null && this.solar[0] == solar[0] && this.solar[1] == solar[1] && this.solar[2] == solar[2]) {
                return true;
            }
        }
        return false;
    }
}
