package com.example.android.g4_project;

import androidx.appcompat.app.AppCompatActivity;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TimePicker;

import java.util.Calendar;

public class Notifications extends AppCompatActivity {

    private TimePicker mealTimePicker, lunchTimePicker, dinnerTimePicker;
    private CheckBox mealButton, lunchButton, dinnerButton;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_notifications);

        mealTimePicker = findViewById(R.id.mealTimePicker);
        //lunchTimePicker = findViewById(R.id.lunchTimePicker);
        //dinnerTimePicker = findViewById(R.id.dinnerTimePicker);

        int mealHour = mealTimePicker.getHour();
        //int lunchHour = lunchTimePicker.getHour();
        //int dinnerHour = dinnerTimePicker.getHour();

        mealButton = findViewById(R.id.mealTime);
        //lunchButton = findViewById(R.id.lunchTime);
        //dinnerButton = findViewById(R.id.dinnerTime);

        createNotificationChannel();

        mealButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean checked) {

                saveButton = findViewById(R.id.saveButton);

                saveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent mealTimeIntent = new Intent(Notifications.this, MealTimeReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(Notifications.this,0, mealTimeIntent, 0);

                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

                        Calendar fireCal = Calendar.getInstance();
                        Calendar currCal = Calendar.getInstance();

                        fireCal.set(Calendar.HOUR_OF_DAY, mealHour);

                        long intendedTime = fireCal.getTimeInMillis();
                        long currentTime = currCal.getTimeInMillis();

                        /*weface5529@lankew.com 0000055555*/

                        if (intendedTime >= currentTime) {
                            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, intendedTime,
                                    AlarmManager.INTERVAL_DAY, pendingIntent);
                        } else {
                            fireCal.add(Calendar.DAY_OF_MONTH, 1);
                            intendedTime = fireCal.getTimeInMillis();

                            alarmManager.setRepeating(AlarmManager.RTC, intendedTime, AlarmManager.INTERVAL_DAY, pendingIntent);
                        }
                    }
                });

            }
        });
    }

    public void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "NotificationReminderChannel";
            String description = "Channel for Notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyMeal",name,importance);

        }
    }
}