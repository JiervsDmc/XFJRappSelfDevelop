package com.huaxia.finance.consumer.activity.protocol;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.bean.MessageHeader;
import com.huaxia.finance.consumer.bean.MessageObject;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.LogUtil;
import com.huaxia.finance.consumer.util.ToastUtils;
import com.huaxia.finance.consumer.util.json.JsonUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.Map;

/**
 * 壹分期注册协议
 *
 */
public class RegistProtocolActivity extends BaseActivity {

	@ViewInject(R.id.btn_new)
	private TextView mBtnNew;
	@ViewInject(R.id.ll_loading)
	private LinearLayout llLoading;
	@ViewInject(R.id.wv_protocol)
	private WebView mWvProtocol;
	@ViewInject(R.id.progressBar)
	private ProgressBar mProgressbar;
	private WebSettings mWebSettings;
	private int type;
	private String url;

	@Override
	protected int getLayout() {
		return R.layout.activity_protocol;
	}

	@Override
	protected String getTitleText() {
		return "";
	}


	@Override
	protected void setup() {
		super.setup();
		type=getIntent().getIntExtra("type",0);
		if (type == 1) {
			setTitle("壹分期注册协议");
		}else {
			setTitle("壹分期隐私协议");
		}
		LogUtil.getLogutil().d("值"+type);
		mProgressbar.setVisibility(View.VISIBLE);
		llLoading.setVisibility(View.INVISIBLE);
		// 初始化WebView,设置WebView属性
		initWebView();
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("SetJavaScriptEnabled") private void initWebView() {
		// 设置支持JavaScript等
		mWebSettings = mWvProtocol.getSettings();
		mWebSettings.setJavaScriptEnabled(true);
		mWebSettings.setBuiltInZoomControls(true);
		mWebSettings.setLightTouchEnabled(true);
		mWebSettings.setSupportZoom(true);
		mWebSettings.setAllowFileAccess(true);
		mWebSettings.setDomStorageEnabled(true);
		mWebSettings.setSavePassword(false);
		Map<String, Object> map = new HashMap<>();
		if(type==1) {
			map.put("contractType","C4");
		}else if(type==2) {
			map.put("contractType","C3");
		}
		MessageHeader header = ApiCaller.makeHeader();
		header.setTradeType("");
		header.setTradeCode("");
		header.setSession("");
		header.setToken("");
		header.setFlowID(System.currentTimeMillis() + ApiCaller.generateString(5));
		MessageObject object = new MessageObject();
		object.setBody(map);
		object.setHead(header);
		try {
			String msg = JsonUtils.of().toJson(object);
			StringBuilder builder=new StringBuilder();
			url=builder.append(Constant.PROTOCOL_URL).append(msg).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(Constant.PROTOCOL_URL.startsWith("file://")) {
			return;
		}
		if (Build.VERSION.SDK_INT > 10&& Build.VERSION.SDK_INT < 17) {
			mWvProtocol.removeJavascriptInterface("searchBoxJavaBridge_");
		}
		LogUtil.getLogutil().d("H5页面url"+url);
		mWvProtocol.loadUrl(url);
		mWvProtocol.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				 // 进度发生变化
		        mProgressbar.setProgress(newProgress);
		        LogUtil.getLogutil().d(newProgress+"");
			}
		});
		// webview监听事件
		mWvProtocol.setWebViewClient(new WebViewClient() {
			//网页开始加载
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				LogUtil.getLogutil().d("网页开始加载");
			}
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}
			//网页加载结束
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				 mProgressbar.setVisibility(View.GONE);
				LogUtil.getLogutil().d("网页加载结束");

			}
			@Override
		    public void onReceivedError(WebView view, int errorCode,
		    		String description, String failingUrl) {
				mWvProtocol.setVisibility(View.INVISIBLE);
				//ToastUtils.showSafeToast(RegistProtocolActivity.this, "网页加载失败！");
				mProgressbar.setVisibility(View.GONE);
				llLoading.setVisibility(View.VISIBLE);
				mBtnNew.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						mWvProtocol.setVisibility(View.VISIBLE);
						setup();
					}
				});
		    }
		});
		
	}

}
