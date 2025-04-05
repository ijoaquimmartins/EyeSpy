package com.mss.eyespy;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;



import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GlobalClass {

    Context context;
    public static final String SHARED_PREFS = "sharedprefs";

    // Open Navigation Drawer
    public static void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    // Close Navigation Drawer if it's open
    public static void closeDrawer(DrawerLayout drawerLayout) {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    // Redirect to another activity
    public static void redirectActivity(Activity activity, Class<?> secondActivity) {
        Intent intent = new Intent(activity, secondActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    public static void setAppVersion(Context context, TextView textView) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            String versionName = pInfo.versionName;
            textView.setText("Version " + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            textView.setText("Version not found");
        }
    }
    public static void logout(Context context) {
        SharedPreferences sharedPreferences = context.getApplicationContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();

        //Log.d("SharedPreferences", "Value after clear: " + sharedPreferences.getAll().size());
        Intent intent = new Intent(context, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public static void exitApp(Activity activity) {
        activity.finishAffinity();
        System.exit(0);
    }

    public static String convertDateFormat(String inputDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");

        try {
            Date date = inputFormat.parse(inputDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static class DateTimeUtils {
        public boolean isDateTimeSetManually(Context context) {
            try {
                int autoTime = Settings.Global.getInt(context.getContentResolver(), Settings.Global.AUTO_TIME);
                int autoTimeZone = Settings.Global.getInt(context.getContentResolver(), Settings.Global.AUTO_TIME_ZONE);

                // If either setting is disabled (0), then the user has manually set date/time
                return autoTime == 0 || autoTimeZone == 0;
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();

                return false;
            }
        }
    }
    public static void deleteAllImagesInAppPicturesDir(Context context) {
        File picturesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (picturesDir != null && picturesDir.exists()) {
            File[] files = picturesDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }
        }
    }

}
