package com.yiju.ldol.bean;

import java.io.Serializable;

/**
 * Created by thbpc on 2018/3/28 0028.
 */

public class DetailsReturnData implements Serializable {
    public int position;
    public int praise;//0取消点赞 1点赞
    public String praiseNum;//0取消点赞 1点赞
    public String commentNum;//评论数
    public String shareNum;//分享数
    public int paid;// 0-未付费 1-已付费
}
