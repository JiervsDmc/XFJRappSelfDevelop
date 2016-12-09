package com.huaxia.finance.consumer.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.activity.RoleChangeActivity;
import com.huaxia.finance.consumer.activity.samefunction.UserInfoActivity;
import com.huaxia.finance.consumer.base.BaseFragment;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.AccountMgr;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.DialogUtil;
import com.huaxia.finance.consumer.util.IsNullUtils;
import com.huaxia.finance.consumer.util.LogUtil;
import com.huaxia.finance.consumer.util.ToastUtils;
import com.huaxia.finance.consumer.util.Utils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * 一分期主页
 * Created by lipiao on 2016/7/8.
 */
public class ApplyFragment extends BaseFragment implements View.OnClickListener {

    private LinearLayout apply;
    private LinearLayout nextMonthReturn;
    private Intent intent;
    private AccountMgr mgr;
    private TextView tvApply;
    private TextView tvNextTime;
    private TextView tvNextPrice;
    private TextView tvSurplus;
    private ImageView ivImageView;
    private Bundle bundleA;
    //是否去申请
    private boolean isApply = false;

    private OnButtonOrder buttonOrder;

    public void setButton(OnButtonOrder buttonOrder) {
        this.buttonOrder = buttonOrder;
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_student_apply;
    }

    @Override
    protected String getTitleText() {
        return "华夏壹分期";
    }

    @Override
    protected void setup() {
        super.setup();
        mgr = new AccountMgr(getActivity());
        if (!Utils.isNetworkAvailable(getActivity())) {
            apply.setVisibility(View.GONE);
            tvSurplus.setText("网络故障");
            nextMonthReturn.setClickable(false);
            return;
        } else {
            getOrder();
        }
        bundleA = getArguments();
        apply.setOnClickListener(this);
        nextMonthReturn.setOnClickListener(this);
    }

    @Override
    protected void findViews(View view) {
        super.findViews(view);
        apply = (LinearLayout) view.findViewById(R.id.apply_installment);
        nextMonthReturn = (LinearLayout) view.findViewById(R.id.next_month_return);
        tvApply = (TextView) view.findViewById(R.id.tv_apply);
        tvNextTime = (TextView) view.findViewById(R.id.tv_next_time);
        tvNextPrice = (TextView) view.findViewById(R.id.tv_next_price);
        tvSurplus = (TextView) view.findViewById(R.id.tv_surplus);
        ivImageView = (ImageView) view.findViewById(R.id.iv_imageView);
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        switch (view.getId()) {
            case R.id.apply_installment:
                //todo：是否已经借款？需要判断
                if (isApply == true) {
                    Cursor cursor = getActivity().getContentResolver().query(ContactsContract.Data.CONTENT_URI, null, null, null, ContactsContract.Data.RAW_CONTACT_ID);
                    if (cursor.moveToFirst() == false) {
                        DialogUtil.inform(getActivity(), "读取联系人被拒绝，现在去设置?", new DialogInterface.OnClickListener() {
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
                        return;
                    }
                    if (!IsNullUtils.isNull("" + bundleA.getString("role")) && (!bundleA.getString("role").equals("null"))) {//已经有身份
                        intent = new Intent(getActivity(), UserInfoActivity.class);
                        intent.putExtra("role", bundleA.getString("role"));
                    } else {//没有设置身份
                        intent = new Intent(getActivity(), RoleChangeActivity.class);
                    }
                    startActivity(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setAction("com.huaxia.finance.broadcast.toorder");
                    getActivity().sendBroadcast(intent);
                    buttonOrder.onButtonOrder();
                }
               /* Intent intent = new Intent(getActivity(), PreOrder1Activity.class);
                startActivity(intent);*/
                //todo：fragment跳转接口
//                buttonOrder.onButtonOrder();

                break;

            case R.id.next_month_return:
                /*intent = new Intent(getActivity(), OrderDetailActivity.class);
                startActivity(intent);*/
                buttonOrder.onButtonOrder();
                break;
        }
    }


    @Override
    protected boolean showBtnLeft() {
        return false;
    }

    //获取首页信息
    private void getOrder() {
        Map<String, Object> body = new HashMap<>();
        body.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        ApiCaller.call(getActivity(), Constant.URL, "0034", "appService", body, new ApiCaller.MyStringCallback(getActivity(), true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                apply.setVisibility(View.GONE);
                tvSurplus.setText("网络故障");
                nextMonthReturn.setClickable(false);

            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                LogUtil.getLogutil().d("首页信息结果是" + response);
                if (head.getResponseCode().contains("0000") && !body.isEmpty()) {
                    if (body.get("mayApply").toString().contains("0")) {
                        tvApply.setText("查看详情");
                        apply.setVisibility(View.VISIBLE);
                        tvSurplus.setText(body.get("remainedMoney").toString());
                        isApply = false;
                        if (!IsNullUtils.isNull((String) body.get("nextRepayDate"))) {
                            tvNextTime.setText(body.get("nextRepayDate").toString() + "还款");
                            tvNextPrice.setText(new BigDecimal((String) body.get("nextPayMoney")).setScale(2, BigDecimal.ROUND_HALF_UP) + "元");
                            nextMonthReturn.setClickable(true);
                            return;
                        } else {
                            tvNextTime.setText("");
                            tvNextPrice.setText("0.00" + "元");
                            nextMonthReturn.setClickable(false);
                        }
                    } else if (body.get("mayApply").toString().contains("1")) {
                        apply.setVisibility(View.VISIBLE);
                        tvSurplus.setText("0.00");
                        tvNextTime.setText("");
                        tvNextPrice.setText("0.00元");
                        tvApply.setText("申请分期");
                        nextMonthReturn.setClickable(false);
                        isApply = true;
                    }

                } else {
                    apply.setVisibility(View.GONE);
                    tvSurplus.setText("网络故障");
                    nextMonthReturn.setClickable(false);
                    ToastUtils.showSafeToast(getActivity(), head.getResponseMsg());
                }
            }

        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (!Utils.isNetworkAvailable(getActivity())) {
                apply.setVisibility(View.GONE);
                tvSurplus.setText("网络故障");
                nextMonthReturn.setClickable(false);
                return;
            } else {
                getOrder();
            }
        }
    }

    /**
     * fragment之间跳转接口
     */
    public interface OnButtonOrder {
        void onButtonOrder();
    }
}
