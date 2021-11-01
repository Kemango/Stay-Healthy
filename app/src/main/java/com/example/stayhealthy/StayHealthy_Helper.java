package com.example.stayhealthy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StayHealthy_Helper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Data_Input.db";
    private static final int SCHEMA_VERSION = 1;

    public StayHealthy_Helper (Context context){
        super(context,DATABASE_NAME,null,SCHEMA_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE Stayhealthy_table ( _id INTEGER PRIMARY KEY AUTOINCREMENT, SH_NAME Text,"+
                "mDisplayDate TEXT, steps TEXT,lat REAL, lon REAL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }

    public Cursor getALL(){
        return (getReadableDatabase().rawQuery(
                "SELECT _id, SH_NAME, mDisplayDate, steps, lat, lon FROM Stayhealthy_table ORDER BY SH_NAME",null));
    }

    public Cursor getById(String id) {
        String[] args = {id};

        return (getReadableDatabase().rawQuery(
                "SELECT _id, SH_NAME, mDisplayDate, steps, lat, lon FROM Stayhealthy_table WHERE _ID = ?", args));
    }

    public void update(String id, String SH_NAME, String mDisplayDate, String steps, double lat, double lon) {
        ContentValues cv = new ContentValues();
        String[] args = {id};
        cv.put("SH_NAME",SH_NAME);
        cv.put("mDisplayDate",mDisplayDate);
        cv.put("steps",steps);
        cv.put("lat", lat);
        cv.put("lon", lon);


        getWritableDatabase().update("Stayhealthy_table", cv, " _ID = ?", args);
    }

    public void insert( String SH_NAME, String mDisplayDate,String steps, double lat, double lon) {
        ContentValues cv =new ContentValues();

        cv.put("SH_NAME",SH_NAME);
        cv.put("mDisplayDate",mDisplayDate);
        cv.put("steps",steps);
        cv.put("lat", lat);
        cv.put("lon", lon);


        getWritableDatabase().insert("Stayhealthy_table","SH_NAME", cv);
    }
    public String getID(Cursor c) { return (c.getString(0));}
    public String getSH_NAME(Cursor c){
        return (c.getString(1));
    }
    public String getmDisplayDate(Cursor c){
        return (c.getString(2));
    }
    public String getsteps(Cursor c){
        return (c.getString(3));
    }
    public Double getLatitude(Cursor c) {
        return (c.getDouble(4));
    }
    public Double getLongitude(Cursor c) {
        return (c.getDouble(5));
    }
}