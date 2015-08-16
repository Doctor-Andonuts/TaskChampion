package com.doctorandonuts.taskchampion;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.doctorandonuts.taskchampion.sync.TaskWarriorSync;


public class TaskListActivity extends Activity implements TaskListFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getFragmentManager().findFragmentById(android.R.id.content) == null) {
            TaskListFragment list = new TaskListFragment();
            getFragmentManager().beginTransaction().add(android.R.id.content, list, "ArrayListFrag").commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task_list, menu);
        return true;
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
            Toast.makeText(getApplicationContext(),"Attempting Sync...", Toast.LENGTH_SHORT).show();

            TaskWarriorSync taskWarriorSync = new TaskWarriorSync(this);
            taskWarriorSync.execute();

            return true;
        } else if (id == R.id.action_clear) {
            TaskListFragment fragment = (TaskListFragment) getFragmentManager().findFragmentByTag("ArrayListFrag");
            fragment.clearData();
            ArrayAdapter adapter = (ArrayAdapter) fragment.getListAdapter();
            adapter.notifyDataSetChanged();
        } else if (id == R.id.action_load) {
            TaskListFragment fragment = (TaskListFragment) getFragmentManager().findFragmentByTag("ArrayListFrag");
            fragment.refreshData();
            ArrayAdapter adapter = (ArrayAdapter) fragment.getListAdapter();
            adapter.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }

    public void refreshTaskListFragment() {
        TaskListFragment fragment = (TaskListFragment) getFragmentManager().findFragmentByTag("ArrayListFrag");
        fragment.refreshData();
        ArrayAdapter adapter = (ArrayAdapter) fragment.getListAdapter();
        adapter.notifyDataSetChanged();
    }

    public void onFragmentInteraction(String uuid) {
        Log.i("FragmentList", "onFragmentInteraction: " + uuid);
    }
}
