package com.litemobiletools.todolist;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
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

import java.util.Random;

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

    }
    public void create(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Item");

        final EditText input = new EditText(this);
        input.setHint("Enter item name");

        builder.setView(input);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = input.getText().toString().trim();
//                String cat_name = "general_list";

                if (!name.isEmpty()) {
                    long newItemId = myDatabase.insertItem(name, cat_name);

                    if (newItemId != -1) {
                        loadDataFromDatabase(cat_name); // Refresh list
                    } else {
                        Toast.makeText(TaskList.this, "Error inserting item", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(TaskList.this, "Please enter a name", Toast.LENGTH_SHORT).show();
                }
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
                    String  name = cursor.getString(cursor.getColumnIndex("name"));
                    int isChecked = cursor.getInt(cursor.getColumnIndex("is_checked"));

                    //TEXT VIEW
                    CheckBox checkboxtext = new CheckBox(this);

                    checkboxtext.setText(name);
                    checkboxtext.setTextSize(16);

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
                        } else {
                            checkboxtext.setPaintFlags(checkboxtext.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        }

                    });

                    checkboxtext.setOnLongClickListener(v -> {
                        showOptionsDialog(varId, name, cat_name);
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
    private void showUpdateDialog(int id, String oldName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Item");

        final EditText input = new EditText(this);
        input.setText(oldName);
        builder.setView(input);

        builder.setPositiveButton("Update", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (!newName.isEmpty()) {
                boolean updated = myDatabase.updateItem(id, newName);

                if (updated) {
                    loadDataFromDatabase(cat_name); // refresh
                } else {
                    Toast.makeText(TaskList.this, "Update failed!", Toast.LENGTH_SHORT).show();
                }
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
                loadDataFromDatabase(cat_name); // refresh
            } else {
                Toast.makeText(TaskList.this, "Delete failed!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showOptionsDialog(int id, String name, String cat_name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an option");

        builder.setPositiveButton("Update", (dialog, which) -> {
            showUpdateDialog(id, name);     // your update method
        });

        builder.setNegativeButton("Delete", (dialog, which) -> {
            showDeleteDialog(id, cat_name); // your delete method
        });

        builder.setNeutralButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.show();
    }



}