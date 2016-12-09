package com.huaxia.finance.consumer.application;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;

import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.util.Utils;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import cn.tongdun.android.shell.FMAgent;
import okhttp3.OkHttpClient;


/**
 * Created by lipiao on 2016/7/5.
 */
public class XFJRApplication extends Application {
    private static final String APP_LOG_PATH = "/XFJR/log/";
    private static XFJRApplication xfjrApplication;
    public  String versionCode;
    @Override
    public void onCreate() {
        super.onCreate();
        xfjrApplication = this;
        try {
            PackageManager mPackageManager = getPackageManager();
            PackageInfo packageInfo = mPackageManager.getPackageInfo(
                    getPackageName(), 0);
            // 获取到版本号
           versionCode = packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        MobclickAgent.openActivityDurationTrack(false);
        //注册时申请的APPID,建议在测试阶段建议设置成true，发布时设置为false。
        CrashReport.initCrashReport(getApplicationContext(), "26d76cf72d", false);
//        initException();
        OkHttpClient okHttpClient=new OkHttpClient.Builder()
                .readTimeout(30000, TimeUnit.MILLISECONDS)
                .writeTimeout(30000,TimeUnit.MILLISECONDS)
                .connectTimeout(30000,TimeUnit.MILLISECONDS)
                .build();
        OkHttpUtils.initClient(okHttpClient);

        //同盾初始化sdk,FMAgent.ENV_SANDBOX表示测试环境，FMAgent.ENV_PRODUCTION表示生产环境
        FMAgent.init(getApplicationContext(),FMAgent.ENV_SANDBOX);
    }

    public static synchronized  XFJRApplication getInstance(){
        return xfjrApplication;
    }

    /**
     * 将异常log日志保存到本地文件中
     */
    private void initException() {
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler());
    }

    private class MyExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);
            try {
                StringBuffer sb = new StringBuffer();
                Field[] fields = Build.class.getFields();
                for (Field field : fields) {
                    String value = field.get(null).toString();
                    String name = field.getName();
                    sb.append(name + ":" + value + "\n");
                }
                String errormsg = sw.toString();
                sb.append(errormsg);
                String error = sb.toString();
                if (Utils.isSDcardExist() && Constant.DEBUG) {
                    File filePath = new File(Utils.getSDCard() + APP_LOG_PATH);
                    if (!filePath.exists()) {
                        filePath.mkdirs();
                    }
                    File file = new File(filePath, Utils.getCurrentStringDay("yyyyMMddHHmmss") + ".txt");
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(error.getBytes("UTF-8"));
                    fos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

}
