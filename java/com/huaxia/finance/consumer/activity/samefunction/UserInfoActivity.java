package com.huaxia.finance.consumer.activity.samefunction;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextPaint;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.activity.RoleChangeActivity;
import com.huaxia.finance.consumer.activity.freelance.FreelanceFamilyInfoActivity;
import com.huaxia.finance.consumer.activity.order.PreOrder1Activity;
import com.huaxia.finance.consumer.activity.order.PreOrderActivity;
import com.huaxia.finance.consumer.activity.student.SchoolInfoActivity;
import com.huaxia.finance.consumer.activity.student.StudentAttachmentUploadActivity;
import com.huaxia.finance.consumer.activity.worker.WorkingInfoActivity;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.DialogUtil;
import com.huaxia.finance.consumer.util.IsNullUtils;
import com.huaxia.finance.consumer.util.LogUtil;
import com.huaxia.finance.consumer.util.ToastUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class UserInfoActivity extends BaseActivity implements View.OnClickListener {
    //企业主：01，工作族,02，学生党,03，自由职业者：04
    @ViewInject(R.id.identity_RL)
    private LinearLayout identityRL;
    @ViewInject(R.id.roleInfo_RL)
    private LinearLayout roleInfoRL;
    @ViewInject(R.id.bankCardInfo_RL)
    private LinearLayout bankCardInfoRL;
    @ViewInject(R.id.contactInfo_RL)
    private LinearLayout contactInfoRL;
    @ViewInject(R.id.certificationInfo_RL)
    private LinearLayout certificationInfoRL;
    @ViewInject(R.id.internetInfo_RL)
    private LinearLayout internetInfoRL;
    @ViewInject(R.id.info_next_btn)
    private Button nextBtn;
    @ViewInject(R.id.info_next_btn)
    private Button btn;
    @ViewInject(R.id.user_text1)
    private TextView tv1;
    @ViewInject(R.id.user_text2)
    private TextView tv2;
    @ViewInject(R.id.user_text3)
    private TextView tv3;
    @ViewInject(R.id.user_text4)
    private TextView tv4;
    @ViewInject(R.id.user_text5)
    private TextView tv5;
    @ViewInject(R.id.user_text6)
    private TextView tv6;
    @ViewInject(R.id.roleInfo_TV)
    private TextView roleInfoTV;

    @ViewInject(R.id.text_1)
    private TextView text1;
    @ViewInject(R.id.text_2)
    private TextView text2;

    private String role;
    private Map mapDate;
    private String lawFlag;
    private String bankFlag;
    private ContactInfoData contactInfoData;

    private List<Map<String, String>> allContact = new ArrayList<>();

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0xfff0) {
                //获取所有联系人操作完成
                allContact.addAll(contactInfoData.getList());
                LogUtil.getLogutil().i("获取所有联系人："+ContactInfoData.list);
            } else if (msg.what == 0xfff1) {
                //如果没有数据
                DialogUtil.inform(UserInfoActivity.this, "读取联系人信息失败", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onBackPressed();
                    }
                });
//                DialogUtil.inform(UserInfoActivity.this, "读取联系人被拒绝，现在去设置?", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                        Uri uri = Uri.fromParts("package", "com.huaxia.finance.consumer", null);
//                        intent.setData(uri);
//                        startActivity(intent);
//                    }
//                }, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        onBackPressed();
//                    }
//                });
            }
        }
    };

    @Override
    protected int getLayout() {
        return R.layout.activity_user_info;
    }

    @Override
    protected String getTitleText() {
        return "我的资料";
    }

    @Override
    protected String getBtnRightText() {
        return "[切换]";
    }

    @Override
    protected boolean showBtnRight() {
        return true;
    }

    @Override
    protected void onBtnRightClick(View view) {
        Intent intent = new Intent(UserInfoActivity.this, RoleChangeActivity.class);
        startActivity(intent);
    }


    @Override
    protected void setup() {
        super.setup();
        mapDate = new HashMap();
        role = getIntent().getStringExtra("role");
        TextPaint tp1 = text1.getPaint();
        tp1.setFakeBoldText(true);
        TextPaint tp2 = text2.getPaint();
        tp2.setFakeBoldText(true);
        identityRL.setOnClickListener(this);
        roleInfoRL.setOnClickListener(this);
        bankCardInfoRL.setOnClickListener(this);
        contactInfoRL.setOnClickListener(this);
        certificationInfoRL.setOnClickListener(this);
        internetInfoRL.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        btn.setOnClickListener(this);
        contactInfoData = new ContactInfoData(this, handler);

    }

    public void setDView(String role) {
        LogUtil.getLogutil().d("role值" + role);
        switch (role) {
            case "03":
                setTitle("学生党");
                roleInfoTV.setText("学校信息");
                break;
            case "02":
                setTitle("上班族");
                roleInfoTV.setText("工作信息");
                break;
            case "01":
                setTitle("企业主");
                roleInfoTV.setText("工作信息");
                break;
            case "04":
                setTitle("自由职业者");
                roleInfoTV.setText("家庭信息");
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        tv1.setTextColor(getResources().getColor(R.color.text_contact_color));
        tv2.setTextColor(getResources().getColor(R.color.text_contact_color));
        tv3.setTextColor(getResources().getColor(R.color.text_contact_color));
        tv4.setTextColor(getResources().getColor(R.color.text_contact_color));
        tv5.setTextColor(getResources().getColor(R.color.text_contact_color));
        tv6.setTextColor(getResources().getColor(R.color.text_contact_color));
        getDate();
    }

    //获取用户信息
    public void getDate() {
        Map map = new HashMap();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        ApiCaller.call(this, Constant.URL, "0024", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                //toast("数据查看失败");
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                if (head.getResponseCode().toString().contains("0000")) {
                    if (body.size() == 0) {
                        return;
                    }
                    if (body.get("userIds").equals("") || body.get("userIds") == null) {
                        return;
                    }
                    mapDate = (Map) body.get("userIds");
                    setViewDate(mapDate);
                } else {
                    ToastUtils.showSafeToast(UserInfoActivity.this, head.getResponseMsg());
                }

            }
        });
        LogUtil.getLogutil().d("地址" + Constant.URL);
    }

    public String getRightText(String txt) {
        if (txt.equals("1")) {
            return "去查看";
        } else {
            return "去填写";
        }
    }

    public void setViewDate(Map date) {
        if (!date.get("identityFlag").equals("null") && !IsNullUtils.isNull(date.get("identityFlag"))) {
            role = "" + date.get("identityFlag");
        }
        lawFlag = "" + date.get("lawFlag");
        bankFlag = "" + date.get("bankFlag");
        setDView(role);
        //身份认证
        tv1.setText(getRightText("" + date.get("detailFlag")));
        if (tv1.getText().equals("去查看")) {
            tv1.setTextColor(getResources().getColor(R.color.usetinfo_text));
        }
        //银行卡
        if (bankFlag.equals("1") && lawFlag.equals("1")) {
            tv3.setText("去查看");
            tv3.setTextColor(getResources().getColor(R.color.usetinfo_text));
        } else {
            tv3.setText("去填写");
        }
        //联系人
        tv4.setText(getRightText("" + date.get("contractFlag")));
        if (tv4.getText().equals("去查看")) {
            tv4.setTextColor(getResources().getColor(R.color.usetinfo_text));
        }
        //互联网
        tv6.setText(getRightText("" + date.get("internetFlag")));
        if (tv6.getText().equals("去查看")) {
            tv6.setTextColor(getResources().getColor(R.color.usetinfo_text));
        }
        //认证信息
        tv5.setText(getRightText("" + date.get("authFlag")));
        if (tv5.getText().equals("去查看")) {
            tv5.setTextColor(getResources().getColor(R.color.usetinfo_text));
        }
        //工作，学生，自由职业
        switch (role) {
            case "03":
                roleInfoTV.setText("学校信息");
                tv2.setText(getRightText("" + date.get("schoolFlag")));
                if (tv2.getText().equals("去查看")) {
                    tv2.setTextColor(getResources().getColor(R.color.usetinfo_text));
                }
                break;
            case "02":
                roleInfoTV.setText("工作信息");
                tv2.setText(getRightText("" + date.get("companyFlag")));
                if (tv2.getText().equals("去查看")) {
                    tv2.setTextColor(getResources().getColor(R.color.usetinfo_text));
                }
                break;
            case "01":
                roleInfoTV.setText("工作信息");
                tv2.setText(getRightText("" + date.get("companyFlag")));
                if (tv2.getText().equals("去查看")) {
                    tv2.setTextColor(getResources().getColor(R.color.usetinfo_text));
                }
                break;
            case "04":
                roleInfoTV.setText("家庭信息");
                tv2.setText(getRightText("" + date.get("freeFlag")));
                if (tv2.getText().equals("去查看")) {
                    tv2.setTextColor(getResources().getColor(R.color.usetinfo_text));
                }
                break;
        }
        //1：已拉取  0：去拉取
        LogUtil.getLogutil().d("值"+ContactInfoData.list);
        if ("0".equals(date.get("pullFlag"))&&IsNullUtils.isNull(ContactInfoData.list)) {
            contactInfoData.getContactInfo();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        Intent intent = new Intent();
        switch (view.getId()) {
            case R.id.identity_RL:
                intent.setClass(UserInfoActivity.this, IdentityAuthenticationActivity.class);
                intent.putExtra("role", role);
                startActivity(intent);
                break;
            case R.id.roleInfo_RL:
                switch (role) {
                    case "03":
                        intent.setClass(UserInfoActivity.this, SchoolInfoActivity.class);
                        break;
                    case "01":
                    case "02":
                        intent.setClass(UserInfoActivity.this, WorkingInfoActivity.class);
                        intent.putExtra("role", role);
                        break;
                    case "04":
                        intent.setClass(UserInfoActivity.this, FreelanceFamilyInfoActivity.class);
                        break;
                }
                startActivity(intent);
                break;
            case R.id.bankCardInfo_RL:
                if (!IsNullUtils.isNull(bankFlag)) {
                    if (bankFlag.equals("1")) {
                        intent.setClass(UserInfoActivity.this, BankCardInfoSecondActivity.class);
                    } else if (bankFlag.equals("0")) {
                        intent.setClass(UserInfoActivity.this, BankCardBfInfoActivity.class);
                    }
                } else {
                    intent.setClass(UserInfoActivity.this, BankCardBfInfoActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.contactInfo_RL:
                intent.setClass(UserInfoActivity.this, ContactInfoActivity.class);
                intent.putExtra("role", role);
                startActivity(intent);
                break;
            case R.id.certificationInfo_RL:
                if (tv1.getText().equals("去填写")) {
                    toast("请先完善身份认证信息");
                    return;
                }
                getRZDate();
                break;
            case R.id.internetInfo_RL:
                intent.setClass(UserInfoActivity.this, InternetInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.info_next_btn:
                toNext();
                break;
        }
    }

    public void toNext() {
        if (tv1.getText().equals("去填写")) {
            toast("请填写身份认证信息");
            return;
        }
        if (tv2.getText().equals("去填写")) {
            switch (role) {
                case "03":
                    toast("请填写学校信息");
                    break;
                case "02":
                case "01":
                    toast("请填写工作信息");
                    break;
                case "04":
                    toast("请填写家庭信息");
                    break;
            }
            return;
        }
        if (tv3.getText().equals("去填写")) {
            toast("请填写银行卡信息");
            return;
        }

        if (lawFlag.equals("0")) {
            toast("请完善银行卡信息");
            return;
        }

        if (tv4.getText().equals("去填写")) {
            toast("请填写联系人信息");
            return;
        }
        if (tv5.getText().equals("去填写")) {
            toast("请填写认证信息");
            return;
        }

        // TODO: 2016/9/12 消费金融一期
//        Intent intent = new Intent(UserInfoActivity.this, PreOrderActivity.class);
        // TODO: 2016/9/12 消费金融二期
        Intent intent = new Intent(UserInfoActivity.this, PreOrder1Activity.class);
        intent.putExtra("role", role);
        startActivity(intent);

    }

    public void getRZDate() {
        Map map = new HashMap();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        ApiCaller.call(this, Constant.URL, "0020", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                //toast("数据查看失败");
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
                Map RZDate = (Map) body.get("userDetail");
                if (!IsNullUtils.isNull(RZDate.get("commonCellphone"))) {
                    if (tv5.getText().toString().equals("去查看")) {
                        //直接跳转到已经认证的页面
                        Intent intent = new Intent(UserInfoActivity.this, CertifiedInfoActivity.class);
                        intent.putExtra("Mobil", "" + RZDate.get("commonCellphone"));
                        startActivity(intent);
                    } else if (tv5.getText().toString().equals("去填写")) {
                        //跳转得到填写认证资料页面
                        Intent intent = new Intent(UserInfoActivity.this, CertificationInfoActivity.class);
                        intent.putExtra("Mobil", "" + RZDate.get("commonCellphone"));
                        startActivity(intent);
                    }

                } else {
                    toast("请先完成身份认证信息填写");
                }
            }
        });

    }
}
