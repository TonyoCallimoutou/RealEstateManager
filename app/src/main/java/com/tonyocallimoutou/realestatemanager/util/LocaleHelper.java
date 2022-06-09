package com.tonyocallimoutou.realestatemanager.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.tonyocallimoutou.realestatemanager.R;

import java.util.Locale;

public class LocaleHelper {

    public static ContextWrapper setLocale(Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String defaultLanguage = sharedPreferences.getString(context.getString(R.string.shared_preference_language),context.getResources().getConfiguration().locale.getLanguage());

        Configuration config = context.getResources().getConfiguration();


        config.locale = new Locale(defaultLanguage);


        return new ContextWrapper(context.createConfigurationContext(config));
    }
}
