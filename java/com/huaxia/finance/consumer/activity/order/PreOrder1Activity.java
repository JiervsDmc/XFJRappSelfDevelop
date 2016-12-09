package com.huaxia.finance.consumer.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.activity.CaptureActivity;
import com.huaxia.finance.consumer.activity.protocol.ConsultationProtocolActivity;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.AccountMgr;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.IsNullUtils;
import com.huaxia.finance.consumer.util.ListDialog;
import com.huaxia.finance.consumer.util.LogUtil;
import com.huaxia.finance.consumer.util.RegularExpressionUtil;
import com.huaxia.finance.consumer.util.ToastUtils;
import com.huaxia.finance.consumer.util.json.JsonUtils;
import com.huaxia.finance.consumer.view.ClearEditText;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class PreOrder1Activity extends BaseActivity implements View.OnClickListener,ListDialog.OnSelectItem, View.OnFocusChangeListener {
    @ViewInject(R.id.order_next)
    private TextView orderNext;
    @ViewInject(R.id.tv_commodity)
    private TextView tvCommodity;
    @ViewInject(R.id.merchant_name)
    private LinearLayout merchantName;
    @ViewInject(R.id.tv_merchant_name)
    private EditText tvMerchantName;
    @ViewInject(R.id.merchant_classification)
    private LinearLayout merchantClassification;
    @ViewInject(R.id.tv_merchant_classification)
    private EditText tvMerchantClassification;
    @ViewInject(R.id.stage_number)
    private LinearLayout stageNumber;
    @ViewInject(R.id.tv_stage_number)
    private EditText tvStageNumber;
    @ViewInject(R.id.et_commodity_name)
    private ClearEditText etCommodityName;
    @ViewInject(R.id.et_commodity_price)
    private ClearEditText etCommodityPrice;
    @ViewInject(R.id.tv_price)
    private TextView tvPrice;
    @ViewInject(R.id.et_shoufu)
    private ClearEditText etShoufu;
    @ViewInject(R.id.tv_installment_amount)
    private EditText tvInstallmentAmount;
    @ViewInject(R.id.each_should_also)
    private EditText eachShouldAlso;
    @ViewInject(R.id.one_time_interest)
    private EditText oneTimeInterest;
    @ViewInject(R.id.tv_salesperson)
    private TextView tvSalesperson;
    @ViewInject(R.id.et_salesperson_name)
    private ClearEditText etSalespersonName;
    @ViewInject(R.id.et_salesperson_phone)
    private ClearEditText etSalespersonPhone;
    @ViewInject(R.id.tv_trial)
    private TextView tvTrial;

    private ListDialog listViewDialog;
    private int clickTag=-1;
    private String role;
    private String[] dates;
    private String[] number;
    private String freePeriod;
    private List loanTermsList=null;
    private List ratesList=null;
    private List periodList;
    private String productTypeId;
    private String companyId;
    private String orderNo;

    private String shouF;
    private String shouE;
    private String shouM;
    private String commodityPriceE;
    private String commodityPriceF;
    private String  commodityPriceM;
    private String[] id;
    private String mMonthlyFeeRate;
    private final static int SCANNIN_GREQUEST_CODE = 1;
    @Override
    protected int getLayout() {
        return R.layout.activity_pre_orders;
    }

    @Override
    protected String getTitleText() {
        return "购买订单";
    }

    @Override
    protected void setup() {
        super.setup();
        //getMerchant();
        mgr = new AccountMgr(this);
        role = getIntent().getStringExtra("role");
        //字体加粗
        fontWeight();
        //不可编辑
        cannotEdit();
        listViewDialog = new ListDialog(this,this);
        orderNext.setOnClickListener(this);
        merchantName.setOnClickListener(this);
        tvMerchantName.setOnClickListener(this);
        tvTrial.setOnClickListener(this);
        etCommodityPrice.setOnFocusChangeListener(this);
        etShoufu.setOnFocusChangeListener(this);
        etShoufu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                shouE=etShoufu.getText().toString();
                if(!IsNullUtils.isNull(shouF)&&!shouF.substring(0,shouF.lastIndexOf(".")).equals(shouE)) {
                    if(IsNullUtils.isNull(shouM)) {
                        if (!IsNullUtils.isNull(eachShouldAlso)) {
                            eachShouldAlso.getText().clear();
                        }
                        if (!IsNullUtils.isNull(oneTimeInterest)) {
                            oneTimeInterest.getText().clear();
                        }
                    }else if(!IsNullUtils.isNull(shouM)&&!shouE.contains(shouM)){
                        if (!IsNullUtils.isNull(eachShouldAlso)) {
                            eachShouldAlso.getText().clear();
                        }
                        if (!IsNullUtils.isNull(oneTimeInterest)) {
                            oneTimeInterest.getText().clear();
                        }
                    }
                }

            }
            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().startsWith("0")) {
                    if (editable.length() > 0 ) {
                        etShoufu.setFilters(new InputFilter[]{new InputFilter.LengthFilter(0)});
                    }
                }
                etShoufu.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
                String commodityPrice = null;
                if(etCommodityPrice.getText().toString().length()>0) {
                    commodityPrice=etCommodityPrice.getText().toString().substring(0,etCommodityPrice.getText().toString().lastIndexOf("."));
                }
                String shoufu=etShoufu.getText().toString();
                if(!TextUtils.isEmpty(commodityPrice)&&!TextUtils.isEmpty(shoufu)) {
                    if(new BigDecimal(etShoufu.getText().toString()).compareTo(new BigDecimal(etCommodityPrice.getText().toString()))>0) {
                        etShoufu.setText(etCommodityPrice.getText().toString().substring(0,etCommodityPrice.getText().toString().lastIndexOf(".")));
                    }
                    if(!etShoufu.getText().toString().contains(".00")) {
                        if(Integer.parseInt(commodityPrice)-Integer.parseInt(shoufu)<0) {
                            tvInstallmentAmount.setText("0.00");
                        }else {
                            tvInstallmentAmount.setText(String.valueOf(Integer.parseInt(commodityPrice)-Integer.parseInt(shoufu))+".00");
                            LogUtil.getLogutil().d("tvInstallmentAmount的值"+String.valueOf(Integer.parseInt(commodityPrice)-Integer.parseInt(shoufu))+".00");
                        }

                    }else {
                        if(Integer.parseInt(commodityPrice)-Integer.parseInt(etShoufu.getText().toString().substring(0,etShoufu.getText().toString().lastIndexOf(".")))<0) {
                            tvInstallmentAmount.setText("0.00");
                            tvTrial.setClickable(false);
                        }else {
                            tvInstallmentAmount.setText(String.valueOf(Integer.parseInt(commodityPrice)-Integer.parseInt(etShoufu.getText().toString().substring(0,etShoufu.getText().toString().lastIndexOf("."))))+".00");
                            LogUtil.getLogutil().d("tvInstallmentAmount的值"+String.valueOf(Integer.parseInt(commodityPrice)-Integer.parseInt(etShoufu.getText().toString().substring(0,etShoufu.getText().toString().lastIndexOf("."))))+".00");
                        }

                    }
                }else {
                    if(TextUtils.isEmpty(shoufu)&&!TextUtils.isEmpty(tvInstallmentAmount.getText().toString())&&!TextUtils.isEmpty(commodityPrice)) {
                        tvInstallmentAmount.setText(String.valueOf(new BigDecimal(commodityPrice).setScale(2,BigDecimal.ROUND_HALF_UP)));
                    }else if(TextUtils.isEmpty(shoufu)&&TextUtils.isEmpty(commodityPrice)) {
                        tvInstallmentAmount.getText().clear();
                    }
                }

            }
        });

        etCommodityPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                commodityPriceE=etCommodityPrice.getText().toString();
                LogUtil.getLogutil().d("commodityPriceE值"+commodityPriceE);

                if(!IsNullUtils.isNull(commodityPriceF)&&!commodityPriceF.substring(0,commodityPriceF.lastIndexOf(".")).equals(commodityPriceE)) {
                    if(IsNullUtils.isNull(commodityPriceM)) {
                        if (!IsNullUtils.isNull(eachShouldAlso)) {
                            eachShouldAlso.getText().clear();
                        }
                        if (!IsNullUtils.isNull(oneTimeInterest)) {
                            oneTimeInterest.getText().clear();
                        }
                    }else if(!IsNullUtils.isNull(commodityPriceM)&&!commodityPriceE.contains(commodityPriceM)){
                        if (!IsNullUtils.isNull(eachShouldAlso)) {
                            eachShouldAlso.getText().clear();
                        }
                        if (!IsNullUtils.isNull(oneTimeInterest)) {
                            oneTimeInterest.getText().clear();
                        }
                    }

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 1 && editable.toString().equals("0")) {
                    editable.clear();
                    return;
                }
                etCommodityPrice.setFilters(new InputFilter[]{new InputFilter.LengthFilter(7)});
                String shoufu = null;
                if(etShoufu.getText().toString().length()>0&&etShoufu.getText().toString().contains(".00")) {
                    shoufu=etShoufu.getText().toString().substring(0,etShoufu.getText().toString().lastIndexOf("."));
                }else if(etShoufu.getText().toString().length()>0&&!etShoufu.getText().toString().contains(".00")) {
                    shoufu=etShoufu.getText().toString();
                }
                String commodityPrice=etCommodityPrice.getText().toString();
                if(!TextUtils.isEmpty(commodityPrice)&&!TextUtils.isEmpty(shoufu)) {
                    if(!etCommodityPrice.getText().toString().contains(".00")) {
                        if(Integer.parseInt(commodityPrice)-Integer.parseInt(shoufu)<0) {
                            tvInstallmentAmount.setText("0.00");
                        }else {
                            tvInstallmentAmount.setText(String.valueOf(Integer.parseInt(commodityPrice)-Integer.parseInt(shoufu))+".00");
                            LogUtil.getLogutil().d("tvInstallmentAmount的值"+(Integer.parseInt(commodityPrice)-Integer.parseInt(shoufu)));
                        }
                    }else {
                        if(Integer.parseInt(shoufu)-Integer.parseInt(etCommodityPrice.getText().toString().substring(0,etCommodityPrice.getText().toString().lastIndexOf(".")))<0) {
                            tvInstallmentAmount.setText(String.valueOf(Integer.parseInt(etCommodityPrice.getText().toString().substring(0,etCommodityPrice.getText().toString().lastIndexOf(".")))-Integer.parseInt(shoufu))+".00");
                            LogUtil.getLogutil().d("tvInstallmentAmount的值"+String.valueOf(Integer.parseInt(etCommodityPrice.getText().toString().substring(0,etCommodityPrice.getText().toString().lastIndexOf(".")))-Integer.parseInt(shoufu))+".00");
                        }else {
                            tvInstallmentAmount.setText("0.00");
                        }
                    }
                } else {
                    if(TextUtils.isEmpty(shoufu)&&!TextUtils.isEmpty(tvInstallmentAmount.getText().toString())&&!TextUtils.isEmpty(commodityPrice)) {
                        tvInstallmentAmount.setText(String.valueOf(new BigDecimal(commodityPrice).setScale(2,BigDecimal.ROUND_HALF_UP)));
                    }else if(TextUtils.isEmpty(shoufu)&&TextUtils.isEmpty(commodityPrice)) {
                        tvInstallmentAmount.getText().clear();
                    }else if(TextUtils.isEmpty(etCommodityPrice.getText())) {
                        tvInstallmentAmount.getText().clear();
                    }
                }

            }
        });

    }

    private void cannotEdit() {
        tvMerchantClassification.setKeyListener(null);
        tvMerchantName.setKeyListener(null);
        tvInstallmentAmount.setKeyListener(null);
        tvStageNumber.setKeyListener(null);
        eachShouldAlso.setKeyListener(null);
        oneTimeInterest.setKeyListener(null);
    }

    private void fontWeight() {
        TextPaint tp= tvCommodity.getPaint();
        TextPaint tp1=tvPrice.getPaint();
        TextPaint tp2=tvSalesperson.getPaint();
        tp.setFakeBoldText(true);
        tp1.setFakeBoldText(true);
        tp2.setFakeBoldText(true);
    }
    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.order_next:
                generateOrder();
                break;
            case R.id.tv_merchant_name:
            case R.id.merchant_name:
                Intent intent=new Intent(this, CaptureActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
                break;
            case R.id.merchant_classification:
                clickTag=1;
                LogUtil.getLogutil().d("dates1的长度"+dates.length);
                if(!IsNullUtils.isNull(dates)) {
                    listViewDialog.show("商户分类", dates);
                }
                break;
            case R.id.stage_number:
                clickTag=2;
                if(!IsNullUtils.isNull(number)) {
                    listViewDialog.show("分期期数", number);
                }
                break;
            case R.id.tv_trial:
                if(!tvInstallmentAmount.getText().toString().equals("0.00")) {
                    toTrial();
                }else {
                    ToastUtils.showSafeToast(this,"分期金额不能为0");
                }

                break;
        }
    }

    @Override
    public void onListViewSelect(String selected,int position) {
        switch (clickTag){
            case 1:
                tvMerchantClassification.setText(selected);
                String mMerchantClassification= tvMerchantClassification.getText().toString().trim();
                if(!TextUtils.isEmpty(mMerchantClassification)) {
                    if(!IsNullUtils.isNull(etCommodityName)) {
                        etCommodityName.getText().clear();
                    } if(!IsNullUtils.isNull(etCommodityPrice)) {
                        etCommodityPrice.getText().clear();
                    }if(!IsNullUtils.isNull(etShoufu)) {
                        etShoufu.getText().clear();
                    }if(!IsNullUtils.isNull(tvInstallmentAmount)) {
                        tvInstallmentAmount.getText().clear();
                    }if(!IsNullUtils.isNull(tvStageNumber)) {
                        tvStageNumber.getText().clear();
                    }if(!IsNullUtils.isNull(eachShouldAlso)) {
                        eachShouldAlso.getText().clear();
                    }if(!IsNullUtils.isNull(oneTimeInterest)) {
                        oneTimeInterest.getText().clear();
                    }if(!IsNullUtils.isNull(etSalespersonName)) {
                        etSalespersonName.getText().clear();
                    }if(!IsNullUtils.isNull(etSalespersonPhone)) {
                        etSalespersonPhone.getText().clear();
                    }
                    stageNumber.setOnClickListener(this);
                    productTypeId=typeIdList.get(position).toString();
                    LogUtil.getLogutil().d("loanList的值"+loanTermsList.get(position));
                    String number1=(String)loanTermsList.get(position);
                    LogUtil.getLogutil().d("number的值"+number1);
                    number =  number1.replace("[","").replace("]","").split(",");
                    LogUtil.getLogutil().d("productTypeId的值"+productTypeId);
                    freePeriod=(String) periodList.get(position);
                    LogUtil.getLogutil().d("freePeriod的值"+freePeriod);
                    rates=(String)ratesList.get(position);
                    LogUtil.getLogutil().d("rates值....."+rates);
                }else {
                    stageNumber.setClickable(false);
                }

                break;
            case 2:
                tvStageNumber.setText(selected);
                String stageNumber=tvStageNumber.getText().toString().trim();
                if(!TextUtils.isEmpty(stageNumber)) {
                   if(!IsNullUtils.isNull(eachShouldAlso)) {
                        eachShouldAlso.getText().clear();
                    }if(!IsNullUtils.isNull(oneTimeInterest)) {
                        oneTimeInterest.getText().clear();
                    }if(!IsNullUtils.isNull(etSalespersonName)) {
                        etSalespersonName.getText().clear();
                    }if(!IsNullUtils.isNull(etSalespersonPhone)) {
                        etSalespersonPhone.getText().clear();
                    }
                    for (int j=0;j<number.length;j++){
                        if(stageNumber.equals(number[j].trim())){
                            mMonthlyFeeRate=rates.replace("[","").replace("]","").split(",")[j].trim();
                        }
                    }
                }
                break;

        }
    }

    private List list;
    private  List typeIdList;
    private List typeList;
    private  Map map1 ;
    private String rates;
    private void initData(List idList,List typeList) {
        this.list=idList;
        this.typeList=typeList;
    }

    //试算
    private void toTrial() {
        String merchantName =tvMerchantName.getText().toString().trim();
        String merchantClassification= tvMerchantClassification.getText().toString().trim();
        String commodityName=etCommodityName.getText().toString().trim();
        String commodityPrice=etCommodityPrice.getText().toString().trim();
        final String shoufu=etShoufu.getText().toString().trim();
        String stageNumber=tvStageNumber.getText().toString().trim();
        //String mMonthlyFeeRate=monthlyFeeRate.getText().toString().trim();
        if(TextUtils.isEmpty(merchantName)) {
            ToastUtils.showSafeToast(this,"商户名称不能为空");
            return;
        }else if(TextUtils.isEmpty(merchantClassification)) {
            ToastUtils.showSafeToast(this,"商品分类不能为空");
            return;
        }else if(TextUtils.isEmpty(commodityName)) {
            ToastUtils.showSafeToast(this,"商品名称不能为空");
            return;
        }else if(TextUtils.isEmpty(commodityPrice)) {
            ToastUtils.showSafeToast(this,"商品价格不能为空");
            return;
        }
        else if(TextUtils.isEmpty(shoufu)) {
            ToastUtils.showSafeToast(this,"首付不能为空");
            return;
        }
        else if(TextUtils.isEmpty(stageNumber)) {
            ToastUtils.showSafeToast(this,"分期期数不能为空");
            return;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("companyId",companyId);
        map.put("productTypeId",productTypeId);
        map.put("productPrice",commodityPrice);
        map.put("firstPayment",shoufu);
        map.put("periods",stageNumber);
        map.put("borrowRate",mMonthlyFeeRate);
        map.put("discountPeriods",freePeriod);
        ApiCaller.call(this, Constant.URL, "0008", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                LogUtil.getLogutil().d("试算结果是"+response);
                if(head.getResponseCode().contains("0000")&&!body.isEmpty()) {
                    eachShouldAlso.setText(body.get("pmtAmount").toString());
                    oneTimeInterest.setText(body.get("oneCusInterest").toString());
                    orderNo=body.get("orderNo").toString();
                    return;
                }else {
                    ToastUtils.showSafeToast(PreOrder1Activity.this, head.getResponseMsg());
                    return;
                }
            }

        });
    }

    private void generateOrder() {
        String merchantName =tvMerchantName.getText().toString().trim();
        String merchantClassification= tvMerchantClassification.getText().toString().trim();
        String commodityName=etCommodityName.getText().toString().trim();
        String commodityPrice=etCommodityPrice.getText().toString().trim();
        final String shoufu=etShoufu.getText().toString().trim();
        String installmentAmount=tvInstallmentAmount.getText().toString().trim();
        String stageNumber=tvStageNumber.getText().toString().trim();
        //String mMonthlyFeeRate=monthlyFeeRate.getText().toString().trim();
        String mEachShouldAlso=eachShouldAlso.getText().toString().trim();
        String mOneTimeInterest=oneTimeInterest.getText().toString().trim();
        String salespersonName=etSalespersonName.getText().toString().trim();
        String salespersonPhone=etSalespersonPhone.getText().toString().trim();
        if(TextUtils.isEmpty(merchantName)) {
            ToastUtils.showSafeToast(this,"商户名称不能为空");
            return;
        }else if(TextUtils.isEmpty(merchantClassification)) {
            ToastUtils.showSafeToast(this,"商品分类不能为空");
            return;
        }else if(TextUtils.isEmpty(commodityName)) {
            ToastUtils.showSafeToast(this,"商品名称不能为空");
            return;
        }else if(TextUtils.isEmpty(commodityPrice)) {
            ToastUtils.showSafeToast(this,"商品价格不能为空");
            return;
        }
        else if(TextUtils.isEmpty(shoufu)) {
            ToastUtils.showSafeToast(this,"首付不能为空");
            return;
        }
        else if(TextUtils.isEmpty(stageNumber)) {
            ToastUtils.showSafeToast(this,"分期期数不能为空");
            return;
        }else if(TextUtils.isEmpty(mEachShouldAlso)) {
            ToastUtils.showSafeToast(this,"每期应还不能为空");
            return;
        }else if(TextUtils.isEmpty(salespersonName)) {
            ToastUtils.showSafeToast(this,"销售员不能为空");
            return;
        }else if(TextUtils.isEmpty(salespersonPhone)) {
            ToastUtils.showSafeToast(this,"销售员电话不能为空");
            return;
        }else if(!RegularExpressionUtil.isMobileNO(salespersonPhone)) {
            ToastUtils.showSafeToast(this, "手机号码有误，请重新输入");
            return;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("userUuid",mgr.getVal(UniqueKey.APP_USER_ID));
        map.put("companyId",companyId);
        map.put("name",merchantName);
        map.put("productTypeId",productTypeId);
        map.put("productType",merchantClassification);
        LogUtil.getLogutil().d("companyId的值"+companyId);
        LogUtil.getLogutil().d("productTypeId的值"+productTypeId);
        map.put("productName",commodityName);
        map.put("productPrice",commodityPrice);
        map.put("firstPayment",shoufu);
        map.put("stagesMoney",installmentAmount);
        map.put("periods",stageNumber);
        map.put("onceInterest",mOneTimeInterest);
        map.put("borrowRate",mMonthlyFeeRate);
        map.put("discountPeriods",freePeriod);
        map.put("salesmanName",salespersonName);
        map.put("salesmanCellphone",salespersonPhone);
        map.put("orderNo",orderNo);
        Intent intent=new Intent(this, ConsultationProtocolActivity.class);
        intent.putExtra("role", role);
        intent.putExtra("type","2");
        List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();
        listData.add(map);
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", (Serializable) listData);
        intent.putExtras(bundle);
        startActivity(intent);
        LogUtil.getLogutil().d("集合转字符窜"+map);
    }


    @Override
    public void onFocusChange(View view, boolean hasFoucs) {
        switch (view.getId()){
            case R.id.et_commodity_price:
                if(!hasFoucs&&etCommodityPrice.getText().toString().length()>0&&!etCommodityPrice.getText().toString().contains(".00")) {
                    // etCommodityPrice.setText(etCommodityPrice.getText()+".00");
                    etCommodityPrice.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                    etCommodityPrice.setText(String.valueOf(new BigDecimal(etCommodityPrice.getText().toString()).setScale(2, BigDecimal.ROUND_HALF_UP)));
                    commodityPriceF=etCommodityPrice.getText().toString();
                    LogUtil.getLogutil().d("commodityPriceF值"+commodityPriceF);

                }else {
                    etCommodityPrice.setSelection(etCommodityPrice.getText().toString().toCharArray().length);
                    if(etCommodityPrice.getText().toString().length()>0) {
                        //etCommodityPrice.setText(etCommodityPrice.getText().toString().substring(0,etCommodityPrice.getText().toString().lastIndexOf(".")));
                        etCommodityPrice.setText(String.valueOf(new BigDecimal(etCommodityPrice.getText().toString()).setScale(2, BigDecimal.ROUND_HALF_UP)).substring(0,etCommodityPrice.getText().toString().lastIndexOf(".")));
                        commodityPriceM=etCommodityPrice.getText().toString();
                        LogUtil.getLogutil().d("commodityPriceM值"+commodityPriceM);
                    }
                }
                break;
            case R.id.et_shoufu:
                if(!hasFoucs&&etShoufu.getText().toString().length()>0&&!etShoufu.getText().toString().contains(".00")) {
                    //etShoufu.setText(etShoufu.getText()+".00");
                    etShoufu.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
                    etShoufu.setText(String.valueOf(new BigDecimal(etShoufu.getText().toString()).setScale(2, BigDecimal.ROUND_HALF_UP)));
                    shouF=etShoufu.getText().toString();
                    LogUtil.getLogutil().d("shouF的值"+shouF);
                }else {
                    etShoufu.setSelection(etShoufu.getText().toString().toCharArray().length);
                    if(etShoufu.getText().toString().length()>0) {
                        etShoufu.setText(String.valueOf(new BigDecimal(etShoufu.getText().toString()).setScale(2, BigDecimal.ROUND_HALF_UP)).substring(0,etShoufu.getText().toString().lastIndexOf(".")));
                        shouM=etShoufu.getText().toString();
                    }
                }
                break;
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if(resultCode == RESULT_OK){
                    Bundle bundle = data.getExtras();
                    //显示扫描到的内容
                   initView(bundle.getString("result"));
                    LogUtil.getLogutil().d("result的值"+bundle.getString("result"));
                }
                break;

        }
    }

    private void initView(String result) {
        try {
            JSONObject object=new JSONObject(result);
            if(!IsNullUtils.isNull(object.get("HXQRCODE"))) {
                JSONObject ob=new JSONObject(object.get("HXQRCODE").toString());
                companyId=ob.get("id").toString();
                LogUtil.getLogutil().d("companyId的值"+companyId);
                String mMerchantName =tvMerchantName.getText().toString().trim();
                if(!TextUtils.isEmpty(mMerchantName)) {
                    clear();
                    merchantClassification.setOnClickListener(PreOrder1Activity.this);
                }else {
                    merchantClassification.setClickable(false);
                }
                getMerchant();
            }
        } catch (Exception e) {
            //ToastUtils.showSafeToast(PreOrder1Activity.this,"二维码无效");
            e.printStackTrace();
        }

    }
    //获取商户信息
    private void getMerchant() {
        final Map<String, Object> map = new HashMap<>();
        map.put("id",companyId);
        ApiCaller.call(this, Constant.URL, "0040", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                LogUtil.getLogutil().d("商品信息是"+response);
                if(head.getResponseCode().contains("0000")&&!body.isEmpty()) {
                    List commodityList=new ArrayList();
                    commodityList=(List) body.get("result");
                    for (int j=0;j<commodityList.size();j++){
                        Map map=(Map)commodityList.get(j);
                        tvMerchantName.setText(map.get("companyName").toString());
                        String mMerchantName =tvMerchantName.getText().toString().trim();
                        if(!TextUtils.isEmpty(mMerchantName)) {
                            merchantClassification.setOnClickListener(PreOrder1Activity.this);
                        }else {
                            merchantClassification.setClickable(false);
                        }
                        List typeList=new ArrayList();
                        typeList=(List)map.get("productTypes");
                        List nameList=new ArrayList();
                        typeIdList=new ArrayList();
                        loanTermsList=new ArrayList();
                        ratesList=new ArrayList();
                        periodList=new ArrayList();
                        for (int i=0;i<typeList.size();i++){
                            map1=(Map)typeList.get(i);
                            nameList.add(map1.get("name").toString());
                            LogUtil.getLogutil().d("nameList的长度"+nameList);
                            typeIdList.add(map1.get("id"));
                            loanTermsList.add(map1.get("loanTerms").toString());
                            ratesList.add(map1.get("rates").toString());
                            periodList.add(map1.get("freePeriod").toString());
                            LogUtil.getLogutil().d("periodList的值"+periodList);
                        }
                        dates=(String[]) nameList.toArray(new String[typeList.size()]);
                        LogUtil.getLogutil().d("dates的长度"+dates.length);
                    }

                }else {
                    ToastUtils.showSafeToast(PreOrder1Activity.this,head.getResponseMsg());
                    merchantName.setClickable(false);
                }
            }
        });
    }

    private void clear() {
        if(!IsNullUtils.isNull(tvMerchantClassification)) {
            tvMerchantClassification.getText().clear();
        }if(!IsNullUtils.isNull(etCommodityName)) {
            etCommodityName.getText().clear();
        } if(!IsNullUtils.isNull(etCommodityPrice)) {
            etCommodityPrice.getText().clear();
        }if(!IsNullUtils.isNull(etShoufu)) {
            etShoufu.getText().clear();
        }if(!IsNullUtils.isNull(tvInstallmentAmount)) {
            tvInstallmentAmount.getText().clear();
        }if(!IsNullUtils.isNull(tvStageNumber)) {
            tvStageNumber.getText().clear();
        }if(!IsNullUtils.isNull(eachShouldAlso)) {
            eachShouldAlso.getText().clear();
        }if(!IsNullUtils.isNull(oneTimeInterest)) {
            oneTimeInterest.getText().clear();
        }if(!IsNullUtils.isNull(etSalespersonName)) {
            etSalespersonName.getText().clear();
        }if(!IsNullUtils.isNull(etSalespersonPhone)) {
            etSalespersonPhone.getText().clear();
        }
    }


}
