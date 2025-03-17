package com.mss.eyespy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int alarmId = intent.getIntExtra("alarm_id", -1);
        Toast.makeText(context, "Executing Task for Alarm: " + alarmId, Toast.LENGTH_LONG).show();

        // Perform the task (Example: Call an API, show notification, etc.)
        performTask(context, alarmId);

        // Clear the alarm after execution
    //    clearAlarm(context, alarmId);
    }

    private void performTask(Context context, int alarmId) {
        // Example task: Show a notification, update database, or call an API
        Log.d("AlarmReceiver", "Performing task for alarm ID: " + alarmId);
    }

    private void clearAlarm(Context context, int alarmId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            Log.d("AlarmReceiver", "Alarm " + alarmId + " cleared.");
        }
    }
}
