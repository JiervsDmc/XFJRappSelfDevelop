package com.huaxia.finance.consumer.activity.worker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.activity.order.OrderSubmitPassActivity;
import com.huaxia.finance.consumer.activity.samefunction.ContactInfoData;
import com.huaxia.finance.consumer.adapter.WorkerUploadAdapter;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.BaiduLocationUtil;
import com.huaxia.finance.consumer.util.FileManager;
import com.huaxia.finance.consumer.util.IsNullUtils;
import com.huaxia.finance.consumer.util.LocationUtils;
import com.huaxia.finance.consumer.util.LogUtil;
import com.huaxia.finance.consumer.util.ToastUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * 工作族- 企业主--影像附件上传
 */
public class WorkerAttachmentUploadActivity extends BaseActivity implements WorkerUploadAdapter.OnSelectPicMethod, View.OnClickListener {

    @ViewInject(R.id.lv_required_information)
    private ListView lvRequiredInformation;
    private Button btn;

    protected String imgPath0 = null;
    // 拍照和相册相关
    protected static final int CODE_PICK_IMG = 1;
    protected static final int CODE_TAKE_PHOTO = 2;
    private List<String> list;
    private List pageIdList;
    private List bitmapList;
    private WorkerUploadAdapter adapter;
    private Dialog dialog;

    protected static final int CODE_GPS_LOCATION = 3;

    //是否有首付，true：有
    private boolean havePayment;
    private String orderNo;
    private String imageType;
    private String number;
    private static final int ALLCALL = 0100;
    //定位纬经度
    //定位纬经度
    private String gpsCode;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = null;

    @Override
    protected int getLayout() {
        return R.layout.activity_attachment_upload;
    }

    @Override
    protected String getTitleText() {
        return "影像附件";
    }

    @Override
    protected void setup() {
        super.setup();
        havePayment = getIntent().getBooleanExtra("havePayment", false);
        orderNo = getIntent().getStringExtra("orderNo");
        number = getIntent().getStringExtra("number");
        if (number.contains("2")) {
            getImage();
        }
        list = new ArrayList<>();
        pageIdList = new ArrayList();
        bitmapList = new ArrayList();
        for (int i = 0; i < 7; i++) {
            list.add("1");
            pageIdList.add("1");
            bitmapList.add("1");
        }
        adapter = new WorkerUploadAdapter(this, list, bitmapList, havePayment, this);
        lvRequiredInformation.setAdapter(adapter);
        View mFootView = getLayoutInflater().inflate(R.layout.item_footer, null);
        btn = (Button) mFootView.findViewById(R.id.upload_submit);
        btn.setOnClickListener(this);
        lvRequiredInformation.addFooterView(mFootView);
    }

    private void getImage() {
        Map<String, Object> map = new HashMap<>();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        map.put("orderNo", orderNo);
        ApiCaller.call(this, Constant.URL, "0038", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                LogUtil.getLogutil().d("获取缩略图结果是" + response);
                if (head.getResponseCode().contains("0000")) {
                    if (!IsNullUtils.isNull(body.get("ecmPage"))) {
                        List ecmPageList = new ArrayList();
                        ecmPageList = (List) body.get("ecmPage");
                        for (int i = 0; i < ecmPageList.size(); i++) {
                            Map ecmPageMap = (Map) ecmPageList.get(i);
                            if (ecmPageMap.get("classCode").toString().contains("0101")) {
                                if (ecmPageMap.get("pageRemark").toString().contains("0")) {
                                    bitmapList.set(0, FileManager.stringtoBitmap(ecmPageMap.get("picture").toString()));
                                    pageIdList.set(0, ecmPageMap.get("pageId").toString());
                                } else if (ecmPageMap.get("pageRemark").toString().contains("1")) {
                                    bitmapList.set(1, FileManager.stringtoBitmap(ecmPageMap.get("picture").toString()));
                                    pageIdList.set(1, ecmPageMap.get("pageId").toString());
                                }
                            } else if (ecmPageMap.get("classCode").toString().contains("0103")) {
                                bitmapList.set(2, FileManager.stringtoBitmap(ecmPageMap.get("picture").toString()));
                                pageIdList.set(2, ecmPageMap.get("pageId").toString());
                            } else if (ecmPageMap.get("classCode").toString().contains("0106")) {
                                bitmapList.set(3, FileManager.stringtoBitmap(ecmPageMap.get("picture").toString()));
                                pageIdList.set(3, ecmPageMap.get("pageId").toString());
                            } else if (ecmPageMap.get("classCode").toString().contains("0107")) {
                                if (ecmPageMap.get("pageRemark").toString().contains("2")) {
                                    bitmapList.set(4, FileManager.stringtoBitmap(ecmPageMap.get("picture").toString()));
                                    pageIdList.set(4, ecmPageMap.get("pageId").toString());
                                } else if (ecmPageMap.get("pageRemark").toString().contains("3")) {
                                    bitmapList.set(5, FileManager.stringtoBitmap(ecmPageMap.get("picture").toString()));
                                    pageIdList.set(5, ecmPageMap.get("pageId").toString());
                                }
                            } else if (ecmPageMap.get("classCode").toString().contains("0104")) {
                                bitmapList.set(6, FileManager.stringtoBitmap(ecmPageMap.get("picture").toString()));
                                pageIdList.set(6, ecmPageMap.get("pageId").toString());
                            }
                        }
                        initUI(0);
                    }/*else {
                        ToastUtils.showSafeToast(WorkerAttachmentUploadActivity.this, head.getResponseMsg());
                        return;
                    }*/
                } else {
                    ToastUtils.showSafeToast(WorkerAttachmentUploadActivity.this, head.getResponseMsg());
                }
            }

        });
    }

    private Message msg;

    private void initUI(final int i) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                switch (i) {
                    case 0:
                        msg = new Message();
                        msg.what = ALLCALL;
                        handler.sendMessage(msg);
                        break;
                }
            }
        }.start();

    }


    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.upload_submit:
                Intent intent = new Intent(this, OrderSubmitPassActivity.class);
                if (havePayment) {
                    if (list.get(0).equals("1") || list.get(1).equals("1") || list.get(2).equals("1") || list.get(3).equals("1")) {
                        if (bitmapList.get(0).equals("1") || bitmapList.get(1).equals("1") || bitmapList.get(2).equals("1") || bitmapList.get(3).equals("1")) {
                            if ((list.get(0).equals("1") && bitmapList.get(0).equals("1")) || (list.get(1).equals("1") && bitmapList.get(1).equals("1")) || (list.get(2).equals("1") && bitmapList.get(2).equals("1")) || (list.get(3).equals("1") && bitmapList.get(3).equals("1"))) {
                                toast("请上传必须的影像附件");
                            } else {
                                openGPSSettting(intent);
                                return;
                            }

                        } else {
                            openGPSSettting(intent);
                        }
                    } else {
                        openGPSSettting(intent);
                    }
                } else {
                    if (list.get(0).equals("1") || list.get(1).equals("1") || list.get(2).equals("1")) {
                        if (bitmapList.get(0).equals("1") || bitmapList.get(1).equals("1") || bitmapList.get(2).equals("1")) {
                            if ((list.get(0).equals("1") && bitmapList.get(0).equals("1")) || (list.get(1).equals("1") && bitmapList.get(1).equals("1")) || (list.get(2).equals("1") && bitmapList.get(2).equals("1"))) {
                                toast("请上传必须的影像附件");
                            } else {
                                openGPSSettting(intent);
                                return;
                            }

                        } else {
                            openGPSSettting(intent);
                            return;
                        }
                    } else {
                        openGPSSettting(intent);
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(CODE_GPS_LOCATION == requestCode && RESULT_OK == resultCode) {

        }

        if (CODE_PICK_IMG == requestCode && RESULT_OK == resultCode) {
            Uri selectedImageUri = data.getData();
            String path = FileManager.getRealPathFromURI(this, selectedImageUri);
            imageType = FileManager.getFileName(path);
            uploadImage(path);
            LogUtil.getLogutil().d("图片名" + FileManager.getFileName(path));
//            list.set(POSITON, path);
        } else if (CODE_TAKE_PHOTO == requestCode && RESULT_OK == resultCode) {
            if (!IsNullUtils.isNull(imgPath0)) {
                imageType = FileManager.getFileName(imgPath0);
                uploadImage(imgPath0);
            }
//            list.set(POSITON, imgPath0);
        }
//        adapter.notifyDataSetChanged();
    }

    public int POSITON = -1;
    public int TYPE = -1;

    @Override
    public void onIcon(int position, int Type) {
        POSITON = position;
        TYPE = Type;
        popOption();
    }

    protected void popOption() {

        View view = LayoutInflater.from(this).inflate(
                R.layout.photo_choose_dialog, null);
        dialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = dialog.getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_menu_animstyle);
        WindowManager.LayoutParams wl = window.getAttributes();
        wl.x = 0;
        wl.y = getWindowManager().getDefaultDisplay()
                .getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        // 设置显示位置
        dialog.onWindowAttributesChanged(wl);
        // 设置点击外围消失
        dialog.setCanceledOnTouchOutside(true);
        view.findViewById(R.id.take_phone_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePhoto();
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.native_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickAPicture();
                dialog.dismiss();
            }
        });
        view.findViewById(R.id.close_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void pickAPicture() {
        FileManager.pickAPicture(this, null, CODE_PICK_IMG);
    }

    protected void takePhoto() {
        imgPath0 = FileManager.takePhoto(this, null, CODE_TAKE_PHOTO);
    }

    private void uploadImage(final String file) {
        Map map = new HashMap();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        map.put("orderNo", orderNo);
        map.put("imageType", imageType);
        if (TYPE == 2) {
            map.put("pageId", pageIdList.get(POSITON));
        }
        LogUtil.getLogutil().d("imageType" + imageType);
        Bitmap bitmap = FileManager.getSmallBitmap(file);
        long x = FileManager.getBitmapsize(bitmap);
        LogUtil.getLogutil().d("图片大小" + x);
        if (bitmap == null) {
            toast("图片格式错误");
            return;
        }
        String s = FileManager.bitmaptoString(bitmap);
        LogUtil.getLogutil().d("file值" + file);
        switch (POSITON) {
            case 0:
                map.put("idCardFront", s);
                map.put("classCode", "0101");
                map.put("pageRemark", "0");
                break;
            case 1:
                map.put("idCardBack", s);
                map.put("classCode", "0101");
                map.put("pageRemark", "1");
                break;
            case 2:
                map.put("employmentProof", s);
                map.put("classCode", "0103");
                break;
            case 3:
                map.put("paymentProof", s);
                map.put("classCode", "0106");
                break;
            case 4:
                map.put("householdIndex", s);
                map.put("classCode", "0107");
                map.put("pageRemark", "2");
                break;
            case 5:
                map.put("householdSelf", s);
                map.put("classCode", "0107");
                map.put("pageRemark", "3");
                break;
            case 6:
                map.put("assetsProof", s);
                map.put("classCode", "0104");
                break;
        }
        ApiCaller.call(this, Constant.URL, "0014", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                // toast("上传图片失败");
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                if (head.getResponseCode().contains("0000") && !body.isEmpty()) {
                    //请求成功后设置本地图片
                    list.set(POSITON, file);
                    LogUtil.getLogutil().d("list值" + list);
                    adapter.notifyDataSetChanged();
                    pageIdList.set(POSITON, body.get("pageId").toString());

                } else {
                    toast(head.getResponseMsg());
                }

            }
        });
    }


    //影像上传完成
    private void toSubmitPass(final Intent intent) {
        Map<String, Object> map = new HashMap<>();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        map.put("orderNo", orderNo);
        map.put("gpsCode", gpsCode);
        if (!IsNullUtils.isNull(ContactInfoData.list)) {
            map.put("pullInfo", ContactInfoData.list);
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
                    ContactInfoData.list=new ArrayList<Map<String, String>>();
                    startActivity(intent);
                    finish();
                    ToastUtils.showSafeToast(WorkerAttachmentUploadActivity.this, head.getResponseMsg());
                    return;
                } else {
                    ToastUtils.showSafeToast(WorkerAttachmentUploadActivity.this, head.getResponseMsg());
                }
            }

        });
    }

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            adapter = null;
            switch (msg.what) {
                case ALLCALL:
                    adapter = new WorkerUploadAdapter(WorkerAttachmentUploadActivity.this, list, bitmapList, havePayment, WorkerAttachmentUploadActivity.this);
                    lvRequiredInformation.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    break;

            }
        }

        ;
    };

    /**
     * 判断GSP模块是否存在或者开启
     */
    protected void openGPSSettting(Intent intent) {
        LocationManager alm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (alm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            toSubmitPass(intent);
            return;
        }

        toast("请开启GPS");
        new AlertDialog.Builder(WorkerAttachmentUploadActivity.this).setTitle("提示")
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
        //初始化定位
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        myListener = new MyLocationListener();
        mLocationClient.registerLocationListener( myListener );    //注册监听函数
        //开启定位
        LocationClientOption option = BaiduLocationUtil.initLocation();
        mLocationClient.setLocOption(option);
        mLocationClient.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //关闭定位
        mLocationClient.stop();
        mLocationClient.unRegisterLocationListener(myListener);
    }

}
