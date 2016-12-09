package com.huaxia.finance.consumer.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lipiao on 2016/8/1.
 * 正则表达式
 */
public class RegularExpressionUtil {
    /**
     * 手机号码
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^0?(13|15|18|14|17)[0-9]{9}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }


    /**
     * 密码验证
     * @param mobiles
     * @return
     */
    public static boolean isPassword(String mobiles) {
        Pattern p = Pattern.compile("^[0-9a-zA-Z]{4,12}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 身份证
     * @param idCard
     * @return
     */
    public static boolean isIDCard(String idCard){
//        Pattern p = Pattern.compile("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9]|X)$");
        Pattern p = Pattern.compile("((11|12|13|14|15|21|22|23|31|32|33|34|35|36|37|41|42|43|44|45|46|50|51|52|53|54|61|62|63|64|65|71|81|82|91)\\d{4})((((19|20)(([02468][048])|([13579][26]))0229))|((20[0-9][0-9])|(19[0-9][0-9]))((((0[1-9])|(1[0-2]))((0[1-9])|(1\\d)|(2[0-8])))|((((0[1,3-9])|(1[0-2]))(29|30))|(((0[13578])|(1[02]))31))))((\\d{3}(x|X))|(\\d{4}))");
        Matcher m = p.matcher(idCard);
        return  m.matches();
    }

    /**
     * 银行卡
     * @param bankCard
     * @return
     */
    public static boolean isBankCard(String bankCard){
        Pattern p = Pattern.compile("^\\d{16}|\\d{19}$");
        Matcher m = p.matcher(bankCard);
        return  m.matches();
    }

    /**
     * 银行卡
     * @param text
     * @return
     */
    public static boolean isChinese(char text){
        Pattern p = Pattern.compile("[\\u4e00-\\u9fa5]");
        Matcher m = p.matcher(text+"");
        return  m.matches();
    }
}
