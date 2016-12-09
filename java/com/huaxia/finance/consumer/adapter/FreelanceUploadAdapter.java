package com.huaxia.finance.consumer.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huaxia.finance.consumer.R;

import java.util.List;

/**
 * Created by lipiao on 2016/7/21.
 */
public class FreelanceUploadAdapter extends BaseAdapter{
    private Activity activity;
    private List<String> picList;
    private List bitmapList;
    private OnSelectPicMethod onSelectPicMethod;
    private boolean haveFirst;
    public FreelanceUploadAdapter(Activity activity,List<String> picList,List bitmapList,boolean haveFirst,OnSelectPicMethod onSelectPicMethod){
        this.picList = picList;
        this.bitmapList=bitmapList;
        this.activity = activity;
        this.haveFirst = haveFirst;
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
            viewHolder.rlIdCard=(RelativeLayout)convertView.findViewById(R.id.rl_idCard);
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
            viewHolder.idCardPositiveUpload.setText("财力证明");
            viewHolder.tvInformationCategory.setVisibility(View.GONE);
        }else if(position==3) {
            if(haveFirst){
                viewHolder.idCardPositiveUpload.setText("首付凭证");
                viewHolder.tvInformationCategory.setVisibility(View.GONE);
            }else{
                viewHolder.rlIdCard.setVisibility(View.GONE);
            }
        } else if(position==4) {
            viewHolder.tvInformationCategory.setVisibility(View.VISIBLE);
            viewHolder.tvInformationCategory.setText("选传资料");
            viewHolder.idCardPositiveUpload.setText("户口簿主页");
            viewHolder.tvInformationCategory.setVisibility(View.VISIBLE);
            TextPaint tp = viewHolder.tvInformationCategory.getPaint();
            tp.setFakeBoldText(true);
        }
        else if(position==5) {
            viewHolder.idCardPositiveUpload.setText("户口簿本人页");
            viewHolder.tvInformationCategory.setVisibility(View.GONE);
        }
        viewHolder.idCardPositiveChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectPicMethod.onIcon(position,2);
            }
        });

        viewHolder.ivIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSelectPicMethod.onIcon(position,1);
            }
        });

        if(picList.get(position).equals("1")&&bitmapList.get(position).equals("1")) {
            Glide.with(activity).load(R.drawable.addpicbg).into(viewHolder.ivIcon);
            viewHolder.ivIcon.setClickable(true);
            viewHolder.idCardPositiveChange.setClickable(false);
            viewHolder.idCardPositiveChange.setTextColor(activity.getResources().getColor(R.color.color2));
        } else{
            if(picList.get(position).equals("1")) {
                viewHolder.ivIcon.setImageBitmap((Bitmap) bitmapList.get(position));
            }else {
                Glide.with(activity).load(picList.get(position)).into(viewHolder.ivIcon);
            }
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
        RelativeLayout rlIdCard;
    }
    public interface OnSelectPicMethod{
        void onIcon(int position,int Type);
    }
}
