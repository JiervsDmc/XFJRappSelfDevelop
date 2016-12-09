package com.huaxia.finance.consumer.util;

import android.app.Activity;

import com.alibaba.fastjson.JSON;
import com.huaxia.finance.consumer.bean.AreaBean;
import com.huaxia.finance.consumer.bean.CityBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by lipiao on 2016/7/14.
 */
public class CityDataUtil {
    private Activity activity;

    private List<CityBean> provinceList;

    public CityDataUtil(Activity activity) {
        this.activity = activity;
        provinceList = JSON.parseArray(Utils.readTestJson(activity, "area.json"), CityBean.class);
    }

    /**
     * 所有省
     */
    public String[] mProvinceDatas;
    /**
     * key - 省 value - 市
     */
    public Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();
    /**
     * 当前省的名称
     */
    public String mCurrentProviceName;
    /**
     * 当前市的名称
     */
    public String mCurrentCityName;

    public String PCName = "";

    public String LOCode = "";

    public String codeGetString(String pid, String cid) {
        if (provinceList.size() != 0) {
            for (CityBean bean : provinceList) {
                if (bean.getAreaCode().equals(pid)) {
                    if (bean.getZones().size() == 0) {
                        PCName = bean.getAreaName() + " ";
                    } else {
                        for (AreaBean areaBean : bean.getZones()) {
                            if (areaBean.getAreaCode().equals(cid)) {
                                PCName = bean.getAreaName() + " " + areaBean.getAreaName();
                                break;
                            }
                        }
                    }
                }
            }
        }
        return PCName;
    }

    public String stringGetCode(String pidName, String cidName) {
        if (provinceList != null && !provinceList.isEmpty()) {
            for (CityBean bean : provinceList) {
                if (bean.getAreaName().equals(pidName)) {
                    if (bean.getZones().size() == 0) {
                        LOCode = bean.getAreaCode() + " ";
                    } else {
                        for (AreaBean areaBean : bean.getZones()) {
                            if (areaBean.getAreaName().equals(cidName)) {
                                LOCode = bean.getAreaCode() + " " + areaBean.getAreaCode();
                                break;
                            }
                        }
                    }
                }
            }
        }
        return LOCode;
    }

    /**
     * 解析省市区的XML数据
     */
    public void initProvinceDatas() {
        //*/ 初始化默认选中的省、市、区
        if (provinceList != null && !provinceList.isEmpty()) {
            mCurrentProviceName = provinceList.get(0).getAreaName();
            List<AreaBean> cityList = provinceList.get(0).getZones();
            if (cityList != null && !cityList.isEmpty()) {
                mCurrentCityName = cityList.get(0).getAreaName();
            }
        }
        mProvinceDatas = new String[provinceList.size()];
        for (int i = 0; i < provinceList.size(); i++) {
            // 遍历所有省的数据
            mProvinceDatas[i] = provinceList.get(i).getAreaName();
            List<AreaBean> cityList = provinceList.get(i).getZones();
            String[] cityNames = new String[cityList.size()];
            for (int j = 0; j < cityList.size(); j++) {
                // 遍历省下面的所有市的数据
                cityNames[j] = cityList.get(j).getAreaName();
            }
            // 省-市的数据，保存到mCitisDatasMap
            mCitisDatasMap.put(provinceList.get(i).getAreaName(), cityNames);
        }
    }


}
