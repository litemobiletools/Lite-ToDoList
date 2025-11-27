package com.litemobiletools.todolist;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.Calendar;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    public DatabaseHelper myDatabase;
    ImageView shareBtn;
    private static final int NOTIFICATION_PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myDatabase = new DatabaseHelper(this);

        shareBtn = findViewById(R.id.shareBtn);
        shareBtn.setOnClickListener(v -> {
            String shareMessage = "📌 *TO-DO List Lite*\n\n"
                    + "Stay organized every day with this simple and fast task manager.\n"
                    + "✔ Create tasks\n"
                    + "✔ Mark as complete\n"
                    + "✔ Category-wise organization\n"
                    + "✔ Instant progress counter\n\n"
                    + "Download now and boost your productivity!\n\n"
                    + "👉 https://play.google.com/store/apps/details?id=com.litemobiletools.todolist"; // <-- replace with your real link

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

            startActivity(Intent.createChooser(shareIntent, "Share TO-DO List Lite"));
        });

        // Handle back press with dialog
        this.getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showExitDialog();
            }
        });

        createNotificationChannel(); // call once on app start
        requestNotificationPermission();  // <-- IMPORTANT
    }
    public void general_list(View view) {
        Intent intense = new Intent(MainActivity.this, TaskList.class);
        intense.putExtra("catName", "general_list");
        startActivity(intense);
    }
    public void btnWishList(View view) {
        Intent intense = new Intent(MainActivity.this, TaskList.class);
        intense.putExtra("catName", "wish_list");
        startActivity(intense);
    }
    public void btnGoToList(View view) {
        Intent intense = new Intent(MainActivity.this, TaskList.class);
        intense.putExtra("catName", "goto_list");
        startActivity(intense);
    }
    public void btnShoppingList(View view) {
        Intent intense = new Intent(MainActivity.this, TaskList.class);
        intense.putExtra("catName", "shopping_list");
        startActivity(intense);
    }
    public void btnworkList(View view) {
        Intent intense = new Intent(MainActivity.this, TaskList.class);
        intense.putExtra("catName", "work_list");
        startActivity(intense);
    }
    public void btnPersonalList(View view) {
        Intent intense = new Intent(MainActivity.this, TaskList.class);
        intense.putExtra("catName", "personal_list");
        startActivity(intense);
    }
    public void btnStudyList(View view) {
        Intent intense = new Intent(MainActivity.this, TaskList.class);
        intense.putExtra("catName", "study_list");
        startActivity(intense);
    }
    public void btnFinanceList(View view) {
        Intent intense = new Intent(MainActivity.this, TaskList.class);
        intense.putExtra("catName", "finance_list");
        startActivity(intense);
    }
    public void btnHelthList(View view) {
        Intent intense = new Intent(MainActivity.this, TaskList.class);
        intense.putExtra("catName", "helth_list");
        startActivity(intense);
    }
    public void btnEventList(View view) {
        Intent intense = new Intent(MainActivity.this, TaskList.class);
        intense.putExtra("catName", "event_list");
        startActivity(intense);
    }
    @Override
    protected void onResume() {
        super.onResume();

        // Assuming you have a TextView to show the message
        TextView txtGreeting = findViewById(R.id.txtGreeting);
        // Get current hour of the day (0-23)
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String greeting;
        if (hour >= 5 && hour < 12) {
            greeting = "Hi, Good Morning";
        } else if (hour >= 12 && hour < 17) {
            greeting = "Hi, Good Afternoon";
        } else if (hour >= 17 && hour < 21) {
            greeting = "Hi, Good Evening";
        } else {
            greeting = "Hi, Good Night";
        }
        // Set greeting to TextView
        txtGreeting.setText(greeting);
        //counter for all
        int uncheckCount = myDatabase.getItemCount();
        TextView txtCounter = findViewById(R.id.txtCounter);
        txtCounter.setText("You have total " + uncheckCount + " tasks pending.");

        //counter for general
        int general_list = myDatabase.getUncheckedCount("general_list");
        TextView general_list_text = findViewById(R.id.general_listText);
        general_list_text.setText("You have " + general_list + " things to do." );

        //counter for wish
        int wish_list = myDatabase.getUncheckedCount("wish_list");
        TextView wish_listtext = findViewById(R.id.wish_listtext);
        wish_listtext.setText("You have " + wish_list + " wishes." );

        //counter for goto_list
        int goto_list = myDatabase.getUncheckedCount("goto_list");
        TextView goto_listtext = findViewById(R.id.goto_listtext);
        goto_listtext.setText("You have " + goto_list + " places to go." );

        //counter for shopping_list
        int shopping_list = myDatabase.getUncheckedCount("shopping_list");
        TextView shopping_listtext = findViewById(R.id.shopping_listtext);
        shopping_listtext.setText("You have " + shopping_list + " items to buy." );

        //counter for work_list
        int work_list = myDatabase.getUncheckedCount("work_list");
        TextView work_listtext = findViewById(R.id.work_listtext);
        work_listtext.setText("You have " + work_list + " tasks at work." );

        //counter for personal_list
        int personal_list = myDatabase.getUncheckedCount("personal_list");
        TextView personal_listtext = findViewById(R.id.personal_listtext);
        personal_listtext.setText("You have " + personal_list + " goals to achieve." );

        //counter for study_list
        int study_list = myDatabase.getUncheckedCount("study_list");
        TextView study_listtext = findViewById(R.id.study_listtext);
        study_listtext.setText("You have " + study_list + " lessons to complete." );

        //counter for finance_list
        int finance_list = myDatabase.getUncheckedCount("finance_list");
        TextView finance_listtext = findViewById(R.id.finance_listtext);
        finance_listtext.setText("You have " + finance_list + " bills to manage." );

        //counter for helth_list
        int helth_list = myDatabase.getUncheckedCount("helth_list");
        TextView helth_listtext = findViewById(R.id.helth_listtext);
        helth_listtext.setText("You have " + helth_list + " health tasks today." );

        //counter for event_list
        int event_list = myDatabase.getUncheckedCount("event_list");
        TextView event_listtext = findViewById(R.id.event_listtext);
        event_listtext.setText("You have " + event_list + " events coming up." );

        //random color
        View myView = findViewById(R.id.myShapeView);
        // Cast background to GradientDrawable
        GradientDrawable drawable = (GradientDrawable) myView.getBackground();
        // Generate a random color
        Random random = new Random();
        int randomColor = Color.argb(
                255, // Alpha
                random.nextInt(256), // Red
                random.nextInt(256), // Green
                random.nextInt(256)  // Blue
        );
        // Set random color dynamically
        drawable.setColor(randomColor);
        txtGreeting.setTextColor(getContrastColor(randomColor));
        txtCounter.setTextColor(getContrastColor(randomColor));
        shareBtn.setColorFilter(getContrastColor(randomColor));
        TextView backBtn = findViewById(R.id.backBtn);
        backBtn.setTextColor(getContrastColor(randomColor));
    }
    // Show confirmation dialog
    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Exit TO-DO List Lite")
                .setMessage("Are you sure you want to exit the app?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", (dialog, which) -> {
                    finishAffinity(); // Close app completely
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss(); // Close dialog
                })
                .setCancelable(false)
                .show();
    }
    //random text color
    private int getContrastColor(int color) {
        // Calculate luminance
        double luminance = (0.299 * Color.red(color) +
                0.587 * Color.green(color) +
                0.114 * Color.blue(color)) / 255;

        // If luminance is bright → return black text, otherwise white
        return luminance > 0.5 ? Color.BLACK : Color.WHITE;
    }
    //notification system
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Task Reminder";
            String description = "Channel for task reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("task_channel", name, importance);
            channel.setDescription(description);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                // Show explanation BEFORE asking the permission
                showNotificationPermissionExplanation();
            }
        }
    }
    @SuppressWarnings("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notifications enabled!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void showNotificationPermissionExplanation() {
        new AlertDialog.Builder(this)
                .setTitle("Enable Notifications")
                .setMessage("We use notifications only to remind you about tasks. We do not collect or share any data.")
                .setPositiveButton("Allow", (dialog, which) -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        // Safe: Only Android 13+ needs POST_NOTIFICATIONS
                        requestPermissions(
                                new String[]{Manifest.permission.POST_NOTIFICATIONS},
                                NOTIFICATION_PERMISSION_CODE
                        );
                    }
                })
                .setNegativeButton("No Thanks", (dialog, which) -> dialog.dismiss())
                .show();
    }
}