package com.huaxia.finance.consumer.adapter;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.http.ApiCaller;
import com.huaxia.finance.consumer.util.LogUtil;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class OrderDetailAdapter extends BaseAdapter {
    private Activity mActivity;
    private ViewHolder viewHolder;
    private List list;
    private OnItemPay itemPayListener;
    private int clickPosition;
    private boolean setClickPostion;
    public interface OnItemPay{
        void payOnline(int clickPositon,int position);
    }
    private void setClickPosition(int position){
        if(!setClickPostion){
            clickPosition = position;
        }
    }
    public OrderDetailAdapter(Activity avtivity, List list, OnItemPay listener){
        this.mActivity=avtivity;
        this.list=list;
        this.itemPayListener = listener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
            if(convertView==null) {
               convertView= LayoutInflater.from(mActivity).inflate(R.layout.layout_order_detail_item,null);
                viewHolder = new ViewHolder();
                viewHolder.tvNumberPeriods= (TextView) convertView.findViewById(R.id.tv_number_periods);
                viewHolder.tvPeriodsprice= (TextView) convertView.findViewById(R.id.tv_periods_price);
                viewHolder.tvRepaymentTime= (TextView) convertView.findViewById(R.id.tv_repayment_time);
                viewHolder.tvRepaymentState= (TextView) convertView.findViewById(R.id.tv_repayment_state);
                viewHolder.llMonthPayinteamt= (LinearLayout) convertView.findViewById(R.id.ll_month_payinteamt);
                viewHolder.llOverduePrice= (LinearLayout) convertView.findViewById(R.id.ll_overdue_price);
                viewHolder.llOtherPrice= (LinearLayout) convertView.findViewById(R.id.ll_other_price);
                viewHolder.tvMonthPrice= (TextView) convertView.findViewById(R.id.tv_month_price);
                viewHolder.tvOverduePrice= (TextView) convertView.findViewById(R.id.tv_overdue_price);
                viewHolder.tvReturnPrice= (TextView) convertView.findViewById(R.id.tv_return_price);
                viewHolder.llReplay=(RelativeLayout) convertView.findViewById(R.id.ll_replay);
                viewHolder.view=convertView.findViewById(R.id.view);
                viewHolder.view1=convertView.findViewById(R.id.view1);
                viewHolder.llAdvancePrice=(LinearLayout)convertView.findViewById(R.id.ll_advance_price);
                viewHolder.tvAdvancePrice=(TextView)convertView.findViewById(R.id.tv_advance_price);
                viewHolder.llReductionPrice=(LinearLayout)convertView.findViewById(R.id.ll_reduction_price);
                viewHolder.tvReductionPrice=(TextView)convertView.findViewById(R.id.tv_reduction_price);
                viewHolder.tv_repay_title=(TextView)convertView.findViewById(R.id.tv_repay_title);
                convertView.setTag(viewHolder);
            }else {
                viewHolder= (ViewHolder) convertView.getTag();
            }
        for (int i=position;i<list.size();i++){
            Map map=(Map)list.get(i);
            if(map.get("seqid").equals("0")) {
                viewHolder.tvNumberPeriods.setText("一次性服务费");
                viewHolder.tvPeriodsprice.setText(map.get("payinteamt").toString()+"元");
                if(Double.parseDouble(map.get("payinteamt").toString())==0) {
                    viewHolder.llReplay.setVisibility(View.GONE);
                    viewHolder.view1.setVisibility(View.GONE);
                }else {
                    viewHolder.llReplay.setVisibility(View.VISIBLE);
                    viewHolder.view1.setVisibility(View.VISIBLE);
                if(map.get("status").toString().equals("010")||map.get("status").toString().equals("050")) {
                    viewHolder.tv_repay_title.setVisibility(View.GONE);
                    viewHolder.tvRepaymentState.setText("已还清");
                    viewHolder.tvRepaymentState.setTextColor(mActivity.getResources().getColor(R.color.app_pass_color));
                    viewHolder.tvRepaymentState.setBackgroundResource(R.drawable.pay_bg_null);
                    viewHolder.tvRepaymentState.setClickable(false);
                    viewHolder.tvRepaymentTime.setText(map.get("finishdate").toString());
                    viewHolder.tvRepaymentTime.setTextColor(mActivity.getResources().getColor(R.color.app_hint_text_color));
                }else {
                    setClickPosition(position);
                    setClickPostion = true;
                    viewHolder.tv_repay_title.setVisibility(View.VISIBLE);
                    viewHolder.tvRepaymentState.setText("在线还款");
                    viewHolder.tvRepaymentTime.setText(map.get("paydate").toString().replace("/","-"));
                    viewHolder.tvRepaymentTime.setTextColor(mActivity.getResources().getColor(R.color.app_hint_text_color));
                    viewHolder.tvRepaymentState.setTextColor(mActivity.getResources().getColor(R.color.app_blue_color));
                    viewHolder.tvRepaymentState.setBackgroundResource(R.drawable.pay_bg);
                    viewHolder.tvRepaymentState.setClickable(true);
                    viewHolder.tvRepaymentState.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemPayListener.payOnline(clickPosition,position);
                        }
                    });
                }}
                viewHolder.view.setVisibility(View.GONE);
                viewHolder.llAdvancePrice.setVisibility(View.GONE);
                viewHolder.llReductionPrice.setVisibility(View.GONE);
                viewHolder.llMonthPayinteamt.setVisibility(View.GONE);
                viewHolder.llOverduePrice.setVisibility(View.GONE);
                viewHolder.llOtherPrice.setVisibility(View.GONE);
                break;
            }else {
                viewHolder.tvNumberPeriods.setText(map.get("seqid").toString()+"/"+(list.size()-1)+"期");
                viewHolder.tvPeriodsprice.setText(map.get("payamt").toString()+"元");
                if(map.get("status").toString().equals("010")||map.get("status").toString().equals("050")) {
                    viewHolder.tv_repay_title.setVisibility(View.GONE);
                    viewHolder.llOtherPrice.setVisibility(View.VISIBLE);
                    viewHolder.tvRepaymentState.setText("已还清");
                    viewHolder.tvRepaymentTime.setText(map.get("finishdate").toString());
                    viewHolder.tvRepaymentTime.setTextColor(mActivity.getResources().getColor(R.color.app_hint_text_color));
                    viewHolder.tvRepaymentState.setTextColor(mActivity.getResources().getColor(R.color.app_pass_color));
                    viewHolder.tvRepaymentState.setBackgroundResource(R.drawable.pay_bg_null);
                    viewHolder.tvRepaymentState.setClickable(false);
                   // viewHolder.tvReturnPrice.setText(String.valueOf(new BigDecimal(map.get("actualpaycorpusamt").toString()).add(new BigDecimal(map.get("actualpayinteamt").toString())).add(new BigDecimal(map.get("actualfineamt").toString())).setScale(2, BigDecimal.ROUND_HALF_UP))+"元");
                    viewHolder.tvReturnPrice.setText(map.get("actualpayamt").toString());
                    if(Double.parseDouble(map.get("payfineamt").toString())==0) {
                        viewHolder.llOverduePrice.setVisibility(View.GONE);
                    }else {
                        viewHolder.llOverduePrice.setVisibility(View.VISIBLE);
                        viewHolder.tvOverduePrice.setText(map.get("payfineamt").toString()+"元");
                        viewHolder.tvOverduePrice.setTextColor(mActivity.getResources().getColor(R.color.app_hint_text_color));
                    }
                    if(Double.parseDouble(map.get("payinteamt").toString())==0){
                        viewHolder.llMonthPayinteamt.setVisibility(View.GONE);
                        viewHolder.view.setVisibility(View.GONE);
                    }else {
                        viewHolder.view.setVisibility(View.VISIBLE);
                        viewHolder.llMonthPayinteamt.setVisibility(View.VISIBLE);
                        viewHolder.tvMonthPrice.setText(map.get("payinteamt").toString()+"元");
                        viewHolder.tvMonthPrice.setTextColor(mActivity.getResources().getColor(R.color.app_hint_text_color));
                    }

                    if(Double.parseDouble(map.get("payfeeamt5").toString())==0) {
                        viewHolder.llAdvancePrice.setVisibility(View.GONE);
                    }else {
                        viewHolder.view.setVisibility(View.VISIBLE);
                        viewHolder.llAdvancePrice.setVisibility(View.VISIBLE);
                        viewHolder.tvAdvancePrice.setText(map.get("payfeeamt5").toString()+"元");
                        viewHolder.tvAdvancePrice.setTextColor(mActivity.getResources().getColor(R.color.app_hint_text_color));
                    }
                    if(Double.parseDouble(map.get("applyreducesum").toString())==0) {
                        viewHolder.llReductionPrice.setVisibility(View.GONE);
                    }else {
                        viewHolder.view.setVisibility(View.VISIBLE);
                        viewHolder.llReductionPrice.setVisibility(View.VISIBLE);
                        viewHolder.tvReductionPrice.setText(map.get("applyreducesum").toString()+"元");
                        viewHolder.tvReductionPrice.setTextColor(mActivity.getResources().getColor(R.color.app_hint_text_color));
                    }
                }else {
                    setClickPosition(position);
                    setClickPostion = true;
                    viewHolder.tv_repay_title.setVisibility(View.VISIBLE);
                    viewHolder.tvRepaymentState.setText("在线还款");
                    viewHolder.tvRepaymentTime.setText(map.get("paydate").toString().replace("/","-"));
                    viewHolder.tvRepaymentTime.setTextColor(mActivity.getResources().getColor(R.color.app_hint_text_color));
                    viewHolder.tvRepaymentState.setTextColor(mActivity.getResources().getColor(R.color.app_blue_color));
                    viewHolder.tvRepaymentState.setBackgroundResource(R.drawable.pay_bg);
                    viewHolder.tvRepaymentState.setClickable(true);
                    viewHolder.tvRepaymentState.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            itemPayListener.payOnline(clickPosition,position);
                        }
                    });
                    //String s=String.valueOf(new BigDecimal(map.get("actualpaycorpusamt").toString()).add(new BigDecimal(map.get("actualpayinteamt").toString())).add(new BigDecimal(map.get("actualfineamt").toString())).setScale(2, BigDecimal.ROUND_HALF_UP));
                    if(Double.parseDouble(map.get("actualpayamt").toString())==0) {
                        viewHolder.llOtherPrice.setVisibility(View.GONE);
                    }else {
                        viewHolder.llOtherPrice.setVisibility(View.VISIBLE);
                       // viewHolder.tvReturnPrice.setText(s+"元");
                        viewHolder.tvReturnPrice.setText(map.get("actualpayamt").toString());
                    }
                    if(Double.parseDouble(map.get("payinteamt").toString())==0){
                        viewHolder.llMonthPayinteamt.setVisibility(View.GONE);
                        viewHolder.view.setVisibility(View.GONE);
                    }else {
                        viewHolder.view.setVisibility(View.VISIBLE);
                        viewHolder.llMonthPayinteamt.setVisibility(View.VISIBLE);
                        viewHolder.tvMonthPrice.setText(map.get("payinteamt").toString()+"元");
                        viewHolder.tvMonthPrice.setTextColor(mActivity.getResources().getColor(R.color.app_hint_text_color));
                    }
                    if(Double.parseDouble(map.get("payfineamt").toString())==0) {
                        viewHolder.llOverduePrice.setVisibility(View.GONE);
                    }else {
                        viewHolder.view.setVisibility(View.VISIBLE);
                        viewHolder.llOverduePrice.setVisibility(View.VISIBLE);
                        viewHolder.tvOverduePrice.setText(map.get("payfineamt").toString()+"元");
                        viewHolder.tvOverduePrice.setTextColor(mActivity.getResources().getColor(R.color.app_hint_text_color));
                    }
                    if(Double.parseDouble(map.get("payfeeamt5").toString())==0) {
                        viewHolder.llAdvancePrice.setVisibility(View.GONE);
                    }else {
                        viewHolder.view.setVisibility(View.VISIBLE);
                        viewHolder.llAdvancePrice.setVisibility(View.VISIBLE);
                        viewHolder.tvAdvancePrice.setText(map.get("payfeeamt5").toString()+"元");
                        viewHolder.tvAdvancePrice.setTextColor(mActivity.getResources().getColor(R.color.app_hint_text_color));
                    }
                    if(Double.parseDouble(map.get("applyreducesum").toString())==0) {
                        viewHolder.llReductionPrice.setVisibility(View.GONE);
                    }else {
                        viewHolder.view.setVisibility(View.VISIBLE);
                        viewHolder.llReductionPrice.setVisibility(View.VISIBLE);
                        viewHolder.tvReductionPrice.setText(map.get("applyreducesum").toString()+"元");
                        viewHolder.tvReductionPrice.setTextColor(mActivity.getResources().getColor(R.color.app_hint_text_color));
                    }
                }
            }
            break;
        }
        return convertView;
    }
    static class ViewHolder {
        TextView tvNumberPeriods;
        TextView tvPeriodsprice;
        TextView tvRepaymentTime;
        TextView tvRepaymentState;
        LinearLayout llMonthPayinteamt;
        LinearLayout llOverduePrice;
        LinearLayout llOtherPrice;
        RelativeLayout llReplay;
        TextView tvMonthPrice;
        TextView tvOverduePrice;
        TextView tvReturnPrice;
        View view;
        View view1;
        LinearLayout llAdvancePrice;
        TextView tvAdvancePrice;
        LinearLayout llReductionPrice;
        TextView tvReductionPrice;
        TextView tv_repay_title;
    }
}
