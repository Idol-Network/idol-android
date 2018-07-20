package com.yiju.ldol.bean.response;

import com.yiju.idol.api.BaseReslut;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhanghengzhen on 2018/3/23.
 */

public class PersonListResp extends BaseReslut {

    public List<PersonListBean> personList;

    public static class PersonListBean implements Serializable {
        public String avatar;//头像
        public boolean follow;//登录用户是否关注此明星
        public String followNum;//被关注人数
        public boolean mallFlag;//是否显示商城按钮
        public int personId;//影人编号
        public String name;//影人名称
        public PersonSignVideoBean personSignVideo;//签到明星视频
        public PersonSignVideoHomePageBean personSignVideoHomePage;//主页明星视频
        public String picUrl;//视频封面图
        public int roomId;//影人主聊天室ID
        public boolean sign;//今日登录用户是否针对此明星签到
        public List<PersonPlotSignAfterBean> personPlotSignAfter;//签到后剧情
        public List<PersonPlotSignBeforeBean> personPlotSignBefore;//签到前剧情
        public List<PersonSignPicBean> personSignPic;//签到明星图片
        public List<PersonSignPicHomePageBean> personSignPicHomePage;//主页明星图片
        public List<Long> userSignDateList;//用户针对此明星当月签到记录
        public String userSignDirections;//签到说明
        public String teamId;
        public int userId;
    }

    public static class PersonSignVideoBean implements Serializable {
        public long duration;
        public int personId;
        public String picUrl;
        public String playUrl;
        public String summary;
        public int type;
    }

    public static class PersonSignVideoHomePageBean implements Serializable {
        public int duration;
        public int personId;
        public String picUrl;
        public String playUrl;
        public String summary;
        public int type;
    }

    public static class PersonPlotSignAfterBean implements Serializable {
        public String content;
        public int personId;
        public int type;
    }

    public static class PersonPlotSignBeforeBean implements Serializable {
        public String content;
        public int personId;
        public int type;
    }

    public static class PersonSignPicBean implements Serializable {
        public int personId;
        public String picUrl;
        public int type;
    }

    public static class PersonSignPicHomePageBean implements Serializable {
        public int personId;
        public String picUrl;
        public int type;
    }

}
