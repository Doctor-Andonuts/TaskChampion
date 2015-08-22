package com.doctorandonuts.taskchampion;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.doctorandonuts.taskchampion.sync.TaskWarriorSync;
import com.doctorandonuts.taskchampion.task.CustomArrayAdapter;
import com.doctorandonuts.taskchampion.task.Task;

import java.util.Comparator;


public class TaskListActivity extends Activity implements TaskListFragment.OnFragmentInteractionListener,TaskDetailsFragment.OnFragmentInteractionListener {

    private Comparator descriptionSort = new Comparator<Task>() {
        @Override
        public int compare(Task lhs, Task rhs) {
            return lhs.getValue("description").compareTo(rhs.getValue("description"));
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getFragmentManager().findFragmentById(android.R.id.content) == null) {
            TaskListFragment taskListFragment = new TaskListFragment();
            getFragmentManager().beginTransaction().add(android.R.id.content, taskListFragment, "ArrayListFrag").commit();
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

            TaskWarriorSync taskWarriorSync = new TaskWarriorSync(this);
            taskWarriorSync.execute();

            return true;
        } else if (id == R.id.action_clear_all) {
            SharedPreferences sharedPref = getSharedPreferences("com.doctorandonuts.taskchampion.prefSync", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("syncKey", "");
            editor.commit();
            TaskListFragment fragment = (TaskListFragment) getFragmentManager().findFragmentByTag("ArrayListFrag");
            fragment.clearData();
            CustomArrayAdapter adapter = (CustomArrayAdapter) fragment.getListAdapter();
            adapter.sort(descriptionSort);
        } else if (id == android.R.id.home) {
            getFragmentManager().popBackStack();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void refreshTaskListFragment() {
        TaskListFragment taskListFragment = (TaskListFragment) getFragmentManager().findFragmentByTag("ArrayListFrag");
        taskListFragment.refreshData();
        CustomArrayAdapter adapter = (CustomArrayAdapter) taskListFragment.getListAdapter();
        adapter.sort(descriptionSort);
        //adapter.notifyDataSetChanged();
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
        Log.i("FragmentList", "onFragmentInteraction: Uri");
    }
}
