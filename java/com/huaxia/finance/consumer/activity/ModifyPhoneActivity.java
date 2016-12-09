package com.huaxia.finance.consumer.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.activity.samefunction.IdentityAuthenticationActivity;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.util.RegularExpressionUtil;
import com.huaxia.finance.consumer.util.ToastUtils;
import com.huaxia.finance.consumer.view.ClearEditText;
import com.lidroid.xutils.view.annotation.ViewInject;

public class ModifyPhoneActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.et_new_number)
    private ClearEditText etNewNumber;
    @ViewInject(R.id.et_image_code)
    private ClearEditText etImageCode;
    @ViewInject(R.id.et_verification_code)
    private ClearEditText etVerificationCode;
    @ViewInject(R.id.order_next)
    private TextView order_next;
    @ViewInject(R.id.get_image_code)
    private ImageView getImageCode;
    @ViewInject(R.id.tv_get_verification_code)
    private TextView tvGetVerificationCode;

    @Override
    protected int getLayout() {
        return R.layout.activity_modify_phone;
    }

    @Override
    protected String getTitleText() {
        return "修改手机号";
    }

    @Override
    protected void setup() {
        super.setup();
        order_next.setOnClickListener(this);
        getImageCode.setOnClickListener(this);
        tvGetVerificationCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.tv_get_verification_code:
                if (!RegularExpressionUtil.isMobileNO(etNewNumber.getText().toString())) {
                    ToastUtils.showSafeToast(this,"请输入正确的手机号码");
                    return;
                }
                if (TextUtils.isEmpty(etNewNumber.getText().toString())) {
                    ToastUtils.showSafeToast(this, "手机号码为空");
                    return;
                }
                startCounter(60);
                break;
            case R.id.get_image_code:

                break;

            case R.id.order_next:
                Intent intent = new Intent(this, IdentityAuthenticationActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 获取验证码倒计时
     * @param sec  倒计时时间
     */
    private void startCounter(int sec) {
        new CountDownTimer((sec + 1) * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                int left = (int) ((millisUntilFinished - 1000) / 1000);
                tvGetVerificationCode.setText("" + left + "S");
                tvGetVerificationCode.setEnabled(false);
            }

            @Override
            public void onFinish() {
                tvGetVerificationCode.setText("获取验证码");
                tvGetVerificationCode.setEnabled(true);
            }
        }.start();
    }
}
