package com.yiju.ldol.ui.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.yiju.ldol.base.BaseImmersionActivity;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by thbpc on 2018/3/21 0021.
 */

public class ImageActivity extends BaseImmersionActivity {

    public static final String IMAGE_URLS = "imageurls";

    @BindView(R.id.vp_image_group)
    ViewPager vpImageGroup;

    @Override
    public int getLayout() {
        return R.layout.ac_image;
    }

    @Override
    public void initView() {

    }


    @Override
    public void initData(Bundle savedInstanceState) {
        ArrayList<String> imageList = getIntent().getStringArrayListExtra(IMAGE_URLS);
        if (imageList == null || imageList.isEmpty()) {
            finish();
        }
        vpImageGroup.setAdapter(new ImagePagerAdpter(imageList));
    }

    @OnClick(R.id.vp_image_group)
    public void onViewClicked() {

    }
}
