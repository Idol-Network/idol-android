package com.yiju.ldol.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.yiju.idol.base.Constant;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by d on 2016/6/13.
 */
public class FileUtils {


    private FileUtils() {
    }

    /**
     * 多级目录是否存在，不存在则创建
     *
     * @param strFolder
     * @return
     */
    public static boolean isFolderExists(Context context, String strFolder) {
        if (strFolder == null) {
            return false;
        }
        File file = new File(strFolder);
        if (!file.exists()) {
            if (file.mkdirs()) {
                return true;
            } else {
//                ToastUtils.showToast(context, "无法创建文件夹");
                return false;
            }
        }
        return true;

    }

    private static String createFilePath(String path) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //为true的话，内置sd卡存在
            return Constant.SD_PATH + path;
        }
        return null;
    }

    public static String getImagePath(Context context, String path) {
        String filePath = createFilePath(path);
        if (isFolderExists(context, filePath)) {
            return filePath;
        }
        return "";
    }

    public static void downloadZip(String url, final String path, final String zipName, final DownloadCallback cb) {
        OkHttpClient mOkHttpClient = new OkHttpClient();
        okhttp3.Request request = new okhttp3.Request.Builder().url(url).build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtils.d("tang", "onFailure");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[1024 * 1024];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    File file = new File(path, zipName);
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                        sum += len;
                        cb.onProgress(sum);
                    }
                    fos.flush();
                    File zipFile = new File(path, zipName);
                    cb.onComplete(unZipFolder(zipFile, path));
                } catch (Exception e) {
                    cb.onComplete(false);
//                    Log.d("tang", "文件下载失败 = " + e.toString());
                } finally {
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

    /**
     * DeCompress the ZIP to the path
     *
     * @param zipFile    name of ZIP
     * @param folderPath path to be unZIP
     * @throws Exception
     */
    public static boolean unZipFolder(File zipFile, String folderPath) {
        boolean ret = true;
        try {
            ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFile));
            ZipEntry zipEntry;
            String szName = "";
            while ((zipEntry = inZip.getNextEntry()) != null) {
                szName = zipEntry.getName();
                if (zipEntry.isDirectory()) {
                    szName = szName.substring(0, szName.length() - 1);
                    File folder = new File(folderPath + File.separator + szName);
                    folder.mkdirs();
                } else {
                    File file = new File(folderPath + File.separator + szName);
                    file.createNewFile();
                    FileOutputStream out = new FileOutputStream(file);
                    int len;
                    byte[] buffer = new byte[1024];
                    while ((len = inZip.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                        out.flush();
                    }
                    out.close();
                }
            }
            inZip.close();
        } catch (IOException e) {
            ret = false;
            LogUtils.d("tang", "解压失败:" + e.getMessage() + "\n" + zipFile + "\n" + folderPath);
        } finally {
            if (zipFile.exists()) {
                boolean delete = zipFile.delete();
            } else {

            }
        }
        return ret;
    }


    public static File getRealFileName(String baseDir, String absFileName) {
        String[] dirs = absFileName.split("/");
        String lastDir = baseDir;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                lastDir += (dirs[i] + "/");
                File dir = new File(lastDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
            }
            File ret = new File(lastDir, dirs[dirs.length - 1]);
            return ret;
        } else {
            return new File(baseDir, absFileName);
        }
    }

    /***
     * 下载文件回调事件
     */
    public interface DownloadCallback {
        void onComplete(boolean succ);

        void onProgress(float per);
    }


    /**
     * 把图片压缩到200K
     *
     * @param oldpath
     *            压缩前的图片路径
     * @param newPath
     *            压缩后的图片路径
     * @return
     */
    /**
     * 把图片压缩到200K
     *
     * @param oldpath 压缩前的图片路径
     * @param newPath 压缩后的图片路径
     * @return
     */
    public static File compressFile(String oldpath, String newPath) {
        Bitmap compressBitmap = FileUtils.decodeFile(oldpath);
        Bitmap newBitmap = ratingImage(oldpath, compressBitmap);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        newBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        byte[] bytes = os.toByteArray();

        File file = null;
        try {
            file = FileUtils.getFileFromBytes(bytes, newPath);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (newBitmap != null) {
                if (!newBitmap.isRecycled()) {
                    newBitmap.recycle();
                }
                newBitmap = null;
            }
            if (compressBitmap != null) {
                if (!compressBitmap.isRecycled()) {
                    compressBitmap.recycle();
                }
                compressBitmap = null;
            }
        }
        return file;
    }

    private static Bitmap ratingImage(String filePath, Bitmap bitmap) {
        int degree = readPictureDegree(filePath);
        return rotaingImageView(degree, bitmap);
    }

    /**
     * 旋转图片
     *
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        System.out.println("angle2=" + angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 把字节数组保存为一个文件
     *
     * @param b
     * @param outputFile
     * @return
     */
    public static File getFileFromBytes(byte[] b, String outputFile) {
        File ret = null;
        BufferedOutputStream stream = null;
        try {
            ret = new File(outputFile);
            FileOutputStream fstream = new FileOutputStream(ret);
            stream = new BufferedOutputStream(fstream);
            stream.write(b);
        } catch (Exception e) {
            // log.error("helper:get file from byte process error!");
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // log.error("helper:get file from byte process error!");
                    e.printStackTrace();
                }
            }
        }
        return ret;
    }

    /**
     * 图片压缩
     *
     * @param fPath
     * @return
     */
    public static Bitmap decodeFile(String fPath) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        opts.inDither = false; // Disable Dithering mode
        opts.inPurgeable = true; // Tell to gc that whether it needs free
        opts.inInputShareable = true; // Which kind of reference will be used to
        BitmapFactory.decodeFile(fPath, opts);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        if (opts.outHeight > REQUIRED_SIZE || opts.outWidth > REQUIRED_SIZE) {
            final int heightRatio = Math.round((float) opts.outHeight
                    / (float) REQUIRED_SIZE);
            final int widthRatio = Math.round((float) opts.outWidth
                    / (float) REQUIRED_SIZE);
            scale = heightRatio < widthRatio ? heightRatio : widthRatio;//
        }
        Log.i("scale", "scal =" + scale);
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = scale;
        Bitmap bm = BitmapFactory.decodeFile(fPath, opts).copy(Bitmap.Config.ARGB_8888, false);
        return bm;
    }


    /**
     * 创建目录
     *
     * @param path
     */
    public static boolean setMkdir(String path) {
        File file = new File(path);
        if (!file.exists()) {
            LogUtils.e("file", "目录不存在  创建目录    ");
            return file.mkdirs();
        } else {
            LogUtils.e("file", "目录存在");
            return true;
        }
    }

    /**
     * 获取目录名称
     *
     * @param url
     * @return FileName
     */
    public static String getFileName(String url) {
        int lastIndexStart = url.lastIndexOf("/");
        if (lastIndexStart != -1) {
            return url.substring(lastIndexStart + 1, url.length());
        } else {
            return null;
        }
    }

    /**
     * 删除该目录下的文件
     *
     * @param path
     */
    public static boolean delFile(String path) {
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (file.exists()) {
                return file.delete();
            }
        }
        return false;
    }

    /**
     * 获取文件大小
     *
     * @param path
     * @return
     */
    public static long getFileSize(String path) {
        //传入文件路径
        File file = new File(path);
        //测试此文件是否存在
        if (file.exists()) {
            return file.length();
        } else {
            return 0;
        }
    }

    /**
     * @param path 文件夹路径
     */
    public static void deleteAllFiles(String path) {
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            if (!file.exists()) {
                return;
            }
            File[] files = file.listFiles();
            for (File f : files) {
                if (!f.isDirectory()) {
                    f.delete();
                } else {
                    deleteAllFiles(f.getAbsolutePath());
                }
            }
        }
    }

    /**
     * @param strcontent
     * @param filePath
     * @param fileName
     */
    // 将字符串写入到文本文件中
    public static void writeTxtToFile(String strcontent, String filePath, String fileName) {
        //生成文件夹之后，再生成文件，不然会出错
        String strFilePath = filePath + fileName;
        // 每次写入时，都换行写
        String strContent = strcontent + "\r\n";
        try {
            File file1 = new File(filePath);
            if (!file1.exists()) {
                file1.mkdirs();
            }
            file1 = new File(filePath, fileName);
            RandomAccessFile raf = new RandomAccessFile(file1, "rwd");
            raf.seek(file1.length());
            raf.write(strContent.getBytes());
            raf.close();
        } catch (Exception e) {
            Log.e("TestFile", "Error on write File:" + e);
        }
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public void copyFile(String oldPath, String newPath) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(oldPath);
            if (oldfile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                int length;
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread; //字节数 文件大小
                    System.out.println(bytesum);
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }
}




