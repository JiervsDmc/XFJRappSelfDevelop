package com.huaxia.finance.consumer.activity.samefunction;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.ToastUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * 查询密码
 */
public class QueryPasswordActivity extends BaseActivity implements View.OnClickListener {
    private String token;
    private String website;
    private String mobilePhone;
    private String servicePsw;
    private String process_code;
    @ViewInject(R.id.tv_confirm)
    private TextView tvConfirm;
    @ViewInject(R.id.et_query_password)
    private TextView etQueryPassword;
    @Override
    protected int getLayout() {
        return R.layout.activity_query_password;
    }

    @Override
    protected String getTitleText() {
        return "查询密码";
    }

    @Override
    protected void setup() {
        super.setup();
        mobilePhone = getIntent().getStringExtra("mobilePhone");
        servicePsw = getIntent().getStringExtra("servicePsw");
        token = getIntent().getStringExtra("token");
        website = getIntent().getStringExtra("website");
        process_code = getIntent().getStringExtra("process_code");
        tvConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.tv_confirm:
                getConfirm();
                break;
        }
    }

    private void getConfirm() {
        String queryPassword=etQueryPassword.getText().toString();
        if(TextUtils.isEmpty(queryPassword)) {
            ToastUtils.showSafeToast(this,"查询密码不能为空");
            return;
        }
        Map map = new HashMap();
        map.put("mobilePhone", mobilePhone);
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        map.put("servicePsw", servicePsw);
        map.put("token", token);
        map.put("website", website);
        map.put("queryPsw", queryPassword);
        map.put("processCode",process_code);
        ApiCaller.call(this, Constant.URL, "0037", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
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
                            Intent intent = new Intent(QueryPasswordActivity.this, JDCertificationInfoActivity.class);
                            intent.putExtra("token", token);
                            intent.putExtra("website", website);
                            intent.putExtra("process_code", process_code);
                            startActivity(intent);
                            finish();
                        }else if(process_code.equals("30000")||process_code.equals("0")||process_code.equals("10009")){
                            Intent intent = new Intent(QueryPasswordActivity.this, UserInfoActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                        }else{
                            toast("" + body.get("message"));
                        }
                    }
                }else {
                    toast("" + head.getResponseMsg());
                }
            }
        });
    }
}
