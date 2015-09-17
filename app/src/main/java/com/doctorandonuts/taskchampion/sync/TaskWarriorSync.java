package com.doctorandonuts.taskchampion.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.doctorandonuts.taskchampion.TaskListActivity;
import com.doctorandonuts.taskchampion.task.Task;
import com.doctorandonuts.taskchampion.task.TaskManager;

import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by Mr Saturn on 8/7/2015 for TaskChampion
 */
public class TaskWarriorSync extends AsyncTask<Void, Void, String> {

    private int numberOfTasksUploaded;
    private TaskListActivity _taskListActivity;

    private String TAG = "TaskWarriorSync";
    private Context _context;
    public TaskWarriorSync (TaskListActivity taskListActivity) {
        _taskListActivity = taskListActivity;
        _context = taskListActivity.getBaseContext();

    }

    private Cert cert = new Cert();

    @Override
    protected String doInBackground(Void... params) {
        SharedPreferences sharedPref = _context.getSharedPreferences("com.doctorandonuts.taskchampion.prefSync", Context.MODE_PRIVATE);
        String syncKey = sharedPref.getString("syncKey", "");

        final Msg sync = new Msg();
        sync.clear();
        final StringBuilder payload = new StringBuilder();
        payload.append(syncKey);
        payload.append("\n");

        TaskManager taskManager = new TaskManager(_context);
        List<Task> taskList = taskManager.getBacklogData();
        numberOfTasksUploaded = taskList.size();
        for(Task task : taskList) {
            payload.append(task.getJsonString());
            payload.append("\n");
        }
//        String uuid = UUID.randomUUID().toString();
//        payload.append("{\"description\":\"test\",\"entry\":\"20150825T175211Z\",\"status\":\"pending\",\"uuid\":\""+uuid+"\"}");

        sync.setPayload(payload.toString());
//        Log.d(TAG, "ALL: " + sync.serialize());
        sync.serialize();

        TLSClient tlsClient = new TLSClient();
        try {
            tlsClient.init(cert.ca_cert, cert.Andonuts_cert, cert.Andonuts_key);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        try {
            tlsClient.connect(cert.server, cert.port);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        Log.d(TAG, "send: " + sync.serialize());
        tlsClient.send(sync.serialize());
        final String response = tlsClient.recv();
        Log.d(TAG, "response: " + response);
        tlsClient.close();

        try {
            sync.parse(response);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }


        if (sync.getHeaderCode().equals("200") || sync.getHeaderCode().equals("201")) {
            taskManager.clearFile("backlog");
        }


        return sync.getPayload();
    }

    @Override
    protected void onPostExecute(String payloadData) {
        super.onPostExecute(payloadData);

        TaskManager taskManager = new TaskManager(_context);
        taskManager.importPayload(payloadData);

        // Checks for new sync key
        String newSyncKey = "";
        String[] splitData = payloadData.split("\n");
        for (String split : splitData) {
            if(Pattern.matches("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}", split)) {
                newSyncKey = split;
            }
        }

        // Stores new sync key if there was one
        if(!newSyncKey.equals("")) {
            SharedPreferences sharedPref = _context.getSharedPreferences("com.doctorandonuts.taskchampion.prefSync", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("syncKey", newSyncKey);
            editor.apply();
        }

        _taskListActivity.refreshTaskListFragment();

        // -1 because one is the sync key, and not a task
        int numberOfTasksDownloaded = splitData.length - 1;


        Toast.makeText(_context, numberOfTasksDownloaded + " tasks downloaded\n" + numberOfTasksUploaded + " tasks uploaded", Toast.LENGTH_SHORT).show();


        Log.d(TAG, "DONE");
    }
}
