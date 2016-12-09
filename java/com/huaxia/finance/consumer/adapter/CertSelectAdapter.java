package com.huaxia.finance.consumer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.activity.UploadCertificationActivity;
import com.huaxia.finance.consumer.bean.CertSelectBean;

import java.util.List;

/**
 * Created by mirui on 2016/11/28.
 */

public class CertSelectAdapter extends BaseAdapter {
    List<CertSelectBean> certList;
    private Context mContext;
    public CertSelectAdapter(List<CertSelectBean> list, UploadCertificationActivity context) {
        certList = list;
        mContext = context;
    }

    @Override
    public int getCount() {
        return certList.size();
    }

    @Override
    public Object getItem(int position) {
        return certList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null) {
            convertView= LayoutInflater.from(mContext).inflate(R.layout.layout_cert_select_item,null);
            viewHolder = new ViewHolder();
            viewHolder.tv_select = (TextView) convertView.findViewById(R.id.tv_select);
            viewHolder.iv_select = (ImageView) convertView.findViewById(R.id.iv_select);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        viewHolder.tv_select.setText(certList.get(position).getCertName());
        viewHolder.iv_select.setVisibility(certList.get(position).isSelected()? View.VISIBLE:View.GONE);
        return convertView;
    }

    static class ViewHolder{
        TextView tv_select;
        ImageView iv_select;
    }
}
