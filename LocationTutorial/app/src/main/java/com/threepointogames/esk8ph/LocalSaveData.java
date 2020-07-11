package com.threepointogames.esk8ph;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;


public class LocalSaveData {

    public static void saveData(Context context,String PREF,String key,String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String loadData(Context context,String PREF,String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF, MODE_PRIVATE);
        String text = sharedPreferences.getString(key, "");
        return text;
    }

}
