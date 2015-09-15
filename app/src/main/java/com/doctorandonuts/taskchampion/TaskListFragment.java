package com.doctorandonuts.taskchampion;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.doctorandonuts.taskchampion.task.CustomArrayAdapter;
import com.doctorandonuts.taskchampion.task.Task;
import com.doctorandonuts.taskchampion.task.TaskManager;

import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends ListFragment {
    private List<Task> tasks = new ArrayList<>();
    private OnFragmentInteractionListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_list, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        setHasOptionsMenu(true);
        assert getActivity().getActionBar() != null;
        ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle("TaskChampion");

        menu.clear();
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.menu_task_list, menu);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        refreshData();

        CustomArrayAdapter arrayAdapter = new CustomArrayAdapter(getActivity(), tasks);
        TaskComparator taskComparator = new TaskComparator();
        arrayAdapter.sort(taskComparator);
        arrayAdapter.notifyDataSetChanged();
        setListAdapter(arrayAdapter);

        SharedPreferences sharedPref = getActivity().getSharedPreferences("com.doctorandonuts.taskchampion.prefSync", Context.MODE_PRIVATE);
        String context = sharedPref.getString("context", "none");
        String contextString;
        switch(context) {
            case "none":
                contextString = "None";
                break;
            case "home":
                contextString = "Home";
                break;
            case "work":
                contextString = "Work";
                break;
            default:
                contextString = "None";
                break;
        }
        TextView contextTextView = (TextView) getActivity().findViewById(R.id.contextTextView);
        contextTextView.setText("Context: " + contextString);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("FragmentList", "Item clicked: " + id);

        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(tasks.get(position));
        }
    }

    public void refreshData() {
        TaskManager taskManager = new TaskManager(getActivity());
//        tasks = taskManager.getPendingTasks();
        tasks.clear();
        tasks.addAll(taskManager.getPendingTasks());
        Log.d("FragmentList", "DATA REFRESH");
    }

    public void clearData() {
        TaskManager taskManager = new TaskManager(getActivity());
        taskManager.clearFile("pending");
        taskManager.clearFile("completed");
        taskManager.clearFile("backlog");
        tasks.clear();
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Task task);
    }
}
