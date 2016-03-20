package com.example.anjana.pescom.util;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {

    private static Preferences sPreferences;

    private final SharedPreferences mSharedPreferences;

    private static String SHARED_PREF_NAME = "sharedPref";

    private static String KEY_TOKEN = "token";
    private static String KEY_PH_NUMBER = "token";

    private Preferences(Context context) {
        mSharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
    }

    public static Preferences getPreferences(Context context) {
        return sPreferences != null ? sPreferences : new Preferences(context);
    }

    public void setToken(String token) {
        mSharedPreferences.edit().putString(KEY_TOKEN, token).commit();
    }

    public String getToken() {
        return mSharedPreferences.getString(KEY_TOKEN, null);
    }

    public void setNumber(String number) {
        mSharedPreferences.edit().putString(KEY_PH_NUMBER, number).commit();
    }

    public String getNumber() {
        return mSharedPreferences.getString(KEY_PH_NUMBER, null);
    }
}
