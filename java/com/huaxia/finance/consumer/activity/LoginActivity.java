package com.huaxia.finance.consumer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.AccountMgr;
import com.huaxia.finance.consumer.storage.Constans;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.PrefMgr;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.IsNullUtils;
import com.huaxia.finance.consumer.util.LogUtil;
import com.huaxia.finance.consumer.util.MD5Util;
import com.huaxia.finance.consumer.util.RegularExpressionUtil;
import com.huaxia.finance.consumer.util.ToastUtils;
import com.huaxia.finance.consumer.view.ClearEditText;

import java.util.HashMap;
import java.util.Map;

import cn.tongdun.android.shell.FMAgent;
import okhttp3.Call;

/**
 * 登录
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    private TextView loginBtn;
    private TextView tvForgetPassword;
    private TextView tvToRegist;
    private ClearEditText etPhoneNumber;
    private  ClearEditText etPassword;
    public AccountMgr mgr;
    private String blackBox;
    private String device_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mgr = new AccountMgr(this);
        etPhoneNumber=(ClearEditText)findViewById(R.id.et_phone_number);
        etPassword=(ClearEditText)findViewById(R.id.et_password);
        loginBtn = (TextView) findViewById(R.id.login_btn);
        tvForgetPassword = (TextView) findViewById(R.id.tv_forget_password);
        tvToRegist = (TextView) findViewById(R.id.tv_to_regist);
        loginBtn.setOnClickListener(this);
        tvForgetPassword.setOnClickListener(this);
        tvToRegist.setOnClickListener(this);
        etPhoneNumber.setText(mgr.getVal(UniqueKey.APP_MOBILE));
        etPassword.setText(mgr.getVal(UniqueKey.APP_PASSWORD));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                blackBox = FMAgent.onEvent(LoginActivity.this);
                try {
                    TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                    device_id = tm.getDeviceId();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                LogUtil.getLogutil().d("设备号"+ device_id);
                //校验是否为黑名单，再登录
                checkAccount();
                //toLogin();
                break;

            case R.id.tv_forget_password:
                Intent intentForget = new Intent(this, ForgetPasswordActivity.class);
                startActivity(intentForget);
                break;

            case R.id.tv_to_regist:
                Intent intentRegist = new Intent(this, RegistActivity.class);
                startActivity(intentRegist);

                break;
        }
    }
    //登录
    private void toLogin() {
       final String phoneNumber=etPhoneNumber.getText().toString().trim();
        final String passWord=etPassword.getText().toString().trim();
        if(TextUtils.isEmpty(phoneNumber)) {
            ToastUtils.showSafeToast(this, "手机号码不能为空");
            return;
        }else if(!RegularExpressionUtil.isMobileNO(phoneNumber)) {
            ToastUtils.showSafeToast(this, "手机号码有误，请重新输入");
            return;
        }else if(TextUtils.isEmpty(passWord)) {
            ToastUtils.showSafeToast(this, "密码不能为空");
            return;
        }else if(!RegularExpressionUtil.isPassword(passWord)) {
            ToastUtils.showSafeToast(this,"密码须为四位以上十二位以下数字或字母");
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("phoneNo",phoneNumber);
        map.put("blackBox",blackBox);
        map.put("device_id",device_id);
        StringBuilder strBui = new StringBuilder();
        map.put("password", MD5Util.md5(strBui.append(Constans.ACCESS_KEY).append(passWord).toString()).toUpperCase());
        ApiCaller.call(this, Constant.URL, "0004", "authService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                LogUtil.getLogutil().d("登录结果是"+response);
                if(head.getResponseCode().contains("0000")) {
                    mgr.putString(UniqueKey.APP_USER_ID,body.get("userId").toString());
                    mgr.putString(UniqueKey.APP_MOBILE, phoneNumber);
//                    ToastUtils.showSafeToast(LoginActivity.this, head.getResponseMsg());
                    getRole(""+body.get("userId"));
                }else {
                    ToastUtils.showSafeToast(LoginActivity.this,head.getResponseMsg());
                }
            }

        });

    }
    public void getRole(String uuid){
        Map<String, Object> map = new HashMap<>();
        map.put("userUuid",uuid);
        ApiCaller.call(this, Constant.URL, "0026", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                LogUtil.getLogutil().d("个人信息结果是" + response);
                if (head.getResponseCode().contains("0000") && !body.isEmpty()) {
                    if (body.size()!=0){
                        Map map = (Map) body.get("result");
                        /*if(IsNullUtils.isNull(""+map.get("identityTag"))){
                            ToastUtils.showSafeToast(LoginActivity.this, "登录失败，请检查网络");
                            return;
                        }*/
                        mgr.putString(UniqueKey.ROLE,""+map.get("identityTag"));
                    }
                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    mgr.clear();
                    ToastUtils.showSafeToast(LoginActivity.this, "登录失败，请检查网络");
                }
            }

        });
    }

    /**
     * 校验是否为黑名单再登录
     */
    public void checkAccount() {
        final String phoneNumber=etPhoneNumber.getText().toString().trim();
        if(TextUtils.isEmpty(phoneNumber)) {
            ToastUtils.showSafeToast(this, "手机号码不能为空");
            return;
        }else if(!RegularExpressionUtil.isMobileNO(phoneNumber)) {
            ToastUtils.showSafeToast(this, "手机号码有误，请重新输入");
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("mobilePhone",phoneNumber);
        ApiCaller.call(this, Constant.URL, "0046", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null){

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                if (head.getResponseCode().contains("0000")) {
                    toLogin();
                }else if (head.getResponseCode().contains("0001")){
                    //弹窗告知客户该账号为黑名单账户
                    new AlertDialog.Builder(LoginActivity.this).setTitle("提示")
                            .setMessage("您的账号已暂时停用，有问题请打客户电话")
                            .setCancelable(false)
                            .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();
                }else {
                    ToastUtils.showSafeToast(LoginActivity.this,head.getResponseMsg());
                }
            }
        });
    }

    private long exitTime = 0;

    /**
     * 双击退出程序
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 1000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
                return false;
            } else {
                System.exit(0);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
