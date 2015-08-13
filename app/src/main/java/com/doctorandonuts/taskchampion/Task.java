package com.doctorandonuts.taskchampion;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by jgowing on 8/13/2015.
 */
public class Task {
    private String description;
    private String uuid;
    private String TAG = "TaskClass";

    public Task(JSONObject taskJson) {
        try {
            this.description = taskJson.getString("description");
            this.uuid = taskJson.getString("uuid");
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    public String getDescription() {
        return description;
    }

    public String getUuid() {
        return uuid;
    }
}
