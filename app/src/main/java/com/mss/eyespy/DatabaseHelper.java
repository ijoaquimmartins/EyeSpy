package com.mss.eyespy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Database Name & Version
    private static final String DATABASE_NAME = "EyeSpyDb.db";
    private static final int DATABASE_VERSION = 1;

    // Table Name
    private static final String TABLE_NAME = "qrscan";

    // Column Names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String QR_LOCATION = "qrlocation";
    private static final String SCAN_TIMEFR = "scantimefrm";
    private static final String SCAN_TIMETO = "scantimeto";
    private static final String QRCODE_ID = "qrcodeid";
    private static final String SCANNED = "scanned";


    // Create Table Query
    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    QR_LOCATION + " TEXT, " +
                    SCAN_TIMEFR + " NUMERIC, " +
                    SCAN_TIMETO + " NUMERIC, " +
                    QRCODE_ID + " INTEGER, " +
                    SCANNED + " INTEGER)";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // **INSERT DATA**
    public boolean insertTimings(String name, String qrlocation, String scantimefrm, String scantimeto, String qrcodeid, String scanned) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(QR_LOCATION, qrlocation);
        values.put(SCAN_TIMEFR, scantimefrm);
        values.put(SCAN_TIMETO, scantimeto);
        values.put(QRCODE_ID, qrcodeid);
        values.put(SCANNED, scanned);
        long result = db.insert(TABLE_NAME, null, values);
        db.close();
        return result != -1; // Returns true if inserted, false otherwise
    }

    // **UPDATE DATA**
    public boolean updateTimings(int id, String scanned) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SCANNED, scanned);
        int result = db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0; // Returns true if updated, false otherwise
    }

    // **DELETE DATA**
    public boolean deleteTimings(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return result > 0; // Returns true if deleted, false otherwise
    }

    // **FETCH DATA**
    public Cursor getTimings() {
        SQLiteDatabase db = this.getReadableDatabase();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = sdf.format(new Date());

        String query = "SELECT * FROM " + TABLE_NAME +
                " ORDER BY ABS(strftime('%H:%M', time_column) - strftime('%H:%M', ?)) ASC";

        return db.rawQuery(query, new String[]{currentTime});
    }
}
