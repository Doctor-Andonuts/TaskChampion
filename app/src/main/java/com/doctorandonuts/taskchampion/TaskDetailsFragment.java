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
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TaskDetailsFragment extends Fragment {
    private Task task;
    private TextView descriptionTextView;
//    TaskManager taskManager = new TaskManager(getActivity());

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
        refreshDetailView(view);
        super.onViewCreated(view, savedInstanceState);
    }

    private void refreshDetailView(View view) {
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


        TextView priorityTextView = (TextView) view.findViewById(R.id.priorityText);
        (view.findViewById(R.id.priorityLabel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPriority();
            }
        });
        priorityTextView.setText(task.getFormatedValue("priority"));
        priorityTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPriority();
            }
        });


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
            TaskManager taskManager = new TaskManager(getActivity());

            if(task.hasValue("relativeRecurDue") || task.hasValue("relativeRecurWait")) {
                // I need to copy the old task to new, newTask = task does not work because it points to the same object in memory (copies the pointer)
                Task newRecurTask = null;
                try {
                    newRecurTask = new Task(new JSONObject(task.getJsonString()));
                    newRecurTask.setValue("uuid", UUID.randomUUID().toString());
                } catch (Exception e) {
                    Log.e("JSON", "PROBLEMS");
                }

                if(newRecurTask.hasValue("relativeRecurDue")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'kkmmss'Z'", Locale.US);
                    sdf.setTimeZone(TimeZone.getTimeZone("est"));
                    Calendar calendar = durationToCalendarTime(newRecurTask.getValue("relativeRecurDue"));
                    newRecurTask.setValue("due", sdf.format(calendar.getTime()));
                }
                if(newRecurTask.hasValue("relativeRecurWait")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'kkmmss'Z'", Locale.US);
                    sdf.setTimeZone(TimeZone.getTimeZone("est"));
                    Calendar calendar = durationToCalendarTime(newRecurTask.getValue("relativeRecurWait"));
                    newRecurTask.setValue("wait", sdf.format(calendar.getTime()));
                }
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'kkmmss'Z'", Locale.US);
                Date now = new Date();
                sdf.setTimeZone(TimeZone.getTimeZone("est"));

                newRecurTask.setValue("modified", sdf.format(now));
                newRecurTask.setValue("entry", sdf.format(now));

                taskManager.addOrUpdateTask(newRecurTask);
            }

            task.done();
            taskManager.addOrUpdateTask(task);

            getFragmentManager().popBackStack();
            Toast.makeText(getActivity(), "Marked done",Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_delete) {
            TaskManager taskManager = new TaskManager(getActivity());
            task.delete();
            taskManager.addOrUpdateTask(task);

            getFragmentManager().popBackStack();
            Toast.makeText(getActivity(), "Marked deleted",Toast.LENGTH_SHORT).show();
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

    private void editPriority() {
        AlertDialog levelDialog;

        // Strings to Show In Dialog with Radio Buttons
        final CharSequence[] items = {"High","Medium","Low","None"};

        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select The Priority");
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                switch (item) {
                    case 0:
                        task.setValue("priority", "H");
                        break;
                    case 1:
                        task.setValue("priority", "M");
                        break;
                    case 2:
                        task.setValue("priority", "L");
                        break;
                    case 3:
                        task.setValue("priority", "");
                        break;
                }
                dialog.dismiss();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'kkmmss'Z'", Locale.US);
                Date now = new Date();
                sdf.setTimeZone(TimeZone.getTimeZone("est"));
                task.setValue("modified", sdf.format(now));

                TaskManager taskManager = new TaskManager(getActivity());
                taskManager.addOrUpdateTask(task);
                refreshDetailView(getView());
            }
        });
        levelDialog = builder.create();
        levelDialog.show();
    }

    private void editTags() {
        AlertDialog dialog;

        final CharSequence[] items = {"home","computer","errand","purchase","someday","next"};
        final ArrayList<Integer> selectedItems = new ArrayList<>();

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
                    public void onClick(DialogInterface dialog, int id) {
                        ArrayList<CharSequence> tagsArrayList = new ArrayList<>();
                        for (Integer selectedItem : selectedItems) {
                            tagsArrayList.add(items[selectedItem]);
                        }
                        JSONArray tagsJsonArray = new JSONArray(tagsArrayList);

                        task.setTags(tagsJsonArray);

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'kkmmss'Z'", Locale.US);
                        Date now = new Date();
                        sdf.setTimeZone(TimeZone.getTimeZone("est"));

                        task.setValue("modified", sdf.format(now));

                        TaskManager taskManager = new TaskManager(getActivity());
                        taskManager.addOrUpdateTask(task);

                        refreshDetailView(getView());
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


    private Calendar durationToCalendarTime(String duration) {
        Calendar calendar = Calendar.getInstance();

        Pattern pattern = Pattern.compile("(\\d*) ?([a-zA-Z]+)");
        Matcher matcher = pattern.matcher(duration);
        if(matcher.find()) {
            String numberString = matcher.group(1);
            String text = matcher.group(2);

            if(numberString.equals("")) {
                switch (text) {
                    case "second":
                    case "sec":
                        // 1 second
                        calendar.add(Calendar.SECOND, 1);
                        break;
                    case "minutes":
                    case "min":
                        // 1 minute
                        calendar.add(Calendar.MINUTE, 1);
                        break;
                    case "hour":
                    case "hr":
                        // 1 hour
                        calendar.add(Calendar.HOUR, 1);
                        break;
                    case "daily":
                    case "day":
                        // 1 day
                        calendar.add(Calendar.DATE, 1);
                        break;
                    case "weekyl":
                    case "week":
                    case "wk":
                        // 1 week (7 days)
                        calendar.add(Calendar.DATE, 7);
                        break;
                    case "weekdays":
                        // 1 every weekday Monday to Friday
                        // TODO: What?!?!
                        break;
                    case "biweekly":
                    case "fortnight":
                    case "sennight":
                        // 14 days
                        calendar.add(Calendar.DATE, 14);
                        break;
                    case "monthly":
                    case "month":
                    case "mth":
                    case "mo":
                        // 30 days / 1 month (imprecise)
                        calendar.add(Calendar.MONTH, 1);
                        break;
                    case "bimonthly":
                        // 61 days / 2 months (imprecise)
                        calendar.add(Calendar.MONTH, 2);
                        break;
                    case "quarterly":
                    case "quarter":
                    case "qrtr":
                    case "q":
                        // 91 days / 3 months (imprecise)
                        calendar.add(Calendar.MONTH, 3);
                        break;
                    case "semiannual":
                        // 183 days / 6 months (imprecise)
                        calendar.add(Calendar.MONTH, 6);
                        break;
                    case "annual":
                    case "yearly":
                    case "year":
                    case "yr":
                        // 365 days / 1 year (imprecise)
                        calendar.add(Calendar.YEAR, 1);
                        break;
                    case "biannual":
                    case "biyearly":
                        // 730 days / 2 year (imprecise)
                        calendar.add(Calendar.YEAR, 2);
                        break;
                }
            } else {
                int number = Integer.parseInt(numberString);
                switch (text) {
                    case "seconds":
                    case "second":
                    case "secs":
                    case "sec":
                    case "s":
                        // seconds
                        calendar.add(Calendar.SECOND, number);
                        break;
                    case "minutes":
                    case "minute":
                    case "mins":
                    case "min":
                        // minutes
                        calendar.add(Calendar.MINUTE, number);
                        break;
                    case "hours":
                    case "hour":
                    case "hrs":
                    case "h":
                        // minutes
                        calendar.add(Calendar.HOUR, number);
                        break;
                    case "days":
                    case "day":
                    case "d":
                        // days
                        calendar.add(Calendar.DATE, number);
                        break;
                    case "weeks":
                    case "week":
                    case "wks":
                    case "wk":
                    case "w":
                        // weeks / 7 days
                        calendar.add(Calendar.DATE, number * 7);
                        break;
                    case "fortnight":
                    case "sennight":
                        // 14 days
                        calendar.add(Calendar.DATE, number * 14);
                        break;
                    case "months":
                    case "month":
                    case "mnths":
                    case "mths":
                    case "mth":
                    case "mo":
                    case "m":
                        // 30 days / 1 month (imprecise)
                        calendar.add(Calendar.MONTH, number);
                        break;
                    case "quarterly":
                    case "quarters":
                    case "quarter":
                    case "qrtrs":
                    case "qrtr":
                    case "qtr":
                    case "q":
                        // 91 days / 3 months (imprecise)
                        calendar.add(Calendar.MONTH, number * 3);
                        break;
                    case "years":
                    case "year":
                    case "yrs":
                    case "yr":
                    case "y":
                        // 365 days / 1 year (imprecise)
                        calendar.add(Calendar.YEAR, number);
                        break;
                }
            }
        }
        return calendar;
    }
}
