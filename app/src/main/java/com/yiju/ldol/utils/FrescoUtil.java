package com.yiju.ldol.utils;

import android.content.Context;
import android.net.Uri;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

/**
 * Created by zhanghengzhen on 2017/6/26.
 */

public class FrescoUtil {

    private FrescoUtil() {
    }

    /**
     * 显示缩略图 防止卡顿
     *
     * @param imageView
     * @param url
     * @param widthDp
     * @param heightDp
     */
    public static void loadImage(SimpleDraweeView imageView, String url, float widthDp, float heightDp) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setResizeOptions(new ResizeOptions(DensityUtil.dip2px(widthDp), DensityUtil.dip2px(heightDp)))
                .setAutoRotateEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(imageView.getController())
                .setControllerListener(new BaseControllerListener<ImageInfo>())
                .build();
        imageView.setController(controller);
    }

    /**
     * 显示缩略图 防止卡顿
     *
     * @param imageView
     * @param url
     * @param width
     * @param height
     */
    public static void loadImage(SimpleDraweeView imageView, String url, int width, int height) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url))
                .setResizeOptions(new ResizeOptions(width, height))
                .setAutoRotateEnabled(true)
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(imageView.getController())
                .setControllerListener(new BaseControllerListener<ImageInfo>())
                .build();
        imageView.setController(controller);
    }

    /**
     * 显示缩略图 防止卡顿
     *
     * @param imageView
     * @param uri
     * @param widthDp
     * @param heightDp
     */
    public static void loadImage(SimpleDraweeView imageView, Uri uri, float widthDp, float heightDp) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setAutoRotateEnabled(true)
                .setResizeOptions(new ResizeOptions(DensityUtil.dip2px(widthDp), DensityUtil.dip2px(heightDp)))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(imageView.getController())
                .setControllerListener(new BaseControllerListener<ImageInfo>())
                .build();
        imageView.setController(controller);
    }

    public static void getCacheBitmap(Context mContext, Uri uri, BaseBitmapDataSubscriber dataSubscriber) {
        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithSource(uri)
                .setProgressiveRenderingEnabled(true)
                .build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>>
                dataSource = imagePipeline.fetchDecodedImage(imageRequest, mContext);
        dataSource.subscribe(dataSubscriber, CallerThreadExecutor.getInstance());

    }
}
