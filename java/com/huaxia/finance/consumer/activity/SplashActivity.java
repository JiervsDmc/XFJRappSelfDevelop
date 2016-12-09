package com.huaxia.finance.consumer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.AccountMgr;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.LogUtil;
import com.huaxia.finance.consumer.util.MD5Util;
import com.huaxia.finance.consumer.util.RegularExpressionUtil;
import com.huaxia.finance.consumer.util.ToastUtils;
import com.huaxia.finance.consumer.util.UpdateAppUtil;
import com.lidroid.xutils.ViewUtils;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by lipiao on 2016/7/5.
 * logo页面
 */
public class SplashActivity extends Activity {

    private int version;
    private ProgressDialog progressDialog;
    private String apkUrl;
    private UpdateAppUtil updateAppUtil;
    private AccountMgr mgr;
    private String updateMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        ViewUtils.inject(this);
        mgr = new AccountMgr(this);
        updateAppUtil = new UpdateAppUtil(this);
        // TODO: 2016/8/17 测试注释，正式上线需打开
        getCurrentVersion();
       checkAppVersion();
        // TODO: 2016/8/17 测试用，正式上线续注释
       //goon(SplashActivity.this);

    }


    public void getCurrentVersion() {
        PackageManager packageManager = getPackageManager();
        PackageInfo packageInfo;
        try {
            packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            version = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void checkAppVersion() {
        Toast.makeText(SplashActivity.this, "正在检查更新", Toast.LENGTH_SHORT).show();
        Map map = new HashMap();
        map.put("type","android");
        map.put("versionNumber", version);
        //TODO:根据陈善华要求，无需登录信息的接口传入的token和session为空，需要更改
        ApiCaller.call(this, Constant.URL, "0033", "appService", map,true, new ApiCaller.MyStringCallback(this, false, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                goon(SplashActivity.this);
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                if (head.getResponseCode().equals("0000")) {
                    //进行网络请求获取是否有版本更新 todo:网络请求成功发送消息
                    if(body.size()==0){//当前版本为最新版本
                        goon(SplashActivity.this);
                        return;
                    }
                    updateMsg = ""+body.get("contents");
                    apkUrl = ""+body.get("downloadUrl");
                   // 10：不强制，20：强制升级
                    if(body.get("forceUpdate").equals("10")){
                        handler1.sendEmptyMessage(1);
                    }else if(body.get("forceUpdate").equals("20")){
                        handler1.sendEmptyMessage(0);
                    }

                }else{
                    goon(SplashActivity.this);
                }
            }
        });

    }

    private Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what==1) {//如果需要更新弹出框
                showUpdateDialog();
            }else if(msg.what==0){
                forceUpdateDialog();
            }

        }
    };


    /**
     * 强制更新弹出框
     */
    private void forceUpdateDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("有版本可以更新");
        builder.setMessage(updateMsg.replace("\\n","\n"));
        //builder.setMessage(Html.fromHtml(updateMsg));
        //builder.setMessage("1 修复一次性服务费为0时的显示问题;\n1 修复一次性服务费为0时的显示问题;");
        builder.setCancelable(false);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    downFile(apkUrl);
                } else {
                    Toast.makeText(SplashActivity.this, "SD卡不可用，请插入SD卡", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.create().show();
    }

    /**
     * 有更新显示弹出框
     */
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("有版本可以更新");
        //builder.setMessage(updateMsg);
        builder.setMessage(updateMsg.replace("\\n","\n"));
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    downFile(apkUrl);
                } else {
                    Toast.makeText(SplashActivity.this, "SD卡不可用，请插入SD卡", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goon(SplashActivity.this);
            }
        });
        builder.create().show();
    }

    /**
     * 下载apk
     * @param url
     */
    void downFile(final String url) {
        progressDialog = new ProgressDialog(SplashActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("正在下载");
        progressDialog.setMessage("请稍后...");
        progressDialog.setProgress(0);
        progressDialog.setMax(0);
        progressDialog.setProgressNumberFormat("%1d M/%2d M");
        progressDialog.show();
        progressDialog.setCancelable(false);
        updateAppUtil.downLoadFile(url, progressDialog, handler1);
    }

    /**
     * 跳转到登录页面
     * 判断toke和seeion是否为空
     * @param context
     */
    private void goon(final Context context) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(mgr.getVal(UniqueKey.APP_MOBILE)!=null&&mgr.getVal(UniqueKey.TOKEN)!=null&&mgr.getVal(UniqueKey.SESSION)!=null&&mgr.getVal(UniqueKey.ROLE)!=null) {
                    String acounnt = mgr.getVal(UniqueKey.APP_MOBILE);
                    checkAccount(acounnt);
//                   Intent intent = new Intent(SplashActivity.this, MainActivity.class);
//                   context.startActivity(intent);
//                   (Activity) context).finish();
                }else {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            }
        }, 2000);
    }

    /**
     * 校验是否为黑名单
     */
    public void checkAccount(String phoneNumber) {
        final String mobilePhone = phoneNumber;
        Map<String, Object> map = new HashMap<>();
        map.put("mobilePhone",mobilePhone);
        ApiCaller.call(this, Constant.URL, "0046", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null){

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                if (head.getResponseCode().contains("0000")) {
                    //如果账号不为黑名单，继续免登陆机制登录
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    SplashActivity.this.startActivity(intent);
                    SplashActivity.this.finish();
                }else{
                    //否则跳登陆界面
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    SplashActivity.this.startActivity(intent);
                    SplashActivity.this.finish();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);
    }
}
