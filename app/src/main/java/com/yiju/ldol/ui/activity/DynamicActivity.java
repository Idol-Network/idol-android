package com.yiju.ldol.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.yiju.ldol.base.BaseImmersionActivity;
import com.yiju.ldol.bean.DetailsReturnData;
import com.yiju.ldol.bean.DynamicVideoDetails;
import com.yiju.ldol.bean.response.DynamicListResp;
import com.yiju.ldol.bean.response.UserPraiseResp;
import com.yiju.ldol.listener.OnShareClickListener;
import com.yiju.ldol.ui.view.refresh.CustomHeader;
import com.yiju.ldol.utils.DialogUtils;

import butterknife.BindView;


/**
 * 动态
 * Created by thbpc on 2018/3/19 0019.
 */

public class DynamicActivity extends BaseImmersionActivity {

    public static final String PERSON_ID = "person_Id";
    public static final String DETAILS_DATA = "details_data";

    @BindView(R.id.rv_dynamic)
    RecyclerView rvDynamic;
    @BindView(R.id.srl_content)
    SmoothRefreshLayout srlContent;

    private DynamicAdapter mAdapter;
    private int nextPageIndex = 1;
    private int personId;
    private LinearLayoutManager layoutManager;

    @Override
    public int getLayout() {
        return R.layout.ac_dynamic;
    }

    @Override
    public void initView() {
//        mImmersionBar.titleBarMarginTop(rlDynamicTitle);


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

        mAdapter = new DynamicAdapter(null);
        mAdapter.setOnLoadMoreListener(() -> {
            //上拉加载
            getData();
        }, rvDynamic);
        srlContent.setOnChildNotYetInEdgeCannotMoveHeaderCallBack((parent, child, header) -> ScrollCompat.canChildScrollUp(rvDynamic));
        srlContent.setOnChildNotYetInEdgeCannotMoveFooterCallBack((parent, child, footer) -> ScrollCompat.canChildScrollDown(rvDynamic));
        srlContent.setLoadMoreScrollTargetView(rvDynamic);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvDynamic.setLayoutManager(layoutManager);


        rvDynamic.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                DynamicListResp.DynamicItemsBean item = mAdapter.getItem(position);
                int itemType = item.getItemType();
                switch (itemType) {
                    case IMultiItemType.TYPE_DYNAMIC_IMAGE: {
                        Intent intent = new Intent(mContext, DynamicImageActivity.class);
                        intent.putExtra(DynamicImageActivity.PIC_DATA, item);
                        startActivityForResult(intent, position);
                        break;
                    }
                    case IMultiItemType.TYPE_DYNAMIC_TEXT: {
                        Intent intent = new Intent(mContext, DynamicTextActivity.class);
                        intent.putExtra(DynamicTextActivity.TEXT_DATA, item);
                        startActivityForResult(intent, position);
                        break;
                    }
                    case IMultiItemType.TYPE_DYNAMIC_VIDEO: {
                        DynamicVideoDetails details = new DynamicVideoDetails();
                        details.commentNum = item.commentNum;
                        details.praise = item.praise;
                        details.dynamicId = item.dynamicId;
                        details.praiseNum = item.praiseNum;
                        details.shareNum = item.shareNum;
                        Intent intent = new Intent(mContext, DynamicVideoActivity.class);
                        intent.putExtra(DynamicVideoActivity.VIDEO_DATA, item);
                        intent.putExtra(DynamicVideoActivity.VIDEO_DETAILS, details);
                        startActivityForResult(intent, position);
                        break;
                    }
                }
            }
        });
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                DynamicListResp.DynamicItemsBean item = mAdapter.getItem(position);
                switch (view.getId()) {
                    case R.id.tv_like_Num:
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
                    case R.id.tv_comment_Num:

                        break;
                    case R.id.tv_share_Num:
                        DialogUtils.showShareDialog(DynamicActivity.this, new OnShareClickListener() {
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
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if (requestCode == 3) {//DynamicVideoActivity
            DynamicVideoDetails details = (DynamicVideoDetails) data.getSerializableExtra(DETAILS_DATA);
            DynamicListResp.DynamicItemsBean item = mAdapter.getItem(requestCode);
            item.commentNum = details.commentNum;
            item.praiseNum = details.praiseNum;
            item.praise = details.praise;
            item.shareNum = details.shareNum;
        } else {
            DetailsReturnData details = (DetailsReturnData) data.getSerializableExtra(DETAILS_DATA);
            DynamicListResp.DynamicItemsBean item = mAdapter.getItem(requestCode);
            item.commentNum = details.commentNum;
            item.praiseNum = details.praiseNum;
            item.praise = details.praise;
            item.shareNum = details.shareNum;
        }
        mAdapter.notifyItemChanged(requestCode);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        personId = getIntent().getIntExtra(PERSON_ID, 5);
        getData();
    }

    private void getData() {
        APIHelper.getInstance().getDynamicList(personId, nextPageIndex, new CallBack<DynamicListResp>() {
            @Override
            public void onSuccess(DynamicListResp data) {
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
                    srlContent.setState(SmoothRefreshLayout.STATE_EMPTY);//空布局
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
    private void onDataLoad(DynamicListResp data) {
        if (data.dynamicItems != null) {
            if (nextPageIndex > 1) {
                //上拉加载添加数据
                mAdapter.addData(data.dynamicItems);
                //TODO 必须放在设置数据之后才能生效
                if (data.dynamicItems.size() < 20) {//是否还有分页
                    mAdapter.loadMoreEnd(true);
                }
                mAdapter.loadMoreComplete();
            } else {
                //下拉刷新重新加载数据
                mAdapter.setNewData(data.dynamicItems);
                srlContent.setState(SmoothRefreshLayout.STATE_CONTENT);
                if (data.dynamicItems.size() < 20) {//是否还有分页 setNewData之后调用loadMoreComplete()是无效的，会自动默认为还能继续加载
                    mAdapter.loadMoreEnd(true);//不能用false，否则滚动到底部时还会触发上拉加载
                    mAdapter.setEnableLoadMore(false);
                }
                mAdapter.disableLoadMoreIfNotFullPage(rvDynamic);//不满页触发加载更多 默认第一次加载会进入回调，如果不需要可以配置
            }
        } else if (nextPageIndex == 1) {//第一页为空 显示无数据
            srlContent.setState(SmoothRefreshLayout.STATE_EMPTY);//无数据
        }
    }
}
