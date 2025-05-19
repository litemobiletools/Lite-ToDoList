package com.nasheed.now;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Stack;

public class DetailsFragment extends Fragment {
    private static final String ARG_ITEM_ID = "item_id";
    private int itemId;
    private EditText editTextItem;
    private DatabaseHelper databaseHelper;
    private Handler handler = new Handler();
    private Runnable saveRunnable;
    private Stack<String> undoStack = new Stack<>();
    private Stack<String> redoStack = new Stack<>();

    public FloatingActionButton fab;

    public static DetailsFragment newInstance(int itemId) {
        DetailsFragment fragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ITEM_ID, itemId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        editTextItem = view.findViewById(R.id.editTextItem);

        //mEditor.setEditorBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundColor(Color.BLUE);
        //mEditor.setBackgroundResource(R.drawable.bg);
        editTextItem.setPadding(10, 10, 10, 10);
        //mEditor.setBackground("https://raw.githubusercontent.com/wasabeef/art/master/chip.jpg");
        //mEditor.setInputEnabled(false);

//        view.findViewById(R.id.action_insert_checkbox).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                editTextItem.insertTodo();
//            }
//        });

        databaseHelper = new DatabaseHelper(getActivity());
        if (getArguments() != null) {
            itemId = getArguments().getInt(ARG_ITEM_ID);
        }
        // Load data from database
        loadItemFromDatabase();

//        fab = view.findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                saveItemToDatabase(text);
//            }
//        });

        editTextItem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Optional: Called before the text is changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Optional: Called as the text is changing
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Called after the text has been changed
                saveItemToDatabase(s.toString());
            }
        });



        return view;
    }
    private void loadItemFromDatabase() {
        Cursor cursor = databaseHelper.getItemById(itemId);
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range")
            String itemName = cursor.getString(cursor.getColumnIndex("name"));
            editTextItem.setText(itemName);
            cursor.close();
        }
    }
    private void saveItemToDatabase(String newName) {
        boolean result = databaseHelper.updateItem(itemId, newName);
        if (result) {
            Toast.makeText(getActivity(), "Item saved", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Error saving item", Toast.LENGTH_SHORT).show();
        }
    }
}