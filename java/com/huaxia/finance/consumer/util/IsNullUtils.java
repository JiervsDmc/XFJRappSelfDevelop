package com.huaxia.finance.consumer.util;

import java.util.List;
import java.util.Map;

/**
 * Created by lipiao on 2016/7/5.
 */
public class IsNullUtils {
	public static boolean isNull(Object obj) {
        return null == obj;
    }
    public static boolean isNull(List<?> list) {
        return null == list || list.size() == 0 ? true : false;
    }
    public static boolean isNull(String str) {
    	return null == str || str.length() == 0;
    }
    public static boolean isNull(Map<?, ?> map) {
        return null == map || map.isEmpty();
    }
    public static  boolean isNull(String[] arry){
        return null == arry || (arry.length == 0 ? true : false);
    }

    public static boolean isNull(int num) {
        return 0 == num;
    }
}
