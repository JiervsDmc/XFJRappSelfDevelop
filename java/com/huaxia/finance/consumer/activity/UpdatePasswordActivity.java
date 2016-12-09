package com.huaxia.finance.consumer.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.Constans;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.ActivityManagerUtil;
import com.huaxia.finance.consumer.util.IsNullUtils;
import com.huaxia.finance.consumer.util.MD5Util;
import com.huaxia.finance.consumer.util.RegularExpressionUtil;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class UpdatePasswordActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.up_login_btn)
    private Button btn;
    @ViewInject(R.id.update_password_LL)
    private LinearLayout updateLL;
    @ViewInject(R.id.update_password_success_LL)
    private LinearLayout successLL;
    @ViewInject(R.id.old_pws)
    private EditText oldPws;
    @ViewInject(R.id.new_pws)
    private EditText newPws;
    @ViewInject(R.id.new_pws_ag)
    private EditText newPwsA;


    private boolean isUpdate;


    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            String s = mgr.getVal(UniqueKey.APP_MOBILE);
            mgr.clear();
            mgr.putString(UniqueKey.APP_MOBILE, s);
            ActivityManagerUtil.getAppManager().finishAllActivity();
            Intent intent = new Intent(UpdatePasswordActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    };

    @Override
    protected int getLayout() {
        return R.layout.activity_update_password;
    }

    @Override
    protected String getTitleText() {
        return "修改密码";
    }

    @Override
    protected void setup() {
        super.setup();
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.up_login_btn:
                hintKb();
                uploadDate();
                break;
        }
    }

    public void uploadDate() {
        if (IsNullUtils.isNull(oldPws.getText().toString())) {
            toast("请输入原始密码");
            return;
        } else if (!RegularExpressionUtil.isPassword(oldPws.getText().toString())) {
            toast("原始密码须为四位以上十二位以下数字或字母");
            return;
        }
        if (IsNullUtils.isNull(newPws.getText().toString())) {
            toast("请输入新密码");
            return;
        } else if (!RegularExpressionUtil.isPassword(newPws.getText().toString())) {
            toast("新密码须为四位以上十二位以下数字或字母");
            return;
        }
        if (IsNullUtils.isNull(newPwsA.getText().toString())) {
            toast("请再次输入新密码");
            return;
        } else if (!RegularExpressionUtil.isPassword(newPwsA.getText().toString())) {
            toast("确认新密码须为四位以上十二位以下数字或字母");
            return;
        }
        if (oldPws.getText().toString().equals(newPws.getText().toString())) {
            toast("新密码不能和原密码相同");
            return;
        }
        if (!newPws.getText().toString().equals(newPwsA.getText().toString())) {
            toast("两次新密码不一致");
            return;
        }
        Map map = new HashMap();
        map.put("userId", mgr.getVal(UniqueKey.APP_USER_ID));
        StringBuilder strBui = new StringBuilder();
        map.put("oldPassword", MD5Util.md5(strBui.append(Constans.ACCESS_KEY).append(oldPws.getText().toString()).toString()).toUpperCase());
        StringBuilder strBu = new StringBuilder();
        map.put("password", MD5Util.md5(strBu.append(Constans.ACCESS_KEY).append(newPws.getText().toString()).toString()).toUpperCase());
        StringBuilder strB = new StringBuilder();
        map.put("againPassword", MD5Util.md5(strB.append(Constans.ACCESS_KEY).append(newPwsA.getText().toString()).toString()).toUpperCase());
        ApiCaller.call(this, Constant.URL, "0009", "authService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                toast("网络异常");
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                if (head.getResponseCode().equals("0000")) {
                    isUpdate = true;
                    updateView();
                } else{
                    toast(head.getResponseMsg());
                }
            }
        });

    }

    private void updateView() {
        hideBtnLeft();
        setTitle("修改成功");
        updateLL.setVisibility(View.GONE);
        successLL.setVisibility(View.VISIBLE);
        handler.postDelayed(runnable, 2000);
        mgr.putString(UniqueKey.APP_PASSWORD, "");
        mgr.putString(UniqueKey.ROLE, "");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (handler != null) {
                handler.removeCallbacks(runnable);
            }
            if (isUpdate) {
                ActivityManagerUtil.getAppManager().finishAllActivity();
                Intent intent = new Intent(UpdatePasswordActivity.this, LoginActivity.class);
                startActivity(intent);
            } else {
                onBackPressed();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
