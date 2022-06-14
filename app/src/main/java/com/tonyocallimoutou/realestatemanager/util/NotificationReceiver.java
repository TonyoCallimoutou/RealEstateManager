package com.tonyocallimoutou.realestatemanager.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.ui.MainActivity;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID = "CHANNEL_1";

    public static final String NOTIFICATION_TITLE = "TITLE";
    public static final String NOTIFICATION_CONTENT = "CONTENT";

    private Notification notification;

    private String title;
    private String content;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onReceive(Context context, Intent intent) {

        title = intent.getStringExtra(NOTIFICATION_TITLE);
        content = intent.getStringExtra(NOTIFICATION_CONTENT);

        initNotification(context);
        getNotification(context);
    }

    private void initNotification(Context context) {
        Intent activityIntent = new Intent(context, MainActivity.class);

        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(context, 0, activityIntent, PendingIntent.FLAG_MUTABLE);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NotificationReceiver.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_room)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true);

        notification = builder.build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getNotification(Context context){
        CharSequence name = context.getString(R.string.notification_channel_name);
        String description = context.getString(R.string.notification_channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
        notificationChannel.setDescription(description);
        notificationManager.createNotificationChannel(notificationChannel) ;

        notificationManager.notify(1 , notification) ;
    }
}
