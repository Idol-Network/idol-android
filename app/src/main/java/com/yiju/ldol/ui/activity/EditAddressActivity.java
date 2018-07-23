package com.yiju.ldol.ui.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.yiju.ldol.base.BaseImmersionActivity;
import com.yiju.ldol.bean.response.AddressItemBean;
import com.yiju.ldol.bean.response.AddressListResp;
import com.yiju.ldol.ui.view.SimpleDividerItemDecoration;
import com.yiju.ldol.utils.DensityUtil;
import com.yiju.ldol.utils.DialogUtils;

import butterknife.BindView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;
import de.greenrobot.event.ThreadMode;

public class EditAddressActivity extends BaseImmersionActivity {

    public static final String HAS_ADDRESS = "has_address";

    @BindView(R.id.fl_title)
    FrameLayout mFlTitle;
    @BindView(R.id.rv_edit_address)
    RecyclerView mRvEditAddress;
    private AddressAdapter mAdapter;
    @BindView(R.id.tv_save)
    TextView tvSave;

    private boolean isNetWork;//是否正在请求数据
    private Handler mHandler = new Handler();

    @Override
    public int getLayout() {
        return R.layout.act_edit_address;
    }

    @Override
    public void initView() {
        mImmersionBar.titleBar(mFlTitle);
        mAdapter = new AddressAdapter();
        mRvEditAddress.setHasFixedSize(true);
        mRvEditAddress.setItemAnimator(null);
        mRvEditAddress.setLayoutManager(new LinearLayoutManager(this));
        mRvEditAddress.setAdapter(mAdapter);
        mRvEditAddress.addItemDecoration(new SimpleDividerItemDecoration(this, DensityUtil.dip2px(0.5f)));
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> {
            switch (view.getId()) {
                case R.id.iv_check://设置是否选择 1为选中 0为未选中
                    AddressItemBean item = mAdapter.getItem(position);
                    if (item.def == 1) {
                        break;
                    }
                    item.def = 1;
                    int oldPos = mAdapter.getCheckedPosition();
                    if (oldPos != -1) {
                        AddressItemBean oldItem = mAdapter.getItem(oldPos);
                        oldItem.def = 0;
                        mAdapter.setData(oldPos, oldItem);
                    }
                    mAdapter.setCheckedPosition(position);
                    mAdapter.setData(position, item);
                    break;
                case R.id.tv_delete://删除地址
                    delAddress(position, view);
                    break;
                case R.id.fl_edit_address://若包裹了侧滑删除，则item点击事件会无需，需要对子item添加点击事件
                    //传递待编辑地址
                    Intent intent = new Intent(EditAddressActivity.this, AddAddressActivity.class);
                    intent.putExtra(AddAddressActivity.ADDRESS_DATA, mAdapter.getItem(position));
                    startActivity(intent);
                    break;
                default:
                    break;
            }
        });
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        boolean hasAddress = getIntent().getBooleanExtra(HAS_ADDRESS, false);
        if (hasAddress) {
            getData();
        }
    }

    private void getData() {
        showProgressDialog();
        APIHelper.getInstance().getAddressList(new CallBack<AddressListResp>() {
            @Override
            public void onSuccess(AddressListResp data) {
                if (isFinishing()) {
                    return;
                }
                disMissDialog();
                if (data != null) {
                    mAdapter.setCheckedPosition(-1);
                    mAdapter.setNewData(data.addressItems);
                }
            }

            @Override
            public void onFailure(BaseReslut data) {
                if (isFinishing()) {
                    return;
                }
                disMissDialog();
                super.onFailure(data);
            }
        });
    }

    /**
     * 设置默认地址
     */
    private void setDefAddress() {
        if (isNetWork) {
            return;
        }
        isNetWork = true;
        showProgressDialog();
        int checkedPosition = mAdapter.getCheckedPosition();
        AddressItemBean item = mAdapter.getItem(checkedPosition);
        APIHelper.getInstance().setDefAddress(item.addressId, new CallBack() {
            @Override
            public void onSuccess(BaseReslut data) {
                if (isFinishing()) {
                    return;
                }
                isNetWork = false;
                disMissDialog();
                item.def = 1;
                tvSave.setEnabled(false);
                showToast(getString(R.string.saved_successfully));
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //将修改后的地址通知上个界面刷新
                        EventBus.getDefault().post(item);
                        finish();
                    }
                }, 1000);
            }

            @Override
            public void onFailure(BaseReslut data) {
                if (isFinishing()) {
                    return;
                }
                isNetWork = false;
                disMissDialog();
                super.onFailure(data);
            }
        });
    }

    private void delAddress(int position, View actionView) {
        AddressItemBean item2Del = mAdapter.getItem(position);
        DialogUtils.showCenterDialog(this, R.layout.dialog_no_title, 270,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                new DialogUtils.InitViewsListener() {
                    @Override
                    public void setAction(Dialog dialog, View view) {
                        TextView tvMsg = view.findViewById(R.id.tv_dialog_msg);
                        tvMsg.setText(R.string.sure_to_delete_address);
                        TextView tvConfirm = view.findViewById(R.id.tv_confirm);
                        TextView tvCancel = view.findViewById(R.id.tv_cancel);
                        View.OnClickListener listener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                switch (v.getId()) {
                                    case R.id.tv_confirm:
                                        APIHelper.getInstance().deleteAddress(item2Del.addressId, new CallBack() {
                                            @Override
                                            public void onSuccess(BaseReslut data) {
                                                disMissDialog();
                                                //删除成功 快速关闭侧滑
                                                SwipeMenuLayout smLayout = (SwipeMenuLayout) actionView.getParent();
                                                smLayout.quickClose();
                                                if (item2Del.def == 1) {
                                                    //默认地址被删除 通知订单界面更新地址
                                                    EventBus.getDefault().post(IEventType.ON_DEF_ADDRESS_CHANGED);
                                                    mAdapter.setCheckedPosition(-1);
                                                    mAdapter.remove(position);
                                                }
                                            }

                                            @Override
                                            public void onFailure(BaseReslut data) {
                                                disMissDialog();
                                                //删除失败 关闭侧滑
                                                SwipeMenuLayout smLayout = (SwipeMenuLayout) actionView.getParent();
                                                smLayout.smoothClose();
                                                super.onFailure(data);
                                            }
                                        });
                                        break;
                                    case R.id.tv_cancel:
                                        SwipeMenuLayout smLayout = (SwipeMenuLayout) actionView.getParent();
                                        smLayout.smoothClose();
                                        break;
                                    default:
                                        break;
                                }
                                dialog.dismiss();
                            }
                        };
                        tvConfirm.setOnClickListener(listener);
                        tvCancel.setOnClickListener(listener);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void onEvent(String type) {//接收消息
        switch (type) {
            case IEventType.ON_ADDRESS_LIST_UPDATE://地址有更新
                //刷新数据
                getData();
                break;
            default:
                break;
        }
    }

    @OnClick({R.id.iv_back, R.id.fl_address, R.id.tv_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.fl_address:
                Intent intent = new Intent(EditAddressActivity.this, AddAddressActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_save://保存
                if (mAdapter.getCheckedPosition() != -1) {
                    setDefAddress();
                }
                break;
            default:
                break;
        }
    }
}
