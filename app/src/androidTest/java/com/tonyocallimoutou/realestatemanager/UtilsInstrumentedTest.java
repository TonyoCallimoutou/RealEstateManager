package com.tonyocallimoutou.realestatemanager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.tonyocallimoutou.realestatemanager.ui.MainActivity;
import com.tonyocallimoutou.realestatemanager.util.Utils;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.shadows.ShadowNetworkInfo;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class UtilsInstrumentedTest {

    private static Activity currentActivity;

    @Rule
    public ActivityScenarioRule<MainActivity> mainScenarioRule = new ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Before
    public void init() {
        mainScenarioRule.getScenario().onActivity(activity -> {
            currentActivity = activity;
        });
    }

    @AfterClass
    public static void restore(){
        setWifi(true);
        setMobileInternet(true);
    }

    private static void setWifi(boolean enable) {
        NetworkInfo wifi = ShadowNetworkInfo.newInstance(NetworkInfo.DetailedState.DISCONNECTED, ConnectivityManager.TYPE_WIFI, 0, true, NetworkInfo.State.CONNECTED);
        NetworkInfo mobile = ShadowNetworkInfo.newInstance(NetworkInfo.DetailedState.CONNECTED, ConnectivityManager.TYPE_MOBILE, 0, true, NetworkInfo.State.CONNECTED);
    }

    private static void setMobileInternet(boolean enable) {
    }

    @Test
    public void connexionTestWithInternet()  {
        setWifi(true);
        setMobileInternet(true);
        assertTrue(Utils.isInternetAvailable(currentActivity.getApplicationContext()));

        setMobileInternet(false);
        assertTrue(Utils.isInternetAvailable(currentActivity.getApplicationContext()));

        setMobileInternet(true);
        setWifi(false);
        assertTrue(Utils.isInternetAvailable(currentActivity.getApplicationContext()));
    }

    @Test
    public void connexionTestWithoutInternet() {
        setWifi(false);
        setMobileInternet(false);
        assertFalse(Utils.isInternetAvailable(currentActivity.getApplicationContext()));
    }
}