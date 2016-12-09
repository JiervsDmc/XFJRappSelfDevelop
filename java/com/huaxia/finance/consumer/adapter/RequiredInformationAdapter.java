package com.huaxia.finance.consumer.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.huaxia.finance.consumer.R;
import java.util.List;

public class RequiredInformationAdapter extends BaseAdapter {
    private Activity mAvtivity;
    private ViewHolder viewHolder;
    private List mList;
    private OnSelectPicMethod onSelectPicMethod;

    public RequiredInformationAdapter(Activity avtivity, List list, OnSelectPicMethod onSelectPicMethod){
        this.mAvtivity=avtivity;
        this.mList=list;
        this.onSelectPicMethod=onSelectPicMethod;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {
            if(convertView==null) {
               convertView= LayoutInflater.from(mAvtivity).inflate(R.layout.layout_required_information_item,null);
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
            viewHolder.tvInformationCategory.setText("必填资料");
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
            viewHolder.tvInformationCategory.setText("选填资料");
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

        int blueColor = mAvtivity.getResources().getColor(
                R.color.app_blue_color);
        int grayColor=mAvtivity.getResources().getColor(R.color.color2);

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

       if(!mList.isEmpty()) {
           if(mList.get(position).equals("1")) {
               viewHolder.ivIcon.setImageBitmap(BitmapFactory.decodeResource(mAvtivity.getResources(),R.drawable.addpicbg));
               viewHolder.idCardPositiveChange.setClickable(false);
               viewHolder.ivIcon.setClickable(true);
               viewHolder.idCardPositiveChange.setTextColor(grayColor);
           }else {
               viewHolder.ivIcon.setImageBitmap((Bitmap) mList.get(position));
               viewHolder.ivIcon.setClickable(false);
               viewHolder.idCardPositiveChange.setClickable(true);
               viewHolder.idCardPositiveChange.setTextColor(blueColor);
           }

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
