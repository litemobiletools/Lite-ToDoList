package com.litemobiletools.todolist;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import kotlinx.coroutines.scheduling.Task;

public class TaskList extends AppCompatActivity {
    public DatabaseHelper myDatabase;
    String cat_name;
    TextView txtCounter;
    String catname;
    private ImageView emptyviewId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        myDatabase = new DatabaseHelper(this);

        cat_name = getIntent().getStringExtra("catName");
        loadDataFromDatabase(cat_name);

        TextView category_name = findViewById(R.id.category_name);
        ImageView cat_image = findViewById(R.id.cat_image);

        if(cat_name.equals("general_list")){
            catname = "General List";
            cat_image.setImageResource(R.drawable.general);

        }else if(cat_name.equals("wish_list")){
            catname = "Wish List";
            cat_image.setImageResource(R.drawable.wish);

        }else if(cat_name.equals("goto_list")){
            catname = "Go to List";
            cat_image.setImageResource(R.drawable.goto2);

        }else if(cat_name.equals("shopping_list")){
            catname = "Shopping List";
            cat_image.setImageResource(R.drawable.shopping);

        }else if(cat_name.equals("work_list")){
            catname = "Work Tasks";
            cat_image.setImageResource(R.drawable.work);

        }else if(cat_name.equals("personal_list")){
            catname = "Personal Goals";
            cat_image.setImageResource(R.drawable.personal);

        }else if(cat_name.equals("study_list")){
            catname = "Study Tasks";
            cat_image.setImageResource(R.drawable.study);

        }else if(cat_name.equals("finance_list")){
            catname = "Bills & Finance";
            cat_image.setImageResource(R.drawable.finance);

        }else if(cat_name.equals("helth_list")){
            catname = "Health & Medicine";
            cat_image.setImageResource(R.drawable.medicin);

        }else if(cat_name.equals("event_list")){
            catname = "Events and Special Days";
            cat_image.setImageResource(R.drawable.events);
        }
        category_name.setText(catname);

        //delete all task
        ImageView btnDeleteAll = findViewById(R.id.btnDeleteAll);
        btnDeleteAll.setOnClickListener(v -> {
            new AlertDialog.Builder(TaskList.this)
                    .setTitle("Delete All Tasks?")
                    .setMessage("Are you sure you want to delete all tasks in this category?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Yes", (dialog, which) -> {

                        // 1. Get all tasks in this category
                        Cursor cursor = myDatabase.getItemByCat(cat_name);
                        if (cursor != null && cursor.moveToFirst()) {
                            int idIndex = cursor.getColumnIndex("id"); // <-- make sure "id" is correct
                            if (idIndex != -1) {
                                do {
                                    int taskId = cursor.getInt(idIndex);
                                    cancelScheduledNotification(taskId);
                                } while (cursor.moveToNext());
                            }
                            cursor.close();
                        }

                        // DELETE ALL
                        myDatabase.deleteAllByCategory(cat_name);  // <-- category name

                        // Update counter UI
                        loadDataFromDatabase(cat_name);

                        // Optional: Show success message
                        Toast.makeText(TaskList.this, "All tasks deleted!", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(v -> {
            finish();  // Go back to MainActivity
        });

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

        category_name.setTextColor(getContrastColor(randomColor));
        txtCounter = findViewById(R.id.txtCounter);
        txtCounter.setTextColor(getContrastColor(randomColor));
        btnDeleteAll.setColorFilter(getContrastColor(randomColor));
        backBtn.setColorFilter(getContrastColor(randomColor));

        FloatingActionButton addTaskBtn = findViewById(R.id.addTaskBtn);
        addTaskBtn.setBackgroundTintList(ColorStateList.valueOf(randomColor));

        // Set FloatingActionButton icon color (auto white/black)
        int iconTint = getContrastColor(randomColor);
        addTaskBtn.setImageTintList(ColorStateList.valueOf(iconTint));

    }

    public void create(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Item");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        // Task input box
        final EditText input = new EditText(this);
        input.setHint("Enter item name");
        layout.addView(input);

        // Date text
        final TextView dateText = new TextView(this);
        dateText.setText("Select Date");
        dateText.setPadding(0, 30, 0, 10);
        layout.addView(dateText);

        // Time text
        final TextView timeText = new TextView(this);
        timeText.setText("Select Time");
        timeText.setPadding(0, 20, 0, 10);
        layout.addView(timeText);

        builder.setView(layout);

        // Calendar to hold selected date & time
        final Calendar calendar = Calendar.getInstance();

        // Pick Date
        dateText.setOnClickListener(v -> {

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(TaskList.this,
                    (view1, year1, month1, dayOfMonth) -> {
                        calendar.set(Calendar.YEAR, year1);
                        calendar.set(Calendar.MONTH, month1);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                        String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                        dateText.setText(selectedDate);
                    }, year, month, day);
            datePickerDialog.show();
        });

        // Pick Time
        timeText.setOnClickListener(v -> {

            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(TaskList.this,
                    (view12, hourOfDay, minute1) -> {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute1);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);

                        String selectedTime = String.format("%02d:%02d", hourOfDay, minute1);
                        timeText.setText(selectedTime);
                    }, hour, minute, false);
            timePickerDialog.show();
        });


        // Submit Button
        builder.setPositiveButton("Submit", (dialog, which) -> {
            String name = input.getText().toString().trim();
            String date = dateText.getText().toString();
            String time = timeText.getText().toString();

            if (name.isEmpty()) {
                Toast.makeText(TaskList.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                return;
            }

            // Save milliseconds
            long datetimeMillis;
            // if date or time not picked → use current time
            if (date.equals("Select Date") || time.equals("Select Time")) {
                datetimeMillis = System.currentTimeMillis(); // current time
            }else{
                datetimeMillis = calendar.getTimeInMillis();
            }

            long newItemId = myDatabase.insertItem(name, cat_name, datetimeMillis);

            if (newItemId != -1) {
                // schedule using newItemId and stored datetimeMillis
                scheduleNotification(newItemId, name, datetimeMillis);
                loadDataFromDatabase(cat_name);
            } else {
                Toast.makeText(TaskList.this, "Error inserting item", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    // main for loop main content
    @SuppressLint("Range")
    public void loadDataFromDatabase(String cat_name){
        Cursor cursor = myDatabase.getItemByCat(cat_name);

        LinearLayout ll = findViewById(R.id.taskListContainer);
        ll.removeAllViews();  // VERY IMPORTANT: CLEAR OLD ITEMS

        //counter
        int uncheckCount = myDatabase.getUncheckedCount(cat_name);
        txtCounter = findViewById(R.id.txtCounter);
        txtCounter.setText("You have " + uncheckCount + " tasks pending.");

        emptyviewId = findViewById(R.id.emptyviewId);

        if (cursor.getCount() == 0) {
            Toast.makeText(getApplicationContext(), "Task has been empty.", Toast.LENGTH_LONG).show();
            // Check if itemList is empty after processing the cursor
            emptyviewId.setVisibility(View.VISIBLE);
        } else {
            if (cursor.moveToFirst()) {
                do {
                    int varId = cursor.getInt(cursor.getColumnIndex("id"));
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    int isChecked = cursor.getInt(cursor.getColumnIndex("is_checked"));
                    long date_time = cursor.getLong(cursor.getColumnIndex("date_time"));

                    //TEXT VIEW
                    CheckBox checkboxtext = new CheckBox(this);

//                    checkboxtext.setText(name + " " + date_time);
                    checkboxtext.setTextSize(18);
                    checkboxtext.setTextColor(Color.parseColor("#242424"));

                    String dateText = formatDate(date_time);
                    String finalText = name + "<br><small><font color='#888888'><i>" + dateText + "</i></font></small>";
                    checkboxtext.setText(fromHtmlCompat(finalText));

                    // Set Checked or Unchecked based on DB value
                    checkboxtext.setChecked(isChecked == 1);

                    checkboxtext.setPaintFlags(isChecked == 1 ? checkboxtext.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG : checkboxtext.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

                    // Listener for update when user checks/unchecks
                    checkboxtext.setOnCheckedChangeListener((buttonView, isCheckedNow) -> {
                        int newValue = isCheckedNow ? 1 : 0;
                        myDatabase.updateItemCheckedStatus(varId, newValue);

                        int uncheckCount2 = myDatabase.getUncheckedCount(cat_name);
                        txtCounter.setText("You have " + uncheckCount2 + " tasks pending.");

                        if (newValue == 1) {
                            checkboxtext.setPaintFlags(checkboxtext.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            //When updating a task's datetime/name
                            cancelScheduledNotification(varId);
                        } else {
                            checkboxtext.setPaintFlags(checkboxtext.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                            //schedul notfication
                            scheduleNotification(varId, name, date_time);
                        }

                    });

                    checkboxtext.setOnLongClickListener(v -> {
                        showOptionsDialog(varId, name, cat_name, date_time);
                        return true;
                    });

                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    lp.setMargins(0, 10, 0, 10);
                    ll.addView(checkboxtext, lp);

                } while (cursor.moveToNext());
            }
            cursor.close();
            emptyviewId.setVisibility(View.GONE);
        }
    }
    private void showUpdateDialog(int id, String oldName, long oldDatetimeMillis) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Item");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 20);

        // Name EditText
        final EditText input = new EditText(this);
        input.setText(oldName);
        layout.addView(input);

        // Date TextView
        final TextView dateText = new TextView(this);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(oldDatetimeMillis); // set existing date/time
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        dateText.setText(String.format("%02d/%02d/%04d", day, month + 1, year));
        dateText.setPadding(0, 20, 0, 10);
        layout.addView(dateText);

        // Time TextView
        final TextView timeText = new TextView(this);
        timeText.setText(String.format("%02d:%02d", hour, minute));
        timeText.setPadding(0, 10, 0, 20);
        layout.addView(timeText);

        builder.setView(layout);

        // Pick Date
        dateText.setOnClickListener(v -> {
            DatePickerDialog datePicker = new DatePickerDialog(TaskList.this,
                    (view, y, m, d) -> {
                        calendar.set(Calendar.YEAR, y);
                        calendar.set(Calendar.MONTH, m);
                        calendar.set(Calendar.DAY_OF_MONTH, d);
                        dateText.setText(String.format("%02d/%02d/%04d", d, m + 1, y));
                    }, year, month, day);
            datePicker.show();
        });

        // Pick Time
        timeText.setOnClickListener(v -> {
            TimePickerDialog timePicker = new TimePickerDialog(TaskList.this,
                    (view, h, min) -> {
                        calendar.set(Calendar.HOUR_OF_DAY, h);
                        calendar.set(Calendar.MINUTE, min);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        timeText.setText(String.format("%02d:%02d", h, min));
                    }, hour, minute, false);
            timePicker.show();
        });

        // Update button
        builder.setPositiveButton("Update", (dialog, which) -> {
            String newName = input.getText().toString().trim();

            if (newName.isEmpty()) {
                Toast.makeText(TaskList.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                return;
            }

            long newDatetimeMillis = calendar.getTimeInMillis();

            //When updating a task's datetime/name
            cancelScheduledNotification(id);
            boolean updated = myDatabase.updateItem(id, newName, newDatetimeMillis);

            if (updated) {
                scheduleNotification(id, newName, newDatetimeMillis);
                loadDataFromDatabase(cat_name);
            } else {
                Toast.makeText(TaskList.this, "Update failed!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showDeleteDialog(int id, String cat_name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Item?");
        builder.setMessage("Are you sure you want to delete this item?");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            boolean deleted = myDatabase.deleteItem(id);

            if (deleted) {
                cancelScheduledNotification(id);
                loadDataFromDatabase(cat_name); // refresh
            } else {
                Toast.makeText(TaskList.this, "Delete failed!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showOptionsDialog(int id, String name, String cat_name, long oldDatetimeMillis) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an option");

        builder.setPositiveButton("Update", (dialog, which) -> {
            showUpdateDialog(id, name, oldDatetimeMillis);     // your update method
        });

        builder.setNegativeButton("Delete", (dialog, which) -> {
            showDeleteDialog(id, cat_name); // your delete method
        });

        builder.setNeutralButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.show();
    }

    private int getContrastColor(int color) {
        // Calculate luminance
        double luminance = (0.299 * Color.red(color) +
                0.587 * Color.green(color) +
                0.114 * Color.blue(color)) / 255;

        // If luminance is bright → return black text, otherwise white
        return luminance > 0.5 ? Color.BLACK : Color.WHITE;
    }
    //21 html formate
    public static Spanned fromHtmlCompat(String html) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(html);
        }
    }
    //    date formate
    public String formatDate(long timeInMillis) {
        Calendar inputCal = Calendar.getInstance();
        inputCal.setTimeInMillis(timeInMillis);

        Calendar todayCal = Calendar.getInstance();

        // Set to date-only
        Calendar inputDate = (Calendar) inputCal.clone();
        inputDate.set(Calendar.HOUR_OF_DAY, 0);
        inputDate.set(Calendar.MINUTE, 0);
        inputDate.set(Calendar.SECOND, 0);
        inputDate.set(Calendar.MILLISECOND, 0);

        Calendar todayDate = (Calendar) todayCal.clone();
        todayDate.set(Calendar.HOUR_OF_DAY, 0);
        todayDate.set(Calendar.MINUTE, 0);
        todayDate.set(Calendar.SECOND, 0);
        todayDate.set(Calendar.MILLISECOND, 0);

        long diff = (inputDate.getTimeInMillis() - todayDate.getTimeInMillis()) / (1000 * 60 * 60 * 24);

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());

        if (diff == 0) {
            return "Today, " + timeFormat.format(inputCal.getTime());
        } else if (diff == 1) {
            return "Tomorrow, " + timeFormat.format(inputCal.getTime());
        } else if (diff < 7) {
            SimpleDateFormat weekFormat = new SimpleDateFormat("EEEE, h:mm a", Locale.getDefault());
            return weekFormat.format(inputCal.getTime());
        } else {
            SimpleDateFormat fullFormat = new SimpleDateFormat("EEEE, MMM dd, yyyy", Locale.getDefault());
            return fullFormat.format(inputCal.getTime());
        }
    }
    //notification system
    private void scheduleNotification(long taskId, String taskName, long timeMillis) {

        // 1️⃣ Check exact alarm permission for Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (!am.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                return; // stop here → cannot schedule alarm
            }
        }

        if (timeMillis <= System.currentTimeMillis()) {
            // do not schedule past alarms
            return;
        }

        Intent intent = new Intent(this, TaskNotificationReceiver.class);
        intent.putExtra("task_name", taskName);
        intent.putExtra("task_id", (int) taskId);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) taskId, // requestCode -> use task id (cast to int)
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeMillis, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeMillis, pendingIntent);
            }
        }
    }
    private void cancelScheduledNotification(long taskId) {
        Intent intent = new Intent(this, TaskNotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) taskId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }

}