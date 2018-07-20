package com.yiju.ldol.utils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.yiju.idol.R;
import com.yiju.idol.base.Constant;

public class DownloadPicUtil {

    private Context mContext;
    private DownloadManager mDownloadManager; // 下载管理器
    private long mDownloadId; // 下载ID

    // 下载完成的接收器
    private BroadcastReceiver mApkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadId == mDownloadId) {
                Toast.makeText(mContext, mContext.getString(R.string.file_saved) + Constant.DOWNLOAD_PIC_PATH, Toast.LENGTH_LONG).show();
                destroyArgs();
            }
        }
    };


    public DownloadPicUtil(Context context) {
        mContext = context;
        initArgs();
    }

    /**
     * 下载文件
     *
     * @param url
     */
    public void download(String url) {
        String suffix = url.substring(url.lastIndexOf("."));
        String fileName = "IMG" + System.currentTimeMillis() + suffix;//文件名
        // 设置下载Url
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        // 设置文件类型
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
        request.setMimeType(mimeString);
        // 下载时提示，下载完成自动关闭通知栏
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        // 存储sdcard目录的download文件夹
        request.setDestinationInExternalPublicDir(Constant.DOWNLOAD_PIC_PATH, fileName);
        request.setTitle(fileName);
        // 开始下载
        mDownloadId = mDownloadManager.enqueue(request);
    }

    // 初始化
    private void initArgs() {
        mDownloadManager = (DownloadManager) mContext.getSystemService((Context.DOWNLOAD_SERVICE));
        mContext.registerReceiver(mApkReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    //注销广播
    private void destroyArgs() {
        mContext.unregisterReceiver(mApkReceiver);
    }

}