package com.doctorandonuts.taskchampion;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
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

    private void readPendingFile() {
        writePendingFile("");

        try {
            File file = new File(_context.getFilesDir(), FILENAME);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;

            while((line = bufferedReader.readLine()) != null) {
                Log.d("TaskListFile", line);
                JSONObject jsonLine = new JSONObject(line);
                _pending.add(jsonLine);
            }
        } catch (Exception e ) {
            Log.d(TAG, "Problem reading: " + e.toString());
        }

        Log.d(TAG, "done reading");
    }

    public void importPayload(String payloadData) {
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

    }


    private void addTask(JSONObject taskToAdd) {
        Boolean taskFound = false;
        for( JSONObject pendingTask : _pending) {
            try {
                if(pendingTask.getString("uuid").equals(taskToAdd.getString("uuid"))) {
                    Log.d(TAG, "Pending: " + pendingTask.getString("modified"));
                    Log.d(TAG, "New: " + taskToAdd.getString("modified"));
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
