package com.huaxia.finance.consumer.activity.freelance;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.activity.UploadCertificationActivity;
import com.huaxia.finance.consumer.activity.order.OrderSubmitPassActivity;
import com.huaxia.finance.consumer.activity.samefunction.ContactInfoData;
import com.huaxia.finance.consumer.activity.samefunction.UserInfoActivity;
import com.huaxia.finance.consumer.adapter.FreelanceUploadListAdapter;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.BaiduLocationUtil;
import com.huaxia.finance.consumer.util.DialogUtil;
import com.huaxia.finance.consumer.util.IsNullUtils;
import com.huaxia.finance.consumer.util.LogUtil;
import com.huaxia.finance.consumer.util.ToastUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

public class FreelanceAttachmentInfoActivity extends BaseActivity {
    @ViewInject(R.id.lv_upload_info)
    private ListView lv_upload_info;
    @ViewInject(R.id.ll_loading)
    private LinearLayout ll_loading;
    @ViewInject(R.id.btn_new)
    private TextView mBtnNew;
//    private boolean havePayment;
    private String orderNo;
    private String number;
    //定位纬经度
    //定位纬经度
    private String gpsCode;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = null;
    private List<Map<String, String>> allContact = new ArrayList<>();
    @Override
    protected int getLayout() {
        return R.layout.activity_freelance_attachment_info;
    }

    @Override
    protected String getTitleText() {
        return "影像上传";
    }

    @Override
    protected void setup() {
        super.setup();
//        havePayment = getIntent().getBooleanExtra("havePayment", false);
        orderNo = getIntent().getStringExtra("orderNo");
        number = getIntent().getStringExtra("number");
        ll_loading.setVisibility(View.GONE);
        lv_upload_info.setVisibility(View.VISIBLE);
        getUpLoadList();
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        myListener = new MyLocationListener();
        mLocationClient.registerLocationListener( myListener );    //注册监听函数
        //开启定位
        LocationClientOption option = BaiduLocationUtil.initLocation();
        mLocationClient.setLocOption(option);
        mLocationClient.start();
        getContactInfo();
    }

    private void getContactInfo() {
        if(IsNullUtils.isNull(ContactInfoData.list)){
            contactInfoData = new ContactInfoData(this, handler);
            contactInfoData.getContactInfo();
            LogUtil.getLogutil().i("getContactInfo所有联系人："+contactInfoData.list);
        }else{
            allContact = contactInfoData.list;
        }
    }

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0xfff0) {
                //获取所有联系人操作完成
                allContact.addAll(contactInfoData.getList());
            } else if (msg.what == 0xfff1) {
                //如果没有数据
                DialogUtil.inform(FreelanceAttachmentInfoActivity.this, "读取联系人信息失败", new DialogInterface.OnClickListener() {
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
    private void getUpLoadList() {
        Map<String, Object> map = new HashMap<>();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        map.put("orderNo", orderNo);
        ApiCaller.call(this, Constant.URL, "0042", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null){
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                updateEmpty();
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                LogUtil.getLogutil().d("获取上传信息列表" + response);
                if (head.getResponseCode().contains("0000")){
                      List listImage = new ArrayList();
                      listImage = (List) body.get("imageList");
                      upDateList(listImage);
                }else{
                    updateEmpty();
                    toast(head.getResponseMsg());
                }
            }
        });
    }

    private void upDateList(final List list) {
        ll_loading.setVisibility(View.GONE);
        lv_upload_info.setVisibility(View.VISIBLE);
        View footView = LayoutInflater.from(this).inflate(R.layout.item_footer_commit,null);
        FreelanceUploadListAdapter adapter = new FreelanceUploadListAdapter(list,this);
        lv_upload_info.setAdapter(adapter);
        lv_upload_info.addFooterView(footView);
        footView.findViewById(R.id.upload_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FreelanceAttachmentInfoActivity.this, OrderSubmitPassActivity.class);
                openGPSSettting(intent);
            }
        });
        lv_upload_info.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(((Map)list.get(position)).get("tabFlag")!=null&&((Map)list.get(position)).get("tabFlag").equals("0")){
                    return;
                }
                Intent intent = new Intent(FreelanceAttachmentInfoActivity.this, UploadCertificationActivity.class);
                intent.putExtra("imageAlias",((Map)list.get(position)).get("imageAlias").toString());
                intent.putExtra("imageName",((Map)list.get(position)).get("imageName").toString());
                intent.putExtra("classCode",((Map)list.get(position)).get("classCode").toString());
                intent.putExtra("orderNo",orderNo);
                intent.putExtra("number",number);
                startActivity(intent);
            }
        });

    }

    /**
     * 判断GSP模块是否存在或者开启
     */
    protected void openGPSSettting(Intent intent) {
        LocationManager alm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (alm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            toSubmitPass(intent);
            return;
        }

        Toast.makeText(this, "请开启GPS!", Toast.LENGTH_SHORT).show();
        new AlertDialog.Builder(FreelanceAttachmentInfoActivity.this).setTitle("提示")
                .setMessage("是否开启GPS获取地理位置")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, 3);
                    }
                }).show();

    }
    private ContactInfoData contactInfoData;
    //影像上传完成
    private void toSubmitPass(final Intent intent) {
        Map<String, Object> map = new HashMap<>();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        map.put("orderNo", orderNo);
        map.put("gpsCode",gpsCode);
        if (!IsNullUtils.isNull(allContact)) {
            map.put("pullInfo", allContact);
        }
        ApiCaller.call(this, Constant.URL, "0023", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                LogUtil.getLogutil().d("图片上传结果是" + response);
                if (head.getResponseCode().contains("0000")) {
                    //ActivityManagerUtil.getAppManager().finishAllActivity();
                    ContactInfoData.list=new ArrayList<Map<String, String>>();
                    startActivity(intent);
                    finish();
                    ToastUtils.showSafeToast(FreelanceAttachmentInfoActivity.this, head.getResponseMsg());
                    return;
                } else {
                    ToastUtils.showSafeToast(FreelanceAttachmentInfoActivity.this, head.getResponseMsg());
                }
            }

        });
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            StringBuilder sb = new StringBuilder();
            gpsCode = sb.append(location.getLongitude()).append(",").append(location.getLatitude()).toString();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭定位
        mLocationClient.stop();
        mLocationClient.unRegisterLocationListener(myListener);
    }

    private void updateEmpty(){
        ll_loading.setVisibility(View.VISIBLE);
        lv_upload_info.setVisibility(View.GONE);
        mBtnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setup();
            }
        });
    }
}
