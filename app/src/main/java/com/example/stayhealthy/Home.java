package com.example.stayhealthy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Home extends AppCompatActivity {
    private DrawerLayout mDrawerlayout;
    private ActionBarDrawerToggle mToggle;

    private CardView CardViewSave1;
    private CardView CardViewSave2;
    private CardView CardViewSave3;
    private CardView CardViewSave4;

    TextView t1;
    int score = 0;
    DatabaseReference mAuth;

    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;

    Point_System userPoint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Records");

        mDrawerlayout = (DrawerLayout) findViewById(R.id.drawer);
        mToggle = new ActionBarDrawerToggle(this,mDrawerlayout,R.string.open,R.string.close);
        mDrawerlayout.addDrawerListener(mToggle);
        NavigationView nvDrawer =(NavigationView) findViewById(R.id.nv);                            //Read NavigationView
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupDrawerContent(nvDrawer);                                                               //NavigationView

        CardViewSave1 = findViewById(R.id.Data_CardView);
        CardViewSave1.setOnClickListener(onSave1);
        CardViewSave1.setOnTouchListener(touch_display1);

        CardViewSave2 = findViewById(R.id.Information_CardView);
        CardViewSave2.setOnClickListener(onSave2);
        CardViewSave2.setOnTouchListener(touch_display2);

        CardViewSave3 = findViewById(R.id.Music_CardView);
        CardViewSave3.setOnClickListener(onSave3);
        CardViewSave3.setOnTouchListener(touch_display3);

        CardViewSave4 = findViewById(R.id.Reward_CardView);
        CardViewSave4.setOnClickListener(onSave4);
        CardViewSave4.setOnTouchListener(touch_display4);

        t1 =findViewById(R.id.home_Point);

        user =FirebaseAuth.getInstance().getCurrentUser();
        reference =FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();
        final TextView username = (TextView)findViewById(R.id.greeting);

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
               /*Point_System */userPoint = snapshot.getValue(Point_System.class);


                if(userProfile != null)
                {
                    String fullname = userProfile.fullName;
                    username.setText("Welcome " + fullname + " !");
                  //  mAuth.get



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Home.this,"Something wrong happened!", Toast.LENGTH_SHORT).show();
            }
        });
        reference.child(userID).child("Records").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userPoint = snapshot.getValue(Point_System.class);
                Integer points = userPoint.point;
                t1.setText(Integer.toString(points));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        SharedPreferences sp = this.getSharedPreferences("Myscore", Context.MODE_PRIVATE);
        score = sp.getInt("score", 0);
   //    t1.setText(Integer.toString(score));
    }

    public void selectItemDrawer(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.Profile:
                Intent intent;
                intent = new Intent(Home.this, Profile.class);                   //About
                Toast.makeText(this, "Welcome to your profile",Toast.LENGTH_LONG).show();
                startActivity(intent);
                break;
            case R.id.Goals:
                intent = new Intent(Home.this, Goals.class);                   //About
                Toast.makeText(this, "A Goal without timeline is just a dream!",Toast.LENGTH_LONG).show();
                startActivity(intent);
                break;
            case R.id.about:
                intent = new Intent(Home.this, Intent_about.class);                   //About
                Toast.makeText(this, "For More Information",Toast.LENGTH_LONG).show();
                startActivity(intent);
                break;
            case (R.id.logout):
                FirebaseAuth.getInstance().signOut();                                               //Logout
                onBackPressed();
                //startActivity(new Intent(Home.this,Login_Page.class));
                break;
        }
    }
    private void setupDrawerContent(NavigationView navigationView){                                 //Allow me to click into my navigationView
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectItemDrawer(item);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {                                          //allow the navigation button to rotate and change
        if (mToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private View.OnClickListener onSave1 = new View.OnClickListener() {                             // CardView for Data Input
        @Override
        public void onClick(View v) {

            Intent intent;
            intent = new Intent(Home.this, Data_Input.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener onSave2 = new View.OnClickListener() {                             // CardView for Data Input
        @Override
        public void onClick(View v) {

            Intent intent;
            intent = new Intent(Home.this, Information_Display.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener onSave3 = new View.OnClickListener() {                             // CardView for Data Input
        @Override
        public void onClick(View v) {

            Intent intent;
            intent = new Intent(Home.this, Music.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener onSave4 = new View.OnClickListener() {                             // CardView for Data Input
        @Override
        public void onClick(View v) {

            Intent intent;
            intent = new Intent(Home.this, Reward.class);
            startActivity(intent);
        }
    };

    private View.OnTouchListener touch_display1= new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_UP) {
                CardViewSave1.setBackgroundColor(Color.argb(255,127,255,212));
            } else if(event.getAction() == MotionEvent.ACTION_DOWN) {
                CardViewSave1.setBackgroundColor(Color.argb(100,127,255,212));
            }
            return false;
        }
    };

    private View.OnTouchListener touch_display2= new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_UP) {
                CardViewSave2.setBackgroundColor(Color.argb(255,127,255,212));
            } else if(event.getAction() == MotionEvent.ACTION_DOWN) {
                CardViewSave2.setBackgroundColor(Color.argb(100,127,255,212));
            }
            return false;
        }
    };

    private View.OnTouchListener touch_display3= new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_UP) {
                CardViewSave3.setBackgroundColor(Color.argb(255,127,255,212));
            } else if(event.getAction() == MotionEvent.ACTION_DOWN) {
                CardViewSave3.setBackgroundColor(Color.argb(100,127,255,212));
            }
            return false;
        }
    };

    private View.OnTouchListener touch_display4= new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_UP) {
                CardViewSave4.setBackgroundColor(Color.argb(255,127,255,212));
            } else if(event.getAction() == MotionEvent.ACTION_DOWN) {
                CardViewSave4.setBackgroundColor(Color.argb(100,127,255,212));
            }
            return false;
        }
    };

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Are you sure you want to Log Out?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i){
                        startActivity(new Intent(Home.this,Login_Page.class));
                        //finish();
                        //System.exit(0);
                    }
                })

                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}