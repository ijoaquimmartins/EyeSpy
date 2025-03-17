package com.mss.eyespy;

import static com.mss.eyespy.SharedPreferences.*;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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
    private AlarmManager alarmManager;
    private String patrolingURL, stMassage, time;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        progressBar = findViewById(R.id.progressBar);
        tvTask = findViewById(R.id.tvTask);

        ImageView gifImageView = findViewById(R.id.gifImageView);
        Glide.with(this).asGif().load(R.drawable.loading).into(gifImageView);

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

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
        simulateTask(2000); // Simulate work for 2 seconds
    }

    private void loadAssets() {
        simulateTask(1500); // Simulate work for 1.5 seconds
    }

    private void initializeComponents() {
        setAlarm();
        simulateTask(2500); // Simulate work for 2.5 seconds
    }
    private void setAlarm() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        FormBody formBody = new FormBody.Builder()
                .add("userid", UserTableId)
                .build();

        Request request = new Request.Builder()
                .url(patrolingURL)
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                stMassage = "Error fetching data. Please restart the app.";
                runOnUiThread(() -> showAlertDialog());
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string().trim();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String error = jsonResponse.optString("error");
                        String msg = jsonResponse.optString("msg");

                        if (!error.isEmpty()) {
                            Log.e("SetAlarm", "Server Error: " + error);
                            return;
                        }
                        JSONArray alarms = jsonResponse.getJSONArray("alarms");
                        for (int i = 0; i < alarms.length(); i++) {
                            JSONObject alarm = alarms.getJSONObject(i);
                            int alarmId = alarm.getInt("id");
                            String time = alarm.getString("time"); // Expected format: "yyyy-MM-dd HH:mm:ss"
                            setExactAlarm(alarmId, time);

                        }
                    } catch (Exception e) {
                        Log.e("SetAlarm", "Error parsing JSON", e);
                    }
                }
            }
        });
    }
    private void setExactAlarm(int alarmId, String alarmTime) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // Android 12+
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                requestExactAlarmPermission();
                return;
            }
        }
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date date = sdf.parse(alarmTime);

            if (date == null) {
                Log.e("SetAlarm", "Invalid time format: " + alarmTime);
                return;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.putExtra("alarm_id", alarmId);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

            Log.d("SetAlarm", "Alarm " + alarmId + " set for " + alarmTime);
        } catch (Exception e) {
            Log.e("SetAlarm", "Error setting alarm", e);
        }
    }
    private void requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            startActivity(intent);
        }
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

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Massage");
        builder.setMessage(stMassage);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                Intent i = new Intent(Loading.this, Login.class);
                startActivity(i);
                finish();

            }
        });
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}