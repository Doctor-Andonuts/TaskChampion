package com.doctorandonuts.taskchampion;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by jgowing on 8/10/2015.
 */
public class TaskList {
    private String TAG = "TaskListFile";
    private static final String FILENAME = "pending.data";
    private Context _context;
    private ArrayList<JSONObject> _pending = new ArrayList<>();

    public TaskList (Context context)
    {
        _context = context;
    }


    public List<String> getDescriptionList(List<String> descriptionList) {
        readPendingFile();
        descriptionList.clear();

        for ( JSONObject taskJson : _pending ) {
            try {
                descriptionList.add(taskJson.getString("description"));
            } catch (Exception e) {
                Log.d(TAG, "getDescription: " + e.toString());
            }
        }

        return descriptionList;
    }


    private void readPendingFile() {
        try {
            File file = new File(_context.getFilesDir(), FILENAME);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;

            while((line = bufferedReader.readLine()) != null) {
                //Log.d("TaskListFile", line);
                JSONObject jsonLine = new JSONObject(line);
                _pending.add(jsonLine);
            }
        } catch (Exception e ) {
            Log.d(TAG, "Problem reading: " + e.toString());
        }

        Log.d(TAG, "done reading");
    }

    public void importPayload(String payloadData) {
        Log.d(TAG, "trying import");
        readPendingFile();

        String[] splitData = payloadData.split("\n");
        for(Integer i=0; i<splitData.length; i++) {
            if (!Pattern.matches("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}", splitData[i])) {
                try {
                    JSONObject line = new JSONObject(splitData[i]);
                    addTask(line);
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }
        }

        Log.d(TAG, "trying write");
        writePendingFile(arraylistToString(_pending));
    }

    private String arraylistToString(ArrayList<JSONObject> arrayList) {
        String returnString = "";
        for (JSONObject listItem : arrayList) {
            returnString += listItem.toString() + "\n";
        }

        return returnString;
    }


    private void addTask(JSONObject taskToAdd) {
        Boolean taskFound = false;
        for(Integer i=0; i<_pending.size(); i++) {
            try {
                if(_pending.get(i).getString("uuid").equals(taskToAdd.getString("uuid"))) {
                    taskFound = true;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'kkmmss'Z'");
                    Date currentTaskDate = sdf.parse(_pending.get(i).getString("modified"));
                    Date newTaskDate = sdf.parse(taskToAdd.getString("modified"));

                    //Log.d(TAG, "Pending: " + _pending.get(i).getString("modified"));
                    //Log.d(TAG, "New: " + taskToAdd.getString("modified"));

                    if(newTaskDate.after(currentTaskDate)) {
                        _pending.set(i, taskToAdd);
                    }

                    //Log.d(TAG, "----");
                }
            } catch(Exception e) {
                Log.d(TAG, e.toString());
            }
        }
        if(!taskFound) {
            _pending.add(taskToAdd);
        }
    }


    public void writePendingFile(String taskPendingData) {
        try {
            FileOutputStream fileOutputStream = _context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fileOutputStream.write(taskPendingData.getBytes());
            fileOutputStream.close();
            Log.d("TaskListFile", "Writing done");
        } catch (Exception e) {
            Log.d("TaskListFile", "Problem writing: " + e.toString());
        }
    }

}
