package com.yiju.ldol.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yiju.idol.R;
import com.yiju.idol.api.APIHelper;
import com.yiju.idol.api.BaseReslut;
import com.yiju.idol.api.CallBack;
import com.yiju.idol.base.BaseFragment;
import com.yiju.idol.base.entity.IMultiItemType;
import com.yiju.idol.bean.DetailsReturnData;
import com.yiju.idol.bean.VideoIntentData;
import com.yiju.idol.bean.response.BuyerListResp;
import com.yiju.idol.bean.response.PurchasingOrderTitle;
import com.yiju.idol.ui.activity.TVDetailsActivity;
import com.yiju.idol.ui.adapter.PurchasingTVAdapter;
import com.yiju.idol.ui.view.refresh.CustomHeader;

import java.util.ArrayList;

import butterknife.BindView;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;
import me.dkzwm.widget.srl.RefreshingListenerAdapter;
import me.dkzwm.widget.srl.SmoothRefreshLayout;
import me.dkzwm.widget.srl.extra.IRefreshView;
import me.dkzwm.widget.srl.utils.ScrollCompat;

/**
 * Created by Allan_Zhang on 2018/6/26.
 */

public class PurchasingTVFt extends BaseFragment {

    @BindView(R.id.rv_purchasing)
    RecyclerView rvPurchasing;
    @BindView(R.id.srl_content)
    SmoothRefreshLayout srlContent;

    private PurchasingTVAdapter mAdapter;
    private Handler mHandler = new Handler();
    private int nextPageIndex = 1;
    private boolean isNetWork;//是否正在请求数据
    private boolean initFlag;//是否已加载过数据

    @Override
    public int getLayout() {
        return R.layout.ft_purchasing;
    }

    @Override
    public void OnActCreate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void initView(View view) {
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

        mAdapter = new PurchasingTVAdapter(null);
        mAdapter.setOnLoadMoreListener(() -> {
            //上拉加载
            nextPageIndex++;
            getData();
        }, rvPurchasing);
        srlContent.setOnChildNotYetInEdgeCannotMoveHeaderCallBack((parent, child, header) -> ScrollCompat.canChildScrollUp(rvPurchasing));
        srlContent.setOnChildNotYetInEdgeCannotMoveFooterCallBack((parent, child, footer) -> ScrollCompat.canChildScrollDown(rvPurchasing));
        srlContent.setLoadMoreScrollTargetView(rvPurchasing);
        rvPurchasing.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvPurchasing.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((adapter, view1, position) -> {
            IMultiItemType item = mAdapter.getItem(position);
            if (item == null) {
                return;
            }
            VideoIntentData data = new VideoIntentData();
            data.position = position;
            switch (item.getItemType()) {
                case IMultiItemType.TYPE_PURCHASING_ORDER_NUM:
                    PurchasingOrderTitle title = (PurchasingOrderTitle) item;
                    BuyerListResp.OrderSkuListBean skuBean = title.getSkuBean();
                    data.praiseNum = skuBean.praiseNum;
                    data.shareNum = skuBean.shareNum;
                    data.videoId = skuBean.goodsId;
                    data.videoUrl = skuBean.playUrl;
                    data.picUrl = skuBean.picUrl;
                    data.summary = skuBean.title;
                    data.viewNum = skuBean.viewNum;
                    break;
                case IMultiItemType.TYPE_PURCHASING_ORDER_ITEM:
                    BuyerListResp.OrderSkuListBean bean = (BuyerListResp.OrderSkuListBean) item;
                    data.praiseNum = bean.praiseNum;
                    data.shareNum = bean.shareNum;
                    data.videoId = bean.goodsId;
                    data.videoUrl = bean.playUrl;
                    data.picUrl = bean.picUrl;
                    data.summary = bean.title;
                    data.viewNum = bean.viewNum;
                    break;
                default:
                    return;
            }
            Intent intent = new Intent(getActivity(), TVDetailsActivity.class);
            intent.putExtra(TVDetailsActivity.VIDEO_DATA, data);
            startActivityForResult(intent, position);
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!initFlag && isVisibleToUser) {
            initFlag = true;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (srlContent != null) {
                        srlContent.autoRefresh();
                    } else {
                        mHandler.postDelayed(this, 100);
                    }
                }
            });
        }
    }

    private void getData() {
        if (isNetWork) {
            return;
        }
        isNetWork = true;
        APIHelper.getInstance().getBuyerList(3, nextPageIndex, new CallBack<BuyerListResp>() {
            @Override
            public void onSuccess(BuyerListResp data) {
                if (isRemoving()) {
                    return;
                }
                isNetWork = false;
                mAdapter.setEnableLoadMore(true);
                if (nextPageIndex == 1) {
                    //下拉刷新结束
                    srlContent.refreshComplete();
                }
                if (data != null) {
                    onDataLoad(data);
                } else if (nextPageIndex == 1) {//第一页为空 显示无数据
                    srlContent.setState(SmoothRefreshLayout.STATE_EMPTY);//空布局
                }
            }

            @Override
            public void onFailure(BaseReslut data) {
                if (isRemoving()) {
                    return;
                }
                isNetWork = false;
                if (nextPageIndex > 1) {
                    //上拉加载
                    mAdapter.loadMoreFail();
                    nextPageIndex -= 1;//下拉失败，下一页id减少1
                } else {
                    //下拉刷新
                    srlContent.refreshComplete();
                    srlContent.setState(SmoothRefreshLayout.STATE_EMPTY);//显示错误布局
                }
                mAdapter.setEnableLoadMore(true);
                super.onFailure(data);
            }
        });
    }

    /**
     * 显示数据
     */
    private void onDataLoad(BuyerListResp data) {
        if (data.goodsOrderList != null && data.goodsOrderList.size() > 0) {
            if (nextPageIndex > 1) {
                //上拉加载添加数据
                ArrayList<IMultiItemType> listData = new ArrayList<>();
                for (BuyerListResp.GoodsOrderListBean bean : data.goodsOrderList) {
                    //添加头部
                    BuyerListResp.OrderSkuListBean skuBean = null;
                    if (bean.orderSkuList != null && !bean.orderSkuList.isEmpty()) {
                        skuBean = bean.orderSkuList.get(0);
                    }
                    listData.add(new PurchasingOrderTitle(bean.orderNumber, bean.createTime, skuBean));
                    //订单内容
                    if (bean.orderSkuList != null) {
                        listData.addAll(bean.orderSkuList);
                    }
                }
                mAdapter.addData(listData);
                //TODO 必须放在设置数据之后才能生效
                if (data.goodsOrderList.size() < 20) {//是否还有分页
                    mAdapter.loadMoreEnd(false);
                } else {
                    mAdapter.loadMoreComplete();
                }
            } else {
                //下拉刷新重新加载数据
                srlContent.setState(SmoothRefreshLayout.STATE_CONTENT);
                ArrayList<IMultiItemType> listData = new ArrayList<>();
                for (BuyerListResp.GoodsOrderListBean bean : data.goodsOrderList) {
                    //添加头部
                    BuyerListResp.OrderSkuListBean skuBean = null;
                    if (bean.orderSkuList != null && !bean.orderSkuList.isEmpty()) {
                        skuBean = bean.orderSkuList.get(0);
                    }
                    listData.add(new PurchasingOrderTitle(bean.orderNumber, bean.createTime, skuBean));
                    //订单内容
                    if (bean.orderSkuList != null) {
                        listData.addAll(bean.orderSkuList);
                    }
                }
                mAdapter.setNewData(listData);
                if (data.goodsOrderList.size() < 20) {//是否还有分页 setNewData之后调用loadMoreComplete()是无效的，会自动默认为还能继续加载
                    mAdapter.loadMoreEnd(true);//不能用false，否则滚动到底部时还会触发上拉加载
                }
                mAdapter.disableLoadMoreIfNotFullPage(rvPurchasing);//不满页触发加载更多 默认第一次加载会进入回调，如果不需要可以配置
            }
        } else if (nextPageIndex == 1) {//第一页为空 显示无数据
            srlContent.setState(SmoothRefreshLayout.STATE_EMPTY);//无数据
        } else {
            mAdapter.loadMoreEnd(false);//加载更多无数据
        }
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(DetailsReturnData eventBean) {//接收消息
        if (mAdapter == null || mAdapter.getData().size() == 0) {
            return;
        }
        IMultiItemType item = mAdapter.getItem(eventBean.position);
        switch (item.getItemType()) {
            case IMultiItemType.TYPE_PURCHASING_ORDER_NUM://若点击的是头部 则更新下一条item
                if (eventBean.position + 1 < mAdapter.getData().size() - 1) {
                    IMultiItemType item2 = mAdapter.getItem(eventBean.position + 1);
                    if (item2.getItemType() == IMultiItemType.TYPE_PURCHASING_ORDER_ITEM) {
                        BuyerListResp.OrderSkuListBean skuBean2 = (BuyerListResp.OrderSkuListBean) item2;
                        skuBean2.commentNum = eventBean.commentNum;
                        skuBean2.praiseNum = eventBean.praiseNum;
                        skuBean2.shareNum = eventBean.shareNum;
                        mAdapter.setData(eventBean.position + 1, skuBean2);
                    }
                }
                break;
            case IMultiItemType.TYPE_PURCHASING_ORDER_ITEM:
                BuyerListResp.OrderSkuListBean skuBean = (BuyerListResp.OrderSkuListBean) item;
                skuBean.commentNum = eventBean.commentNum;
                skuBean.praiseNum = eventBean.praiseNum;
                skuBean.shareNum = eventBean.shareNum;
                mAdapter.setData(eventBean.position, skuBean);
                break;
            default:
                break;
        }

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
