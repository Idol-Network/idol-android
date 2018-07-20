/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yiju.ldol.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;


/**
 *
 */
public class ImageUtils {

    public static Bitmap decodeScaleImage(String path, int width, int heigh) {
        BitmapFactory.Options localOptions = getBitmapOptions(path);
        int i = calculateInSampleSize(localOptions, width, heigh);
        //EMLog.d("img", "original wid" + localOptions.outWidth + " original height:" + localOptions.outHeight + " sample:" + i);
        localOptions.inSampleSize = i;
        localOptions.inJustDecodeBounds = false;
        Bitmap localBitmap1 = BitmapFactory.decodeFile(path, localOptions);
        int j = readPictureDegree(path);
        Bitmap localBitmap2 = null;
        if ((localBitmap1 != null) && (j != 0)) {
            localBitmap2 = rotaingImageView(j, localBitmap1);
            localBitmap1.recycle();
            localBitmap1 = null;
            return localBitmap2;
        }
        return localBitmap1;
    }


    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static int caculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (width > reqWidth || height > reqHeight) {
            int widthRadio = Math.round(width * 1.0f / reqWidth);
            int heightRadio = Math.round(height * 1.0f / reqHeight);
            inSampleSize = Math.max(widthRadio, heightRadio);
        }
        return inSampleSize;
    }

    public static int readPictureDegree(String paramString) {
        int i = 0;
        try {
            ExifInterface localExifInterface = new ExifInterface(paramString);
            int j = localExifInterface.getAttributeInt("Orientation", 1);
            switch (j) {
                case 6:
                    i = 90;
                    break;
                case 3:
                    i = 180;
                    break;
                case 8:
                    i = 270;
                case 4:
                case 5:
                case 7:
            }
        } catch (IOException localIOException) {
            localIOException.printStackTrace();
        }
        return i;
    }

    /**
     * 旋转图片
     *
     * @param angle  被旋转角度
     * @param bitmap 图片对象
     * @return 旋转后的图片
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        Bitmap returnBm = null;
        // 根据旋转角度，生成旋转矩阵
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            // 将原始图片按照旋转矩阵进行旋转，并得到新的图片
            returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
        }
        if (returnBm == null) {
            returnBm = bitmap;
        }
        if (bitmap != returnBm) {
            bitmap.recycle();
        }
        return returnBm;
    }

    public static BitmapFactory.Options getBitmapOptions(String paramString) {
        BitmapFactory.Options localOptions = new BitmapFactory.Options();
        localOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(paramString, localOptions);
        return localOptions;
    }

    public static byte[] bitmap2Bytes(Bitmap bm, boolean compress) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 90;
        bm.compress(Bitmap.CompressFormat.JPEG, options, baos);
        if (compress) {
            while (baos.toByteArray().length / 1024 > 30 && options != 0) {
                // 清空baos
                baos.reset();
                // 这里压缩options%，把压缩后的数据存放到baos中
                bm.compress(Bitmap.CompressFormat.JPEG, options, baos);
                options -= 10;
            }
        }
        return baos.toByteArray();
    }

    /**
     * 根据路径获得图片并压缩返回bitmap
     *
     * @param filePath
     * @return Bitmap
     */
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        //480*800
        options.inSampleSize = calculateInSampleSize(options, 450, 450);
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    public static long getBitmapsize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();

    }

    /**
     * 保存bitmap到本地
     *
     * @param bm
     * @param dir     目录
     * @param picName 文件名
     * @param quality 图片质量 1~100
     * @return
     */
    public static String saveBitmap(Bitmap bm, String dir, String picName, int quality) {
        File f = new File(dir);
        if (!f.exists()) {
            f.mkdirs();
        }
        f = new File(dir, picName);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, quality, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return f.getAbsolutePath();
    }


    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * 根据Uri 获取bitmap对象
     *
     * @param path
     * @return
     */
    public static Bitmap decodeUriAsBitmap(Context context, String path) {
        Uri uri = Uri.fromFile(new File(path));
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }

    public static void gc(ImageView... imageView) {
        for (ImageView iv : imageView) {
            if (iv != null && iv.getDrawable() != null) {
                Bitmap oldBitmap = ((BitmapDrawable) iv.getDrawable()).getBitmap();
                iv.setImageDrawable(null);
                if (oldBitmap != null) {
                    oldBitmap.recycle();
                }
            }
        }
        System.gc();
    }

    /**
     * 以最省内存的方式读取本地资源的图片
     *
     * @param context
     * @param resId
     * @return
     */
    public static Bitmap readBitmap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * 以最省内存的方式读取本地资源的图片 或者SDCard中的图片
     *
     * @param imagePath 图片在SDCard中的路径
     * @return
     */
    public static Bitmap readBitmap(String imagePath) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.ARGB_4444;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        //获取资源图片
        return BitmapFactory.decodeFile(imagePath, opt);
    }

    public static void downLoadPic(String url, final String path, final String name) {
        okhttp3.Request request = new okhttp3.Request.Builder().url(url).build();
        OkHttpClient okHttpClient = initOkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mOkHttpClient = null;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    File file = new File(path);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    File f = new File(file, name);
                    fos = new FileOutputStream(f);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                    }
                    fos.flush();
                } catch (Exception e) {
                    LogUtils.d("tang", "文件下载失败 = " + e.toString());
                } finally {
                    mOkHttpClient = null;
                    try {
                        if (is != null) is.close();
                    } catch (IOException e) {
                    }
                    try {
                        if (fos != null)
                            fos.close();

                    } catch (IOException e) {
                    }
                }
            }
        });
    }

    private static OkHttpClient initOkHttpClient() {
        if (mOkHttpClient == null) {
            mOkHttpClient = new OkHttpClient();
        }
        return mOkHttpClient;
    }

    private static OkHttpClient mOkHttpClient;


    /**
     * @param filePath 源路径
     * @param savePath 压缩后存储路径
     * @param fileName 存储名字
     * @return
     */
    public static String compressImage(String filePath, String savePath, String fileName) {
//        LogUtil.d("tang", "原大小:" + (FileUtils.getFileSize(filePath) / 1024) + "kb");
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, 1080, 1920);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        long size = ImageUtils.getBitmapsize(bitmap);
        int level = 90;
        if (size > 1024 * 1024) {//1M以及以上
            level = 30;
        } else if (size > 512 * 1024) {//0.5M-1M
            level = 60;
        } else if (size > 200 * 1024) {//0.25M-0.5M
            level = 75;
        }
        File f = new File(savePath);
        if (!f.exists()) {
            f.mkdirs();
        }
        f = new File(savePath, fileName);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, level, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        LogUtil.d("tang", "压缩后大小:" + (FileUtils.getFileSize(f.getAbsolutePath()) / 1024) + "kb");
        return f.getAbsolutePath();
    }

    /**
     * 处理旋转后的图片
     *
     * @param originpath 原图路径
     * @return 返回修复完毕后的图片路径
     */
    public static String saveRotateAndCopressPic(String originpath, String savePath, String fileName) {
        // 取得图片旋转角度
        int angle = readPictureDegree(originpath);
        // 把原图压缩后得到Bitmap对象
        Bitmap bmp = getCompressPhoto(originpath);
        // 修复图片被旋转的角度
        Bitmap bitmap = rotaingImageView(angle, bmp);

        // 保存修复后的图片并返回保存后的图片路径
        return savePhotoToSD(bitmap, savePath, fileName);
    }

    /**
     * 保存Bitmap图片在SD卡中
     * 如果没有SD卡则存在手机中
     *
     * @param bitmap 需要保存的Bitmap图片
     * @return 保存成功时返回图片的路径，失败时返回null
     */
    public static String savePhotoToSD(Bitmap bitmap, String savePath, String fileName) {
        long size = ImageUtils.getBitmapsize(bitmap);
        int level = 90;
        if (size > 1024 * 1024) {//1M以及以上
            level = 30;
        } else if (size > 512 * 1024) {//0.5M-1M
            level = 60;
        } else if (size > 200 * 1024) {//0.25M-0.5M
            level = 75;
        }
        File f = new File(savePath);
        if (!f.exists()) {
            f.mkdirs();
        }
        f = new File(savePath, fileName);
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, level, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        LogUtil.d("tang", "压缩后大小:" + (FileUtils.getFileSize(f.getAbsolutePath()) / 1024) + "kb");
        return f.getAbsolutePath();
    }

    /**
     * 把原图按1/10的比例压缩
     *
     * @param path 原图的路径
     * @return 压缩后的图片
     */
    public static Bitmap getCompressPhoto(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateInSampleSize(options, 1080, 1920);
        Bitmap bmp = BitmapFactory.decodeFile(path, options);
        options = null;
        return bmp;
    }
}