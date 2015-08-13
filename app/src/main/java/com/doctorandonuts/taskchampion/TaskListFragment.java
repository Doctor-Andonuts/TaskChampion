package com.doctorandonuts.taskchampion;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mr Saturn on 8/9/2015 for TaskChampion
 */
public class TaskListFragment extends ListFragment {
    private ArrayAdapter<String> arrayAdapter;
    private List<String> descriptions = new ArrayList<>();


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        refreshData();

        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, descriptions);
        setListAdapter(arrayAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("FragmentList", "Item clicked: " + id);
    }

    public void refreshData() {
        TaskList taskList = new TaskList(getActivity());
        descriptions = taskList.getDescriptionList(descriptions);
        Log.d("FragmentList", "DATA REFRESH");
    }

    public void clearData() {
        descriptions.clear();
    }
}
