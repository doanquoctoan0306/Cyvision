package com.example.tuan_dong.map.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by Tuan-Dong on 1/24/2018.
 */

public class AddressModify {
    private DBHelper dbHelper;
    private ArrayList<Address> arrayList;

    public AddressModify(Context context) {
        dbHelper = new DBHelper(context);
    }

    //PT Them
    public void insert() {
        //Mo ket noi den Database
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBHelper.ADD_KEY_NAME, DBHelper.EMPTY_VALUE);
        values.put(DBHelper.ADD_KEY_LONGITUDE, DBHelper.EMPTY_VALUE);
        values.put(DBHelper.ADD_KEY_LATITUDE, DBHelper.EMPTY_VALUE);

        db.insert(DBHelper.ADD_TABLE_NAME, null, values);
        db.close();
    }

    //PT Sua
    public void update(Address adress) {
        //Mo ket noi den Database
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBHelper.ADD_KEY_NAME, adress.getName());
        values.put(DBHelper.ADD_KEY_LONGITUDE, adress.getLng());
        values.put(DBHelper.ADD_KEY_LATITUDE, adress.getLat());

        db.update(DBHelper.ADD_TABLE_NAME, values, DBHelper.KEY_ID + "=?", new String[]{String.valueOf(adress.getId())});
        db.close();

    }

    //PT xoa
    public void delete(int id) {
        //Mo ket noi den Database
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DBHelper.ADD_KEY_NAME, DBHelper.EMPTY_VALUE);
        values.put(DBHelper.ADD_KEY_LONGITUDE, DBHelper.EMPTY_VALUE);
        values.put(DBHelper.ADD_KEY_LATITUDE, DBHelper.EMPTY_VALUE);

        db.update(DBHelper.ADD_TABLE_NAME, values, DBHelper.KEY_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    //lay tat ca du lieu trong bang
    public Cursor getPlaceList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.ADD_TABLE_NAME, new String[]{DBHelper.KEY_ID, DBHelper.ADD_KEY_LATITUDE, DBHelper.ADD_KEY_LONGITUDE, DBHelper.ADD_KEY_NAME}, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    //lay tat ca du lieu trong bang vao list
    public ArrayList<Address> getPlaceArrayList() {
        arrayList = new ArrayList<Address>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.ADD_TABLE_NAME, new String[]{DBHelper.KEY_ID, DBHelper.ADD_KEY_LATITUDE, DBHelper.ADD_KEY_LONGITUDE, DBHelper.ADD_KEY_NAME}, null, null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                Address data = new Address(cursor.getInt(0), cursor.getDouble(1), cursor.getDouble(2), cursor.getString(3));
                arrayList.add(data);
            }
        }
        return arrayList;
    }

    //Lay 1 du lieu
    public Address fetchPlaceByID(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.ADD_TABLE_NAME, new String[]{DBHelper.KEY_ID, DBHelper.ADD_KEY_LATITUDE, DBHelper.ADD_KEY_LONGITUDE, DBHelper.ADD_KEY_NAME}, DBHelper.KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return new Address(cursor.getInt(0), cursor.getDouble(1), cursor.getDouble(2), cursor.getString(3));
    }

}