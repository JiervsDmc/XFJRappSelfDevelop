package com.huaxia.finance.consumer.activity.order;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.activity.samefunction.BankCardAddActivity;
import com.huaxia.finance.consumer.adapter.BankCardSelectAdapter;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.bean.PayInfoBean;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.ConvertUtils;
import com.huaxia.finance.consumer.util.IsNullUtils;
import com.huaxia.finance.consumer.util.LogUtil;
import com.huaxia.finance.consumer.util.ToastUtils;
import com.huaxia.finance.consumer.util.Utils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class PayActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.tv_pay_num)
    TextView tv_pay_num;
    @ViewInject(R.id.tv_goods)
    TextView tv_goods;

    @ViewInject(R.id.tv_time_left)
    TextView tv_time_left;
    @ViewInject(R.id.lv_bankcard)
    ListView lv_bankcard;
    @ViewInject(R.id.btn_pay)
    Button btn_pay;
    @ViewInject(R.id.rl_add_card_item)
    RelativeLayout rl_add_card_item;
    private View footView;
    private BankCardSelectAdapter banckCardSelectAdater;
    private CountDownTimer countTimerOder;
    private String orderNo;
    private String productName;
    private String repayMoney;
    private String repayPeriod;
    private String totalPeriod;
    private List bankList = new ArrayList();
    private Map payRecord;
    private Dialog timeUpDialog;

    private int secondCount;
    private String platformRisk;
    private String platformFee;
    private String penaltyFee;
    private String consuleFee;
    private String platformFeeCheck;
    private String platformFeeService;
    private String orderStatus;
    private String payfineamt;
    private String payinteamt;
    private String paycorpusamt;
    private String status;
    private String bankId;
    private String seqid;

    @Override
    protected int getLayout() {
        return R.layout.activity_pay;
    }

    @Override
    protected String getTitleText() {
        return "支付确认";
    }

    @Override
    protected void setup() {
        super.setup();
        footView = LayoutInflater.from(this).inflate(R.layout.item_footer_card, null);
        Intent intent = getIntent();
        PayInfoBean info = (PayInfoBean) intent.getSerializableExtra("payinfo");
        orderNo = info.getOrderNo();
        productName = info.getProductName();
        repayMoney = info.getRepayMoney();
        repayPeriod = info.getRepayPeriod();
        totalPeriod = info.getTotalPeriod();
        secondCount = info.getSecond();
        platformRisk = info.getPlatformRisk();
        platformFee = info.getPlatformFee();
        penaltyFee = info.getPenaltyFee();
        consuleFee = info.getConsuleFee();
        platformFeeCheck = info.getPlatformFeeCheck();
        platformFeeService = info.getPlatformFeeService();
        orderStatus = info.getContractStatus();
        payfineamt = info.getPayfineamt();
        payinteamt = info.getPayinteamt();
        paycorpusamt = info.getPaycorpusamt();
        status = info.getStatus();
        seqid = info.getSeqid();
        bankList = (List) intent.getSerializableExtra("bankList");
        payRecord = (Map) intent.getSerializableExtra("payRecord");
        startOderCounter(secondCount);
        updateBankCardList();
        updatePayRecord();
        LogUtil.getLogutil().d("totalPeriod = " + totalPeriod);
    }

    private void updatePayRecord() {
        repayMoney = ConvertUtils.mapToString(payRecord,"repayMoney");
        productName = ConvertUtils.mapToString(payRecord,"productName");
        repayPeriod = ConvertUtils.mapToString(payRecord,"repayPeriod");
        totalPeriod = ConvertUtils.mapToString(payRecord,"totalPeriod");
        orderNo = ConvertUtils.mapToString(payRecord,"orderNo");
        LogUtil.getLogutil().d("totalPeriod == " + totalPeriod);
        tv_pay_num.setText(repayMoney);
        if(ConvertUtils.equals(seqid,"0")){
            tv_goods.setText("服务费 - " + productName);
        }else{
            tv_goods.setText(seqid + "/" + totalPeriod + " - " + productName);
        }
    }

    private void updateBankCardList() {
        if (bankList.size() == 0) {
            rl_add_card_item.setVisibility(View.VISIBLE);
            lv_bankcard.setVisibility(View.GONE);
            btn_pay.setBackgroundColor(getResources().getColor(R.color.light_dark));
            btn_pay.setClickable(false);
        } else {
            rl_add_card_item.setVisibility(View.GONE);
            lv_bankcard.setVisibility(View.VISIBLE);
            btn_pay.setBackgroundColor(getResources().getColor(R.color.app_blue_color));
            btn_pay.setClickable(true);
            if (lv_bankcard.getFooterViewsCount() > 0) {
                lv_bankcard.removeFooterView(footView);
            }
            lv_bankcard.addFooterView(footView);
            bankId = ConvertUtils.mapToString((Map)bankList.get(0),"id");
            banckCardSelectAdater = new BankCardSelectAdapter(PayActivity.this, bankList);
            lv_bankcard.setAdapter(banckCardSelectAdater);
        }
    }

    @OnClick(R.id.btn_pay)
    public void Pay(View view) {
        if (Utils.isFastDoubleClick()) {
            return;
        }
        commitPayInfo();
    }

    private void commitPayInfo() {
        Map<String, Object> map = new HashMap<>();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        map.put("status", status);
        map.put("orderNo", orderNo);
        map.put("repayPeriod", repayPeriod);
        map.put("repaymentAmount", paycorpusamt);
        map.put("repaymentInterest", payinteamt);
        map.put("platformPenalty", payfineamt);
        map.put("platformRisk", platformRisk);
        map.put("platformFee", platformFee);
        map.put("penaltyFee", penaltyFee);
        map.put("consuleFee", consuleFee);
        map.put("platformFeeCheck", platformFeeCheck);
        map.put("platformFeeService", platformFeeService);
        map.put("contractStatus", orderStatus);
        map.put("payAmount", repayMoney);
        map.put("bankId", bankId);
        ApiCaller.call(this, Constant.URL, "0051", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                LogUtil.getLogutil().d("支付结果" + response);
                if (head.getResponseCode().contains("0000")) {
                    Intent intent = new Intent(PayActivity.this, PayResultActivity.class);
                    String tradeStatus = head.getTradeStatus();
                    intent.putExtra("tradeStatus", tradeStatus);
                    if(ConvertUtils.equals(tradeStatus,"1")||ConvertUtils.equals(tradeStatus,"6")||ConvertUtils.equals(tradeStatus,"2")){
                        startActivity(intent);
                        finish();
                    }else {
                        ToastUtils.showSafeToast(PayActivity.this, head.getResponseMsg());
                    }
                } else {
                    ToastUtils.showSafeToast(PayActivity.this, head.getResponseMsg());
                }
            }
        });
    }


    private int minute = 0;
    private int second = 0;

    /**
     * 倒计时
     *
     * @param sec 倒计时时间（单位：s）
     */
    private void startOderCounter(final int sec) {
        cancelCountTimer(countTimerOder);
        countTimerOder = new CountDownTimer((sec + 1) * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int left = (int) ((millisUntilFinished - 1000) / 1000);
                minute = left / 60;
                second = left % 60;
                if (second < 10) {
                    tv_time_left.setText("剩余 " + minute + " ：0" + second);
                } else {
                    tv_time_left.setText("剩余 " + minute + " ：" + second);
                }
            }

            @Override
            public void onFinish() {
                tv_time_left.setText("订单超时");
                btn_pay.setBackgroundColor(getResources().getColor(R.color.light_dark));
                btn_pay.setClickable(false);
                timeUpDialog = new AlertDialog.Builder(PayActivity.this).setTitle("提示")
                        .setMessage("订单超时，请返回重新操作")
                        .setCancelable(false)
                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                timeUpDialog.dismiss();
                                PayActivity.this.finish();
                            }
                        }).show();
            }
        };
        countTimerOder.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelCountTimer(countTimerOder);
    }

    private void cancelCountTimer(CountDownTimer countTimer) {
        if (countTimer != null) {
            countTimer.cancel();
        }
    }
}
