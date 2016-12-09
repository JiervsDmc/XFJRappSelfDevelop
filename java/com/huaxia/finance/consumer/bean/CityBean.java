package com.huaxia.finance.consumer.bean;

import java.util.List;

/**
 * Created by lipiao on 2016/7/25.
 */
public class CityBean {

    private String areaCode;
    private String areaName;
    private String pid;
    private List<AreaBean> zones;

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public List<AreaBean> getZones() {
        return zones;
    }

    public void setZones(List<AreaBean> zones) {
        this.zones = zones;
    }
}
