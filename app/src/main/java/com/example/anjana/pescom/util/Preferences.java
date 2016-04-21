package com.example.anjana.pescom.util;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

public class Preferences {

    private static Preferences sPreferences;

    private SharedPreferences mSharedPreferences;

    private final static String SHARED_PREF_NAME = "sharedPref";

    private final static String KEY_TOKEN = "token";
    private final static String KEY_PH_NUMBER = "ph_number";
    private final static String KEY_SERVER_URL = "server_url";
    private final static String KEY_MESSAGES = "messages";
    private final static String KEY_REGISTERED_NUMBERS = "registered_numbers";

    public Preferences(Context context) {
        mSharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        sPreferences = this;
    }

    public static Preferences getPreferences(Context context) {
        return sPreferences != null ? sPreferences : new Preferences(context);
    }

    public void setToken(String token) {
        mSharedPreferences.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return mSharedPreferences.getString(KEY_TOKEN, null);
    }

    public void setNumber(String number) {
        mSharedPreferences.edit().putString(KEY_PH_NUMBER, number).apply();
    }

    public String getNumber() {
        return mSharedPreferences.getString(KEY_PH_NUMBER, null);
    }

    public void setUrl(String url) {
        mSharedPreferences.edit().putString(KEY_SERVER_URL, url).apply();
    }

    public String getUrl(String ep) {
        return getUrl() + ep;
    }

    public String getUrl() {
        return mSharedPreferences.getString(KEY_SERVER_URL,
                "https://agile-savannah-99226.herokuapp.com/");
    }

    public void addMessageFor(String phno, JSONObject jsonObject) {
        JSONArray existingMessages = getMessagesFor(phno);
        if(existingMessages == null)
            existingMessages = new JSONArray();
        existingMessages.put(jsonObject);
        mSharedPreferences.edit().putString(getMessagesKey(phno), existingMessages.toString()).commit();
    }

    public JSONArray getMessagesFor(String phno) {
        try {
            return new JSONArray(mSharedPreferences.getString(getMessagesKey(phno), "[]"));
        } catch (JSONException e) {
            throw new IllegalArgumentException(
                    mSharedPreferences.getString(getMessagesKey(phno), "[]")
                            + " is not a valid JSON!");
        }
    }

    private String getMessagesKey(String phno) {
        return KEY_MESSAGES + phno;
    }

    public void setRegisteredNumbers(Set<String> numbers) {
        mSharedPreferences.edit().putStringSet(KEY_REGISTERED_NUMBERS,numbers);
    }

    public Set<String> getRegisteredNumbers() {
        return mSharedPreferences.getStringSet(KEY_REGISTERED_NUMBERS,null);
    }

}
