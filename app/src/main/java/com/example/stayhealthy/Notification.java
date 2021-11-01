package com.example.stayhealthy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class Notification extends AppCompatActivity implements View.OnClickListener {

    private int notificationId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // Set onClick Listener
        findViewById(R.id.noti_setbtn).setOnClickListener(this);
        findViewById(R.id.noti_cancelbtn).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        TimePicker timePicker = findViewById(R.id.noti_timepicker);

        // Intent
        Intent intent = new Intent(Notification.this, AlarmReceiver.class);
        intent.putExtra("notificationId", notificationId);

        // PendingIntent
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                Notification.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT
        );

        // AlarmManager
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        switch (view.getId()) {
            case R.id.noti_setbtn:
                int hour = timePicker.getCurrentHour();
                int minute = timePicker.getCurrentMinute();

                // Create time.
                Calendar startTime = Calendar.getInstance();
                startTime.set(Calendar.HOUR_OF_DAY, hour);
                startTime.set(Calendar.MINUTE, minute);
                startTime.set(Calendar.SECOND, 0);
                long alarmStartTime = startTime.getTimeInMillis();

                // Set Alarm
                alarmManager.set(AlarmManager.RTC_WAKEUP, alarmStartTime, pendingIntent);
                Toast.makeText(this, "Done!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, Home.class));
                break;

            case R.id.noti_cancelbtn:
                alarmManager.cancel(pendingIntent);
                Toast.makeText(this, "Canceled.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, Home.class));
                break;
        }

    }
}
