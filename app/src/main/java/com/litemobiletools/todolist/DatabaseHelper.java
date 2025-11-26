package com.litemobiletools.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "todolist.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_ITEMS = "todo_list";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DATE = "date_time";
    private static final String COLUMN_CHECKED = "is_checked";
    private static final String COLUMN_CAT = "cat_name";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_ITEMS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " + COLUMN_DATE + " TEXT," + COLUMN_CHECKED + " INTEGER," + COLUMN_CAT + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    public long insertItem(String name, String cat_name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        // Get the current date and time
        String currentDateAndTime = new SimpleDateFormat("hh:mma d-MMM-yy", Locale.getDefault()).format(new Date());
        values.put(COLUMN_DATE, currentDateAndTime);
        values.put(COLUMN_CHECKED, 0);
        values.put(COLUMN_CAT, cat_name);
        return db.insert(TABLE_ITEMS, null, values);
    }

    public Cursor getItemByCat(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_ITEMS, null, COLUMN_CAT + "=?", new String[]{String.valueOf(id)}, null, null, null);
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
    public void updateItemCheckedStatus(int itemId, int isChecked) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_checked", isChecked);
        db.update(TABLE_ITEMS, values, "id = ?", new String[]{String.valueOf(itemId)});
        db.close();
    }

    public int getUncheckedCount(String catName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM "+TABLE_ITEMS+" WHERE is_checked = 0 AND cat_name = ?",
                new String[]{catName}
        );

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    //all count
    public int getItemCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_ITEMS + " WHERE is_checked = 0", null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }
    //delete all
    public void deleteAllByCategory(String catName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, "cat_name = ?", new String[]{catName});
    }

}
