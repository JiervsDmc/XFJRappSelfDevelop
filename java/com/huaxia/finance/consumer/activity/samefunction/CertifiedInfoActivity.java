package com.huaxia.finance.consumer.activity.samefunction;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.activity.ModifyPhoneActivity;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.lidroid.xutils.view.annotation.ViewInject;

public class CertifiedInfoActivity extends BaseActivity implements View.OnClickListener{

    @ViewInject(R.id.mobil_rz_number)
    private TextView mobile;
    @ViewInject(R.id.internet_btn)
    private TextView update;

    private String number;

    @Override
    protected int getLayout() {
        return R.layout.activity_certified_info;
    }

    @Override
    protected String getTitleText() {
        return "认证信息";
    }

    @Override
    protected void setup() {
        super.setup();
        number = getIntent().getStringExtra("Mobil");
        mobile.setText(number);
        update.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.internet_btn:
                Intent intent = new Intent(CertifiedInfoActivity.this, ModifyPhoneActivity.class);
                startActivity(intent);
                break;
        }
    }
}
