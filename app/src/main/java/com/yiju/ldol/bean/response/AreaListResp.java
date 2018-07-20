package com.yiju.ldol.bean.response;

import com.yiju.idol.api.BaseReslut;

import java.util.List;

/**
 * Created by thbpc on 2018/3/24 0024.
 */

public class AreaListResp extends BaseReslut {

    public List<AreaListBean> areaList;

    public static class AreaListBean {
        public int areaId;
        public String name;
    }
}
