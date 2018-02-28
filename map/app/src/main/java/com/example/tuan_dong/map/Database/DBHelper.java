package com.example.tuan_dong.map.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Tuan-Dong on 1/24/2018.
 */

public class DBHelper extends SQLiteOpenHelper {
    public static final String EMPTY_VALUE = "---";
    private static final int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = "Address.db";
    public static final String ADD_TABLE_NAME = "address";
    public static final String KEY_ID = "_id";
    public static final String ADD_KEY_LATITUDE = "LATITUDE";
    public static final String ADD_KEY_LONGITUDE = "LONGITUDE";
    public static final String ADD_KEY_NAME = "NAME";

    public static final String PHONE_TABLE_NAME = "phone";
    public static final String PHONE_KEY_NAME = "NAME";
    public static final String PHONE_KEY_NUMBER = "NUMBER";

    private static final String CREATE_TABLE_LOCATION = "CREATE TABLE " + ADD_TABLE_NAME + " ("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + ADD_KEY_LATITUDE + " REAL,"
            + ADD_KEY_LONGITUDE + " REAL,"
            + ADD_KEY_NAME + " TEXT)";

    private static final String CREATE_TABLE_PHONE_NUMBER = "CREATE TABLE " + PHONE_TABLE_NAME + " ("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + PHONE_KEY_NAME + " TEXT,"
            + PHONE_KEY_NUMBER + " TEXT)";

    private Context m_context;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.m_context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_LOCATION);
        sqLiteDatabase.execSQL(CREATE_TABLE_PHONE_NUMBER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ADD_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PHONE_TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}