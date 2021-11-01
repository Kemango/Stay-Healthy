package com.example.stayhealthy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CreditCard_Payment extends AppCompatActivity {

    private Button PayNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card__payment);

        PayNow = findViewById(R.id.Pay_now);
        PayNow.setOnClickListener(onSavePay);
    }

    private View.OnClickListener onSavePay = new View.OnClickListener() {                             // CardView for Data Input
        @Override
        public void onClick(View v) {

            Intent intent;
            intent = new Intent(CreditCard_Payment.this, Home.class);
            startActivity(intent);
        }
    };
}