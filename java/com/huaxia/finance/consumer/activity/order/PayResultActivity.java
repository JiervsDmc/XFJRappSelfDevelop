package com.huaxia.finance.consumer.activity.order;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

public class PayResultActivity extends BaseActivity {
    @ViewInject(R.id.tv_result)
    TextView tv_result;
    @ViewInject(R.id.tv_show_detail)
    TextView tv_show_detail;
    @ViewInject(R.id.iv_pay_result)
    ImageView iv_pay_result;
    private String tradeStatus;
    Handler mHandler = new Handler();
    @Override
    protected int getLayout() {
        return R.layout.activity_pay_result;
    }

    @Override
    protected String getTitleText() {
        return "提交成功";
    }

    @Override
    protected void setup() {
        super.setup();
        tradeStatus = getIntent().getStringExtra("tradeStatus");
        switch (tradeStatus){
            case "6":
                tv_result.setText("支付结果银行处理中！");
                tv_show_detail.setText("查看我的还款计划表");
                iv_pay_result.setImageResource(R.drawable.pay_result_process);
                break;
            case "1":
                tv_result.setText("恭喜您，在线还款成功！");
                tv_show_detail.setText("查看我的还款计划表");
                iv_pay_result.setImageResource(R.drawable.pay_result_succ);
                break;
            case "2":
                tv_result.setText("很抱歉，支付失败！");
                tv_show_detail.setText("请点此重新支付");
                iv_pay_result.setImageResource(R.drawable.pay_result_faild);
                break;
        }
        endThisActivity(OrderDetailActivity.class);
    }

    private void endThisActivity(final Class clazz){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(PayResultActivity.this,clazz);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        },5000);
    }
    @OnClick(R.id.tv_show_detail)
    public void tv_show_detail(View view) {
        Intent intent = new Intent(PayResultActivity.this, OrderDetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
