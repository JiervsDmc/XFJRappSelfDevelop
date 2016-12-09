package com.huaxia.finance.consumer.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.activity.LoginActivity;
import com.huaxia.finance.consumer.activity.MainActivity;
import com.huaxia.finance.consumer.activity.samefunction.UserInfoActivity;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.ActivityManagerUtil;

public class OrderSubmitPassActivity extends BaseActivity implements View.OnClickListener{

    @Override
    protected int getLayout() {
        return R.layout.activity_order_submit_pass;
    }

    @Override
    protected String getTitleText() {
        return "提交成功";
    }

    @Override
    protected void setup() {
        super.setup();
        hideBtnLeft();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(OrderSubmitPassActivity.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                intent.putExtra("type","1");
                startActivity(intent);
                finish();
            }
        },3000);
    }

  /*  @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.back:
//                ActivityManagerUtil.getAppManager().finishAllActivity();
                Intent intent1 = new Intent(OrderSubmitPassActivity.this,MainActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent1);
                break;
        }
    }*/
}
