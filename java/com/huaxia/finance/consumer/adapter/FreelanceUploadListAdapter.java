package com.huaxia.finance.consumer.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by mirui on 2016/11/28.
 */

public class FreelanceUploadListAdapter extends BaseAdapter{

    private List dataList = new ArrayList<>();
    private Context mContext;
    public FreelanceUploadListAdapter(List list,Context context) {
        dataList = list;
        mContext = context;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null) {
            convertView= LayoutInflater.from(mContext).inflate(R.layout.layout_upload_info_item,null);
            viewHolder = new ViewHolder();
            viewHolder.tv_upload_info = (TextView) convertView.findViewById(R.id.tv_upload_info);
            viewHolder.rl_upload_item = (RelativeLayout) convertView.findViewById(R.id.rl_upload_item);
            viewHolder.iv_arrow = (ImageView) convertView.findViewById(R.id.iv_arrow);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        Map map = (Map) dataList.get(position);
        TextPaint tp = viewHolder.tv_upload_info.getPaint();
        if(map.get("tabFlag")!=null&&map.get("tabFlag").equals("0")){
            tp.setFakeBoldText(true);
            viewHolder.rl_upload_item.setBackgroundColor(Color.parseColor("#F0F0F0"));
            viewHolder.iv_arrow.setVisibility(View.GONE);
        }else{
            tp.setFakeBoldText(false);
            viewHolder.rl_upload_item.setBackgroundColor(Color.parseColor("#FFFFFF"));
            viewHolder.iv_arrow.setVisibility(View.VISIBLE);
        }
        viewHolder.tv_upload_info.setText(map.get("imageName")+"");
        return convertView;
    }

    static class ViewHolder {
        TextView tv_upload_info;
        RelativeLayout rl_upload_item;
        ImageView iv_arrow;
    }
}
