package com.example.notealarm;


import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class AlarmReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        String n_text = "";
        if (extras != null) {
            n_text = extras.getString("text");
        }

        Intent nextActivity = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, nextActivity, PendingIntent.FLAG_MUTABLE);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notealarm")
                .setSmallIcon(R.drawable.roundedbutton)
                .setContentTitle("Напоминание")
                .setContentText(n_text)
                .setSubText(context.getString(R.string.app_name))
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis());


        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        try {
            notificationManagerCompat.notify(123, builder.build());
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }

    }
}
