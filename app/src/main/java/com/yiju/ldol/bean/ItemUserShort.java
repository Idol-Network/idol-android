package com.yiju.ldol.bean;

/**
 * Created by Allan_Zhang on 2016/7/13.
 */
public class ItemUserShort {

    public ItemUserShort(int userId, String avater, int level) {
        this.avater = avater;
        this.userId = userId;
        this.level = level;
    }

    public String avater;
    public int userId;
    public int level;
}
