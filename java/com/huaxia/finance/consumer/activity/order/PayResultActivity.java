package com.huaxia.finance.consumer.activity.order;

import android.content.Intent;
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
    private String payResult;

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
        payResult = "2";
        switch (payResult){
            case "0":
                tv_result.setText("支付结果银行处理中！");
                tv_show_detail.setText("查看我的还款计划表");
                iv_pay_result.setImageResource(R.drawable.ordersuccess);
                break;
            case "1":
                tv_result.setText("恭喜您，在线还款成功！");
                tv_show_detail.setText("查看我的还款计划表");
                iv_pay_result.setImageResource(R.drawable.ordersuccess);
                break;
            case "2":
                tv_result.setText("很抱歉，支付失败！");
                tv_show_detail.setText("请点此重新支付");
                iv_pay_result.setImageResource(R.drawable.ordersuccess);
                break;
        }
    }

    @OnClick(R.id.tv_show_detail)
    public void tv_show_detail(View view) {
        Intent intent = null;
        switch (payResult){
            case "0":
            case "1":
                intent = new Intent(PayResultActivity.this, OrderDetailActivity.class);
                break;
            case "2":
                intent = new Intent(PayResultActivity.this, PayActivity.class);
                break;
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }
}
