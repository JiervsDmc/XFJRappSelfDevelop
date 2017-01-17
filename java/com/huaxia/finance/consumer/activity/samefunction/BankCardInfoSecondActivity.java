package com.huaxia.finance.consumer.activity.samefunction;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.activity.protocol.SignedBankCardProtocolActivity;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.CityDataUtil;
import com.huaxia.finance.consumer.util.IsNullUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class BankCardInfoSecondActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.bank2_name)
    private TextView userName;
    @ViewInject(R.id.bank2_number)
    private TextView cardNumber;
    @ViewInject(R.id.bank2_bank_name)
    private TextView bankName;
    @ViewInject(R.id.bank2_pho)
    private TextView mobilNumber;
    @ViewInject(R.id.update_card2)
    private TextView updateCard2;
    @ViewInject(R.id.update_card1)
    private TextView updateCard1;
    @ViewInject(R.id.qianshudaikou)
    private Button btn;
    @ViewInject(R.id.tv_branch_city)
    private TextView tvBranchCity;
    @ViewInject(R.id.tv_branch_name)
    private TextView tvBranchName;
    private String bankCard;
    private CityDataUtil cityDataUtil;
    @Override
    protected int getLayout() {
        return R.layout.activity_bank_card_info_second;
    }

    @Override
    protected String getTitleText() {
        return "银行卡信息";
    }

    @Override
    protected void setup() {
        super.setup();
        cityDataUtil = new CityDataUtil(this);
        btn.setOnClickListener(this);
        updateCard1.setOnClickListener(this);
        updateCard2.setOnClickListener(this);
        getDate();
    }

    public void getDate() {
        Map map = new HashMap();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        ApiCaller.call(this, Constant.URL, "0027", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                if(head.getResponseCode().equals("0000")){
                    if (body.size() == 0) {
                        return;
                    }
                    setDataToView(body);
                }else{
                    toast(head.getResponseMsg());
                }
            }
        });
    }

    public void setDataToView(Map date) {
        //为空时标识没有签约，其余均可是为已签约
        if (IsNullUtils.isNull(""+date.get("isContracted"))) {
            btn.setVisibility(View.VISIBLE);
            updateCard2.setVisibility(View.VISIBLE);
            updateCard1.setVisibility(View.GONE);
        } else {
            btn.setVisibility(View.GONE);
            updateCard2.setVisibility(View.GONE);
            updateCard1.setVisibility(View.VISIBLE);
        }
        bankCard = ""+date.get("tailCardNo");
        userName.setText("" + date.get("realName"));
        cardNumber.setText("**** **** **** " + date.get("tailCardNo"));
        bankName.setText("" + date.get("bankName"));
        tvBranchCity.setText(cityDataUtil.codeGetString(date.get("provinceId").toString(),date.get("cityId").toString()));
        tvBranchName.setText(date.get("bankAddress").toString());
        mobilNumber.setText("" + date.get("cellphone"));
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        Intent intent = null;
        switch (view.getId()) {
            case R.id.qianshudaikou:
                intent = new Intent(BankCardInfoSecondActivity.this, SignedBankCardProtocolActivity.class);
                intent.putExtra("BankCard", bankCard);
                startActivity(intent);
                break;
            case R.id.update_card1:
                intent = new Intent(this, BankCardBfInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.update_card2:
                intent = new Intent(this, BankCardBfInfoActivity.class);
                startActivity(intent);
                break;
        }
    }
}
