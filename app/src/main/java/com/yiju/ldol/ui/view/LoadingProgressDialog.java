package com.yiju.ldol.ui.view;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yiju.idol.R;
import com.yiju.idol.utils.LogUtils;


public class LoadingProgressDialog extends ProgressDialog {

	@SuppressWarnings("unused")
	private Context mContext;
	private TextView mLoadingMessage;
	private TextView mLoadingTitle;
	
	private ImageView mLoadingImage;
	@SuppressLint("HandlerLeak") 
	private Handler mViewUpdateHandler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if (mMessageConent != null) {
					mLoadingMessage.setText(mMessageConent);
					mLoadingMessage.setVisibility(View.GONE);
				}
				break;
			case 1:
				if(mMessageTitle != null){
					mLoadingTitle.setText(mMessageTitle);
					mLoadingTitle.setVisibility(View.VISIBLE);
				}
				break;
			}
			
		};
	};
	
	private String mMessageConent;
	private String mMessageTitle;
	private boolean isLoading;
	
	private AnimationDrawable mAnimationDrawable;

	
	
	public LoadingProgressDialog(Context context) {
		super(context, R.style.loading_dialog_style);
		this.mContext = context;
	}

	
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_loading);
		mLoadingMessage = (TextView) findViewById(R.id.loading_tv_message);
		mLoadingTitle = (TextView) findViewById(R.id.loading_tv_title);
		mLoadingImage = (ImageView) findViewById(R.id.loading_img);
		mLoadingImage.setImageResource(R.drawable.animation_joustar_loading_content);
		mAnimationDrawable = (AnimationDrawable) mLoadingImage.getDrawable();
	}
	
	
	@Override
	public void setMessage(CharSequence message) {
		super.setMessage(message);
		LogUtils.i("LoadingProgressDialog", "---message = "+message);
		mLoadingMessage.setText(message);
	}
	
	@Override
	public void show() {
		super.show();
		isLoading = true;
		mAnimationDrawable.start();

	}
	
	@Override
	public void dismiss() {
		super.dismiss();
		isLoading = false;
		mAnimationDrawable.stop();
	}
	
	
	public boolean isLoading(){
		return isLoading;

	}
	
	@Override
	public void setCanceledOnTouchOutside(boolean cancel) {
		super.setCanceledOnTouchOutside(cancel);
		if(cancel){
			dismiss();
		}
	}
	
	/**
	 * 设置Dialog的内容
	 * @param content
	 */
	public void setMessageContent(String content){
		if(content != null){
			onProgressChanged(content);
		}
	}
	
	/**
	 * 设置Dialog的Title
	 * @param content
	 */
	public void setMessageTitle(String content){
		if(content != null){
			onMessageChanged(content);
		}
	}
	
	
	private void onProgressChanged(String content) {
		mMessageConent = content;
		if(mViewUpdateHandler != null){
			mViewUpdateHandler.sendEmptyMessage(0); 
		}
	}
	
	private void onMessageChanged(String content){
		mMessageTitle = content;
		if(mViewUpdateHandler != null){
			mViewUpdateHandler.sendEmptyMessage(1); 
		}
	}
	
	public static LoadingProgressDialog getInstants(Context context){
		return new LoadingProgressDialog(context);
	}
	
}
