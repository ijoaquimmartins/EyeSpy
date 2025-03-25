package com.mss.eyespy;

import static com.mss.eyespy.SharedPreferences.*;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Loading extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView tvTask;
    private int progress = 0;
    private Handler handler = new Handler(Looper.getMainLooper());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        progressBar = findViewById(R.id.progressBar);
        tvTask = findViewById(R.id.tvTask);
        startLoading();
    }
    private void startLoading() {
        new Thread(() -> {
            runTask("Fetching data", this::fetchData);
            runTask("Loading assets", this::loadAssets);
            runTask("Initializing components", this::initializeComponents);
            runTask("Finalizing setup", this::finalizeSetup);

            // Show completion message
            handler.post(() -> tvTask.setText("Completed!"));
            Intent i = new Intent(Loading.this, MainActivity.class);
            startActivity(i);
            finish();
        }).start();
    }

    private void runTask(String taskName, Runnable taskFunction) {
        handler.post(() -> tvTask.setText(taskName)); // Update UI with current task
        taskFunction.run(); // Execute the task
        updateProgress(); // Increase progress after task completion
    }

    private void updateProgress() {
        progress += 25; // Since we have 4 tasks, each gets 25%
        handler.post(() -> progressBar.setProgress(progress));
    }

    // Simulating tasks (Replace with real logic)
    private void fetchData() {
        checkPermissions();
    }

    private void loadAssets() {
        simulateTask(2000);
    }
    private void initializeComponents() {
        simulateTask(2500); // Simulate work for 2.5 seconds
    }
    private void finalizeSetup() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            VersionName = pInfo.versionName;
            //   Toast.makeText(this, "Version: " + versionName, Toast.LENGTH_LONG).show();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void simulateTask(int duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            String imageUrl = ImageURL+ProfilePhoto;
//            ImageHelper.downloadAndSaveImage(this, imageUrl);
        } else {
            Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
        }
    }
}