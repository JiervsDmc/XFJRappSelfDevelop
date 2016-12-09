package com.huaxia.finance.consumer.activity.samefunction;

import android.widget.ListView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.adapter.BankCardListAdapter;
import com.huaxia.finance.consumer.base.BaseActivity;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

/**
 * Created by wangjie01 on 2016/12/8.
 */
public class BankCard3Activity extends BaseActivity {

    @ViewInject(R.id.lv_bank_card_list)
    ListView bankCardList;

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
        bankCardList.setAdapter(new BankCardListAdapter(this,new ArrayList()));
    }

    //获取银行卡列表信息
    protected void getBankCardList() {

    }
}
