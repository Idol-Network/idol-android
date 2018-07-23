package com.yiju.ldol.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.gyf.barlibrary.OnKeyboardListener;
import com.yiju.ldol.base.App;
import com.yiju.ldol.base.BaseImmersionActivity;
import com.yiju.ldol.bean.DetailsReturnData;
import com.yiju.ldol.bean.DynamicVideoDetails;
import com.yiju.ldol.bean.response.AddCommentResp;
import com.yiju.ldol.bean.response.CommentResp;
import com.yiju.ldol.bean.response.DynamicListResp;
import com.yiju.ldol.bean.response.UserPraiseResp;
import com.yiju.ldol.listener.OnShareClickListener;
import com.yiju.ldol.ui.view.refresh.CustomHeader;
import com.yiju.ldol.utils.DialogUtils;

import java.util.ArrayList;

import butterknife.BindView;


/**
 * Created by thbpc on 2018/3/19 0019.
 */

public class DynamicVideoActivity extends BaseImmersionActivity {
    public static String VIDEO_DATA = "video_data";
    public static String VIDEO_DETAILS = "video_details";

    @BindView(R.id.rv_dynamic_comment)
    RecyclerView rvDynamicComment;
    @BindView(R.id.et_dynamic_input)
    EditText etDynamicInput;
    @BindView(R.id.rl_main)
    RelativeLayout rlMain;
    @BindView(R.id.bt_dynamic_send)
    Button btDynamicSend;
    @BindView(R.id.iv_dynamic_back)
    ImageView ivDynamicBack;
    @BindView(R.id.rl_dynamic_title)
    RelativeLayout rlDynamicTitle;
    @BindView(R.id.srl_content)
    SmoothRefreshLayout srlContent;

    private DynamicListResp.DynamicItemsBean dynamicItemsBean;
    private DynamicVideoAdpter mAdapter;

    private CommentResp.UserCommentItemsBean reCommentBean;
    private ArrayList<String> picUrls = new ArrayList<>();


    private int nextPageIndex = 1;
    private DynamicVideoDetails mDetails;
    private TextWatcher watcher;


    @Override
    public int getLayout() {
        return R.layout.ac_dynamic_image;
    }

    @Override
    public void initView() {
        mImmersionBar.titleBarMarginTop(rlDynamicTitle).keyboardEnable(true).setOnKeyboardListener(new OnKeyboardListener() {
            @Override
            public void onKeyboardChange(boolean isPopup, int keyboardHeight) {
                if (isPopup) {//显示
                    etDynamicInput.setMaxLines(3);
                } else {//关闭
                    etDynamicInput.setMaxLines(1);
                    if (TextUtils.isEmpty(etDynamicInput.getText().toString())) {
                        reCommentBean = null;
                        etDynamicInput.setHint("");
                    }
                }
            }
        }).init();

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
                    btDynamicSend.setVisibility(View.GONE);
                } else {
                    btDynamicSend.setVisibility(View.VISIBLE);
                }
            }
        };
        etDynamicInput.addTextChangedListener(watcher);

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

        mAdapter = new DynamicVideoAdpter(null);
        rvDynamicComment.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(() -> {
            //上拉加载
            getData();
        }, rvDynamicComment);
        srlContent.setOnChildNotYetInEdgeCannotMoveHeaderCallBack((parent, child, header) -> ScrollCompat.canChildScrollUp(rvDynamicComment));
        srlContent.setOnChildNotYetInEdgeCannotMoveFooterCallBack((parent, child, footer) -> ScrollCompat.canChildScrollDown(rvDynamicComment));
        srlContent.setLoadMoreScrollTargetView(rvDynamicComment);
        rvDynamicComment.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        rvDynamicComment.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                IMultiItemType item = mAdapter.getItem(position);
                int itemType = item.getItemType();
                switch (itemType) {
                    case IMultiItemType.TYPE_DYNAMIC_IMAGE://头部
                        break;
                    case IMultiItemType.TYPE_COMMENT: //评论
                    case IMultiItemType.TYPE_RECOMMENT: //回复
                        reCommentBean = (CommentResp.UserCommentItemsBean) item;
                        etDynamicInput.setHint("@" + reCommentBean.userNickName);
                        etDynamicInput.setSelection(etDynamicInput.length());
                        showKeyboard(etDynamicInput);
                        break;
                }
            }
        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.sim_dynamic_pic:
                        break;
                    case R.id.tv_dynamic_shareNum:
                        DialogUtils.showShareDialog(DynamicVideoActivity.this, new OnShareClickListener() {
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
                    case R.id.tv_dynamic_likeNum:
                        DynamicVideoDetails item = (DynamicVideoDetails) mAdapter.getItem(position);
                        if (item.praise == 1) {//0-否 1-是
                            APIHelper.getInstance().praiseCancel(1, item.dynamicId, new CallBack<UserPraiseResp>() {
                                @Override
                                public void onSuccess(UserPraiseResp data) {
                                    item.praise = 0;
                                    item.praiseNum = data.num;
                                    mAdapter.setData(position, item);
                                }
                            });
                        } else {
                            APIHelper.getInstance().praise(1, item.dynamicId, new CallBack<UserPraiseResp>() {
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

        rvDynamicComment.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                JZVideoPlayer jzvd = view.findViewById(R.id.play_dynamic);
                if (jzvd != null && JZUtils.dataSourceObjectsContainsUri(jzvd.dataSourceObjects, JZMediaManager.getCurrentDataSource())) {
                    JZVideoPlayer currentJzvd = JZVideoPlayerManager.getCurrentJzvd();
                    if (currentJzvd != null && currentJzvd.currentScreen != JZVideoPlayer.SCREEN_WINDOW_FULLSCREEN) {
                        JZVideoPlayer.releaseAllVideos();
                    }
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        etDynamicInput.removeTextChangedListener(watcher);
        watcher = null;
        super.onDestroy();
        hideKeybord(etDynamicInput);
    }

    /**
     * 设置回传数据
     */
    private void setReslutData() {
        DynamicVideoDetails details = (DynamicVideoDetails) mAdapter.getItem(1);
        DetailsReturnData data = new DetailsReturnData();
        data.commentNum = details.commentNum;
        data.praise = details.praise;
        data.shareNum = details.shareNum;
        data.praiseNum = details.praiseNum;
        Intent intent = new Intent(mContext, DynamicActivity.class);
        intent.putExtra(DynamicActivity.DETAILS_DATA, data);
        setResult(3, intent);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        dynamicItemsBean = (DynamicListResp.DynamicItemsBean) getIntent().getSerializableExtra(VIDEO_DATA);
        mDetails = (DynamicVideoDetails) getIntent().getSerializableExtra(VIDEO_DETAILS);
        if (dynamicItemsBean == null || mDetails == null) {
            finish();
            return;
        }
        ArrayList<IMultiItemType> list = new ArrayList<>();
        list.add(0, dynamicItemsBean);
        list.add(1, mDetails);
        mAdapter.setNewData(list);
        picUrls.clear();
        for (DynamicListResp.DynamicItemsBean.ContentBean contentBean : dynamicItemsBean.content) {
            picUrls.add(contentBean.url);
        }
    }

    private void getData() {
        APIHelper.getInstance().getCommentList(1, dynamicItemsBean.dynamicId, nextPageIndex, new CallBack<CommentResp>() {
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
                //上拉加载添加数据
                list.addAll(data.userCommentItems);
                mAdapter.addData(list);
                if (data.userCommentItems.size() < 20) {//是否还有分页
                    mAdapter.loadMoreEnd(true);
                    mAdapter.loadMoreComplete();
                } else {
                    mAdapter.loadMoreComplete();
                }
            } else {
                //下拉刷新重新加载数据
                list.addAll(data.userCommentItems);
                list.add(0, dynamicItemsBean);
                list.add(1, mDetails);
                srlContent.setState(SmoothRefreshLayout.STATE_CONTENT);
                mAdapter.setNewData(list);
                if (data.userCommentItems.size() < 20) {//是否还有分页 setNewData之后调用loadMoreComplete()是无效的，会自动默认为还能继续加载
                    mAdapter.loadMoreEnd(true);//不能用false，否则滚动到底部时还会触发上拉加载
                    mAdapter.setEnableLoadMore(false);
                }
//                mAdapter.disableLoadMoreIfNotFullPage(rvDynamicComment);//不满页触发加载更多 默认第一次加载会进入回调，如果不需要可以配置
            }
        } else if (nextPageIndex == 1) {//第一页为空 显示无数据
            mAdapter.loadMoreEnd(true);
//            srlContent.setState(SmoothRefreshLayout.STATE_EMPTY);//无数据
        } else {

        }
    }

    @OnClick({R.id.iv_dynamic_back, R.id.bt_dynamic_send})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_dynamic_back:
                setReslutData();
                finish();
                break;
            case R.id.bt_dynamic_send:
                String comment = etDynamicInput.getText().toString().trim();
                if (reCommentBean == null) {//评论
                    APIHelper.getInstance().addComment(1, dynamicItemsBean.dynamicId, comment, 0, new CallBack<AddCommentResp>() {
                        @Override
                        public void onSuccess(AddCommentResp data) {
                            etDynamicInput.setText("");
                            //更新评论数量 ，位置1
                            mDetails.commentNum = data.num;
                            mAdapter.setData(1, mDetails);
                            CommentResp.UserCommentItemsBean bean = new CommentResp.UserCommentItemsBean();
                            bean.content = comment;
                            bean.userNickName = App.getApp().getUser().nickName;
                            bean.userPicUrl = App.getApp().getUser().avatar;
                            bean.releaseTime = System.currentTimeMillis();
                            //更新评论 ，位置2
                            mAdapter.getData().add(2, bean);
                            mAdapter.notifyDataSetChanged();
                        }
                    });
                } else {//回复
                    APIHelper.getInstance().addComment(1, dynamicItemsBean.dynamicId, comment, reCommentBean.userCommentId, new CallBack<AddCommentResp>() {
                        @Override
                        public void onSuccess(AddCommentResp data) {
                            //更新评论数量 ，位置1
                            mDetails.commentNum = data.num;
                            mAdapter.setData(1, mDetails);
                            CommentResp.UserCommentItemsBean bean = new CommentResp.UserCommentItemsBean();
                            bean.content = comment;
                            bean.userNickName = App.getApp().getUser().nickName;
                            bean.userPicUrl = App.getApp().getUser().avatar;
                            bean.releaseTime = System.currentTimeMillis();
                            bean.parentNickName = reCommentBean.userNickName;
                            bean.parentContent = reCommentBean.content;
                            bean.userCommentId = data.userCommentId;
                            //更新评论 ，位置2
                            mAdapter.getData().add(2, bean);
                            mAdapter.notifyDataSetChanged();
                            reCommentBean = null;
                            etDynamicInput.setText("");
                            etDynamicInput.setHint("");
                        }
                    });
                }
                hideKeybord(etDynamicInput);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        setReslutData();
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }

}
