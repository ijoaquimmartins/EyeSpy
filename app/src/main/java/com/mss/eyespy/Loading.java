package com.mss.eyespy;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Loading extends AppCompatActivity {
    private ProgressBar progressBar;
    private TextView tvTask;
    private String[] tasks = {"Fetching data", "Loading assets", "Initializing components", "Finalizing setup"};
    private int progress = 0;
    private int taskIndex = 0;
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
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (taskIndex < tasks.length) {
                    tvTask.setText(tasks[taskIndex]);
                    progress = ((taskIndex + 1) * 100) / tasks.length;
                    progressBar.setProgress(progress);
                    taskIndex++;
                    handler.postDelayed(this, 1000);
                } else {
                    tvTask.setText("Completed!");
                }
            }
        });
    }
}