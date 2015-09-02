package com.doctorandonuts.taskchampion.task;

import android.content.Context;
import android.content.SharedPreferences;
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

public class TaskManager {
    private String TAG = "TaskListFile";
    private static final String COMPLETED_FILENAME = "completed.data";
    private static final String PENDING_FILENAME = "pending.data";
    private static final String BACKLOG_FILENAME = "backlog.data";
    private Context _context;

    public TaskManager(Context context)
    {
        _context = context;
    }


    public List<Task> getPendingTasks() {
        List<Task> returnTaskList = new ArrayList<>();
        HashMap<String, Task> hashMapTaskList = readFile("pending");
        List<String> blockingUuid = new ArrayList<>();

        SharedPreferences sharedPref = _context.getSharedPreferences("com.doctorandonuts.taskchampion.prefSync", Context.MODE_PRIVATE);
        String context = sharedPref.getString("context", "");

        List<String> tagFilter = new ArrayList<>();
        tagFilter.clear();
        if(context.equals("home")) {
            tagFilter.add("home");
            tagFilter.add("computer");
        } else if(context.equals("work")) {
            tagFilter.add("work");
            tagFilter.add("computer");
        }

        for (Task task : hashMapTaskList.values()) {
            if (task.hasValue("depends")) {
                String[] depends = task.getValue("depends").split(",");
                for (String uuid : depends) {
                    if (hashMapTaskList.containsKey(uuid)) {
                        String dependsStatus = hashMapTaskList.get(uuid).getValue("status");
                        if (dependsStatus.equals("pending") || dependsStatus.equals("waiting") || dependsStatus.equals("recuring")) {
                            task.setBlocked(true);
                            blockingUuid.add(uuid);
                        }
                    }
                }
            }
        }

        for (String uuid : blockingUuid) {
            if( hashMapTaskList.containsKey(uuid)) {
                hashMapTaskList.get(uuid).setBlocking(true);
            }
        }


        for (Task task : hashMapTaskList.values()) {
            if(task.getValue("status").equals("pending") && !task.isBlocked()) {
                if(!tagFilter.isEmpty()) {
                    if (task.hasValue("tags")) {
                        try {
                            JSONArray tags = new JSONArray(task.getValue("tags"));
                            for (int i = 0; i < tags.length(); i++) {
                                if (tagFilter.contains(tags.get(i))) {
                                    task.calcUrgency();
                                    returnTaskList.add(task);
                                }
                            }
                        } catch (Exception e) {
                            Log.d(TAG, "ARG");
                        }
                    }
                } else {
                    task.calcUrgency();
                    returnTaskList.add(task);
                }
            }
        }

        return returnTaskList;
    }
    public void importPayload(String payloadData) {
        Log.d(TAG, "trying import");

        HashMap<String, Task> pendingTaskList = readFile("pending");
        HashMap<String, Task> completedTaskList = readFile("completed");

        String[] splitData = payloadData.split("\n");
        for (String aSplitData : splitData) {
            if (!Pattern.matches("[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}", aSplitData)) {
                try {
                    Task task = new Task(new JSONObject(aSplitData));
                    addOrUpdateTask(task, pendingTaskList, completedTaskList);
                } catch (Exception e) {
                    Log.d(TAG, "importPayload 1: " + e.toString());
                    Log.d(TAG, "importPayload 1: " + payloadData);
                }
            }
        }

        Log.d(TAG, "trying write");
        writeFile("pending", taskHashMapToString(pendingTaskList));
        writeFile("completed", taskHashMapToString(completedTaskList));
    }
    public void addOrUpdateTask(Task newTask) {
        HashMap<String, Task> pendingTaskList = readFile("pending");
        HashMap<String, Task> completedTaskList = readFile("completed");

        addOrUpdateTask(newTask, pendingTaskList, completedTaskList);
        appendBacklogFile(newTask.getJsonString() + "\n");

        writeFile("pending", taskHashMapToString(pendingTaskList));
        writeFile("completed", taskHashMapToString(completedTaskList));
    }
    public void clearFile(String file) {
        writeFile(file, "");
    }
    public List<Task> getBacklogData() {
        List<Task> returnTaskList = new ArrayList<>();

        try {
            File file = new File(_context.getFilesDir(), BACKLOG_FILENAME);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;

            while((line = bufferedReader.readLine()) != null) {
                returnTaskList.add(new Task(new JSONObject(line)));
            }
        } catch (Exception e ) {
            Log.d(TAG, "Problem reading " + BACKLOG_FILENAME + ": " + e.toString());
        }

        Log.d(TAG, "Done reading " + BACKLOG_FILENAME);
        return returnTaskList;
    }

    private void addOrUpdateTask(Task task, HashMap<String, Task> pendingTaskList, HashMap<String, Task> completedTaskList) {
        String taskUuid = task.getValue("uuid");


        Boolean isInPendingTaskList = pendingTaskList.containsKey(taskUuid);
        Boolean isInCompletedTaskList = completedTaskList.containsKey(taskUuid);

        if(!isInPendingTaskList && !isInCompletedTaskList) {
            if (goesInPendingFile(task)) {
                pendingTaskList.put(taskUuid, task);
            } else {
                completedTaskList.put(taskUuid, task);
            }
        } else {
            Task oldTask = null;

            if(isInPendingTaskList) {
                // Is in pending file
                oldTask = pendingTaskList.get(taskUuid);
            } else if(isInCompletedTaskList) {
                // Is in completed file
                oldTask = completedTaskList.get(taskUuid);
            }

            try {
                if (!oldTask.hasValue("modified")) {
                    if (goesInPendingFile(task)) {
                        pendingTaskList.put(taskUuid, task);
                    } else {
                        pendingTaskList.remove(taskUuid);
                        completedTaskList.put(taskUuid, task);
                    }
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'kkmmss'Z'");
                    Date oldTaskModified = sdf.parse(oldTask.getValue("modified"));
                    Date parseTaskModified = sdf.parse(task.getValue("modified"));
                    if (parseTaskModified.getTime() - oldTaskModified.getTime() > 0) {
                        if (goesInPendingFile(task)) {
                            completedTaskList.remove(taskUuid);
                            pendingTaskList.put(taskUuid, task);
                        } else {
                            pendingTaskList.remove(taskUuid);
                            completedTaskList.put(taskUuid, task);
                        }
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "addOrUpdateTask 1: " + e.toString());
            }
        }
    }
    private HashMap<String, Task> readFile(String taskFile) {
        HashMap<String, Task> taskHashMap = new HashMap<>();
        String fileName;
        switch(taskFile) {
            case "pending" :
                fileName = PENDING_FILENAME;
                break;
            case "completed":
                fileName = COMPLETED_FILENAME;
                break;
            default:
                Log.e(TAG, "No correct file provided");
                return null;
        }

        try {
            File file = new File(_context.getFilesDir(), fileName);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;

            while((line = bufferedReader.readLine()) != null) {
                Task task = new Task(new JSONObject(line));
                // I assume everything coming from a file is only 1 uuid per so I can just add them.
                if(task.getValue("status").equals("waiting")) {

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'kkmmss'Z'");
                    Date taskWaitDate = sdf.parse(task.getValue("wait"));
                    Date now = new Date();
                    if( now.getTime() - taskWaitDate.getTime() > 0) {
                        /*
                        TODO: Since I am changing this it might be good to write this change to
                        the pending file, but not the backlog since no real changes where made
                        */
                        task.setValue("status", "pending");
                    }
                }
                taskHashMap.put(task.getValue("uuid"), task);
            }
        } catch (Exception e ) {
            Log.d(TAG, "Problem reading " + fileName + ": " + e.toString());
        }

        Log.d(TAG, "Done reading " + fileName);
        return taskHashMap;
    }
    private void appendBacklogFile(String taskData) {
        try {
            FileOutputStream fileOutputStream = _context.openFileOutput(BACKLOG_FILENAME, Context.MODE_APPEND);
            fileOutputStream.write(taskData.getBytes());
            fileOutputStream.close();
            Log.d("TaskListFile", "Done Writing data to " + BACKLOG_FILENAME);
        } catch (Exception e) {
            Log.d("TaskListFile", "Problem writing data to : " + BACKLOG_FILENAME + e.toString());
        }
    }
    private void writeFile(String file, String taskPendingData) {
        String fileName;
        switch(file) {
            case "pending" :
                fileName = PENDING_FILENAME;
                break;
            case "completed":
                fileName = COMPLETED_FILENAME;
                break;
            case "backlog":
                fileName = BACKLOG_FILENAME;
                break;
            default:
                Log.e(TAG, "No correct file provided");
                return;
        }

        try {
            FileOutputStream fileOutputStream = _context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fileOutputStream.write(taskPendingData.getBytes());
            fileOutputStream.close();
            Log.d("TaskListFile", "Done Writing data to " + fileName);
        } catch (Exception e) {
            Log.d("TaskListFile", "Problem writing data to : " + fileName + e.toString());
        }
    }
    private String taskHashMapToString(HashMap<String, Task> taskHashMap) {
        String returnString = "";
        for (Task task : taskHashMap.values()) {
            returnString += task.getJsonString() + "\n";
        }

        return returnString;
    }

    private Boolean goesInPendingFile(Task task) {
        if (task.getValue("status").equals("pending") || task.getValue("status").equals("waiting") || task.getValue("status").equals("recurring")) {
            return true;
        } else {
            return false;
        }
    }

}
