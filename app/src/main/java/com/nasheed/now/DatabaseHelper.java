package com.nasheed.now;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mydatabase.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_ITEMS = "items";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";

    private static final String COLUMN_DATE = "date_Time";
    private static final String COLUMN_CHECKED = "is_checked";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_ITEMS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " + COLUMN_DATE + " TEXT," + COLUMN_CHECKED + " INTEGER)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    public long insertItem(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        // Get the current date
        //String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        //values.put(COLUMN_DATE, currentDate);
        // Get the current date and time
        String currentDateAndTime = new SimpleDateFormat("hh:mma d-MMM-yy", Locale.getDefault()).format(new Date());
        values.put(COLUMN_DATE, currentDateAndTime);
        values.put(COLUMN_CHECKED, 0);
        return db.insert(TABLE_ITEMS, null, values);
    }

    public Cursor getAllItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_ITEMS, null, null, null, null, null, "id DESC");
    }

    public Cursor getItemById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_ITEMS, null, COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
    }

    public boolean updateItem(int id, String newName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, newName);

        // Get the current date and time
        String currentDateAndTime = new SimpleDateFormat("hh:mma d-MMM-yy", Locale.getDefault()).format(new Date());
        values.put(COLUMN_DATE, currentDateAndTime);

        int rows = db.update(TABLE_ITEMS, values, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }
    public boolean deleteItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete(TABLE_ITEMS, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }


    public void updateItemCheckedStatus(int itemId, boolean isChecked) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_checked", isChecked ? 1 : 0);
        db.update("items", values, "id = ?", new String[]{String.valueOf(itemId)});
        db.close();
    }

}
