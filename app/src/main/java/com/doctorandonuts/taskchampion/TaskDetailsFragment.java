package com.doctorandonuts.taskchampion;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
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

        ((TextView) view.findViewById(R.id.statusText)).setText(task.getValue("status"));
        ((TextView) view.findViewById(R.id.entryText)).setText(task.getValue("entry"));
        ((TextView) view.findViewById(R.id.projectText)).setText(task.getValue("project"));
        ((TextView) view.findViewById(R.id.dueText)).setText(task.getValue("due"));
        ((TextView) view.findViewById(R.id.tagsText)).setText(task.getValue("tags"));
        ((TextView) view.findViewById(R.id.endText)).setText(task.getValue("end"));
        ((TextView) view.findViewById(R.id.waitText)).setText(task.getValue("wait"));
        ((TextView) view.findViewById(R.id.annotationText)).setText(task.getValue("annotation"));
        ((TextView) view.findViewById(R.id.priorityText)).setText(task.getValue("priority"));
        ((TextView) view.findViewById(R.id.dependsText)).setText(task.getValue("depends"));
        ((TextView) view.findViewById(R.id.modifiedText)).setText(task.getValue("modified"));
        ((TextView) view.findViewById(R.id.uuidText)).setText(task.getValue("uuid"));
        ((TextView) view.findViewById(R.id.isBlockedText)).setText(task.isBlocked().toString());
        ((TextView) view.findViewById(R.id.isBlockingText)).setText(task.isBlocking().toString());
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



//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p/>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        public void onFragmentInteraction();
//    }

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
}
