package com.doctorandonuts.taskchampion.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.regex.Pattern;

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
    protected String doInBackground(Void... params) {
        SharedPreferences sharedPref = _context.getSharedPreferences("com.doctorandonuts.taskchampion.prefSync", Context.MODE_PRIVATE);

        // This sets the sync key to nothing to make sure I can keep testing
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("syncKey", "");
        editor.commit();


        String syncKey = sharedPref.getString("syncKey", "");
        Log.d(TAG, "Getting sync key: " + syncKey);

        final Msg sync = new Msg();
        sync.clear();
        final StringBuilder payload = new StringBuilder();
        payload.append(syncKey);
        sync.setPayload(payload.toString());
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

        tlsClient.send(sync.serialize());
        final String response = tlsClient.recv();
        tlsClient.close();

        try {
            sync.parse(response);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        return sync.getPayload();
    }

    @Override
    protected void onPostExecute(String payloadData) {
        super.onPostExecute(payloadData);

        // Checks for new sync key
        String newSyncKey = "";
        String[] splitData = payloadData.split("\n");
        for(Integer i=0; i<splitData.length; i++) {
            if(Pattern.matches("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}", splitData[i])) {
                newSyncKey = splitData[i];
                Log.d(TAG, "NEW SYNC KEY #" + i + ": " + splitData[i]);
            } else {
                Log.d(TAG, "#" + i + ": " + splitData[i]);
            }
        }

        // Stores new sync key if there was one
        if(!newSyncKey.equals("")) {
            SharedPreferences sharedPref = _context.getSharedPreferences("com.doctorandonuts.taskchampion.prefSync", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("syncKey", newSyncKey);
            editor.commit();
        }



        Log.d(TAG, "DONE");
    }
}
