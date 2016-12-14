package com.huaxia.finance.consumer.activity;

import android.widget.TextView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * Created by wangjie01 on 2016/12/8.
 */
public class RepayDetailActivity extends BaseActivity {

    @ViewInject(R.id.tv_detail_price)
    TextView tv_detail_price;
    @ViewInject(R.id.tv_detail_result)
    TextView tv_detail_result;
    @ViewInject(R.id.tv_project_info)
    TextView tv_project_info;
    @ViewInject(R.id.tv_create_time)
    TextView tv_create_time;
    @ViewInject(R.id.tv_repay_bank)
    TextView tv_repay_bank;
    @ViewInject(R.id.order_no)
    TextView order_no;
    @ViewInject(R.id.serial_no)
    TextView serial_no;
    @ViewInject(R.id.tv_repay_time)
    TextView tv_repay_time;

    @Override
    protected int getLayout() {
        return R.layout.activity_repay_online_detail;
    }

    @Override
    protected String getTitleText() {
        return "还款详情";
    }

    @Override
    protected void setup() {
        super.setup();
        //根据数据完成在线还款详情页面

    }
}
