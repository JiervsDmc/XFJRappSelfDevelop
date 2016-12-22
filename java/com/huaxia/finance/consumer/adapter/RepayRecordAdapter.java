package com.huaxia.finance.consumer.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wangjie01 on 2016/12/8.
 */
public class RepayRecordAdapter extends BaseAdapter {

    private Activity mActivity;
    private List list;
    private ViewHolder viewHolder;
    private HashMap recordMap;

    public RepayRecordAdapter(Activity mActivity,List list) {
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
            convertView = View.inflate(mActivity, R.layout.item_repay_record_list,null);
            viewHolder.iv_bank_logo = (ImageView) convertView.findViewById(R.id.iv_bank_logo);
            viewHolder.tv_bank_tailNumber = (TextView) convertView.findViewById(R.id.tv_bank_tailNumber);
            viewHolder.tv_repay_time = (TextView) convertView.findViewById(R.id.tv_repay_time);
            viewHolder.tv_repay_result = (TextView) convertView.findViewById(R.id.tv_repay_result);
            viewHolder.tv_project = (TextView) convertView.findViewById(R.id.tv_project);
            viewHolder.tv_online_price = (TextView) convertView.findViewById(R.id.tv_online_price);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //根据得到的列表数据加载每一条支付记录
        recordMap = (HashMap) list.get(position);
        if (recordMap!= null) {
            String bankName = recordMap.get("bankName").toString();
            String cardNo = recordMap.get("cardNo").toString();
            String tailNumber = cardNo.substring(cardNo.length()-4,cardNo.length());
            viewHolder.tv_bank_tailNumber.setText(bankName+"("+tailNumber+")");
            viewHolder.tv_repay_time.setText(recordMap.get("payTime").toString());
            viewHolder.tv_project.setText(recordMap.get("productName").toString());
            String repayMoney = recordMap.get("repayMoney").toString();
            viewHolder.tv_online_price.setText("¥ "+new BigDecimal(repayMoney).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            String repayResult = recordMap.get("repayStatus").toString();
            setRepayResult(repayResult);
            setBankLogo(bankName);
        }

        return convertView;
    }

    //设置银行logo
    public void setBankLogo(String bankName) {
        if(bankName.contains("中国工商银行")) {
            viewHolder.iv_bank_logo.setBackgroundResource(R.drawable.icbc);
        }else if(bankName.contains("招商银行")) {
            viewHolder.iv_bank_logo.setBackgroundResource(R.drawable.cmb);
        }else if(bankName.contains("中国农业银行")) {
            viewHolder.iv_bank_logo.setBackgroundResource(R.drawable.abc);
        }else if(bankName.contains("广东发展银行")) {
            viewHolder.iv_bank_logo.setBackgroundResource(R.drawable.cgb);
        }else if(bankName.contains("中信银行")) {
            viewHolder.iv_bank_logo.setBackgroundResource(R.drawable.ccb);
        }else if(bankName.contains("中国银行")) {
            viewHolder.iv_bank_logo.setBackgroundResource(R.drawable.boc);
        }else if(bankName.contains("中国建设银行")) {
            viewHolder.iv_bank_logo.setBackgroundResource(R.drawable.cj);
        }else if(bankName.contains("中国光大银行")) {
            viewHolder.iv_bank_logo.setBackgroundResource(R.drawable.ceb);
        }else if(bankName.contains("中国民生银行")) {
            viewHolder.iv_bank_logo.setBackgroundResource(R.drawable.cmbc);
        }else if(bankName.contains("平安银行")) {
            viewHolder.iv_bank_logo.setBackgroundResource(R.drawable.pab);
        }else if(bankName.contains("兴业银行")) {
            viewHolder.iv_bank_logo.setBackgroundResource(R.drawable.cib);
        }else if(bankName.contains("交通银行")) {
            viewHolder.iv_bank_logo.setBackgroundResource(R.drawable.bcm);
        }else if(bankName.contains("华夏银行")) {
            viewHolder.iv_bank_logo.setBackgroundResource(R.drawable.hxm);
        }else if(bankName.contains("上海浦东发展银行")) {
            viewHolder.iv_bank_logo.setBackgroundResource(R.drawable.spdm);
        }else if(bankName.contains("上海银行")) {
        viewHolder.iv_bank_logo.setBackgroundResource(R.drawable.bos);
        }else if(bankName.contains("邮储银行")) {
        viewHolder.iv_bank_logo.setBackgroundResource(R.drawable.psbc);
    }
        return;
    }

    //设置支付结果
    public void setRepayResult(String repayResult) {

        switch(repayResult) {
            case "RS01" :
                viewHolder.tv_repay_result.setText("待还款");
                viewHolder.tv_repay_result.setTextColor(mActivity.getResources().getColor(R.color.app_theme_color));
                break;
            case "RS02" :
                viewHolder.tv_repay_result.setText("支付成功");
                viewHolder.tv_repay_result.setTextColor(mActivity.getResources().getColor(R.color.green));
                break;
            case "RS03" :
                viewHolder.tv_repay_result.setText("支付失败");
                viewHolder.tv_repay_result.setTextColor(mActivity.getResources().getColor(R.color.red));
                break;
            case "RS04" :
                viewHolder.tv_repay_result.setText("还款处理中");
                viewHolder.tv_repay_result.setTextColor(mActivity.getResources().getColor(R.color.app_blue_color));
                break;
            case "RS05" :
                viewHolder.tv_repay_result.setText("交易关闭");
                viewHolder.tv_repay_result.setTextColor(mActivity.getResources().getColor(R.color.app_blue_color));
                break;
            case "RS06" :
                viewHolder.tv_repay_result.setText("其他状态");
                viewHolder.tv_repay_result.setTextColor(mActivity.getResources().getColor(R.color.app_blue_color));
                break;
        }
    }

    class ViewHolder {
        ImageView iv_bank_logo;
        TextView tv_bank_tailNumber;
        TextView tv_repay_time;
        TextView tv_repay_result;
        TextView tv_project;
        TextView tv_online_price;
    }
}
