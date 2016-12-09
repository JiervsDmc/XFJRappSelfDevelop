package com.huaxia.finance.consumer.http;

import android.accounts.NetworkErrorException;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.huaxia.finance.consumer.activity.LoginActivity;
import com.huaxia.finance.consumer.application.XFJRApplication;
import com.huaxia.finance.consumer.bean.MessageHeader;
import com.huaxia.finance.consumer.bean.MessageObject;
import com.huaxia.finance.consumer.storage.AccountMgr;
import com.huaxia.finance.consumer.storage.Constans;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.ActivityManagerUtil;
import com.huaxia.finance.consumer.util.DialogUtil;
import com.huaxia.finance.consumer.util.IsNullUtils;
import com.huaxia.finance.consumer.util.LogUtil;
import com.huaxia.finance.consumer.util.MD5Util;
import com.huaxia.finance.consumer.util.ToastUtils;
import com.huaxia.finance.consumer.util.json.JsonUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.apache.http.client.HttpClient;

import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by lipiao on 2016/7/5.
 */
public class ApiCaller {
    public static final String ALLCHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static String reqString;
    private static AccountMgr mgr;
    private static boolean clean_TS;
    private static int timoutL;

    public static void call(final Context ctx, String api, String tradeCode, String tradeType, Map body, boolean cleanTS, MyStringCallback handler) {
        clean_TS = cleanTS;
        call(ctx, api, tradeCode, tradeType, body, handler);
    }

    public static void call(final Context ctx, int timout, String api, String tradeCode, String tradeType, Map body, MyStringCallback handler) {
        timoutL = timout;
        call(ctx, api, tradeCode, tradeType, body, handler);
    }

    public static void call(final Context ctx, String api, String tradeCode, String tradeType, Map body, MyStringCallback handler) {
        try {
            if (Constant.DEBUG)
                Toast.makeText(ctx, "api=" + api + "\nbody=" + JSON.toJSONString(body), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!isNetworkAvailable(ctx)) {
            Toast.makeText(ctx, "网络连接失败，请检查网络！", Toast.LENGTH_SHORT).show();
            return;
        }
        mgr = new AccountMgr(ctx);
        MessageHeader header = makeHeader();
        header.setTradeType(tradeType);
        header.setTradeCode(tradeCode);
        header.setVersion(XFJRApplication.getInstance().versionCode);
        if (clean_TS) {
            header.setToken("");
            header.setSession("");
        } else {
            header.setToken(mgr.getVal(UniqueKey.TOKEN));
            header.setSession(mgr.getVal(UniqueKey.SESSION));
        }

        header.setFlowID(System.currentTimeMillis() + generateString(10));
        MessageObject object = new MessageObject();
        object.setBody(body);
        object.setHead(header);
        Map<String, Object> req = new HashMap<>();
        Map<String, Object> reqObj = new TreeMap<>();
        reqObj.put("head", header);
        reqObj.put("body", body);
        try {
            String reqObjString = JsonUtils.of().toJson(reqObj);
            //进行MD5签名运算
            String msg = JsonUtils.of().toJson(object);
            LogUtil.getLogutil().d("msg的结果" + msg);
            StringBuilder strBui = new StringBuilder();
            strBui.append(Constans.ACCESS_KEY).append(tradeCode).append("json").append(msg).toString();
            String macString = MD5Util.md5(strBui.toString()).toUpperCase();
            LogUtil.getLogutil().d("拼接后的结果" + strBui);
            LogUtil.getLogutil().d("MD5加密过后的结果" + macString);
            req.put("JSON", reqObjString);
            req.put("MAC", macString);
            reqString = JsonUtils.of().toJson(req);
            LogUtil.getLogutil().i("请求字符串:"+reqString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (timoutL != 0) {
            OkHttpUtils.postString()
                    .url(api)
                    .mediaType(MediaType.parse("application/json;charset=utf-8"))
                    .content(reqString)
                    .build()
                    .readTimeOut(timoutL)
                    .writeTimeOut(timoutL)
                    .connTimeOut(timoutL)
                    .execute(handler);
            timoutL = 0;
        } else {
            OkHttpUtils.postString()
                    .url(api)
                    .mediaType(MediaType.parse("application/json;charset=utf-8"))
                    .content(reqString)
                    .build()
                    .execute(handler);
        }

    }

    public static class MyStringCallback extends StringCallback<Object> {

        protected Context ctx;
        private ProgressDialog dlg;
        protected boolean showConfirmDlg;
        protected DialogInterface.OnClickListener ok;
        protected DialogInterface.OnClickListener err;
        private boolean showWaitDlg;
        private boolean cancelabe;
        public static Map body = new HashMap();
        public static MessageHeader head;

        public MyStringCallback(Context ctx, boolean showWaitDlg, boolean cancelabe, boolean showConfirmDlg, DialogInterface.OnClickListener ok,
                                DialogInterface.OnClickListener err) {
            this.ctx = ctx;
            this.ctx = ctx;
            this.showConfirmDlg = showConfirmDlg;
            this.ok = ok;
            this.err = err;
            this.showWaitDlg = showWaitDlg;
            this.cancelabe = cancelabe;

        }

        @Override
        public void onError(Call call, Exception e, int id) {
            if (null != dlg) {
                dlg.dismiss();
            }
            Toast.makeText(ctx, "请检查网络", Toast.LENGTH_LONG).show();
            if (showConfirmDlg) {
                DialogUtil.inform(ctx, "网络请求失败...", false, null, null);
            }


        }

        @Override
        public void onResponse(String response, int id) {
            if (null != dlg)
                dlg.dismiss();
            if (showConfirmDlg)
                DialogUtil.inform(ctx, "成功", ok);
            try {
                Map<String, Object> objRes = JsonUtils.of().toObject(response, Map.class);
                String msgJSON = objRes.get("JSON").toString();
                MessageObject msResponseObj = JsonUtils.of().toObject(msgJSON, MessageObject.class);
                body = msResponseObj.getBody();
                LogUtil.getLogutil().i("msgJSON的值" + msgJSON);
                head = msResponseObj.getHead();
               /* StringBuilder strBui = new StringBuilder();
                strBui.append(Constans.ACCESS_KEY).append(head.getTradeCode()).append("json").append(msgJSON).toString();
                String macReturn = MD5Util.md5(strBui.toString()).toUpperCase();
                LogUtil.getLogutil().d("MD5加密过后的结果" + macReturn);*/
                if (head.getResponseCode().contains("6666")) {
                    Toast.makeText(ctx, head.getResponseMsg(), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(ctx, LoginActivity.class);
                    ActivityManagerUtil.getAppManager().finishAllActivity();
                    ctx.startActivity(intent);
                    mgr.clear();
                }
                if (clean_TS) {
                    clean_TS = false;
                } else {
                    if (!IsNullUtils.isNull(head.getSession())) {
                        mgr.putString(UniqueKey.SESSION, head.getSession());
                    }
                    if (!IsNullUtils.isNull(head.getToken())) {
                        mgr.putString(UniqueKey.TOKEN, head.getToken());
                    }
                }

            } catch (Exception e) {

            }
        }

        @Override
        public void onBefore(Request request, int id) {
            if (showWaitDlg) {
                ProgressDialog dlg = ProgressDialog.show(ctx, "", "稍等...");
                dlg.setCancelable(cancelabe);
                dlg.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        OkHttpUtils.getInstance().cancelTag(this);
                    }
                });
                this.dlg = dlg;
            }

        }

    }

    /**
     * 生成网络请求流水号
     *
     * @param length
     * @return
     */
    public static String generateString(int length) {
        StringBuffer sb = new StringBuffer();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(ALLCHAR.charAt(random.nextInt(ALLCHAR.length())));
        }
        return sb.toString();
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


    public static MessageHeader makeHeader() {
        MessageHeader header = new MessageHeader();
        header.setVersion("1.0");
        header.setChannel("cf");
        header.setDevice("android");
        header.setTradeTime("");
        header.setFlowID(System.currentTimeMillis() + "6617");
        header.setTradeCode("0002");
        header.setTradeType("authService");
        header.setMsgType("json");
        header.setResponseTime("");
        header.setResponseCode("");
        header.setResponseMsg("");
        header.setTradeStatus("");
        header.setOperatorID("test");
        return header;
    }

}
