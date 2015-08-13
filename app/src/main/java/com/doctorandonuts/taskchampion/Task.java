package com.doctorandonuts.taskchampion;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by jgowing on 8/13/2015.
 */
public class Task {
    private JSONObject taskJson;
    private String TAG = "TaskClass";

    public Task(JSONObject taskJson) {
        this.taskJson = taskJson;
    }

    public String getValue(String key) {
        String value = "ERROR";
        try {
            value = taskJson.getString(key);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        return value;
    }
}
