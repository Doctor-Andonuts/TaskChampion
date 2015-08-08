package com.doctorandonuts.taskchampion.sync;

import android.os.AsyncTask;

/**
 * Created by Mr Saturn on 8/7/2015 for TaskChampion
 */
public class TaskWarriorSync extends AsyncTask<Void, Void, String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... params) {
        return "ASYNC";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
