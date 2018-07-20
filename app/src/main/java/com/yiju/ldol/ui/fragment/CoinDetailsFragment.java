package com.yiju.ldol.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yiju.idol.R;
import com.yiju.idol.api.APIHelper;
import com.yiju.idol.api.CallBack;
import com.yiju.idol.base.BaseFragment;
import com.yiju.idol.bean.response.PersoncurrencyDetailsResp;
import com.yiju.idol.ui.activity.CoinDetailsActivity;
import com.yiju.idol.ui.adapter.CoinDetailsAdapter;

import butterknife.BindView;

public class CoinDetailsFragment extends BaseFragment {
    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    private CoinDetailsAdapter adapter;

    @Override
    public int getLayout() {
        return R.layout.ft_coindetails;
    }

    public static CoinDetailsFragment getInstance(int personCurrencyId) {
        CoinDetailsFragment fragment = new CoinDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(CoinDetailsActivity.PERSON_CURRENCY_ID, personCurrencyId);
        fragment.setArguments(bundle);
        return fragment;
    }

    /**
     * 发行时间
     * 发行总量
     * 流通总量
     * 众筹价格
     * 白皮书
     * 官网
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     */
    @Override
    public void OnActCreate(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        int personcurrencyid = getArguments().getInt(CoinDetailsActivity.PERSON_CURRENCY_ID);
        APIHelper.getInstance().getPersoncurrencyDetails(personcurrencyid, new CallBack<PersoncurrencyDetailsResp>() {
            @Override
            public void onSuccess(PersoncurrencyDetailsResp data) {
                adapter.setData(data);
            }
        });
    }

    @Override
    public void initView(View view) {
        recyclerview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        adapter = new CoinDetailsAdapter();
        recyclerview.setAdapter(adapter);
        recyclerview.setNestedScrollingEnabled(false);
    }

    @Override
    public void onClick(View v) {

    }

}
