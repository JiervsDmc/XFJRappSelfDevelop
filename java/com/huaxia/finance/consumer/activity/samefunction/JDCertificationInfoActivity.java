package com.huaxia.finance.consumer.activity.samefunction;

import android.content.Intent;
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

public class JDCertificationInfoActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.submit_btn)
    private Button btn;
    @ViewInject(R.id.skip_JD)
    private TextView skipJD;
    @ViewInject(R.id.JD_name)
    private EditText JDName;
    @ViewInject(R.id.JD_psw)
    private EditText JDPsw;

    private String token;
    private String website;
    private String process_code;

    @Override
    protected int getLayout() {
        return R.layout.activity_jdcertification_info;
    }

    @Override
    protected String getTitleText() {
        return "认证信息";
    }

    @Override
    protected void setup() {
        super.setup();
        token = getIntent().getStringExtra("token");
        website = getIntent().getStringExtra("website");
        process_code = getIntent().getStringExtra("process_code");
        btn.setOnClickListener(this);
        skipJD.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.submit_btn:
                hintKb();
                uploadDate();
                break;
            case R.id.skip_JD:
                skipJD();
                break;
        }
    }

    public void skipJD() {
        Map map = new HashMap();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        map.put("token", token);
        map.put("processCode", process_code);
        ApiCaller.call(this, Constant.URL, "0018", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
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
                        if (process_code.equals("10008")||process_code.equals("30000")||process_code.equals("0")||process_code.equals("10009")) {
                            Intent intent = new Intent(JDCertificationInfoActivity.this, UserInfoActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                        }else {
                            toast("" + body.get("message"));
                        }
                    }
                } else {
                    toast(head.getResponseMsg());
                }
            }
        });

    }

    public void uploadDate() {
        if (IsNullUtils.isNull(JDName.getText().toString())) {
            toast("请输入京东账号");
            return;
        }
        if (IsNullUtils.isNull(JDPsw.getText().toString())) {
            toast("请输入京东密码");
            return;
        }
        Map map = new HashMap();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        map.put("jdAccount", JDName.getText().toString());
        map.put("jdPassword", JDPsw.getText().toString());
        map.put("token", token);
        map.put("website", website);
        map.put("processCode", process_code);
        ApiCaller.call(this, Constant.URL, "0017", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
               // toast("数据查看失败");
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                if (head.getResponseCode().equals("0000")) {
                    if (body.size() != 0) {
                        process_code = ""+body.get("process_code");
                        if (process_code.equals("10008")||process_code.equals("30000")||process_code.equals("0")||process_code.equals("10009")) {
                            Intent intent = new Intent(JDCertificationInfoActivity.this, UserInfoActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                        } else {
                            toast("" + body.get("message"));
                        }
                    }
                } else {
                    toast(head.getResponseMsg());
                }
            }
        });

    }
}
