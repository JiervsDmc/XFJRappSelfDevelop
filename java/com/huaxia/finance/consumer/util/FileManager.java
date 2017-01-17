package com.huaxia.finance.consumer.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by lipiao on 2016/7/1.
 * 对文件进行基本的处理
 */
public class FileManager {

    static String hx_file;

    public static Boolean fileExists(String path) {
        File file = new File(path);
        return file.exists();
    }

    /**
     * 获取随机生成的文件名称
     *
     * @param str
     * @param type 文件格式 .mp3 / .mp4 等不传则没有格式
     * @return 传入文件名+当前时间
     */
    public static String getRandom(String str, String type) {
        Long time = System.currentTimeMillis();
        String timeStr = time.toString();
        timeStr = timeStr.substring(timeStr.length() - 6);
        str = str + timeStr;
        if (null != type) {
            str = str + type;
        }
        return str;
    }

    /**
     * 初始化HXXC文件夹，用于存储HXXC资源文件
     *
     * @return 文件夹路径
     */
    public static String initFilePath() {
        if (externalMemoryAvailable()) {
            hx_file = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + File.separator + "HXXC";
        } else {
            hx_file = Environment.getRootDirectory().getAbsolutePath()
                    + File.separator + "HXXC";
        }
        File file = new File(hx_file);
        if (!file.exists()) {
            file.mkdirs();
        }
        return hx_file;
    }

    /**
     * 删除对应文件（文件夹内文件）
     *
     * @param filePath
     * @return
     */
    public static boolean deletfile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.isDirectory()) {
                for (File file2 : file.listFiles()) {
                    deletfile(file2.getAbsolutePath());
                    file2.delete();
                }
            } else {
                return file.delete();
            }
        }
        return false;
    }

    /**
     * 通过url获取文件绝对路径
     *
     * @param context
     * @param uri
     * @return the file path or null
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri)
            return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri,
                    new String[]{ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

   /* *//**
     * 获取压缩图片
     *
     * @param filePath
     * @return
     *//*
    public static Bitmap getSmallBitmap(String filePath) {

        if (!new File(filePath).exists()) {
            return null;
        }
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        //一般手机的分辨率为 480*800 ，所以我们压缩后图片期望的宽带定为480，高度设为800
        options.inSampleSize = calculateInSampleSize(options, 480, 800);
        options.inJustDecodeBounds = false;
        Bitmap bm  = BitmapFactory.decodeFile(filePath, options);
        if (bm == null) {
            return null;
        }
        int degree = readPictureDegree(filePath);
        bm = rotateBitmap(bm, degree);
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        } finally {
            try {
                if (baos != null)
                    baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bm;
    }*/

    /**
     * 获取压缩图片
     *
     * @param srcPath
     * @return
     */
    public static Bitmap getSmallBitmap(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;// 这里设置高度为800f
        float ww = 600f;// 这里设置宽度为600f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w >= h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w <= h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inPreferredConfig = Bitmap.Config.ARGB_4444;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return bitmap;
    }

    /**
     * 计算图片压缩值
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
        }

        return inSampleSize;
    }

    /**
     * 处理图片旋转
     *
     * @param path
     * @return
     */
    private static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
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
     * 旋转
     *
     * @param bitmap
     * @param rotate
     * @return
     */
    private static Bitmap rotateBitmap(Bitmap bitmap, int rotate) {
        if (bitmap == null)
            return null;

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        // Setting post rotate to 90
        Matrix mtx = new Matrix();
        mtx.postRotate(rotate);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }


    /**
     * 获取手机的可用内存
     *
     * @return
     */
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks * blockSize;
    }

    /**
     * 判断SD卡是否存在，并且是否具有读写权限
     *
     * @return
     */
    public static boolean externalMemoryAvailable() {
        try {
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 调用摄像头拍摄照片
     *
     * @param act
     * @param frag
     * @param requestCode
     * @return
     */
    public static String takePhoto(Activity act, Fragment frag, int requestCode) {
        File file = null;
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        /*
         * 如果不给EXTRA_OUTPUT参数，从Intent返回的是低解析度的照片，因为intent 无法传递太大的数据;
		 * 如果给EXTRA_OUTPUT参数，照片不会从onActivity()的intent返回，你要手动 从之前指定的Uri获取； 我们使用后者
		 */
        try {
            file = createTempImageFile();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            if (null != act)
                act.startActivityForResult(intent, requestCode);
            else
                frag.startActivityForResult(intent, requestCode);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        LogUtil.getLogutil().d("值"+file.getAbsolutePath());
        return file.getAbsolutePath();
    }

    /**
     * 创建拍摄照片保存路径
     * todo all android phones will create .jpg when taking photo?
     *
     * @return
     * @throws IOException
     */
    public static File createTempImageFile() throws IOException {
        final String PREFIX = "IMG";
        final String SUFFIX = ".jpg";
        File dir = null;
        if (!externalMemoryAvailable()) {
            if (getAvailableInternalMemorySize() / (1024 * 1024) < 20) {
                Log.d("shanghai", "拍照时手机内存不足20M");
            } else {
                dir = Environment.getDataDirectory();
            }
        } else {
            dir = new File(Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/hxxc/photo");
        }
        boolean success = true;
        if (!dir.exists()) {
            // 注意mkdir()和mkdirs()的区别
            // success = dir.mkdir();
            success = dir.mkdirs();
        }
        if (!success) {
            return null;
        }
        File f = File.createTempFile(PREFIX, SUFFIX, dir);
        return f;
    }

    /**
     * 获取uri对应的实际路径
     *
     * @param context
     * @param contentUri
     * @return
     */
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        if (contentUri.getScheme().equals("file")) {
            return contentUri.getPath();
        } else {
            String[] proj = {MediaStore.Images.Media.DATA};
            CursorLoader loader = new CursorLoader(context, contentUri, proj,
                    null, null, null);
            Cursor cursor = loader.loadInBackground();
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
    }


    /**
     * 选取本地照片
     *
     * @param act
     * @param frag
     * @param requestCode
     */
    public static void pickAPicture(Activity act, Fragment frag, int requestCode) {
        Intent intent_gallery = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        if (null != act)
            act.startActivityForResult(intent_gallery, requestCode);
        else
            frag.startActivityForResult(intent_gallery, requestCode);
    }

    /**
     * 将Bitmap转换成字符串
     *
     * @param bitmap
     * @return
     */
    public static String bitmaptoString(Bitmap bitmap) {
        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);

        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;

    }

    /**
     * 将字符串转换成Bitmap类型
     *
     * @param string
     * @return
     */
    public static Bitmap stringtoBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 获取图片类型
     *
     * @param pathandname
     * @return
     */
    public static String getFileName(String pathandname) {

        int start = pathandname.lastIndexOf(".");
        if (start != -1) {
            return pathandname.substring(start + 1);
        } else {
            return null;
        }

    }

    public static long getBitmapsize(Bitmap bitmap) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        // Pre HC-MR1
        return bitmap.getRowBytes() * bitmap.getHeight();

    }

}
