package com.huaxia.finance.consumer.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;

import java.util.HashMap;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder = new ViewHolder();
        if (convertView == null) {
            convertView = View.inflate(mActivity, R.layout.item_bank_card_list,null);
            viewHolder.bankNumber = (TextView) convertView.findViewById(R.id.bank_number);
            viewHolder.llBank = (LinearLayout) convertView.findViewById(R.id.ll_bank);
            viewHolder.ivBank = (ImageView) convertView.findViewById(R.id.iv_bank);
            viewHolder.tvBankName = (TextView) convertView.findViewById(R.id.tv_bank_name);
            viewHolder.tvXing = (TextView) convertView.findViewById(R.id.tv_xing);
            viewHolder.isOnlineCard = (TextView) convertView.findViewById(R.id.is_onlineCard);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //根据请求到的银行卡数据加载列表
        HashMap bankCardMap = (HashMap) list.get(position);
        if (bankCardMap!= null) {
            viewHolder.tvBankName.setText(bankCardMap.get("bankName").toString());
            viewHolder.tvXing.setVisibility(View.VISIBLE);
            String cardNo = bankCardMap.get("cardNo").toString();
            String TailNo = cardNo.substring(cardNo.length()-4,cardNo.length());
            viewHolder.bankNumber.setText(TailNo);
            if (bankCardMap.get("bindType").toString().equals("BT02")) {
                //民生绑定
                viewHolder.isOnlineCard.setVisibility(View.VISIBLE);
            }else {
                //宝付绑定
                viewHolder.isOnlineCard.setVisibility(View.GONE);
            }
            viewHolder.llBank.setBackgroundResource(R.drawable.bank_red);
            viewHolder.isOnlineCard.setTextColor(mActivity.getResources().getColor(R.color.app_red));
            setBankLogo(bankCardMap.get("bankName").toString());
        }
        return convertView;
    }

    public void setBankLogo(String bankName) {
        if(bankName.contains("中国工商银行")) {
            viewHolder.ivBank.setBackgroundResource(R.drawable.icbc);
        }else if(bankName.contains("招商银行")) {
            viewHolder.ivBank.setBackgroundResource(R.drawable.cmb);
        }else if(bankName.contains("中国农业银行")) {
            viewHolder.ivBank.setBackgroundResource(R.drawable.abc);
        }else if(bankName.contains("广东发展银行")) {
            viewHolder.ivBank.setBackgroundResource(R.drawable.cgb);
        }else if(bankName.contains("中信银行")) {
            viewHolder.ivBank.setBackgroundResource(R.drawable.ccb);
        }else if(bankName.contains("中国银行")) {
            viewHolder.ivBank.setBackgroundResource(R.drawable.boc);
        }else if(bankName.contains("中国建设银行")) {
            viewHolder.ivBank.setBackgroundResource(R.drawable.cj);
        }else if(bankName.contains("中国光大银行")) {
            viewHolder.ivBank.setBackgroundResource(R.drawable.ceb);
        }else if(bankName.contains("中国民生银行")) {
            viewHolder.ivBank.setBackgroundResource(R.drawable.cmbc);
        }else if(bankName.contains("平安银行")) {
            viewHolder.ivBank.setBackgroundResource(R.drawable.pab);
        }else if(bankName.contains("兴业银行")) {
            viewHolder.ivBank.setBackgroundResource(R.drawable.cib);
        }
        else if(bankName.contains("交通银行")) {
            viewHolder.ivBank.setBackgroundResource(R.drawable.bcm);
        }else if(bankName.contains("华夏银行")) {
            viewHolder.ivBank.setBackgroundResource(R.drawable.hxm);
        }else if(bankName.contains("上海浦东发展银行")) {
            viewHolder.ivBank.setBackgroundResource(R.drawable.spdm);
        }else if(bankName.contains("上海银行")) {
            viewHolder.ivBank.setBackgroundResource(R.drawable.bos);
        }else if(bankName.contains("邮储银行")) {
            viewHolder.ivBank.setBackgroundResource(R.drawable.psbc);
        }
        return;
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
