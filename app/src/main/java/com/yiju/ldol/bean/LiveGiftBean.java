package com.yiju.ldol.bean;

import java.io.Serializable;

/**
 * Created by Allan_Zhang on 2016/10/28.
 */
public class LiveGiftBean implements Serializable {
    public int bgMusic;//0-没有背景音乐 1-有背景音乐
    public int bigAnimation;//0-没有全屏飘落动画 1-有全屏飘落动画
    public String connectedDigit;//连送礼物数量 以,分隔
    public String connectedSpecial;//超过该礼物数量，改变礼物背景颜色 以,分隔
    public String endTime;//(string, optional): 结束时间 ,
    public int experience;//增加经验值
    public int giftId;//(integer, optional): 礼物ID ,
    public String identity;// (string, optional): 标示串 ,
    public int isStop;// (integer, optional): 是否停止使用 ,
    public String picUrl; //用于拼接url(string, optional): 礼物图片 ,
    public String playId;// (string, optional): 执行ID ,
    public long price;// (integer, optional): 礼物价格币 ,
    public String priceStr;//礼物价格币
    public String startTime;// (string, optional): 开始时间 ,
    public String title;// (string, optional): 礼物名 ,
    public int type;// (integer, optional): 礼物类型 0为连续礼物，1为大型礼物 ,2为榜单用户进入直播间
    public String zipUrl;// (string, optional): 下载地址
    public int position;
}
