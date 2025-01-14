package com.expensetracker;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class NotificationsActivity extends AppCompatActivity {

    private Switch notificationsSwitch;
    private ImageView backIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        // Initialize views
        notificationsSwitch = findViewById(R.id.notifications_switch);
        backIcon = findViewById(R.id.back_button);

        // Set initial state of the switch
        boolean isNotificationsEnabled = getSharedPreferences("prefs", MODE_PRIVATE)
                .getBoolean("notifications_enabled", true);
        notificationsSwitch.setChecked(isNotificationsEnabled);

        // Set click listener for back icon
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Set change listener for the switch
        notificationsSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            getSharedPreferences("prefs", MODE_PRIVATE)
                    .edit()
                    .putBoolean("notifications_enabled", isChecked)
                    .apply();

            if (isChecked) {
                requestNotificationPermissionAndSchedule();
            } else {
                cancelDailyNotification();
            }
        });

        // Schedule the notification if it's enabled
        if (isNotificationsEnabled) {
            requestNotificationPermissionAndSchedule();
        }
    }
    private void requestNotificationPermissionAndSchedule() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            } else {
                scheduleDailyNotification();
            }
        } else {
            scheduleDailyNotification();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show();
                scheduleDailyNotification();
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void scheduleDailyNotification() {
        Intent intent = new Intent(NotificationsActivity.this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                NotificationsActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 11);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent);
        }
    }

    private void cancelDailyNotification() {
        Intent intent = new Intent(NotificationsActivity.this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                NotificationsActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
