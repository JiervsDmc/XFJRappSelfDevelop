package com.huaxia.finance.consumer.activity.enterprise;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.util.CityDataUtil;
import com.huaxia.finance.consumer.util.DialogSelectAddress;
import com.huaxia.finance.consumer.util.ListViewDialog;
import com.lidroid.xutils.view.annotation.ViewInject;

public class EnterpriseWorkInfoActivity extends BaseActivity implements View.OnClickListener, DialogSelectAddress.OnSelectCityDialog, ListViewDialog.OnSelectItem {

    @ViewInject(R.id.select_enterprise_city)
    private TextView selectCity;
    @ViewInject(R.id.enterprise_workname)
    private EditText workName;//工作单位名
    @ViewInject(R.id.enterprise_address)
    private EditText address;//详细街道信息
    @ViewInject(R.id.enterprise_gudinghaoma)
    private EditText gudingNumber;
    @ViewInject(R.id.enterprise_quhao)
    private EditText quhaoNumber;
    @ViewInject(R.id.enterprise_month_income)
    private EditText monthIncome;//每月收入
    @ViewInject(R.id.enterprise_education)
    private TextView education;//学历
    @ViewInject(R.id.enterprise_workInfo_submit)
    private Button submit;

    private DialogSelectAddress dialogCity;
    private ListViewDialog listViewDialog;
    private CityDataUtil cityDataUtil;

    @Override
    protected int getLayout() {
        return R.layout.activity_enterprise_work_info;
    }

    @Override
    protected String getTitleText() {
        return "工作信息";
    }

    @Override
    protected void setup() {
        super.setup();
        cityDataUtil = new CityDataUtil(this);
        listViewDialog = new ListViewDialog(this, this);
        selectCity.setOnClickListener(this);
        dialogCity = new DialogSelectAddress(this, this);
        education.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.select_enterprise_city:
            case R.id.image0:
                dialogCity.showDialog();
                break;
            case R.id.enterprise_education:
            case R.id.image1:
                listViewDialog.show("学历", getResources().getStringArray(R.array.education));
                break;
            case R.id.enterprise_workInfo_submit:
                finish();
                break;

        }
    }


    @Override
    public void onListViewSelect(String selected) {
        education.setText(selected);

    }

    @Override
    public void onSelectCity(String selectedCity, String cityCode, String pidCode) {
        selectCity.setText(selectedCity);
    }
}
