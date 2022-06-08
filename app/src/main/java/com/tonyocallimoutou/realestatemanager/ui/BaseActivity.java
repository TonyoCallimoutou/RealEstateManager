package com.tonyocallimoutou.realestatemanager.ui;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.tonyocallimoutou.realestatemanager.util.LocaleHelper;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }
}
