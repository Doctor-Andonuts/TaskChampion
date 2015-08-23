package com.doctorandonuts.taskchampion.task;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class TaskList {
    private String TAG = "TaskListFile";
    private static final String FILENAME = "pending.data";
    private Context _context;
    private HashMap<String, Task> taskHashMap = new HashMap<>();

    public TaskList (Context context)
    {
        _context = context;
    }


    public List<Task> getTaskList(List<Task> taskList) {
        readPendingFile();
        taskList.clear();
        List<String> blockingUuid = new ArrayList<>();

        for ( Task task : taskHashMap.values() ) {
            if(task.getValue("status").equals("pending")) {
                taskList.add(task);
                if(task.hasValue("depends")) {
                    String[] depends = task.getValue("depends").split(",");
                    for (String uuid : depends) {
                        if(taskHashMap.containsKey(uuid)) {
                            String dependsStatus = taskHashMap.get(uuid).getValue("status");
                            if(dependsStatus.equals("pending") || dependsStatus.equals("waiting") || dependsStatus.equals("recuring")) {
                                task.setBlocked(true);
                                blockingUuid.add(uuid);
                            }
                        }
                    }
                }
            }
        }

        for (Task task : taskList) {
            if( blockingUuid.contains(task.getValue("uuid"))) {
                task.setBlocking(true);
            }
            task.calcUrgency();

        }

        return taskList;
    }


    private void readPendingFile() {
        taskHashMap.clear();
        try {
            File file = new File(_context.getFilesDir(), FILENAME);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;

            while((line = bufferedReader.readLine()) != null) {
                Task task = new Task(new JSONObject(line));
                taskHashMap.put(task.getValue("uuid"), task);
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
        for (String aSplitData : splitData) {
            if (!Pattern.matches("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}", aSplitData)) {
                try {
                    addTask(new Task(new JSONObject(aSplitData)));
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }
        }

        Log.d(TAG, "trying write");
        writePendingFile(arrayListToString());
    }


    private String arrayListToString() {
        String returnString = "";
        for (Task task : taskHashMap.values()) {
            returnString += task.getJsonString() + "\n";
        }

        return returnString;
    }


    private void addTask(Task taskToAdd) {
        String newUuid = taskToAdd.getValue("uuid");

        if(taskHashMap.containsKey(newUuid)) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'kkmmss'Z'");
                Date currentTaskDate = sdf.parse(taskHashMap.get(newUuid).getValue("modified"));
                Date newTaskDate = sdf.parse(taskToAdd.getValue("modified"));

                if(newTaskDate.after(currentTaskDate)) {
                    taskHashMap.put(newUuid, taskToAdd);
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
            }
        } else {
            taskHashMap.put(newUuid, taskToAdd);
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
