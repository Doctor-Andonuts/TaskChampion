package com.doctorandonuts.taskchampion.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

/**
 * Created by Mr Saturn on 8/7/2015 for TaskChampion
 */
public class TaskWarriorSync extends AsyncTask<Context, Void, String> {

    Cert cert = new Cert();

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Context... params) {
        Context context = params[0];
        SharedPreferences sharedPref = context.getSharedPreferences("com.doctorandonuts.taskchampion.prefSync", Context.MODE_PRIVATE);
        String syncKey = sharedPref.getString("syncKey", "");


        return syncKey;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
