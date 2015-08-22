package com.doctorandonuts.taskchampion.task;

import android.util.Log;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Task {
    private JSONObject taskJson;
    private String TAG = "TaskClass";
    private HashMap<String, Float> urgencyCoefficients = new HashMap<>();
    private Float urgency;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'kkmmss'Z'");

    public Task(JSONObject taskJson) {
        this.taskJson = taskJson;
        this.urgencyCoefficients.put("next", 15.0f); //DONE
        this.urgencyCoefficients.put("due", 12.0f); //DONE
        this.urgencyCoefficients.put("blocking", 8.0f);
        this.urgencyCoefficients.put("priority.H", 6.0f);
        this.urgencyCoefficients.put("priority.M", 3.9f);
        this.urgencyCoefficients.put("priority.L", 1.8f);
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
        urgency += urgency_next() * urgencyCoefficients.get("next");
        urgency += urgency_due() * urgencyCoefficients.get("due");


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

    private Float urgency_next() {
        if(hasTag("next")) {
            return 1.0f;
        }
        return 0.0f;
    }

    ////////////////////////////////////////////////////////////////////////////////
    //     Past                  Present                              Future
    //     Overdue               Due                                     Due
    //     -7 -6 -5 -4 -3 -2 -1  0  1  2  3  4  5  6  7  8  9 10 11 12 13 14 days
    // <-- 1.0                         linear                            0.2 -->
    //     capped                                                        capped
    private Float urgency_due() {
        if(taskJson.has("due")) {
            try {
                Date taskDate =   sdf.parse(getValue("due"));   // initialize start date
                Date now = new Date();
                long duration  = now.getTime() - taskDate.getTime();
                long daysOverdue = TimeUnit.MILLISECONDS.toDays(duration);

                if(daysOverdue > 7.0f) { return 1.0f; } // < 1 wk ago
                else if(daysOverdue > -14.0f) { return ((daysOverdue + 14.0f) * 0.8f / 21.0f) + 0.2f; } // > 2 wks
                else return 0.2f;

            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        }
        return 0.0f;
    }


    private Boolean hasTag(String tag) {
        if(getValue("tags").contains(tag)) {
            return true;
        }
        return false;
    }
}
