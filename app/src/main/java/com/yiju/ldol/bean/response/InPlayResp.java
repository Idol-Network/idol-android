package com.yiju.ldol.bean.response;

import com.yiju.idol.api.BaseReslut;
import com.yiju.idol.bean.LiveGiftBean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Allan_Zhang on 2016/6/21.
 */
public class InPlayResp extends BaseReslut {

    public boolean admin;//是否为管理员
    public String userGradePic;//管理员标识，用于区别显示的图标
    public boolean follow;
    public boolean gag;
    public GroupMessageBean groupMessage;
    public LiveParameterBean liveParameter;
    public LivePrevueBean livePrevue;
    public LiveShareBean liveShare;
    public int number;//用于存储排名对应的礼物id，若大于0，表示当前用户为排行榜前三，需显示进入直播间动画
    public boolean openShop;//是否打开商城
    public boolean prize;
    public boolean see;
    public int type;
    public int liveType;//直播类型：0为礼物直播，1为投票直播
    public boolean mallOpen;//当openShop为true时，若mallOpen为true则直接展示商品列表
    public String bgPicUrl;//播放的后大背景图
    public UserBalanceBean anchorBalance;//主播的掌声信息
    public UserBalanceBean userBalance;
    public UserLiveBean userLive;
    public ArrayList<LiveSysMessage> liveSysMessages;
    public ArrayList<UserLiveHitsBean> userLiveHitses;
    public ArrayList<LiveGiftBean> liveGifts;//礼物列表
    public ArrayList<LiveBackUrlsBean> liveBackUrls;
    public ArrayList<UserBlacksBean> userBlacks;

    public long likeTime;
    public int liveBackIndex;//回放视频的下标，从0开始
    public int liveBackTime;//回放视频开始的世界
    public String tag;//练习生名字标签
    public String tagUrl;//练习生网页
    public int runwayMinLevel;//超过该等级时展示用户进入动画
    public int barrageMinLevel;//可发弹幕的最低等级

    public static class GroupMessageBean implements Serializable {
        public String groupHelpUrl;
        public LiveGroupBean liveGroup;
        public LiveGroupItemBean liveGroupItem;
        public LiveGroupTypeBean liveGroupType;
        public boolean liveIng;//进入剧场直播间时判断 若为ture 则提示是否继续直播
        public int liveTime;//直播时长（秒）
        public int starNum;//星星数量
        public int userFirstDeathTime;//第一次死亡保护时长(分钟)
        public int userGroupProtectTime;//开播鸡蛋保护时长(分钟)
        public boolean userLineUp;
        public boolean userLiveIng;//当前直播间是否有人在直播
        public ArrayList<String> videoUrls;//无直播时循环播放的视频

        public static class LiveGroupBean implements Serializable {
            public String createTime;
            public int createUser;
            public String endTime;
            public int groupId;
            public int groupTypeId;
            public int liveId;
            public String picUrl;
            public String remark;
            public int roomId;
            public String startTime;
            public int status;
            public String summary;
            public String title;
            public int userId;
        }

        public static class LiveGroupItemBean implements Serializable {
            public int addTime;
            public int allotEggNum;//已分配鸡蛋数 aggNum-allotEggNum为到可分配的红包数量
            public String createTime;
            public long eggActiveTime;//激活红包时间 当前时间-激活红包时间 = 红包倒计时剩余时间
            public long deathTime;
            public int eggNum;
            public String grabTime;
            public int groupId;
            public int healthPoint;
            public String inComeStr;//收入
            public int itemId;
            public int liveId;
            public int status;//状态 0-排队中，1-放弃，2-发直播中，3-直播中，4-结束 ，10-第一次死亡，11-死亡 ,
            public int ticketNum;
            public int userId;
        }

        public static class LiveGroupTypeBean implements Serializable {
            public int awardAmount;
            public String remark;
            public int ticketNum;
            public String title;
            public int typeId;
        }
    }

    public static class LiveParameterBean implements Serializable {
        public int frames;
        public int rate;
    }

    public static class LivePrevueBean implements Serializable {
        public String content;
        public String createTime;
        public int createUser;
        public int interactValue;
        public int livePrevueId;
        public String liveTime;
        public String liveTimeStr;
        public String picUrl;
        public String prizePicUrl;
        public int robotCount;
        public int status;
        public String title;
        public UserLiveBaseBean userLiveBase;
        public UserBean user;
        public int userId;
    }

    public static class LiveShareBean implements Serializable {
        public String content;
        public String picUrl;
        public String title;
        public String url;
    }

    public static class UserBalanceBean implements Serializable {
        public long applause;
        public long applauseFrozen;
        public long applausePrice;
        public long strength;
        public long totalApplause;
        public long totalStrength;
        public int userId;
        public int egg;
        public int sendEgg;
        public int sendTicket;
        public int ticket;
    }

    public static class UserLiveBean implements Serializable {
        public long applauseNum;
        public int backHits;
        public String backHitsStr;
        public int category;
        public String categoryName;
        public int cdnType;
        public String cityName;
        public long createTime;//当前直播创建时间
        public String createTimeStr;
        public long endTime;
        public int followNum;
        public int interactValue;
        public int lastStrength;
        public double latitude;
        public int liveHits;
        public long praiseNum;
        public String liveHitsStr;
        public int liveId;
        public int liveModal;//0：回放模式；1：直播模式
        public int liveType;
        public double longitude;
        public String picUrl;
        public int prevueId;
        public String relationStar;
        public long startTime;
        public String startTimeStr;
        public int status;
        public long statusTime;//状态开始时间 持续时间 = 时间戳 - 状态开始时间
        public int strengthNum;
        public UserBean user;
        public int userId;
        public UserLiveBaseBean userLiveBase;

    }

    public static class UserLiveBaseBean implements Serializable {
        public int cdnType;
        public String hdlUrl;
        public String hlsUrl;
        public String pushUrl;
        public int roomId;
        public String rtmpUrl;
        public int userId;
    }

    public static class LiveBackUrlsBean implements Serializable {
        public int liveBackId;
        public int liveId;
        public String playUrl;
    }

    public static class UserBlacksBean implements Serializable {
        public int blackUserId;
        public String createTime;
        public int userId;
    }

    public static class UserLiveHitsBean implements Serializable {
        public String endTime;//离开时间
        public int firstIn;//0为直播进入，1为点播进入
        public int liveId;//直播ID
        public int online;//是否在线 0-离线 1-在线
        public String statusTime;//进入时间
        public UserBean user;//直播用户信息
        public int userId;//用户ID
    }

    public static class UserBean implements Serializable {
        public String avatar;
        public boolean follow;
        public String nickName;
        public String sign;
        public int status;
        public int userId;
        public int userSex;
        public int vip;
        public int level;
    }

    public static class LiveSysMessage implements Serializable {
        public String addtime;
        public String content;
        public int messId;
        public String title;
    }
}
