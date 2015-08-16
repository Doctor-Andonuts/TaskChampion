package com.doctorandonuts.taskchampion;

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

/**
 * Created by Mr Saturn on 8/9/2015 for TaskChampion
 */
public class TaskListFragment extends ListFragment {
    private CustomArrayAdapter arrayAdapter;
    private List<Task> tasks = new ArrayList<>();


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        refreshData();

        //arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, descriptions);
        arrayAdapter = new CustomArrayAdapter(getActivity(), tasks);
        setListAdapter(arrayAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("FragmentList", "Item clicked: " + id);
    }

    public void refreshData() {
        TaskList taskList = new TaskList(getActivity());
        tasks = taskList.getDescriptionList(tasks);
        Log.d("FragmentList", "DATA REFRESH");
    }

    public void clearData() {
        tasks.clear();
    }
}
