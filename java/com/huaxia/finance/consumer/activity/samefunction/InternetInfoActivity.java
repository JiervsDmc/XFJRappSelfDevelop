package com.huaxia.finance.consumer.activity.samefunction;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

public class InternetInfoActivity extends BaseActivity implements View.OnClickListener{

    @ViewInject(R.id.QQ_number)
    private EditText QQNumber;
    @ViewInject(R.id.WeChat_number)
    private EditText WeChatNumber;
    @ViewInject(R.id.internet_btn)
    private Button nextBtn;

    private Map mapDate;

    @Override
    protected int getLayout() {
        return R.layout.activity_internet_info;
    }

    @Override
    protected String getTitleText() {
        return "互联网信息";
    }

    @Override
    protected void setup() {
        super.setup();
        mapDate = new HashMap();
        getDate();
        nextBtn.setOnClickListener(this);
    }
    public void getDate(){
        Map map = new HashMap();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        ApiCaller.call(this, Constant.URL, "0020", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
               // toast("数据查看失败");
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                if (body.size()==0){
                    return;
                }
                if (body.get("userDetail").equals("") || body.get("userDetail") == null) {
                    return;
                }
                mapDate = (Map) body.get("userDetail");
                setDate(mapDate);
            }
        });

    }
    public void setDate(Map date){
        if(!date.get("qqAccount").equals("")&&date.get("qqAccount")!=null){
            QQNumber.setText(""+date.get("qqAccount"));
        }
        if(!date.get("weChatAccount").equals("")&&date.get("weChatAccount")!=null){
            WeChatNumber.setText(""+date.get("weChatAccount"));
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.internet_btn:
                dataUpload();
                break;
        }
    }
    public void dataUpload() {
        if(IsNullUtils.isNull(QQNumber.getText().toString())){
            toast("请输入你的QQ号");
            return;
        }
        if(IsNullUtils.isNull(WeChatNumber.getText().toString())){
            toast("请输入你的微信号");
            return;
        }
        Map map = new HashMap();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        map.put("qqAccount",QQNumber.getText().toString());
        map.put("weChatAccount",WeChatNumber.getText().toString());
        ApiCaller.call(this, Constant.URL, "0007", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                //toast("数据提交失败");
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                if (head.getResponseCode().equals("0000")) {
                    finish();
                }else{
                    toast(head.getResponseMsg());
                }
            }
        });
    }
}
