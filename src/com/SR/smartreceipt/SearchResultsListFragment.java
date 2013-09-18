package com.SR.smartreceipt;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SearchResultsListFragment extends ListFragment {

	public SearchResultsListFragment() {
		// TODO Auto-generated constructor stub
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_search, container, false);
    }

}
