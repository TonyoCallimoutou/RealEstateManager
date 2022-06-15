package com.tonyocallimoutou.realestatemanager.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tonyocallimoutou.realestatemanager.repository.RealEstateRepository;
import com.tonyocallimoutou.realestatemanager.repository.UserRepository;
import com.tonyocallimoutou.realestatemanager.ui.MainActivity;

public class NetworkAvailableReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isConnected = Utils.isInternetAvailable(context);

        isConnected = false;
        MainActivity.test(isConnected);
        UserRepository.ConnectionChanged(isConnected);
        RealEstateRepository.ConnectionChanged(isConnected);
    }
}
