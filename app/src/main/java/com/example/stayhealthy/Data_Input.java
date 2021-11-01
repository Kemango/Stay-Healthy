package com.example.stayhealthy;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class Data_Input extends AppCompatActivity implements SensorEventListener {
    private Button SubmitButton;
    int score = 0;                                                                                  //Point system

    private static final String TAG = "Main Activity";
    private TextView mDisplayDate = null;                                                           //Date (Text View) + list view
    private DatePickerDialog.OnDateSetListener mDateSetListener;                                    //Date
    private EditText SH_NAME;                                                                       //list view//add
    private StayHealthy_Helper helper =null;
    private String SHID = "";

    private TextView location = null;
    private GPSTracker gpsTracker;
    private double latitude = 0.0d;
    private double longitude = 0.0d;
    private double myLatitude = 0.0d;
    private double myLongitude = 0.0d;

    DatabaseReference mAuth;
    Point_System point_system;

    TextView steps;
    TextToSpeech t2;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    int stepCount = 0;
    private final int High = 13;
    private final int Low = 9;
    boolean AtHeight = false;

    TextView timerText;                                                                             //Timer
    private Button stopStartButton;

    Timer timer;
    TimerTask timerTask;
    Double time = 0.0;

    boolean timerStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data__input);

        timerText = (TextView) findViewById(R.id.timerText);
        stopStartButton = (Button) findViewById(R.id.startStopButton);
        //stopStartButton.setOnClickListener(startstop);

        timer = new Timer();

        location = findViewById(R.id.location);
        gpsTracker = new GPSTracker(Data_Input.this);

        mAuth = FirebaseDatabase.getInstance().getReference("Users")
                //.child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Records");
        point_system = new Point_System();

        mDisplayDate = (TextView) findViewById(R.id.get_Date);
        SH_NAME = findViewById(R.id.get_Name);

        helper = new StayHealthy_Helper(this);

        t2 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR)
                {
                    t2.setLanguage(Locale.UK);
                }
            }
        });

        SHID = getIntent().getStringExtra("ID");
        if (SHID != null) {
            load();
        }

        steps = findViewById(R.id.tv_steps);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel =new NotificationChannel("my notification", "my notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager =getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        SharedPreferences sp = this.getSharedPreferences("Myscore", Context.MODE_PRIVATE);
        score = sp.getInt("score", 0);

        SubmitButton = findViewById(R.id.submit_button);
        SubmitButton.setOnClickListener(onSave4);

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gpsTracker.canGetLocation()) {
                    latitude = gpsTracker.getLatitude();
                    longitude = gpsTracker.getLongitude();
                    location.setText(String.valueOf(latitude) + ", " + String.valueOf(longitude));
                    // \n is for the new line
                    Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude
                            + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                }
            }
        });

        mDisplayDate.setOnClickListener(new View.OnClickListener() {                                //Date functions
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        Data_Input.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {                               //Display Date
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String combineStr = dayOfMonth + "/" + (month+1) + "/" + year;
                Toast.makeText(view.getContext(), combineStr, Toast.LENGTH_LONG).show();
                Log.d(TAG, "onDateSet: date: " + dayOfMonth + "/" + month + "/" + year);
                mDisplayDate.setText(dayOfMonth + "/" + (month+1) + "/" + year);
            }
        };

    }

    protected void onResume() {
        super.onResume();
        // turn on the sensor
        mSensorManager.registerListener(this, mSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);    // turn off listener to save power
    }

    private View.OnClickListener onSave4 = new View.OnClickListener() {                             // Button for Data Input
        @Override
        public void onClick(View v) {
            if (stepCount >= 0 && stepCount <= 9) {                                                                         // Tier 0
                showWarningDialog4();

            }if (stepCount >= 10 && stepCount <= 19) {                                                                         // Tier 1
                showSuccessDialog1();
            }
            if (stepCount >= 20 && stepCount <= 29) {                                                                          // Tier 2
                showSuccessDialog2();
            }
            if (stepCount >= 30 && stepCount <= 39) {                                                                         // Tier 3
                showSuccessDialog3();
            }
            if (stepCount >= 40 && stepCount <= 49) {                                                                        // Tier 4
                showSuccessDialog4();
            }
            if (stepCount >= 50 ) {                                                                        // Tier 5
                showSuccessDialog5();
            }
        }
    };

    @Override
    public void onBackPressed() {                                                                   //what will happen after u press back
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Are you sure you want to Exit?"+'\n'+"Data will not be save!")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i){
                        startActivity(new Intent(Data_Input.this,Home.class));
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
        // Intent in = new Intent(Data_Input.this, Home.class);
        // startActivity(in);
        //super.onBackPressed();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float X = event.values[0];
        float Y = event.values[1];
        float Z = event.values[2];
        double mg = Math.round((X * X) + (Y * Y) + (Z + Z));
        if ((mg > High) && (AtHeight == false)) {
            AtHeight = true;
        }
        if ((mg < Low) && (AtHeight == true)) {
            stepCount++;
            steps.setText(String.valueOf(stepCount));
            AtHeight = false;
            if(stepCount == 10 ){                                                                                                                 //notification when reached every tier
                NotificationCompat.Builder builder = new NotificationCompat.Builder(Data_Input.this,"my notification");
                builder.setContentTitle("Stay Healthy");
                builder.setContentText("Tier 1 Reached");
                builder.setSmallIcon(R.drawable.ic_warning);
                builder.setAutoCancel(true);
                t2.speak("Congratulation! You reach you Daily Goals",TextToSpeech.QUEUE_FLUSH,null);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(Data_Input.this);
                managerCompat.notify(1,builder.build());
            }if(stepCount == 20 ){                                                                                                                //notification when reached every tier
                NotificationCompat.Builder builder = new NotificationCompat.Builder(Data_Input.this,"my notification");
                builder.setContentTitle("Stay Healthy");
                builder.setContentText("Tier 2 Reached");
                builder.setSmallIcon(R.drawable.ic_warning);
                builder.setAutoCancel(true);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(Data_Input.this);
                managerCompat.notify(1,builder.build());
            }if(stepCount == 30 ){                                                                                                                //notification when reached every tier
                NotificationCompat.Builder builder = new NotificationCompat.Builder(Data_Input.this,"my notification");
                builder.setContentTitle("Stay Healthy");
                builder.setContentText("Tier 3 Reached");
                builder.setSmallIcon(R.drawable.ic_warning);
                builder.setAutoCancel(true);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(Data_Input.this);
                managerCompat.notify(1,builder.build());
            }if(stepCount == 40 ){                                                                                                                  //notification when reached every tier
                NotificationCompat.Builder builder = new NotificationCompat.Builder(Data_Input.this,"my notification");
                builder.setContentTitle("Stay Healthy");
                builder.setContentText("Tier 4 Reached");
                builder.setSmallIcon(R.drawable.ic_warning);
                builder.setAutoCancel(true);

                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(Data_Input.this);
                managerCompat.notify(1,builder.build());
            }if(stepCount == 50 ){                                                                                                                  //notification when reached every tier
                NotificationCompat.Builder builder = new NotificationCompat.Builder(Data_Input.this,"my notification");
                builder.setContentTitle("Stay Healthy");
                builder.setContentText("Tier 5 Reached");
                builder.setSmallIcon(R.drawable.ic_warning);
                builder.setAutoCancel(true);
                t2.speak("Good working reaching to the end! You have reached the Max Tier",TextToSpeech.QUEUE_FLUSH,null);
                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(Data_Input.this);
                managerCompat.notify(1,builder.build());
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    //Tier1
    private void showSuccessDialog1() {
        AlertDialog.Builder builder_S1 = new AlertDialog.Builder(Data_Input.this, R.style.AlertDialogTheme);
        View view_S1 = LayoutInflater.from(Data_Input.this).inflate(
                R.layout.layout_success_dialog,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer_S1)
        );
        builder_S1.setView(view_S1);
        ((TextView) view_S1.findViewById(R.id.textTitle_S1)).setText(getResources().getString(R.string.success_title));
        ((TextView) view_S1.findViewById(R.id.textMessage_S1)).setText(getResources().getString(R.string.success_text));
        ((Button) view_S1.findViewById(R.id.buttonYes_S1)).setText(getResources().getString(R.string.yes));
        ((Button) view_S1.findViewById(R.id.buttonNo_S1)).setText(getResources().getString(R.string.no));
        ((ImageView) view_S1.findViewById(R.id.imageIcon_S1)).setImageResource(R.drawable.ic_baseline_done_24);

        final AlertDialog alertDialog = builder_S1.create();

        view_S1.findViewById(R.id.buttonYes_S1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                String nameDate = mDisplayDate.getText().toString();                                //SQlite Date
                String nameName = SH_NAME.getText().toString();                                     //SQlite Name
                String nameStep = steps.getText().toString();

                if (SHID == null) {                                                                 //Transfer to SQlite
                    //Insert record into SQLite table
                    helper.insert(nameName,nameDate,nameStep,latitude, longitude);
                } else {
                    helper.update(SHID,nameName,nameDate,nameStep,latitude, longitude);
                }
                score += 100;
                point_system.setPoint(score);
                mAuth.setValue(point_system);                                                       //upload to database

                SharedPreferences sp = getSharedPreferences("Myscore", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("score", score);
                editor.apply();
                Toast.makeText(Data_Input.this, "Good Job! 100 points have been added!", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(Data_Input.this, Home.class);
                startActivity(in);
            }
        });

        view_S1.findViewById(R.id.buttonNo_S1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
                Toast.makeText(Data_Input.this, "Cancel", Toast.LENGTH_SHORT).show();
            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    //Tier2
    private void showSuccessDialog2() {
        AlertDialog.Builder builder_S2 = new AlertDialog.Builder(Data_Input.this, R.style.AlertDialogTheme);
        View view_S2 = LayoutInflater.from(Data_Input.this).inflate(
                R.layout.layout_success_dialog2,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer_S2)
        );
        builder_S2.setView(view_S2);
        ((TextView) view_S2.findViewById(R.id.textTitle_S2)).setText(getResources().getString(R.string.success_title));
        ((TextView) view_S2.findViewById(R.id.textMessage_S2)).setText(getResources().getString(R.string.success_text2));
        ((Button) view_S2.findViewById(R.id.buttonYes_S2)).setText(getResources().getString(R.string.yes));
        ((Button) view_S2.findViewById(R.id.buttonNo_S2)).setText(getResources().getString(R.string.no));
        ((ImageView) view_S2.findViewById(R.id.imageIcon_S2)).setImageResource(R.drawable.ic_baseline_done_24);

        final AlertDialog alertDialog = builder_S2.create();

        view_S2.findViewById(R.id.buttonYes_S2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameDate = mDisplayDate.getText().toString();                                //SQlite Date
                String nameName = SH_NAME.getText().toString();                                     //SQlite Name
                String nameStep = steps.getText().toString();

                if (SHID == null) {                                                                 //Transfer to SQlite
                    //Insert record into SQLite table
                    helper.insert(nameName,nameDate,nameStep,latitude, longitude);
                } else {
                    helper.update(SHID,nameName,nameDate,nameStep,latitude, longitude);
                }
                alertDialog.dismiss();
                score += 200;
                point_system.setPoint(score);
                mAuth.setValue(point_system);                                                       //upload to database

                SharedPreferences sp = getSharedPreferences("Myscore", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("score", score);
                editor.apply();
                Toast.makeText(Data_Input.this, "Good Job! 200 points have been added!", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(Data_Input.this, Home.class);
                startActivity(in);
            }
        });

        view_S2.findViewById(R.id.buttonNo_S2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
                Toast.makeText(Data_Input.this, "Cancel", Toast.LENGTH_SHORT).show();
            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    //Tier3
    private void showSuccessDialog3() {
        AlertDialog.Builder builder_S3 = new AlertDialog.Builder(Data_Input.this, R.style.AlertDialogTheme);
        View view_S3 = LayoutInflater.from(Data_Input.this).inflate(
                R.layout.layout_success_dialog3,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer_S3)
        );
        builder_S3.setView(view_S3);
        ((TextView) view_S3.findViewById(R.id.textTitle_S3)).setText(getResources().getString(R.string.success_title));
        ((TextView) view_S3.findViewById(R.id.textMessage_S3)).setText(getResources().getString(R.string.success_text3));
        ((Button) view_S3.findViewById(R.id.buttonYes_S3)).setText(getResources().getString(R.string.yes));
        ((Button) view_S3.findViewById(R.id.buttonNo_S3)).setText(getResources().getString(R.string.no));
        ((ImageView) view_S3.findViewById(R.id.imageIcon_S3)).setImageResource(R.drawable.ic_baseline_done_24);

        final AlertDialog alertDialog = builder_S3.create();

        view_S3.findViewById(R.id.buttonYes_S3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameDate = mDisplayDate.getText().toString();                                //SQlite Date
                String nameName = SH_NAME.getText().toString();                                     //SQlite Name
                String nameStep = steps.getText().toString();

                if (SHID == null) {                                                                 //Transfer to SQlite
                    //Insert record into SQLite table
                    helper.insert(nameName,nameDate,nameStep,latitude, longitude);
                } else {
                    helper.update(SHID,nameName,nameDate,nameStep,latitude, longitude);
                }
                alertDialog.dismiss();
                score += 300;
                point_system.setPoint(score);
                mAuth.setValue(point_system);                                                       //upload to database

                SharedPreferences sp = getSharedPreferences("Myscore", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("score", score);
                editor.apply();
                Toast.makeText(Data_Input.this, "Good Job! 300 points have been added!", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(Data_Input.this, Home.class);
                startActivity(in);
            }
        });

        view_S3.findViewById(R.id.buttonNo_S3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
                Toast.makeText(Data_Input.this, "Cancel", Toast.LENGTH_SHORT).show();
            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    //Tier4
    private void showSuccessDialog4() {
        AlertDialog.Builder builder_S4 = new AlertDialog.Builder(Data_Input.this, R.style.AlertDialogTheme);
        View view_S4 = LayoutInflater.from(Data_Input.this).inflate(
                R.layout.layout_success_dialog4,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer_S4)
        );
        builder_S4.setView(view_S4);
        ((TextView) view_S4.findViewById(R.id.textTitle_S4)).setText(getResources().getString(R.string.success_title));
        ((TextView) view_S4.findViewById(R.id.textMessage_S4)).setText(getResources().getString(R.string.success_text4));
        ((Button) view_S4.findViewById(R.id.buttonYes_S4)).setText(getResources().getString(R.string.yes));
        ((Button) view_S4.findViewById(R.id.buttonNo_S4)).setText(getResources().getString(R.string.no));
        ((ImageView) view_S4.findViewById(R.id.imageIcon_S4)).setImageResource(R.drawable.ic_baseline_done_24);

        final AlertDialog alertDialog = builder_S4.create();

        view_S4.findViewById(R.id.buttonYes_S4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameDate = mDisplayDate.getText().toString();                                //SQlite Date
                String nameName = SH_NAME.getText().toString();                                     //SQlite Name
                String nameStep = steps.getText().toString();

                if (SHID == null) {                                                                 //Transfer to SQlite
                    //Insert record into SQLite table
                    helper.insert(nameName,nameDate,nameStep,latitude, longitude);
                } else {
                    helper.update(SHID,nameName,nameDate,nameStep,latitude, longitude);
                }
                alertDialog.dismiss();
                score += 400;
                point_system.setPoint(score);
                mAuth.setValue(point_system);                                                       //upload to database

                SharedPreferences sp = getSharedPreferences("Myscore", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("score", score);
                editor.apply();
                Toast.makeText(Data_Input.this, "Good Job! 400 points have been added!", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(Data_Input.this, Home.class);
                startActivity(in);
            }
        });

        view_S4.findViewById(R.id.buttonNo_S4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
                Toast.makeText(Data_Input.this, "Cancel", Toast.LENGTH_SHORT).show();
            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    //Tier5
    private void showSuccessDialog5() {
        AlertDialog.Builder builder_S5 = new AlertDialog.Builder(Data_Input.this, R.style.AlertDialogTheme);
        View view_S5 = LayoutInflater.from(Data_Input.this).inflate(
                R.layout.layout_success_dialog5,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer_S5)
        );
        builder_S5.setView(view_S5);
        ((TextView) view_S5.findViewById(R.id.textTitle_S5)).setText(getResources().getString(R.string.success_title));
        ((TextView) view_S5.findViewById(R.id.textMessage_S5)).setText(getResources().getString(R.string.success_text4));
        ((Button) view_S5.findViewById(R.id.buttonYes_S5)).setText(getResources().getString(R.string.yes));
        ((Button) view_S5.findViewById(R.id.buttonNo_S5)).setText(getResources().getString(R.string.no));
        ((ImageView) view_S5.findViewById(R.id.imageIcon_S5)).setImageResource(R.drawable.ic_baseline_done_24);

        final AlertDialog alertDialog = builder_S5.create();

        view_S5.findViewById(R.id.buttonYes_S5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameDate = mDisplayDate.getText().toString();                                //SQlite Date
                String nameName = SH_NAME.getText().toString();                                     //SQlite Name
                String nameStep = steps.getText().toString();

                if (SHID == null) {                                                                 //Transfer to SQlite
                    //Insert record into SQLite table
                    helper.insert(nameName,nameDate,nameStep,latitude, longitude);
                } else {
                    helper.update(SHID,nameName,nameDate,nameStep,latitude, longitude);
                }
                alertDialog.dismiss();
                score += 500;
                point_system.setPoint(score);
                mAuth.setValue(point_system);                                                       //upload to database

                SharedPreferences sp = getSharedPreferences("Myscore", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("score", score);
                editor.apply();
                Toast.makeText(Data_Input.this, "Good Job! 500 points have been added!", Toast.LENGTH_SHORT).show();
                Intent in = new Intent(Data_Input.this, Home.class);
                startActivity(in);
            }
        });

        view_S5.findViewById(R.id.buttonNo_S5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
                Toast.makeText(Data_Input.this, "Cancel", Toast.LENGTH_SHORT).show();
            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    //Warning
    private void showWarningDialog4() {
        AlertDialog.Builder builder4 = new AlertDialog.Builder(Data_Input.this, R.style.AlertDialogTheme);
        View view4 = LayoutInflater.from(Data_Input.this).inflate(
                R.layout.layout_warning_dialog4,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer4)
        );
        builder4.setView(view4);
        ((TextView) view4.findViewById(R.id.textTitle4)).setText(getResources().getString(R.string.warning_title));
        ((TextView) view4.findViewById(R.id.textMessage4)).setText(getResources().getString(R.string.warning_text2));
        ((Button) view4.findViewById(R.id.buttonYes4)).setText(getResources().getString(R.string.yes));
        ((Button) view4.findViewById(R.id.buttonNo4)).setText(getResources().getString(R.string.no));
        ((ImageView) view4.findViewById(R.id.imageIcon4)).setImageResource(R.drawable.ic_warning);

        final AlertDialog alertDialog = builder4.create();

        view4.findViewById(R.id.buttonYes4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameDate = mDisplayDate.getText().toString();                                //SQlite Date
                String nameName = SH_NAME.getText().toString();                                     //SQlite Name
                String nameStep = steps.getText().toString();

                if (SHID == null) {                                                                 //Transfer to SQlite
                    //Insert record into SQLite table
                    helper.insert(nameName,nameDate,nameStep,latitude, longitude);
                } else {
                    helper.update(SHID,nameName,nameDate,nameStep,latitude, longitude);
                }
                alertDialog.dismiss();
                Intent in = new Intent(Data_Input.this, Home.class);
                startActivity(in);
                Toast.makeText(Data_Input.this, "Sry you don't have enough steps, Try again next time!", Toast.LENGTH_SHORT).show();  //if wrong change to short
            }
        });

        view4.findViewById(R.id.buttonNo4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
                Toast.makeText(Data_Input.this, "Cancel", Toast.LENGTH_SHORT).show();
            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    @Override
    protected void onDestroy(){
        helper.close();
        super.onDestroy();
        gpsTracker.stopUsingGPS();
    }

    private void load() {
        Cursor c = helper.getById(SHID);
        c.moveToFirst();
        mDisplayDate.setText(helper.getmDisplayDate(c));
        SH_NAME.setText(helper.getSH_NAME(c));
        latitude = helper.getLatitude(c);
        longitude = helper.getLongitude(c);
        location.setText(String.valueOf(latitude) + ", " + String.valueOf(longitude));
    }

    public void startStopTapped(View v) {
        if(timerStarted == false)
        {
            timerStarted = true;
            stopStartButton.setText("STOP");
            stopStartButton.setTextColor(ContextCompat.getColor(this,R.color.red));

            startTimer();
        }
        else
        {
            timerStarted = false;
            stopStartButton.setText("START");
            stopStartButton.setTextColor(ContextCompat.getColor(this,R.color.green));

            timerTask.cancel();
        }
    }

    private void startTimer()
    {
        timerTask = new TimerTask()
        {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        time++;
                        timerText.setText(getTimerText());
                    }
                });
            }

        };
        timer.scheduleAtFixedRate(timerTask, 0 ,1000);
    }

    private String getTimerText()
    {
        int rounded = (int) Math.round(time);

        int seconds = ((rounded % 86400) % 3600) % 60;
        int minutes = ((rounded % 86400) % 3600) / 60;
        int hours = ((rounded % 86400) / 3600);

        return formatTime(seconds, minutes, hours);
    }

    private String formatTime(int seconds, int minutes, int hours)
    {
        return String.format("%02d",hours) + " : " + String.format("%02d",minutes) + " : " + String.format("%02d",seconds);
    }

}