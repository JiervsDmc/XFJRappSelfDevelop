package com.huaxia.finance.consumer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.util.ConvertUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by mirui on 2016/12/8.
 */

public class BankCardSelectAdapter extends BaseAdapter {
    private Context mContext;
    List dataList = new ArrayList<>();
    public BankCardSelectAdapter(Context context, List list) {
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
        Map map = (Map) dataList.get(position);
        String cardNo = ConvertUtils.mapToString(map,"cardNo");
        String subCarNo = cardNo.length()>4?cardNo.substring(cardNo.length() - 4):cardNo;
        holder.tv_card.setText(ConvertUtils.mapToString(map,"bankName") +  " (" + subCarNo + " )");
        holder.iv_icon.setBackgroundResource(getDrawableByCode(ConvertUtils.mapToString(map,"bankCode")));
        return convertView;
    }

    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_card;
        ImageView iv_select;
    }

    private int getDrawableByCode(String bankCode){
        switch (bankCode){
            case "B005":
                return R.drawable.icbc;
            case "B008":
                return R.drawable.abc;
            case "B007":
                return R.drawable.boc;
            case "B013":
                return R.drawable.ceb;
            case "B012":
                return R.drawable.ccb;
            case "B019":
                return R.drawable.cgb;
            case "B021":
                return R.drawable.pab;
            case "B014":
                return R.drawable.cmbc;
            case "B006":
                return R.drawable.cj;
            case "B009":
                return R.drawable.bcm;
            case "B015":
                return R.drawable.cib;
            case "B010":
                return R.drawable.cmb;
            case "B017":
                return R.drawable.spdm;
            case "B016":
                return R.drawable.hxm;
            case "B011":
                return R.drawable.psbc;
            case "B020":
                return R.drawable.bos;
            default:
                return R.drawable.icbc;
        }
    }
}
