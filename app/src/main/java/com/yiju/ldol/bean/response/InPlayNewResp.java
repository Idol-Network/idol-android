package com.yiju.ldol.bean.response;

import com.yiju.idol.api.BaseReslut;
import com.yiju.idol.bean.LiveGiftBean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Allan_Zhang on 2018/4/19.
 */

public class InPlayNewResp extends BaseReslut {

    public boolean admin;
    public AnchorWalletBean anchorWallet;
    public int barrageMinLevel;
    public String bgPicUrl;
    public boolean gag;
    public int level;
    public int liveBackIndex;
    public int liveBackTime;
    public int number;
    public int runwayMinLevel;
    public boolean see;
    public ShareBean share;
    public String userGradePic;
    public UserLiveItemBean userLiveItem;
    public UserWalletBean userWallet;
    public List<LiveBackUrlsBean> liveBackUrls;
    public List<LiveGiftBean> liveGifts;
    public List<LiveSysMessagesBean> liveSysMessages;
    public List<UserBlacksBean> userBlacks;
    public List<UserLiveHitsesBean> userLiveHitses;

    public long likeTime;//用于点赞计时

    public static class AnchorWalletBean implements Serializable {
        public String num;
        public int userId;
    }

    public static class ShareBean implements Serializable {
        public String content;
        public String picUrl;
        public String title;
        public String url;
    }

    public static class UserLiveItemBean implements Serializable {
        public String avatar;//直播用户头像
        public String name;//主播名称
        public String backHits;//回放人气
        public int cdnType;
        public long coinNum;//币数
        public String coinNumStr;//币数(字符串)
        public String createTime;
        public String endTime;
        public String externalStreamUrl;
        public int giftNum;
        public String liveHits;
        public int liveId;
        public int liveModal;//直播类型(0-直播 1-回放)
        public int liveType;//直播类型 0-明星直播
        public String picUrl;
        public int praiseNum;
        public String startTime;
        public int status;//状态 1-直播 9-暂停 10-停止 20-点播 30-禁止 40-回收站
        public String statusTime;
        public String title;
        public int userId;
        public UserLiveBaseExtendedBean userLiveBaseExtended;
        public int wideScreen;
    }

    public static class UserLiveBaseExtendedBean implements Serializable {
        public int cdnType;
        public String hdlUrl;
        public String hlsUrl;
        public String pushUrl;
        public String roomId;
        public String rtmpUrl;
        public int timeOut;
        public int userId;
    }

    public static class UserWalletBean implements Serializable {
        public long num;//可用币数
        public String numStr;//可用币数(字符串)
        public int userId;
    }

    public static class LiveBackUrlsBean implements Serializable {
        public String bitRate;
        public float duration;
        public int liveBackId;
        public int liveId;
        public String liveKey;
        public int mergeType;
        public String picUrl;
        public String playUrl;
        public int size;
    }

    public static class LiveSysMessagesBean implements Serializable {
        public String addtime;
        public String content;
        public int messId;
        public String title;
    }

    public static class UserBlacksBean implements Serializable {
        public int blackUserId;
        public String createTime;
        public int userId;
    }

    public static class UserLiveHitsesBean implements Serializable {
        public String endTime;
        public int firstIn;
        public int hitsId;
        public int liveId;
        public int num;
        public int online;
        public String quitTime;
        public int robot;
        public String statusTime;
        public int timeLength;
        public UserBean user;
        public int userId;
    }

    public static class UserBean implements Serializable {
        public String areaCode;
        public String avatar;
        public String createTime;
        public String lastLoginTime;
        public int level;
        public String mobile;
        public String nickName;
        public String password;
        public String sign;
        public int status;
        public String token;
        public int userId;
    }
}
