package com.yiju.ldol.bean.events;

import com.yiju.idol.base.entity.IEventType;

import java.io.Serializable;

/**
 * Created by Allan_Zhang on 2018/3/27.
 */

public class EventFetterRefresh implements IEventType, Serializable {

    private int clickPosition;//记录跳转界面时点击的item

    private int praise;

    private String praiseNum;

    private String commentNum;

    public EventFetterRefresh(int clickPosition, int praise, String praiseNum, String commentNum) {
        this.clickPosition = clickPosition;
        this.praise = praise;
        this.praiseNum = praiseNum;
        this.commentNum = commentNum;
    }

    public int getClickPosition() {
        return clickPosition;
    }

    public int getPraise() {
        return praise;
    }

    public String getPraiseNum() {
        return praiseNum;
    }

    public String getCommentNum() {
        return commentNum;
    }

    @Override
    public String getType() {
        return ON_FETTER_UPDATE;
    }
}
