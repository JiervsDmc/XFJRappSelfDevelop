package com.huaxia.finance.consumer.activity.samefunction;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.Data;

import com.huaxia.finance.consumer.util.IsNullUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by lipiao on 2016/10/28.
 */
public class ContactInfoData {
    private Context context;
    public static List<Map<String, String>> list;
    private List<Map<String, String>> listPhone;
    private Map map;
    private String emailStr = "";
    private String addressStr = "";
    private String nameStr = "";
    private Handler handler;

    public ContactInfoData(Context context, Handler handler) {
        this.context = context;
        list = new ArrayList<>();
        this.handler = handler;
    }

    public void getContactInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String mimetype = "";
                int oldrid = -1;
                int contactId = -1;
                Cursor cursor = context.getContentResolver().query(Data.CONTENT_URI, null, null, null, Data.RAW_CONTACT_ID);
                if (cursor.getCount() == 0) {
                    handler.sendEmptyMessage(0xfff1);
                    return;
                }
                map = new HashMap();
                while (cursor.moveToNext()) {
                    contactId = cursor.getInt(cursor.getColumnIndex(Data.RAW_CONTACT_ID));
                    if (oldrid != contactId) {
                        if (!IsNullUtils.isNull(map) && !map.isEmpty()) {
                            if (map.get("email").toString().endsWith("&")) {
                                map.put("email", map.get("email").toString().substring(0, map.get("email").toString().length() - 1));
                            }
                            if (map.get("address").toString().endsWith("&")) {
                                map.put("address", map.get("address").toString().substring(0, map.get("address").toString().length() - 1));
                            }
                            list.add(map);
                        }
                        emailStr = new String();
                        addressStr = new String();
                        nameStr = new String();
                        map = new HashMap();
                        listPhone = new ArrayList<>();
                        oldrid = contactId;
                    }
                    mimetype = cursor.getString(cursor.getColumnIndex(Data.MIMETYPE));
                    // 获得通讯录中每个联系人的ID
                    // 获得通讯录中联系人的名字
                    if (StructuredName.CONTENT_ITEM_TYPE.equals(mimetype)) {
                        nameStr =  cursor.getString(cursor.getColumnIndex(StructuredName.DISPLAY_NAME));
                    }
                    map.put("name", nameStr);
                    // 获取电话信息
                    if (Phone.CONTENT_ITEM_TYPE.equals(mimetype)) {
                        // 取出电话类型
                        int phoneType = cursor.getInt(cursor.getColumnIndex(Phone.TYPE));
                        Map mapP = new HashMap();
                        switch (phoneType) {
                            case Phone.TYPE_MOBILE:
                                mapP.put("label", "手机");
                                break;
                            case Phone.TYPE_HOME:
                                mapP.put("label", "住宅电话");
                                break;
                            case Phone.TYPE_WORK:
                                mapP.put("label", "单位电话");
                                break;
                            case Phone.TYPE_FAX_WORK:
                                mapP.put("label", "单位传真");
                                break;
                            case Phone.TYPE_FAX_HOME:
                                mapP.put("label", "住宅传真");
                                break;
                            case Phone.TYPE_COMPANY_MAIN:
                                mapP.put("label", "公司总机");
                                break;
                            case Phone.TYPE_CAR:
                                mapP.put("label", "车载电话");
                                break;
                            case Phone.TYPE_MAIN:
                                mapP.put("label", "总机");
                                break;
                            case Phone.TYPE_WORK_MOBILE:
                                mapP.put("label", "单位手机");
                                break;
                            default:
                                mapP.put("label", "其他");

                        }

                        mapP.put("mobile", cursor.getString(cursor.getColumnIndex(Phone.NUMBER)));
                        listPhone.add(mapP);
                    }
                    map.put("phones", listPhone);

                    // 查找email地址
                    if (Email.CONTENT_ITEM_TYPE.equals(mimetype)) {
                        emailStr += cursor.getString(cursor.getColumnIndex(Email.DATA)) + "&";

                    }
                    map.put("email", emailStr);

                    // 查找通讯地址
                    if (StructuredPostal.CONTENT_ITEM_TYPE.equals(mimetype)) {
                        addressStr += cursor.getString(cursor.getColumnIndex(StructuredPostal.COUNTRY)) +
                                cursor.getString(cursor.getColumnIndex(StructuredPostal.REGION)) +
                                cursor.getString(cursor.getColumnIndex(StructuredPostal.CITY)) +
                                cursor.getString(cursor.getColumnIndex(StructuredPostal.STREET)) + "&";
                    }
                    map.put("address", addressStr);
                }
                if (!IsNullUtils.isNull(map) && !map.isEmpty()) {
                    if (map.get("email").toString().endsWith("&")) {
                        map.put("email", map.get("email").toString().substring(0, map.get("email").toString().length() - 1));
                    }
                    if (map.get("address").toString().endsWith("&")) {
                        map.put("address", map.get("address").toString().substring(0, map.get("address").toString().length() - 1));
                    }
                    list.add(map);
                }
                cursor.close();
                handler.sendEmptyMessage(0xfff0);
            }
        }).start();
    }

    public List<Map<String, String>> getList() {
        return list;
    }
}
