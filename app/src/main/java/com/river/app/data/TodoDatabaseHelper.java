package com.river.app.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.river.app.data.DatabaseDescription.Todo;

/**
 * Created by cezar on 21/02/16.
 */
public class TodoDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TODO.db";
    private static final int DATABASE_VERSION = 1;

    // constructor
    public TodoDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // creates the contacts table when the database is created
    @Override
    public void onCreate(SQLiteDatabase db) {
    // SQL for creating the contacts table
        final String CREATE_CONTACTS_TABLE =
                "CREATE TABLE " + Todo.TABLE_NAME + "(" + Todo._ID + " integer primary key, " +
                        Todo.COLUMN_CATEGORY + " TEXT, " +
                        Todo.COLUMN_RESUME + " TEXT, " +
                        Todo.COLUMN_DESCRIPTION + " TEXT, " +
                        Todo.COLUMN_DATE + " DATETIME);";

        db.execSQL(CREATE_CONTACTS_TABLE); // create the contacts table
    }

    // normally defines how to upgrade the database when the schema changes
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
    }
}
