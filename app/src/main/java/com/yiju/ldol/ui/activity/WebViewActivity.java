package com.yiju.ldol.ui.activity;


import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yiju.ldol.base.BaseImmersionActivity;
import com.yiju.ldol.utils.JSKit;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 协议
 */
public class WebViewActivity extends BaseImmersionActivity {
    public static final String URI = "content";
    @BindView(R.id.iv_webview_back)
    ImageView ivWebviewBack;
    @BindView(R.id.tv_webview_title)
    TextView tvWebviewTitle;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.webview)
    WebView mWebView;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private JSKit js;

    @Override
    public int getLayout() {
        return R.layout.activity_protocol;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initView() {
        mImmersionBar.titleBarMarginTop(rlTitle);
    }


    @SuppressLint("JavascriptInterface")
    @Override
    public void initData(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);
        String uri = getIntent().getStringExtra(URI);
        if (TextUtils.isEmpty(uri)) {
            finish();
        }
        //实例化js对象
        js = new JSKit(this, mWebView);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.addJavascriptInterface(js, "phone");
        mWebView.setWebViewClient(new QJXWebViewClient());
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setDefaultTextEncodingName("UTF-8");
        settings.setDomStorageEnabled(true);
        settings.setSupportZoom(false);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        String userAgentString = settings.getUserAgentString();
        settings.setUserAgentString(userAgentString + ";grtstarapp");
//        mWebView.loadData(content, "text/html;charset=UTF-8", null);
        mWebView.loadUrl(uri);
        // 此方法可以处理mWebView 在加载时和加载完成时一些操作
        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 这里是设置activity的标题
                    tvWebviewTitle.setText(mWebView.getTitle());
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @OnClick(R.id.iv_webview_back)
    public void onViewClicked() {
        finish();
    }

    // Web视图
    private class QJXWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(final WebView view, String url) {
//            /**
//             * 推荐采用的新的二合一接口(payInterceptorWithUrl),只需调用一次
//             */
//            final PayTask task = new PayTask(WebViewActivity.this);
//            boolean isIntercepted = task.payInterceptorWithUrl(url, false, new H5PayCallback() {
//                @Override
//                public void onPayResult(final H5PayResultModel result) {
//                    // 支付结果返回
//                    final String url = result.getReturnUrl();
//                    if (!TextUtils.isEmpty(url)) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                view.loadUrl(url);
//                            }
//                        });
//                    }
//                }
//            });
//            /**
//             * 判断是否成功拦截
//             * 若成功拦截，则无需继续加载该URL；否则继续加载
//             */
//            if (!isIntercepted) {
            view.loadUrl(url);
//            }
            return true;
        }
    }

    public boolean onKeyDown(int keyCoder, KeyEvent event) {
        if (keyCoder == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();   //goBack()表示返回webView的上一页面
            } else {
                finish();
            }
            return true;
        }
        return false;
    }


    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mWebView.onPause(); // 暂停网页中正在播放的视频
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.removeAllViews();
        mWebView.destroy();
        mWebView = null;
    }
}
