package com.yiju.ldol.ui.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.constant.MemberQueryType;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomUpdateInfo;
import com.yiju.idol.R;
import com.yiju.idol.base.BaseImmersionActivity;
import com.yiju.idol.ui.adapter.ChatRoomInfoAdapter;
import com.yiju.idol.utils.DensityUtil;
import com.yiju.idol.utils.DialogUtils;

import java.io.Serializable;

import butterknife.BindView;
import butterknife.OnClick;

public class ChatRoomInfoActivity extends BaseImmersionActivity {

    private static final String CHAT_ROOM_INFO = "chat_room_info";
    private static final String CAN_EDIT_ANNOUNCEMENT = "can_edit_announcement";

    @BindView(R.id.rcv_chatroom)
    RecyclerView rcvChatroom;
    @BindView(R.id.iv_chatinfo_back)
    ImageView ivChatinfoBack;
    @BindView(R.id.iv_chatinfo_title)
    TextView ivChatinfoTitle;
    @BindView(R.id.rl_chatinfo_title)
    RelativeLayout rlChatinfoTitle;

    private ChatRoomInfo chatRoomInfo;
    private ChatRoomInfoAdapter mAdapter;

    public static void start(Context context, ChatRoomInfo info, boolean isCanEdit) {
        Intent intent = new Intent(context, ChatRoomInfoActivity.class);
        intent.putExtra(CHAT_ROOM_INFO, (Serializable) info);
        intent.putExtra(CAN_EDIT_ANNOUNCEMENT, isCanEdit);
        context.startActivity(intent);
    }

    @Override
    public int getLayout() {
        return R.layout.ac_chatroominfo;
    }

    @Override
    public void initView() {
        mImmersionBar.titleBarMarginTop(rlChatinfoTitle);
        GridLayoutManager layout = new GridLayoutManager(mContext, 5);
        rcvChatroom.setLayoutManager(layout);
        layout.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return position == 0 ? 5 : 1;
            }
        });
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        chatRoomInfo = (ChatRoomInfo) getIntent().getSerializableExtra(CHAT_ROOM_INFO);
        boolean isCanEdit = getIntent().getBooleanExtra(CAN_EDIT_ANNOUNCEMENT, false);
        if (chatRoomInfo == null) {
            finish();
            return;
        }
        mAdapter = new ChatRoomInfoAdapter(isCanEdit ? new ChatRoomInfoAdapter.OnItemClickLisenter() {
            @Override
            public void onAnnouncementClick() {
                DialogUtils.showCenterDialog(ChatRoomInfoActivity.this, R.layout.dialog_edit_announcement_layout, DensityUtil.dip2px(75), WindowManager.LayoutParams.WRAP_CONTENT, new DialogUtils.InitViewsListener() {
                    @Override
                    public void setAction(Dialog dialog, View view) {
                        EditText etEdit = view.findViewById(R.id.et_edit);
                        view.findViewById(R.id.bt_cancel).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        view.findViewById(R.id.bt_ok).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String announcement = etEdit.getText().toString().trim();
                                setAnnouncement(announcement);
                            }
                        });
                    }
                });
            }
        } : null);
        rcvChatroom.setAdapter(mAdapter);
        mAdapter.setRoomInfo(chatRoomInfo);
        getMembers();
    }

    private void getMembers() {
        // 以游客为例，从最新时间开始，查询10条
//        NIMClient.getService(ChatRoomService.class).fetchRoomMembers(chatRoomInfo.getRoomId(), MemberQueryType.ONLINE_NORMAL, 0, 100).setCallback(new RequestCallbackWrapper<List<ChatRoomMember>>() {
//            @Override
//            public void onResult(int code, List<ChatRoomMember> result, Throwable exception) {
//                mAdapter.addRoomMembers(result);
//            }
//        });

        NimUIKit.getChatRoomProvider().fetchRoomMembers(chatRoomInfo.getRoomId(), MemberQueryType.NORMAL, 0, 10, (success, result, code) -> {
            if (success) {
                // 结果集
                mAdapter.addRoomMembers(result);
                // 固定成员已经拉完
                NimUIKit.getChatRoomProvider().fetchRoomMembers(chatRoomInfo.getRoomId(), MemberQueryType.GUEST, 0, 100, (success1, result1, code1) -> {
                    if (success1) {
                        // 结果集
                        mAdapter.addRoomMembers(result1);
                    }
                });
            }
        });
    }


    @OnClick(R.id.iv_chatinfo_back)
    public void onViewClicked() {
        finish();
    }

    /**
     * 修改公告
     *
     * @param announcement
     */
    private void setAnnouncement(String announcement) {
        // 以更新聊天室信息为例
        ChatRoomUpdateInfo updateInfo = new ChatRoomUpdateInfo();
        updateInfo.setAnnouncement(announcement);
        NIMClient.getService(ChatRoomService.class)
                .updateRoomInfo(chatRoomInfo.getRoomId(), updateInfo, false, null)
                .setCallback(new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 成功
                        chatRoomInfo.setAnnouncement(announcement);
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailed(int i) {
                        // 失败
                    }

                    @Override
                    public void onException(Throwable throwable) {
                        // 错误
                    }
                });
    }


}
