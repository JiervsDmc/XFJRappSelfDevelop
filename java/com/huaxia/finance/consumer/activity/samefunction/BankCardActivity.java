package com.huaxia.finance.consumer.activity.samefunction;


import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.LogUtil;
import com.huaxia.finance.consumer.util.ToastUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Call;

public class BankCardActivity extends BaseActivity {
    @ViewInject(R.id.bank_number)
    private TextView bankNumber;
    @ViewInject(R.id.iv_bank)
    private ImageView ivBank;
    @ViewInject(R.id.ll_bank)
    private LinearLayout llBank;
    @ViewInject(R.id.tv_bank_name)
    private TextView tvBankName;
    @ViewInject(R.id.tv_xing)
    private TextView tvXing;


    @Override
    protected int getLayout() {
        return R.layout.activity_bank_card;
    }

    @Override
    protected String getTitleText() {
        return "我的银行卡";
    }

    @Override
    protected void setup() {
        super.setup();
        getBankCardInform();
    }
    //获取银行卡信息
    private void getBankCardInform() {
        Map<String, Object> map = new HashMap<>();
        map.put("userUuid",mgr.getVal(UniqueKey.APP_USER_ID));
        ApiCaller.call(this, Constant.URL, "0027", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                tvXing.setVisibility(View.GONE);
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                LogUtil.getLogutil().d("银行卡信息结果是"+response);
                if(head.getResponseCode().contains("0000")) {
                    if(!body.isEmpty()) {
                        tvXing.setVisibility(View.VISIBLE);
                        if(body.get("bankName").toString().contains("中国工商银行")) {
                            llBank.setBackgroundResource(R.drawable.bank_red);
                            ivBank.setBackgroundResource(R.drawable.icbc);
                            tvBankName.setText(body.get("bankName").toString());
                        }else if(body.get("bankName").toString().contains("招商银行")) {
                            llBank.setBackgroundResource(R.drawable.bank_red);
                            ivBank.setBackgroundResource(R.drawable.cmb);
                            tvBankName.setText(body.get("bankName").toString());
                        }else if(body.get("bankName").toString().contains("中国农业银行")) {
                            llBank.setBackgroundResource(R.drawable.bank_blue);
                            ivBank.setBackgroundResource(R.drawable.abc);
                            tvBankName.setText(body.get("bankName").toString());
                        }else if(body.get("bankName").toString().contains("广东发展银行")) {
                            llBank.setBackgroundResource(R.drawable.bank_red);
                            ivBank.setBackgroundResource(R.drawable.cgb);
                            tvBankName.setText(body.get("bankName").toString());
                        }else if(body.get("bankName").toString().contains("中信银行")) {
                            llBank.setBackgroundResource(R.drawable.bank_red);
                            ivBank.setBackgroundResource(R.drawable.ccb);
                            tvBankName.setText(body.get("bankName").toString());
                        }else if(body.get("bankName").toString().contains("中国银行")) {
                            llBank.setBackgroundResource(R.drawable.bank_red);
                            ivBank.setBackgroundResource(R.drawable.boc);
                            tvBankName.setText(body.get("bankName").toString());
                        }else if(body.get("bankName").toString().contains("中国建设银行")) {
                            llBank.setBackgroundResource(R.drawable.bank_blue);
                            ivBank.setBackgroundResource(R.drawable.cj);
                            tvBankName.setText(body.get("bankName").toString());
                        }else if(body.get("bankName").toString().contains("中国光大银行")) {
                            llBank.setBackgroundResource(R.drawable.bank_blue);
                            ivBank.setBackgroundResource(R.drawable.ceb);
                            tvBankName.setText(body.get("bankName").toString());
                        }else if(body.get("bankName").toString().contains("中国民生银行")) {
                            llBank.setBackgroundResource(R.drawable.bank_blue);
                            ivBank.setBackgroundResource(R.drawable.cmbc);
                            tvBankName.setText(body.get("bankName").toString());
                        }else if(body.get("bankName").toString().contains("平安银行")) {
                            llBank.setBackgroundResource(R.drawable.bank_red);
                            ivBank.setBackgroundResource(R.drawable.pab);
                            tvBankName.setText(body.get("bankName").toString());
                        }else if(body.get("bankName").toString().contains("兴业银行")) {
                            llBank.setBackgroundResource(R.drawable.bank_blue);
                            ivBank.setBackgroundResource(R.drawable.cib);
                            tvBankName.setText(body.get("bankName").toString());
                        }
                        else if(body.get("bankName").toString().contains("交通银行")) {
                            llBank.setBackgroundResource(R.drawable.bank_blue);
                            ivBank.setBackgroundResource(R.drawable.bcm);
                            tvBankName.setText(body.get("bankName").toString());
                        }else if(body.get("bankName").toString().contains("华夏银行")) {
                            llBank.setBackgroundResource(R.drawable.bank_red);
                            ivBank.setBackgroundResource(R.drawable.hxm);
                            tvBankName.setText(body.get("bankName").toString());
                        }else if(body.get("bankName").toString().contains("上海浦东发展银行")) {
                            llBank.setBackgroundResource(R.drawable.bank_blue);
                            ivBank.setBackgroundResource(R.drawable.spdm);
                            tvBankName.setText(body.get("bankName").toString());
                        }
                        bankNumber.setText(body.get("tailCardNo").toString());
                        return;
                    }
                }else {
                    ToastUtils.showSafeToast(BankCardActivity.this,head.getResponseMsg());
                    tvXing.setVisibility(View.GONE);
                }
            }

        });
    }
}
