package com.huaxia.finance.consumer.bean;

/**
 * Created by mirui on 2016/12/8.
 */

public class CardSelectBean {
    private String cardName;
    private String imgType;
    private String selectType;//0表示去绑定银行卡，1表示选中，2表示未选中

    public String getSelectType() {
        return selectType;
    }

    public void setSelectType(String selectType) {
        this.selectType = selectType;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getImgType() {
        return imgType;
    }

    public void setImgType(String imgType) {
        this.imgType = imgType;
    }

}
