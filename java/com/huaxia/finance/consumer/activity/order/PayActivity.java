package com.huaxia.finance.consumer.activity.order;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.activity.samefunction.BankCardAddActivity;
import com.huaxia.finance.consumer.adapter.BankCardSelectAdapter;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.bean.CardSelectBean;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.IsNullUtils;
import com.huaxia.finance.consumer.util.LogUtil;
import com.huaxia.finance.consumer.util.ToastUtils;
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
    private View footView;
    private BankCardSelectAdapter banckCardSelectAdater;
    List<CardSelectBean> list = new ArrayList<>();
    private boolean enterAddCardActivity;
    private CountDownTimer countTimerOder;
    private CountDownTimer countTimerSMS;

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
        startOderCounter(200);
        footView = LayoutInflater.from(this).inflate(R.layout.item_footer_add_card,null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //        getPayInfo();
        getTestInfo();
    }
    private void getTestInfo() {
        list.clear();
        for (int i = 0;i<5;i++){
            CardSelectBean bean = new CardSelectBean();
            if(i==0){
                bean.setSelectType("1");
            }else{
                bean.setSelectType("2");
            }
            bean.setCardName("建设银行");
            bean.setImgType("0");
            list.add(bean);
        }
        if(list.size() == 0){
            enterAddCardActivity =true;
            CardSelectBean bean = new CardSelectBean();
            bean.setCardName("您还未绑定银行卡，请点击绑定");
            bean.setImgType("0");
            bean.setSelectType("0");
            list.add(bean);
            btn_pay.setBackgroundColor(getResources().getColor(R.color.app_to_login));
            btn_pay.setClickable(false);
        }else{
            btn_pay.setBackgroundColor(getResources().getColor(R.color.app_blue_color));
            btn_pay.setClickable(true);
            if(lv_bankcard.getFooterViewsCount()>0){
                lv_bankcard.removeFooterView(footView);
            }
            lv_bankcard.addFooterView(footView);
        }
        banckCardSelectAdater = new BankCardSelectAdapter(this,list);

        footView.findViewById(R.id.rl_add_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PayActivity.this, BankCardAddActivity.class);
                startActivity(intent);
            }
        });
        lv_bankcard.setAdapter(banckCardSelectAdater);
        lv_bankcard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(enterAddCardActivity){
                    startActivity(new Intent(PayActivity.this,BankCardAddActivity.class));
                    return;
                }
                for (int i = 0;i<list.size();i++){
                    list.get(i).setSelectType("2");
                }
                list.get(position).setSelectType("1");
                banckCardSelectAdater.notifyDataSetChanged();
            }
        });
    }

    private void getPayInfo() {
        Map<String, Object> mapDetail = new HashMap<>();
        mapDetail.put("userUuid",mgr.getVal(UniqueKey.APP_USER_ID));
        ApiCaller.call(this, Constant.URL, "0009", "appService", mapDetail, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);

            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                LogUtil.getLogutil().d("支付确认"+response);
                if(head.getResponseCode().contains("0000")) {
//                    BankCardSelectAdapter banckCardSelectAdater = new BankCardSelectAdapter();
//                    lv_bankcard.setAdapter(banckCardSelectAdater);
                }else {
                    ToastUtils.showSafeToast(PayActivity.this,head.getResponseMsg());
                }
            }
        });
    }

    @OnClick(R.id.btn_pay)
    public void Pay(View view){
          checkPayInfo();
    }

    private void checkPayInfo() {
        //检查是否选择银行卡

        showSMSDialog();
    }

    private void commitPayInfo() {
        Intent intent = new Intent(this,PayResultActivity.class);
        startActivity(intent);
    }

    TextView timeCount;
    EditText codeET;
    private AlertDialog dialog;
    public void showSMSDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_bank_sms, null);
        Button btn = (Button) view.findViewById(R.id.bank_sms_sure);
        ImageView ivClose=(ImageView)view.findViewById(R.id.iv_close);
        btn.setOnClickListener(this);
        ivClose.setOnClickListener(this);
        builder.setView(view);
        timeCount = (TextView) view.findViewById(R.id.bank_sms_time);
        timeCount.setOnClickListener(this);
        codeET = (EditText) view.findViewById(R.id.verification_code);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
        startSMSCounter(10);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                cancelCountTimer(countTimerSMS);
            }
        });
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.bank_sms_time:
                //重新发送验证码
                cancelCountTimer(countTimerSMS);
                startSMSCounter(10);
                break;
            case R.id.bank_sms_sure:
                hintKb();
                if (IsNullUtils.isNull(codeET.getText().toString())) {
                    toast("请输入短信验证码");
                    return;
                }
                dialog.dismiss();
                commitPayInfo();
                break;
            case R.id.iv_close:
                dialog.dismiss();
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
                    if(second<10){
                        tv_time_left.setText("剩余 " + minute + " ：0" + second);
                    }else{
                        tv_time_left.setText("剩余 " + minute + " ：" + second);
                    }
            }

            @Override
            public void onFinish() {
                    tv_time_left.setText("订单超时");
                    btn_pay.setBackgroundColor(getResources().getColor(R.color.app_to_login));
                    btn_pay.setClickable(false);
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

    private void cancelCountTimer(CountDownTimer countTimer){
        if(countTimer!=null){
            countTimer.cancel();
        }
    }
}
