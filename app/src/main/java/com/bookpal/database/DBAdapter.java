package com.bookpal.database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asif on 13-09-2015.
 */
public class DBAdapter {
    private static final String DATABASE_NAME = "UserAddress";
    private static final int DATABASE_VERSION = 1;
    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase objSQLiteDatabase;

    private static final String TABLE_Address = "Address";
    private static final String StrQueryAddress = "CREATE TABLE IF NOT EXISTS " + TABLE_Address + " (_id INTEGER PRIMARY KEY, id text, " +
            "pincode text, area text, zone text, state text);";

    // Default Constructor
    public DBAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    // Create Database
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase objSQLiteDatabase) {
            try {
                objSQLiteDatabase.execSQL(StrQueryAddress);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w("DBAdapter", "Upgrading database from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data");

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_Address);
            onCreate(db);
        }
    }

    // Open DataBase
    public DBAdapter open() throws SQLException {
        objSQLiteDatabase = DBHelper.getWritableDatabase();
        return this;
    }

    // Close Database
    public void close() {
        DBHelper.close();
    }

    // Insert Data
    /*public long InsertData(String id, String dob, String status, String ethnicity, String weight, String height, String is_veg, String drink, String image) {
        // TODO Auto-generated method stub

        ContentValues initialValues = new ContentValues();
        initialValues.put("id", id);
        initialValues.put("dob", dob);
        initialValues.put("status", status);
        initialValues.put("ethnicity", ethnicity);
        initialValues.put("weight", weight);
        initialValues.put("height", height);
        initialValues.put("is_veg", is_veg);
        initialValues.put("drink", drink);
        initialValues.put("image", image);

        return objSQLiteDatabase.insert(TABLE_Favorite, null, initialValues);
    }*/

    // Get Pincode
    public List<String> GetPincodes() {
        Cursor cursor = null;
        try {
            cursor = objSQLiteDatabase.rawQuery(
                    "SELECT pincode FROM " + TABLE_Address, null);
        } catch (Exception ex) {
            ex.printStackTrace();
            cursor.close();
        }

        List<String> list = new ArrayList<>();

        int i = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(cursor.getString(0));
            i++;
            cursor.moveToNext();
        }
        return list;
    }

    // Get Area
    public List<String> GetArea() {
        Cursor cursor = null;
        try {
            cursor = objSQLiteDatabase.rawQuery(
                    "SELECT area FROM " + TABLE_Address, null);
        } catch (Exception ex) {
            ex.printStackTrace();
            cursor.close();
        }

        List<String> list = new ArrayList<>();

        int i = 0;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(cursor.getString(0));
            i++;
            cursor.moveToNext();
        }
        return list;
    }
}
