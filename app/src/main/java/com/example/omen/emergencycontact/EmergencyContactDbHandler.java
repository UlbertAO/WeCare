package com.example.omen.emergencycontact;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class EmergencyContactDbHandler extends SQLiteOpenHelper {

    public static final String DATABASE_NAME= "wecare.db";
    public static final int DB_VIRSION=1;
    public static final String TABLE_NAME="emergency_contacts";

    public static final String name="name";
    public static final String phone_number="phone_number";


    public EmergencyContactDbHandler(@Nullable Context context) {
        super(context, TABLE_NAME, null, DB_VIRSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //create tabe query
        String create="CREATE TABLE "+TABLE_NAME + " ( "+ phone_number + " TEXT PRIMARY KEY," + name + " TEXT)";
        db.execSQL(create);
        Log.d("asd", "onCreate: db created ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean insertContact(EmergencyContact emergencyContact){
        SQLiteDatabase db=this.getWritableDatabase();

        ContentValues contentValues=new ContentValues();
        contentValues.put(phone_number,emergencyContact.getPhoneNumber());
        contentValues.put(name,emergencyContact.getName());

        long result=db.insert(TABLE_NAME,null,contentValues);
        db.close();
        if(result==-1){
            Log.d("asd", "insertion Failed");
            return false;
        }
        Log.d("asd", "inserted :"+ result+" : "+emergencyContact.getPhoneNumber());
        return true;
    }

    public void deleteContact(EmergencyContact emergencyContact){
        SQLiteDatabase db=this.getWritableDatabase();
//        db.execSQL("DELETE FROM "+TABLE_NAME+" WHERE "+phone_number+"='"+
//                emergencyContact.getPhoneNumber()+"' AND "+name+"='"+emergencyContact.getName()+"'");
//        db.delete(TABLE_NAME,phone_number+"=? AND "+name+"=?",
        int result=db.delete(TABLE_NAME,phone_number+"=?", new String[]{emergencyContact.getPhoneNumber()});
        db.close();
        if(result==-1) Log.d("asd", "NOT deleted :"+emergencyContact.getPhoneNumber());
        else  Log.d("asd", "deleted :"+emergencyContact.getPhoneNumber());


    }

    public int getCount(){
        String query="select * from "+ TABLE_NAME;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor =db.rawQuery(query,null);
        return cursor.getCount();
    }

    public List<EmergencyContact> getAllContacts(){
        List<EmergencyContact> emergencyContactsList=new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String select = "select * from " + TABLE_NAME + ";";
        Cursor cursor = db.rawQuery(select, null);

        if(cursor.moveToFirst()){
            do{
                EmergencyContact emergencyContact=new EmergencyContact();

                emergencyContact.setPhoneNumber(cursor.getString(0));
                emergencyContact.setName(cursor.getString(1));

                emergencyContactsList.add(emergencyContact);
            }while(cursor.moveToNext());
        }
        return emergencyContactsList;
    }

}
