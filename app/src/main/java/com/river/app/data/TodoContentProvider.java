package com.river.app.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.river.app.R;

public class TodoContentProvider extends ContentProvider {
    // UriMatcher helps ContentProvider determine operation to perform
    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);
    private static final int ONE_TASK = 1; // manipulate one TODO

    // constants used with UriMatcher to determine operation to perform
    private static final int TASKS = 2; // manipulate TODO table

    // static block to configure this ContentProvider's UriMatcher
    static {
        // Uri for Contact with the specified id (#)
        uriMatcher.addURI(DatabaseDescription.AUTHORITY,
                DatabaseDescription.Todo.TABLE_NAME + "/#", ONE_TASK);
        // Uri for Contacts table
        uriMatcher.addURI(DatabaseDescription.AUTHORITY,
                DatabaseDescription.Todo.TABLE_NAME, TASKS);
    }

    // used to access the database
    private TodoDatabaseHelper dbHelper;

    public TodoContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int numberOfRowsDeleted;
        switch (uriMatcher.match(uri)) {
            case ONE_TASK:
// get from the uri the id of contact to update
                String id = uri.getLastPathSegment();
// delete the contact
                numberOfRowsDeleted = dbHelper.getWritableDatabase().delete(
                        DatabaseDescription.Todo.TABLE_NAME, DatabaseDescription.Todo._ID + "=" + id, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.invalid_delete_uri) + uri);
        }
// notify observers that the database changed
        if (numberOfRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numberOfRowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri newTaskUri = null;
        switch (uriMatcher.match(uri)) {
            case TASKS:
                // insert the new contact--success yields new contact's row id
                long rowId = dbHelper.getWritableDatabase().insert(
                        DatabaseDescription.Todo.TABLE_NAME, null, values);
                // if the contact was inserted, create an appropriate Uri;
                // otherwise, throw an exception
                if (rowId > 0) { // SQLite row IDs start at 1
                    newTaskUri = DatabaseDescription.Todo.buildTaskUri(rowId);
                    // notify observers that the database changed
                    getContext().getContentResolver().notifyChange(uri, null);
                } else
                    throw new SQLException(
                            getContext().getString(R.string.invalid_insert_uri) + uri);
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.invalid_insert_uri) + uri);
        }
        return newTaskUri;
    }

    @Override
    public boolean onCreate() {
        dbHelper = new TodoDatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(DatabaseDescription.Todo.TABLE_NAME);
        switch (uriMatcher.match(uri)) {
            case ONE_TASK:
                queryBuilder.appendWhere(DatabaseDescription.Todo._ID + " = " + uri.getLastPathSegment());
                break;
            case TASKS:
                break;
            default:
                throw new UnsupportedOperationException(getContext().getString(R.string.invalid_query_uri) + uri);
        }
        Cursor cursor = queryBuilder.query(dbHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int numberOfRowsUpdated; // 1 if update successful; 0 otherwise
        switch (uriMatcher.match(uri)) {
            case ONE_TASK:
// get from the uri the id of contact to update
                String id = uri.getLastPathSegment();
// update the contact
                numberOfRowsUpdated = dbHelper.getWritableDatabase().update(
                        DatabaseDescription.Todo.TABLE_NAME, values, DatabaseDescription.Todo._ID + "=" + id,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException(
                        getContext().getString(R.string.invalid_update_uri) + uri);
        }
// if changes were made, notify observers that the database changed
        if (numberOfRowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numberOfRowsUpdated;
    }
}
