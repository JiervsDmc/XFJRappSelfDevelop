package com.huaxia.finance.consumer.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;

import java.util.List;

/**
 * Created by wangjie01 on 2016/12/8.
 */
public class BankCardListAdapter extends BaseAdapter {

    private Activity mActivity;
    private List list;
    private ViewHolder viewHolder;

    public BankCardListAdapter(Activity mActivity,List list) {
        this.mActivity = mActivity;
        this.list = list;
    }

    @Override
    public int getCount() {
        //return list.size();
        return 4;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = View.inflate(mActivity, R.layout.item_bank_card_list,null);
            viewHolder.bankNumber = (TextView) convertView.findViewById(R.id.tv_bank_name);
            viewHolder.llBank = (LinearLayout) convertView.findViewById(R.id.ll_bank);
            viewHolder.ivBank = (ImageView) convertView.findViewById(R.id.iv_bank);
            viewHolder.tvBankName = (TextView) convertView.findViewById(R.id.tv_bank_name);
            viewHolder.tvXing = (TextView) convertView.findViewById(R.id.tv_xing);
            viewHolder.isOnlineCard = (TextView) convertView.findViewById(R.id.is_onlineCard);
            convertView.setTag(convertView);
        }else {
            convertView.getTag();
        }
        //根据请求到的银行卡数据加载列表


        return convertView;
    }

    class ViewHolder {
        ImageView ivBank;
        TextView bankNumber;
        LinearLayout llBank;
        TextView tvBankName;
        TextView tvXing;
        TextView isOnlineCard;
    }
}
