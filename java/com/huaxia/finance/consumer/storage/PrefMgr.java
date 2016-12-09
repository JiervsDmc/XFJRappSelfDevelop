package com.huaxia.finance.consumer.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by lipiao on 2016/7/1.
 */
public class PrefMgr {
    private SharedPreferences pref;

    public PrefMgr(Context ctx) {
        pref = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public String getString(UniqueKey key) {
        return pref.getString(key.toString(), null);
    }

    public String getString(UniqueKey key, String defVal) {
        return pref.getString(key.toString(), defVal);
    }

    public void putString(UniqueKey key, String val) {
        pref.edit().putString(key.toString(), val).commit();
    }
    public int getInt(UniqueKey key, int defVal) {
        return pref.getInt(key.toString(), defVal);
    }

    public void putLong(UniqueKey key, long val) {
        pref.edit().putLong(key.toString(), val).commit();
    }

    public long getLong(UniqueKey key, long defVal) {
        return pref.getLong(key.toString(), defVal);
    }

    public void putInt(UniqueKey key, int val) {
        pref.edit().putInt(key.toString(), val).commit();
    }

    public float getFloat(UniqueKey key, float defVal) {
        return pref.getFloat(key.toString(), defVal);
    }

    public void putFloat(UniqueKey key, float val) {
        pref.edit().putFloat(key.toString(), val).commit();
    }

    public boolean getBool(UniqueKey key, boolean defVal) {
        return pref.getBoolean(key.toString(), defVal);
    }

    public void putBool(UniqueKey key, boolean val) {
        pref.edit().putBoolean(key.toString(), val).commit();
    }

    // 便于链式调用
    public SharedPreferences.Editor getEditor() {
        return pref.edit();
    }
}
