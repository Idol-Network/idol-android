package com.yiju.ldol.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.yiju.ldol.base.BaseImmersionActivity;
import com.yiju.ldol.bean.response.CountryListResp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by thbpc on 2018/3/16 0016.
 */

public class CountryActivity extends BaseImmersionActivity {
    @BindView(R.id.iv_city_back)
    ImageView ivCityBack;
    @BindView(R.id.rl_city_title)
    RelativeLayout rlCityTitle;
    @BindView(R.id.et_search_text)
    EditText etSearchText;
    @BindView(R.id.rv_city_list)
    RecyclerView rvCityList;
    private CountryListAdapter mAdapter;

    @Override
    public int getLayout() {
        return R.layout.ac_city;
    }

    @Override
    public void initView() {
        mImmersionBar.titleBarMarginTop(rlCityTitle);
        rvCityList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new CountryListAdapter(new CountryListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(CountryListResp.CountryListBean bean) {
                Intent intent = new Intent(mContext, PhoneRegisterActivity.class);
                intent.putExtra("country", bean);
                setResult(123, intent);
                finish();
            }

        });
        rvCityList.setAdapter(mAdapter);
        etSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    mAdapter.setData(objects);
                } else {
                    // 创建集合保存过滤后的数据
                    List<Object> mList = new ArrayList<>();
                    // 遍历原始数据集合，根据搜索的规则过滤数据
                    for (Object obj : objects) {
                        if (obj instanceof CountryListResp.CountryListBean) {
                            CountryListResp.CountryListBean bean = (CountryListResp.CountryListBean) obj;
                            if (bean.name.contains(s)) {
                                mList.add(obj);
                            }
                        }
                    }
                    mAdapter.setData(mList);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private ArrayList<Object> objects = new ArrayList<>();

    @Override
    public void initData(Bundle savedInstanceState) {
        APIHelper.getInstance().getCountryList(new CallBack<CountryListResp>() {
            @Override
            public void onSuccess(CountryListResp data) {
                objects.clear();
                for (CountryListResp.CountryListBean bean : data.countryList) {
                    if (!objects.contains(bean.firstLetter)) {
                        objects.add(bean.firstLetter);
                    }
                    objects.add(bean);
                }
                mAdapter.setData(objects);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideKeybord(etSearchText);
    }

    @OnClick(R.id.iv_city_back)
    public void onViewClicked() {
        finish();
    }
}
