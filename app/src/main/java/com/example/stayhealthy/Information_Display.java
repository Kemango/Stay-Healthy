package com.example.stayhealthy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.CursorAdapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Information_Display extends AppCompatActivity {

    private Cursor model =null;                                                                     //SQLite
    private SH_Adapter adapter = null;                                                             //SQLite
    private StayHealthy_Helper helper =null;                                                             //SQLite
    private ListView list;                                                                          //SQLite
    private TextView empty =null;                                                                   //SQLite
    private TextView mDisplayDate = null;

    private GPSTracker gpsTracker;
    private double latitude = 0.0d;
    private double longitude = 0.0d;
    private double myLatitude = 0.0d;
    private double myLongitude = 0.0d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information__display);

        empty = findViewById(R.id.empty);
        helper = new StayHealthy_Helper(this);
        list = findViewById(R.id.StayHealthy_List);
        model =helper.getALL();
        adapter= new SH_Adapter(this, model ,0);
        list.setAdapter(adapter);
        list.setOnItemClickListener(onListClick);

        mDisplayDate = (TextView) findViewById(R.id.get_Date);
        gpsTracker = new GPSTracker(Information_Display.this);
    }
    @Override
    protected void onResume(){
        super.onResume();
        if(model!=null){
            model.close();
        }
        model = helper.getALL();
        if(model.getCount()>0){
            empty.setVisibility(View.INVISIBLE);
        }
        adapter.swapCursor(model);
    }

    @Override
    protected void onDestroy(){
        helper.close();
        super.onDestroy();
        gpsTracker.stopUsingGPS();
    }

    static class SH_Holder {
        private TextView SH_Date = null;
        private TextView SH_Name = null;
        private TextView steps = null;

        SH_Holder(View row){
            SH_Date = row.findViewById(R.id.healthDate);
            SH_Name = row.findViewById(R.id.healthName);
            steps = row.findViewById(R.id.healthSteps);
        }
        void populateFrom(Cursor c, StayHealthy_Helper helper){

            String temp ="Name: "+helper.getSH_NAME(c);                                        //List Display Date
            SH_Name.setText(temp);
            String temp1 ="Date: "+helper.getmDisplayDate(c);                                      //List Display Event
            SH_Date.setText(temp1);
            String temp2 ="Steps: "+helper.getsteps(c);                                       //List Display Describe
            steps.setText(temp2);
        }
    }

    private AdapterView.OnItemClickListener onListClick = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (gpsTracker.canGetLocation()) {
                //model.moveToPosition(position);
                //String recordID = helper.getID(model);
                // Cursor c =helper.getById(recordID);
                // c.moveToFirst();
                myLatitude = gpsTracker.getLatitude();
                myLongitude = gpsTracker.getLongitude();
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();

                model.moveToPosition(position);
                String recordID = helper.getID(model);
                Intent intent;
                intent = new Intent(Information_Display.this, StayHealthyMap.class);         //  Changing to google
                intent.putExtra("LATITUDE", latitude);
                intent.putExtra("LONGITUDE", longitude);
                intent.putExtra("MYLATITUDE", myLatitude);
                intent.putExtra("MYLONGITUDE", myLongitude);
                intent.putExtra("ID", recordID);
                Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + myLatitude
                        + "\nLong: " + myLongitude, Toast.LENGTH_LONG).show();

                // intent.putExtra("NAME", mDisplayDate.getText().toString());                       //this cost the problem
                startActivity(intent);
            }

        }
    };

    class SH_Adapter extends CursorAdapter {                                                        // sqlite
        SH_Adapter(Context context, Cursor cursor, int flags ){
            super(context, cursor ,flags);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor){
            SH_Holder holder =(SH_Holder) view.getTag();
            holder.populateFrom(cursor,helper);
        }

        @Override
        public View newView(Context context,Cursor cursor, ViewGroup parent){
            LayoutInflater inflater =getLayoutInflater();
            View row =inflater.inflate(R.layout.row,parent,false);
            SH_Holder holder= new SH_Holder(row);
            row.setTag(holder);
            return (row);
        }
    }

}