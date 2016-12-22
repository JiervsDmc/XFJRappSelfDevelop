package com.huaxia.finance.consumer.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Toast;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.activity.LoginActivity;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.AccountMgr;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.util.LogUtil;
import com.huaxia.finance.consumer.util.ToastUtils;
import com.huaxia.finance.consumer.util.Utils;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * 退出登录选择框
 */
public class LogOutCheckDialog extends Dialog implements OnClickListener {

	private Activity mActivity;
	private Dialog dialog;
	private AccountMgr mgr;
	public LogOutCheckDialog(Activity activity) {
		super(activity);
		this.mActivity = activity;
		initView(mActivity);
		mgr = new AccountMgr(mActivity);
	}

	@SuppressWarnings("deprecation")
	private void initView(Context context) {

		View view = LayoutInflater.from(context).inflate(
				R.layout.layout_exit_dialog, null);
		view.findViewById(R.id.tv_cancel).setOnClickListener(this);
		view.findViewById(R.id.tv_ok).setOnClickListener(this);
		dialog = new Dialog(context, R.style.Dialog);
		dialog.setContentView(view);
		dialog.show();
	
		/*WindowManager manager = ((Activity) context).getWindowManager();
		Display display = manager.getDefaultDisplay(); 
		int width = display.getWidth();
		int height = display.getHeight();*/
		/*int width = 720;
		int height = 540;*/
		dialog.getWindow().setGravity(Gravity.CENTER);
		//dialog.getWindow().setLayout(width, height);

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.tv_cancel:
			dialog.dismiss();
			break;
		case R.id.tv_ok:
			if (!Utils.isNetworkAvailable(mActivity)) {
				mgr.clear();
				Intent intent = new Intent(mActivity, LoginActivity.class);
				mActivity.startActivity(intent);
				mActivity.finish();
				ToastUtils.showSafeToast(mActivity,"退出登录成功");
				dialog.dismiss();
				return;
			}
			logOut();
			break;
		default:
			break;
		}
	}
	private void logOut(){
		Map<String, Object> map = new HashMap<>();
		ApiCaller.call(mActivity, Constant.URL, "0007", "authService", map, new ApiCaller.MyStringCallback(mActivity, true, false, false, null, null) {
			@Override
			public void onError(Call call, Exception e, int id) {
				//super.onError(call, e, id);
				mgr.clear();
				Intent intent = new Intent(mActivity, LoginActivity.class);
				mActivity.startActivity(intent);
				mActivity.finish();
				ToastUtils.showSafeToast(mActivity,"退出登录成功");
				dialog.dismiss();
				return;
			}

			@Override
			public void onResponse(String response, int id) {
				super.onResponse(response, id);
				LogUtil.getLogutil().d("退出登录结果是"+response);
				mgr.clear();
				Intent intent = new Intent(mActivity, LoginActivity.class);
				mActivity.startActivity(intent);
				mActivity.finish();
				ToastUtils.showSafeToast(mActivity,head.getResponseMsg());
				dialog.dismiss();
				return;
			}

		});
	}
	


	public void show() {
		if (dialog != null) {
			dialog.show();

		}
	}

	public boolean isShowing() {
		if (dialog != null) {
			return dialog.isShowing();
		}
		return false;
	}

	public void hides() {
		if (dialog != null) {
			if (dialog.isShowing()) {
				dialog.hide();
			}
		}
	}

	public void dismiss() {

		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
			dialog = null;
		}
	}


}
