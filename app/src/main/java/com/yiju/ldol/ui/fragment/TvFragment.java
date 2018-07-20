package com.yiju.ldol.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.yiju.idol.R;
import com.yiju.idol.api.APIHelper;
import com.yiju.idol.api.BaseReslut;
import com.yiju.idol.api.CallBack;
import com.yiju.idol.base.BaseFragment;
import com.yiju.idol.base.Constant;
import com.yiju.idol.base.entity.IMultiItemType;
import com.yiju.idol.bean.DetailsReturnData;
import com.yiju.idol.bean.VideoIntentData;
import com.yiju.idol.bean.response.UserPraiseResp;
import com.yiju.idol.bean.response.VideoListResp;
import com.yiju.idol.listener.JPermissionListener;
import com.yiju.idol.listener.OnShareClickListener;
import com.yiju.idol.ui.activity.AVActivity;
import com.yiju.idol.ui.activity.DynamicActivity;
import com.yiju.idol.ui.activity.TVDetailsActivity;
import com.yiju.idol.ui.activity.UserDetailsActivity;
import com.yiju.idol.ui.adapter.TVAdapter;
import com.yiju.idol.ui.view.refresh.CustomHeader;
import com.yiju.idol.utils.DialogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import me.dkzwm.widget.srl.RefreshingListenerAdapter;
import me.dkzwm.widget.srl.SmoothRefreshLayout;
import me.dkzwm.widget.srl.extra.IRefreshView;
import me.dkzwm.widget.srl.utils.ScrollCompat;

public class TvFragment extends BaseFragment {

    @BindView(R.id.rv_tv)
    RecyclerView rvTv;
    @BindView(R.id.srl_content)
    SmoothRefreshLayout srlContent;

    private TVAdapter mAdapter;
    private int nextPageIndex = 1;
    private int personId;

    @Override
    public int getLayout() {
        return R.layout.ac_tvactivity;
    }


    @Override
    public void OnActCreate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        personId = getArguments().getInt(UserDetailsActivity.PERSON_ID, -1);
        getData();
    }

    @Override
    public void initView(View view) {
//        mImmersionBar.titleBarMarginTop(rlTvBack);
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

        mAdapter = new TVAdapter(null);
        mAdapter.setOnLoadMoreListener(() -> {
            //上拉加载
            getData();
        }, rvTv);
        srlContent.setOnChildNotYetInEdgeCannotMoveHeaderCallBack((parent, child, header) -> ScrollCompat.canChildScrollUp(rvTv));
        srlContent.setOnChildNotYetInEdgeCannotMoveFooterCallBack((parent, child, footer) -> ScrollCompat.canChildScrollDown(rvTv));
        srlContent.setLoadMoreScrollTargetView(rvTv);
        rvTv.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));


        rvTv.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                IMultiItemType item = (IMultiItemType) adapter.getItem(position);
                switch (item.getItemType()) {
                    case IMultiItemType.TYPE_VIDEO_LIVE:
                        VideoListResp.UserLiveItemBean live = (VideoListResp.UserLiveItemBean) item;
                        requestPermission(new JPermissionListener(getActivity(), 1) {
                            @Override
                            public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                                Intent intent = new Intent(getActivity(), AVActivity.class);
                                intent.putExtra("type", live.liveModal);
                                intent.putExtra(Constant.JPUSH_ID, live.liveId);
                                startActivity(intent);
                            }
                        }, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        break;
                    case IMultiItemType.TYPE_VIDEO_REPLAY:
                        VideoListResp.VideoItemsBean video = (VideoListResp.VideoItemsBean) item;
                        VideoIntentData data = new VideoIntentData();
                        data.praise = video.praise;
                        data.praiseNum = video.praiseNum;
                        data.shareNum = video.shareNum;
                        data.videoId = video.videoId;
                        data.videoUrl = video.playUrl;
                        data.picUrl = video.picUrl;
                        data.summary = video.summary;
                        data.viewNum = video.viewNum;
                        data.fee = video.fee;
                        data.paid = video.paid;
                        Intent intent = new Intent(mContext, TVDetailsActivity.class);
                        intent.putExtra(TVDetailsActivity.VIDEO_DATA, data);
                        startActivityForResult(intent, position);
                        break;
                }
            }
        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {

                switch (view.getId()) {
                    case R.id.tv_video_likeNum:
                        IMultiItemType item = (IMultiItemType) adapter.getItem(position);
                        switch (item.getItemType()) {
                            case IMultiItemType.TYPE_VIDEO_LIVE:
                                break;
                            case IMultiItemType.TYPE_VIDEO_REPLAY:
                                VideoListResp.VideoItemsBean video = (VideoListResp.VideoItemsBean) item;
                                if (video.praise == 1) {//0-否 1-是
                                    APIHelper.getInstance().praiseCancel(3, video.videoId, new CallBack<UserPraiseResp>() {
                                        @Override
                                        public void onSuccess(UserPraiseResp data) {
                                            video.praise = 0;
                                            video.praiseNum = data.num;
                                            mAdapter.setData(position, item);
                                        }
                                    });
                                } else {
                                    APIHelper.getInstance().praise(3, video.videoId, new CallBack<UserPraiseResp>() {
                                        @Override
                                        public void onSuccess(UserPraiseResp data) {
                                            video.praise = 1;
                                            video.praiseNum = data.num;
                                            mAdapter.setData(position, item);
                                        }
                                    });
                                }
                                break;
                        }
                        break;
                    case R.id.tv_video_commentNum:

                        break;
                    case R.id.tv_video_sharetNum:
                        DialogUtils.showShareDialog(mContext, new OnShareClickListener() {
                            @Override
                            public void weixin() {

                            }

                            @Override
                            public void sina() {

                            }

                            @Override
                            public void facebook() {
                                shareData(SHARE_MEDIA.FACEBOOK, "Title", "contentText", "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1531398979509&di=dc006f12d62a922ed28d9a8f1f1feb14&imgtype=0&src=http%3A%2F%2Fp2.ifengimg.com%2Fa%2F2018_27%2F72730aef04d07e5.jpg", "https://www.baidu.com");
                            }

                            @Override
                            public void twitter() {
                                shareTwitter("Title", "contentText", "https://platform-lookaside.fbsbx.com/platform/profilepic/?asid=105564397033892&height=200&width=200&ext=1531648991&hash=AeQO6nciy_Rjrcf9", "https://www.google.com");
                            }

                            @Override
                            public void copy() {

                            }
                        });
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        DetailsReturnData details = (DetailsReturnData) data.getSerializableExtra(DynamicActivity.DETAILS_DATA);
        IMultiItemType item = mAdapter.getItem(requestCode);
        if (item != null) {
            switch (item.getItemType()) {
                case IMultiItemType.TYPE_VIDEO_LIVE:
                    break;
                case IMultiItemType.TYPE_VIDEO_REPLAY:
                    VideoListResp.VideoItemsBean video = (VideoListResp.VideoItemsBean) item;
                    video.viewNum = details.commentNum;
                    video.praiseNum = details.praiseNum;
                    video.praise = details.praise;
                    video.shareNum = details.shareNum;
                    //返回付费情况
                    VideoListResp.VideoItemsBean videoItemsBean = (VideoListResp.VideoItemsBean) mAdapter.getItem(requestCode);
                    videoItemsBean.paid = details.paid;
                    break;


            }
            mAdapter.notifyItemChanged(requestCode);
        }
    }


    private void getData() {
        APIHelper.getInstance().getVideoList(personId, nextPageIndex, new CallBack<VideoListResp>() {
            @Override
            public void onSuccess(VideoListResp data) {
                if (mContext == null || mContext.isFinishing()) {
                    return;
                }
                //下拉刷新结束
                if (nextPageIndex == 1) {
                    srlContent.refreshComplete();
                }
                if (data != null) {
                    onDataLoad(data);
                } else if (nextPageIndex == 1) {//第一页为空 显示无数据
                    srlContent.setState(SmoothRefreshLayout.STATE_EMPTY);//空布局
                }
                nextPageIndex++;
            }

            @Override
            public void onFailure(BaseReslut data) {
                if (mContext == null || mContext.isFinishing()) {
                    return;
                }
                if (nextPageIndex > 1) {
                    //上拉加载
                    mAdapter.loadMoreFail();
                } else {
                    //下拉刷新
                    srlContent.refreshComplete();
                    srlContent.setState(SmoothRefreshLayout.STATE_ERROR);//显示错误布局
                }
                mAdapter.setEnableLoadMore(true);
                super.onFailure(data);
            }
        });
    }

    /**
     * 显示数据
     */
    private void onDataLoad(VideoListResp data) {
        if (data.videoItems != null) {
            if (nextPageIndex > 1) {
                //上拉加载添加数据
                mAdapter.addData(data.videoItems);
                //TODO 必须放在设置数据之后才能生效
                if (data.videoItems.size() < 20) {//是否还有分页
                    mAdapter.loadMoreEnd(true);
                } else {
                    mAdapter.loadMoreComplete();
                }
            } else {
                //下拉刷新重新加载数据
                srlContent.setState(SmoothRefreshLayout.STATE_CONTENT);
                List<IMultiItemType> dataList = new ArrayList<>();
                if (data.userLiveItem != null) {
                    dataList.add(data.userLiveItem);
                }
                if (data.videoItems != null) {
                    dataList.addAll(data.videoItems);
                }
                mAdapter.setNewData(dataList);
                if (data.videoItems.size() < 20) {//是否还有分页 setNewData之后调用loadMoreComplete()是无效的，会自动默认为还能继续加载
                    mAdapter.loadMoreEnd(true);//不能用false，否则滚动到底部时还会触发上拉加载
                    mAdapter.setEnableLoadMore(false);
                }
                mAdapter.disableLoadMoreIfNotFullPage(rvTv);//不满页触发加载更多 默认第一次加载会进入回调，如果不需要可以配置
            }
        } else if (nextPageIndex == 1) {//第一页为空 显示无数据
            srlContent.setState(SmoothRefreshLayout.STATE_EMPTY);//无数据
        }
    }

    @Override
    public void onClick(View v) {

    }

}
