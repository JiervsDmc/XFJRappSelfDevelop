package com.huaxia.finance.consumer.activity.samefunction;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.activity.protocol.SignedBankCardProtocolActivity;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.ConvertUtils;
import com.huaxia.finance.consumer.util.DialogSelectAddress;
import com.huaxia.finance.consumer.util.IsNullUtils;
import com.huaxia.finance.consumer.util.ListViewDialog;
import com.huaxia.finance.consumer.util.LogUtil;
import com.huaxia.finance.consumer.util.RegularExpressionUtil;
import com.huaxia.finance.consumer.util.ToastUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class BankCardBfInfoActivity extends BaseActivity implements View.OnClickListener, ListViewDialog.OnSelectItem, DialogSelectAddress.OnSelectCityDialog {

    @ViewInject(R.id.bankCard_userName)
    private EditText bankCardUserName;
    @ViewInject(R.id.ID_card_number)
    private EditText IDCardNumber;
    @ViewInject(R.id.bank_card_number)
    private EditText bankCardNumber;
    @ViewInject(R.id.bank_card_name)
    private TextView bankCardName;
    @ViewInject(R.id.bank_card_phone_number)
    private TextView bankCardPhoneNumber;
    @ViewInject(R.id.bank_card_btn)
    private Button nextBtn;
    @ViewInject(R.id.iv_select)
    private ImageView ivSelect;
    @ViewInject(R.id.tv_branch_city)
    private TextView tvBranchCity;
    @ViewInject(R.id.et_branch_name)
    private EditText etBranchName;

    private ListViewDialog listViewDialog;
    private AlertDialog dialog;
    private TextView timeCount;
    private EditText codeET;
    private DialogSelectAddress dialogCity;
    private String pid;
    private String cid;
    private String bankCode;
    private String tradeNo;
    private CountDownTimer countDownTimer;

    @Override
    protected int getLayout() {
        return R.layout.activity_bank_card_info;
    }

    @Override
    protected String getTitleText() {
        return "银行卡信息";
    }

    @Override
    protected void setup() {
        super.setup();
        nextBtn.setOnClickListener(this);
        bankCardName.setOnClickListener(this);
        ivSelect.setOnClickListener(this);
        tvBranchCity.setOnClickListener(this);
        listViewDialog = new ListViewDialog(this, this);
        dialogCity = new DialogSelectAddress(this,this);
    }

    public void checkIsNull() {
        if (IsNullUtils.isNull(bankCardUserName.getText().toString())) {
            toast("请填写开户姓名");
            return;
        }
        if (IsNullUtils.isNull(IDCardNumber.getText().toString())) {
            toast("请填写身份证号码");
            return;
        }
        if (IsNullUtils.isNull(bankCardNumber.getText().toString())) {
            toast("请填写银行卡号");
            return;
        }
        if (!RegularExpressionUtil.isBankCard(bankCardNumber.getText().toString())) {
            toast("请填写正确的银行卡号");
            return;
        }
        if (IsNullUtils.isNull(bankCardName.getText().toString())) {
            toast("请填写银行名称");
            return;
        }
        if(IsNullUtils.isNull(tvBranchCity.getText().toString())) {
            toast("请填写还款开户行");
            return;
        } if(IsNullUtils.isNull(etBranchName.getText().toString())) {
            toast("请填写支行名称");
            return;
        }
        if (IsNullUtils.isNull(bankCardPhoneNumber.getText().toString())) {
            toast("请填写预留手机号码");
            return;
        }
        if (!RegularExpressionUtil.isIDCard(IDCardNumber.getText().toString())) {
            toast("请输入正确格式的身份证号码");
            return;
        }
        uploadDate();

    }

    /**
     * 预绑卡获取验证码
     */
    public void uploadDate() {
        final Map map = new HashMap();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        map.put("cardNo", bankCardNumber.getText().toString());
        map.put("bankName", bankCardName.getText().toString());
        map.put("bankAddress",etBranchName.getText().toString());
        map.put("cellphone", bankCardPhoneNumber.getText().toString());
        map.put("provinceId",pid);
        map.put("cityId", cid);
        map.put("realName", bankCardUserName.getText().toString());
        map.put("bankCode", bankCode);
        map.put("idCard", IDCardNumber.getText().toString().toUpperCase());
        ApiCaller.call(this, Constant.URL, "0048", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                //toast("网络异常");
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                LogUtil.getLogutil().d("绑卡获取验证码0048 = " + response);
                if (head.getResponseCode().equals("0000")) {
                    ToastUtils.showSafeToast(BankCardBfInfoActivity.this, head.getResponseMsg());
                    tradeNo = ConvertUtils.mapToString(body,"tradeNo");
                    if (dialog == null || !dialog.isShowing()) {
                        showSMSDialog();
                    }
                } else if (head.getResponseCode().equals("8888")){
                    Intent intent = new Intent(BankCardBfInfoActivity.this, SignedBankCardProtocolActivity.class);
                    intent.putExtra("BankCard", bankCardNumber.getText().toString());
                    startActivity(intent);
                }else if(head.getResponseCode().equals("0025")){
                    ToastUtils.showSafeToast(BankCardBfInfoActivity.this, head.getResponseMsg());
                    finish();
                }else {
                    ToastUtils.showSafeToast(BankCardBfInfoActivity.this, head.getResponseMsg());
                }
            }
        });
    }

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
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                cancelTimer(countDownTimer);
            }
        });
        startCounter(60);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.bank_card_btn:
                checkIsNull();
                break;
            case R.id.bank_card_name:
                listViewDialog.show("银行名称", getResources().getStringArray(R.array.bank_card_name));
                break;
            case R.id.bank_sms_sure:
                hintKb();
                if (IsNullUtils.isNull(codeET.getText().toString())) {
                    toast("请输入短信验证码");
                    return;
                }
                uploadBindDate();
                break;
            case R.id.bank_sms_time:
                //重新发送短信调用原来的接口
                uploadDate();
                break;
            case R.id.iv_close:
                dialog.dismiss();
                break;
            case R.id.iv_select:
            case R.id.tv_branch_city:
                dialogCity.showDialog();
                break;

        }
    }

    /**
     * 银行卡绑定
     */
    public void uploadBindDate() {
        Map map = new HashMap();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        map.put("cardNo", bankCardNumber.getText().toString());
        map.put("bankName", bankCardName.getText().toString());
        map.put("bankAddress",etBranchName.getText().toString());
        map.put("cellphone", bankCardPhoneNumber.getText().toString());
        map.put("provinceId",pid);
        map.put("cityId", cid);
        map.put("realName", bankCardUserName.getText().toString());
        map.put("bankCode", bankCode);
        map.put("smsCode", codeET.getText().toString());
        map.put("tradeNo", tradeNo);
        map.put("idCard", IDCardNumber.getText().toString().toUpperCase());
        ApiCaller.call(this, Constant.URL, "0049", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                LogUtil.getLogutil().d("确认绑卡0049 = " + response);
                if (head.getResponseCode().equals("0000")) {
                    dialog.dismiss();
//                    Intent intent = new Intent(BankCardBfInfoActivity.this, UserInfoActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                    startActivity(intent);
                    //银行卡绑定
                    Intent intent = new Intent(BankCardBfInfoActivity.this, SignedBankCardProtocolActivity.class);
                    intent.putExtra("BankCard", bankCardNumber.getText().toString());
                    startActivity(intent);
                } else {
                    toast(head.getResponseMsg());
                }
            }
        });
    }

    @Override
    public void onListViewSelect(String selected) {
        //中国工商银行 B005
        bankCardName.setText(selected);
        switch (selected){
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
    }

    /**
     * 获取验证码倒计时
     *
     * @param sec 倒计时时间（单位：s）
     */
    private void startCounter(int sec) {
        cancelTimer(countDownTimer);
        countDownTimer = new CountDownTimer((sec + 1) * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int left = (int) ((millisUntilFinished - 1000) / 1000);
                timeCount.setText("" + left + "s");
                timeCount.setTextColor(getResources().getColor(R.color.app_hint_text_color));
                timeCount.setEnabled(false);
                dialog.setCancelable(false);
            }

            @Override
            public void onFinish() {
                timeCount.setText("获取验证码");
                timeCount.setTextColor(getResources().getColor(R.color.app_blue_color));
                timeCount.setEnabled(true);
                dialog.setCancelable(true);
            }
        };
        countDownTimer.start();
    }

    @Override
    public void onSelectCity(String selectedCity, String cityCode, String pidCode) {
        tvBranchCity.setText(selectedCity);
        pid = pidCode;
        cid = cityCode;
    }

    private void cancelTimer(CountDownTimer countTimer){
        if(countTimer!=null){
            countTimer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        cancelTimer(countDownTimer);
        super.onDestroy();
    }
}