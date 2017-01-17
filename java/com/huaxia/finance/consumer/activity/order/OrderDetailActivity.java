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
import com.huaxia.finance.consumer.bean.PayInfoBean;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.ConvertUtils;
import com.huaxia.finance.consumer.util.LogUtil;
import com.huaxia.finance.consumer.util.MD5Util;
import com.huaxia.finance.consumer.util.ToastUtils;
import com.huaxia.finance.consumer.util.json.JsonUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class OrderDetailActivity extends BaseActivity implements View.OnClickListener, OrderDetailAdapter.OnItemPay {
    @ViewInject(R.id.lv_order_detail)
    private ListView lvOrderDetail;
    @ViewInject(R.id.tv_order_number)
    private TextView tvOrderNumber;
    @ViewInject(R.id.tv_repayment_category)
    private TextView tvRepaymentCategory;
    private Intent intent;
    private String orderNo;
    private String approvalAmount;
    private String stagesMoney;
    private String productName;
    private int totalPeriod;
    private int payPosition;
    private List orderDetailList;
    private String order_status;
    private String paycorpusamt;
    private String payfineamt;
    private String payinteamt;
    private String status;

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
        orderNo = getIntent().getStringExtra("orderNo");
        approvalAmount = getIntent().getStringExtra("approvalAmount");
        stagesMoney = getIntent().getStringExtra("stagesMoney");
        LogUtil.getLogutil().d("接收到的orderNo" + orderNo);
//        getOrderDetail();
        //如果是经过降额处理的，页底有3个协议
        if (!approvalAmount.equals(stagesMoney) && approvalAmount != null && stagesMoney != null) {
            View mFootView = getLayoutInflater().inflate(R.layout.order_add_detail_footer, null);
            TextView tvProtocol = (TextView) mFootView.findViewById(R.id.tv_protocol);
            TextView tvProtocol1 = (TextView) mFootView.findViewById(R.id.tv_protocol1);
            TextView tvProtocol3 = (TextView) mFootView.findViewById(R.id.tv_protocol3);
            lvOrderDetail.addFooterView(mFootView);
            tvProtocol.setOnClickListener(this);
            tvProtocol1.setOnClickListener(this);
            tvProtocol3.setOnClickListener(this);
        } else {
            View mFootView = getLayoutInflater().inflate(R.layout.order_detail_footer, null);
            TextView tvProtocol = (TextView) mFootView.findViewById(R.id.tv_protocol);
            TextView tvProtocol1 = (TextView) mFootView.findViewById(R.id.tv_protocol1);
            lvOrderDetail.addFooterView(mFootView);
            tvProtocol.setOnClickListener(this);
            tvProtocol1.setOnClickListener(this);
        }
    }

    //获取用户详情
    private void getOrderDetail() {
        Map<String, Object> mapDetail = new HashMap<>();
        mapDetail.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        mapDetail.put("orderNo", orderNo);
        LogUtil.getLogutil().d("orderNo的值" + orderNo);
        ApiCaller.call(this, Constant.URL, "0009", "appService", mapDetail, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);

            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                LogUtil.getLogutil().d("还款计划结果是" + response);
                if (head.getResponseCode().contains("0000")) {
                    productName = body.get("productName").toString();
                    order_status = ConvertUtils.mapToString(body,"ORDER_STATUS");
                    if (!body.get("schedule").toString().isEmpty()) {
                        tvOrderNumber.setText(body.get("orderNo").toString());
                        if (body.get("orderStatus").toString().contains("ST02")) {
                            tvRepaymentCategory.setText("正在还款");
                            tvRepaymentCategory.setBackgroundResource(R.drawable.button_yuanjiao);
                        } else if (body.get("orderStatus").toString().contains("ST06")) {
                            tvRepaymentCategory.setText("逾期");
                            tvRepaymentCategory.setBackgroundResource(R.drawable.corners_red_bg);
                        } else if (body.get("orderStatus").toString().contains("ST07")) {
                            tvRepaymentCategory.setText("已结清");
                            tvRepaymentCategory.setBackgroundResource(R.drawable.corners_green_bg);
                        }
                        orderDetailList = new ArrayList();
                        orderDetailList = (List) body.get("schedule");
                        totalPeriod = orderDetailList.size() - 1;
                        boolean flag = false;
                        payPosition = -1;
                        for (int i = 0; i < orderDetailList.size(); i++) {
                            if (!flag) {
                                Map map = (Map) orderDetailList.get(i);
                                if (map.get("mayRepay").toString().equals("0")) {
                                    payPosition = i;
                                    flag = true;
                                }
                            }
                        }
                        LogUtil.getLogutil().d("payPosition = " + payPosition);
                        OrderDetailAdapter detailAdaptor = new OrderDetailAdapter(OrderDetailActivity.this, orderDetailList, OrderDetailActivity.this);
                        lvOrderDetail.setAdapter(detailAdaptor);
                    }
                } else {
                    ToastUtils.showSafeToast(OrderDetailActivity.this, head.getResponseMsg());
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_protocol:
                intent = new Intent(this, LoanProtocolActivity.class);
                intent.putExtra("type", "1");
                intent.putExtra("orderNo", orderNo);
                startActivity(intent);
                break;

            case R.id.tv_protocol1:
                intent = new Intent(this, ConsultationProtocolActivity.class);
                intent.putExtra("type", "1");
                intent.putExtra("orderNo", orderNo);
                startActivity(intent);
                break;

            case R.id.tv_protocol3:
                intent = new Intent(this, AddLoanProtocolActivity.class);
                intent.putExtra("type", "1");
                intent.putExtra("orderNo", orderNo);
                startActivity(intent);
                break;
        }
    }
    private String repayMoney;
    private String repayPeriod;
    private String seqid;
    @Override
    public void payOnline(int position) {
        boolean isOnePay;
        Map map = (Map)orderDetailList.get(position);
        seqid = ConvertUtils.mapToString(map,"seqid");
        paycorpusamt = ConvertUtils.mapToString(map,"paycorpusamt");
        payfineamt = ConvertUtils.mapToString(map,"payfineamt");
        payinteamt = ConvertUtils.mapToString(map,"payinteamt");
        status = ConvertUtils.mapToString(map,"status");
        if(seqid.equals("0")){
            repayMoney = payinteamt;
            isOnePay = true;
        }else{
            repayMoney = ConvertUtils.mapToString(map,"payamt");
            isOnePay = false;
        }
        repayPeriod = isOnePay?"0":seqid;
        if(ConvertUtils.equals(ConvertUtils.mapToString(map,"mayRepay"),"2")){
            ToastUtils.showSafeToast(this,"该笔分期银行系统正在处理中，请稍后再试！");
            return;
        }
        if(position!=payPosition){
            ToastUtils.showSafeToast(this,"请按分期期数顺序还款！");
        }else {
            checkIfCanPay();
        }

    }

    private void checkIfCanPay() {
            Map<String, Object> mapDetail = new HashMap<>();
            mapDetail.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
            mapDetail.put("orderNo", orderNo);
            mapDetail.put("productName", productName);
            mapDetail.put("repayMoney", repayMoney);
            mapDetail.put("repayPeriod", repayPeriod);
            mapDetail.put("totalyPeriod", totalPeriod);
            ApiCaller.call(this, Constant.URL, "0047", "appService", mapDetail, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
                @Override
                public void onError(Call call, Exception e, int id) {
                    super.onError(call, e, id);
                }

                @Override
                public void onResponse(String response, int id) {
                    super.onResponse(response, id);
                    LogUtil.getLogutil().d("支付确认" + response);
                    if (head.getResponseCode().contains("0000")) {
                        int second = ConvertUtils.mapToInt(body,"second");
                        List bankList = new ArrayList();
                        Map mapBank = ConvertUtils.mapToMap(body,"bank");
                        if(mapBank!=null){
                            bankList.add(mapBank);
                        }
                        Map payRecord = ConvertUtils.mapToMap(body,"repayRecord");

                        Intent intent = new Intent(OrderDetailActivity.this, PayActivity.class);
                        PayInfoBean info = new PayInfoBean();
                        info.setOrderNo(orderNo);
                        info.setStatus(status);
                        info.setProductName(productName);
                        info.setRepayMoney(repayMoney);
                        info.setRepayPeriod(repayPeriod);
                        info.setTotalPeriod(ConvertUtils.toString(totalPeriod));
                        info.setSecond(second);
                        info.setPaycorpusamt(paycorpusamt);
                        info.setPayfineamt(payfineamt);
                        info.setPlatformRisk("0");
                        info.setPlatformFee("0");
                        info.setPenaltyFee("0");
                        info.setConsuleFee("0");
                        info.setPlatformFeeCheck("0");
                        info.setPlatformFeeService("0");
                        info.setContractStatus(order_status);
                        info.setPayinteamt(payinteamt);
                        info.setSeqid(seqid);
                        intent.putExtra("payinfo",info);
                        intent.putExtra("bankList", (Serializable) bankList);
                        intent.putExtra("payRecord", (Serializable) payRecord);
                        startActivity(intent);
                    } else {
                        ToastUtils.showSafeToast(OrderDetailActivity.this, head.getResponseMsg());
                    }
                }
            });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getOrderDetail();
    }
}
