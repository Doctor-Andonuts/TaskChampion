package com.doctorandonuts.taskchampion;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.doctorandonuts.taskchampion.task.Task;
import com.doctorandonuts.taskchampion.task.TaskManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class TaskDetailsFragment extends Fragment {
    private Task task;
    private TextView descriptionTextView;

    public TaskDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        assert getActivity().getActionBar() != null;
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Details");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_details, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        descriptionTextView = (TextView) view.findViewById(R.id.descriptionText);
        descriptionTextView.setText(task.getValue("description"));
        descriptionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDescription();
            }
        });

        ((TextView) view.findViewById(R.id.statusText)).setText(task.getFormatedValue("status"));


        ((TextView) view.findViewById(R.id.projectText)).setText(task.getFormatedValue("project"));


        ((TextView) view.findViewById(R.id.dueText)).setText(task.getFormatedValue("due"));


        TextView tagsTextView = (TextView) view.findViewById(R.id.tagsText);
        (view.findViewById(R.id.tagsLabel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTags();
            }
        });
        tagsTextView.setText(task.getFormatedValue("tags"));
        tagsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTags();
            }
        });


        ((TextView) view.findViewById(R.id.waitText)).setText(task.getFormatedValue("wait"));
        ((TextView) view.findViewById(R.id.annotationText)).setText(task.getFormatedValue("annotation"));
        ((TextView) view.findViewById(R.id.priorityText)).setText(task.getFormatedValue("priority"));
        ((TextView) view.findViewById(R.id.dependsText)).setText(task.getFormatedValue("depends"));


        ((TextView) view.findViewById(R.id.entryText)).setText(task.getFormatedValue("entry"));
        ((TextView) view.findViewById(R.id.modifiedText)).setText(task.getFormatedValue("modified"));
        ((TextView) view.findViewById(R.id.endText)).setText(task.getFormatedValue("end"));
        ((TextView) view.findViewById(R.id.isBlockedText)).setText(task.isBlocked().toString());
        ((TextView) view.findViewById(R.id.isBlockingText)).setText(task.isBlocking().toString());
        ((TextView) view.findViewById(R.id.uuidText)).setText(task.getFormatedValue("uuid"));


        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_task_details, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {
            task.done();
            TaskManager taskManager = new TaskManager(getActivity());
            taskManager.addOrUpdateTask(task);
            getFragmentManager().popBackStack();
            Toast.makeText(getActivity(), "Marked done",Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setTask(Task task) {
        this.task = task;
    }


    public void editDescription() {
        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edit Descrption");
        builder.setCancelable(true);
        final EditText descriptionInput = new EditText(getActivity());
        descriptionInput.setText(descriptionTextView.getText());
        builder.setView(descriptionInput);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'kkmmss'Z'", Locale.US);
                Date now = new Date();
                sdf.setTimeZone(TimeZone.getTimeZone("est"));

                task.setValue("modified", sdf.format(now));
                task.setValue("description", descriptionInput.getText().toString());
                descriptionTextView.setText(descriptionInput.getText());

                TaskManager taskManager = new TaskManager(getActivity());
                taskManager.addOrUpdateTask(task);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void editTags() {
        Toast.makeText(getActivity(), "THIS DOES NOTHING", Toast.LENGTH_SHORT).show();
    }


}
