package com.example.stayhealthy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.widget.TextView;

import java.util.Locale;

public class Splash_Screen extends AppCompatActivity {
    TextToSpeech t1;
    TextView TV1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash__screen);
        getSupportActionBar().hide();

        TV1=(TextView) findViewById(R.id.StayHealthy);

        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR)
                {
                    t1.setLanguage(Locale.ENGLISH);
                }
            }
        });
        
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                String toSpeak =TV1.getText().toString();
                t1.speak(toSpeak,TextToSpeech.QUEUE_FLUSH,null);
                Intent mainIntent = new Intent(Splash_Screen.this,Login_Page.class);
                Splash_Screen.this.startActivity(mainIntent);
                Splash_Screen.this.finish();
            }
        }, 3000);

    }
}