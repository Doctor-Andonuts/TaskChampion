package com.doctorandonuts.taskchampion;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        loadView(view);
        super.onViewCreated(view, savedInstanceState);
    }

    private void loadView(View view) {
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


    private void editDescription() {
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

    private void editTags() {
        AlertDialog dialog;

        final CharSequence[] items = {"home","computer","errand","purchase","someday","next"};
        final ArrayList<Integer> selectedItems = new ArrayList();

        String currentTaskTagsString = task.getValue("tags");
        boolean[] tagsSelected = new boolean[6];
        if(currentTaskTagsString.contains("home")) {
            tagsSelected[0] = true;
            selectedItems.add(0);
        } else { tagsSelected[0] = false; }
        if(currentTaskTagsString.contains("computer")) {
            tagsSelected[1] = true;
            selectedItems.add(1);
        } else { tagsSelected[1] = false; }
        if(currentTaskTagsString.contains("errand")) {
            tagsSelected[2] = true;
            selectedItems.add(2);
        } else { tagsSelected[2] = false; }
        if(currentTaskTagsString.contains("purchase")) {
            tagsSelected[3] = true;
            selectedItems.add(3);
        } else { tagsSelected[3] = false; }
        if(currentTaskTagsString.contains("someday")) {
            tagsSelected[4] = true;
            selectedItems.add(4);
        } else { tagsSelected[4] = false; }
        if(currentTaskTagsString.contains("next")) {
            tagsSelected[5] = true;
            selectedItems.add(5);
        } else { tagsSelected[5] = false; }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Tags");

        builder.setMultiChoiceItems(items, tagsSelected,
                new DialogInterface.OnMultiChoiceClickListener() {
                    // indexSelected contains the index of item (of which checkbox checked)
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            selectedItems.add(indexSelected);
                        } else if (selectedItems.contains(indexSelected)) {
                            // Else, if the item is already in the array, remove it
                            selectedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                })
                // Set the action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {//selectedItems.get(selectedItems.indexOf(0));
                        ArrayList tagsArrayList = new ArrayList();
                        for (Integer selectedItem : selectedItems) {
                            tagsArrayList.add(items[selectedItem]);
                        }
                        JSONArray tagsJsonArray = new JSONArray(tagsArrayList);

                        task.setValue("tags", tagsJsonArray.toString());
                        TaskManager taskManager = new TaskManager(getActivity());
                        taskManager.addOrUpdateTask(task);

                        loadView(getView());
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });

        dialog = builder.create();//AlertDialog dialog; create like this outside onClick
        dialog.show();
    }
}
