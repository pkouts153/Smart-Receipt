package com.SR.smartreceipt;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SearchResultsListFragment extends ListFragment {
	
	Cursor cursor;
	String[] columns;
	int[] textviews;
	SimpleCursorAdapter simpleCursorAdapter;
	
	View view;
	
	public SearchResultsListFragment() {
		// TODO Auto-generated constructor stub
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {

    	cursor = SearchResultsActivity.c;

    	columns = cursor.getColumnNames();
    	textviews = new int[]{R.id.id,R.id.name,R.id.price,R.id.category,R.id.purchase_date,R.id.store};
    	//R.id.id,R.id.name,R.id.category,R.id.price,R.id.purchase_date,R.id.store
    	simpleCursorAdapter = new SimpleCursorAdapter(inflater.getContext(), R.layout.fragment_search_results_row, cursor, columns, textviews, 0);
        simpleCursorAdapter.bindView(container, inflater.getContext(), cursor);
        
        setListAdapter(simpleCursorAdapter);
        
        // Inflate the layout for this fragment
         
        view = inflater.inflate(R.layout.fragment_search_results_list, container, false);
        return view;
        }
    
    public View getView(){
    	return view;
    }
    
}
