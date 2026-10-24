package com.lejia.launcher;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 轻量配置存储，兼容 Android 4.4。
 */
public final class Prefs {

    private static final String NAME = "lejia_settings";

    public static final String KEY_THEME = "theme_id";
    public static final String KEY_HOUR_24 = "hour_24";
    public static final String KEY_SHOW_SYSTEM = "show_system_apps";

    private Prefs() {
    }

    private static SharedPreferences get(Context context) {
        return context.getApplicationContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        return get(context).getBoolean(key, defValue);
    }

    public static void setBoolean(Context context, String key, boolean value) {
        get(context).edit().putBoolean(key, value).apply();
    }

    public static String getString(Context context, String key, String defValue) {
        return get(context).getString(key, defValue);
    }

    public static void setString(Context context, String key, String value) {
        get(context).edit().putString(key, value).apply();
    }
}
