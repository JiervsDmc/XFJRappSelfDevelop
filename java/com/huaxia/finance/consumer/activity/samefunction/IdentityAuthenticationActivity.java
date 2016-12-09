package com.huaxia.finance.consumer.activity.samefunction;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.activity.ModifyPhoneActivity;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.CityDataUtil;
import com.huaxia.finance.consumer.util.DialogSelectAddress;
import com.huaxia.finance.consumer.util.IsNullUtils;
import com.huaxia.finance.consumer.util.ListViewDialog;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * 身份认证
 */

public class IdentityAuthenticationActivity extends BaseActivity implements View.OnClickListener, DialogSelectAddress.OnSelectCityDialog, ListViewDialog.OnSelectItem {

    @ViewInject(R.id.select_changzhu_address)
    private TextView selectAddress;
    @ViewInject(R.id.marital_status)
    private TextView maritalStatus;//婚姻状况
    @ViewInject(R.id.user_CZ_address)
    private EditText CZAddress;//常住地址
    @ViewInject(R.id.identity_phone_number)
    private TextView phoneNumber;//手机号码
    @ViewInject(R.id.identity_update_phone)
    private TextView updatePhone;
    @ViewInject(R.id.identity_btn)
    private Button nextBtn;

    private String pid;
    private String cid;

    private DialogSelectAddress dialogCity;
    private String role;
    private CityDataUtil cityDataUtil;
    private Map mapDate;

    @Override
    protected int getLayout() {
        return R.layout.activity_identity_authentication;
    }

    @Override
    protected String getTitleText() {
        return "身份认证";
    }

    @Override
    protected void setup() {
        super.setup();
        mapDate = new HashMap();
        role = getIntent().getStringExtra("role");
        cityDataUtil = new CityDataUtil(this);
        phoneNumber.setText(mgr.getVal(UniqueKey.APP_MOBILE));
        selectAddress.setOnClickListener(this);
        dialogCity = new DialogSelectAddress(this, this);
        nextBtn.setOnClickListener(this);
        updatePhone.setOnClickListener(this);
        maritalStatus.setOnClickListener(this);
        getDate();
    }

    /**
     * 从服务器获取上次填写过的数据
     */
    public void getDate() {
        Map map = new HashMap();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        ApiCaller.call(this, Constant.URL, "0020", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
               // toast("数据查看失败");
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                if (body.size() == 0) {
                    return;
                }
                if (body.get("userDetail").equals("") || body.get("userDetail") == null) {
                    return;
                }
                mapDate = (Map) body.get("userDetail");
                setDate(mapDate);
            }
        });

    }

    public void setDate(Map date) {
        switch ((String) date.get("maritalStatus")) {
            case "10":
                maritalStatus.setText("已婚");
                break;
            case "20":
                maritalStatus.setText("未婚");
                break;
            case "21":
                maritalStatus.setText("离异");
                break;
            case "25":
                maritalStatus.setText("丧偶");
                break;
            case "30":
                maritalStatus.setText("再婚");
                break;
        }
        //todo:接口调试注意更改
//        role = Integer.valueOf(""+date.get("identityTag"));
        pid = "" + date.get("provinceId");
        cid = "" + date.get("cityId");
        selectAddress.setText(cityDataUtil.codeGetString("" + date.get("provinceId"), "" + date.get("cityId")));
        CZAddress.setText("" + date.get("commonAddress"));
        if (date.get("commonCellphone").equals("")) {
            phoneNumber.setText(mgr.getVal(UniqueKey.APP_MOBILE));
        } else {
            phoneNumber.setText("" + date.get("commonCellphone"));
        }

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        Intent intent;
        switch (view.getId()) {
            case R.id.select_changzhu_address:
            case R.id.image1:
                dialogCity.showDialog();
                break;
            case R.id.marital_status:
            case R.id.image0:
                //婚姻状况
                new ListViewDialog(IdentityAuthenticationActivity.this, this).show("婚姻状况", getResources().getStringArray(R.array.marital_status));
                break;
            case R.id.identity_update_phone:
                //修改手机号码
                intent = new Intent(IdentityAuthenticationActivity.this, ModifyPhoneActivity.class);
                startActivity(intent);
                break;
            case R.id.identity_btn:
                checkDate();
                break;
        }
    }

    @Override
    public void onListViewSelect(String selected) {


        maritalStatus.setText(selected);
    }

    @Override
    public void onSelectCity(String selectedCity, String cityCode, String pidCode) {
        selectAddress.setText(selectedCity);
        pid = pidCode;
        cid = cityCode;
    }

    public void checkDate() {
        if (IsNullUtils.isNull(maritalStatus.getText().toString())) {
            toast("请填写婚姻状况");
            return;
        }
        if (IsNullUtils.isNull(selectAddress.getText().toString()) || IsNullUtils.isNull(CZAddress.getText().toString())) {
            toast("请输入住址的详细区县街道地址");
            return;
        }
        dataUpload();
    }

    public void dataUpload() {
        Map map = new HashMap();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        switch (maritalStatus.getText().toString()) {
            case "已婚":
                map.put("maritalStatus", String.valueOf(10));
                break;
            case "未婚":
                map.put("maritalStatus", String.valueOf(20));
                break;
            case "离异":
                map.put("maritalStatus", String.valueOf(21));
                break;
            case "丧偶":
                map.put("maritalStatus", String.valueOf(25));
                break;
            case "再婚":
                map.put("maritalStatus", String.valueOf(30));
                break;
        }
        map.put("provinceId", pid);
        map.put("cityId", cid);
        map.put("commonCellphone", mgr.getVal(UniqueKey.APP_MOBILE));
        map.put("identityTag",role);
        map.put("commonAddress", CZAddress.getText().toString());

        ApiCaller.call(this, Constant.URL, "0010", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                //toast("数据提交失败");
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
