package com.huaxia.finance.consumer.base;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.util.ScreenUtils;

/**
 * Created by mirui on 2016/11/25.
 */

public abstract class BasePopWindow {

    private final PopupWindow popupWindow;

    public BasePopWindow(Context context, Activity activity,int x,int y) {
        View customView = getResId();
//        RelativeLayout rl_content = (RelativeLayout) customView.findViewById(R.id.rl_content);
//        Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(),
//                R.anim.anim_entry);
//        rl_content.startAnimation(animFadein);

        popupWindow = new PopupWindow(customView);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(context.getResources().getDrawable(R.color.transparent));
        popupWindow.setAnimationStyle(R.style.img_pop_animstyle);
        popupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.TOP, ScreenUtils.px2dip(context,y),ScreenUtils.px2dip(context,y));
        initData();
    }

    public void dismissPop(){
        popupWindow.dismiss();
    }
    protected abstract void initData();

    protected abstract View getResId();
}
