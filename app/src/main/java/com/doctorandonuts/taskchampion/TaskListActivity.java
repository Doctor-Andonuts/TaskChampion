package com.doctorandonuts.taskchampion;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.doctorandonuts.taskchampion.sync.TaskWarriorSync;
import com.doctorandonuts.taskchampion.task.CustomArrayAdapter;
import com.doctorandonuts.taskchampion.task.Task;
import com.doctorandonuts.taskchampion.task.TaskManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;


public class TaskListActivity extends Activity implements TaskListFragment.OnFragmentInteractionListener {



    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'kkmmss'Z'", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getFragmentManager().findFragmentById(android.R.id.content) == null) {
            TaskListFragment taskListFragment = new TaskListFragment();
            getFragmentManager().beginTransaction()
                    .add(android.R.id.content, taskListFragment, "ArrayListFrag")
                    .commit();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sync) {
            Log.d("TaskWarriorSync", "Attempting Sync...");
            Toast.makeText(getApplicationContext(), "Attempting Sync...", Toast.LENGTH_SHORT).show();

            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo ni = cm.getActiveNetworkInfo();
            if (ni == null) {
                // There are no active networks.
                Toast.makeText(this, "No connection to internet detected", Toast.LENGTH_SHORT).show();
                return false;
            }


            TaskWarriorSync taskWarriorSync = new TaskWarriorSync(this);
            taskWarriorSync.execute();

            return true;
        } else if (id == R.id.action_clear_all) {
            SharedPreferences sharedPref = getSharedPreferences("com.doctorandonuts.taskchampion.prefSync", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("syncKey", "");
            editor.putString("context", "");
            editor.apply();
            TaskListFragment fragment = (TaskListFragment) getFragmentManager().findFragmentByTag("ArrayListFrag");
            fragment.clearData();
            CustomArrayAdapter adapter = (CustomArrayAdapter) fragment.getListAdapter();
            adapter.notifyDataSetChanged();
            return true;
        } else if (id == R.id.action_add) {
            onFragmentInteraction();
            return true;
        } else if (id == R.id.action_set_context) {
            AlertDialog levelDialog;
            final TextView contextTextView = (TextView) findViewById(R.id.contextTextView);

            // Strings to Show In Dialog with Radio Buttons
            final CharSequence[] items = {"Home","Work","None"};

            // Creating and Building the Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select The Difficulty Level");
            builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    SharedPreferences sharedPref = getSharedPreferences("com.doctorandonuts.taskchampion.prefSync", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    switch (item) {
                        case 0:
                            editor.putString("context", "home");
                            contextTextView.setText("Context: Home");
                            break;
                        case 1:
                            editor.putString("context", "work");
                            contextTextView.setText("Context: Work");
                            break;
                        case 2:
                            editor.putString("context", "");
                            contextTextView.setText("Context: None");
                            break;
                    }
                    editor.apply();
                    dialog.dismiss();
                    refreshTaskListFragment();
                }
            });
            levelDialog = builder.create();
            levelDialog.show();

            return true;
        } else if (id == android.R.id.home) {
            hideSoftKeyboard(findViewById(android.R.id.content));
            getFragmentManager().popBackStack();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void refreshTaskListFragment() {
        TaskListFragment taskListFragment = (TaskListFragment) getFragmentManager().findFragmentByTag("ArrayListFrag");
        taskListFragment.refreshData();
        CustomArrayAdapter adapter = (CustomArrayAdapter) taskListFragment.getListAdapter();
        TaskComparator taskComparator = new TaskComparator();
        adapter.sort(taskComparator);
    }

    public void onFragmentInteraction(Task task) {
        Log.i("FragmentList", "onFragmentInteraction: " + task.getValue("uuid"));
        TaskDetailsFragment taskDetailsFragment = new TaskDetailsFragment();
        taskDetailsFragment.setTask(task);
        TaskListFragment taskListFragment = (TaskListFragment) getFragmentManager().findFragmentByTag("ArrayListFrag");
        getFragmentManager().beginTransaction()
                .remove(taskListFragment)
                .add(android.R.id.content, taskDetailsFragment, "TaskDetailFragment")
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected void onResume() {
        refreshTaskListFragment();
        super.onResume();
    }

    public void onFragmentInteraction() {
        TaskCreateFragment taskCreateFragment = new TaskCreateFragment();
        TaskListFragment taskListFragment = (TaskListFragment) getFragmentManager().findFragmentByTag("ArrayListFrag");
        getFragmentManager().beginTransaction()
                .remove(taskListFragment)
                .add(android.R.id.content, taskCreateFragment, "TaskCreateFragment")
                .addToBackStack(null)
                .commit();
    }


    public void addTask(View view) {
        hideSoftKeyboard(view);

        TextView editDescription = (TextView) findViewById(R.id.editDescription);
        TextView tagsTextView = (TextView) findViewById(R.id.createTagsText);

        JSONObject newTaskJson = new JSONObject();

        try {
            String uuid = UUID.randomUUID().toString();
            Date now = new Date();
            sdf.setTimeZone(TimeZone.getTimeZone("est"));

            newTaskJson.put("status", "pending");
            newTaskJson.put("uuid", uuid);
            newTaskJson.put("entry", sdf.format(now));
            newTaskJson.put("description", editDescription.getText());
        } catch(Exception e) {
            Log.e("CreateTest", "I DONT KNOW" + e.toString());
        }

        Task newTask = new Task(newTaskJson);
        try {
            JSONArray tags = new JSONArray(tagsTextView.getText().toString());
            newTask.setTags(tags);
        } catch (Exception e) {
            Log.d("CreateTest Error: ", e.toString());
        }

        Log.d("CreateTest", newTask.getJsonString());

        TaskManager taskManager = new TaskManager(getBaseContext());
        taskManager.addOrUpdateTask(newTask);

        refreshTaskListFragment();
        TaskListFragment taskListFragment = (TaskListFragment) getFragmentManager().findFragmentByTag("ArrayListFrag");
        TaskCreateFragment taskCreateFragment = (TaskCreateFragment) getFragmentManager().findFragmentByTag("TaskCreateFragment");
        getFragmentManager().beginTransaction()
                .remove(taskCreateFragment)
                .add(android.R.id.content, taskListFragment, "ArrayListFrag")
                .commit();

        Toast.makeText(getBaseContext(), "New Task Added", Toast.LENGTH_SHORT).show();
    }


    private void hideSoftKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager)  getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
