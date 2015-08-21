package com.doctorandonuts.taskchampion.task;

import android.util.Log;

import org.json.JSONObject;

public class Task {
    private JSONObject taskJson;
    private String TAG = "TaskClass";

    public Task(JSONObject taskJson) {
        this.taskJson = taskJson;
    }

    public String getValue(String key) {
        String value = "";
        try {
            value = taskJson.getString(key);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        return value;
    }

    public String getJsonString() {
        return taskJson.toString();
    }
}
