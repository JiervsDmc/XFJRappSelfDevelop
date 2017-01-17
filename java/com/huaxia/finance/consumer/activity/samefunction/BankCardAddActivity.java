package com.huaxia.finance.consumer.activity.samefunction;

import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.activity.order.PayActivity;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.DialogSelectAddress;
import com.huaxia.finance.consumer.util.IsNullUtils;
import com.huaxia.finance.consumer.util.ListViewDialog;
import com.huaxia.finance.consumer.util.LogUtil;
import com.huaxia.finance.consumer.util.RegularExpressionUtil;
import com.huaxia.finance.consumer.util.ToastUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class BankCardAddActivity extends BaseActivity implements ListViewDialog.OnSelectItem, DialogSelectAddress.OnSelectCityDialog {
    @ViewInject(R.id.et_user_name)
    EditText et_user_name;
    @ViewInject(R.id.et_bank_name)
    EditText et_bank_name;
    @ViewInject(R.id.et_bank_card_number)
    EditText et_bank_card_number;
    @ViewInject(R.id.tv_bank_card_location)
    TextView tv_bank_card_location;
    @ViewInject(R.id.et_branch_city)
    EditText et_branch_city;
    @ViewInject(R.id.et_phone_number)
    EditText et_phone_number;
    @ViewInject(R.id.et_identify_code)
    EditText et_identify_code;
//    @ViewInject(R.id.iv_select)
//    ImageView iv_select;
    @ViewInject(R.id.tv_count)
    TextView tv_count;
    private ListViewDialog listViewDialog;
    private DialogSelectAddress dialogCity;
    private String pid;
    private String cid;
//    private boolean isAgree;
    private String bankCode;
    private CountDownTimer countDownTimer;
    private String transId;
    private String realName;
    @Override
    protected int getLayout() {
        return R.layout.activity_bank_card_add;
    }

    @Override
    protected String getTitleText() {
        return "添加银行卡";
    }

    @Override
    protected void setup() {
        super.setup();
        realName = getIntent().getStringExtra("realName");
        et_user_name.setText(realName);
        listViewDialog = new ListViewDialog(this, this);
        dialogCity = new DialogSelectAddress(this, this);
    }

    public boolean checkIsNull(boolean hasCode) {
        if (IsNullUtils.isNull(et_bank_name.getText().toString()) || et_bank_name.getText().toString().equals("请选择银行")) {
            toast("请选择银行");
            return true;
        }
        if (!RegularExpressionUtil.isBankCard(et_bank_card_number.getText().toString())) {
            toast("请填写正确的银行卡号");
            return true;
        }
        if (IsNullUtils.isNull(tv_bank_card_location.getText().toString())) {
            toast("请填写银行卡所在地");
            return true;
        }
        if (IsNullUtils.isNull(et_branch_city.getText().toString())) {
            toast("请填写开户支行");
            return true;
        }
        if (IsNullUtils.isNull(et_phone_number.getText().toString())) {
            toast("请填写预留手机号码");
            return true;
        }
        if (hasCode && IsNullUtils.isNull(et_identify_code.getText().toString())) {
            toast("请填写验证码");
            return true;
        }
        return false;
    }

    public void uploadDate() {
        final Map map = new HashMap();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        map.put("cardNo", et_bank_card_number.getText().toString());
        map.put("bankName", et_bank_name.getText().toString());
        map.put("bankAddress", et_branch_city.getText().toString());
        map.put("cellphone", et_phone_number.getText().toString());
        map.put("provinceId", pid);
        map.put("cityId", cid);
        map.put("realName", et_user_name.getText().toString());
        map.put("bankCode", bankCode);
        map.put("smsCode", et_identify_code.getText().toString());
        map.put("transId", transId);
        LogUtil.getLogutil().d("bankCodebankCode = " + bankCode);
        ApiCaller.call(this, Constant.URL, "0049", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                //toast("网络异常");
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                if (head.getResponseCode().equals("0000")) {
                    Intent intent = new Intent(BankCardAddActivity.this, PayActivity.class);
                    intent.putExtra("refresh","true");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    cancelCountTimer(countDownTimer);
                    startActivity(intent);
                } else {
                    ToastUtils.showSafeToast(BankCardAddActivity.this, head.getResponseMsg());
                }

            }
        });
    }

//    @OnClick(R.id.iv_select)
//    public void iv_select(View view) {
//        isAgree = !isAgree;
//        iv_select.setImageDrawable(isAgree ? getResources().getDrawable(R.drawable.add_card_selected) : getResources().getDrawable(R.drawable.add_card_default));
//    }

//    @OnClick(R.id.tv_protocol)
//    public void tv_protocol(View view) {
//
//    }

    @OnClick(R.id.et_bank_name)
    public void et_bank_name(View view) {
        listViewDialog.show("银行名称", getResources().getStringArray(R.array.pay_bank_card_name));
    }

    @OnClick(R.id.bank_card_btn)
    public void bank_card_btn(View view) {
//        if (!isAgree) {
//            ToastUtils.showSafeToast(this, "请先同意认证协议");
//            return;
//        }
        if(!checkIsNull(true)){
            uploadDate();
        }
    }

    @OnClick(R.id.tv_bank_card_location)
    public void tv_bank_card_location(View view) {
        dialogCity.showDialog();
    }

    @OnClick(R.id.tv_count)
    public void tv_count(View view) {
        if(!checkIsNull(false)){
            getSMSCode();
        }
    }

    private void getSMSCode() {
        final Map map = new HashMap();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        map.put("accountName", et_user_name.getText().toString());
        map.put("accountNo", et_bank_card_number.getText().toString());
        map.put("mobileNo", et_phone_number.getText().toString());
        map.put("bankCode", bankCode);
        ApiCaller.call(this, Constant.URL, "0048", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                //toast("网络异常");
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                if (head.getResponseCode().equals("0000")) {
                    transId = (String) body.get("transId");
                    startCounter(60);
                } else {
                    ToastUtils.showSafeToast(BankCardAddActivity.this, head.getResponseMsg());
                }

            }
        });
    }

    @Override
    public void onListViewSelect(String selected) {
        et_bank_name.setText(selected);
        switch (selected) {
            case "中国工商银行":
                bankCode = "B005";
                break;
            case "中国农业银行":
                bankCode = "B008";
                break;
            case "中国银行":
                bankCode = "B007";
                break;
            case "中国光大银行":
                bankCode = "B013";
                break;
            case "中信银行":
                bankCode = "B012";
                break;
            case "广东发展银行":
                bankCode = "B019";
                break;
            case "平安银行":
                bankCode = "B021";
                break;
            case "中国民生银行":
                bankCode = "B014";
                break;

            case "中国建设银行":
                bankCode = "B006";
                break;
            case "交通银行":
                bankCode = "B009";
                break;
            case "兴业银行":
                bankCode = "B015";
                break;
            case "招商银行":
                bankCode = "B010";
                break;
            case "上海浦东发展银行":
                bankCode = "B017";
                break;
            case "华夏银行":
                bankCode = "B016";
                break;
            case "中国邮政储蓄银行":
                bankCode = "B011";
                break;
            case "上海银行":
                bankCode = "B020";
                break;
        }
        LogUtil.getLogutil().d("bankCode = " + bankCode);
    }

    /**
     * 获取验证码倒计时
     *
     * @param sec 倒计时时间（单位：s）
     */
    private void startCounter(int sec) {
        countDownTimer = new CountDownTimer((sec + 1) * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int left = (int) ((millisUntilFinished - 1000) / 1000);
                tv_count.setText("" + left + "s");
                tv_count.setTextColor(getResources().getColor(R.color.app_hint_text_color));
                tv_count.setEnabled(false);
            }

            @Override
            public void onFinish() {
                tv_count.setText("获取验证码");
                tv_count.setTextColor(getResources().getColor(R.color.app_blue_color));
                tv_count.setEnabled(true);
            }
        };
        countDownTimer.start();
    }

    private void cancelCountTimer(CountDownTimer countTimer) {
        if (countTimer != null) {
            countTimer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        cancelCountTimer(countDownTimer);
        super.onDestroy();
    }

    @Override
    public void onSelectCity(String selectedCity, String cityCode, String pidCode) {
        tv_bank_card_location.setText(selectedCity);
        pid = pidCode;
        cid = cityCode;
    }
}
