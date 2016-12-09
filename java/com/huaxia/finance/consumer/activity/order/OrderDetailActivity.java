package com.huaxia.finance.consumer.activity.order;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.activity.protocol.AddLoanProtocolActivity;
import com.huaxia.finance.consumer.activity.protocol.ConsultationProtocolActivity;
import com.huaxia.finance.consumer.activity.protocol.LoanProtocolActivity;
import com.huaxia.finance.consumer.adapter.OrderDetailAdapter;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.bean.MessageHeader;
import com.huaxia.finance.consumer.bean.MessageObject;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.LogUtil;
import com.huaxia.finance.consumer.util.MD5Util;
import com.huaxia.finance.consumer.util.ToastUtils;
import com.huaxia.finance.consumer.util.json.JsonUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class OrderDetailActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.lv_order_detail)
    private ListView lvOrderDetail;
    @ViewInject(R.id.tv_order_number)
    private TextView tvOrderNumber;
    @ViewInject(R.id.tv_repayment_category)
    private TextView tvRepaymentCategory;
    private  Intent intent;
    private String orderNo;
    private String approvalAmount;
    private String stagesMoney;

    @Override
    protected int getLayout() {
        return R.layout.activity_order_detail;
    }

    @Override
    protected String getTitleText() {
        return "还款详情";
    }

    @Override
    protected void setup() {
        super.setup();
        orderNo=getIntent().getStringExtra("orderNo");
        approvalAmount = getIntent().getStringExtra("approvalAmount");
        stagesMoney = getIntent().getStringExtra("stagesMoney");
        LogUtil.getLogutil().d("接收到的orderNo"+orderNo);
        getOrderDetail();
        //如果是经过降额处理的，页底有3个协议
        if (!approvalAmount.equals(stagesMoney)&& approvalAmount!= null && stagesMoney!=null) {
            View mFootView = getLayoutInflater().inflate(R.layout.order_add_detail_footer, null);
            TextView tvProtocol=(TextView) mFootView.findViewById(R.id.tv_protocol);
            TextView tvProtocol1=(TextView) mFootView.findViewById(R.id.tv_protocol1);
            TextView tvProtocol3=(TextView) mFootView.findViewById(R.id.tv_protocol3);
            lvOrderDetail.addFooterView(mFootView);
            tvProtocol.setOnClickListener(this);
            tvProtocol1.setOnClickListener(this);
            tvProtocol3.setOnClickListener(this);
        }else {
            View mFootView = getLayoutInflater().inflate(R.layout.order_detail_footer, null);
            TextView tvProtocol=(TextView) mFootView.findViewById(R.id.tv_protocol);
            TextView tvProtocol1=(TextView) mFootView.findViewById(R.id.tv_protocol1);
            lvOrderDetail.addFooterView(mFootView);
            tvProtocol.setOnClickListener(this);
            tvProtocol1.setOnClickListener(this);
        }
    }
    //获取用户详情
    private void getOrderDetail() {
        Map<String, Object> mapDetail = new HashMap<>();
        mapDetail.put("userUuid",mgr.getVal(UniqueKey.APP_USER_ID));
        mapDetail.put("orderNo",orderNo);
        LogUtil.getLogutil().d("orderNo的值"+orderNo);
        ApiCaller.call(this, Constant.URL, "0009", "appService", mapDetail, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);

            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                LogUtil.getLogutil().d("还款计划结果是"+response);
                if(head.getResponseCode().contains("0000")) {
                    if(!body.get("schedule").toString().isEmpty()) {
                    tvOrderNumber.setText(body.get("orderNo").toString());
                    if(body.get("orderStatus").toString().contains("ST02")) {
                        tvRepaymentCategory.setText("还款中");
                        tvRepaymentCategory.setBackgroundResource(R.drawable.button_yuanjiao);
                    }else if(body.get("orderStatus").toString().contains("ST06")) {
                        tvRepaymentCategory.setText("逾期");
                        tvRepaymentCategory.setBackgroundResource(R.drawable.corners_red_bg);
                    }else if(body.get("orderStatus").toString().contains("ST07")) {
                        tvRepaymentCategory.setText("已结清");
                        tvRepaymentCategory.setBackgroundResource(R.drawable.corners_green_bg);
                    }
                    List orderDetailList = new ArrayList();
                    orderDetailList=(List)body.get("schedule");
                    for(int i=0;i<orderDetailList.size();i++) {
                        OrderDetailAdapter detailAdaptor=new OrderDetailAdapter(OrderDetailActivity.this,orderDetailList);
                        lvOrderDetail.setAdapter(detailAdaptor);
                    }
                    }
                }else {
                    ToastUtils.showSafeToast(OrderDetailActivity.this,head.getResponseMsg());
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.tv_protocol:
                intent=new Intent(this, LoanProtocolActivity.class);
                intent.putExtra("type","1");
                intent.putExtra("orderNo",orderNo);
                startActivity(intent);
                break;

            case R.id.tv_protocol1:
                intent=new Intent(this, ConsultationProtocolActivity.class);
                intent.putExtra("type","1");
                intent.putExtra("orderNo",orderNo);
                startActivity(intent);
                break;

            case R.id.tv_protocol3:
                intent=new Intent(this, AddLoanProtocolActivity.class);
                intent.putExtra("type","1");
                intent.putExtra("orderNo",orderNo);
                startActivity(intent);
                break;
        }
    }
}
