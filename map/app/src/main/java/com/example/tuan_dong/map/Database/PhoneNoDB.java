package com.example.tuan_dong.map.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by QuangTran on 2/9/2018.
 */

public class PhoneNoDB {
    private static int PHONE_NUMBER_QUANTITY = 6;
    private SQLiteDatabase m_database;
    private DBHelper m_helper;
    private Context m_context;

    public PhoneNoDB(Context context) {
        m_context = context;
        m_helper = new DBHelper(m_context);
    }

    public void open() {
        try {
            m_database = m_helper.getWritableDatabase();
        } catch (Exception e) {
            Toast.makeText(m_context, e.getMessage(), Toast.LENGTH_LONG).show();
            Log.w("Read only", "Database is opened in read-only mode");
            try {
                m_database = m_helper.getReadableDatabase();
            } catch (Exception ex) {
                Toast.makeText(m_context, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void close() {
        if (m_database == null) {
            return;
        }
        m_helper.close();
        m_database = null;
    }

    public void insertDefault() {
        //Mo ket noi den Database
        this.open();
        ContentValues values = new ContentValues();
        values.put(DBHelper.PHONE_KEY_NAME, DBHelper.EMPTY_VALUE);
        values.put(DBHelper.PHONE_KEY_NUMBER, DBHelper.EMPTY_VALUE);
        m_database.insert(DBHelper.PHONE_TABLE_NAME, null, values);
        this.close();
    }

    public void init() {
        for (int i = 0; i < PHONE_NUMBER_QUANTITY; i++)
            this.insertDefault();
    }

    public void update(PhoneNumber no) {
        this.open();

        ContentValues values = new ContentValues();
        values.put(DBHelper.PHONE_KEY_NAME, no.getName());
        values.put(DBHelper.PHONE_KEY_NUMBER, no.getNumber());
        m_database.update(DBHelper.PHONE_TABLE_NAME, values, DBHelper.KEY_ID + "=?", new String[]{String.valueOf(no.getID())});

        this.close();
    }

    public void delete(int id) {
        this.open();

        ContentValues values = new ContentValues();
        values.put(DBHelper.PHONE_KEY_NAME, DBHelper.EMPTY_VALUE);
        values.put(DBHelper.PHONE_KEY_NUMBER, DBHelper.EMPTY_VALUE);
        m_database.update(DBHelper.PHONE_TABLE_NAME, values, DBHelper.KEY_ID + "=?", new String[]{String.valueOf(id)});

        this.close();
    }

    public Cursor getAllNumbers() {
        this.open();
        Cursor cursor = m_database.query(DBHelper.PHONE_TABLE_NAME, new String[]{DBHelper.KEY_ID, DBHelper.PHONE_KEY_NAME, DBHelper.PHONE_KEY_NUMBER}, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        Cursor rs = cursor;
        this.close();
        return rs;
    }

    public List getAllNumbersIntoList() {
        Cursor cursor = this.getAllNumbers();
        List<PhoneNumber> rs = new ArrayList<>();

        if (cursor == null)
            return rs;

        while (cursor.moveToNext()) {
            PhoneNumber data = new PhoneNumber(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
            rs.add(data);
        }
        return rs;
    }

    public PhoneNumber getPhoneNumber(int id) {
        this.open();
        Cursor cursor = m_database.query(DBHelper.PHONE_TABLE_NAME, new String[]{DBHelper.KEY_ID, DBHelper.PHONE_KEY_NAME, DBHelper.PHONE_KEY_NUMBER}, DBHelper.KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor == null) {
            return new PhoneNumber();
        }
        cursor.moveToFirst();
        this.close();
        return new PhoneNumber(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
    }

    public PhoneNumber findPhoneNumberByNumber(String number) {
        this.open();
        ContentValues values = new ContentValues();
        values.put(DBHelper.PHONE_KEY_NAME, number);
        Cursor cursor = m_database.query(DBHelper.PHONE_TABLE_NAME, new String[]{DBHelper.KEY_ID, DBHelper.PHONE_KEY_NAME, DBHelper.PHONE_KEY_NUMBER}, DBHelper.PHONE_KEY_NUMBER + "=?", new String[]{number}, null, null, null);
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        cursor.moveToFirst();
        this.close();
        return new PhoneNumber(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
    }
}
