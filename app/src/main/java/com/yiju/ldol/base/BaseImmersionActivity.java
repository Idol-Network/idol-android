package com.yiju.ldol.base;


import com.gyf.barlibrary.ImmersionBar;

/**
 * 基类
 */
public abstract class BaseImmersionActivity extends BaseActivity {
    protected ImmersionBar mImmersionBar;

    @Override
    public void setBase() {
        super.setBase();
        mImmersionBar = ImmersionBar.with(this);
        mImmersionBar.init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mImmersionBar != null) {
            mImmersionBar.destroy();
        }
    }
}



