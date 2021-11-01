package com.example.stayhealthy;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class Intent_about extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intent_about);
        getSupportActionBar().hide();
    }
}