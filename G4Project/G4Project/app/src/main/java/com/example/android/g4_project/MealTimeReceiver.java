package com.example.android.g4_project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MealTimeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive (Context context, Intent intent) {

        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(context, "notifyMeal")
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("It's Meal Time!!")
                .setContentText("Open the app now to find restaurants for you.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager.notify(200, notiBuilder.build());
    }
}
