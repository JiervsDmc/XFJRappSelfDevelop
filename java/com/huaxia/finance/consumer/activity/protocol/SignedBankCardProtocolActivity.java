package com.huaxia.finance.consumer.activity.protocol;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.activity.samefunction.UserInfoActivity;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.LogUtil;
import com.huaxia.finance.consumer.util.ToastUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class SignedBankCardProtocolActivity extends BaseActivity {

    @ViewInject(R.id.btn_new)
    private TextView mBtnNew;
    @ViewInject(R.id.ll_loading)
    private LinearLayout llLoading;
    @ViewInject(R.id.wv_protocolA)
    private WebView mWvProtocol;
    @ViewInject(R.id.progressBarA)
    private ProgressBar mProgressbar;

    private WebSettings mWebSettings;

    private String bankNumber;

    private String url;
    private boolean Tag;

    @Override
    protected int getLayout() {
        return R.layout.activity_signed_bank_card_protocol;
    }

    @Override
    protected String getTitleText() {
        return "签署代扣协议";
    }

    @Override
    protected void setup() {
        super.setup();
        bankNumber = getIntent().getStringExtra("BankCard");
        mProgressbar.setVisibility(View.VISIBLE);
        llLoading.setVisibility(View.GONE);
        getURL();
    }

    public void getURL() {
        final Map map = new HashMap();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        map.put("cardNo", bankNumber);
        ApiCaller.call(this, Constant.URL, "0035", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                toast("数据提交失败");
                mWvProtocol.setVisibility(View.INVISIBLE);
                ToastUtils.showSafeToast(SignedBankCardProtocolActivity.this, "网页加载失败！");
                mProgressbar.setVisibility(View.GONE);
                llLoading.setVisibility(View.VISIBLE);
                mBtnNew.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mWvProtocol.setVisibility(View.VISIBLE);
                        setup();
                    }
                });
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                if (head.getResponseCode().equals("0000")) {
                    if (body.size() != 0) {
                        url = "" + body.get("contractUrl");
                        initWebView();
                    }
                }else {
                    mWvProtocol.setVisibility(View.INVISIBLE);
                    //ToastUtils.showSafeToast(SignedBankCardProtocolActivity.this, "网页加载失败！");
                    mProgressbar.setVisibility(View.GONE);
                    llLoading.setVisibility(View.VISIBLE);
                    mBtnNew.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            mWvProtocol.setVisibility(View.VISIBLE);
                            setup();
                        }
                    });
                }

            }
        });
    }

    public void uploadURL(String url) {
        Map map = new HashMap();
        map.put("url", url);
        ApiCaller.call(this, Constant.URL, "0036", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                toast("数据提交失败");
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                if (head.getResponseCode().equals("0000")) {
                    Intent intent = new Intent(SignedBankCardProtocolActivity.this, UserInfoActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }

            }
        });
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        // 设置支持JavaScript等
        mWebSettings = mWvProtocol.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setLightTouchEnabled(true);
        mWebSettings.setSupportZoom(true);
        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setSavePassword(false);
        if(url.startsWith("file://")) {
            return;
        }
        if (Build.VERSION.SDK_INT > 10&& Build.VERSION.SDK_INT < 17) {
            mWvProtocol.removeJavascriptInterface("searchBoxJavaBridge_");
        }
        mWvProtocol.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // 进度发生变化
                mProgressbar.setProgress(newProgress);
                LogUtil.getLogutil().d(newProgress + "");
            }
        });
        // webview监听事件
        mWvProtocol.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                LogUtil.getLogutil().d("网页开始加载");
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url1) {
                if(Tag) {
                    uploadURL(url1);
                    return Tag;
                }else {
                    return Tag;
                }
            }

            @Override
            public void onPageFinished(WebView view, String urll) {
                mProgressbar.setVisibility(View.GONE);
                Tag = true;
                LogUtil.getLogutil().d("网页加载结束");
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                mWvProtocol.setVisibility(View.INVISIBLE);
                ToastUtils.showSafeToast(SignedBankCardProtocolActivity.this, "网页加载失败！");
                mProgressbar.setVisibility(View.GONE);
                llLoading.setVisibility(View.VISIBLE);
                mBtnNew.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        mWvProtocol.setVisibility(View.VISIBLE);
                        setup();
                    }
                });
            }
        });
        mWvProtocol.loadUrl(url);

    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWvProtocol.canGoBack()) {
            mWvProtocol.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
