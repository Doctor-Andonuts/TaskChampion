package com.doctorandonuts.taskchampion;

import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by Mr Saturn on 8/9/2015 for TaskChampion
 */
public class ArrayListFragment extends ListFragment {
    private ArrayAdapter<String> arrayAdapter;
    public String[] oldTitles =
            {
                    "Henry IV",
                    "Henry V",
                    "Henry VIII",
                    "Richard II",
                    "Richard III",
                    "Merchant of Venice",
                    "Othello",
                    "King Lear"
            };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, oldTitles);
        setListAdapter(arrayAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("FragmentList", "Item clicked: " + id);
    }

    public void refreshData() {
        oldTitles[0] = "NEW Henry IV";
        oldTitles[1] = "NEW Henry V";
        oldTitles[2] = "NEW Henry VIII";
        //arrayAdapter.notifyDataSetChanged();
        //((ArrayAdapter)this.getListAdapter()).notifyDataSetChanged();
        Log.d("FragmentList", "DATA REFRESH");
    }
}
