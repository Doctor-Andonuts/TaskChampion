package com.doctorandonuts.taskchampion.task;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.doctorandonuts.taskchampion.R;
import com.doctorandonuts.taskchampion.task.Task;

import java.util.List;

public class CustomArrayAdapter extends ArrayAdapter<Task> {
    private final Context context;
    private final List<Task> tasks;

    public CustomArrayAdapter(Context context, List<Task> tasks) {
        super(context, -1, tasks);
        this.context = context;
        this.tasks = tasks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.task_list_item, parent, false);
        TextView firstLine_textView = (TextView) rowView.findViewById(R.id.firstLine);
        firstLine_textView.setText(tasks.get(position).getValue("description"));

        return rowView;
    }


}
