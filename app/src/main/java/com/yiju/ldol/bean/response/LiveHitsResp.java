package com.yiju.ldol.bean.response;

import com.yiju.idol.api.BaseReslut;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhanghengzhen on 2016/10/13.
 */
public class LiveHitsResp extends BaseReslut {

    public int liveHits;//当前人数
    public List<UserLiveHitsesBean> userLiveHitses;

    public static class UserLiveHitsesBean implements Serializable {
        public String endTime;
        public int firstIn;
        public int liveId;
        public int num;
        public int online;
        public int robot;
        public String statusTime;
        public int timeLength;
        public UserBean user;
        public int userId;

        public static class UserBean implements Serializable {
            public String avatar;
            public int category;
            public String categoryName;
            public boolean follow;
            public int liveType;
            public String nickName;
            public int sex;
            public String sign;
            public String tag;
            public int totalApplause;
            public int userId;
            public int userSex;
            public int userStatus;
            public int vip;
            public int level;
        }

    }
}
