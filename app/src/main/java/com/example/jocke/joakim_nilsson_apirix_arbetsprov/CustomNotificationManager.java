package com.example.jocke.joakim_nilsson_apirix_arbetsprov;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jocke on 2016-11-30.
 */

public class CustomNotificationManager {
    
    private static final int ID_SMALL_NOTIFICATION = 233;
    private Context context;
    private static final int ID_BIG_NOTIFICATION = 234;
    

    CustomNotificationManager(Context context) {
        this.context = context;
    }
    
    public void showSmallNotification(String title, String message, Intent intent) {
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        context,
                        ID_SMALL_NOTIFICATION,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        
        
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        Notification notification;
            notification = mBuilder.setSmallIcon(R.mipmap.ic_launcher).setTicker( "Ticker " +title).setWhen(0)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent)
                .setContentTitle("Title " +title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentText("Message " +message)
                .build();
        
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ID_SMALL_NOTIFICATION, notification);
    }

    //The method will return Bitmap from an image URL
    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



}
