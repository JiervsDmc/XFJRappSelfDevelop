package com.huaxia.finance.consumer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.bean.CardSelectBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mirui on 2016/12/8.
 */

public class BankCardSelectAdapter extends BaseAdapter {
    private Context mContext;
    List<CardSelectBean> dataList = new ArrayList<>();

    public BankCardSelectAdapter(Context context, List<CardSelectBean> list) {
        mContext = context;
        dataList = list;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder ;
        if(convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_card_select_item,null);
            holder = new ViewHolder();
            holder.tv_card = (TextView) convertView.findViewById(R.id.tv_card);
            holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.iv_select = (ImageView) convertView.findViewById(R.id.iv_select);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        CardSelectBean cardSelectBean = dataList.get(position);
        switch (cardSelectBean.getSelectType().toString()){
            case "0":
                holder.iv_select.setVisibility(View.VISIBLE);
                holder.iv_select.setImageResource(R.drawable.jiantou_right);
                break;
            case "1":
                holder.iv_select.setVisibility(View.VISIBLE);
                holder.iv_select.setImageResource(R.drawable.cert_selected);
                break;
            case "2":
                holder.iv_select.setVisibility(View.GONE);
                break;
        }
        holder.tv_card.setText(dataList.get(position).getCardName());
        return convertView;
    }

    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_card;
        ImageView iv_select;
    }
}
