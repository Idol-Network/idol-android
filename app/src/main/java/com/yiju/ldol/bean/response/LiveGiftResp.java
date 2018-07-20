package com.yiju.ldol.bean.response;

import android.text.TextUtils;

import com.yiju.idol.api.BaseReslut;

import java.io.Serializable;
import java.util.List;

/**
 * Created by d on 2016/10/31.
 */
public class LiveGiftResp extends BaseReslut {


    public List<LiveGiftsBean> liveGifts;

    public static class LiveGiftsBean implements Serializable {
        public String endTime;
        public int giftId;
        public String identity;
        public int isStop;
        public String picUrl;
        public String playId;
        public long price;
        public String startTime;
        public String title;
        public int type;
        public String zipUrl;

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof LiveGiftsBean) {
                if (TextUtils.equals(this.identity.split(",")[0], ((LiveGiftsBean) obj).identity.split(",")[0])) {
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        }
    }
}
