package com.yiju.ldol.listener;

import android.app.Activity;
import android.net.Uri;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.lzy.imagepicker.loader.ImageLoader;


public class GlideImageLoader implements ImageLoader {

    @Override
    public void displayImage(Activity activity, String path, View view, int width, int height) {
        if (view instanceof SimpleDraweeView) {
            SimpleDraweeView sim = (SimpleDraweeView) view;
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse("file://" + path))
                    .setResizeOptions(new ResizeOptions(width,height))
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(sim.getController())
                    .build();
            sim.setController(controller);
        }
    }

    @Override
    public void clearMemoryCache() {
    }
}
