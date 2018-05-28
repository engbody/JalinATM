package com.swg.jalinatm.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.swg.jalinatm.R;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Andrew Widjaja on 5/28/2018.
 */

public class Preferences {
    private static SharedPreferences sharedPreferences = null;

    public static void openPref(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getResources().getString(R.string.app_name),
                Context.MODE_PRIVATE);
    }

    public static void deleteKey(Context context, String key) {
        HashMap<String, String> result = new HashMap<String, String>();

        Preferences.openPref(context);
        for (Map.Entry<String, ?> entry : Preferences.sharedPreferences.getAll()
                .entrySet()) {
            result.put(entry.getKey(), (String) entry.getValue());
        }

        boolean b = result.containsKey(key);
        if (b) {
            Preferences.openPref(context);
            SharedPreferences.Editor prefsPrivateEditor = Preferences.sharedPreferences.edit();
            prefsPrivateEditor.remove(key);

            prefsPrivateEditor.commit();
            prefsPrivateEditor = null;
            Preferences.sharedPreferences = null;
        }
    }

    public static void setDetail(Context context, String key, String value) {
        Preferences.openPref(context);
        SharedPreferences.Editor prefsPrivateEditor = Preferences.sharedPreferences.edit();
        prefsPrivateEditor.putString(key, value);

        prefsPrivateEditor.commit();
        prefsPrivateEditor = null;
        Preferences.sharedPreferences = null;
    }

    public static Boolean checkDetail(Context context, String key) {
        HashMap<String, String> result = new HashMap<String, String>();

        Preferences.openPref(context);
        for (Map.Entry<String, ?> entry : Preferences.sharedPreferences.getAll()
                .entrySet()) {
            result.put(entry.getKey(), (String) entry.getValue());
        }

        boolean b = result.containsKey(key);
        return b;
    }

    public static String getDetail(Context context, String key) {
        HashMap<String, String> result = new HashMap<String, String>();

        Preferences.openPref(context);
        for (Map.Entry<String, ?> entry : Preferences.sharedPreferences.getAll()
                .entrySet()) {
            result.put(entry.getKey(), (String) entry.getValue());
        }

        String b = result.get(key);
        return b;
    }
}
