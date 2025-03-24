package com.mss.eyespy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "eyespy.db";
    private static final int DATABASE_VERSION = 1;

    // Table 1: Users
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_MOBILE_NO = "mobileno";
    private static final String COLUMN_USER_UNIQUE_ID = "userid";
    private static final String COLUMN_FIRST_NAME = "first_name";
    private static final String COLUMN_MIDDLE_NAME = "middle_name";
    private static final String COLUMN_LAST_NAME = "last_name";
    private static final String COLUMN_USER_ACCESS = "user_access";
    private static final String COLUMN_PROFILE_PHOTO = "profilephoto";
    public static final String COLUMN_EDITED_DATETIME = "editeddatetime";

    // Table 2: Scanned QR
    private static final String TABLE_SCANNED_QR = "scanned_qr";
    private static final String COLUMN_QR_ID = "id";
    private static final String COLUMN_QR_CODE = "qr_code";
    private static final String COLUMN_QR_NAME = "name";
    private static final String COLUMN_QR_LOCATION = "location";
    private static final String COLUMN_QR_DATETIME = "datetime";

    // Create Table Queries
    private static final String CREATE_TABLE_USERS =
            "CREATE TABLE " + TABLE_USERS + " ("
                    + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_MOBILE_NO + " TEXT, "
                    + COLUMN_USER_UNIQUE_ID + " TEXT, "
                    + COLUMN_FIRST_NAME + " TEXT, "
                    + COLUMN_MIDDLE_NAME + " TEXT, "
                    + COLUMN_LAST_NAME + " TEXT, "
                    + COLUMN_USER_ACCESS + " TEXT, "
                    + COLUMN_PROFILE_PHOTO + " BLOB, "
                    + COLUMN_EDITED_DATETIME + " TEXT"
                    + ");";

    private static final String CREATE_TABLE_SCANNED_QR =
            "CREATE TABLE " + TABLE_SCANNED_QR + " ("
                    + COLUMN_QR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + COLUMN_QR_CODE + " TEXT, "
                    + COLUMN_QR_NAME + " TEXT, "
                    + COLUMN_QR_LOCATION + " TEXT, "
                    + COLUMN_QR_DATETIME + " TEXT DEFAULT CURRENT_TIMESTAMP"
                    + ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_SCANNED_QR);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCANNED_QR);
        onCreate(db);
    }

    // Insert User
    public boolean insertUser(String mobileno, String userid, String firstName, String middleName,
                              String lastName, String userAccess, String profilePhoto, String editeddatetime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MOBILE_NO, mobileno);
        values.put(COLUMN_USER_UNIQUE_ID, userid);
        values.put(COLUMN_FIRST_NAME, firstName);
        values.put(COLUMN_MIDDLE_NAME, middleName);
        values.put(COLUMN_LAST_NAME, lastName);
        values.put(COLUMN_USER_ACCESS, userAccess);
        values.put(COLUMN_PROFILE_PHOTO, profilePhoto);
        values.put(COLUMN_EDITED_DATETIME, editeddatetime);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    // Insert Scanned QR
    public boolean insertScannedQR(String qrCode, String name, String location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_QR_CODE, qrCode);
        values.put(COLUMN_QR_NAME, name);
        values.put(COLUMN_QR_LOCATION, location);

        long result = db.insert(TABLE_SCANNED_QR, null, values);
        return result != -1;
    }

    // Retrieve All Users
    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_USERS, null);
    }

    public Cursor getUserById(String userid) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT " + COLUMN_EDITED_DATETIME + " FROM " + TABLE_USERS + " WHERE " + COLUMN_MOBILE_NO + "= ?", new String[]{userid});
    }

    // Retrieve All QR Scans
    public Cursor getAllScannedQR() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_SCANNED_QR, null);
    }
}
