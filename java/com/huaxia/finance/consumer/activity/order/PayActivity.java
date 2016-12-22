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
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
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
    private CountDownTimer countTimerSMS;
    private String orderNo;
    private String productName;
    private String repayMoney;
    private String repayPeriod;
    private String totalPeriod;
    private int selecIndex;
    private List bankList = new ArrayList();
    private String cardNum;
    private String bankCode;
    private String bankCardId;
//    private String bankName;
    private Map payRecord;
    private String realName;
    private String businessNo;
    private Dialog timeUpDialog;
    private String cellphone;

    private int secondCount;
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
        footView = LayoutInflater.from(this).inflate(R.layout.item_footer_add_card, null);
        Intent intent = getIntent();
        orderNo = intent.getStringExtra("orderNo");
        productName = intent.getStringExtra("productName");
        repayMoney = intent.getStringExtra("repayMoney");
        repayPeriod = intent.getStringExtra("repayPeriod");
        totalPeriod = intent.getStringExtra("totalPeriod");
//        getPayInfo();

        secondCount = intent.getIntExtra("second",0);
        realName = intent.getStringExtra("realName");
        bankList = (List) intent.getSerializableExtra("bankList");
        payRecord = (Map) intent.getSerializableExtra("payRecord");
        startOderCounter(secondCount);
        updateBankCardList();
        updatePayRecord();
        LogUtil.getLogutil().d("totalPeriod = " + totalPeriod);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String refresh = intent.getStringExtra("refresh");
        if (refresh != null && refresh.equals("true")) {
            getPayInfo();
        }
    }

    private void getPayInfo() {
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
                    int second = 0;
                    try {
                        second = Integer.valueOf(body.get("second").toString());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    startOderCounter(second);
                    realName = body.get("name").toString();
                    bankList = (List) body.get("bankList");
                    payRecord = (Map) body.get("repayRecord");
                    updateBankCardList();
                    updatePayRecord();
                } else {
                    ToastUtils.showSafeToast(PayActivity.this, head.getResponseMsg());
                }
            }
        });
    }

    private void updatePayRecord() {
        repayMoney = payRecord.get("repayMoney").toString();
        productName = payRecord.get("productName").toString();
        repayPeriod = payRecord.get("repayPeriod").toString();
        totalPeriod = payRecord.get("totalPeriod").toString();
        LogUtil.getLogutil().d("totalPeriod == " + totalPeriod);
        orderNo = payRecord.get("orderNo").toString();
        tv_pay_num.setText(repayMoney);
        tv_goods.setText(productName);
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
            banckCardSelectAdater = new BankCardSelectAdapter(PayActivity.this, bankList, selecIndex);

            footView.findViewById(R.id.rl_add_card).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(IsNullUtils.isNull(realName)){
                        toast("网络繁忙");
                        return;
                    }
                    Intent intent = new Intent(PayActivity.this, BankCardAddActivity.class);
                    intent.putExtra("realName", realName);
                    startActivity(intent);
                }
            });
            cardNum = ((Map) bankList.get(0)).get("cardNo").toString();
            bankCode = ((Map) bankList.get(0)).get("bankCode").toString();
            bankCardId = ((Map) bankList.get(0)).get("id").toString();
//            bankName = ((Map) bankList.get(0)).get("bankName").toString();
            cellphone = ((Map) bankList.get(0)).get("cellphone").toString();
            lv_bankcard.setAdapter(banckCardSelectAdater);
            lv_bankcard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    banckCardSelectAdater.setSelectIndex(position);
                    banckCardSelectAdater.notifyDataSetChanged();
                    cardNum = ((Map) bankList.get(position)).get("cardNo").toString();
                    bankCode = ((Map) bankList.get(position)).get("bankCode").toString();
                    bankCardId = ((Map) bankList.get(position)).get("id").toString();
//                    bankName = ((Map) bankList.get(position)).get("bankName").toString();
                    cellphone = ((Map) bankList.get(position)).get("cellphone").toString();
                }
            });
        }
    }

    @OnClick(R.id.rl_add_card_item)
    public void rl_add_card_item(View view) {
        if(IsNullUtils.isNull(realName)){
            toast("网络繁忙");
            return;
        }
        Intent intent = new Intent(PayActivity.this, BankCardAddActivity.class);
        intent.putExtra("realName", realName);
        startActivity(intent);
    }

    @OnClick(R.id.btn_pay)
    public void Pay(View view) {
        if (Utils.isFastDoubleClick()) {
            return;
        }
        checkPayInfo();
    }

    private void checkPayInfo() {
        Map<String, Object> map = new HashMap<>();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        map.put("cardNo", cardNum);
        map.put("cellphone", cellphone);
        map.put("realName", realName);
        map.put("bankCode", bankCode);
        map.put("repayMoney", repayMoney);
        map.put("orderNo", orderNo);
        map.put("repayPeriod", repayPeriod);
        map.put("bankId", bankCardId);
        ApiCaller.call(this, Constant.URL, "0050", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                LogUtil.getLogutil().d("支付获取验证码" + response);
                if (head.getResponseCode().contains("0000")) {
                    businessNo = body.get("businessNo").toString();
                    showSMSDialog();
                } else {
                    ToastUtils.showSafeToast(PayActivity.this, head.getResponseMsg());
                }
            }
        });
    }

    private void commitPayInfo(String code) {
        Map<String, Object> map = new HashMap<>();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        map.put("cardNo", cardNum);
        map.put("cellphone", cellphone);
        map.put("realName", realName);
        map.put("bankCode", bankCode);
        map.put("smsCode", code);
        map.put("businessNo", businessNo);
        map.put("orderNo", orderNo);
        map.put("repayPeriod", repayPeriod);
        map.put("repayMoney", repayMoney);
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
                    intent.putExtra("tradeStatus", head.getTradeStatus().toString());
                    startActivity(intent);
                } else {
                    ToastUtils.showSafeToast(PayActivity.this, head.getResponseMsg());
                }
            }
        });

    }

    TextView timeCount;
    EditText codeET;
    private AlertDialog dialogSMS;

    public void showSMSDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_bank_sms, null);
        Button btn = (Button) view.findViewById(R.id.bank_sms_sure);
        ImageView ivClose = (ImageView) view.findViewById(R.id.iv_close);
        btn.setOnClickListener(this);
        ivClose.setOnClickListener(this);
        builder.setView(view);
        timeCount = (TextView) view.findViewById(R.id.bank_sms_time);
        timeCount.setOnClickListener(this);
        codeET = (EditText) view.findViewById(R.id.verification_code);
        dialogSMS = builder.create();
        dialogSMS.setCancelable(false);
        dialogSMS.setView(view, 0, 0, 0, 0);
        dialogSMS.show();
        startSMSCounter(60);
        dialogSMS.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                cancelCountTimer(countTimerSMS);
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.bank_sms_time:
                //重新发送验证码
                cancelCountTimer(countTimerSMS);
                startSMSCounter(60);
                break;
            case R.id.bank_sms_sure:
                hintKb();
                if (IsNullUtils.isNull(codeET.getText().toString())) {
                    toast("请输入短信验证码");
                    return;
                }
                commitPayInfo(codeET.getText().toString());
                dialogSMS.dismiss();
                break;
            case R.id.iv_close:
                dialogSMS.dismiss();
                break;

        }
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
                                if (dialogSMS != null && dialogSMS.isShowing()) {
                                    dialogSMS.dismiss();
                                }
                                timeUpDialog.dismiss();
                                PayActivity.this.finish();
                            }
                        }).show();
            }
        };
        countTimerOder.start();
    }

    private void startSMSCounter(final int sec) {
        cancelCountTimer(countTimerSMS);
        countTimerSMS = new CountDownTimer((sec + 1) * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int left = (int) ((millisUntilFinished - 1000) / 1000);
                timeCount.setText("" + left + "s");
                timeCount.setTextColor(getResources().getColor(R.color.app_hint_text_color));
                timeCount.setEnabled(false);
            }

            @Override
            public void onFinish() {
                timeCount.setText("获取验证码");
                timeCount.setTextColor(getResources().getColor(R.color.app_blue_color));
                timeCount.setEnabled(true);
            }
        };
        countTimerSMS.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cancelCountTimer(countTimerOder);
        cancelCountTimer(countTimerSMS);
    }

    private void cancelCountTimer(CountDownTimer countTimer) {
        if (countTimer != null) {
            countTimer.cancel();
        }
    }
}
