package com.huaxia.finance.consumer.activity.samefunction;

import android.content.Intent;
import android.text.TextPaint;
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
import com.huaxia.finance.consumer.util.RegularExpressionUtil;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class CertificationInfoActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.certification_now)
    private Button btn;
    @ViewInject(R.id.method_get_SMS)
    private TextView textView;
    @ViewInject(R.id.certification_mobil)
    private TextView mobilTV;
    @ViewInject(R.id.certification_psw)
    private EditText pswTV;


    private String mobil;
    private String process_code;


    @Override
    protected int getLayout() {
        return R.layout.activity_certification_info;
    }

    @Override
    protected String getTitleText() {
        return "认证信息";
    }

    @Override
    protected void setup() {
        super.setup();
        mobil = getIntent().getStringExtra("Mobil");
        if (IsNullUtils.isNull(mobil)) {
            toast("请填写身份认证信息");
            return;
        }
        mobilTV.setText(mobil);
        TextPaint tp = textView.getPaint();
        tp.setFakeBoldText(true);
        btn.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.certification_now:
                hintKb();
                uploadDate();
                break;
        }
    }


    public void uploadDate() {
        Map map = new HashMap();
        if (IsNullUtils.isNull(pswTV.getText().toString())) {
            toast("请输入服务密码");
            return;
        }
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        map.put("mobilePhone", mobilTV.getText().toString());
        map.put("servicePsw", pswTV.getText().toString());
        map.put("processCode",process_code);
        ApiCaller.call(this, 120000 ,Constant.URL, "0013", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                //toast("数据查看失败");
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                if (head.getResponseCode().equals("0000")) {
                    if (body.size() != 0&&!IsNullUtils.isNull(body.get("process_code"))) {
                        process_code = ""+body.get("process_code");
                        if (process_code.equals("10008")) {//数据开始采集
                            //联通直接跳过短信认证
                            Intent intent = new Intent(CertificationInfoActivity.this, JDCertificationInfoActivity.class);
                            intent.putExtra("token", "" + body.get("token"));
                            intent.putExtra("website", "" + body.get("website"));
                            intent.putExtra("process_code", process_code);
                            startActivity(intent);
                        } else if (process_code.equals("10002")) {//获取短信验证码
                            //移动，电信需要进入短信验证
                            Intent intent = new Intent(CertificationInfoActivity.this, SMSVerificationActivity.class);
                            intent.putExtra("mobilePhone", mobilTV.getText().toString());
                            intent.putExtra("servicePsw", pswTV.getText().toString());
                            intent.putExtra("token", "" + body.get("token"));
                            intent.putExtra("website", "" + body.get("website"));
                            intent.putExtra("process_code", process_code);
                            startActivity(intent);
                        }else if(process_code.equals("10022")) {
                            //北京移动跳到查询密码
                            Intent intent = new Intent(CertificationInfoActivity.this, QueryPasswordActivity.class);
                            intent.putExtra("mobilePhone", mobilTV.getText().toString());
                            intent.putExtra("servicePsw", pswTV.getText().toString());
                            intent.putExtra("token", "" + body.get("token"));
                            intent.putExtra("website", "" + body.get("website"));
                            intent.putExtra("process_code", process_code);
                            startActivity(intent);
                        }else if(process_code.equals("30000")||process_code.equals("0")||process_code.equals("10009")) {
                            CertificationInfoActivity.this.finish();
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
