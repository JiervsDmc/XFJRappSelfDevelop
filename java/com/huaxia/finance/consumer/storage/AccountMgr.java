package com.huaxia.finance.consumer.storage;

import android.content.Context;

/**
 * Created by lipiao on 2016/7/1.
 * 存储用户登录的基本信息类
 */
public class AccountMgr extends PrefMgr {

    public AccountMgr(Context ctx) {
        super(ctx);
    }

    // 清除所有登录和账户信息
    public void clear() {
        getEditor().clear().commit();
    }

    public String getVal(UniqueKey key) {
        return getString(key);
    }

    public String getVal(UniqueKey key, String defVal) {
        return getString(key, defVal);
    }
}
