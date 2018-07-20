package com.yiju.ldol.bean.response;

import com.yiju.idol.api.BaseReslut;
import com.yiju.idol.base.entity.IMultiItemType;

import java.io.Serializable;
import java.util.List;

/**
 * Created by thbpc on 2018/3/24 0024.
 */

public class DynamicListResp extends BaseReslut {


    public List<DynamicItemsBean> dynamicItems;

    public static class DynamicItemsBean implements Serializable, IMultiItemType {

        public String commentNum;
        public int dynamicId;
        public String dynamicSource;
        public String personPicUrl;
        public int praise;
        public String praiseNum;
        public String shareNum;
        public String time;
        public String title;
        public List<ContentBean> content;

        @Override
        public int getItemType() {
            if (content == null || content.isEmpty()) {
                return IMultiItemType.TYPE_DYNAMIC_TEXT;
            }
            switch (content.get(0).type) {
                case 1:
                    return IMultiItemType.TYPE_DYNAMIC_IMAGE;
                case 2:
                    return IMultiItemType.TYPE_DYNAMIC_VIDEO;
                default:
                    return IMultiItemType.TYPE_DYNAMIC_TEXT;
            }
        }

        public static class ContentBean implements Serializable {

            public int orderNo;
            public int type;
            public String url;
            public String videoPic;
        }
    }
}
