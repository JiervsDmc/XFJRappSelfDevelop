package com.huaxia.finance.consumer.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.Date;

/**
 * Created by lipiao on 2016/7/1.
 */
public class Utils {
    public static final String TAG = "PushDemoActivity";
    public static final String RESPONSE_METHOD = "method";
    public static final String RESPONSE_CONTENT = "content";
    public static final String RESPONSE_ERRCODE = "errcode";
    protected static final String ACTION_LOGIN = "com.baidu.pushdemo.action.LOGIN";
    public static final String ACTION_MESSAGE = "com.baiud.pushdemo.action.MESSAGE";
    public static final String ACTION_RESPONSE = "bccsclient.action.RESPONSE";
    public static final String ACTION_SHOW_MESSAGE = "bccsclient.action.SHOW_MESSAGE";
    protected static final String EXTRA_ACCESS_TOKEN = "access_token";
    public static final String EXTRA_MESSAGE = "message";

    public static String logStringCache = "";
    //校验合格字符数组
    public static final String STANDARDNAME=
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890@&+.-, ~!%*()#";
    // 获取ApiKey
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (PackageManager.NameNotFoundException e) {

        }
        return apiKey;
    }

    public static void sendSms(Context ctx, String mobile, String body) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("sms:" + mobile));
        i.putExtra("sms_body", body);
        ctx.startActivity(i);
    }

    public static String getDeviceId(Context ctx) {
        /*
         * * Settings.Secure.ANDROID_ID returns the unique DeviceID
		   * Works for Android 2.2 and above
		   */
        String androidId = Settings.Secure.getString(ctx.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if (!TextUtils.isEmpty(androidId))
            return androidId;

        TelephonyManager telephonyManager = (TelephonyManager) ctx
                .getSystemService(Context.TELEPHONY_SERVICE);

		/*
        * getDeviceId() function Returns the unique device ID.
		* for example,the IMEI for GSM and the MEID or ESN for CDMA phones.
		*/
        String imei = telephonyManager.getDeviceId();
        return imei;
    }

    public static List<String> getTagsList(String originalText) {
        if (originalText == null || originalText.equals("")) {
            return null;
        }
        List<String> tags = new ArrayList<String>();
        int indexOfComma = originalText.indexOf(',');
        String tag;
        while (indexOfComma != -1) {
            tag = originalText.substring(0, indexOfComma);
            tags.add(tag);

            originalText = originalText.substring(indexOfComma + 1);
            indexOfComma = originalText.indexOf(',');
        }

        tags.add(originalText);
        return tags;
    }


    public static int dpToPx(Context ctx, int dp) {
        DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        int px = Math.round(dp
                * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static int pxToDp(Context ctx, int px) {
        DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        int dp = Math.round(px
                / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    // dp转px
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    // px转dp
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static String getFileNameFromPath(String path) {
        String filename = path.substring(path.lastIndexOf("/") + 1);
        return filename;
    }

    public static String getFileSuffixFromPath(String path) {
        String suffix = path.substring(path.lastIndexOf(".") + 1);
        return suffix;
    }

    public static void call(Context ctx, String tel) {
        String uri = "tel:" + tel;
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse(uri));
        ctx.startActivity(intent);
    }

    public static String format(String ms) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(ms);
        } catch (Exception e) {
            return "";
        }

    }

    public static String add(String foo, String bar, String third) {
        if (TextUtils.isEmpty(foo)
                && TextUtils.isEmpty(bar)
                && TextUtils.isEmpty(third)
                )
            return "";

        double a = 0;
        double b = 0;
        double c = 0;
        try {
            a = Double.valueOf(foo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            b = Double.valueOf(bar);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            c = Double.valueOf(third);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ((int) (a + b + c)) + "";
    }

    public static String add(String foo, String bar) {
        if (TextUtils.isEmpty(foo)
                && TextUtils.isEmpty(bar))
            return "";

        double a = 0;
        double b = 0;
        try {
            a = Double.valueOf(foo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            b = Double.valueOf(bar);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ((int) (a + b)) + "";
    }

    public static String getBuildTime(Context ctx) {
        try {
            ApplicationInfo ai = ctx.getPackageManager().getApplicationInfo(
                    ctx.getPackageName(), 0);
            ZipFile zf = new ZipFile(ai.sourceDir);
            ZipEntry ze = zf.getEntry("classes.dex");
            long time = ze.getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String s = sdf.format(new Date(time));
            zf.close();
            return s;
        } catch (Exception e) {
            return null;
        }
    }

    public static int getVerCode(Context context) {
        int verCode = -1;
        try {
            verCode = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionCode;
            return verCode;
        } catch (Exception e) {
            return -1;
        }
    }

    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
            return verName;
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean isServiceRunning(Context ctx, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    // 遇到大的Double，不要显示为科学计数法
    public static String cookBigDouble(Double d) {
        java.text.NumberFormat fmt = java.text.NumberFormat.getInstance();
        fmt.setGroupingUsed(false);
        return fmt.format(d);
    }

    public static boolean isSDcardExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
    public static File getSDCard() {
        return Environment.getExternalStorageDirectory();
    }
    public static String getCurrentStringDay(String format) {
        Date d = new Date(System.currentTimeMillis());
        return mFormat(d, format);
    }
    public static long getAvailableInternalMemorySize(){
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return availableBlocks*blockSize;
    }


    public static String mFormat(Date date, String pattern) {
        String dateString = "";
        if (date != null && pattern != null) {
            try {
                dateString = new SimpleDateFormat(pattern).format(date);
            } catch (IllegalArgumentException ex) {
            }
        }
        return dateString;
    }
    /**
     * 读取本地json文件
     * @param ctx
     * @param file
     * @return
     */
    public static String readTestJson(Context ctx, String file) {
        StringBuilder buf = new StringBuilder();
        InputStream json = null;
        BufferedReader in = null;
        try {
            json = ctx.getAssets().open(file);
            in = new BufferedReader(new InputStreamReader(json, "UTF-8"));
            String str = null;

            while ((str = in.readLine()) != null) {
                buf.append(str);

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return buf.toString();
    }
    /**
     * 判断网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager mgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = mgr.getAllNetworkInfo();
        if (info != null) {
            for (int i = 0; i < info.length; i++) {
                if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    return true;
            }
        }
        return false;
    }

    private static long lastClickTime;
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if ( 0 < timeD && timeD < 1000) {
            return true;
        }
        lastClickTime = time;
        return false;
    }


    /**
     * 修正不呼和格式的姓名
     * @param name
     * @return
     */
    public static String fixName(String name) {
        String fixName = "";
        String letter;
        if (name != null) {
            char[] namechars = name.trim().toCharArray();
            for (int i=0;i<namechars.length;i++) {
                StringBuilder builder = new StringBuilder();
                letter = builder.append(namechars[i]).toString();
                Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]+");
                Matcher m = pattern.matcher(letter);
                if(m.find()&&m.group(0).equals(letter)){
                    //该字符为一个汉字，不作处理
                }else {
                    if (!STANDARDNAME.contains(letter)) {
                        //如果该字符不是可用字符，则转成""
                        namechars[i] = ' ';
                    }
                }
            }
            fixName = String.valueOf(namechars);
            fixName = fixName.replaceAll(" ","");
        }
        return fixName;
    }
}
