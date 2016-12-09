package com.huaxia.finance.consumer.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.storage.AccountMgr;
import com.huaxia.finance.consumer.util.ActivityManagerUtil;
import com.lidroid.xutils.ViewUtils;
import com.umeng.analytics.MobclickAgent;
import com.zhy.http.okhttp.OkHttpUtils;

/**
 * Created by lipiao on 2016/7/5.
 */
public abstract class BaseActivity extends Activity {
    protected View view;
    TextView title;
    LinearLayout btnLeft;
    protected Button btnRight;

    public AccountMgr mgr;

    public Handler progressHandler = new ProgressHandler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getLayout());
        ActivityManagerUtil.getAppManager().addActivity(this);
        mgr = new AccountMgr(this);

        findViews();
        ViewUtils.inject(this);
        setup();

    }
    protected void findViews(){
        title = (TextView) this.findViewById(R.id.title);
        btnLeft = (LinearLayout) this.findViewById(R.id.back);
        btnRight = (Button) this.findViewById(R.id.btn_right);
    }
    protected void setup(){
        title.setText(getTitleText());
        btnLeft.setVisibility(showBtnLeft() ? View.VISIBLE : View.INVISIBLE);

        btnRight.setText(getBtnRightText());
        btnRight.setVisibility(showBtnRight()?View.VISIBLE:View.INVISIBLE);
    }
    protected boolean showBtnLeft() {
        return true;
    }
    protected boolean showBtnRight() {
        return false;
    }

    protected  void setTitle(String tilteN){
        title.setText(tilteN);
    }
    protected void hideBtnRight() {
        btnRight.setVisibility(View.INVISIBLE);
    }
    protected void hideBtnLeft(){
        btnLeft.setVisibility(View.INVISIBLE);
    }

    protected void setBtnRightText(String txt) {
        btnRight.setText(txt);
    }
    /**
     * 设置页面布局
     * @return
     */
    protected abstract int getLayout();

    /**
     * 设置title标题
     * @return
     */
    protected abstract String getTitleText();

    protected String getBtnRightText() {
        return null;
    }

    protected void onBtnRightClick(View view) {
    }
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                onBackPressed();
                break;
            case R.id.btn_right:
                onBtnRightClick(view);
                break;
            default:
                break;
        }
    }

    protected void toast(String text) {
        Toast t = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        t.show();
    }

    private static void updateProgress(String local_path, String url, int current, int total,
                                       boolean completed, Handler handler) {
        Message msg = new Message();
        Bundle b = new Bundle();
        b.putString("local_path", local_path);
        b.putString("url", url);
        b.putInt("current", current);
        b.putInt("total", total);
        b.putBoolean("completed", completed);
        msg.setData(b);
        handler.sendMessage(msg);
    }

    class ProgressHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle b = msg.getData();
            String url = b.getString("url");
            String local_path = b.getString("local_path");
            int current = b.getInt("current");
            int total = b.getInt("total");
            boolean completed = b.getBoolean("completed");

            if (completed)
                saveUrlToOurServer(local_path, url);
        }
    }

    protected void saveUrlToOurServer(String local_path, String url) {
        // todo
    }
    public void startActivityByIntent(Class clazz) {
        startActivity(new Intent(this, clazz));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.getInstance().cancelTag(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getName());
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getName());
        MobclickAgent.onPause(this);
    }

    /**
     * 隐藏软键盘
     */
    public void hintKb() {
        InputMethodManager imm = (InputMethodManager) this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && this.getCurrentFocus() != null) {
            if (this.getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(this.getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
