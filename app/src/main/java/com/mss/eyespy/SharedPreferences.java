package com.mss.eyespy;

import android.app.Application;

public class SharedPreferences extends Application {
    public static String VersionName, MobileNo, UserFullName, UserId, UserAccess, ProfilePhoto, EditedDateTime, UserType, UserTableId="0";

    public static String LocProfilePath;
    public static String URL = "http://100.168.10.74:8000/api/";
    public static String ImageURL = "http://100.168.10.74:8000/";
}
