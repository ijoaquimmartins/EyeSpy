package com.mss.eyespy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
                    SCAN_TIMEFR + " TEXT, " +
                    SCAN_TIMETO + " TEXT, " +
                    QRCODE_ID + " INTEGER, " +
                    SCANNED + " INTEGER)";
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        insertInitialData(db);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public List<PatrollingList> getTimings() {
        List<PatrollingList> patrollingList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = sdf.format(new Date());

        String query = "SELECT * FROM " + TABLE_NAME +
                " WHERE " + SCANNED + " = 0 " +
                " ORDER BY ABS((strftime('%H', " + SCAN_TIMEFR + ") * 3600 + strftime('%M', " + SCAN_TIMEFR + ") * 60) - " +
                "(strftime('%H', ?) * 3600 + strftime('%M', ?) * 60)) ASC";


        Cursor cursor = db.rawQuery(query, new String[]{currentTime, currentTime});

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int columnId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String columnName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                String qrLocation = cursor.getString(cursor.getColumnIndexOrThrow(QR_LOCATION));
                String scanTimefr = cursor.getString(cursor.getColumnIndexOrThrow(SCAN_TIMEFR));
                String scanTimeto = cursor.getString(cursor.getColumnIndexOrThrow(SCAN_TIMETO));
                int qrcodeId = cursor.getInt(cursor.getColumnIndexOrThrow(QRCODE_ID));

                patrollingList.add(new PatrollingList(columnId, columnName, qrLocation, scanTimefr, scanTimeto, qrcodeId));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return patrollingList;
    }

    private void insertInitialData(SQLiteDatabase db) {
        db.execSQL("INSERT INTO " + TABLE_NAME + " (name, qrlocation, scantimefrm, scantimeto, qrcodeid, scanned) VALUES" +
                "('Main Entrance', 'Main Entrance', '08:00', '08:15', 1001, 0)," +
                "('Front Desk', 'Lobby', '09:00', '09:15', 1002, 0)," +
                "('Exit Gate', 'Exit Gate', '12:00', '12:15', 1003, 0);");
    }
}
