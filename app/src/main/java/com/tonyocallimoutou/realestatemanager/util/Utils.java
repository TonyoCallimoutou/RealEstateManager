package com.tonyocallimoutou.realestatemanager.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Spinner;

import androidx.preference.PreferenceManager;

import com.google.android.libraries.places.api.model.Place;
import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.ui.MainActivity;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class Utils {
    /**
     * Conversion d'un prix d'un bien immobilier (Dollars vers Euros)
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param dollars
     * @return
     */
    public static int convertDollarToEuro(int dollars) {
        return (int) Math.round(dollars * 0.812);
    }

    public static int convertEuroToDollar(int euros) {
        return (int) Math.round(euros / 0.812);
    }

    /**
     * Conversion de la date d'aujourd'hui en un format plus approprié
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @return
     */
    public static String getTodayDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault());
        return dateFormat.format(new Date());
    }

    /**
     * Vérification de la connexion réseau
     * NOTE : NE PAS SUPPRIMER, A MONTRER DURANT LA SOUTENANCE
     * @param context
     * @return
     */
    public static Boolean isInternetAvailable(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return (networkInfo != null && networkInfo.isConnected());
    }


    public static Uri convertDrawableResourcesToUri(Context context, int drawableId) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + context.getResources().getResourcePackageName(drawableId)
                + '/' + context.getResources().getResourceTypeName(drawableId)
                + '/' + context.getResources().getResourceEntryName(drawableId) );
    }

    public static String getKeyMoney(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String defaultLanguageKey = sharedPreferences.getString(context.getString(R.string.shared_preference_language),context.getResources().getConfiguration().locale.getLanguage());

        String[] languageKey = context.getResources().getStringArray(R.array.language_key);
        String[] moneyStr = context.getResources().getStringArray(R.array.money);

        String defaultMoney;
        if (Arrays.asList(languageKey).contains(defaultLanguageKey)) {
            defaultMoney = moneyStr[Arrays.asList(languageKey).indexOf(defaultLanguageKey)];
        }
        else {
            defaultMoney = moneyStr[0];
        }

        String money = sharedPreferences.getString(context.getString(R.string.shared_preference_money), defaultMoney);

        String[] moneyKey = money.split(" ");

        return moneyKey[moneyKey.length-1];
    }

    public static int getPriceInUSD(Context context, int price, String moneyKey) {
        if (moneyKey.equals(context.getString(R.string.money_key_euro))) {
            return convertEuroToDollar(price);
        }
        return price;
    }

    public static int convertPriceUSDInMoneyKey(Context context, int price, String moneyKey) {
        if (moneyKey.equals(context.getString(R.string.money_key_euro))) {
            return convertDollarToEuro(price);
        }
        return price;
    }

    public static String getStringOfPrice(int price) {
        return NumberFormat.getNumberInstance(Locale.US).format(price);
    }

    public static String getStringOfPriceWithActualMoney(Context context, int price) {
        String moneyKey = getKeyMoney(context);

        if (moneyKey.equals(context.getString(R.string.money_key_euro))) {
            price = convertDollarToEuro(price);
        }

        return getStringOfPrice(price)+" " + moneyKey;
    }

    public static int getIntOfStringPrice(String price) {
        try {
            return Integer.parseInt(price.replace(",",""));
        }
        catch (Exception e){
            String str = price.replaceFirst(".$","");
            return Integer.parseInt(str.replace(",",""));
        }
    }

    public static int getAgeOfRealEstate(RealEstate realEstate) {
        String today = getTodayDate();
        String creation = realEstate.getCreationDate();

        String[] dateToday = today.split("/");
        String[] dateCreation = creation.split("/");

        int yearsGap = Integer.parseInt(dateToday[2]) - Integer.parseInt(dateCreation[2]);
        int monthGap = Integer.parseInt(dateToday[1]) - Integer.parseInt(dateCreation[1]);
        if (Integer.parseInt(dateToday[0]) >= Integer.parseInt(dateCreation[0])) {
            monthGap ++;
        }

        return monthGap +yearsGap * 12;
    }

    public static int getAgeOfSold(RealEstate realEstate) {
        String today = getTodayDate();
        String sold = realEstate.getSoldDate();

        String[] dateToday = today.split("/");
        String[] dateSold = sold.split("/");

        int yearsGap = Integer.parseInt(dateToday[2]) - Integer.parseInt(dateSold[2]);
        int monthGap = Integer.parseInt(dateToday[1]) - Integer.parseInt(dateSold[1]);
        if (Integer.parseInt(dateToday[0]) >= Integer.parseInt(dateSold[0])) {
            monthGap ++;
        }

        return monthGap + yearsGap * 12;
    }

    public static float getDistanceFromCityInKm(RealEstate realEstate, Place place) {
        Location realEstateLocation = new Location("");
        Location placeLocation = new Location("");

        if (realEstate.getPlace() != null) {
            realEstateLocation.setLatitude(realEstate.getPlace().getLat());
            realEstateLocation.setLongitude(realEstate.getPlace().getLng());

            placeLocation.setLatitude(place.getLatLng().latitude);
            placeLocation.setLongitude(place.getLatLng().longitude);
        }

        return (placeLocation.distanceTo(realEstateLocation) / 1000);

    }

    public static String getKeyFromLanguage(Context context, String language) {
        String[] listLanguage = context.getResources().getStringArray(R.array.language);
        String[] key = context.getResources().getStringArray(R.array.language_key);

        return key[Arrays.asList(listLanguage).indexOf(language)];

    }
 }
