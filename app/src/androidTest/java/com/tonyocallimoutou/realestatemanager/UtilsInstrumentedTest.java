package com.tonyocallimoutou.realestatemanager;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.tonyocallimoutou.realestatemanager.ui.MainActivity;
import com.tonyocallimoutou.realestatemanager.util.Utils;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

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
        if (enable) {
            InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("svc wifi enable");
        }
        else {
            InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("svc wifi disable");
        }
    }

    private static void setMobileInternet(boolean enable) {
        if (enable) {
            InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("svc data enable");
        }
        else {
            InstrumentationRegistry.getInstrumentation().getUiAutomation().executeShellCommand("svc data disable");
        }
    }

    @Test
    public void connexionTestWithInternet() throws InterruptedException {
        setWifi(true);
        setMobileInternet(true);
        Thread.sleep(1000);
        assertTrue(Utils.isInternetAvailable(currentActivity.getApplicationContext()));

        setMobileInternet(false);
        Thread.sleep(1000);
        assertTrue(Utils.isInternetAvailable(currentActivity.getApplicationContext()));

        setMobileInternet(true);
        setWifi(false);
        Thread.sleep(1000);
        assertTrue(Utils.isInternetAvailable(currentActivity.getApplicationContext()));
    }

    @Test
    public void connexionTestWithoutInternet() throws InterruptedException {
        setWifi(false);
        setMobileInternet(false);
        Thread.sleep(1000);
        assertFalse(Utils.isInternetAvailable(currentActivity.getApplicationContext()));
    }
}