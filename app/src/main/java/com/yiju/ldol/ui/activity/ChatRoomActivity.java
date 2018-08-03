package com.yiju.ldol.ui.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.business.chatroom.fragment.ChatRoomMessageFragment;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nim.uikit.common.ui.dialog.DialogMaker;
import com.netease.nim.uikit.common.util.log.LogUtil;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.ResponseCode;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.chatroom.ChatRoomService;
import com.netease.nimlib.sdk.chatroom.ChatRoomServiceObserver;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomInfo;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomKickOutEvent;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomMember;
import com.netease.nimlib.sdk.chatroom.model.ChatRoomStatusChangeData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomData;
import com.netease.nimlib.sdk.chatroom.model.EnterChatRoomResultData;
import com.yiju.idol.R;
import com.yiju.idol.base.App;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomActivity extends UI {
    private static final String ROOM_ID = "room_id";

    private String mRoomId;
    private ChatRoomInfo roomInfo;

    private boolean hasEnterSuccess = false; // 是否已经成功登录聊天室
    /**
     * 子页面
     */
    private ChatRoomMessageFragment messageFragment;
    private AbortableFuture<EnterChatRoomResultData> enterRequest;

    private TextView ivChatTitle;

    private boolean isCanEdit = false;

    public static void start(Context context, String roomid) {
        Intent intent = new Intent(context, ChatRoomActivity.class);
        intent.putExtra(ROOM_ID, roomid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_chatroom);
        mRoomId = getIntent().getStringExtra(ROOM_ID);
        if (TextUtils.isEmpty(mRoomId)) {
            return;
        }
        findView(R.id.iv_chat_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findView(R.id.iv_chat_details).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatRoomInfoActivity.start(ChatRoomActivity.this, roomInfo, isCanEdit);
            }
        });
        ivChatTitle = findView(R.id.iv_chat_title);

        getChatRoomMember(App.getApp().getUserId());
        initMessageFragment();
        // 注册监听
        registerObservers(true);
        // 登录聊天室
        enterRoom();
    }

    private void initMessageFragment() {
        messageFragment = (ChatRoomMessageFragment) getSupportFragmentManager().findFragmentById(R.id.ft_chatroom_msg);
        if (messageFragment != null) {
            messageFragment.init(mRoomId);
        } else {
            // 如果Fragment还未Create完成，延迟初始化
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    initMessageFragment();
                }
            }, 50);
        }
    }

    private String TAG = "tang";
    Observer<ChatRoomStatusChangeData> onlineStatus = new Observer<ChatRoomStatusChangeData>() {
        @Override
        public void onEvent(ChatRoomStatusChangeData chatRoomStatusChangeData) {
            if (!chatRoomStatusChangeData.roomId.equals(mRoomId)) {
                return;
            }
            if (chatRoomStatusChangeData.status == StatusCode.CONNECTING) {
                DialogMaker.updateLoadingMessage("连接中...");
            } else if (chatRoomStatusChangeData.status == StatusCode.LOGINING) {
                DialogMaker.updateLoadingMessage("登录中...");
            } else if (chatRoomStatusChangeData.status == StatusCode.LOGINED) {
//                if (fragment != null) {
//                    fragment.updateOnlineStatus(true);
//                }
            } else if (chatRoomStatusChangeData.status == StatusCode.UNLOGIN) {
//                if (fragment != null) {
//                    fragment.updateOnlineStatus(false);
//                }

                // 登录成功后，断网重连交给云信SDK，如果重连失败，可以查询具体失败的原因
                if (hasEnterSuccess) {
                    int code = NIMClient.getService(ChatRoomService.class).getEnterErrorCode(mRoomId);
                    Toast.makeText(ChatRoomActivity.this, "getEnterErrorCode=" + code, Toast.LENGTH_LONG).show();
                    LogUtil.d(TAG, "chat room enter error code:" + code);
                }
            } else if (chatRoomStatusChangeData.status == StatusCode.NET_BROKEN) {
//                if (fragment != null) {
//                    fragment.updateOnlineStatus(false);
//                }
                Toast.makeText(ChatRoomActivity.this, "当前网络不可用", Toast.LENGTH_SHORT).show();
            }

            LogUtil.i(TAG, "chat room online status changed to " + chatRoomStatusChangeData.status.name());
        }
    };

    Observer<ChatRoomKickOutEvent> kickOutObserver = new Observer<ChatRoomKickOutEvent>() {
        @Override
        public void onEvent(ChatRoomKickOutEvent chatRoomKickOutEvent) {
            Toast.makeText(ChatRoomActivity.this, "被踢出聊天室，原因:" + chatRoomKickOutEvent.getReason(), Toast.LENGTH_SHORT).show();
            onExitedChatRoom();
        }
    };

    public void onExitedChatRoom() {
        NimUIKit.exitedChatRoom(mRoomId);
        finish();
    }

    private void registerObservers(boolean register) {
        NIMClient.getService(ChatRoomServiceObserver.class).observeOnlineStatus(onlineStatus, register);
        NIMClient.getService(ChatRoomServiceObserver.class).observeKickOutEvent(kickOutObserver, register);
    }

    private void enterRoom() {
        DialogMaker.showProgressDialog(this, null, "", true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (enterRequest != null) {
                    enterRequest.abort();
                    onLoginDone();
                    finish();
                }
            }
        }).setCanceledOnTouchOutside(false);
        hasEnterSuccess = false;
        EnterChatRoomData data = new EnterChatRoomData(mRoomId);
        enterRequest = NIMClient.getService(ChatRoomService.class).enterChatRoomEx(data, 1);
        enterRequest.setCallback(new RequestCallback<EnterChatRoomResultData>() {
            @Override
            public void onSuccess(EnterChatRoomResultData result) {
                onLoginDone();
                roomInfo = result.getRoomInfo();
                ivChatTitle.setText(roomInfo.getName());
                NimUIKit.enterChatRoomSuccess(result, false);
                initMessageFragment();
                hasEnterSuccess = true;
            }

            @Override
            public void onFailed(int code) {
                onLoginDone();
                if (code == ResponseCode.RES_CHATROOM_BLACKLIST) {
                    Toast.makeText(ChatRoomActivity.this, "你已被拉入黑名单，不能再进入", Toast.LENGTH_SHORT).show();
                } else if (code == ResponseCode.RES_ENONEXIST) {
                    Toast.makeText(ChatRoomActivity.this, "聊天室不存在", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ChatRoomActivity.this, "enter chat room failed, code=" + code, Toast.LENGTH_SHORT).show();
                }
                finish();
            }

            @Override
            public void onException(Throwable exception) {
                onLoginDone();
                Toast.makeText(ChatRoomActivity.this, "enter chat room exception, e=" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void onLoginDone() {
        enterRequest = null;
        DialogMaker.dismissProgressDialog();
    }

    public ChatRoomInfo getRoomInfo() {
        return roomInfo;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        registerObservers(false);
    }

    @Override
    public void onBackPressed() {
        if (messageFragment == null || !messageFragment.onBackPressed()) {
            super.onBackPressed();
        }

        logoutChatRoom();
    }

    private void logoutChatRoom() {
        NIMClient.getService(ChatRoomService.class).exitChatRoom(mRoomId);
        onExitedChatRoom();
    }

    /**
     * 获取成员信息
     */
    private void getChatRoomMember(int userId) {
        List<String> accounts = new ArrayList<>();
        accounts.add(String.valueOf(userId));
        NIMClient.getService(ChatRoomService.class)
                .fetchRoomMembersByIds(mRoomId, accounts)
                .setCallback(new RequestCallbackWrapper<List<ChatRoomMember>>() {
                    @Override
                    public void onResult(int i, List<ChatRoomMember> list, Throwable throwable) {
                        if (list != null && !list.isEmpty()) {
                            switch (list.get(0).getMemberType()) {
                                case ADMIN://管理者
                                case CREATOR://创建者
                                    isCanEdit = true;
                                    break;
                                default:
                                    isCanEdit = false;
                                    break;
                            }
                        }
                    }
                });
    }
}
