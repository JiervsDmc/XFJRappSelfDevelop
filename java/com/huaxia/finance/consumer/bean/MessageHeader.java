package com.huaxia.finance.consumer.bean;

import java.io.Serializable;

/**
 * Created by lipiao on 2016/7/27.
 */
public class MessageHeader implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -4481572888349642667L;

    /**
     * 版本号
     */
    private String version;

    /**
     * 传入的交易渠道
     */
    private String channel;

    /**
     * 传入的设备
     */
    private String device;

    /**
     * 交易日期 格式 yyyyMMddHHmmss
     */
    private String tradeTime;

    /**
     * 交易流水号，不允许重复，调用方传入，用于后续查询交易状态
     */
    private String flowID;

    /**
     * 交易代码
     */
    private String tradeCode;

    /**
     * 交易类型
     */
    private String tradeType="appService";

    /**
     * 报文类型 默认JSON格式
     */
    private String msgType;

    /**
     * 会话代码 用于前置服务器
     */
    private String session;

    /**
     * 身份凭据 用于前置服务器
     */
    private String token;

    /**
     * 响应时间yyyyMMddHHmmss
     */
    private String responseTime;

    /**
     * 响应代码
     */
    private String responseCode;

    /**
     * 响应信息
     */
    private String responseMsg;

    /**
     * 交易状态 成功，失败，处理中
     */
    private String tradeStatus;
    /**
     * 操作员
     */
    private String operatorID;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getTradeTime() {
        return tradeTime;
    }

    public void setTradeTime(String tradeTime) {
        this.tradeTime = tradeTime;
    }

    public String getFlowID() {
        return flowID;
    }

    public void setFlowID(String flowID) {
        this.flowID = flowID;
    }

    public String getTradeCode() {
        return tradeCode;
    }

    public void setTradeCode(String tradeCode) {
        this.tradeCode = tradeCode;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseMsg() {
        return responseMsg;
    }

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }

    public String getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public String getOperatorID() {
        return operatorID;
    }

    public void setOperatorID(String operatorID) {
        this.operatorID = operatorID;
    }

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

}
