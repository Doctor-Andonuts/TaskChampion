package com.doctorandonuts.taskchampion;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.doctorandonuts.taskchampion.sync.TaskWarriorSync;

import org.json.JSONObject;

import java.util.regex.Pattern;


public class TaskListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_task_list);

        if (getFragmentManager().findFragmentById(android.R.id.content) == null) {
            ArrayListFragment list = new ArrayListFragment();
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

            // TODO: Load the data from file if it can find any
            // TODO: Load sync data from taskd server
            // TODO: Parse the data returned and combine it with the file
            // TODO: Save the file
            // TODO: Tell the list fragment to do a refresh after async completes which will parse back from file and load the list again

            TaskWarriorSync taskWarriorSync = new TaskWarriorSync(getBaseContext());
            taskWarriorSync.execute();

            ArrayListFragment fragment = (ArrayListFragment) getFragmentManager().findFragmentByTag("ArrayListFrag");
            fragment.refreshData();
            ArrayAdapter adapter = (ArrayAdapter) fragment.getListAdapter();
            adapter.notifyDataSetChanged();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
