package com.tonyocallimoutou.realestatemanager.util;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.model.RealEstate;
import com.tonyocallimoutou.realestatemanager.ui.MainActivity;

import java.util.Calendar;

public class UtilNotification {

    private static Notification notification;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void createNotification(Context context, RealEstate realEstate) {
        initNotification(context,realEstate);
        setNotification(context);
    }


    private static void initNotification(Context context, RealEstate realEstate) {
        Intent activityIntent = new Intent(context, MainActivity.class);

        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_MUTABLE);



        String title;
        if (realEstate.getProgressSync() == 100) {
            title = context.getString(R.string.notification_title_finish);
        }
        else {
            title = context.getString(R.string.notification_title_unfinish);
        }
        String content = realEstate.getStringType(context) + " - " + realEstate.getPlace().getAddress();

        int progress = (int) Math.round(realEstate.getProgressSync());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationReceiver.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_room)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true);

        if (realEstate.getProgressSync() != 100) {
                builder.setProgress(100,progress,false);
        }

        notification = builder.build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void setNotification(Context context){
        CharSequence name = context.getString(R.string.notification_channel_name);
        String description = context.getString(R.string.notification_channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ID = "CHANNEL_1";
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        notificationChannel.setDescription(description);
        notificationManager.createNotificationChannel(notificationChannel) ;

        notificationManager.notify(1 , notification) ;
    }
}
