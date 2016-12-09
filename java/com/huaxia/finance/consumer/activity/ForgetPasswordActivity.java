package com.huaxia.finance.consumer.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.Constans;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.util.FileManager;
import com.huaxia.finance.consumer.util.LogUtil;
import com.huaxia.finance.consumer.util.MD5Util;
import com.huaxia.finance.consumer.util.RegularExpressionUtil;
import com.huaxia.finance.consumer.util.ToastUtils;
import com.huaxia.finance.consumer.view.ClearEditText;
import com.lidroid.xutils.view.annotation.ViewInject;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Call;

/**
 * 忘记密码
 */
public class ForgetPasswordActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.et_phone_number)
    private ClearEditText etPhoneNumber;
    @ViewInject(R.id.et_new_password)
    private ClearEditText etNewPassword;
    @ViewInject(R.id.et_ok_password)
    private ClearEditText etOkPassword;
    @ViewInject(R.id.et_image_code)
    private ClearEditText etImageCode;
    @ViewInject(R.id.get_image_code)
    private ImageView getImageCode;
    @ViewInject(R.id.et_verification_code)
    private ClearEditText etVerificationCode;
    @ViewInject(R.id.tv_get_verification_code)
    private TextView getVerificationCode;
    @ViewInject(R.id.tv_ok)
    private TextView tvOk;
    @ViewInject(R.id.ll_login)
    private LinearLayout llLogin;
    private String uuid;
    @Override
    protected int getLayout() {
        return R.layout.activity_forget_password;
    }

    @Override
    protected String getTitleText() {
        return "忘记密码";
    }

    @Override
    protected void setup() {
        super.setup();
        getImageCodeNet();
        getImageCode.setOnClickListener(this);
        getVerificationCode.setOnClickListener(this);
        tvOk.setOnClickListener(this);
        llLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()){
            case R.id.get_image_code:
                getImageCodeNet();
                break;
            case R.id.tv_get_verification_code:
                String phoneNumber= etPhoneNumber.getText().toString().trim();
                String imageCode=etImageCode.getText().toString().trim();

                if(TextUtils.isEmpty(phoneNumber)) {
                    ToastUtils.showSafeToast(this, "手机号码不能为空");
                    return;
                }
                else if(!RegularExpressionUtil.isMobileNO(phoneNumber)) {
                    ToastUtils.showSafeToast(this, "手机号码有误，请重新输入");
                    return;
                }else if(TextUtils.isEmpty(imageCode)) {
                    ToastUtils.showSafeToast(this,"图形验证码不能为空");
                    return;
                }else {
                    getVerityCode(phoneNumber,imageCode);
                }
                break;
            case R.id.tv_ok:
                toLogIn();
                break;
            case R.id.ll_login:
                finish();
                break;
        }
    }



    //获取图片验证码
    private void getImageCodeNet() {
        Map<String, Object> map = new HashMap<>();
        uuid=System.console() + ApiCaller.generateString(5);
        map.put("UUID", uuid);
        ApiCaller.call(this, Constant.URL, "0002", "authService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                LogUtil.getLogutil().d("获取图片验证码结果是"+response);
                if(head.getResponseCode().contains("0000")&&!body.isEmpty()) {
                    // getImageCode.setImageBitmap(GenerateImage(body.get("code").toString()));
                    getImageCode.setImageBitmap(FileManager.stringtoBitmap(body.get("code").toString()));
                    return;
                }else {
                    ToastUtils.showSafeToast(ForgetPasswordActivity.this,head.getResponseMsg());
                    return;
                }

            }

        });
    }

    //获取短信验证码
    private void getVerityCode(String phoneNumber,String imageCode) {

        Map<String, Object> map = new HashMap<>();
        map.put("UUID", uuid);
        map.put("verificationCode",imageCode);
        map.put("phoneNumber",phoneNumber);
        map.put("type",2);
        LogUtil.getLogutil().d("verificationCode....."+imageCode+"phoneNumber....."+phoneNumber);
        ApiCaller.call(this, Constant.URL, "0005", "authService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                LogUtil.getLogutil().d("获取短信验证码结果是"+response);
                if(head.getResponseCode().contains("0000")) {
                    startCounter(60);
                    ToastUtils.showSafeToast(ForgetPasswordActivity.this,head.getResponseMsg());
                    return;
                }else {
                    ToastUtils.showSafeToast(ForgetPasswordActivity.this,head.getResponseMsg());
                    getImageCodeNet();
                    return;
                }

            }

        });
    }
    //修改密码提交
    private void toLogIn() {
        String phoneNumber= etPhoneNumber.getText().toString().trim();
        String password=etNewPassword.getText().toString().trim();
        String okPassword=etOkPassword.getText().toString().trim();
        String imageCode=etImageCode.getText().toString().trim();
        String verificationCode=etVerificationCode.getText().toString().trim();
        if(TextUtils.isEmpty(phoneNumber)) {
            ToastUtils.showSafeToast(this, "手机号码不能为空");
            return;
        }else if(!RegularExpressionUtil.isMobileNO(phoneNumber)) {
            ToastUtils.showSafeToast(this, "手机号错误");
            return;
        }else if(TextUtils.isEmpty(password)) {
            ToastUtils.showSafeToast(this, "密码不能为空");
            return;
        }else if(!RegularExpressionUtil.isPassword(password)) {
            ToastUtils.showSafeToast(this,"密码须为四位以上十二位以下数字或字母");
            return;
        }else if(!okPassword.equals(password)||!password.equals(okPassword)) {
            ToastUtils.showSafeToast(this, "两次密码输入不一致");
            return;
        }else if(TextUtils.isEmpty(imageCode)) {
            ToastUtils.showSafeToast(this, "图形验证码不能为空");
            return;
        }else if(TextUtils.isEmpty(verificationCode)) {
            ToastUtils.showSafeToast(this, "短信验证码不能为空");
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("UUID", uuid);
        map.put("phoneNo",phoneNumber);
        StringBuilder strBui = new StringBuilder();
        map.put("password", MD5Util.md5(strBui.append(Constans.ACCESS_KEY).append(password).toString()).toUpperCase());
        StringBuilder strBu = new StringBuilder();
        map.put("againPassword",MD5Util.md5(strBu.append(Constans.ACCESS_KEY).append(okPassword).toString()).toUpperCase());
        map.put("picture",imageCode);
        map.put("verification",verificationCode);
        ApiCaller.call(this, Constant.URL, "0008", "authService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                LogUtil.getLogutil().d("修改密码结果是"+response);
                if(head.getResponseCode().contains("0000")) {
                    ToastUtils.showSafeToast(ForgetPasswordActivity.this,head.getResponseMsg());
                    finish();
                    return;
                }else {
                    ToastUtils.showSafeToast(ForgetPasswordActivity.this,head.getResponseMsg());
                    getImageCodeNet();
                    return;
                }
            }

        });

    }
    /*// 对字节数组字符串进行Base64解码并生成图片
    public Bitmap GenerateImage(String imgStr) {
        byte[] decode = Base64.decode(imgStr, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
        return bitmap;
    }*/

    /**
     * 获取验证码倒计时
     * @param sec  倒计时时间
     */
    private void startCounter(int sec) {
        new CountDownTimer((sec + 1) * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                int left = (int) ((millisUntilFinished - 1000) / 1000);
                getVerificationCode.setText("" + left + "S");
                getVerificationCode.setEnabled(false);
            }

            @Override
            public void onFinish() {
                getVerificationCode.setText("获取验证码");
                getVerificationCode.setEnabled(true);
            }
        }.start();
    }
}
