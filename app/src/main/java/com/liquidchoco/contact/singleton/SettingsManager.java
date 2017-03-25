package com.liquidchoco.contact.singleton;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.liquidchoco.contact.model.Contact;

import java.util.Map;

/**
 * Created by Yunita Andini on 3/25/17.
 */

public class SettingsManager {
    private static Context context;
    private static SharedPreferences PREF = null;
    private static SharedPreferences.Editor PREF_EDITOR = null;
    private static SettingsManager SETTINGSMANAGER = null;

    private Contact contact;

    public static SettingsManager getInstance() {
        if (SETTINGSMANAGER == null) {
            PREF = AppController.getAppContext().getSharedPreferences("com.yunitaandini.contact", Context.MODE_PRIVATE);
            PREF_EDITOR = PREF.edit();
            SETTINGSMANAGER = new SettingsManager();
            if(PREF!=null) {
                Log.v("SettingsManager", "pref available");
            }else{
                Log.v("SettingsManager", "pref null");
            }
            if(PREF_EDITOR!=null) {
                Log.v("SettingsManager", "pref editor available");
            }else{
                Log.v("SettingsManager", "pref editor null");
            }
            if(SETTINGSMANAGER!=null) {
                Log.v("SettingsManager", "setting manager available");
            }else{
                Log.v("SettingsManager", "setting manager null");
            }
        }
        return SETTINGSMANAGER;
    }

    public SettingsManager() {
        context = AppController.getAppContext();
    }

    public void getAllPref(){
        Map<String,?> keys = PREF.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){
            Log.d("map values",entry.getKey() + ": " + entry.getValue().toString());
        }
    }

    //MARK: Preferences Setter Getter

    public void setStr(String key, String value) {
        PREF_EDITOR.putString(key, value);
        PREF_EDITOR.commit();
    }
    public String getStr(String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return PREF.getString(key, "");
    }

    public String getStr(String key, String defaultValue) {
        return PREF.getString(key, defaultValue);
    }

    public boolean getBool(String key, boolean defaultValue) {
        return PREF.getBoolean(key, defaultValue);
    }

    public void setBool(String key, boolean value) {
        PREF_EDITOR.putBoolean(key, value);
        PREF_EDITOR.commit();
    }

    public int getInt(String key) {
        return PREF.getInt(key, -1);
    }

    public int getInt(String key, int defaultValue) {
        return PREF.getInt(key, defaultValue);
    }

    public void setInt(String key, int value) {
        PREF_EDITOR.putInt(key, value);
        PREF_EDITOR.commit();
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }
}
