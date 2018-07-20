package com.yiju.ldol.bean.response;


import com.yiju.idol.api.BaseReslut;

import java.io.Serializable;

/**
 * 结束直播返回数据
 * Created by zhanghengzhen on 2016/6/25.
 */
public class OutResp extends BaseReslut {

    public String applauseNum;
    public String followNum;
    public String liveHits;
    public LiveShareBean liveShare;
    public String playTimeStr;
    public String praiseNum;//点赞数
    public String income;//收入
    public boolean liveState;//true为直播成功 false为失败

    public static class LiveShareBean implements Serializable {
        public String content;
        public String picUrl;
        public String title;
        public String url;
    }
}
