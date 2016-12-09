package com.huaxia.finance.consumer.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import com.huaxia.finance.consumer.R;

import java.util.List;

/**
 * Created by mirui on 2016/11/29.
 */

public class UploadCertAdapter extends BaseAdapter {
    private List bitmapList;
    private Context mContext;
    public UploadCertAdapter(Context context, List list) {
        mContext = context;
        bitmapList = list;
    }

    @Override
    public int getCount() {
        return bitmapList.size();
    }

    @Override
    public Object getItem(int position) {
        return bitmapList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null) {
            convertView= LayoutInflater.from(mContext).inflate(R.layout.layout_upload_pic_item,null);
            viewHolder = new ViewHolder();
            viewHolder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        viewHolder.iv_pic.setImageBitmap((Bitmap) bitmapList.get(position));
        return convertView;
    }

    static class ViewHolder{
        ImageView iv_pic;
    }
}
