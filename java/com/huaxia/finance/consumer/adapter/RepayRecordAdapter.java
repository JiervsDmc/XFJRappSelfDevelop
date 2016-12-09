package com.huaxia.finance.consumer.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;

import java.util.List;

/**
 * Created by wangjie01 on 2016/12/8.
 */
public class RepayRecordAdapter extends BaseAdapter {

    private Activity mActivity;
    private List list;
    private ViewHolder viewHolder;

    public RepayRecordAdapter(Activity mActivity,List list) {
        this.mActivity = mActivity;
        this.list = list;
    }

    @Override
    public int getCount() {
        return 6;
        //return list.size();
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
            convertView.getTag();
        }
        //根据得到的列表数据加载支付记录的列表


        return convertView;
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
