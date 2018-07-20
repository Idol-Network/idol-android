package com.yiju.ldol.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.Team;
import com.yiju.idol.R;
import com.yiju.idol.api.APIHelper;
import com.yiju.idol.api.BaseReslut;
import com.yiju.idol.api.CallBack;
import com.yiju.idol.base.BaseFragment;
import com.yiju.idol.bean.response.FollowResp;
import com.yiju.idol.bean.response.PersonListResp;
import com.yiju.idol.ui.activity.AddFollowActivity;
import com.yiju.idol.ui.adapter.CategoryAdapter;
import com.yiju.idol.ui.view.refresh.CustomHeader;

import me.dkzwm.widget.srl.RefreshingListenerAdapter;
import me.dkzwm.widget.srl.SmoothRefreshLayout;
import me.dkzwm.widget.srl.extra.IRefreshView;
import me.dkzwm.widget.srl.utils.ScrollCompat;


/**
 * Created by lxmpc on 2016/11/15.
 */

public class CategoryFt extends BaseFragment {
    private RecyclerView mRecyclerView;
    private CategoryAdapter mCategoryAdapter;
    private SmoothRefreshLayout srlContent;

    public static final String CATEGORY_ID = "category_Id";
    private int mCategoryId;
    private AddFollowActivity activity;
    private int nextPageIndex = 1;

    @Override
    public int getLayout() {
        return R.layout.ft_category;
    }

    @Override
    public void OnActCreate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (AddFollowActivity) context;
    }

    @Override
    public void initView(View view) {
        mCategoryId = getArguments().getInt(CATEGORY_ID);

        srlContent = view.findViewById(R.id.srl_content);
        mRecyclerView = view.findViewById(R.id.recyclerview);

        CustomHeader customHeader = new CustomHeader(mContext);
        customHeader.setStyle(IRefreshView.STYLE_SCALE);
        srlContent.setHeaderView(customHeader);
        srlContent.setOnRefreshListener(new RefreshingListenerAdapter() {
            @Override
            public void onRefreshBegin(boolean isRefresh) {
                //下拉刷新
                nextPageIndex = 1;
                mCategoryAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载
                getData();
            }
        });

        mCategoryAdapter = new CategoryAdapter();
        mCategoryAdapter.setOnLoadMoreListener(() -> {
            //上拉加载
            getData();
        }, mRecyclerView);
        srlContent.setOnChildNotYetInEdgeCannotMoveHeaderCallBack((parent, child, header) -> ScrollCompat.canChildScrollUp(mRecyclerView));
        srlContent.setOnChildNotYetInEdgeCannotMoveFooterCallBack((parent, child, footer) -> ScrollCompat.canChildScrollDown(mRecyclerView));
        srlContent.setLoadMoreScrollTargetView(mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mCategoryAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                PersonListResp.PersonListBean item = mCategoryAdapter.getItem(position);
                switch (((Button) view).getText().toString()) {
                    case "Follow":
                        APIHelper.getInstance().followPerson(item.personId, new CallBack<FollowResp>() {
                            @Override
                            public void onSuccess(FollowResp data) {
                                item.follow = true;
                                item.teamId = data.teamId;
                                mCategoryAdapter.notifyDataSetChanged();
                                activity.addAvatars(item.avatar);
                                joinTeam(data.teamId, "");
                            }
                        });
                        break;
                    case "Followed":
                        quitTeam(item.teamId, new RequestCallback() {
                            @Override
                            public void onSuccess(Object o) {
                                APIHelper.getInstance().cancelFollow(item.personId, new CallBack() {
                                    @Override
                                    public void onSuccess(BaseReslut data) {
                                        item.follow = false;
                                        mCategoryAdapter.notifyDataSetChanged();
                                        activity.removeAvatars(item.avatar);

                                    }
                                });
                            }

                            @Override
                            public void onFailed(int i) {

                            }

                            @Override
                            public void onException(Throwable throwable) {

                            }
                        });
                        break;
                }
            }
        });

        mRecyclerView.setAdapter(mCategoryAdapter);
        getData();

    }

    private void getData() {
        APIHelper.getInstance().getPersonList(mCategoryId, nextPageIndex, new CallBack<PersonListResp>() {

            @Override
            public void onSuccess(PersonListResp data) {
                if (activity.isFinishing()) {
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
                if (activity.isFinishing()) {
                    return;
                }
                if (nextPageIndex > 1) {
                    //上拉加载
                    mCategoryAdapter.loadMoreFail();
                } else {
                    //下拉刷新
                    srlContent.refreshComplete();
                    srlContent.setState(SmoothRefreshLayout.STATE_ERROR);//显示错误布局
                }
                mCategoryAdapter.setEnableLoadMore(true);
                super.onFailure(data);
            }
        });
    }

    /**
     * 显示数据
     */
    private void onDataLoad(PersonListResp data) {
        if (data.personList != null) {
            if (nextPageIndex > 1) {
                //上拉加载添加数据
                mCategoryAdapter.addData(data.personList);
                //TODO 必须放在设置数据之后才能生效
                if (data.personList.size() < 20) {//是否还有分页
                    mCategoryAdapter.loadMoreEnd(false);
                } else {
                    mCategoryAdapter.loadMoreComplete();
                }
            } else {
                //下拉刷新重新加载数据
                srlContent.setState(SmoothRefreshLayout.STATE_CONTENT);
                mCategoryAdapter.setNewData(data.personList);
                if (data.personList.size() < 20) {//是否还有分页 setNewData之后调用loadMoreComplete()是无效的，会自动默认为还能继续加载
                    mCategoryAdapter.loadMoreEnd(true);//不能用false，否则滚动到底部时还会触发上拉加载
                    mCategoryAdapter.setEnableLoadMore(false);
                }
                mCategoryAdapter.disableLoadMoreIfNotFullPage(mRecyclerView);//不满页触发加载更多 默认第一次加载会进入回调，如果不需要可以配置
            }
        } else if (nextPageIndex == 1) {//第一页为空 显示无数据
            srlContent.setState(SmoothRefreshLayout.STATE_EMPTY);//无数据
        }
    }


    @Override
    public void onClick(View v) {

    }

    /**
     * 加群
     *
     * @param teamId 群id
     * @param reason 信息
     */
    private void joinTeam(String teamId, String reason) {
        NIMClient.getService(TeamService.class)
                .applyJoinTeam(teamId, reason)
                .setCallback(new RequestCallback<Team>() {
                    @Override
                    public void onSuccess(Team team) {

                    }

                    @Override
                    public void onFailed(int i) {

                    }

                    @Override
                    public void onException(Throwable throwable) {

                    }
                });
    }

    /**
     * 退群
     *
     * @param teamId 群id
     */
    private void quitTeam(String teamId,RequestCallback callback) {
        NIMClient.getService(TeamService.class)
                .quitTeam(teamId)
                .setCallback(callback);
    }
}
