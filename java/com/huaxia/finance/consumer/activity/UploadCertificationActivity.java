package com.huaxia.finance.consumer.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.adapter.UploadCertAdapter;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.base.BasePopWindow;
import com.huaxia.finance.consumer.util.FileManager;
import com.huaxia.finance.consumer.util.IsNullUtils;
import com.huaxia.finance.consumer.util.LogUtil;
import com.huaxia.finance.consumer.util.ToastUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;


public class UploadCertificationActivity extends BaseActivity {
//    @ViewInject(R.id.rl_select)
//    RelativeLayout rl_select;
//    @ViewInject(R.id.lv_cert_select)
//    ListView lv_cert_select;

    @ViewInject(R.id.tv_cert)
    TextView tv_cert;
    @ViewInject(R.id.lv_pic)
    ListView lv_pic;
    @ViewInject(R.id.rl_empty)
    RelativeLayout rl_empty;
    private Intent intent;
    private String reminderTitle = "";
    private String imageName;
    private String orderNo;
    private String imageAlias;
//    private CertSelectAdapter certAdapter;
//    private List<CertSelectBean> certList = new ArrayList<>();
//    private boolean isSelectShow;
    private Dialog dialog;
    protected String imgPath0 = null;
    // 拍照和相册相关
    protected static final int CODE_PICK_IMG = 1;
    protected static final int CODE_TAKE_PHOTO = 2;
    private List pageIdList;
    private List bitmapList;
    private String imageType;
    private static final int ALLCALL = 0100;
    private UploadCertAdapter adapter;
//    private String number;
    private int TYPE = -1;
    private int POSITON = -1;
    private String classCode;

    @Override
    protected int getLayout() {
        return R.layout.activity_upload_certification;
    }

    @Override
    protected String getTitleText() {
        imageName = intent.getStringExtra("imageName");
        return imageName;
    }

    @Override
    protected void setup() {
        intent = getIntent();
        super.setup();
//        havePayment = getIntent().getBooleanExtra("havePayment", false);
        orderNo = getIntent().getStringExtra("orderNo");
//        number = getIntent().getStringExtra("number");
        imageAlias = getIntent().getStringExtra("imageAlias");
        classCode = getIntent().getStringExtra("classCode");
        getReminderTitle();
//        if (number.contains("2")) {
            rl_empty.setVisibility(View.GONE);
            lv_pic.setVisibility(View.VISIBLE);
            getImage();
//        } else {
//            rl_empty.setVisibility(View.VISIBLE);
//            lv_pic.setVisibility(View.GONE);
//        }
        pageIdList = new ArrayList();
        bitmapList = new ArrayList();
    }

    private Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            adapter = null;
            switch (msg.what) {
                case ALLCALL:
                    if (bitmapList.size() == 0) {
                        rl_empty.setVisibility(View.VISIBLE);
                        lv_pic.setVisibility(View.GONE);
                        return;
                    }
                    rl_empty.setVisibility(View.GONE);
                    lv_pic.setVisibility(View.VISIBLE);
                    adapter = new UploadCertAdapter(UploadCertificationActivity.this, bitmapList);
                    lv_pic.setAdapter(adapter);
                    lv_pic.setSelection(bitmapList.size()-1);
                    adapter.notifyDataSetChanged();
                    lv_pic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            TYPE = 2;
                            POSITON = position;
                            final View dialogView = LayoutInflater.from(UploadCertificationActivity.this).inflate(R.layout.dialog_change_cert, null);
                            final ImageView iv_cert_pic = (ImageView) dialogView.findViewById(R.id.iv_cert_pic);
                            new BasePopWindow(UploadCertificationActivity.this, UploadCertificationActivity.this, 0, 0) {
                                @Override
                                protected void initData() {
                                    iv_cert_pic.setImageBitmap((Bitmap) bitmapList.get(position));
                                    dialogView.findViewById(R.id.rl_change).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dismissPop();
                                            popOption();
                                        }
                                    });
                                    dialogView.findViewById(R.id.rl_delete).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            dismissPop();
                                            deleteCertPic();
                                        }
                                    });
                                }

                                @Override
                                protected View getResId() {
                                    return dialogView;
                                }
                            };

                        }
                    });
                    break;
            }
        }

        ;
    };

    private void getReminderTitle() {
        switch (classCode) {
            case "0101"://身份证
                reminderTitle = getResources().getString(R.string.cert_title);
                break;
            case "0102"://学籍证明
                reminderTitle = getResources().getString(R.string.student_title);
                break;
            case "0103"://工作证明
                reminderTitle = getResources().getString(R.string.work_title);
                break;
            case "0104"://财力证明
                reminderTitle = getResources().getString(R.string.finance_title);
                break;
            case "0106"://首付凭证
                reminderTitle = getResources().getString(R.string.shoufu_title);
                break;
            case "0108"://征信报告
                reminderTitle = getResources().getString(R.string.zhengxin_title);
                break;
            case "0110"://个人流水待定
                reminderTitle = getResources().getString(R.string.flow_title);
                break;
            case "0109"://其他凭证
                reminderTitle = getResources().getString(R.string.other_title);
                break;
            default:
                reminderTitle = getResources().getString(R.string.default_title);
                break;
        }
        tv_cert.setText(reminderTitle);
    }

    @OnClick(R.id.rl_add)
    public void addCert(View view) {
        TYPE = -1;
        popOption();
    }

//    @OnClick(R.id.rl_cert)
//    public void selectCert(View view) {
//        upDateSelectPop();
//    }

//    @OnClick(R.id.view_empty)
//    public void disMissSelec(View view) {
//        rl_select.setVisibility(View.GONE);
//    }

//    private void upDateSelectPop() {
//        if(selectList[0].equals("")){
//            return;
//        }
//        final RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) lv_cert_select.getLayoutParams();
//        int topMargin = -selectList.length * ScreenUtils.dip2px(this, 40);
//        layoutParams.topMargin = (isSelectShow ? 0 : topMargin);
//        lv_cert_select.setLayoutParams(layoutParams);
//        ValueAnimator animator;
//        if (isSelectShow) {
//            rl_select.setVisibility(View.GONE);
//            animator = ValueAnimator.ofInt(0, -selectList.length * ScreenUtils.dip2px(this, 40));
//        } else {
//            rl_select.setVisibility(View.VISIBLE);
//            lv_cert_select.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    for (int i = 0; i < certList.size(); i++) {
//                        if (i == position) {
//                            certList.get(i).setSelected(true);
//                        } else {
//                            certList.get(i).setSelected(false);
//                        }
//                    }
//                    certAdapter.notifyDataSetChanged();
//                    rl_select.setVisibility(View.GONE);
//                    tv_cert.setText(certList.get(position).getCertName());
//                }
//            });
//            animator = ValueAnimator.ofInt(-selectList.length * ScreenUtils.dip2px(this, 40), 0);
//        }
//        animator.setDuration(500);
//        animator.start();
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                int value = (int) animation.getAnimatedValue();
//                layoutParams.topMargin = value;
//                lv_cert_select.setLayoutParams(layoutParams);
//            }
//        });
//        isSelectShow = !isSelectShow;
//    }

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
        LogUtil.getLogutil().d("imgPath0值" + imgPath0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (CODE_PICK_IMG == requestCode && RESULT_OK == resultCode) {
            Uri selectedImageUri = data.getData();
            String path = FileManager.getRealPathFromURI(this, selectedImageUri);
            imageType = FileManager.getFileName(path);
            uploadImage(path);
//            list.set(POSITON, path);
        } else if (CODE_TAKE_PHOTO == requestCode && RESULT_OK == resultCode) {
            if (!IsNullUtils.isNull(imgPath0)) {
                imageType = FileManager.getFileName(imgPath0);
                LogUtil.getLogutil().d("imageType值" + imageType);
                LogUtil.getLogutil().d("imgPath0值1" + imgPath0);
                uploadImage(imgPath0);
            }
        }
    }

    private void deleteCertPic() {
        TYPE = 1;
        uploadImage(null);
    }
    private Bitmap bitmap = null;
    private void uploadImage(final String file) {
        String tradeCode = "";
        switch (TYPE){
            case -1://上传
                tradeCode = "0014";
                break;
            case 1://删除
                tradeCode = "0044";
                break;
            case 2://更换
                tradeCode = "0043";
                break;
        }
        Map map = new HashMap();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        map.put("orderNo", orderNo);
        map.put("imageAlias", imageAlias);
        if (TYPE != -1) {
            map.put("pageId", pageIdList.get(POSITON));
        }
        if(TYPE!=1){
            bitmap = FileManager.getSmallBitmap(file);
            long x = FileManager.getBitmapsize(bitmap);
            LogUtil.getLogutil().d("图片大小" + x);
            if (bitmap == null) {
                toast("图片格式错误");
                return;
            }
            String s = FileManager.bitmaptoString(bitmap);
            map.put("classCode", classCode);
            map.put("imageType", imageType);
            map.put("imageStream", s);
            map.put("pageRemark", "0");
        }
        ApiCaller.call(this, Constant.URL, tradeCode, "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                toast("上传图片失败");
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                //请求成功后设置本地图片
                LogUtil.getLogutil().d("处理图片结果" + response);
                if (head.getResponseCode().contains("0000")) {
                    //请求成功后设置本地图片
                    if(TYPE ==2){
                        bitmapList.set(POSITON,bitmap);
                    }else if(TYPE == -1){
                        if(!body.isEmpty()){
                            bitmapList.add(bitmap);
                            pageIdList.add(body.get("pageId").toString());
                        }else{
                            toast(head.getResponseMsg());
                        }
                    }else if(TYPE == 1){
                        bitmapList.remove(POSITON);
                        pageIdList.remove(POSITON);
                    }
                    initUI(0);
                } else {
                    toast(head.getResponseMsg());
                }
            }
        });
    }

    private void getImage() {
        Map<String, Object> map = new HashMap<>();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        map.put("orderNo", orderNo);
        map.put("imageAlias", imageAlias);
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
                    if (!IsNullUtils.isNull(body.get("pageIdMap"))) {
                        Map pageIdMap = (Map) body.get("pageIdMap");
                        Iterator iterator = pageIdMap.keySet().iterator();
                        while ((iterator.hasNext())){
                            String key;
                            String value;
                            key=iterator.next().toString();
                            value= (String) pageIdMap.get(key);
                            bitmapList.add(FileManager.stringtoBitmap(value));
                            pageIdList.add(key);
                        }
                    }
                    initUI(0);
                } else {
                    ToastUtils.showSafeToast(UploadCertificationActivity.this, head.getResponseMsg());
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
}
