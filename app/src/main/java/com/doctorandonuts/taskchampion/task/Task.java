package com.doctorandonuts.taskchampion.task;

import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;

public class Task {
    private JSONObject taskJson;
    private String TAG = "TaskClass";
    private HashMap<String, Float> urgencyCoefficients = new HashMap<>();
    private Float urgency;

    public Task(JSONObject taskJson) {
        this.taskJson = taskJson;
        this.urgencyCoefficients.put("next", 15.0f); //DONE
        this.urgencyCoefficients.put("due", 12.0f);
        this.urgencyCoefficients.put("blocking", 8.0f);
        this.urgencyCoefficients.put("priority", 6.0f);
        this.urgencyCoefficients.put("active", 4.0f);
        this.urgencyCoefficients.put("scheduled", 4.0f);
        this.urgencyCoefficients.put("age", 2.0f);
        this.urgencyCoefficients.put("annotations", 1.0f);
        this.urgencyCoefficients.put("tags", 1.0f);
        this.urgencyCoefficients.put("project", 1.0f);
        this.urgencyCoefficients.put("blocked", 5.0f);
        this.urgencyCoefficients.put("waiting", -3.0f);
        this.urgencyCoefficients.put("user.project", 5.0f);
        this.urgencyCoefficients.put("user.tag", 5.0f);

        urgency = 0.0f;
        if(getValue("tags").contains("next")) {
            urgency += urgencyCoefficients.get("next");
        }


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

    public Float getUrgency() {
        return urgency;
    }
}
