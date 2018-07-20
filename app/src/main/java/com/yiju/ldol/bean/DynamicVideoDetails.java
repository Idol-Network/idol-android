package com.yiju.ldol.bean;

import com.yiju.idol.base.entity.IMultiItemType;

import java.io.Serializable;

/**
 * Created by thbpc on 2018/3/30 0030.
 */

public class DynamicVideoDetails implements Serializable, IMultiItemType {
    public int praise;
    public int dynamicId;
    public String praiseNum;
    public String commentNum;
    public String shareNum;

    @Override
    public int getItemType() {
        return IMultiItemType.TYPE_DYNAMIC_VIDEO_DETAILS;
    }
}
