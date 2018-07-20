package com.yiju.ldol.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;
import com.gyf.barlibrary.OnKeyboardListener;
import com.yiju.idol.R;
import com.yiju.idol.api.APIHelper;
import com.yiju.idol.api.BaseReslut;
import com.yiju.idol.api.CallBack;
import com.yiju.idol.base.App;
import com.yiju.idol.base.BaseActivity;
import com.yiju.idol.base.entity.IMultiItemType;
import com.yiju.idol.bean.DetailsReturnData;
import com.yiju.idol.bean.VideoIntentData;
import com.yiju.idol.bean.response.AddCommentResp;
import com.yiju.idol.bean.response.AddViewNumResp;
import com.yiju.idol.bean.response.CommentResp;
import com.yiju.idol.bean.response.SettlementResp;
import com.yiju.idol.bean.response.UserPraiseResp;
import com.yiju.idol.listener.OnShareClickListener;
import com.yiju.idol.ui.adapter.TVCommentAdapter;
import com.yiju.idol.ui.view.JzVideoPlayer;
import com.yiju.idol.ui.view.refresh.CustomHeader;
import com.yiju.idol.utils.DialogUtils;
import com.yiju.idol.utils.PaypalHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jzvd.JZMediaManager;
import cn.jzvd.JZUserAction;
import cn.jzvd.JZUtils;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import de.greenrobot.event.EventBus;
import me.dkzwm.widget.srl.RefreshingListenerAdapter;
import me.dkzwm.widget.srl.SmoothRefreshLayout;
import me.dkzwm.widget.srl.extra.IRefreshView;
import me.dkzwm.widget.srl.utils.ScrollCompat;

/**
 * Created by thbpc on 2018/3/20 0020.
 */

public class TVDetailsActivity extends BaseActivity {

    public static final String VIDEO_DATA = "video_data";

    @BindView(R.id.jz_tv_video)
    JzVideoPlayer jzTvVideo;
    @BindView(R.id.rv_tv_commtent)
    RecyclerView rvTvCommtent;
    @BindView(R.id.et_tv_input)
    EditText etTvInput;
    @BindView(R.id.bt_tv_send)
    Button btTvSend;
    @BindView(R.id.rl_main)
    RelativeLayout rl_main;
    @BindView(R.id.srl_content)
    SmoothRefreshLayout srlContent;
    @BindView(R.id.iv_player_back)
    ImageView ivBack;

    private int nextPageIndex = 1;
    private TVCommentAdapter mAdapter;

    private VideoIntentData videoData;
    private CommentResp.UserCommentItemsBean reCommentBean;
    private TextWatcher watcher;
    private PaypalHelper mPaypal;

    @Override
    public int getLayout() {
        return R.layout.ac_tvdetails;
    }

    @Override
    public void initData(Bundle savedInstanceState) {

        mPaypal = PaypalHelper.getInstance();
        mPaypal.initPayService(mContext);
    }

    @Override
    public void initView() {
        videoData = (VideoIntentData) getIntent().getSerializableExtra(VIDEO_DATA);

        if (videoData == null) {
            finish();
            return;
        }

        ArrayList<IMultiItemType> list = new ArrayList<>();
        list.add(0, videoData);

        mAdapter = new TVCommentAdapter(null);
        mAdapter.setNewData(list);

        ImmersionBar
                .with(this)
                .keyboardEnable(true)
                .setOnKeyboardListener(new OnKeyboardListener() {
                    @Override
                    public void onKeyboardChange(boolean isPopup, int keyboardHeight) {
                        if (isPopup) {//显示
                            etTvInput.setMaxLines(3);
                        } else {//关闭
                            etTvInput.setMaxLines(1);
                            if (TextUtils.isEmpty(etTvInput.getText().toString())) {
                                reCommentBean = null;
                                etTvInput.setHint("");
                            }
                        }
                    }
                });
        watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    btTvSend.setVisibility(View.GONE);
                } else {
                    btTvSend.setVisibility(View.VISIBLE);
                }
            }
        };
        etTvInput.addTextChangedListener(watcher);
        jzTvVideo.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JZVideoPlayer.backPress();
                setReslutData();
                finish();
            }
        });

        jzTvVideo.setUp(videoData.videoUrl, JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL);
        jzTvVideo.thumbImageView.setImageURI(videoData.picUrl);
        rvTvCommtent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        CustomHeader customHeader = new CustomHeader(mContext);
        customHeader.setStyle(IRefreshView.STYLE_SCALE);
        srlContent.setHeaderView(customHeader);
        srlContent.setOnRefreshListener(new RefreshingListenerAdapter() {
            @Override
            public void onRefreshBegin(boolean isRefresh) {
                //下拉刷新
                nextPageIndex = 1;
                mAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载
                getData();
            }
        });

        rvTvCommtent.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(() -> {
            //上拉加载
            getData();
        }, rvTvCommtent);
        srlContent.setOnChildNotYetInEdgeCannotMoveHeaderCallBack((parent, child, header) -> ScrollCompat.canChildScrollUp(rvTvCommtent));
        srlContent.setOnChildNotYetInEdgeCannotMoveFooterCallBack((parent, child, footer) -> ScrollCompat.canChildScrollDown(rvTvCommtent));
        srlContent.setLoadMoreScrollTargetView(rvTvCommtent);
        rvTvCommtent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        rvTvCommtent.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                IMultiItemType item = mAdapter.getItem(position);
                int itemType = item.getItemType();
                switch (itemType) {
                    case IMultiItemType.TYPE_COMMENT: //评论
                    case IMultiItemType.TYPE_RECOMMENT: //回复
                        reCommentBean = (CommentResp.UserCommentItemsBean) item;
                        etTvInput.setHint("@" + reCommentBean.userNickName);
                        etTvInput.setSelection(etTvInput.length());
                        showKeyboard(etTvInput);
                        break;
                }
            }
        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                VideoIntentData item = (VideoIntentData) mAdapter.getItem(position);
                switch (view.getId()) {
                    case R.id.tv_share_Num:
                        DialogUtils.showShareDialog(TVDetailsActivity.this, new OnShareClickListener() {
                            @Override
                            public void weixin() {

                            }

                            @Override
                            public void sina() {

                            }

                            @Override
                            public void facebook() {

                            }

                            @Override
                            public void twitter() {

                            }

                            @Override
                            public void copy() {

                            }
                        });
                        break;
                    case R.id.tv_like_Num:
                        if (item.praise == 1) {//0-否 1-是
                            APIHelper.getInstance().praiseCancel(3, item.videoId, new CallBack<UserPraiseResp>() {
                                @Override
                                public void onSuccess(UserPraiseResp data) {
                                    item.praise = 0;
                                    item.praiseNum = data.num;
                                    mAdapter.setData(position, item);
                                }
                            });
                        } else {
                            APIHelper.getInstance().praise(3, item.videoId, new CallBack<UserPraiseResp>() {
                                @Override
                                public void onSuccess(UserPraiseResp data) {
                                    item.praise = 1;
                                    item.praiseNum = data.num;
                                    mAdapter.setData(position, item);
                                }
                            });
                        }
                        break;
                }
            }
        });
        jzTvVideo.startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playVideo();
            }
        });
        jzTvVideo.setOnPlayerStateListener(new JzVideoPlayer.OnPlayerStateListener() {
            public void onStart() {
//                Log.d("tang", "开始计时");
            }

            @Override
            public void onPause() {
//                Log.d("tang", "停止计时");
            }
        });
    }

    /**
     * @see JZVideoPlayer 448 line
     * From Copy
     */
    private void playVideo() {
        if (jzTvVideo.dataSourceObjects == null ||
                JZUtils.getCurrentFromDataSource(jzTvVideo.dataSourceObjects, jzTvVideo.currentUrlMapIndex) == null) {
            Toast.makeText(mContext, getResources().getString(cn.jzvd.R.string.no_url), Toast.LENGTH_SHORT).show();
            return;
        }
        if (jzTvVideo.currentState == JZVideoPlayer.CURRENT_STATE_NORMAL) {
            if (videoData.fee == 1 && videoData.paid == 0) {//收费且未付费
                showBuyDialog();
            } else {
                play();
            }
        } else if (jzTvVideo.currentState == JZVideoPlayer.CURRENT_STATE_PLAYING) {
            jzTvVideo.onEvent(JZUserAction.ON_CLICK_PAUSE);
            JZMediaManager.pause();
            jzTvVideo.onStatePause();
        } else if (jzTvVideo.currentState == JZVideoPlayer.CURRENT_STATE_PAUSE) {
            jzTvVideo.onEvent(JZUserAction.ON_CLICK_RESUME);
            JZMediaManager.start();
            jzTvVideo.onStatePlaying();
        } else if (jzTvVideo.currentState == JZVideoPlayer.CURRENT_STATE_AUTO_COMPLETE) {
            jzTvVideo.onEvent(JZUserAction.ON_CLICK_START_AUTO_COMPLETE);
            jzTvVideo.startVideo();
        }
    }

    private void showBuyDialog() {
        DialogUtils.showCenterDialog(TVDetailsActivity.this, R.layout.dialog_buy_video, 270, WindowManager.LayoutParams.WRAP_CONTENT, new DialogUtils.InitViewsListener() {
            @Override
            public void setAction(Dialog dialog, View view) {
                TextView tvContentPrice = view.findViewById(R.id.tv_content_price);
                tvContentPrice.setText(String.format(getString(R.string.buy_video), videoData.fee / 100f));//显示金额 /100
                TextView btCancel = view.findViewById(R.id.bt_cancel);
                TextView btCommit = view.findViewById(R.id.bt_commit);
                View.OnClickListener listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (v.getId()) {
                            case R.id.bt_commit:
                                APIHelper.getInstance().buyNow(videoData.videoId, 3, new CallBack<SettlementResp>() {
                                    @Override
                                    public void onSuccess(SettlementResp data) {
                                        mPaypal.doPayPalPay(TVDetailsActivity.this, 1, data.orderNumber, data.amountStr, data.shortDescription);
                                    }
                                });
                                break;
                        }
                        dialog.dismiss();
                    }
                };
                btCancel.setOnClickListener(listener);
                btCommit.setOnClickListener(listener);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPaypal.onPayResult(resultCode, data, new PaypalHelper.onPayCallback() {
            @Override
            public void onPaySuccess() {
                videoData.paid = 1;
                play();
            }

            @Override
            public void onPayCancel() {

            }

            @Override
            public void onPayError(String message) {

            }
        });
    }

    private void play() {
        if (!JZUtils.getCurrentFromDataSource(jzTvVideo.dataSourceObjects, jzTvVideo.currentUrlMapIndex).toString().startsWith("file") && !
                JZUtils.getCurrentFromDataSource(jzTvVideo.dataSourceObjects, jzTvVideo.currentUrlMapIndex).toString().startsWith("/") &&
                !JZUtils.isWifiConnected(mContext) && !JZVideoPlayer.WIFI_TIP_DIALOG_SHOWED) {
            jzTvVideo.showWifiDialog();
        }
        jzTvVideo.startVideo();
        jzTvVideo.onEvent(JZUserAction.ON_CLICK_START_ICON);
        //开始播放 增加播放次数
        APIHelper.getInstance().addViewNum(videoData.videoId, new CallBack<AddViewNumResp>() {
            @Override
            public void onSuccess(AddViewNumResp data) {
                VideoIntentData videoData = (VideoIntentData) mAdapter.getItem(0);
                videoData.viewNum = data.num;
                mAdapter.setData(0, videoData);
            }
        });
    }

    private void getData() {
        APIHelper.getInstance().getCommentList(3, videoData.videoId, nextPageIndex, new CallBack<CommentResp>() {
            @Override
            public void onSuccess(CommentResp data) {
                if (isFinishing()) {
                    return;
                }
                //下拉刷新结束
                if (nextPageIndex == 1) {
                    srlContent.refreshComplete();
                }
                if (data != null) {
                    onDataLoad(data);
                } else if (nextPageIndex == 1) {//第一页为空 显示无数据
//                    srlContent.setState(SmoothRefreshLayout.STATE_EMPTY);//空布局
                }
                nextPageIndex++;
            }

            @Override
            public void onFailure(BaseReslut data) {
                if (isFinishing()) {
                    return;
                }
                if (nextPageIndex > 1) {
                    //上拉加载
                    mAdapter.loadMoreFail();
                } else {
                    //下拉刷新
                    srlContent.refreshComplete();
//                    srlContent.setState(SmoothRefreshLayout.STATE_ERROR);//显示错误布局
                }
                mAdapter.setEnableLoadMore(true);
                super.onFailure(data);
            }
        });
    }

    /**
     * 显示数据
     */
    private void onDataLoad(CommentResp data) {
        List<IMultiItemType> list = new ArrayList<>();
        if (data.userCommentItems != null) {
            if (nextPageIndex > 1) {
                mAdapter.loadMoreComplete();
                //上拉加载添加数据
                list.addAll(data.userCommentItems);
                mAdapter.addData(list);
                if (data.userCommentItems.size() < 20) {//是否还有分页
                    mAdapter.loadMoreEnd(true);
                }
            } else {
                //下拉刷新重新加载数据
                list.addAll(data.userCommentItems);
                list.add(0, videoData);
                srlContent.setState(SmoothRefreshLayout.STATE_CONTENT);
                mAdapter.setNewData(list);
                if (data.userCommentItems.size() < 20) {//是否还有分页 setNewData之后调用loadMoreComplete()是无效的，会自动默认为还能继续加载
                    mAdapter.loadMoreEnd(true);//不能用false，否则滚动到底部时还会触发上拉加载
                    mAdapter.setEnableLoadMore(false);
                }
            }
        } else if (nextPageIndex == 1) {//第一页为空 显示无数据
            mAdapter.loadMoreEnd(true);
//            srlContent.setState(SmoothRefreshLayout.STATE_EMPTY);//无数据
        } else {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }

    @OnClick({R.id.bt_tv_send, R.id.iv_player_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_player_back:
                JZVideoPlayer.backPress();
                setReslutData();
                finish();
                break;
            case R.id.bt_tv_send:
                String comment = etTvInput.getText().toString().trim();
                if (reCommentBean == null) {//评论
                    APIHelper.getInstance().addComment(3, videoData.videoId, comment, 0, new CallBack<AddCommentResp>() {
                        @Override
                        public void onSuccess(AddCommentResp data) {
                            etTvInput.setText("");
                            CommentResp.UserCommentItemsBean bean = new CommentResp.UserCommentItemsBean();
                            bean.content = comment;
                            bean.userNickName = App.getApp().getUser().nickName;
                            bean.userPicUrl = App.getApp().getUser().avatar;
                            bean.releaseTime = System.currentTimeMillis();
                            mAdapter.getData().add(1, bean);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                } else {//回复
                    APIHelper.getInstance().addComment(3, videoData.videoId, comment, reCommentBean.userCommentId, new CallBack<AddCommentResp>() {
                        @Override
                        public void onSuccess(AddCommentResp data) {
                            CommentResp.UserCommentItemsBean bean = new CommentResp.UserCommentItemsBean();
                            bean.content = comment;
                            bean.userNickName = App.getApp().getUser().nickName;
                            bean.userPicUrl = App.getApp().getUser().avatar;
                            bean.releaseTime = System.currentTimeMillis();
                            bean.parentNickName = reCommentBean.userNickName;
                            bean.parentContent = reCommentBean.content;
                            bean.userCommentId = data.userCommentId;
                            mAdapter.getData().add(1, bean);
                            mAdapter.notifyDataSetChanged();
                            reCommentBean = null;
                            etTvInput.setText("");
                            etTvInput.setHint("");
                        }
                    });
                }
                hideKeybord(etTvInput);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        etTvInput.removeTextChangedListener(watcher);
        watcher = null;
        super.onDestroy();
        hideKeybord(etTvInput);
        mPaypal.stopPayPalService(this);
    }

    /**
     * 设置回传数据
     */
    private void setReslutData() {
        VideoIntentData titleData = (VideoIntentData) mAdapter.getItem(0);
        DetailsReturnData data = new DetailsReturnData();
        data.position = titleData.position;
        data.commentNum = titleData.viewNum;
        data.praise = titleData.praise;
        data.shareNum = titleData.shareNum;
        data.praiseNum = titleData.praiseNum;
        data.paid = titleData.paid;
        Intent intent = new Intent(mContext, DynamicActivity.class);
        intent.putExtra(DynamicActivity.DETAILS_DATA, data);
        setResult(1, intent);
        EventBus.getDefault().post(data);//通知个人中心视频订单刷新
    }

    @Override
    public void onBackPressed() {
        setReslutData();
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

}
