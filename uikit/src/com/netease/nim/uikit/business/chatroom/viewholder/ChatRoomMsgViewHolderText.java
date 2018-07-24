package com.netease.nim.uikit.business.chatroom.viewholder;

import android.graphics.Color;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.session.emoji.MoonUtil;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.common.util.sys.ScreenUtil;
import com.netease.nim.uikit.impl.NimUIKitImpl;

/**
 * Created by hzxuwen on 2016/1/18.
 */
public class ChatRoomMsgViewHolderText extends ChatRoomMsgViewHolderBase {

    protected TextView bodyTextView;


    public ChatRoomMsgViewHolderText(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.nim_message_item_text;
    }

    @Override
    protected void inflateContentView() {
        bodyTextView = findViewById(R.id.nim_message_item_text_body);
    }

    protected String getDisplayText() {
        return message.getContent();
    }

    @Override
    protected boolean isShowBubble() {
        //TODO Allan 2018.04.02 设置左右方向
        return true;
    }

    @Override
    protected boolean isShowHeadImage() {
        //TODO Allan 2018.04.02 是否显示头像
        return true;
    }

    @Override
    protected void bindContentView() {
        //TODO Allan 2018.04.02 是否显示头像 参考MsgViewHolderText#layoutDirection
        layoutDirection();
        MoonUtil.identifyFaceExpression(NimUIKit.getContext(), bodyTextView, getDisplayText(), ImageSpan.ALIGN_BOTTOM);
        bodyTextView.setMovementMethod(LinkMovementMethod.getInstance());
        bodyTextView.setOnLongClickListener(longClickListener);
    }

    private void layoutDirection() {
        if (isReceivedMessage()) {
            bodyTextView.setBackgroundResource(NimUIKitImpl.getOptions().messageLeftBackground);
            bodyTextView.setTextColor(Color.BLACK);
            bodyTextView.setPadding(ScreenUtil.dip2px(15), ScreenUtil.dip2px(8), ScreenUtil.dip2px(10), ScreenUtil.dip2px(8));
        } else {
            bodyTextView.setBackgroundResource(NimUIKitImpl.getOptions().messageRightBackground);
            bodyTextView.setTextColor(Color.WHITE);
            bodyTextView.setPadding(ScreenUtil.dip2px(10), ScreenUtil.dip2px(8), ScreenUtil.dip2px(15), ScreenUtil.dip2px(8));
        }
    }
}
