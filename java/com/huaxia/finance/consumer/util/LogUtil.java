package com.huaxia.finance.consumer.util;

import android.util.Log;

import com.huaxia.finance.consumer.application.XFJRApplication;


public class LogUtil {
	private static LogUtil logUtil;

	private boolean isShowLog = true;
	private String TAG = XFJRApplication.getInstance().getPackageName();
	private String Tag = "123";
	/**
	 * 得到LogUtil
	 * @return
	 */
	public static LogUtil getLogutil() {
		if (logUtil == null) {
			logUtil = new LogUtil();
		}
		return logUtil;
	}

	public void e(String msg) {
		if (!isShowLog)
			return;
		Log.e(TAG, msg);
	}

	public void d(String msg) {
		if (!isShowLog)
			return;
		Log.d(TAG, msg);
	}

	public void i(String msg) {
		if (!isShowLog)
			return;
		Log.i(Tag, msg);
	}
}
