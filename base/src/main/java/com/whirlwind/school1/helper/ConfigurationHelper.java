package com.whirlwind.school1.helper;

import android.content.Context;
import android.preference.PreferenceManager;

public class ConfigurationHelper {

    private static final String introCompleted = "introCompleted", shareDefault = "shareDefault";

    public static boolean getIntroCompleted(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(introCompleted, false);
    }

    public static void setIntroCompleted(Context context, boolean completed) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(introCompleted, completed).apply();
    }

    public static boolean getShareDefault(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(shareDefault, false);
    }
}