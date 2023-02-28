package com.example.serviciu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="info6.db";
    public static final String TABLE_NAME="info_table";
    public static final String TABLE_NAME2="info_table_pre";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db=this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table info_table (ID INTEGER PRIMARY " +
                "KEY AUTOINCREMENT, DATE TEXT, LONGITUDE TEXT, LATITUDE TEXT)");
        sqLiteDatabase.execSQL("create table info_table_pre (ID INTEGER PRIMARY " +
                "KEY AUTOINCREMENT, DATE TEXT, LONGITUDE TEXT, LATITUDE TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //if exists drop old table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME2);
        onCreate(sqLiteDatabase);
    }
    public boolean insertData (String data, String Long, String Lat){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("DATE", data);
        cv.put("LONGITUDE", Long);
        cv.put("LATITUDE", Lat);
        long result = sqLiteDatabase.insert(TABLE_NAME, null, cv);
        if (result == -1) return false;
        else return true;

    }

    public boolean insertDataPre (String data, String Long, String Lat){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("DATE", data);
        cv.put("LONGITUDE", Long);
        cv.put("LATITUDE", Lat);
        long result = sqLiteDatabase.insert(TABLE_NAME2, null, cv);
        if (result == -1) return false;
        else return true;

    }
    public Cursor getAllData(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor res= sqLiteDatabase.rawQuery("select * from "+ TABLE_NAME, null);
        return res;
    }

    public Cursor getAllDataPre(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        Cursor res= sqLiteDatabase.rawQuery("select * from "+ TABLE_NAME2, null);
        return res;
    }

    public void delete(String nume){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+TABLE_NAME+" WHERE Name= "+"'"+nume+"'");
        db.close();
    }

}
