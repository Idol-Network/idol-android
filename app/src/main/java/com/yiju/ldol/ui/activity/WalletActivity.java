package com.yiju.ldol.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.yiju.idol.R;
import com.yiju.idol.api.APIHelper;
import com.yiju.idol.api.BaseReslut;
import com.yiju.idol.api.CallBack;
import com.yiju.idol.base.BaseImmersionActivity;
import com.yiju.idol.base.entity.IMultiItemType;
import com.yiju.idol.bean.response.MyWalletResp;
import com.yiju.idol.ui.adapter.WithdrawalAdapter;
import com.yiju.idol.ui.view.refresh.CustomHeader;
import com.yiju.idol.utils.APKUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import me.dkzwm.widget.srl.RefreshingListenerAdapter;
import me.dkzwm.widget.srl.SmoothRefreshLayout;
import me.dkzwm.widget.srl.extra.IRefreshView;
import me.dkzwm.widget.srl.utils.ScrollCompat;

public class WalletActivity extends BaseImmersionActivity {
    @BindView(R.id.rl_wallet_title)
    RelativeLayout rlWalletTitle;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.srl_content)
    SmoothRefreshLayout srlContent;

    @BindView(R.id.rv_account_details)
    RecyclerView rvAccountDetails;
    private WithdrawalAdapter mAdapter;
    private int nextPageIndex = 1;

    @Override
    public int getLayout() {
        return R.layout.ac_wallet;
    }

    @Override
    public void initView() {
        mImmersionBar.titleBarMarginTop(rlWalletTitle);
        rvAccountDetails.setNestedScrollingEnabled(false);
        rvAccountDetails.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new WithdrawalAdapter(null);
        CustomHeader customHeader = new CustomHeader(mContext);
        customHeader.setStyle(IRefreshView.STYLE_SCALE);
        srlContent.setHeaderView(customHeader);
        srlContent.setOnRefreshListener(new RefreshingListenerAdapter() {
            @Override
            public void onRefreshBegin(boolean isRefresh) {
                //下拉刷新
                nextPageIndex = 1;
                mAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载
                getMyWallet();
            }
        });

        rvAccountDetails.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                getMyWallet();
            }
        }, rvAccountDetails);
        srlContent.setOnChildNotYetInEdgeCannotMoveHeaderCallBack((parent, child, header) -> ScrollCompat.canChildScrollUp(rvAccountDetails));
        srlContent.setOnChildNotYetInEdgeCannotMoveFooterCallBack((parent, child, footer) -> ScrollCompat.canChildScrollDown(rvAccountDetails));
        srlContent.setLoadMoreScrollTargetView(rvAccountDetails);
        rvAccountDetails.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvAccountDetails.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.tv_withdrawal:
                        showToast(getString(R.string.feature_development));
                        break;
                    case R.id.bt_recharge:
                        showToast(getString(R.string.feature_development));
                        break;
                    case R.id.ll_view_idln:
                        MyWalletResp wallet = (MyWalletResp) adapter.getItem(position);
                        Intent intent = new Intent(mContext, WebViewActivity.class);
                        intent.putExtra(WebViewActivity.URI, APKUtils.getLanguage().equals("en") ? wallet.whatIdlnUrlEn : wallet.whatIdlnUrlCn);
                        startActivity(intent);
                        break;
                }
            }
        });
        rvAccountDetails.setAdapter(mAdapter);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        getMyWallet();
    }

    private void getMyWallet() {
        APIHelper.getInstance().myWallet(nextPageIndex, new CallBack<MyWalletResp>() {
            @Override
            public void onSuccess(MyWalletResp data) {
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
    private void onDataLoad(MyWalletResp data) {
        List<IMultiItemType> list = new ArrayList<>();
        if (data.userIncomeItems != null) {
            if (nextPageIndex > 1) {//加载数据
                mAdapter.loadMoreComplete();
                //上拉加载添加数据
                list.addAll(data.userIncomeItems);
                mAdapter.addData(list);
                if (data.userIncomeItems.size() < 20) {//是否还有分页
                    mAdapter.loadMoreEnd(true);
                }
            } else { //刷新或者第一次加载数据
                list.addAll(data.userIncomeItems);
                list.add(0, data);
                srlContent.setState(SmoothRefreshLayout.STATE_CONTENT);
                mAdapter.setNewData(list);
                if (data.userIncomeItems.size() < 20) {//是否还有分页 setNewData之后调用loadMoreComplete()是无效的，会自动默认为还能继续加载
                    mAdapter.loadMoreEnd(true);//不能用false，否则滚动到底部时还会触发上拉加载
                    mAdapter.setEnableLoadMore(false);
                }

            }
        } else if (nextPageIndex == 1) {//第一页为空 显示无数据
            mAdapter.loadMoreEnd(true);
        } else {

        }
    }

    @OnClick({R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
