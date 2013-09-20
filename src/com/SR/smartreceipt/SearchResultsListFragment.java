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
	//Integer position;
	String[] columns;
	int[] textviews;
	SimpleCursorAdapter simpleCursorAdapter;
	
	View view;
	
	public SearchResultsListFragment() {
		
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {

    	cursor = SearchResultsActivity.c;
    	/*if (position==0) {
	    	columns = cursor.getColumnNames();
	    	textviews = new int[]{R.id.id,R.id.name,R.id.price,R.id.category,R.id.purchase_date,R.id.store};
	
	    	simpleCursorAdapter = new SimpleCursorAdapter(inflater.getContext(), R.layout.fragment_search_results_row, cursor, columns, textviews, 0);
	        simpleCursorAdapter.bindView(container, inflater.getContext(), cursor);
	        
	        setListAdapter(simpleCursorAdapter);
    	}
    	else {*/
    		//cursor.
    		
    		
	    	columns = cursor.getColumnNames();
	    	textviews = new int[]{R.id.id,R.id.name,R.id.price,R.id.category,R.id.purchase_date,R.id.store,R.id.username};
	
	    	simpleCursorAdapter = new SimpleCursorAdapter(inflater.getContext(), R.layout.fragment_search_results_row, cursor, columns, textviews, 0);
	        simpleCursorAdapter.bindView(container, inflater.getContext(), cursor);
	        
	        setListAdapter(simpleCursorAdapter);
    	//}
        
        // Inflate the layout for this fragment
         
        view = inflater.inflate(R.layout.fragment_search_results_list, container, false);
        return view;
  
        }
    
    /*public void setCursorAndPosition(Cursor c, Integer p){
    	cursor = c;
    	position = p;
    }
    
    public void setCursor(Cursor c){
    	cursor = c;
    }*/
    
    public View getView(){
    	return view;
    }
    
}
