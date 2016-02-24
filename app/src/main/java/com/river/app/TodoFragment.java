package com.river.app;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.river.app.data.DatabaseDescription;

/**
 * A placeholder fragment containing a simple view.
 */
public class TodoFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int TASKS_LOADER = 0;
    private TodoFragmentListener listener;
    private TodoAdapter todoAdapter;

    public TodoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_todo, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
        todoAdapter = new TodoAdapter(new TodoAdapter.TaskClickListener() {
            @Override
            public void onClick(Uri taskUri) {
                listener.onTaskSelected(taskUri);
            }
        });
        recyclerView.setAdapter(todoAdapter);
        recyclerView.addItemDecoration(new ItemDivider(getContext()));
        recyclerView.setHasFixedSize(true);
        FloatingActionButton addButton =
                (FloatingActionButton) view.findViewById(R.id.addButton);
        addButton.setOnClickListener(
                new View.OnClickListener() {
                    // displays the AddEditFragment when FAB is touched
                    @Override
                    public void onClick(View view) {
                        listener.onAddTask();
                    }
                }
        );
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (TodoFragmentListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(TASKS_LOADER, null, this);
    }

    public void updateTaskList() {
        todoAdapter.notifyDataSetChanged();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case TASKS_LOADER:
                return new CursorLoader(getActivity(), DatabaseDescription.Todo.CONTENT_URI,
                        null, null, null,
                        DatabaseDescription.Todo.COLUMN_RESUME + " COLLATE NOCASE ASC");
            default:
                return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        todoAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        todoAdapter.swapCursor(null);
    }

    public interface TodoFragmentListener {
        public void onTaskSelected(Uri taskUri);

        public void onAddTask();
    }
}
