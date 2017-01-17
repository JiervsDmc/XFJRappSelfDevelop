package com.huaxia.finance.consumer.activity;


import android.widget.TextView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.ConvertUtils;
import com.huaxia.finance.consumer.util.ToastUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by wangjie01 on 2016/12/8.
 */
public class RepayDetailActivity extends BaseActivity {

    @ViewInject(R.id.tv_detail_price)
    TextView tv_detail_price;
    @ViewInject(R.id.tv_detail_result)
    TextView tv_detail_result;
    @ViewInject(R.id.tv_project_info)
    TextView tv_project_info;
    @ViewInject(R.id.tv_create_time)
    TextView tv_create_time;
    @ViewInject(R.id.tv_repay_bank)
    TextView tv_repay_bank;
    @ViewInject(R.id.order_no)
    TextView order_no;
    @ViewInject(R.id.serial_no)
    TextView serial_no;
    @ViewInject(R.id.tv_repay_time)
    TextView tv_repay_time;
    private String userUuid;
    private String flowNo;


    @Override
    protected int getLayout() {
        return R.layout.activity_repay_online_detail;
    }

    @Override
    protected String getTitleText() {
        return "还款详情";
    }

    @Override
    protected void setup() {
        super.setup();
        userUuid = mgr.getVal(UniqueKey.APP_USER_ID);
        flowNo = getIntent().getStringExtra("flowNo");
        //根据数据完成在线还款详情页面
        getOnlineRepayDetail();
    }

    //获取在线还款记录
    protected void getOnlineRepayDetail() {
        Map<String, Object> map = new HashMap<>();
        map.put("userUuid", userUuid);
        map.put("flowNo", flowNo);
        ApiCaller.call(this, Constant.URL, "0053", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                if(head.getResponseCode().contains("0000")) {
                    HashMap detailMap = (HashMap) body.get("record");
                    String repayMoney = ConvertUtils.mapToString(detailMap,"repayMoney");
                    tv_detail_price.setText(new BigDecimal(repayMoney).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                    tv_project_info.setText(ConvertUtils.mapToString(detailMap,"productName"));
                    tv_create_time.setText(ConvertUtils.mapToString(detailMap,"createdAt"));
                    String cardNo = ConvertUtils.mapToString(detailMap,"cardNo");
                    String tailNumber = null;
                    if (cardNo.length()>=4) {
                     tailNumber = cardNo.substring(cardNo.length()-4,cardNo.length());
                    }
                    tv_repay_bank.setText(ConvertUtils.mapToString(detailMap,"bankName")+" ("+tailNumber+")");
                    order_no.setText(ConvertUtils.mapToString(detailMap,"orderNo"));
                    serial_no.setText(ConvertUtils.mapToString(detailMap,"flowNo"));
                    tv_repay_time.setText(ConvertUtils.mapToString(detailMap,"payTime"));
                    setRepayResult(ConvertUtils.mapToString(detailMap,"repayStatus"));
                }else {
                    ToastUtils.showSafeToast(RepayDetailActivity.this,head.getResponseMsg());
                }
            }
        });
    }

    //设置支付结果
    public void setRepayResult(String repayResult) {

        switch(repayResult) {
            case "RS01" :
                tv_detail_result.setText("待还款");
                break;
            case "RS02" :
                tv_detail_result.setText("支付成功");
                break;
            case "RS03" :
                tv_detail_result.setText("支付失败");
                break;
            case "RS04" :
                tv_detail_result.setText("还款处理中");
                break;
            case "RS05" :
                tv_detail_result.setText("交易关闭");
                break;
            case "RS06" :
                tv_detail_result.setText("其他状态");
                break;
        }
    }
}
