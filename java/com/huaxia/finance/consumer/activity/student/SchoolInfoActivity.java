package com.huaxia.finance.consumer.activity.student;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.CityDataUtil;
import com.huaxia.finance.consumer.util.DialogSelectAddress;
import com.huaxia.finance.consumer.util.DialogSelectAddress.OnSelectCityDialog;
import com.huaxia.finance.consumer.util.DialogSelectTime;
import com.huaxia.finance.consumer.util.IsNullUtils;
import com.huaxia.finance.consumer.util.ListViewDialog;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

public class SchoolInfoActivity extends BaseActivity implements View.OnClickListener, OnSelectCityDialog, DialogSelectTime.OnSelectDateDialog, ListViewDialog.OnSelectItem {

    @ViewInject(R.id.select_province_city_district)
    private TextView selectCity;
    @ViewInject(R.id.school_name)
    private EditText schoolName;
    @ViewInject(R.id.zhuanye_school)
    private EditText zhuanyeName;
    @ViewInject(R.id.school_education)
    private TextView schoolEducation;
    @ViewInject(R.id.to_school_time)
    private TextView schoolTime;
    @ViewInject(R.id.school_address)
    private EditText schoolAddress;
    @ViewInject(R.id.month_cost)
    private TextView monthCost;
    @ViewInject(R.id.part_time)
    private TextView partTime;
    @ViewInject(R.id.part_time_income)
    private TextView partTimeIncome;
    @ViewInject(R.id.schoolinfo_submit)
    private Button submit;


    private DialogSelectAddress dialogCity;
    private CityDataUtil cityDataUtil;
    private Map mapDate;


    private ListViewDialog listViewDialog;
    private int clickTag = -1;
    private String pid;
    private String cid;
    private String inTime;


    @Override
    protected int getLayout() {
        return R.layout.activity_school_info;
    }

    @Override
    protected String getTitleText() {
        return "学校信息";
    }

    @Override
    protected void setup() {
        super.setup();
        mapDate = new HashMap();
        cityDataUtil = new CityDataUtil(this);
        listViewDialog = new ListViewDialog(this, this);
        selectCity.setOnClickListener(this);
        schoolTime.setOnClickListener(this);
        schoolEducation.setOnClickListener(this);
        monthCost.setOnClickListener(this);
        partTime.setOnClickListener(this);
        partTimeIncome.setOnClickListener(this);
        dialogCity = new DialogSelectAddress(this, this);
        submit.setOnClickListener(this);
        getDate();
    }

    public void getDate() {
        final Map map = new HashMap();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        ApiCaller.call(this, Constant.URL, "0021", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
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
                    if (body.get("userSchool").equals("") || body.get("userSchool") == null) {
                        return;
                    }
                    mapDate = (Map) body.get("userSchool");
                    setDate(mapDate);
                }
            }
        });

    }

    public void setDate(Map date) {
        schoolName.setText("" + date.get("schoolName"));
        zhuanyeName.setText("" + date.get("discipline"));
        switch ((String) date.get("educationalBackground")) {
            case "1":
                schoolEducation.setText("大专以下");
                break;
            case "2":
                schoolEducation.setText("大专");
                break;
            case "3":
                schoolEducation.setText("本科");
                break;
            case "4":
                schoolEducation.setText("研究生及以上");
                break;

        }
        try {
            Date dd = new SimpleDateFormat("yyyy-MM-dd").parse("" + date.get("admissionAt"));
            String s = new SimpleDateFormat("yyyy年MM月dd日").format(dd);
            schoolTime.setText(s.substring(0, 8));
        } catch (Exception e) {

        }
        cid = "" + date.get("cityId");
        pid = "" + date.get("provinceId");
        selectCity.setText(cityDataUtil.codeGetString("" + date.get("provinceId"), "" + date.get("cityId")));
        schoolAddress.setText("" + date.get("dormAddress"));
        monthCost.setText(getFWString("" + date.get("livingExpenses")));
        if (date.get("concurrentPost").equals("1")) {
            partTime.setText("是");
        } else if (date.get("concurrentPost").equals("2")) {
            partTime.setText("否");
        }
        partTimeIncome.setText(getFWString("" + date.get("concurrentPostEarnings")));
    }

    public String getFWString(String code) {
        String s = null;
        switch (code) {
            case "1":
                s = "500以下";
                break;
            case "2":
                s = "500-1000";
                break;
            case "3":
                s = "1000-1500";
                break;
            case "4":
                s = "1500-2000";
                break;
            case "5":
                s = "2000以上";
                break;
            case "":
                s = "";
                break;
        }
        return s;
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.select_province_city_district:
            case R.id.image2:
                dialogCity.showDialog();
                break;
            case R.id.to_school_time:
            case R.id.image1:
                new DialogSelectTime(SchoolInfoActivity.this, this).showTimeDialog();
                break;
            case R.id.school_education:
            case R.id.image0:
                clickTag = 0;
                listViewDialog.show("在读学历", getResources().getStringArray(R.array.education_student));
                break;
            case R.id.month_cost:
            case R.id.image3:
                clickTag = 1;
                listViewDialog.show("每月生活费", getResources().getStringArray(R.array.income_level));
                break;
            case R.id.part_time:
            case R.id.image4:
                clickTag = 2;
                listViewDialog.show("是否兼职", getResources().getStringArray(R.array.part_time));
                break;
            case R.id.part_time_income:
            case R.id.image5:
                clickTag = 3;
                if (partTime.getText().equals("是")) {
                    listViewDialog.show("兼职收入", getResources().getStringArray(R.array.income_level));
                } else {
                    toast("请确认是否兼职");
                }
                break;
            case R.id.schoolinfo_submit:
                checkDate();
                break;
        }
    }

    @Override
    public void OnSelectDate(String selectDate) {
        schoolTime.setText(selectDate);
    }

    @Override
    public void onListViewSelect(String selected) {
        switch (clickTag) {
            case 0:
                schoolEducation.setText(selected);
                break;
            case 1:
                monthCost.setText(selected);
                break;
            case 2:
                partTime.setText(selected);
                if (selected.equals("否")) {
                    partTimeIncome.setText("");
                }
                break;
            case 3:
                partTimeIncome.setText(selected);
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
        if (IsNullUtils.isNull(schoolName.getText().toString())) {
            toast("请填写就读学校");
            return;
        }
        if (IsNullUtils.isNull(zhuanyeName.getText().toString())) {
            toast("请填写就读专业");
            return;
        }
        if (IsNullUtils.isNull(schoolTime.getText().toString())) {
            toast("请填写入学时间");
            return;
        }
        if (IsNullUtils.isNull(selectCity.getText().toString()) || IsNullUtils.isNull(schoolAddress.getText().toString())) {
            toast("请输入宿舍的详细区县街道地址");
            return;
        }
        if (IsNullUtils.isNull(schoolEducation.getText().toString())) {
            toast("请选择在读学历");
            return;
        }
        if (IsNullUtils.isNull(monthCost.getText().toString())) {
            toast("请填写每月生活费");
            return;
        }
        if (IsNullUtils.isNull(partTime.getText().toString())) {
            toast("请选择是否兼职");
            return;
        }
        if (partTime.getText().equals("是")) {
            if (IsNullUtils.isNull(partTimeIncome.getText().toString())) {
                toast("请选择兼职收入");
                return;
            }
        }
        try {
            Date date = new SimpleDateFormat("yyyy年MM月dd日").parse(schoolTime.getText().toString() + "1日");
            inTime = new SimpleDateFormat("yyyy-MM-dd").format(date);
        } catch (Exception e) {

        }
        dataUpload();
    }

    public String getFWCode(String money) {
        String s = null;
        switch (money) {
            case "500以下":
                s = "1";
                break;
            case "500-1000":
                s = "2";
                break;
            case "1000-1500":
                s = "3";
                break;
            case "1500-2000":
                s = "4";
                break;
            case "2000以上":
                s = "5";
                break;
        }
        return s;
    }

    public void dataUpload() {
        Map map = new HashMap();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        switch (schoolEducation.getText().toString()) {
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
        map.put("provinceId", pid);
        map.put("cityId", cid);
        map.put("schoolName", schoolName.getText().toString());
        map.put("discipline", zhuanyeName.getText().toString());
        map.put("admissionAt", inTime);
        map.put("dormAddress", schoolAddress.getText().toString());
        map.put("livingExpenses", getFWCode(monthCost.getText().toString()));
        if (partTime.getText().toString().equals("否")) {
            map.put("concurrentPost", 2);
        } else {
            map.put("concurrentPost", 1);
        }
        map.put("concurrentPostEarnings", getFWCode(partTimeIncome.getText().toString()));
        ApiCaller.call(this, Constant.URL, "0011", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
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
}
