package com.yiju.ldol.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yiju.idol.R;
import com.yiju.idol.base.BaseFragment;
import com.yiju.idol.bean.response.ActivitiesResp;
import com.yiju.idol.ui.activity.ActItemActivity;
import com.yiju.idol.ui.activity.WelfareActivity;
import com.yiju.idol.ui.adapter.ActivitiesAdapter;
import com.yiju.idol.ui.view.refresh.CustomHeader;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import me.dkzwm.widget.srl.RefreshingListenerAdapter;
import me.dkzwm.widget.srl.SmoothRefreshLayout;
import me.dkzwm.widget.srl.extra.IRefreshView;
import me.dkzwm.widget.srl.utils.ScrollCompat;

/**
 * 活动列表页
 * Created by Allan_Zhang on 2018/7/7.
 */

public class ActivitiesFragment extends BaseFragment {

    @BindView(R.id.rv_activities)
    RecyclerView rvActivities;
    @BindView(R.id.srl_content)
    SmoothRefreshLayout mRefreshLayout;

    private ActivitiesAdapter mAdapter;
    private boolean isNetWork;//是否正在请求数据
    private int nextPageIndex = 1;//下一页

    @Override
    public int getLayout() {
        return R.layout.ft_activities;
    }

    @Override
    public void OnActCreate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getData();
    }

    @Override
    public void initView(View view) {
        CustomHeader customHeader = new CustomHeader(getActivity());
        customHeader.setStyle(IRefreshView.STYLE_SCALE);
        mRefreshLayout.setHeaderView(customHeader);
        mRefreshLayout.setOnRefreshListener(new RefreshingListenerAdapter() {
            @Override
            public void onRefreshBegin(boolean isRefresh) {
                //下拉刷新
                nextPageIndex = 1;
                mAdapter.setEnableLoadMore(false);//这里的作用是防止下拉刷新的时候还可以上拉加载
                getData();
            }
        });
        mAdapter = new ActivitiesAdapter();
        mAdapter.setOnItemClickListener((adapter, view1, position) -> {
            //TODO
            if (position % 2 == 0) {
                ActItemActivity.start(getActivity());
            }else {
                WelfareActivity.start(getActivity());
            }
        });
        mAdapter.setOnLoadMoreListener(() -> {
            //上拉加载
            nextPageIndex++;
            getData();
        }, rvActivities);
        rvActivities.setAdapter(mAdapter);
        rvActivities.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rvActivities.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRefreshLayout.setOnChildNotYetInEdgeCannotMoveHeaderCallBack((parent, child, header) -> ScrollCompat.canChildScrollUp(rvActivities));
        mRefreshLayout.setOnChildNotYetInEdgeCannotMoveFooterCallBack((parent, child, footer) -> ScrollCompat.canChildScrollDown(rvActivities));
        mRefreshLayout.setLoadMoreScrollTargetView(rvActivities);
    }

    private void getData() {
        //TODO
        if (isNetWork) {
            return;
        }
        mRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                addData();
            }
        }, 1000);
//        isNetWork = true;
//        APIHelper.getInstance().getActivities( nextPageIndex, new APIHelper.CallBack<ActivitiesResp>() {
//            @Override
//            public void onSuccess(ActivitiesResp data) {
//                if ( isFinishing()){
//                    return;
//                }
//                isNetWork = false;
//                mAdapter.setEnableLoadMore(true);
//                if (nextPageIndex == 1) {
//                    //下拉刷新结束
//                    mRefreshLayout.refreshComplete();
//                }
//                if (data != null) {
//                    onDataLoad(data);
//                } else if (nextPageIndex == 1) {//第一页为空 显示无数据
//                    mRefreshLayout.setState(SmoothRefreshLayout.STATE_EMPTY);//空布局
//                }
//            }
//
//            @Override
//            public void onFailure(BaseReslut data) {
//                if (isFinishing()){
//                    return;
//                }
//                isNetWork = false;
//                if (nextPageIndex > 1) {
//                    //上拉加载
//                    mAdapter.loadMoreFail();
//                    nextPageIndex -= 1;//下拉失败，下一页id减少1
//                } else {
//                    //下拉刷新
//                    mRefreshLayout.refreshComplete();
//                    mRefreshLayout.setState(SmoothRefreshLayout.STATE_ERROR);//显示错误布局
//                }
//                mAdapter.setEnableLoadMore(true);
//                super.onFailure(data);
//            }
//        });
    }

    private void addData() {
        mRefreshLayout.refreshComplete();
        ArrayList<ActivitiesResp.CrowdfundingBean> list = new ArrayList<>();
        ActivitiesResp.CrowdfundingBean bean1 = new ActivitiesResp.CrowdfundingBean();
        bean1.num = 11234;
        bean1.title = "\"Idol trainee\" 88 signed photo only one person!";
        bean1.tag = "Welfare";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.format(new Date());
        bean1.time = sdf.format(new Date());
        bean1.picUrl = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1526033930753&di=968251a86f42f6f0da52eb0cef1bd56f&imgtype=0&src=http%3A%2F%2Fimgcache.yicai.com%2Fuppics%2Fslides%2F2015%2F05%2F635671224845416228.jpg";
        list.add(bean1);
        ActivitiesResp.CrowdfundingBean bean2 = new ActivitiesResp.CrowdfundingBean();
        bean2.num = 12345;
        bean2.title = "\"Idol trainee\" 88 signed photo only one person!";
        bean2.tag = "Crowdfunding";
        sdf.format(new Date());
        bean2.time = sdf.format(new Date());
        bean2.picUrl = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1526034261679&di=29616b143bebbe1c69dda19741367679&imgtype=0&src=http%3A%2F%2Fdynamic-image.yesky.com%2F1080x-%2FuploadImages%2F2017%2F033%2F10%2FEBVSMKF3HQ9I.jpg";
        list.add(bean2);
        ActivitiesResp.CrowdfundingBean bean3 = new ActivitiesResp.CrowdfundingBean();
        bean3.num = 13234;
        bean3.title = "\"Idol trainee\" 88 signed photo only one person!";
        sdf.format(new Date());
        bean3.time = sdf.format(new Date());
        bean3.picUrl = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1526034302647&di=8315cb6973ba297f55025343a4b224d3&imgtype=0&src=http%3A%2F%2Fa5.topitme.com%2Fl029%2F1002922900d8a23e3f.jpg";
        list.add(bean3);
        ActivitiesResp.CrowdfundingBean bean4 = new ActivitiesResp.CrowdfundingBean();
        bean4.num = 21234;
        bean4.title = "\"Idol trainee\" 88 signed photo only one person!";
        bean4.tag = "Crowdfunding";
        sdf.format(new Date());
        bean4.time = sdf.format(new Date());
        bean4.picUrl = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1526034358996&di=399184ee3fff2b3a5ee794c243799dc5&imgtype=0&src=http%3A%2F%2Fimg4.duitang.com%2Fuploads%2Fitem%2F201504%2F27%2F201504275320_ant2f.jpeg";
        list.add(bean4);
        ActivitiesResp.CrowdfundingBean bean5 = new ActivitiesResp.CrowdfundingBean();
        bean5.num = 33333;
        bean5.title = "\"Idol trainee\" 88 signed photo only one person!";
        bean5.tag = "Welfare";
        sdf.format(new Date());
        bean5.time = sdf.format(new Date());
        bean5.picUrl = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1526034389669&di=4e1b2d59f14aabcc286b34895ab1c52a&imgtype=0&src=http%3A%2F%2Fpic.baike.soso.com%2Fugc%2Fbaikepic2%2F5145%2F20171203143549-1934398889_png_1113_660_806590.jpg%2F0";
        list.add(bean5);
        ActivitiesResp.CrowdfundingBean bean6 = new ActivitiesResp.CrowdfundingBean();
        bean6.num = 12232;
        bean6.title = "\"Idol trainee\" 88 signed photo only one person!";
        bean6.tag = "Crowdfunding";
        sdf.format(new Date());
        bean6.time = sdf.format(new Date());
        bean6.picUrl = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1526034540890&di=74999b556704bf85e6ca2b91c10dd495&imgtype=0&src=http%3A%2F%2Fi0.hdslb.com%2Fbfs%2Farchive%2F14aec28d8c6ff18763c1c8c6d5e3378b6c6eb790.jpg";
        list.add(bean6);
        ActivitiesResp.CrowdfundingBean bean7 = new ActivitiesResp.CrowdfundingBean();
        bean7.num = 55553;
        bean7.title = "\"Idol trainee\" 88 signed photo only one person!";
        sdf.format(new Date());
        bean7.time = sdf.format(new Date());
        bean7.picUrl = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1526034579352&di=95f5c0075d16ccf3bd57a0f7bd7f1808&imgtype=0&src=http%3A%2F%2Ffe.topitme.com%2Fe%2Fb1%2Fd4%2F11978885861bfd4b1eo.jpg";
        list.add(bean7);
        mAdapter.setNewData(list);
        mAdapter.loadMoreEnd(true);
    }

    /**
     * 显示数据
     */
    private void onDataLoad(ActivitiesResp data) {
        if (data.crowdfundings != null && data.crowdfundings.size() > 0) {
            if (nextPageIndex > 1) {
                //上拉加载添加数据
                mAdapter.addData(data.crowdfundings);
                //TODO 必须放在设置数据之后才能生效
                if (data.crowdfundings.size() < 20) {//是否还有分页
                    mAdapter.loadMoreEnd(false);
                } else {
                    mAdapter.loadMoreComplete();
                }
            } else {
                //下拉刷新重新加载数据
                mRefreshLayout.setState(SmoothRefreshLayout.STATE_CONTENT);
                mAdapter.setNewData(data.crowdfundings);
                if (data.crowdfundings.size() < 20) {//是否还有分页 setNewData之后调用loadMoreComplete()是无效的，会自动默认为还能继续加载
                    mAdapter.loadMoreEnd(true);//不能用false，否则滚动到底部时还会触发上拉加载
                }
                mAdapter.disableLoadMoreIfNotFullPage(rvActivities);//不满页触发加载更多 默认第一次加载会进入回调，如果不需要可以配置
            }
        } else if (nextPageIndex == 1) {//第一页为空 显示无数据
            mRefreshLayout.setState(SmoothRefreshLayout.STATE_EMPTY);//无数据
        } else {
            mAdapter.loadMoreEnd(false);//加载更多无数据
        }
    }

    @Override
    public void onClick(View v) {

    }
}
