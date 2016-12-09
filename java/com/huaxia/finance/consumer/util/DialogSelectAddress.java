package com.huaxia.finance.consumer.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.huaxia.finance.consumer.R;
import com.huaxia.finance.consumer.view.wheel.OnWheelChangedListener;
import com.huaxia.finance.consumer.view.wheel.WheelView;
import com.huaxia.finance.consumer.view.wheel.adapters.ArrayWheelAdapter;

/**
 * Created by lipiao on 2016/7/15.
 * 省-市-区   地址选择弹出框
 */
public class DialogSelectAddress implements OnWheelChangedListener, View.OnClickListener {
    private WheelView mViewProvince;
    private WheelView mViewCity;
    private AlertDialog dialog;
    private CityDataUtil dataUtil;

    private TextView selectCitySure;
    private TextView selectCityCancel;

    private OnSelectCityDialog selectCityDialog;

    private Activity activity;

    public DialogSelectAddress(Activity activity, OnSelectCityDialog selectCityDialog) {
        this.activity = activity;
        this.selectCityDialog = selectCityDialog;
    }

    public void showDialog() {
        dataUtil = new CityDataUtil(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_city_select, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.Dialot_CitySelect);
        builder.setView(view);
        setUpViews(view);
        dialog = builder.create();
        dialog.setView(view);
        dialog.show();
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int) (display.getWidth());
        dialog.getWindow().setAttributes(lp);
    }

    private void setUpViews(View view) {
        mViewProvince = (WheelView) view.findViewById(R.id.id_province);
        mViewCity = (WheelView) view.findViewById(R.id.id_city);
        selectCitySure = (TextView) view.findViewById(R.id.select_city_sure);
        selectCityCancel = (TextView) view.findViewById(R.id.select_city_cancel);

        // 添加change事件
        mViewProvince.addChangingListener(this);
        mViewCity.addChangingListener(this);
        selectCityCancel.setOnClickListener(this);
        selectCitySure.setOnClickListener(this);

        dataUtil.initProvinceDatas();
        mViewProvince.setViewAdapter(new ArrayWheelAdapter<String>(activity, dataUtil.mProvinceDatas));
        // 设置可见条目数量
        mViewProvince.setVisibleItems(7);
        mViewCity.setVisibleItems(7);
        updateCities();
    }

    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mViewProvince) {
            updateCities();
        } else if (wheel == mViewCity) {
            dataUtil.mCurrentCityName = dataUtil.mCitisDatasMap.get(dataUtil.mCurrentProviceName)[newValue];
        }
    }

    /**
     * 根据当前的省，更新市WheelView的信息
     */
    private void updateCities() {
        int pCurrent = mViewProvince.getCurrentItem();
        dataUtil.mCurrentProviceName = dataUtil.mProvinceDatas[pCurrent];
        if (dataUtil.mCitisDatasMap.get(dataUtil.mCurrentProviceName).length <= 0) {
            dataUtil.mCurrentCityName = " ";
        } else {
            dataUtil.mCurrentCityName = dataUtil.mCitisDatasMap.get(dataUtil.mCurrentProviceName)[0];
        }
        String[] cities = dataUtil.mCitisDatasMap.get(dataUtil.mCurrentProviceName);
        if (cities.length <= 0) {
            cities = new String[]{""};
        }
        mViewCity.setViewAdapter(new ArrayWheelAdapter<String>(activity, cities));
        mViewCity.setCurrentItem(0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.select_city_cancel:
                dialog.dismiss();
                break;
            case R.id.select_city_sure:
                String[] codeString = dataUtil.stringGetCode(dataUtil.mCurrentProviceName, dataUtil.mCurrentCityName).split(" ");
                if (codeString.length == 2) {
                    selectCityDialog.onSelectCity(dataUtil.mCurrentProviceName + " " + dataUtil.mCurrentCityName, codeString[1], codeString[0]);
                } else {
                    selectCityDialog.onSelectCity(dataUtil.mCurrentProviceName + " " + dataUtil.mCurrentCityName, "", codeString[0]);

                }
                dialog.dismiss();
                break;
        }
    }

    public interface OnSelectCityDialog {
        void onSelectCity(String selectedCity, String cityCode, String pidCode);
    }
}
