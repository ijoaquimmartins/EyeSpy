package com.mss.eyespy;

import static com.mss.eyespy.SharedPreferences.*;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
        simulateTask(200);
    }
    private void initializeComponents() {
        simulateTask(200); // Simulate work for 2.5 seconds
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