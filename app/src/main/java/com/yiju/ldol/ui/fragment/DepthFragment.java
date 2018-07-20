package com.yiju.ldol.ui.fragment;

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
import com.yiju.idol.bean.response.DepathKlineResp;
import com.yiju.idol.ui.activity.CoinDetailsActivity;
import com.yiju.idol.ui.adapter.DepthAdapter;

import butterknife.BindView;

public class DepthFragment extends BaseFragment {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;

    private DepthAdapter mAdapter;
    private Handler mHandler = new Handler();
    Runnable runnable;

    public static DepthFragment getInstance(int personCurrencyId) {
        DepthFragment fragment = new DepthFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(CoinDetailsActivity.PERSON_CURRENCY_ID, personCurrencyId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getLayout() {
        return R.layout.ft_depath;
    }

    @Override
    public void OnActCreate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int personcurrencyid = getArguments().getInt(CoinDetailsActivity.PERSON_CURRENCY_ID);
        runnable = new Runnable() {
            @Override
            public void run() {
                getDepath(personcurrencyid);
                mHandler.postDelayed(this, 2000);
            }
        };
        mHandler.post(runnable);
    }

    private void getDepath(int personcurrencyid) {
        APIHelper.getInstance().getDepthKline(personcurrencyid, new CallBack<DepathKlineResp>() {
            @Override
            public void onSuccess(DepathKlineResp data) {
                mAdapter.setList(data);
            }

            @Override
            public void onFailure(BaseReslut data) {
            }
        });
    }

    @Override
    public void initView(View view) {
        recyclerview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mAdapter = new DepthAdapter();
        recyclerview.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(runnable);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            mHandler.removeCallbacks(runnable);
        } else {
            mHandler.postDelayed(runnable, 2000);
        }
    }
}
