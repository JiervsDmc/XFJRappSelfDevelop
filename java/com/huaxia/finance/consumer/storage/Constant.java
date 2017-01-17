package com.huaxia.finance.consumer.storage;

/**
 * Created by lipiao on 2016/7/1.
 */
public class Constant {
    public static final boolean DEBUG = false;
    // https SIT_new接口调试地址 发布测试环境
//    public static final String IP = "https://wap-managesit.huaxiafinance.com";
    public static final String IP = "http://192.168.11.123:8081";
    // https UAT_new接口调试地址 发布测试环境
//   public static final String IP="https://wap-manageuat.huaxiafinance.com";
    //生产环境
  //public static final String IP="https://yifenqi.huaxiafinance.com";
    //服务器接口调试地址
//    public static final String IP = "http://192.168.11.123:8086";
    //陈善华接口调试地址
    //public static final String IP ="http://192.168.8.219:8090/";
    //张东雷接口调试地
//    public static final String IP ="http://192.168.12.45:8081";

    //李永超接口调试地址
    //public static final String IP ="http://192.168.9.13:8081";
    //李永超3期接口调试地址
   // public static final String IP ="http://192.168.9.36:8080";
    //UAT_new接口调试地址
//    public static final String IP="http://192.168.11.124:8081";
    //网络请求地址
    public static final String URL =IP +"/huaxia-front/msgProcess/acceptJsonReq.do";
    //协议地址
    public static final String PROTOCOL_URL =IP +"/huaxia-front/showContractPage.do?json=";
    //补充协议地址
    public static final String PROTOCOL1_URL =IP +"/huaxia-front/showContractSupPage?json=";
    //公司简介
    public static final String COMPANY_PROFILE =IP +"/huaxia-front/companyProfile.do";

}
