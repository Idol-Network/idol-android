package com.yiju.ldol.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yiju.idol.R;
import com.yiju.idol.api.APIHelper;
import com.yiju.idol.api.BaseReslut;
import com.yiju.idol.api.CallBack;
import com.yiju.idol.base.BaseFragment;
import com.yiju.idol.bean.response.GoodsListResp;
import com.yiju.idol.ui.activity.GoodsDetailsActivity;
import com.yiju.idol.ui.activity.UserDetailsActivity;
import com.yiju.idol.ui.adapter.AroundAdapter;
import com.yiju.idol.ui.view.refresh.CustomHeader;

import butterknife.BindView;
import me.dkzwm.widget.srl.RefreshingListenerAdapter;
import me.dkzwm.widget.srl.SmoothRefreshLayout;
import me.dkzwm.widget.srl.extra.IRefreshView;
import me.dkzwm.widget.srl.utils.ScrollCompat;

public class AroundFragment extends BaseFragment {

    @BindView(R.id.rv_shop)
    RecyclerView rvShop;
    @BindView(R.id.srl_content)
    SmoothRefreshLayout srlContent;

    private AroundAdapter mAdapter;
    private int nextPageIndex = 1;
    private int userId;

    @Override
    public int getLayout() {
        return R.layout.ft_around;
    }


    @Override
    public void OnActCreate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userId = getArguments().getInt(UserDetailsActivity.USER_ID, -1);
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

        mAdapter = new AroundAdapter();
        mAdapter.setOnLoadMoreListener(() -> {
            //上拉加载
            getData();
        }, rvShop);
        srlContent.setOnChildNotYetInEdgeCannotMoveHeaderCallBack((parent, child, header) -> ScrollCompat.canChildScrollUp(rvShop));
        srlContent.setOnChildNotYetInEdgeCannotMoveFooterCallBack((parent, child, footer) -> ScrollCompat.canChildScrollDown(rvShop));
        srlContent.setLoadMoreScrollTargetView(rvShop);
        rvShop.setLayoutManager(new GridLayoutManager(mContext, 2));

        rvShop.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GoodsListResp.GoodsItemsBean bean = (GoodsListResp.GoodsItemsBean) adapter.getItem(position);
                Intent intent = new Intent(mContext, GoodsDetailsActivity.class);
                intent.putExtra(GoodsDetailsActivity.IMAGE_URL, bean.picUrl);
                intent.putExtra(GoodsDetailsActivity.GOODS_ID, bean.goodsId);
                startActivity(intent);
            }
        });
    }

    private void getData() {//
        APIHelper.getInstance().getGoodsList(userId, nextPageIndex, new CallBack<GoodsListResp>() {
            @Override
            public void onSuccess(GoodsListResp data) {
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
    private void onDataLoad(GoodsListResp data) {
        if (data.goodsItems != null) {
            if (nextPageIndex > 1) {
                //上拉加载添加数据
                mAdapter.addData(data.goodsItems);
                //TODO 必须放在设置数据之后才能生效
                if (data.goodsItems.size() < 20) {//是否还有分页
                    mAdapter.loadMoreEnd(true);
                } else {
                    mAdapter.loadMoreComplete();
                }
            } else {
                //下拉刷新重新加载数据
                srlContent.setState(SmoothRefreshLayout.STATE_CONTENT);
                mAdapter.setNewData(data.goodsItems);
                if (data.goodsItems.size() < 20) {//是否还有分页 setNewData之后调用loadMoreComplete()是无效的，会自动默认为还能继续加载
                    mAdapter.loadMoreEnd(true);//不能用false，否则滚动到底部时还会触发上拉加载
                    mAdapter.setEnableLoadMore(false);
                }
                mAdapter.disableLoadMoreIfNotFullPage(rvShop);//不满页触发加载更多 默认第一次加载会进入回调，如果不需要可以配置
            }
        } else if (nextPageIndex == 1) {//第一页为空 显示无数据
            srlContent.setState(SmoothRefreshLayout.STATE_EMPTY);//无数据
        }
    }

    @Override
    public void onClick(View v) {

    }
}
