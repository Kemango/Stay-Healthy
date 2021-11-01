package com.example.stayhealthy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Goals extends AppCompatActivity {
    private Button buttonGoals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goals);

        buttonGoals = findViewById(R.id.SetGoals_BTN);
        buttonGoals.setOnClickListener(onSaveGoals);

      /*  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel =new NotificationChannel("my notification", "my notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager =getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }*/
    }


    private View.OnClickListener onSaveGoals = new View.OnClickListener() {                             // Button for Data Input
        @Override
        public void onClick(View v) {
/*
            NotificationCompat.Builder builder = new NotificationCompat.Builder(Goals.this,"my notification");
            builder.setContentTitle("MY TITLE");
            builder.setContentText("TESTING");
            builder.setSmallIcon(R.drawable.ic_warning);
            builder.setAutoCancel(true);

            NotificationManagerCompat managerCompat = NotificationManagerCompat.from(Goals.this);
            managerCompat.notify(1,builder.build());


 */
            Intent intent;
            intent = new Intent(Goals.this, Notification.class);
            startActivity(intent);

        }
    };
}