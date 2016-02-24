package com.river.app;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements
        TodoFragment.TodoFragmentListener,
        DetailFragment.DetailFragmentListener,
        AddEditFragment.AddEditFragmentListener {

    // key for storing a contact's Uri in a Bundle passed to a fragment
    public static final String TODO_URI = "todo_uri";
    private TodoFragment todoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // if layout contains fragmentContainer, the phone layout is in use;
        // create and display a ContactsFragment
        if (savedInstanceState == null &&
                findViewById(R.id.fragmentContainer) != null) {
            // create ContactsFragment
            todoFragment = new TodoFragment();

            // add the fragment to the FrameLayout
            FragmentTransaction transaction =
                    getSupportFragmentManager().beginTransaction();

            transaction.add(R.id.fragmentContainer, todoFragment);
            transaction.commit(); // display ContactsFragment

        } else {
            todoFragment = (TodoFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.todoFragment);
        }
    }

    // display a contact
    private void displayTask(Uri taskUri, int viewID) {
        DetailFragment detailFragment = new DetailFragment();

        // specify contact's Uri as an argument to the DetailFragment
        Bundle arguments = new Bundle();
        arguments.putParcelable(TODO_URI, taskUri);
        detailFragment.setArguments(arguments);

        // use a FragmentTransaction to display the DetailFragment
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, detailFragment);
        transaction.addToBackStack(null);
        transaction.commit(); // causes DetailFragment to display
    }

    // display fragment for adding a new or editing an existing contact
    private void displayAddEditFragment(int viewID, Uri taskUri) {
        AddEditFragment addEditFragment = new AddEditFragment();

        // if editing existing contact, provide contactUri as an argument
        if (taskUri != null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(TODO_URI, taskUri);
            addEditFragment.setArguments(arguments);
        }

        // use a FragmentTransaction to display the AddEditFragment
        FragmentTransaction transaction =
                getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, addEditFragment);
        transaction.addToBackStack(null);
        transaction.commit(); // causes AddEditFragment to display
    }

    // display DetailFragment for selected contact
    @Override
    public void onTaskSelected(Uri contactUri) {
        if (findViewById(R.id.fragmentContainer) != null) // phone

            displayTask(contactUri, R.id.fragmentContainer);

        else { // tablet

            // removes top of back stack
            getSupportFragmentManager().popBackStack();
            displayTask(contactUri, R.id.rightPaneContainer);
        }
    }

    // display AddEditFragment to add a new contact
    @Override
    public void onAddTask() {
        if (findViewById(R.id.fragmentContainer) != null) // pone
            displayAddEditFragment(R.id.fragmentContainer, null);
        else // tablet
            displayAddEditFragment(R.id.rightPaneContainer, null);
    }

    @Override
    public void onTaskDelete() {
        getSupportFragmentManager().popBackStack();
        todoFragment.updateTaskList();
    }

    @Override
    public void onEditTask(Uri taskUri) {
        if (findViewById(R.id.fragmentContainer) != null) // phone
            displayAddEditFragment(R.id.fragmentContainer, taskUri);
        else // tablet
            displayAddEditFragment(R.id.rightPaneContainer, taskUri);
    }

    @Override
    public void onAddEditCompleted(Uri taskUri) {
        getSupportFragmentManager().popBackStack();
        todoFragment.updateTaskList(); // refresh contacts
        if (findViewById(R.id.fragmentContainer) == null) { // tablet
            // removes top of back stack
            getSupportFragmentManager().popBackStack();
            // on tablet, display contact that was just added or edited
            displayTask(taskUri, R.id.rightPaneContainer);
        }
    }
}
