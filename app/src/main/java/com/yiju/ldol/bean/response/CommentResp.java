package com.yiju.ldol.bean.response;

import android.text.TextUtils;

import com.yiju.idol.api.BaseReslut;
import com.yiju.idol.base.entity.IMultiItemType;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhanghengzhen on 2018/3/21.
 */

public class CommentResp extends BaseReslut {

    public List<UserCommentItemsBean> userCommentItems;

    public static class UserCommentItemsBean implements Serializable, IMultiItemType {
        public String content;
        public String parentContent;
        public String parentNickName;
        public long releaseTime;
        public int userCommentId;
        public int userId;//评论用户的id
        public String userNickName;
        public String userPicUrl;

        @Override
        public int getItemType() {
            //判断是评论还是回复其他人
            return TextUtils.isEmpty(parentNickName) ? IMultiItemType.TYPE_COMMENT : IMultiItemType.TYPE_RECOMMENT;
        }
    }
}
