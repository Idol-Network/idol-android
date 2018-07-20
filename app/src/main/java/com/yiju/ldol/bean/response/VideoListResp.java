package com.yiju.ldol.bean.response;

import com.yiju.idol.api.BaseReslut;
import com.yiju.idol.base.entity.IMultiItemType;

import java.util.List;

/**
 * Created by thbpc on 2018/3/26 0026.
 */

public class VideoListResp extends BaseReslut {

    public UserLiveItemBean userLiveItem;
    public List<VideoItemsBean> videoItems;

    /*paid (integer, optional): 是否已付费 0-未付费 1-已付费 ,*/
    public static class UserLiveItemBean implements IMultiItemType {

        public String avatar;
        public String backHits;
        public int cdnType;
        public long coinNum;
        public String coinNumStr;
        public String createTime;
        public String endTime;
        public String externalStreamUrl;
        public int giftNum;
        public String liveHits;
        public int liveId;
        public int liveModal;
        public int liveType;
        public String name;
        public String picUrl;
        public int praiseNum;
        public String startTime;
        public String statusTime;
        public String title;
        public int userId;
        public UserLiveBaseExtendedBean userLiveBaseExtended;
        public int wideScreen;


        @Override
        public int getItemType() {
            return IMultiItemType.TYPE_VIDEO_LIVE;
        }

        public static class UserLiveBaseExtendedBean {

            public int cdnType;
            public String hdlUrl;
            public String hlsUrl;
            public String pushUrl;
            public String roomId;
            public String rtmpUrl;
            public int timeOut;
            public int userId;

        }

    }

    public static class VideoItemsBean implements IMultiItemType {

        public long fee;
        public int paid;// 0-未付费 1-已付费
        public String personName;
        public String picUrl;
        public String playUrl;
        public int praise;
        public String praiseNum;
        public String shareNum;
        public String summary;
        public int videoId;
        public String viewNum;
        public int watchTime;

        @Override
        public int getItemType() {
            return IMultiItemType.TYPE_VIDEO_REPLAY;
        }
    }
}
