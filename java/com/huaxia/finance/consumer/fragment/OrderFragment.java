package com.huaxia.finance.consumer.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.activity.RoleChangeActivity;
import com.huaxia.finance.consumer.activity.samefunction.UserInfoActivity;
import com.huaxia.finance.consumer.adapter.OrderAdapter;
import com.huaxia.finance.consumer.base.BaseFragment;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.storage.AccountMgr;
import com.huaxia.finance.consumer.storage.Constant;
import com.huaxia.finance.consumer.storage.UniqueKey;
import com.huaxia.finance.consumer.util.IsNullUtils;
import com.huaxia.finance.consumer.util.LogUtil;
import com.huaxia.finance.consumer.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by lipiao on 2016/7/8.
 */
public class OrderFragment extends BaseFragment implements View.OnClickListener {
    private ListView LvOrder;
    private AccountMgr mgr;
    private LinearLayout orderYes;
    private LinearLayout orderNo;
    private TextView toSubject;
    private LinearLayout orderNoPass;
    private TextView toReload;
    private Bundle bundleA;
    private MyInfoReciver receiver;

    @Override
    protected int getLayout() {
        return R.layout.fragment_student_order;
    }

    @Override
    protected String getTitleText() {
        return "订单";
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        Intent intent = null;
        switch (view.getId()) {
            case R.id.to_subject:
                if (!IsNullUtils.isNull(bundleA.getString("role"))&&(!bundleA.getString("role").equals("null"))) {//已经有身份
                    intent = new Intent(getActivity(), UserInfoActivity.class);
                    intent.putExtra("role", bundleA.getString("role"));
                } else {//没有设置身份
                    intent = new Intent(getActivity(), RoleChangeActivity.class);
                }
                startActivity(intent);
                break;
            case R.id.to_reload:
                getOrder();
                break;
        }
    }

    @Override
    protected boolean showBtnLeft() {
        return false;
    }

    @Override
    protected void findViews(View view) {
        super.findViews(view);
        LvOrder = (ListView) view.findViewById(R.id.lv_order);
        orderYes = (LinearLayout) view.findViewById(R.id.order_yes);
        orderNo = (LinearLayout) view.findViewById(R.id.order_no);
        toSubject = (TextView) view.findViewById(R.id.to_subject);
        orderNoPass = (LinearLayout) view.findViewById(R.id.order_no_pass);
        toReload = (TextView) view.findViewById(R.id.to_reload);
    }

    @Override
    protected void setup() {
        super.setup();
        regiestBroadCast();
        bundleA = getArguments();
        toReload.setOnClickListener(this);
        mgr = new AccountMgr(getActivity());
        if (!Utils.isNetworkAvailable(getActivity())) {
            orderYes.setVisibility(View.GONE);
            orderNo.setVisibility(View.GONE);
            orderNoPass.setVisibility(View.VISIBLE);
            return;
        } else {
            getOrder();
        }
        toSubject.setOnClickListener(this);
    }
    private void regiestBroadCast(){
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.huaxia.finance.broadcast.loadcomplete");
        receiver = new MyInfoReciver();
        getActivity().registerReceiver(receiver, filter);
    }

    public class MyInfoReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            getOrder();
        }
    }

    //获取订单列表
    private void getOrder() {
        Map<String, Object> map = new HashMap<>();
        map.put("userUuid", mgr.getVal(UniqueKey.APP_USER_ID));
        ApiCaller.call(getActivity(), Constant.URL, "0004", "appService", map, new ApiCaller.MyStringCallback(getActivity(), true, false, false, null, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                orderYes.setVisibility(View.GONE);
                orderNo.setVisibility(View.GONE);
                orderNoPass.setVisibility(View.VISIBLE);

            }

            @Override
            public void onResponse(String response, int id) {
                super.onResponse(response, id);
                LogUtil.getLogutil().d("列表结果是" + response);
                if (head.getResponseCode().contains("0000") && !body.isEmpty()) {
                    orderYes.setVisibility(View.VISIBLE);
                    orderNo.setVisibility(View.GONE);
                    orderNoPass.setVisibility(View.GONE);
                    List orderlist = new ArrayList();
                    orderlist = (List) body.get("orderList");
                    if (orderlist.size() != 0) {
                        OrderAdapter adaptor = new OrderAdapter(getActivity(), orderlist);
                        LvOrder.setAdapter(adaptor);
                        adaptor.notifyDataSetChanged();
                    } else {
                        orderYes.setVisibility(View.GONE);
                        orderNo.setVisibility(View.VISIBLE);
                        orderNoPass.setVisibility(View.GONE);
                    }
                } else {
                    orderYes.setVisibility(View.GONE);
                    orderNo.setVisibility(View.GONE);
                    orderNoPass.setVisibility(View.VISIBLE);
                }
                return;
            }

        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            if (!Utils.isNetworkAvailable(getActivity())) {
                orderYes.setVisibility(View.GONE);
                orderNo.setVisibility(View.GONE);
                orderNoPass.setVisibility(View.VISIBLE);
                return;
            } else {
                getOrder();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(receiver != null){
            getActivity().unregisterReceiver(receiver);
            receiver = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getOrder();
    }
}
