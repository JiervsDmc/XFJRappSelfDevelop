package com.huaxia.finance.consumer.activity.samefunction;

import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.IsNullUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class SMSVerificationActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.sms_sure)
    private Button btn;
    @ViewInject(R.id.SMS_timer)
    private TextView SMSTimer;
    @ViewInject(R.id.SMS_Verification)
    private EditText Verification;

    private String token;
    private String website;
    private String mobilePhone;
    private String servicePsw;
    private String process_code;

    @Override
    protected int getLayout() {
        return R.layout.activity_smsverification;
    }

    @Override
    protected String getTitleText() {
        return "短信验证码";
    }

    @Override
    protected void setup() {
        super.setup();
        mobilePhone = getIntent().getStringExtra("mobilePhone");
        servicePsw = getIntent().getStringExtra("servicePsw");
        token = getIntent().getStringExtra("token");
        website = getIntent().getStringExtra("website");
        process_code = getIntent().getStringExtra("process_code");
        btn.setOnClickListener(this);
        SMSTimer.setOnClickListener(this);
        startCounter(60);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.sms_sure:
                hintKb();
                uploadDate();
                break;
            case R.id.SMS_timer:
                getVerificationAgain();
                break;
        }
    }

    public void getVerificationAgain() {
        Map map = new HashMap();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        map.put("mobilePhone", mobilePhone);
        map.put("servicePsw", servicePsw);
        map.put("processCode", process_code);
        ApiCaller.call(this, Constant.URL, "0013", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
//                toast("获取验证码失败");
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                if (body.size() == 0) {
                    return;
                }
                if (head.getResponseCode().equals("0000")) {
                    if (body.size() != 0) {
                        process_code = ""+body.get("process_code");
                        token = "" + body.get("token");
                        website = "" + body.get("website");
                        if(process_code.equals("30000")||process_code.equals("0")||process_code.equals("10009")){
                            Intent intent = new Intent(SMSVerificationActivity.this, UserInfoActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                        }
                    }
                    startCounter(60);
                }

            }
        });
    }

    /**
     * 获取验证码倒计时
     *
     * @param sec 倒计时时间（单位：s）
     */
    private void startCounter(int sec) {
        new CountDownTimer((sec + 1) * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int left = (int) ((millisUntilFinished - 1000) / 1000);
                SMSTimer.setText("" + left + "s");
                SMSTimer.setTextColor(getResources().getColor(R.color.app_blue_color));
                SMSTimer.setEnabled(false);
            }

            @Override
            public void onFinish() {
                SMSTimer.setText("获取验证码");
                SMSTimer.setTextColor(getResources().getColor(R.color.app_blue_color));
                SMSTimer.setEnabled(true);
            }
        }.start();
    }

    /**
     * 提交短信验证码给服务器
     */
    public void uploadDate() {
        if (IsNullUtils.isNull(Verification.getText().toString())) {
            toast("请输入短信验证码");
            return;
        }
        Map map = new HashMap();
        map.put("mobilePhone", mobilePhone);
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        map.put("servicePsw", servicePsw);
        map.put("token", token);
        map.put("website", website);
        map.put("captcha", Verification.getText().toString());
        map.put("processCode",process_code);
        ApiCaller.call(this, Constant.URL, "0037", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                //toast("数据查看失败");
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                if (head.getResponseCode().equals("0000")) {
                    if (body.size() != 0) {
                        process_code = ""+body.get("process_code");
                        token =  "" + body.get("token");
                        website = "" + body.get("website");
                        if (process_code.equals("10008")) {
                            Intent intent = new Intent(SMSVerificationActivity.this, JDCertificationInfoActivity.class);
                            intent.putExtra("token", token);
                            intent.putExtra("website", website);
                            startActivity(intent);
                            finish();
                        }else if(process_code.equals("10001")) {
                            Verification.getText().clear();
                        }else if(process_code.equals("30000")||process_code.equals("0")||process_code.equals("10009")){
                            Intent intent = new Intent(SMSVerificationActivity.this, UserInfoActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                        }else{
                            toast("" + body.get("message"));
                        }
                    }
                }
            }
        });
    }
}
