package com.huaxia.finance.consumer.activity.worker;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.CityDataUtil;
import com.huaxia.finance.consumer.util.DialogSelectAddress;
import com.huaxia.finance.consumer.util.DialogSelectTime;
import com.huaxia.finance.consumer.util.IsNullUtils;
import com.huaxia.finance.consumer.util.ListViewDialog;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class WorkingInfoActivity extends BaseActivity implements View.OnClickListener, DialogSelectAddress.OnSelectCityDialog, DialogSelectTime.OnSelectDateDialog, ListViewDialog.OnSelectItem, View.OnFocusChangeListener {

    @ViewInject(R.id.select_work_city)
    private TextView selectCity;
    @ViewInject(R.id.worker_workname)
    private EditText workName;//工作单位名
    @ViewInject(R.id.worker_address)
    private EditText address;//详细街道信息
    @ViewInject(R.id.worker_gudinghaoma)
    private EditText gudingNumber;
    @ViewInject(R.id.worker_quhao)
    private EditText quhaoNumber;
    @ViewInject(R.id.worker_induction_time)
    private TextView inductionTime;//入职时间
    @ViewInject(R.id.worker_danweixinzhi)
    private TextView danweiXZ;//单位性质
    @ViewInject(R.id.worker_month_income)
    private EditText monthIncome;//每月收入
    @ViewInject(R.id.worker_education)
    private TextView education;//学历
    @ViewInject(R.id.worker_workInfo_submit)
    private Button submit;
    @ViewInject(R.id.ll_tocompany)
    private LinearLayout llTocompany;
    @ViewInject(R.id.view)
    private View view;

    private DialogSelectAddress dialogCity;
    private ListViewDialog listViewDialog;
    private CityDataUtil cityDataUtil;
    private int clickTag = -1;

    private String pid;
    private String cid;
    private String inTime;
    private String role;

    @Override
    protected int getLayout() {
        return R.layout.activity_working_info;
    }

    @Override
    protected void setup() {
        super.setup();
        cityDataUtil = new CityDataUtil(this);
        listViewDialog = new ListViewDialog(this, this);
        selectCity.setOnClickListener(this);
        dialogCity = new DialogSelectAddress(this, this);
        inductionTime.setOnClickListener(this);
        danweiXZ.setOnClickListener(this);
        education.setOnClickListener(this);
        submit.setOnClickListener(this);
        role=getIntent().getStringExtra("role");
        if(role.equals("01")) {
            view.setVisibility(View.GONE);
            llTocompany.setVisibility(View.GONE);
        }else if(role.equals("02")) {
            view.setVisibility(View.VISIBLE);
            llTocompany.setVisibility(View.VISIBLE);
        }
        getDate();
        monthIncome.addTextChangedListener(new TextWatcher() {
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
                    monthIncome.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
                    monthIncome.setSelection(monthIncome.getText().toString().toCharArray().length);
                    monthIncome.setText(editable);

                    }
            }
        });
        monthIncome.setOnFocusChangeListener(this);

    }

    @Override
    protected String getTitleText() {
        return "工作信息";
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.select_work_city:
            case R.id.image0:
                dialogCity.showDialog();
                break;
            case R.id.worker_induction_time:
            case R.id.image1:
                //入职时间
                new DialogSelectTime(WorkingInfoActivity.this, this).showTimeDialog();
                break;
            case R.id.worker_danweixinzhi:
            case R.id.image2:
                clickTag = 0;
                listViewDialog.show("单位性质", getResources().getStringArray(R.array.unit_of_nature));
                break;
            case R.id.worker_education:
            case R.id.image3:
                clickTag = 1;
                listViewDialog.show("学历", getResources().getStringArray(R.array.education));
                break;
            case R.id.worker_workInfo_submit:
                checkDate();
                break;
        }
    }
    public void getDate(){
        Map map = new HashMap();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        ApiCaller.call(this, Constant.URL, "0029", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                toast("数据查看失败");
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                if (body.size() == 0) {
                    return;
                }
                if(body.get("userCompany")==null||body.get("userCompany").equals("")){
                    return;
                }
                Map dateF =(Map) body.get("userCompany");
                setDateView(dateF);
            }
        });
    }
    public void setDateView(Map date){
        workName.setText(""+date.get("name"));
        cid = "" + date.get("cityId");
        pid = "" + date.get("provinceId");
        selectCity.setText(cityDataUtil.codeGetString("" + date.get("provinceId"), "" + date.get("cityId")));
        address.setText(""+date.get("address"));
        quhaoNumber.setText((""+date.get("telephone")).substring(0,date.get("telephone").toString().lastIndexOf("-")));
        gudingNumber.setText((""+date.get("telephone")).substring(date.get("telephone").toString().lastIndexOf("-")+1));
        try {
            Date dd = new SimpleDateFormat("yyyy-MM-dd").parse("" + date.get("entryTime"));
            String s = new SimpleDateFormat("yyyy年MM月dd日").format(dd);
            inductionTime.setText(s.substring(0, 8));
        } catch (Exception e) {

        }
        switch (""+date.get("type")){
            case "00":
                danweiXZ.setText("政府机关");
                break;
            case "10":
                danweiXZ.setText("事业单位");
                break;
            case "20":
                danweiXZ.setText("国企");
                break;
            case "30":
                danweiXZ.setText("外企");
                break;
            case "40":
                danweiXZ.setText("合资");
                break;
            case "50":
                danweiXZ.setText("民营");
                break;
            case "60":
                danweiXZ.setText("私企");
                break;
            case "70":
                danweiXZ.setText("个体");
                break;
        }
       // monthIncome.setText(""+date.get("monthlySalary")+".00");
        monthIncome.setText(String.valueOf(new BigDecimal(date.get("monthlySalary").toString()).setScale(2, BigDecimal.ROUND_HALF_UP)));
        switch ((String) date.get("education")) {
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


    @Override
    public void OnSelectDate(String selectDate) {
        inductionTime.setText(selectDate);
    }

    @Override
    public void onListViewSelect(String selected) {
        switch (clickTag) {
            case 0:
                danweiXZ.setText(selected);
                break;
            case 1:
                education.setText(selected);
                break;
        }

    }

    @Override
    public void onSelectCity(String selectedCity, String cityCode, String pidCode) {
        selectCity.setText(selectedCity);
        pid = pidCode;
        cid = cityCode;
    }

    public void checkDate() {
        if (IsNullUtils.isNull(workName.getText().toString())) {
            toast("请填写工作单位");
            return;
        }
        if (IsNullUtils.isNull(selectCity.getText().toString()) || IsNullUtils.isNull(address.getText().toString())) {
            toast("请输入单位的详细区县街道地址");
            return;
        }
        if (IsNullUtils.isNull(quhaoNumber.getText().toString()) || IsNullUtils.isNull(gudingNumber.getText().toString())) {
            toast("请填写单位电话");
            return;
        }if(gudingNumber.getText().length()<7||gudingNumber.getText().length()>9||quhaoNumber.getText().length()<3||quhaoNumber.getText().length()>4) {
            toast("固定电话号码输入错误");
            return;
        }
        if (role.equals("02")&&IsNullUtils.isNull(inductionTime.getText().toString())) {
            toast("请填写入职时间");
            return;
        }
        if (IsNullUtils.isNull(danweiXZ.getText().toString())) {
            toast("请填写单位性质");
            return;
        }
        if (IsNullUtils.isNull(monthIncome.getText().toString())) {
            toast("请填写每月收入");
            return;
        }
        if (IsNullUtils.isNull(education.getText().toString())) {
            toast("请填写学历");
            return;
        }
        try {
            Date date = new SimpleDateFormat("yyyy年MM月dd日").parse(inductionTime.getText().toString() + "1日");
            inTime = new SimpleDateFormat("yyyy-MM-dd").format(date);
        } catch (Exception e) {

        }
        dataUpload();
    }

    public void dataUpload() {
        Map map = new HashMap();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        switch (education.getText().toString()) {
            case "大专":
                map.put("education", String.valueOf(2));
                break;
            case "本科":
                map.put("education", String.valueOf(3));
                break;
            case "研究生及以上":
                map.put("education", String.valueOf(4));
                break;
            case "大专以下":
                map.put("education", String.valueOf(1));
                break;
        }
        map.put("provinceId", pid);
        map.put("cityId", cid);
        map.put("name", workName.getText().toString());
        map.put("telephone", quhaoNumber.getText().toString() + "-"+gudingNumber.getText().toString());
        map.put("entryTime", inTime);
        map.put("address", address.getText().toString());
        map.put("monthlySalary", new BigDecimal(monthIncome.getText().toString()).setScale(0,BigDecimal.ROUND_DOWN) );
        switch (danweiXZ.getText().toString()) {
            case "政府机关":
                map.put("type", "00");
                break;
            case "事业单位":
                map.put("type", "10");
                break;
            case "国企":
                map.put("type", "20");
                break;
            case "外企":
                map.put("type", "30");
                break;
            case "合资":
                map.put("type", "40");
                break;
            case "民营":
                map.put("type", "50");
                break;
            case "私企":
                map.put("type", "60");
                break;
            case "个体":
                map.put("type", "70");
                break;
        }

        ApiCaller.call(this, Constant.URL, "0028", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                toast("数据提交失败");
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                if (head.getResponseCode().equals("0000")) {
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
            case R.id.worker_month_income:
                if(!hasFocus&&monthIncome.getText().toString().length()>0) {
                    monthIncome.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                    monthIncome.setText(String.valueOf(new BigDecimal(monthIncome.getText().toString()).setScale(2, BigDecimal.ROUND_HALF_UP)));
                }else {
                    if(monthIncome.getText().toString().length()>0) {
                        if(monthIncome.getText().toString().contains(".00")) {
                            monthIncome.setText(String.valueOf(new BigDecimal(monthIncome.getText().toString()).setScale(2, BigDecimal.ROUND_HALF_UP)).substring(0,monthIncome.getText().toString().lastIndexOf(".")));
                        }

                    }
                }
                break;
        }
    }
}
