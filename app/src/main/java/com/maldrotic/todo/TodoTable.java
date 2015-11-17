package com.maldrotic.todo;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TodoTable {

    public static final String TABLE_TODO = "todo";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_PRIORITY = "priority";
    public static final String COLUMN_STATUS = "status";

    private static final String DATABASE_CREATE = "create table " + TABLE_TODO
            + "("
            + COLUMN_ID + " integer primary key autoincrement,"
            + COLUMN_TITLE + " text not null,"
            + COLUMN_DATE + " text not null,"
            + COLUMN_TIME + " text not null,"
            + COLUMN_PRIORITY + " integer not null,"
            + COLUMN_STATUS + " text not null"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        Log.w(TodoTable.class.getName(), "Upgrading db from " + oldVersion + " to " + newVersion);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
        onCreate(database);
    }
}
