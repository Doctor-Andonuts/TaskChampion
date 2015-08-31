package com.doctorandonuts.taskchampion;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.doctorandonuts.taskchampion.sync.TaskWarriorSync;
import com.doctorandonuts.taskchampion.task.CustomArrayAdapter;
import com.doctorandonuts.taskchampion.task.Task;
import com.doctorandonuts.taskchampion.task.TaskManager;

import org.w3c.dom.Text;


public class TaskDetailsFragment extends Fragment {
    private Task task;

//    private OnFragmentInteractionListener mListener;

    // TODO: Rename and change types and number of parameters
    public static TaskDetailsFragment newInstance(String param1, String param2) {
        TaskDetailsFragment fragment = new TaskDetailsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public TaskDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
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
        ((TextView) view.findViewById(R.id.descriptionText)).setText(task.getValue("description"));
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
}
