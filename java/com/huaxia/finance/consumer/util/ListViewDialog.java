package com.huaxia.finance.consumer.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lipiao on 2016/7/20.
 * 要求的弹出框
 */
public class ListViewDialog {
    private Activity activity;
    private AlertDialog dialog;
    private TextView title;
    private ListView listView;
    private OnSelectItem onSelctItem;

    public ListViewDialog(Activity activity,OnSelectItem onSelctItem){
        this.activity = activity;
        this.onSelctItem = onSelctItem;
    }

    public void show(String titleName, final String[] date){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.dialog_listview, null);
        title = (TextView) view.findViewById(R.id.dialog_title);
        listView = (ListView) view.findViewById(R.id.dialog_listView);
        title.setText(titleName);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(activity,R.layout.dialog_listview_item,R.id.listview_item_dialog,date);
        listView.setAdapter(adapter);
        builder.setView(view);
        dialog = builder.create();
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onSelctItem.onListViewSelect(date[i]);
                dialog.dismiss();
            }
        });
        //如果需要控制弹出框的高度可以使用下面代码
//        WindowManager.LayoutParams p = dialog.getWindow().getAttributes();
//        p.height=1000;
//        dialog.getWindow().setAttributes(p);
    }

    public interface  OnSelectItem{
        void onListViewSelect(String selected);
    }

}
