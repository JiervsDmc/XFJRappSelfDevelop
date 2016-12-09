package com.huaxia.finance.consumer.activity.freelance;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.IsNullUtils;
import com.huaxia.finance.consumer.util.ListViewDialog;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class FreelanceFamilyInfoActivity extends BaseActivity implements View.OnClickListener, ListViewDialog.OnSelectItem, View.OnFocusChangeListener {
    @ViewInject(R.id.freelance_house_info)
    private EditText houseInfo;
    @ViewInject(R.id.freelance_income)
    private EditText income;
    @ViewInject(R.id.freelance_education)
    private TextView education;
    @ViewInject(R.id.enterprise_workInfo_submit)
    private TextView submit;

    private ListViewDialog listViewDialog;
    private int clickTag = -1;
    private Map mapDate;

    @Override
    protected int getLayout() {
        return R.layout.activity_freelance_family_info;
    }

    @Override
    protected String getTitleText() {
        return "家庭信息";
    }

    @Override
    protected void setup() {
        super.setup();
        listViewDialog = new ListViewDialog(this, this);
        houseInfo.setKeyListener(null);
        houseInfo.setOnClickListener(this);
        education.setOnClickListener(this);
        submit.setOnClickListener(this);
        income.setOnFocusChangeListener(this);
        income.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            @Override
            public void beforeTextChanged(CharSequence s, int i, int i1, int i2) {
                temp = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (temp.length() == 1 && temp.toString().equals("0")) {
                    editable.clear();
                    return;
                }
                if (temp.length() > 8) {
                    income.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
                    income.setSelection(income.getText().toString().toCharArray().length);
                    income.setText(editable);
                }
            }
        });
        getDate();
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.freelance_house_info:
            case R.id.image0:
                clickTag = 0;
                listViewDialog.show("房产信息", getResources().getStringArray(R.array.realEstate));
                break;
            case R.id.freelance_education:
            case R.id.image2:
                clickTag = 2;
                listViewDialog.show("学历", getResources().getStringArray(R.array.education));
                break;
            case R.id.enterprise_workInfo_submit:
                checkDate();
                break;
        }
    }

    @Override
    public void onListViewSelect(String selected) {
        switch (clickTag) {
            case 0:
                houseInfo.setText(selected);
                break;
            case 1:
                income.setText(selected);
                break;
            case 2:
                education.setText(selected);
                break;
        }

    }

    public void getDate() {
        Map map = new HashMap();
        mapDate = new HashMap();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        ApiCaller.call(this, Constant.URL, "0022", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                toast("数据查看失败");
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                if (head.getResponseCode().equals("0000")) {
                    if (body.size() == 0) {
                        return;
                    }
                    if (body.get("userFreelance").equals("") || body.get("userFreelance") == null) {
                        return;
                    }
                    mapDate = (Map) body.get("userFreelance");
                    setDate(mapDate);
                }
            }
        });
    }

    public void setDate(Map date) {
        switch ((String) date.get("houseProperty")) {
            case "1":
                houseInfo.setText("自有房产，无按揭");
                break;
            case "2":
                houseInfo.setText("自有房产，有按揭");
                break;
            case "3":
                houseInfo.setText("亲属房产");
                break;
            case "4":
                houseInfo.setText("其他");
                break;
        }
        income.setText(date.get("income") + ".00");
        switch ((String) date.get("educationalBackground")) {
            case "2":
                education.setText("大专");
                break;
            case "3":
                education.setText("本科");
                break;
            case "4":
                education.setText("研究生及以上");
                break;
            case "1":
                education.setText("大专以下");
                break;
        }
    }

    public void checkDate() {
        if (IsNullUtils.isNull(houseInfo.getText().toString())) {
            toast("请填写房产信息");
            return;
        }
        if (IsNullUtils.isNull(income.getText().toString())) {
            toast("请填写个人/家庭收入");
            return;
        }
        if (IsNullUtils.isNull(education.getText().toString())) {
            toast("请填写学历");
            return;
        }
        dataUpload();
    }

    public void dataUpload() {
        Map map = new HashMap();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        switch (houseInfo.getText().toString()) {
            case "自有房产，无按揭":
                map.put("houseProperty", String.valueOf(1));
                break;
            case "自有房产，有按揭":
                map.put("houseProperty", String.valueOf(2));
                break;
            case "亲属房产":
                map.put("houseProperty", String.valueOf(3));
                break;
            case "其他":
                map.put("houseProperty", String.valueOf(4));
                break;
        }

        map.put("income", new BigDecimal(income.getText().toString()).setScale(0, BigDecimal.ROUND_DOWN));
        switch (education.getText().toString()) {
            case "大专":
                map.put("educationalBackground", String.valueOf(2));
                break;
            case "本科":
                map.put("educationalBackground", String.valueOf(3));
                break;
            case "研究生及以上":
                map.put("educationalBackground", String.valueOf(4));
                break;
            case "大专以下":
                map.put("educationalBackground", String.valueOf(1));
                break;
        }
        ApiCaller.call(this, Constant.URL, "0012", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                toast("数据提交失败");
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                if(head.getResponseCode().equals("0000")) {
                    finish();
                }else{
                    toast(head.getResponseMsg());
                }
            }
        });
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        switch (view.getId()){
            case R.id.freelance_income:
                if(!hasFocus&&income.getText().toString().length()>0) {
                    income.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                    income.setText(String.valueOf(new BigDecimal(income.getText().toString()).setScale(2, BigDecimal.ROUND_HALF_UP)));
                }else if(hasFocus){
                    if(income.getText().toString().length()>0) {
                        if(income.getText().toString().contains(".00")) {
                            income.setText(String.valueOf(new BigDecimal(income.getText().toString()).setScale(2, BigDecimal.ROUND_HALF_UP)).substring(0,income.getText().toString().lastIndexOf(".")));
                        }

                    }
                }
                break;
        }
    }
}
