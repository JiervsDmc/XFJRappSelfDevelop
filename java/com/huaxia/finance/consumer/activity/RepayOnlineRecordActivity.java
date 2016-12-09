package com.huaxia.finance.consumer.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.adapter.RepayRecordAdapter;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

/**
 * Created by wangjie01 on 2016/12/8.
 */
public class RepayOnlineRecordActivity extends BaseActivity {

    @ViewInject(R.id.lv_repayOnline_record)
    ListView lvRecord;

    @Override
    protected int getLayout() {
        return R.layout.activity_repay_online_record;
    }

    @Override
    protected String getTitleText() {
        return "还款列表";
    }

    @Override
    protected void setup() {
        super.setup();
        RepayRecordAdapter adapter = new RepayRecordAdapter(this,new ArrayList());
        lvRecord.setAdapter(adapter);
        lvRecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(RepayOnlineRecordActivity.this,RepayDetailActivity.class);
                startActivity(intent);
            }
        });
    }

    //获取在线还款记录
    protected void getOnlineRepayRecord() {

    }
}
