package com.huaxia.finance.consumer.bean;

import java.io.Serializable;

/**
 * Created by mirui on 2016/12/28.
 */

public class PayInfoBean implements Serializable {

    private String orderNo;
    private String productName;
    private String repayMoney;
    private String repayPeriod;
    private String totalPeriod;
    private int second;
    private String paycorpusamt;
    private String platformRisk;
    private String platformFee;
    private String penaltyFee;
    private String consuleFee;
    private String platformFeeCheck;
    private String platformFeeService;
    private String contractStatus;
    private String payfineamt;
    private String payinteamt;
    private String status;
    private String seqid;

    public String getSeqid() {
        return seqid;
    }

    public void setSeqid(String seqid) {
        this.seqid = seqid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPayinteamt() {
        return payinteamt;
    }

    public void setPayinteamt(String payinteamt) {
        this.payinteamt = payinteamt;
    }

    public String getPayfineamt() {
        return payfineamt;
    }

    public void setPayfineamt(String payfineamt) {
        this.payfineamt = payfineamt;
    }

    public String getPaycorpusamt() {
        return paycorpusamt;
    }

    public void setPaycorpusamt(String paycorpusamt) {
        this.paycorpusamt = paycorpusamt;
    }

    public String getPlatformRisk() {
        return platformRisk;
    }

    public void setPlatformRisk(String platformRisk) {
        this.platformRisk = platformRisk;
    }

    public String getPlatformFee() {
        return platformFee;
    }

    public void setPlatformFee(String platformFee) {
        this.platformFee = platformFee;
    }

    public String getPenaltyFee() {
        return penaltyFee;
    }

    public void setPenaltyFee(String penaltyFee) {
        this.penaltyFee = penaltyFee;
    }

    public String getConsuleFee() {
        return consuleFee;
    }

    public void setConsuleFee(String consuleFee) {
        this.consuleFee = consuleFee;
    }

    public String getPlatformFeeCheck() {
        return platformFeeCheck;
    }

    public void setPlatformFeeCheck(String platformFeeCheck) {
        this.platformFeeCheck = platformFeeCheck;
    }

    public String getPlatformFeeService() {
        return platformFeeService;
    }

    public void setPlatformFeeService(String platformFeeService) {
        this.platformFeeService = platformFeeService;
    }

    public String getContractStatus() {
        return contractStatus;
    }

    public void setContractStatus(String contractStatus) {
        this.contractStatus = contractStatus;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getRepayMoney() {
        return repayMoney;
    }

    public void setRepayMoney(String repayMoney) {
        this.repayMoney = repayMoney;
    }

    public String getRepayPeriod() {
        return repayPeriod;
    }

    public void setRepayPeriod(String repayPeriod) {
        this.repayPeriod = repayPeriod;
    }

    public String getTotalPeriod() {
        return totalPeriod;
    }

    public void setTotalPeriod(String totalPeriod) {
        this.totalPeriod = totalPeriod;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }
}
