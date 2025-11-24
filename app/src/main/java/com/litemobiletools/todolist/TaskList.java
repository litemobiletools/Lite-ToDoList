package com.litemobiletools.todolist;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TaskList extends AppCompatActivity {
    public DatabaseHelper myDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);
        myDatabase = new DatabaseHelper(this);
    }
    public void create(View view) {
        // Insert a new item in the database
        String name = "Test";
        String cat_name = "general_list";
        long newItemId = myDatabase.insertItem(name, cat_name);
        if (newItemId != -1) { // Check if insertion was successful
            // Refresh the list
            loadDataFromDatabase(cat_name);
        } else {
            Toast.makeText(this, "Error inserting item", Toast.LENGTH_SHORT).show();
        }
    }
    // main for loop main content
    @SuppressLint("Range")
    public void loadDataFromDatabase(String cat_name){
        Cursor cursor = myDatabase.getItemByCat(cat_name);
        if (cursor.getCount() == 0) {
            Toast.makeText(getApplicationContext(), "Not Found", Toast.LENGTH_LONG).show();
        } else {
            if (cursor.moveToFirst()) {
                do {
                    String varId = cursor.getString(cursor.getColumnIndex("Id"));
                    String  name = cursor.getString(cursor.getColumnIndex("name"));
                    //TEXT VIEW
                    CheckBox checkboxtext = new CheckBox(this);

                    checkboxtext.setText(name);
                    checkboxtext.setTextSize(16);
                    checkboxtext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String dataID = varId;
                        }
                    });

                    LinearLayout ll = (LinearLayout)findViewById(R.id.taskListContainer);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    checkboxtext.setLayoutParams(lp);
                    ll.addView(checkboxtext, lp);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
    }
}