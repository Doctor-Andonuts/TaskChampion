package com.doctorandonuts.taskchampion.task;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class Task {
    private JSONObject taskJson;
    private String TAG = "TaskClass";
    private Boolean blocking = false;
    private Boolean blocked = false;
    private Boolean overdue = false;

    private Float urgency;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'kkmmss'Z'", Locale.US);

    public Task(JSONObject taskJson) {
        this.taskJson = taskJson;
    }


    public void calcUrgency() {
        HashMap<String, Float> urgencyCoefficients = new HashMap<>();
        urgencyCoefficients.put("next", 15.0f);
        urgencyCoefficients.put("due", 12.0f);
        urgencyCoefficients.put("blocking", 8.0f);
        urgencyCoefficients.put("priority.H", 6.0f);
        urgencyCoefficients.put("priority.M", 3.9f);
        urgencyCoefficients.put("priority.L", 1.8f);
        urgencyCoefficients.put("scheduled", 5.0f);
        urgencyCoefficients.put("active", 4.0f);
        urgencyCoefficients.put("age", 2.0f);
        urgencyCoefficients.put("annotations", 1.0f);
        urgencyCoefficients.put("tags", 1.0f);
        urgencyCoefficients.put("project", 1.0f);
        urgencyCoefficients.put("blocked", -5.0f);
        urgencyCoefficients.put("waiting", -3.0f);
        urgencyCoefficients.put("uda.someday", -5.0f);

        urgency = 0.0f;
        urgency += urgency_next() * urgencyCoefficients.get("next");
        urgency += urgency_due() * urgencyCoefficients.get("due");
        urgency += urgency_priority_L() * urgencyCoefficients.get("priority.L");
        urgency += urgency_priority_M() * urgencyCoefficients.get("priority.M");
        urgency += urgency_priority_H() * urgencyCoefficients.get("priority.H");
        urgency += urgency_project() * urgencyCoefficients.get("project");
        urgency += urgency_tags() * urgencyCoefficients.get("tags");
        urgency += urgency_waiting() * urgencyCoefficients.get("waiting");
        urgency += urgency_annotations() * urgencyCoefficients.get("annotations");
        urgency += urgency_age() * urgencyCoefficients.get("age");
        urgency += urgency_active() * urgencyCoefficients.get("active");
        urgency += urgency_scheduled() * urgencyCoefficients.get("scheduled");
        urgency += urgency_blocked() * urgencyCoefficients.get("blocked");
        urgency += urgency_blocking() * urgencyCoefficients.get("blocking");
        urgency += urgency_uda_someday() * urgencyCoefficients.get("uda.someday");
    }

    public String getValue(String key) {
        String value = "";
        try {
            value = taskJson.getString(key);
        } catch (Exception e) {
            //Log.d(TAG, e.toString());
        }
        return value;
    }

    public String getFormatedValue(String key) {
        String value = "";
        try {
            value = taskJson.getString(key);
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

        String returnValue = "";
        switch(key) {
            case "tags":
                try {
                    JSONArray tags = new JSONArray(value);
                    for (int i = 0; i < tags.length(); i++) {
                        returnValue += tags.get(i) + ", ";
                    }
                    returnValue = returnValue.substring(0, returnValue.length()-2);

                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
                break;
            case "status":
                returnValue = value.substring(0, 1).toUpperCase() + value.substring(1);
                break;
            case "due":
            case "wait":
            case "entry":
            case "modified":
            case "end":
                try {
                    sdf.setTimeZone(TimeZone.getTimeZone("est"));
                    Date date = sdf.parse(value);

                    SimpleDateFormat sdfFormat = new SimpleDateFormat("MMMM d, yyyy K:mm a", Locale.US);

                    returnValue = sdfFormat.format(date);


                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }

                break;
            case "priority":
                switch(value) {
                    case "L":
                        returnValue = "Low";
                        break;
                    case "M":
                        returnValue = "Medium";
                        break;
                    case "H":
                        returnValue = "High";
                        break;
                }
                break;
            default:
                returnValue = getValue(key);
                break;
        }

        return returnValue;
    }
    public void setValue(String key, String value) {
        try {
            taskJson.put(key, value);
        } catch (Exception e) {
            Log.e(TAG, "setValue Error");
        }
    }
    public void setTags(JSONArray tags) {
        try {
            taskJson.put("tags", tags);
        } catch (Exception e) {
            Log.e(TAG, "setValue Error");
        }
    }

    public void done() {
        try {
            Date now = new Date();
            sdf.setTimeZone(TimeZone.getTimeZone("est"));

            setValue("end", sdf.format(now));
            setValue("modified", sdf.format(now));
            setValue("status", "completed");
        } catch (Exception e) {
            Log.e(TAG, "done Error");
        }
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
    private Float urgency_uda_someday() {
        if(hasTag("someday")) {
            return 1.0f;
        }
        return 0.0f;
    }
    private Float urgency_priority_L() {
        if(hasValue("priority")) {
            if(getValue("priority").equals("L")) {
                return 1.0f;
            }
        }
        return 0.0f;
    }
    private Float urgency_priority_M() {
        if(hasValue("priority")) {
            if(getValue("priority").equals("M")) {
                return 1.0f;
            }
        }
        return 0.0f;
    }
    private Float urgency_priority_H() {
        if(hasValue("priority")) {
            //Log.d(TAG, getValue("priority"));
            if(getValue("priority").equals("H")) {
                return 1.0f;
            }
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
        if(hasValue("due")) {
            try {
                Date taskDueDate =   sdf.parse(getValue("due"));   // initialize start date
                Date now = new Date();
                long duration  = now.getTime() - taskDueDate.getTime();
                long daysOverdue = TimeUnit.MILLISECONDS.toDays(duration);

                if(daysOverdue > 7.0f) {
                    return 1.0f;
                } // < 1 wk ago
                else if(daysOverdue > -14.0f) {
                    overdue = true;
                    return ((daysOverdue + 14.0f) * 0.8f / 21.0f) + 0.2f;
                } // > 2 wks
                else return 0.2f;

            } catch (Exception e) {
                Log.e(TAG, "urgency_due Error");
            }
        }
        return 0.0f;
    }
    private Float urgency_age() {
        Float maxAge = 365.0f;

        if(hasValue("entry")) {
            try {
                Date taskEntryDate =   sdf.parse(getValue("entry"));   // initialize start date
                Date now = new Date();
                long duration  = now.getTime() - taskEntryDate.getTime();
                long age = TimeUnit.MILLISECONDS.toDays(duration);

                if(age > maxAge) { return 1.0f; } // < 1 wk ago
                return (1.0f * age / maxAge);

            } catch (Exception e) {
                Log.e(TAG, "urgency_age Error");
            }
        }
        return 0.0f;
    }
    private Float urgency_scheduled() {
        if(hasValue("scheduled")) {
            try {
                Date taskEntryDate =   sdf.parse(getValue("scheduled"));   // initialize start date
                Date now = new Date();

                if(taskEntryDate.getTime() < now.getTime() ) { return 1.0f; } // < 1 wk ago

            } catch (Exception e) {
                Log.e(TAG, "urgency_scheduled Error");
            }
        }
        return 0.0f;
    }
    private Float urgency_project() {
        if (hasValue("project")) {
            return 1.0f;
        }
        return 0.0f;
    }
    private Float urgency_active() {
        if (hasValue("start")) {
            return 1.0f;
        }
        return 0.0f;
    }
    private Float urgency_waiting() {
        if (getValue("status").equals("waiting")) {
            return 1.0f;
        }
        return 0.0f;
    }
    private Float urgency_blocked() {
        if (isBlocked()) {
            return 1.0f;
        }
        return 0.0f;
    }
    private Float urgency_blocking() {
        if (isBlocking()) {
            return 1.0f;
        }
        return 0.0f;
    }
    private Float urgency_tags() {
        if (hasValue("tags")) {
            try {
                JSONArray tags = new JSONArray(getValue("tags"));
                switch(tags.length()) {
                    case 0: return 0.0f;
                    case 1: return 0.8f;
                    case 2: return 0.9f;
                    default: return 1.0f;
                }
            } catch (Exception e) {
                Log.e(TAG, "urgency_tags Error");
            }
        }
        return 0.0f;
    }
    private Float urgency_annotations() {
        if (hasValue("annotations")) {
            try {
                JSONArray annotations = new JSONArray(getValue("annotations"));
                switch(annotations.length()) {
                    case 0: return 0.0f;
                    case 1: return 0.8f;
                    case 2: return 0.9f;
                    default: return 1.0f;
                }
            } catch (Exception e) {
                Log.e(TAG, "urgency_annotations Error");
            }
        }
        return 0.0f;
    }


    private Boolean hasTag(String tag) {
        if(hasValue("tags")) {
            if (getValue("tags").contains(tag)) {
                return true;
            }
        }
        return false;
    }
    public Boolean hasValue(String value) {
        return taskJson.has(value);
    }


    public Boolean isBlocked() {
        return blocked;
    }

    public Boolean isBlocking() {
        return blocking;
    }

    public void setBlocked(@SuppressWarnings("SameParameterValue") Boolean blocked) {
        this.blocked = blocked;
    }

    public void setBlocking(@SuppressWarnings("SameParameterValue") Boolean blocking) {
        this.blocking = blocking;
    }

    public Boolean isOverDue() {
        return overdue;
    }
}
