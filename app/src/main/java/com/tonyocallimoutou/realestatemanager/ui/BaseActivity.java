package com.tonyocallimoutou.realestatemanager.ui;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import androidx.appcompat.app.AppCompatActivity;

import com.tonyocallimoutou.realestatemanager.util.LocaleHelper;
import com.tonyocallimoutou.realestatemanager.util.NetworkAvailableReceiver;

public class BaseActivity extends AppCompatActivity {

    NetworkAvailableReceiver networkAvailableReceiver = new NetworkAvailableReceiver();

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.setLocale(newBase));
    }

    @Override
    protected void onResume() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkAvailableReceiver, intentFilter);
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(networkAvailableReceiver);
        super.onPause();
    }
}
