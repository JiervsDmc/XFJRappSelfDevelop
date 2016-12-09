package com.huaxia.finance.consumer.util;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * 自定义安全的吐司
 * @author Administrator
 *
 */
public class ToastUtils {
	/**
	 * 可以在主线程和子线程同时运行
	 * @param activity
	 * @param msg
	 */
	public static void showSafeToast(final Activity activity,final String msg){
		if("main".equals(Thread.currentThread().getName())){
			Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
		}else {
			/*
			 * 在 UI 线程中运行我们的任务，如果当前线程是 UI 线程，
			 * 则立即执行，如果不是则该任务发送到 UI 线程的事件队列。
			 */
			activity.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

}
