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
        String defaultLanguage = sharedPreferences.getString(context.getString(R.string.shared_preference_language),context.getResources().getConfiguration().locale.getDisplayCountry());

        Configuration config = context.getResources().getConfiguration();

        String country = Utils.getKeyFromLanguage(context, defaultLanguage);

        config.locale = new Locale(country);


        return new ContextWrapper(context.createConfigurationContext(config));
    }
}
