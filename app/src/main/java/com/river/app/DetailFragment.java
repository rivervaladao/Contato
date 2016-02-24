package com.river.app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.river.app.data.DatabaseDescription;

/**
 * Created by cezar on 21/02/16.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public interface DetailFragmentListener {
        public void onTaskDelete();
        public void onEditTask(Uri taskUri);
    }

    private static final int TASK_LOADER = 0;
    private DetailFragmentListener listener;
    private Uri taskUri;

    private TextView categoryTextView;
    private TextView resumeTextView;
    private TextView descriptionTextView;
    private TextView dateTextView;

    // set DetailFragmentListener when fragment attached
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (DetailFragmentListener) context;
    }
    // remove DetailFragmentListener when fragment detached
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
    // called when DetailFragmentListener's view needs to be created
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true); // this fragment has menu items to display
        // get Bundle of arguments then extract the contact's Uri
        Bundle arguments = getArguments();
        if (arguments != null)
            taskUri = arguments.getParcelable(MainActivity.TODO_URI);
        // inflate DetailFragment's layout
        View view =
                inflater.inflate(R.layout.fragment_details, container, false);
        // get the EditTexts
        categoryTextView = (TextView) view.findViewById(R.id.categoryTextView);
        resumeTextView = (TextView) view.findViewById(R.id.resumeTextView);
        descriptionTextView = (TextView) view.findViewById(R.id.descriptionTextView);
        dateTextView = (TextView) view.findViewById(R.id.dateTextView);

        //load tasks
        getLoaderManager().initLoader(TASK_LOADER, null, this);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu,MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.fragment_details_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){
            case R.id.action_edit:
                listener.onEditTask(taskUri);
                return true;
            case R.id.action_delete:
                deleteTask();
                return true;
        };


        return super.onOptionsItemSelected(item);
    }
    private void deleteTask() {
        // use FragmentManager to display the confirmDelete DialogFragment
        confirmDelete.show(getFragmentManager(), "confirm delete");
    }
    // DialogFragment to confirm deletion of contact
    private final DialogFragment confirmDelete =
            new DialogFragment() {
                // create an AlertDialog and return it
                @Override
                public Dialog onCreateDialog(Bundle bundle) {
                    // create a new AlertDialog Builder
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(getActivity());
                    builder.setTitle(R.string.confirm_title);
                    builder.setMessage(R.string.confirm_message);
                    // provide an OK button that simply dismisses the dialog
                    builder.setPositiveButton(R.string.button_delete,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        DialogInterface dialog, int button) {
                                        // use Activity's ContentResolver to invoke
                                        // delete on the AddressBookContentProvider
                                    getActivity().getContentResolver().delete(
                                            taskUri, null, null);
                                    listener.onTaskDelete(); // notify listener
                                }
                            }
                    );
                    builder.setNegativeButton(R.string.button_cancel, null);
                    return builder.create(); // return the AlertDialog
                }
            };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader;
        switch (id) {
            case TASK_LOADER:
                cursorLoader = new CursorLoader(getActivity(),
                        taskUri, // Uri of contact to display
                        null, // null projection returns all columns
                        null, // null selection returns all rows
                        null, // no selection arguments
                        null); // sort order
                break;
            default:
                cursorLoader = null;
                break;
        }
        return cursorLoader;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst() ) {
            // get the column index for each data item
            int categoryIndex = data.getColumnIndex(DatabaseDescription.Todo.COLUMN_CATEGORY);
            int resumeIndex = data.getColumnIndex(DatabaseDescription.Todo.COLUMN_RESUME);
            int descriptionIndex = data.getColumnIndex(DatabaseDescription.Todo.COLUMN_DESCRIPTION);
            int dateIndex = data.getColumnIndex(DatabaseDescription.Todo.COLUMN_DATE);

            //fill EditTexts
            categoryTextView.setText(data.getString(categoryIndex));
            resumeTextView.setText(data.getString(resumeIndex));
            descriptionTextView.setText(data.getString(descriptionIndex));
            dateTextView.setText(data.getString(dateIndex));

        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
