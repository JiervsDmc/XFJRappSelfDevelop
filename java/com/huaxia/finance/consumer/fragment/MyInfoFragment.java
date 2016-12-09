package com.huaxia.finance.consumer.fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.activity.RepayOnlineRecordActivity;
import com.huaxia.finance.consumer.activity.UpdatePasswordActivity;
import com.huaxia.finance.consumer.activity.protocol.CompanyProfileProtocolActivity;
import com.huaxia.finance.consumer.activity.samefunction.BankCard3Activity;
import com.huaxia.finance.consumer.activity.samefunction.BankCardActivity;
import com.huaxia.finance.consumer.activity.samefunction.UserInfoActivity;
import com.huaxia.finance.consumer.base.BaseFragment;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.AccountMgr;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.LogUtil;
import com.huaxia.finance.consumer.util.ToastUtils;
import com.huaxia.finance.consumer.util.UpdateAppUtil;
import com.huaxia.finance.consumer.view.LogOutCheckDialog;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;


/**
 * 个人中心页面
 * Created by lipiao on 2016/7/5.
 */
public class MyInfoFragment extends BaseFragment {

    private LinearLayout userInfoRL;
    private TextView logoutTV;
    private LinearLayout bankCard;
    private LinearLayout companyInfo;
    private LinearLayout updateVis;
    private LinearLayout updatePassword;
    private LinearLayout callPhone;
    private LinearLayout repayRecord;
    private TextView userPhonrNumber;
    private TextView tvAuthentication;
    private ImageView userHeadPortrait;
    private TextView tvVisionCode;

    private int version;
    private String versionName;
    private ProgressDialog progressDialog;
    private String apkUrl;
    private UpdateAppUtil updateAppUtil;
    private String updateMsg;

    private AccountMgr mgr;

    @Override
    protected int getLayout() {
        return R.layout.fragment_student_myinfo;
    }

    @Override
    protected String getTitleText() {
        return "个人中心";
    }

    @Override
    protected void findViews(View view) {
        super.findViews(view);
        userInfoRL = (LinearLayout) view.findViewById(R.id.userInfo_RL);
        bankCard = (LinearLayout) view.findViewById(R.id.bankCard_RL);
        companyInfo = (LinearLayout) view.findViewById(R.id.companyInfo_RL);
        updateVis = (LinearLayout) view.findViewById(R.id.updateVis_RL);
        updatePassword = (LinearLayout) view.findViewById(R.id.update_password_RL);
        logoutTV = (TextView) view.findViewById(R.id.logout_TV);
        repayRecord = (LinearLayout) view.findViewById(R.id.repay_record);
        callPhone = (LinearLayout) view.findViewById(R.id.call_hotline);
        userPhonrNumber = (TextView) view.findViewById(R.id.user_phone_number);
        tvAuthentication = (TextView) view.findViewById(R.id.tv_authentication);
        userHeadPortrait = (ImageView) view.findViewById(R.id.user_head_portrait);
        tvVisionCode=(TextView)view.findViewById(R.id.tv_vision_code);
    }

    @Override
    protected void setup() {
        super.setup();
        getCurrentVersion();
        tvVisionCode.setText("V "+versionName);
        updateAppUtil = new UpdateAppUtil(getActivity());
        mgr = new AccountMgr(getActivity());
        getUserInfo();
        userInfoRL.setOnClickListener(this);
        logoutTV.setOnClickListener(this);
        bankCard.setOnClickListener(this);
        companyInfo.setOnClickListener(this);
        updatePassword.setOnClickListener(this);
        updateVis.setOnClickListener(this);
        repayRecord.setOnClickListener(this);
        callPhone.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        Intent intent;
        switch (view.getId()) {
            case R.id.userInfo_RL:
                intent = new Intent(getActivity(), UserInfoActivity.class);
                startActivity(intent);
                break;
            case R.id.logout_TV:
                //退出登录已给出提示框，本地保存信息已清空
                goLogOut();
                break;
            case R.id.update_password_RL:
                intent = new Intent(getActivity(), UpdatePasswordActivity.class);
                startActivity(intent);
                //修改密码
                break;
            case R.id.updateVis_RL:
                checkAppVersion();
                break;
            case R.id.bankCard_RL:
                // TODO: 2016/12/9 新的我的银行卡列表页 
                //intent = new Intent(getActivity(), BankCardActivity.class);
                intent = new Intent(getActivity(), BankCard3Activity.class);
                startActivity(intent);
                break;
            case R.id.companyInfo_RL:
                intent = new Intent(getActivity(), CompanyProfileProtocolActivity.class);
                startActivity(intent);
                break;
            case R.id.repay_record:
                intent = new Intent(getActivity(), RepayOnlineRecordActivity.class);
                startActivity(intent);
                break;
            case R.id.call_hotline:
                Intent intent1 = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + "4000466600"));
                startActivity(intent1);
                break;
        }
    }

    public void getCurrentVersion() {
        PackageManager packageManager = getActivity().getPackageManager();
        PackageInfo packageInfo;
        try {
            packageInfo = packageManager.getPackageInfo(getActivity().getPackageName(), 0);
            version = packageInfo.versionCode;
            versionName=packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void checkAppVersion() {
        Toast.makeText(getActivity(), "正在检查更新", Toast.LENGTH_SHORT).show();
        Map map = new HashMap();
        map.put("type", "android");
        map.put("versionNumber", version);
//        map.put("versionNumber", "0");
        //TODO:根据陈善华要求，无需登录信息的接口传入的token和session为空，需要更改
        ApiCaller.call(getActivity(), Constant.URL, "0033", "appService", map,true, new ApiCaller.MyStringCallback(getActivity(), true, false, false, null, null) {
//        ApiCaller.call(getActivity(), Constant.URL, "0033", "appService", map, new ApiCaller.MyStringCallback(getActivity(), true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                if (head.getResponseCode().equals("0000")) {
                    //进行网络请求获取是否有版本更新 todo:网络请求成功发送消息
                    if (body.size() == 0) {//当前版本为最新版本
                        Toast.makeText(getActivity(), "当前版本为最新版本", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    updateMsg = "" + body.get("contents");
                    apkUrl = "" + body.get("downloadUrl");
                    // 10：不强制，20：强制升级
                    if (body.get("forceUpdate").equals("10")) {
                        handler1.sendEmptyMessage(1);
                    } else if (body.get("forceUpdate").equals("20")) {
                        handler1.sendEmptyMessage(0);
                    }

                }
            }
        });

    }

    private Handler handler1 = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 1) {//如果需要更新弹出框
                showUpdateDialog();
            } else if (msg.what == 0) {
                forceUpdateDialog();
            }

        }
    };


    /**
     * 强制更新弹出框
     */
    private void forceUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("有版本可以更新");
        builder.setMessage(updateMsg);
        builder.setCancelable(true);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    downFile(apkUrl);
                } else {
                    Toast.makeText(getActivity(), "SD卡不可用，请插入SD卡", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.create().show();
    }

    /**
     * 有更新显示弹出框
     */
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("有版本可以更新");
        builder.setMessage(updateMsg);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    downFile(apkUrl);
                } else {
                    Toast.makeText(getActivity(), "SD卡不可用，请插入SD卡", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();
    }

    /**
     * 下载apk
     *
     * @param url
     */
    void downFile(final String url) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("正在下载");
        progressDialog.setMessage("请稍后...");
        progressDialog.setProgress(0);
        progressDialog.setMax(0);
        progressDialog.setProgressNumberFormat("%1d M/%2d M");
        progressDialog.show();
        progressDialog.setCancelable(false);
        updateAppUtil.downLoadFile(url, progressDialog, handler1);
    }


    //退出登录
    private void goLogOut() {
        new LogOutCheckDialog(getActivity()).show();
    }

    //获取用户信息
    private void getUserInfo() {
        Map<String, Object> map = new HashMap<>();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        ApiCaller.call(getActivity(), Constant.URL, "0026", "appService", map, new ApiCaller.MyStringCallback(getActivity(), true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                LogUtil.getLogutil().d("个人信息结果是" + response);
                if (head.getResponseCode().contains("0000") && !body.isEmpty()) {
                    Map map = (Map) body.get("result");
                    userPhonrNumber.setText(map.get("cellphone").toString());
                    if (map.get("resultCode").toString().contains("01")) {
                        tvAuthentication.setText("已实名认证");
                    } else {
                        tvAuthentication.setText("未实名认证");
                    }
                    if (map.get("sex").toString().contains("0")) {
                        userHeadPortrait.setImageResource(R.drawable.head_female);
                        return;
                    } else if (map.get("sex").toString().contains("1")) {
                        LogUtil.getLogutil().d("sex的值" + map.get("sex").toString());
                        userHeadPortrait.setImageResource(R.drawable.head_male);
                        return;
                    } else {
                        userHeadPortrait.setImageResource(R.drawable.head_male);
                        return;
                    }

                } else {
                    ToastUtils.showSafeToast(getActivity(), head.getResponseMsg());
                }
            }

        });
    }

    @Override
    protected boolean showBtnLeft() {
        return false;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getUserInfo();
        }
    }
}
