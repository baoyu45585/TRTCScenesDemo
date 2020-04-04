package com.Fliter.utils.asset;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by Wenxc on 2017/2/20.
 * share preference 管理工具类，用于存储字段属性
 */

public class SharePreferenceUtil {
    public static final String SHARE_PREFERENCE_NAME = "secretaries";

    public static void savePreference(Context context, String key, Object object) {
        if (context == null || key == null)
            throw new NullPointerException("context or key can be null");
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        if (sharedPreferences != null) {
            SharedPreferences.Editor edit = sharedPreferences.edit();
            if (object instanceof String) {
                edit.putString(key, (String) object);
            } else if (object instanceof Integer) {
                edit.putInt(key, (Integer) object);
            } else if (object instanceof Long) {
                edit.putLong(key, (Long) object);
            } else if (object instanceof Boolean) {
                edit.putBoolean(key, (Boolean) object);
            } else if (object instanceof Float) {
                edit.putFloat(key, (Float) object);
            } else if (object instanceof Set) {
                edit.putStringSet(key, (Set<String>) object);
            }
            edit.apply();
        }
    }

    public static Object getPreference(Context context, String key, Object defaultValue) {
        if (context == null || key == null)
            throw new NullPointerException("context or key can be null");
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        Object object = null;
        if (sharedPreferences != null) {
            if (defaultValue instanceof String) {
                object = sharedPreferences.getString(key, (String) defaultValue);
            } else if (defaultValue instanceof Long) {
                object = sharedPreferences.getLong(key, (Long) defaultValue);
            } else if (defaultValue instanceof Integer) {
                object = sharedPreferences.getInt(key, (Integer) defaultValue);
            } else if (defaultValue instanceof Boolean) {
                object = sharedPreferences.getBoolean(key, (Boolean) defaultValue);
            } else if (defaultValue instanceof Float) {
                object = sharedPreferences.getFloat(key, (Float) defaultValue);
            } else if (defaultValue instanceof Set) {
                object = sharedPreferences.getStringSet(key, (Set<String>) defaultValue);
            }
            return object;
        }
        return null;
    }


}
