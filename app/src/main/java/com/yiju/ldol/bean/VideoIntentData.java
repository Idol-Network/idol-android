package com.yiju.ldol.bean;

import com.yiju.idol.base.entity.IMultiItemType;

import java.io.Serializable;

/**
 * Created by thbpc on 2018/3/28 0028.
 */

public class VideoIntentData implements IMultiItemType, Serializable {
    public int position;
    public String videoUrl;
    public String picUrl;
    public int videoId;
    public String summary;
    public String praiseNum;
    public String viewNum;
    public String shareNum;
    public int praise;
    public long fee;//是否收费
    public int paid;// 0-未付费 1-已付费

    @Override
    public int getItemType() {
        return IMultiItemType.TYPE_TV_VIDEO_TITLE;
    }
}
