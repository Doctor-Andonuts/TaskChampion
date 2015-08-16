package com.doctorandonuts.taskchampion;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.doctorandonuts.taskchampion.sync.CustomArrayAdapter;
import com.doctorandonuts.taskchampion.task.Task;
import com.doctorandonuts.taskchampion.task.TaskList;

import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends ListFragment {
    private CustomArrayAdapter arrayAdapter;
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        refreshData();

        arrayAdapter = new CustomArrayAdapter(getActivity(), tasks);
        setListAdapter(arrayAdapter);
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
            mListener.onFragmentInteraction(tasks.get(position).getValue("uuid"));
        }
    }

    public void refreshData() {
        TaskList taskList = new TaskList(getActivity());
        tasks = taskList.getTaskList(tasks);
        Log.d("FragmentList", "DATA REFRESH");
    }

    public void clearData() {
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
        void onFragmentInteraction(String id);
    }
}
