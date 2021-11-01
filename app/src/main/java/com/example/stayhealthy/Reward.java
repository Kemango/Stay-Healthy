package com.example.stayhealthy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Reward extends AppCompatActivity {
    int score = 0;
    TextView t2;                                                                                    //testing
    DatabaseReference mAuth;
    Point_System point_system;

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    Point_System userPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);
        point_system = new Point_System();

        mAuth = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Records");

        t2 =findViewById(R.id.Side_point);                                                          //testing

        user =FirebaseAuth.getInstance().getCurrentUser();
        reference =FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();
        reference.child(userID).child("Records").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userPoint = snapshot.getValue(Point_System.class);
                Integer points = userPoint.point;
                t2.setText(Integer.toString(points));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        SharedPreferences sp = this.getSharedPreferences("Myscore", Context.MODE_PRIVATE);
        score = sp.getInt("score",0);
        //t2.setText(Integer.toString(score));                                                        //testing


        findViewById(R.id.Small_Claim).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (score >= 200){
                    showWarningDialog();
                }else {
                    Toast.makeText(Reward.this, "Sry you dont have enough points", Toast.LENGTH_LONG).show();
                }
            }
        });

        findViewById(R.id.Median_claim).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (score >= 400){                                                                  //Testing purpose pls change back to 1600
                    showWarningDialog2();
                }else {
                    Toast.makeText(Reward.this, "Sry you dont have enough points", Toast.LENGTH_LONG).show();
                }
            }
        });

        findViewById(R.id.Large_Claim).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (score >= 600){                                                                  //Testing purpose pls change back to 3000
                    showWarningDialog3();
                }else {
                    Toast.makeText(Reward.this, "Sry you dont have enough points", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void showWarningDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Reward.this, R.style.AlertDialogTheme);
        View view = LayoutInflater.from(Reward.this).inflate(
                R.layout.layout_warning_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle)).setText(getResources().getString(R.string.warning_title));
        ((TextView) view.findViewById(R.id.textMessage)).setText(getResources().getString(R.string.warning_text));
        ((Button) view.findViewById(R.id.buttonYes)).setText(getResources().getString(R.string.yes));
        ((Button) view.findViewById(R.id.buttonNo)).setText(getResources().getString(R.string.no));
        ((ImageView) view.findViewById(R.id.imageIcon)).setImageResource(R.drawable.ic_warning);

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                score -= 200;
                point_system.setPoint(score);
                mAuth.setValue(point_system);                                                       //upload to database

                SharedPreferences sp = getSharedPreferences("Myscore", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("score",score);
                editor.apply();
                Intent in = new Intent(Reward.this, CreditCard_Payment.class);
                startActivity(in);
                Toast.makeText(Reward.this,"Pls enter the requirement information!",Toast.LENGTH_LONG).show();  //if wrong change to short
            }
        });

        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
                Toast.makeText(Reward.this,"Cancel",Toast.LENGTH_SHORT).show();
            }
        });
        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void showWarningDialog2(){
        AlertDialog.Builder builder2 = new AlertDialog.Builder(Reward.this, R.style.AlertDialogTheme);
        View view2 = LayoutInflater.from(Reward.this).inflate(
                R.layout.layout_warning_dialog2,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer2)
        );
        builder2.setView(view2);
        ((TextView) view2.findViewById(R.id.textTitle2)).setText(getResources().getString(R.string.warning_title));
        ((TextView) view2.findViewById(R.id.textMessage2)).setText(getResources().getString(R.string.warning_text));
        ((Button) view2.findViewById(R.id.buttonYes2)).setText(getResources().getString(R.string.yes));
        ((Button) view2.findViewById(R.id.buttonNo2)).setText(getResources().getString(R.string.no));
        ((ImageView) view2.findViewById(R.id.imageIcon2)).setImageResource(R.drawable.ic_warning);

        final AlertDialog alertDialog = builder2.create();

        view2.findViewById(R.id.buttonYes2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                score -= 400;                                                                       //Testing purpose pls change back to 1600
                point_system.setPoint(score);
                mAuth.setValue(point_system);                                                       //upload to database

                SharedPreferences sp = getSharedPreferences("Myscore", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("score",score);
                editor.apply();
                Intent in = new Intent(Reward.this, CreditCard_Payment.class);
                startActivity(in);
                Toast.makeText(Reward.this,"Pls enter the requirement information!",Toast.LENGTH_LONG).show();  //if wrong change to short
            }
        });

        view2.findViewById(R.id.buttonNo2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
                Toast.makeText(Reward.this,"Cancel",Toast.LENGTH_SHORT).show();
            }
        });
        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void showWarningDialog3(){
        AlertDialog.Builder builder3 = new AlertDialog.Builder(Reward.this, R.style.AlertDialogTheme);
        View view3 = LayoutInflater.from(Reward.this).inflate(
                R.layout.layout_warning_dialog3,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer3)
        );
        builder3.setView(view3);
        ((TextView) view3.findViewById(R.id.textTitle3)).setText(getResources().getString(R.string.warning_title));
        ((TextView) view3.findViewById(R.id.textMessage3)).setText(getResources().getString(R.string.warning_text));
        ((Button) view3.findViewById(R.id.buttonYes3)).setText(getResources().getString(R.string.yes));
        ((Button) view3.findViewById(R.id.buttonNo3)).setText(getResources().getString(R.string.no));
        ((ImageView) view3.findViewById(R.id.imageIcon3)).setImageResource(R.drawable.ic_warning);

        final AlertDialog alertDialog = builder3.create();

        view3.findViewById(R.id.buttonYes3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                score -= 600;                                                                       //Testing purpose pls change back to 3000
                point_system.setPoint(score);
                mAuth.setValue(point_system);                                                       //upload to database

                SharedPreferences sp = getSharedPreferences("Myscore", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("score",score);
                editor.apply();
                Intent in = new Intent(Reward.this, CreditCard_Payment.class);
                startActivity(in);
                Toast.makeText(Reward.this,"Pls enter the requirement information!",Toast.LENGTH_LONG).show();             //"Keep working hard to earn more points!"
            }
        });

        view3.findViewById(R.id.buttonNo3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
                Toast.makeText(Reward.this,"Cancel",Toast.LENGTH_SHORT).show();
            }
        });
        if(alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent in = new Intent(Reward.this, Home.class);
        startActivity(in);
        super.onBackPressed();
    }
}