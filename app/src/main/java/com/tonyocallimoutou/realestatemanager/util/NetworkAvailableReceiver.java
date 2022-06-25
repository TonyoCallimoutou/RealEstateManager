package com.tonyocallimoutou.realestatemanager.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tonyocallimoutou.realestatemanager.ui.MainActivity;

public class NetworkAvailableReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isConnected = Utils.isInternetAvailable(context);


        MainActivity.connectionChanged(isConnected);
    }
}
