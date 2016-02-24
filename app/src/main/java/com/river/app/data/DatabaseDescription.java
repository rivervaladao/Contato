package com.river.app.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by cezar on 21/02/16.
 */
public class DatabaseDescription {
    // ContentProvider's name: typically the package name
    public static final String AUTHORITY =
            "com.river.app.todo.data";
    // base URI used to interact with the ContentProvider
    private static final Uri BASE_CONTENT_URI =
            Uri.parse("content://" + AUTHORITY);

    public static final class Todo implements BaseColumns{
        public static final String TABLE_NAME = "todo_tasks"; // table's name

        // Uri for the contacts table
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
        // column names for table's columns
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_RESUME = "resume";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_DATE = "date";
        // creates a Uri for a specific contact
        public static Uri buildTaskUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id) ;
        }
    }
}
