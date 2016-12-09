package com.huaxia.finance.consumer.activity.protocol;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
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
import com.huaxia.finance.consumer.storage.Constans;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.LogUtil;
import com.huaxia.finance.consumer.util.MD5Util;
import com.huaxia.finance.consumer.util.ToastUtils;
import com.huaxia.finance.consumer.util.json.JsonUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.SimpleFormatter;

/**
 * 壹分期咨询服务协议
 *
 */
public class ConsultationProtocolActivity extends BaseActivity implements OnClickListener {

	@ViewInject(R.id.btn_new)
	private TextView mBtnNew;
	@ViewInject(R.id.ll_loading)
	private LinearLayout llLoading;
	@ViewInject(R.id.wv_protocol)
	private WebView mWvProtocol;
	@ViewInject(R.id.progressBar)
	private ProgressBar mProgressbar;
	@ViewInject(R.id.tv_agree)
	private TextView tvAgree;
	@ViewInject(R.id.protocol_detail)
	private TextView protocolDetail;
	@ViewInject(R.id.ll_know)
	private LinearLayout llKnow;
	private WebSettings mWebSettings;
	private List<Map<String, Object>> listData;
	private String role;
	private String url;
	private Map map;
	private String type;
	private String orderNo;
	@Override
	protected int getLayout() {
		return R.layout.activity_loan_protocol;
	}

	@Override
	protected String getTitleText() {
		return "咨询服务协议";
	}

	@Override
	protected void setup() {
		super.setup();
		role = getIntent().getStringExtra("role");
		Bundle bundle = getIntent().getExtras();
		type=getIntent().getStringExtra("type");
		if(type.contains("2")) {
			listData= (List) bundle.getSerializable("data");
			for(int i=0;i<listData.size();i++) {
				map=listData.get(i);
				LogUtil.getLogutil().d("map的值"+map);
			}
		}else {
			orderNo=getIntent().getStringExtra("orderNo");
			llKnow.setVisibility(View.GONE);
		}
		tvAgree.setClickable(true);
		tvAgree.setOnClickListener(this);
		protocolDetail.setText("我已知悉《咨询服务协议》中所有条款并同意全部条款");
		mProgressbar.setVisibility(View.VISIBLE);
		llLoading.setVisibility(View.GONE);
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
		StringBuilder strBui = new StringBuilder();
		if(type.contains("1")) {
			strBui.append(MD5Util.md5("huaxiaxincai").toUpperCase()).append(orderNo).append(mgr.getVal(UniqueKey.APP_USER_ID)).append(new SimpleDateFormat("yyyyMMdd").format(new Date())).toString();
		}else {
			strBui.append(MD5Util.md5("huaxiaxincai").toUpperCase()).append(map.get("orderNo")).append(mgr.getVal(UniqueKey.APP_USER_ID)).append(new SimpleDateFormat("yyyyMMdd").format(new Date())).toString();
		}
		String mac = MD5Util.md5(strBui.toString()).toUpperCase();
		Map<String, Object> body = new HashMap<>();
		body.put("userUuid",mgr.getVal(UniqueKey.APP_USER_ID));
		body.put("contractType","C1");
		body.put("mac",mac);
		if(type.contains("1")) {
			body.put("orderNo",orderNo);
		}else {
			body.put("orderNo",map.get("orderNo"));
			body.put("stagesMoney",map.get("stagesMoney"));
			body.put("discountPeriod",map.get("discountPeriods"));
			body.put("period",map.get("periods"));
			body.put("borrowRate",map.get("borrowRate"));
			body.put("productType",map.get("productType"));
			body.put("merchantName",map.get("name"));
			body.put("productName",map.get("productName"));
			body.put("productPrice",map.get("productPrice"));
		}

		MessageHeader header = ApiCaller.makeHeader();
		header.setTradeType("");
		header.setTradeCode("");
		header.setSession("");
		header.setToken("");
		header.setFlowID(System.currentTimeMillis() + ApiCaller.generateString(5));
		MessageObject object = new MessageObject();
		object.setBody(body);
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
				//ToastUtils.showSafeToast(ConsultationProtocolActivity.this, "网页加载失败！");
				mProgressbar.setVisibility(View.GONE);
				llLoading.setVisibility(View.VISIBLE);
				tvAgree.setClickable(false);
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
	@Override
	public void onClick(View view) {
		super.onClick(view);
		switch (view.getId()){
			case R.id.tv_agree:
				Intent intent=new Intent(this,LoanProtocolActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("data", (Serializable) listData);
				intent.putExtras(bundle);
				intent.putExtra("role",role);
				intent.putExtra("type","2");
				startActivity(intent);
				break;
		}
	}
}
