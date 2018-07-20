package com.yiju.ldol.bean.response;

import com.yiju.idol.api.BaseReslut;

import java.io.Serializable;
import java.util.List;

/**
 * 活动
 * Created by zhanghengzhen on 2018/5/11.
 */

public class ActivitiesResp extends BaseReslut {

    public List<CrowdfundingBean> crowdfundings;

    public static class CrowdfundingBean implements Serializable {
        public int num;
        public String time;
        public String tag;
        public String title;
        public String picUrl;
    }

}
