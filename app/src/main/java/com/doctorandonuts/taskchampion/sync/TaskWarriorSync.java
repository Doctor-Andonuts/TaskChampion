package com.doctorandonuts.taskchampion.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Mr Saturn on 8/7/2015 for TaskChampion
 */
public class TaskWarriorSync extends AsyncTask<Void, Void, String> {

    private String TAG = "TaskWarriorSync";
    private Context _context;
    public TaskWarriorSync (Context context) {
        _context = context;
    }

    private Cert cert = new Cert();

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        SharedPreferences sharedPref = _context.getSharedPreferences("com.doctorandonuts.taskchampion.prefSync", Context.MODE_PRIVATE);
        String syncKey = sharedPref.getString("syncKey", "");

        final Msg sync = new Msg();
        sync.setHeader("protocol", "v1");
        sync.setHeader("type", "sync");
        sync.setHeader("org", "Main");
        sync.setHeader("user", "Doctor Andonuts");
        sync.setHeader("key", "cf5a3fa3-5508-4e28-9497-5b44113d45a8");
        final StringBuilder payload = new StringBuilder();
        sync.setPayload(payload.toString());

        return sync.serialize();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.d(TAG, s);
        Log.d(TAG, "DONE");
        Toast.makeText(_context, s, Toast.LENGTH_SHORT).show();
    }
}
