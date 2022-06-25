package com.tonyocallimoutou.realestatemanager.ui.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;

import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.ui.BaseActivity;
import com.tonyocallimoutou.realestatemanager.ui.MainActivity;
import com.tonyocallimoutou.realestatemanager.viewmodel.ViewModelFactory;
import com.tonyocallimoutou.realestatemanager.viewmodel.ViewModelUser;

public class SettingActivity extends BaseActivity {

    ViewModelUser viewModelUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModelUser = new ViewModelProvider(this, ViewModelFactory.getInstance(this)).get(ViewModelUser.class);
        setContentView(R.layout.activity_setting);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void finish() {
        super.finish();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public static void goToSetting(Activity activity){
        Intent intent = new Intent(activity, SettingActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

}