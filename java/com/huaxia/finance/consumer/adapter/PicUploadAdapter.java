package com.huaxia.finance.consumer.adapter;

import android.app.Activity;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huaxia.finance.consumer.R;

import java.util.List;

/**
 * Created by lipiao on 2016/7/21.
 */
public class PicUploadAdapter extends BaseAdapter{
    private Activity activity;
    private List<String> picList;
    private OnSelectPicMethod onSelectPicMethod;
    public PicUploadAdapter(Activity activity,List<String> picList,OnSelectPicMethod onSelectPicMethod){
        this.picList = picList;
        this.activity = activity;
        this.onSelectPicMethod = onSelectPicMethod;
    }

    @Override
    public int getCount() {
        return picList.size();
    }

    @Override
    public Object getItem(int i) {
        return picList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
       ViewHolder viewHolder;
        if(convertView==null) {
            convertView= LayoutInflater.from(activity).inflate(R.layout.layout_required_information_item,null);
            viewHolder = new ViewHolder();
            viewHolder.tvInformationCategory= (TextView) convertView.findViewById(R.id.tv_information_category);
            viewHolder.idCardPositiveUpload= (TextView) convertView.findViewById(R.id.idCard_positive_upload);
            viewHolder.idCardPositiveChange= (TextView) convertView.findViewById(R.id.idCard_positive_change);
            viewHolder.ivIcon= (ImageView) convertView.findViewById(R.id.iv_icon);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        if(position==0) {
            viewHolder.tvInformationCategory.setVisibility(View.VISIBLE);
            TextPaint tp = viewHolder.tvInformationCategory.getPaint();
            tp.setFakeBoldText(true);
            viewHolder.tvInformationCategory.setText("必传资料");
            viewHolder.idCardPositiveUpload.setText("身份证正面");
        }else if(position==1) {
            viewHolder.idCardPositiveUpload.setText("身份证反面");
            viewHolder.tvInformationCategory.setVisibility(View.GONE);
        }else if(position==2) {
            viewHolder.idCardPositiveUpload.setText("工作证明");
            viewHolder.tvInformationCategory.setVisibility(View.GONE);
        }else if(position==3) {
            viewHolder.idCardPositiveUpload.setText("首付凭证");
            viewHolder.tvInformationCategory.setVisibility(View.GONE);
        }
        else if(position==4) {
            viewHolder.tvInformationCategory.setVisibility(View.VISIBLE);
            viewHolder.tvInformationCategory.setText("选传资料");
            viewHolder.tvInformationCategory.setVisibility(View.VISIBLE);
            TextPaint tp = viewHolder.tvInformationCategory.getPaint();
            tp.setFakeBoldText(true);
            viewHolder.idCardPositiveUpload.setText("户口簿主页");
        }
        else if(position==5) {
            viewHolder.idCardPositiveUpload.setText("户口簿本人页");
            viewHolder.tvInformationCategory.setVisibility(View.GONE);
        }
        else if(position==6) {
            viewHolder.idCardPositiveUpload.setText("财力证明");
            viewHolder.tvInformationCategory.setVisibility(View.GONE);
        }
        viewHolder.idCardPositiveChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectPicMethod.onIcon(position);
            }
        });

        viewHolder.ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectPicMethod.onIcon(position);
            }
        });

        if(picList.get(position).equals("1")) {
            Glide.with(activity).load(R.drawable.addpicbg).into(viewHolder.ivIcon);
            viewHolder.ivIcon.setClickable(true);
            viewHolder.idCardPositiveChange.setClickable(false);
            viewHolder.idCardPositiveChange.setTextColor(activity.getResources().getColor(R.color.color2));
        } else{
            Glide.with(activity).load(picList.get(position)).into(viewHolder.ivIcon);
            viewHolder.ivIcon.setClickable(false);
            viewHolder.idCardPositiveChange.setClickable(true);
            viewHolder.idCardPositiveChange.setTextColor(activity.getResources().getColor(R.color.app_blue_color));
        }
        return convertView;
    }
    static class ViewHolder {
        TextView tvInformationCategory;
        TextView idCardPositiveUpload;
        TextView idCardPositiveChange;
        ImageView ivIcon;
    }
    public interface OnSelectPicMethod{
        void onIcon(int position);
    }
}
