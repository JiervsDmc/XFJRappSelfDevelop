package com.huaxia.finance.consumer.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.adapter.RepayRecordAdapter;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.LogUtil;
import com.huaxia.finance.consumer.util.ToastUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by wangjie01 on 2016/12/8.
 */
public class RepayOnlineRecordActivity extends BaseActivity {

    @ViewInject(R.id.lv_repayOnline_record)
    ListView lvRecord;
    @ViewInject(R.id.ll_noRecord)
    LinearLayout ll_noRecord;
    @ViewInject(R.id.no_load)
    LinearLayout no_load;
    @ViewInject(R.id.to_reload)
    TextView to_reload;
    private String userUuid;
    private RepayRecordAdapter adapter;

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
    }

    //获取在线还款记录
    protected void getOnlineRepayRecord() {
        Map<String, Object> map = new HashMap<>();
        map.put("userUuid", userUuid);
         ApiCaller.call(this, Constant.URL, "0052", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ll_noRecord.setVisibility(View.GONE);
                no_load.setVisibility(View.VISIBLE);
                to_reload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getOnlineRepayRecord();
                    }
                });
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                if(head.getResponseCode().contains("0000")) {
                    LogUtil.getLogutil().d(body.toString());
                    no_load.setVisibility(View.GONE);
                    //得到在线还款记录的列表
                    final ArrayList recordList = (ArrayList) body.get("recordList");
                    if (recordList.size()==0) {
                        ll_noRecord.setVisibility(View.VISIBLE);
                    }else {
                        ll_noRecord.setVisibility(View.GONE);
                        adapter = new RepayRecordAdapter(RepayOnlineRecordActivity.this,recordList);
                        lvRecord.setAdapter(adapter);
                        lvRecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                HashMap itemMap = (HashMap) recordList.get(position);
                                String flowNo = (String) itemMap.get("flowNo");
                                Intent intent = new Intent(RepayOnlineRecordActivity.this,RepayDetailActivity.class);
                                intent.putExtra("flowNo",flowNo);
                                startActivity(intent);
                            }
                        });
                    }
                }else {
                    ToastUtils.showSafeToast(RepayOnlineRecordActivity.this,head.getResponseMsg());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        userUuid = mgr.getVal(UniqueKey.APP_USER_ID);
        getOnlineRepayRecord();
    }
}
