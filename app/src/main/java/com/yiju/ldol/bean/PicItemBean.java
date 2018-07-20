package com.yiju.ldol.bean;

import java.io.Serializable;

/**
 * Created by Allan_Zhang on 2018/3/30.
 */

public class PicItemBean implements Serializable {

    public String bigPicUrl;//图片大图
    public String commentNum;//评论数
    public long fee;//收费 单位:idol
    public int height;//高
    public String highPicUrl;//高清图片URL
    public int picId;
    public String picUrl;//图片URL
    public int praise;//当前用户是否已点赞(0-否 1-是)
    public String praiseNum;//点赞数
    public int width;//宽
    public int paid;//是否已购买 0-未付费 1-已付费
    public String realPicUrl;//已购买图片真实地址
}