package com.doctorandonuts.taskchampion;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;

import android.util.Log;

/**
 * Created by jgowing on 8/10/2015.
 */
public class TaskList {
    private static final String FILENAME = "pending.data";
    private Context _context;

    public TaskList (Context context)
    {
        _context = context;
    }

    public void readPendingFile() {
        try {
            File file = new File(_context.getFilesDir(), FILENAME);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while((line = bufferedReader.readLine()) != null) {
                Log.d("TaskListFile", line);
            }

            Log.d("TaskListFile", "done reading");
        }
        catch(Exception e ) {
            Log.d("TaskListFile", "Problem reading: " + e.toString());
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
