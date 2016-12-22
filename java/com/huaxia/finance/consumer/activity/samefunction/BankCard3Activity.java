package com.huaxia.finance.consumer.activity.samefunction;

import android.widget.ListView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.adapter.BankCardListAdapter;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.ToastUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by wangjie01 on 2016/12/8.
 */
public class BankCard3Activity extends BaseActivity {

    @ViewInject(R.id.lv_bank_card_list)
    ListView bankCardList;
    private String userUuid;
    private BankCardListAdapter bankAdapter;

    @Override
    protected int getLayout() {
        return R.layout.activity_bank_card_list;
    }

    @Override
    protected String getTitleText() {
        return "我的银行卡";
    }

    @Override
    protected void setup() {
        super.setup();
        userUuid = mgr.getVal(UniqueKey.APP_USER_ID);
        getBankCardList();
    }

    //获取银行卡列表信息
    protected void getBankCardList() {

            Map<String, Object> map = new HashMap<>();
            map.put("userUuid", userUuid);
            ApiCaller.call(this, Constant.URL, "0054", "appService", map, new ApiCaller.MyStringCallback(this, true, false, false, null, null) {

                @Override
                public void onError(Call call, Exception e, int id) {
                    super.onError(call, e, id);
                }

                @Override
                public void onResponse(String response, int id) {
                    super.onResponse(response, id);
                    if(head.getResponseCode().contains("0000")) {
                        ArrayList bankList = (ArrayList) body.get("bankList");
                        bankAdapter = new BankCardListAdapter(BankCard3Activity.this,bankList);
                        bankCardList.setAdapter(bankAdapter);
                    }else {
                        ToastUtils.showSafeToast(BankCard3Activity.this,head.getResponseMsg());
                    }
                }
            });
    }
}
