package com.huaxia.finance.consumer.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;


import java.util.Stack;

/**
 * Created by lipiao on 2016/7/5.
 * Activity的管理类
 */
public class ActivityManagerUtil {
	private static Stack<Activity> activityStack;
	private static ActivityManagerUtil instance;
	private ActivityManagerUtil() {}
	public static ActivityManagerUtil getAppManager() {
		if (instance == null) {
			instance = new ActivityManagerUtil();
		}
		return instance;
	}
	public void addActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
		LogUtil.getLogutil().d("add new activity :" + activity.getLocalClassName());
	}

	public Activity currentActivity() {
		Activity activity = activityStack.lastElement();
		return activity;
	}

	public void finishActivity() {
		Activity activity = activityStack.lastElement();
		finishActivity(activity);
	}

	public void finishActivity(Activity activity) {
		
		if (!IsNullUtils.isNull(activity)) {
			activityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}
	
	public void removeActivity(Activity activity){
		LogUtil.getLogutil().d("removeActivity activity :" + activity.getLocalClassName());
		if(activityStack.contains(activity)){
			activityStack.remove(activity);
		}
	}
	
	public void finishActivity(Class<?> cls) {
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
			}
		}
	}

	public void finishAllActivity() {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (!IsNullUtils.isNull(activityStack.get(i))) {
				activityStack.get(i).finish();
			}
		}
		activityStack.clear();
	}


	public void finshToActivity(Class<?> cls){
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				return;
			}
		}
	}
	@SuppressLint("ServiceCast")
	public void AppExit(Context context) {
		try {
			finishAllActivity();
			ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			activityManager.killBackgroundProcesses(context.getPackageName());
			System.exit(0);
		} catch (Exception e) {
		}
	}
	public int getSize(){
		return activityStack.size();
	}
}
