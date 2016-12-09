package com.huaxia.finance.consumer.activity.protocol;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.activity.freelance.FreelanceAttachmentUploadActivity;
import com.huaxia.finance.consumer.activity.student.StudentAttachmentUploadActivity;
import com.huaxia.finance.consumer.activity.worker.WorkerAttachmentUploadActivity;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.bean.MessageHeader;
import com.huaxia.finance.consumer.bean.MessageObject;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.ActivityManagerUtil;
import com.huaxia.finance.consumer.util.BaiduLocationUtil;
import com.huaxia.finance.consumer.util.LocationUtils;
import com.huaxia.finance.consumer.util.LogUtil;
import com.huaxia.finance.consumer.util.MD5Util;
import com.huaxia.finance.consumer.util.ToastUtils;
import com.huaxia.finance.consumer.util.json.JsonUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * 壹分期借款合同
 *
 */
public class AddLoanProtocolActivity extends BaseActivity implements OnClickListener {

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
	@ViewInject(R.id.ll_know)
	private LinearLayout llKnow;
	private WebSettings mWebSettings;
	private Map map;
	private String url;
	private String orderNo;

	private String type;
	//定位纬经度
	private String gpsCode;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = null;

	@Override
	protected int getLayout() {
		return R.layout.activity_loan_protocol;
	}

	@Override
	protected String getTitleText() {
		return "补充壹分期借款合同";
	}

	@Override
	protected void setup() {
		super.setup();
		orderNo = getIntent().getStringExtra("orderNo");
		type = getIntent().getStringExtra("type");
		tvAgree.setClickable(true);
		tvAgree.setOnClickListener(this);
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

		if(Constant.PROTOCOL_URL.startsWith("file://")) {
			return;
		}
		if (Build.VERSION.SDK_INT > 10&& Build.VERSION.SDK_INT < 17) {
			mWvProtocol.removeJavascriptInterface("searchBoxJavaBridge_");
		}

		//如果是从补充协议进来的则已有url
		StringBuilder strBui = new StringBuilder();
		if(type.contains("1")) {
			llKnow.setVisibility(View.GONE);
		}else {
			llKnow.setVisibility(View.VISIBLE);
		}
		strBui.append(MD5Util.md5("huaxiaxincai").toUpperCase()).append(orderNo).append(mgr.getVal(UniqueKey.APP_USER_ID)).append(new SimpleDateFormat("yyyyMMdd").format(new Date())).toString();
		String mac = MD5Util.md5(strBui.toString()).toUpperCase();
			//得到要加载的url
			Map<String, Object> body = new HashMap<>();
			body.put("orderNo",orderNo);
			body.put("userUuid",mgr.getVal(UniqueKey.APP_USER_ID));
			body.put("mac", mac);

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
				url=builder.append(Constant.PROTOCOL1_URL).append(msg).toString();
			} catch (Exception e) {
				e.printStackTrace();
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
				//ToastUtils.showSafeToast(LoanProtocolActivity.this, "网页加载失败！");
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
				openGPSSettting();
				break;
		}
	}

	private void agree() {
		map = new HashMap();
		map.put("orderNo",orderNo);
		map.put("userUuid",mgr.getVal(UniqueKey.APP_USER_ID));
		map.put("gpsCode",gpsCode);
		ApiCaller.call(this, Constant.URL, "0023", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                if(head.getResponseCode().contains("0000")) {
					LogUtil.getLogutil().d(body.toString());
					onBackPressed();
					ToastUtils.showSafeToast(AddLoanProtocolActivity.this,"补充协议成功");
                }else {
					ToastUtils.showSafeToast(AddLoanProtocolActivity.this,head.getResponseMsg());
				}
            }

        });
	}

	/**
	 * 判断GSP模块是否存在或者开启
	 */
	protected void openGPSSettting() {
		LocationManager alm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		if (alm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			agree();
			return;
		}

		toast("请开启GPS!");
		new AlertDialog.Builder(AddLoanProtocolActivity.this).setTitle("提示")
				.setMessage("是否开启GPS获取地理位置")
				.setCancelable(false)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivityForResult(intent, 3);
					}
				}).show();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			StringBuffer sb = new StringBuffer();
			gpsCode = sb.append(location.getLongitude()).append(",").append(location.getLatitude()).toString();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		//初始化定位
		mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
		myListener = new MyLocationListener();
		mLocationClient.registerLocationListener( myListener );    //注册监听函数
		//开启定位
		LocationClientOption option = BaiduLocationUtil.initLocation();
		mLocationClient.setLocOption(option);
		mLocationClient.start();
	}

	@Override
	protected void onStop() {
		super.onStop();
		//关闭定位
		mLocationClient.stop();
		mLocationClient.unRegisterLocationListener(myListener);
	}
}
