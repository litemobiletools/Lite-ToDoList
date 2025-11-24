package com.litemobiletools.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load the default fragment (ButtonFragment)
//        if (savedInstanceState == null) {
//            loadFragment(new TitleFragment());
//        }
    }

    public void details(View view) {
        Intent int1 = new Intent(MainActivity.this, TaskList.class);
        startActivity(int1);
    }

//    private void loadFragment(Fragment fragment) {
//        getSupportFragmentManager()
//                .beginTransaction()
//                .replace(R.id.fragment_container, fragment)
//                .commit();
//    }
}