package com.doctorandonuts.taskchampion;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.doctorandonuts.taskchampion.task.TaskManager;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class TaskCreateFragment extends Fragment {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'kkmmss'Z'", Locale.US);

    public TaskCreateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        assert getActivity().getActionBar() != null;
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Create New Task");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View createFragment = inflater.inflate(R.layout.fragment_task_create, container, false);

        EditText description = (EditText) createFragment.findViewById(R.id.editDescription);

        TextView tagsTextView = (TextView) createFragment.findViewById(R.id.createTagsText);
        (createFragment.findViewById(R.id.createTagsLabel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTags(createFragment);
            }
        });
        tagsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTags(createFragment);
            }
        });

        description.requestFocus();
        showSoftKeyboard();

        return createFragment;
    }

    private void showSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager)  getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }



    private void editTags(final View view) {
        AlertDialog dialog;

        final CharSequence[] items = {"home","computer","errand","purchase","someday","next"};
        final ArrayList<Integer> selectedItems = new ArrayList<>();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select Tags");

        builder.setMultiChoiceItems(items, null,
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

                        TextView tagsTextView = (TextView) view.findViewById(R.id.createTagsText);
                        tagsTextView.setText(tagsJsonArray.toString());
//                        try {
//                            StringBuilder sb = new StringBuilder();
//                            for (int i = 0; i < tagsJsonArray.length(); i++) {
//                                sb.append(tagsJsonArray.get(i));
//                                if(i < tagsJsonArray.length() - 1) {
//                                    sb.append(", ");
//                                }
//                            }
//                            tagsTextView.setText(sb);
//                        } catch (Exception e) {
//
//                        }
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