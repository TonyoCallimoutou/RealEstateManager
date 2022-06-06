package com.tonyocallimoutou.realestatemanager.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Spinner;

import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.ui.MainActivity;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
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
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
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

        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }


    public static Uri convertDrawableResourcesToUri(Context context, int drawableId) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + context.getResources().getResourcePackageName(drawableId)
                + '/' + context.getResources().getResourceTypeName(drawableId)
                + '/' + context.getResources().getResourceEntryName(drawableId) );
    }

    public static String getStringOfPrice(int price) {
        return NumberFormat.getNumberInstance(Locale.US).format(price);
    }

    public static String getStringOfPriceWithActualMoney(int price) {
        return getStringOfPrice(price)+" " + MainActivity.context.getString(R.string.USD);
    }

    public static int getIntOfStringPrice(String price) {
        return Integer.parseInt(price.replace(",",""));

    }

    public static int getIndexOfSpinner(Spinner spinner, String myValue){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myValue)){
                return i;
            }
        }

        return 0;
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
}
