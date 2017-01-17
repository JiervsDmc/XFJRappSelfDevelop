package com.huaxia.finance.consumer.activity.samefunction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.DialogUtil;
import com.huaxia.finance.consumer.util.IsNullUtils;
import com.huaxia.finance.consumer.util.ListViewDialog;
import com.huaxia.finance.consumer.util.LogUtil;
import com.huaxia.finance.consumer.util.RegularExpressionUtil;
import com.huaxia.finance.consumer.util.Utils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;

public class ContactInfoActivity extends BaseActivity implements View.OnClickListener, ListViewDialog.OnSelectItem {
    //父亲01，母亲02，配偶03，子女04，同学05，同事06，朋友07，兄弟姐妹08
    //身份：企业主01，上班族02，学生党03，自由职业者04
    public static final int RELATIONSHIP = 0x145;
    public static final int FAMILYPHO = 0x135;
    public static final int OTHERPHO = 0x125;
    //校验合格字符数组
   /* public static final String STANDARDNAME=
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890@&+.-, ~!%*()#";*/

    @ViewInject(R.id.add_relationship)
    private TextView addRelationship;
    @ViewInject(R.id.relationshio_name)
    private TextView relationshioName;
    @ViewInject(R.id.relationship_pho)
    private TextView relationshioPho;
    @ViewInject(R.id.add_family_contact)
    private TextView addFamily;
    @ViewInject(R.id.family_name)
    private TextView familyName;
    @ViewInject(R.id.family_pho)
    private TextView familyPho;
    @ViewInject(R.id.add_contact_other)
    private TextView addOther;
    @ViewInject(R.id.other_name)
    private TextView otherName;
    @ViewInject(R.id.other_pho)
    private TextView otherPho;

    @ViewInject(R.id.baixian)
    private TextView baixian;
    @ViewInject(R.id.baixian1)
    private TextView baixian1;
    @ViewInject(R.id.baixian2)
    private TextView baixian2;

    @ViewInject(R.id.contact_last)
    private LinearLayout contactLast;
    @ViewInject(R.id.second_LL)
    private LinearLayout contactSecond;
    @ViewInject(R.id.relationship1)
    private TextView contact1;
    @ViewInject(R.id.contact_btn)
    private Button btn;
    @ViewInject(R.id.family_relationship)
    private TextView familyRS;


    private int POINT;
    private String role;
    private AlertDialog dialog;
    private AlertDialog upadateNameDialog;
    private List oldDate;

    private Map dataMap;
    private ListViewDialog listViewDialog;
    private String familyID;
    private String tongshiID;
    private String tongxueID;
    private String frindID;

    @Override
    protected int getLayout() {
        return R.layout.activity_student_contact_info;
    }

    @Override
    protected String getTitleText() {
        return "联系人信息";
    }

    @Override
    protected void setup() {
        super.setup();
        Drawable drawable = getResources().getDrawable(R.drawable.plus_icon);
        drawable.setBounds(0, 0, 40, 40);
        Drawable drawable1 = getResources().getDrawable(R.drawable.editname);
        drawable1.setBounds(0, 0, 30, 30);
        oldDate = new ArrayList();
        listViewDialog = new ListViewDialog(this, this);
        addRelationship.setOnClickListener(this);
        addRelationship.setCompoundDrawables(drawable, null, null, null);
        addFamily.setOnClickListener(this);
        addFamily.setCompoundDrawables(drawable, null, null, null);
        addOther.setOnClickListener(this);
        addOther.setCompoundDrawables(drawable, null, null, null);
        relationshioName.setOnClickListener(this);
        relationshioName.setCompoundDrawables(null, null, drawable1, null);
        relationshioPho.setOnClickListener(this);
        familyName.setOnClickListener(this);
        familyName.setCompoundDrawables(null, null, drawable1, null);
        familyPho.setOnClickListener(this);
        otherName.setOnClickListener(this);
        otherName.setCompoundDrawables(null, null, drawable1, null);
        otherPho.setOnClickListener(this);
        btn.setOnClickListener(this);
        familyRS.setOnClickListener(this);
        familyRS.setClickable(false);
        role = getIntent().getStringExtra("role");
        switch (role) {
            case "03"://同学
                contact1.setText("同学");
                contactLast.setVisibility(View.GONE);
                break;
            case "02"://上班族
                contact1.setText("同事");
                break;
            case "01"://企业主
                contact1.setText("同事");
                break;
            case "04"://自由职业者
                contactSecond.setVisibility(View.GONE);
                break;


        }
        getDate();
    }

    private void getDate() {
        Map mapU = new HashMap();
        mapU.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        ApiCaller.call(this, Constant.URL, "0006", "appService", mapU, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        super.onResponse(response, id);
                        if (body.size() == 0) {
                            return;
                        }
                        if (body.get("contractList") == null || body.get("contractList").equals("")) {
                            return;
                        }

                        oldDate = (List) body.get("contractList");
                        if (oldDate.size() != 0) {
                            for (int i = 0; i < oldDate.size(); i++) {
                                Map map = (Map) oldDate.get(i);
                                switch ("" + map.get("type")) {
                                    case "01":
                                        familyID = "" + map.get("id");
                                        setContactView(FAMILYPHO, map.get("type").toString(), map.get("name").toString(), map.get("phoneNumber").toString(), "父亲", familyID);
                                        break;
                                    case "02":
                                        familyID = "" + map.get("id");
                                        setContactView(FAMILYPHO, map.get("type").toString(), map.get("name").toString(), map.get("phoneNumber").toString(), "母亲", familyID);
                                        break;
                                    case "03":
                                        familyID = "" + map.get("id");
                                        setContactView(FAMILYPHO, map.get("type").toString(), map.get("name").toString(), map.get("phoneNumber").toString(), "配偶", familyID);
                                        break;
                                    case "04":
                                        familyID = "" + map.get("id");
                                        setContactView(FAMILYPHO, map.get("type").toString(), map.get("name").toString(), map.get("phoneNumber").toString(), "子女", familyID);
                                        break;
                                    case "08":
                                        familyID = "" + map.get("id");
                                        setContactView(FAMILYPHO, map.get("type").toString(), map.get("name").toString(), map.get("phoneNumber").toString(), "兄弟姐妹", familyID);
                                        break;
                                    case "05":
                                        tongxueID = "" + map.get("id");
                                        setContactView(RELATIONSHIP, map.get("type").toString(), map.get("name").toString(), map.get("phoneNumber").toString(), "", tongxueID);
                                        break;
                                    case "06":
                                        tongshiID = "" + map.get("id");
                                        setContactView(RELATIONSHIP, map.get("type").toString(), map.get("name").toString(), map.get("phoneNumber").toString(), "", tongshiID);
                                        break;
                                    case "07":
                                        frindID = "" + map.get("id");
                                        setContactView(OTHERPHO, map.get("type").toString(), map.get("name").toString(), map.get("phoneNumber").toString(), "", frindID);
                                        break;
                                }
                            }
                        }
                    }
                }
        );
    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

        switch (view.getId()) {
            case R.id.add_relationship:
            case R.id.relationship_pho:
                startActivityForResult(intent, RELATIONSHIP);
                break;
            case R.id.add_family_contact:
            case R.id.family_pho:
                startActivityForResult(intent, FAMILYPHO);
                break;
            case R.id.add_contact_other:
            case R.id.other_pho:
                startActivityForResult(intent, OTHERPHO);
                break;
            case R.id.relationshio_name:
                POINT = RELATIONSHIP;
                showUpadateName(relationshioName.getText().toString());
                break;
            case R.id.family_name:
                POINT = FAMILYPHO;
                showUpadateName(familyName.getText().toString());
                break;
            case R.id.other_name:
                POINT = OTHERPHO;
                showUpadateName(otherName.getText().toString());
                break;
            case R.id.cancel_update_name:
                upadateNameDialog.dismiss();
                break;
            case R.id.sure_update_name:
                //修改名字
                switch (POINT) {
                    case RELATIONSHIP:
                        if (role.equals("03")) {//同学
                            setContactName("05", updateName.getText().toString());
                        } else if (role.equals("01") || role.equals("02")) {//同事
                            setContactName("06", updateName.getText().toString());
                        }
                        relationshioName.setText(Utils.fixName(updateName.getText().toString()));
                        break;
                    case FAMILYPHO:
                        switch (familyRS.getText().toString()) {
                            case "父亲":
                                setContactName("01", updateName.getText().toString());
                                break;
                            case "母亲":
                                setContactName("02", updateName.getText().toString());
                                break;
                            case "配偶":
                                setContactName("03", updateName.getText().toString());
                                break;
                            case "兄弟姐妹":
                                setContactName("08", updateName.getText().toString());
                                break;
                            case "子女":
                                setContactName("04", updateName.getText().toString());
                                break;
                        }
                        familyName.setText(Utils.fixName(updateName.getText().toString()));
                        break;
                    case OTHERPHO:
                        setContactName("07", updateName.getText().toString());
                        otherName.setText(Utils.fixName(updateName.getText().toString()));
                        break;
                }
                upadateNameDialog.dismiss();
                break;
            case R.id.contact_btn:
                getAllDate();
                break;
            case R.id.family_relationship:
                listViewDialog.show("亲属关系", getResources().getStringArray(R.array.relation));
                break;

        }

    }


    public void getAllDate() {
        dataMap = new HashMap();
        if (contactLast.getVisibility() == View.GONE) {
            if (IsNullUtils.isNull(relationshioName.getText().toString()) || IsNullUtils.isNull(familyName.getText().toString())) {
                Toast.makeText(this, "需补充完成联系人信息", Toast.LENGTH_SHORT).show();
                return;
            }
        } else if (contactSecond.getVisibility() == View.GONE) {
            if (IsNullUtils.isNull(otherName.getText().toString()) || IsNullUtils.isNull(familyName.getText().toString())) {
                Toast.makeText(this, "需补充完成联系人信息", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            if (IsNullUtils.isNull(relationshioName.getText().toString()) || IsNullUtils.isNull(familyName.getText().toString()) || IsNullUtils.isNull(otherName.getText().toString())) {
                Toast.makeText(this, "需补充完成联系人信息", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        dataMap.put("contractList", oldDate);
        dataMap.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        ApiCaller.call(this, Constant.URL, "0001", "appService", dataMap, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                if (head.getResponseCode().equals("0000")) {
                    ContactInfoActivity.this.finish();
                } else {
                    toast(head.getResponseMsg());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            switch (requestCode) {
                case RELATIONSHIP:
                    selectContact(resultCode, data, requestCode);
                    break;
                case FAMILYPHO:
                    selectContact(resultCode, data, requestCode);
                    break;
                case OTHERPHO:
                    selectContact(resultCode, data, requestCode);
                    break;
            }
        } catch (Exception e) {
            DialogUtil.inform(this, "读取联系人被拒绝，现在去设置?", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", "com.huaxia.finance.consumer", null);
                    intent.setData(uri);
                    startActivity(intent);
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
        }

    }


    public void selectContact(int resultCode, Intent data, int requestCode) {
        if (resultCode == RESULT_OK) {
            Uri contactData = data.getData();
            Cursor cursor = managedQuery(contactData, null, null, null, null);
           cursor.moveToFirst();
            List<String> contact = getContactPhone(cursor);
            LogUtil.getLogutil().d("contact的值" + contact);
            if (contact.size() == 0) {
                //只有联系人没有电话号码
                toast("请选择正确的联系人");
                return;
            } else if (contact.size() == 2) {
                // TODO: 2016/10/26   手机号限位
                String phoneNum = contact.get(1).toString().replaceAll("^(\\+86)", "").replaceAll("^(86)", "").replaceAll("-", "").replaceAll(" ", "").trim();
                LogUtil.getLogutil().d("手机号码"+phoneNum);
                if(!RegularExpressionUtil.isMobileNO(phoneNum)) {
                    toast("导入手机号码不正确，请校验");
                    return;
                }
                switch (requestCode) {
                    case RELATIONSHIP:
                        if (role.equals("03")) {
                            //setContactView(requestCode, "05", contact.get(0), contact.get(1), "", tongxueID);
                            setContactView(requestCode, "05", Utils.fixName(contact.get(0)), phoneNum, "", tongxueID);
                        } else if (role.equals("01") || role.equals("02")) {
                            //setContactView(requestCode, "06", contact.get(0), contact.get(1), "", tongshiID);
                            setContactView(requestCode, "06", Utils.fixName(contact.get(0)), phoneNum, "", tongshiID);
                        }
                        break;
                    case FAMILYPHO:
                        //setContactView(requestCode, "01", contact.get(0), contact.get(1), "父亲", familyID);
                        setContactView(requestCode, "01", Utils.fixName(contact.get(0)), phoneNum, "父亲", familyID);
                        break;
                    case OTHERPHO:
                        //setContactView(requestCode, "07", contact.get(0), contact.get(1), "", frindID);
                        setContactView(requestCode, "07", Utils.fixName(contact.get(0)), phoneNum, "", frindID);
                        break;
                }

            } else {
                showDialog(requestCode, contact);
            }
        }
    }

    private int i;

    public void showDialog(final int requestCode, final List<String> contact) {
        i = 0;
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_contact_phone_select, null);
        LinearLayout ll = (LinearLayout) view.findViewById(R.id.dialog_select_phone_layout);
        for (; i < contact.size(); i++) {
            View phoView = inflater.inflate(R.layout.dialog_phone_select_view, null);
            final TextView numberTV = (TextView) phoView.findViewById(R.id.contact_phonenumber);
            TextView line = (TextView) phoView.findViewById(R.id.phone_number_line);
            if (i == 0) {
                continue;
            } else if (i == contact.size() - 1) {
                line.setVisibility(View.GONE);
            }

            numberTV.setText(contact.get(i).toString());
            final String number=(String)numberTV.getText();
            numberTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO: 2016/10/26   手机号限位去空格
                    String phoneNum = number.replaceAll("^(\\+86)", "").replaceAll("^(86)", "").replaceAll("-", "").replaceAll(" ", "").trim();
                    LogUtil.getLogutil().d("手机号码"+phoneNum);
                    if(!RegularExpressionUtil.isMobileNO(phoneNum)) {
                        toast("手机号码有误");
                        return;
                    }

                    switch (requestCode) {
                        case RELATIONSHIP:
                            if (role.equals("03")) {
                                //setContactView(requestCode, "05", contact.get(0), numberTV.getText().toString(), "", tongxueID);
                                setContactView(requestCode, "05", Utils.fixName(contact.get(0)),phoneNum, "", tongxueID);
                            } else if (role.equals("02") || role.equals("01")) {
                                //setContactView(requestCode, "06", contact.get(0), numberTV.getText().toString(), "", tongshiID);
                                setContactView(requestCode, "06", Utils.fixName(contact.get(0)), phoneNum, "", tongshiID);
                            }
                            break;
                        case FAMILYPHO:
                            //setContactView(requestCode, "01", contact.get(0), numberTV.getText().toString(), "父亲", familyID);
                            setContactView(requestCode, "01", Utils.fixName(contact.get(0)), phoneNum, "父亲", familyID);
                            break;
                        case OTHERPHO:
                           // setContactView(requestCode, "07", contact.get(0),numberTV.getText().toString(), "", frindID);
                            setContactView(requestCode, "07", Utils.fixName(contact.get(0)),phoneNum, "", frindID);
                            break;
                    }
                    dialog.dismiss();
                }
            });

            ll.addView(phoView);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        dialog = builder.create();
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
    }

    private EditText updateName;

    public void showUpadateName(String name) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_update_contact_name, null);
        updateName = (EditText) view.findViewById(R.id.need_update_name);
        TextView cancel = (TextView) view.findViewById(R.id.cancel_update_name);
        TextView sure = (TextView) view.findViewById(R.id.sure_update_name);
        cancel.setOnClickListener(this);
        sure.setOnClickListener(this);
        if(name.length()>20){
            String newName=name.trim().substring(0,20);
            //String newNames=fixName(newName);
            updateName.setText(newName);
            //updateName.setText(newNames);
            updateName.setSelection(newName.length());
            //updateName.setSelection(newNames.length());
        }else {
           // String newNames=fixName(name);
            updateName.setText(name);
            updateName.setSelection(name.length());
           /* updateName.setText(newNames);
            updateName.setSelection(newNames.length());*/
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        upadateNameDialog = builder.create();
        upadateNameDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(updateName, InputMethodManager.SHOW_IMPLICIT);
            }
        });
        upadateNameDialog.setView(view, 0, 0, 0, 0);
        upadateNameDialog.show();
    }

    public void setContactName(String type, String updateName) {
        for (int i = 0; i < oldDate.size(); i++) {
            Map map = (Map) oldDate.get(i);
            if (map.get("type").equals(type)) {
                map.put("name", Utils.fixName(updateName));
                oldDate.set(i, map);
            }
        }
    }

    public void setContactDate(String type, String name, String contactName, String pho, String id) {

        if (oldDate.size() <= 0) {
            Map map = new HashMap();
            map.put("id", "");
            map.put("name",name);
            map.put("mobileContractName",contactName);
            map.put("phoneNumber", pho);
            map.put("type", type);
            oldDate.add(map);
        } else {
            int j = 0;
            for (int i = 0; i < oldDate.size(); i++) {
                Map map = (Map) oldDate.get(i);
                if (map.get("id").equals(id)) {
                    map.put("name", name);
                    map.put("mobileContractName", contactName);
                    map.put("phoneNumber", pho);
                    map.put("type", type);
                    oldDate.set(i, map);
                    break;
                } else if (map.get("type").equals(type) && IsNullUtils.isNull("" + map.get("id"))) {
                    map.put("name", name);
                    map.put("mobileContractName", contactName);
                    map.put("phoneNumber", pho);
                    map.put("type", type);
                    oldDate.set(i, map);
                } else if ((type.equals("01") || type.equals("02") || type.equals("03")) && IsNullUtils.isNull("" + map.get("id"))) {
                    if (map.get("type").equals("01") || map.get("type").equals("02") || map.get("type").equals("03")) {
                        map.put("name", name);
                        map.put("mobileContractName", contactName);
                        map.put("phoneNumber", pho);
                        map.put("type", type);
                        oldDate.set(i, map);
                    }
                } else {
                    j++;
                }
            }
            if (j == oldDate.size()) {
                Map map = new HashMap();
                map.put("id", "");
                map.put("name", name);
                map.put("mobileContractName", contactName);
                map.put("phoneNumber", pho);
                map.put("type", type);
                oldDate.add(map);
            }
        }
    }

    public void setContactView(int requestCode, String type, String name, String phone, String familyType, String id) {
        switch (requestCode) {
            case RELATIONSHIP:
                if (role.equals("03") && type.equals("05")) {//同学
                    setContactDate("05", name, name, phone, id);
                    addRelationship.setVisibility(View.GONE);
                    baixian.setVisibility(View.VISIBLE);
                    relationshioName.setVisibility(View.VISIBLE);
                    relationshioPho.setVisibility(View.VISIBLE);
                    relationshioPho.setText(phone);
                    relationshioName.setText(Utils.fixName(name));
                } else if ((role.equals("02") || role.equals("01")) && type.equals("06")) {
                    setContactDate("06", name, name, phone, id);
                    addRelationship.setVisibility(View.GONE);
                    baixian.setVisibility(View.VISIBLE);
                    relationshioName.setVisibility(View.VISIBLE);
                    relationshioPho.setVisibility(View.VISIBLE);
                    relationshioPho.setText(phone);
                    relationshioName.setText(Utils.fixName(name));
                }
                break;
            case FAMILYPHO:
                Drawable drawable = getResources().getDrawable(R.drawable.name_arrow);
                drawable.setBounds(0, 0, 35, 35);
                familyRS.setCompoundDrawables(null, null, drawable, null);
                familyRS.setClickable(true);
                familyRS.setText(familyType);
                switch (familyType) {
                    case "父亲":
                        setContactDate("01", name, name, phone, id);
                        break;
                    case "母亲":
                        setContactDate("02", name, name, phone, id);
                        break;
                    case "配偶":
                        setContactDate("03", name, name, phone, id);
                        break;
                    case "兄弟姐妹":
                        setContactDate("08", name, name, phone, id);
                        break;
                    case "子女":
                        setContactDate("04", name, name, phone, id);
                        break;
                }
                addFamily.setVisibility(View.GONE);
                baixian1.setVisibility(View.VISIBLE);
                familyPho.setVisibility(View.VISIBLE);
                familyName.setVisibility(View.VISIBLE);
                familyPho.setText(phone);
                familyName.setText(Utils.fixName(name));
                break;
            case OTHERPHO:
                setContactDate("07", name, name, phone, id);
                addOther.setVisibility(View.GONE);
                baixian2.setVisibility(View.VISIBLE);
                otherPho.setVisibility(View.VISIBLE);
                otherName.setVisibility(View.VISIBLE);
                otherPho.setText(phone);
                otherName.setText(Utils.fixName(name));
                break;
        }
    }

    /**
     * 获取联系人的所有电话号码
     *
     * @param cursor
     * @return
     */
    private List<String> getContactPhone(Cursor cursor) {
        int phoneColumn = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
        int phoneNum = cursor.getInt(phoneColumn);
        List<String> phoList = new ArrayList<String>();
        if (phoneNum > 0) {
            // 获得联系人的ID号
            int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            String contactId = cursor.getString(idColumn);
            // 获得联系人的电话号码的cursor;
            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
            if (phones.moveToFirst()) {
                // 获取联系人姓名
                String phoneName = phones.getString(phones.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                phoList.add(phoneName);
                // 遍历所有的电话号码
                for (; !phones.isAfterLast(); phones.moveToNext()) {
                    int index = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String phoneNumber = phones.getString(index);
                    phoList.add(phoneNumber);
                }
                if (!phones.isClosed()) {
                    phones.close();
                }
            }
        }
        return phoList;
    }

    @Override
    public void onListViewSelect(String selected) {
        familyRS.setText(selected);
        switch (familyRS.getText().toString()) {
            case "父亲":
                setFamilyName(familyName.getText().toString(), "01");
                break;
            case "母亲":
                setFamilyName(familyName.getText().toString(), "02");
                break;
            case "配偶":
                setFamilyName(familyName.getText().toString(), "03");
                break;
            case "子女":
                setFamilyName(familyName.getText().toString(), "04");
                break;
            case "兄弟姐妹":
                setFamilyName(familyName.getText().toString(), "08");
                break;
        }
    }

    public void setFamilyName(String name, String type) {
        for (int i = 0; i < oldDate.size(); i++) {
            Map map = (Map) oldDate.get(i);
            if (map.get("name").equals(name)) {
                map.put("type", type);
                oldDate.set(i, map);
            }
        }
    }

/*    *//**
     * 修正不呼和格式的姓名
     * @param name
     * @return
     *//*
    public String fixName(String name) {
        String fixName = "";
        String letter;
        if (name != null) {
            char[] namechars = name.trim().toCharArray();
            for (int i=0;i<namechars.length;i++) {
                StringBuilder builder = new StringBuilder();
                letter = builder.append(namechars[i]).toString();
                Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5]+");
                Matcher m = pattern.matcher(letter);
                if(m.find()&&m.group(0).equals(letter)){
                    //该字符为一个汉字，不作处理
                }else {
                    if (!STANDARDNAME.contains(letter)) {
                        //如果该字符不是可用字符，则转成""
                        namechars[i] = ' ';
                    }
                }
            }
            fixName = String.valueOf(namechars);
            fixName = fixName.replaceAll(" ","");
        }
        return fixName;
    }*/
}
