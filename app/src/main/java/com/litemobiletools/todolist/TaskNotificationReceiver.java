package com.litemobiletools.todolist;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class TaskNotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Get data from the alarm
        String taskName = intent.getStringExtra("task_name");
        int taskId = intent.getIntExtra("task_id", 0);
        // 🔐 Android 13+ Notification Permission Check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                        != PackageManager.PERMISSION_GRANTED) {
            return; // Permission not granted → do not show notification
        }

        // 1️⃣ Create intent to open your app/activity when notification is clicked
        Intent activityIntent = new Intent(context, MainActivity.class); // change TaskList to your activity
        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activityIntent.putExtra("task_id", taskId); // optional: pass task id

        // 2️⃣ Wrap in PendingIntent
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                taskId, // unique request code per task
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // 🔔 Create Notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "task_channel")
                .setSmallIcon(R.drawable.ic_notification) // add this drawable to res/drawable
                .setContentTitle("Task Reminder")
                .setContentText(taskName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent); // <-- this is the fix

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(taskId, builder.build()); // use taskId so each notification is identifiable
    }
}
