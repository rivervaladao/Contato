package com.river.app;


import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.river.app.data.DatabaseDescription;

/**
 * Created by cezar on 21/02/16.
 */
public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {

    // interface implemented by TodosFragment to respond
    // when the user touches an item in the RecyclerView
    public interface TaskClickListener {
        void onClick(Uri taskUri);
    }
    // nested subclass of RecyclerView.ViewHolder used to implement
    // the view-holder pattern in the context of a RecyclerView
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView textView;
        private long rowID;
        // TodosAdapter instance variables
        private Cursor cursor = null;

        // configures a RecyclerView item's ViewHolder
        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(android.R.id.text1);
            // attach listener to itemView
            itemView.setOnClickListener(
                    new View.OnClickListener() {
                        // executes when the task in this ViewHolder is clicked
                        @Override
                        public void onClick(View view) {
                            clickListener.onClick(DatabaseDescription.Todo.buildTaskUri(rowID));
                        }
                    }
            );
        }

        // set the database row ID for the task in this ViewHolder
        public void setRowID(long rowID) {
            this.rowID = rowID;
        }
    }

    private final TaskClickListener clickListener;
    private Cursor cursor = null;

    // constructor
    public TodoAdapter(TaskClickListener clickListener) {

        this.clickListener = clickListener;
    }

    // sets up new list item and its ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    // inflate the android.R.layout.simple_list_item_1 layout
        View view = LayoutInflater.from(parent.getContext()).inflate(
                android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(view); // return current item's ViewHolder
    }

    // sets the text of the list item to display the search tag
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        holder.setRowID(cursor.getLong(cursor.getColumnIndex(DatabaseDescription.Todo._ID)));
        holder.textView.setText(cursor.getString(cursor.getColumnIndex(
                DatabaseDescription.Todo.COLUMN_RESUME)));
    }

    // returns the number of items that adapter binds
    @Override
    public int getItemCount() {
        return (cursor != null) ? cursor.getCount() : 0;
    }

    // swap this adapter's current Cursor for a new one
    public void swapCursor(Cursor cursor) {
        this.cursor = cursor;
        notifyDataSetChanged();
    }
}



