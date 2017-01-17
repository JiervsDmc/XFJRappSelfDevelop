package com.huaxia.finance.consumer.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by mirui on 2016/12/27.
 * 避免空指针异常造成的崩溃
 */

public class ConvertUtils {

    public static String toString(Object obj){
        String str = "";
        try {
            str = obj.toString();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.getLogutil().d("ConvertUtils.toString : " + e.getMessage());
        }
        return str;
    }

    public static int toInt(Object obj){
        int num = 0;
        try {
            num = Integer.valueOf(obj.toString());
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.getLogutil().d("ConvertUtils.toInt : " + e.getMessage());
        }
        return num;
    }

    public static String mapToString(Map map , String key){
        String str = "";
        try {
            str = map.get(key).toString();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.getLogutil().d("ConvertUtils.mapToString : " + e.getMessage());
        }
        return str;
    }

    public static int mapToInt(Map map , String key){
        int num = 0;
        try {
            num = Integer.valueOf(map.get(key).toString());
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.getLogutil().d("ConvertUtils.mapToInt : " + e.getMessage());
        }
        return num;
    }

    public static List mapToList(Map map , String key){
        List list = new ArrayList<>();
        try {
            list = (List) map.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.getLogutil().d("ConvertUtils.mapToList : " + e.getMessage());
        }
        return list;
    }

    public static Map mapToMap(Map map , String key){
        Map newMap = null;
        try {
            newMap = (Map) map.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.getLogutil().d("ConvertUtils.mapToMap : " + e.getMessage());
        }
        return newMap;
    }

    public static boolean equals(String strO,String strT){
        boolean isEqauls = false;
        try {
            isEqauls = strO.equals(strT);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.getLogutil().d("ConvertUtils.equals : " + e.getMessage());
        }
        return isEqauls;
    }
}
