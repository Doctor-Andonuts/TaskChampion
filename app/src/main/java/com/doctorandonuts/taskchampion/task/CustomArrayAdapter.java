package com.doctorandonuts.taskchampion.task;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.doctorandonuts.taskchampion.R;

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
        TextView descriptionTextView = (TextView) rowView.findViewById(R.id.description);
        descriptionTextView.setText(tasks.get(position).getValue("description"));
        TextView urgencyTextView = (TextView) rowView.findViewById(R.id.urgency);
        String urgencyString = String.format("%.2f", tasks.get(position).getUrgency());
        urgencyTextView.setText(urgencyString);


        if(tasks.get(position).getValue("tags").contains("someday")) {
            rowView.setBackgroundColor(0xff01579b);
            descriptionTextView.setTextColor(0xffffffff);
            urgencyTextView.setTextColor(0xffdddddd);
        }
        if(tasks.get(position).isOverDue()) {
            rowView.setBackgroundColor(0xffc62828);
            descriptionTextView.setTextColor(0xffffffff);
            urgencyTextView.setTextColor(0xffdddddd);
        }


        return rowView;
    }


}
