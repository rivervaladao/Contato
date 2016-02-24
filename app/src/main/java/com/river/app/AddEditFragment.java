package com.river.app;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.river.app.data.DatabaseDescription.Todo;

/**
 * Created by cezar on 21/02/16.
 */
public class AddEditFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    // constant used to identify the Loader
    private static final int TASK_LOADER = 0;
    private AddEditFragmentListener listener; // MainActivity
    private Uri taskUri; // Uri of selected contact
    private boolean addingNewTask = true; // adding (true) or editing

    private TextInputLayout resumeTextInputLayout;
    private TextInputLayout descriptionTextInputLayout;
    private TextInputLayout categoryTextInputLayout;
    private TextInputLayout dateTextInputLayout;
    private FloatingActionButton saveTaskFAB;

    private CoordinatorLayout coordinatorLayout;
    // set AddEditFragmentListener when Fragment attached

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (AddEditFragmentListener) context;
    }
    // remove AddEditFragmentListener when Fragment detached
    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_add_edit,container,false);

        categoryTextInputLayout = (TextInputLayout)view.findViewById(R.id.categoryTextInputLayout);

        resumeTextInputLayout = (TextInputLayout) view.findViewById(R.id.resumeTextInputLayout);
        resumeTextInputLayout.getEditText().addTextChangedListener(resumeChangedListener);

        descriptionTextInputLayout = (TextInputLayout) view.findViewById(R.id.descriptionTextInputLayout);

        dateTextInputLayout = (TextInputLayout) view.findViewById(R.id.dateTextInputLayout);

        saveTaskFAB = (FloatingActionButton) view.findViewById(R.id.editButton);
        saveTaskFAB.setOnClickListener(saveTaskButtonClicked);
        updateSaveButtonFAB();

        coordinatorLayout = (CoordinatorLayout) getActivity().findViewById(R.id.coordinatorLayout);
        Bundle arguments = getArguments();

        if(arguments != null){
            addingNewTask = false;
            taskUri = arguments.getParcelable(MainActivity.TODO_URI);
        }

        if(taskUri !=null){
            getLoaderManager().initLoader(TASK_LOADER,null,this);
        }
        return view;
    }

    private final TextWatcher resumeChangedListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        // called when the text in nameTextInputLayout changes
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            updateSaveButtonFAB();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    // shows saveButtonFAB only if the name is not empty
    private void updateSaveButtonFAB() {
        String input =
                resumeTextInputLayout.getEditText().getText().toString();
        // if there is a name for the contact, show the FloatingActionButton
        if (input.trim().length() != 0)
            saveTaskFAB.show();
        else
            saveTaskFAB.hide();
    }
    private final View.OnClickListener saveTaskButtonClicked =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // hide the virtual keyboard
                    ((InputMethodManager) getActivity().getSystemService(
                            Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                            getView().getWindowToken(), 0);
                    saveTask(); // save contact to the database
                }
            };
    // saves contact information to the database
    private void saveTask() {
        // create ContentValues object containing contact's key-value pairs
        ContentValues contentValues = new ContentValues();
        contentValues.put(Todo.COLUMN_CATEGORY,
                categoryTextInputLayout.getEditText().getText().toString());
        contentValues.put(Todo.COLUMN_RESUME,
                resumeTextInputLayout.getEditText().getText().toString());
        contentValues.put(Todo.COLUMN_DESCRIPTION,
                descriptionTextInputLayout.getEditText().getText().toString());
        contentValues.put(Todo.COLUMN_DATE,
                dateTextInputLayout.getEditText().getText().toString());

        if (addingNewTask) {
            // use Activity's ContentResolver to invoke
            // insert on the AddressBookContentProvider
            Uri newTaskUri = getActivity().getContentResolver().insert(
                    Todo.CONTENT_URI, contentValues);
            if (newTaskUri != null) {
                Snackbar.make(coordinatorLayout,
                        R.string.task_added, Snackbar.LENGTH_LONG).show();
                listener.onAddEditCompleted(newTaskUri);
            }else {
                Snackbar.make(coordinatorLayout,
                        R.string.task_not_added, Snackbar.LENGTH_LONG).show();
            }
        }
        else {
            // use Activity's ContentResolver to invoke
            // insert on the AddressBookContentProvider
            int updatedRows = getActivity().getContentResolver().update(
                    taskUri, contentValues, null, null);
            if (updatedRows > 0) {
                listener.onAddEditCompleted(taskUri);
                Snackbar.make(coordinatorLayout,
                        R.string.task_updated, Snackbar.LENGTH_LONG).show();
            }
            else {
                Snackbar.make(coordinatorLayout,
                        R.string.task_not_updated, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch(id){
            case TASK_LOADER:
                return new CursorLoader(getActivity(),
            taskUri,null,null,null,null);
            default:
                return  null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst() ) {
            // get the column index for each data item
            int categoryIndex = data.getColumnIndex(Todo.COLUMN_CATEGORY);
            int resumeIndex = data.getColumnIndex(Todo.COLUMN_RESUME);
            int descriptionIndex = data.getColumnIndex(Todo.COLUMN_DESCRIPTION);
            int dateIndex = data.getColumnIndex(Todo.COLUMN_DATE);

            //fill EditTexts
            categoryTextInputLayout.getEditText().setText(data.getString(categoryIndex));
            resumeTextInputLayout.getEditText().setText(data.getString(resumeIndex));
            descriptionTextInputLayout.getEditText().setText(data.getString(descriptionIndex));
            dateTextInputLayout.getEditText().setText(data.getString(dateIndex));

            updateSaveButtonFAB();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public interface AddEditFragmentListener {
        public void onAddEditCompleted(Uri taskUri);
    }
}
