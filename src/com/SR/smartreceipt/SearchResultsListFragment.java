package com.SR.smartreceipt;

import java.util.ArrayList;
import java.util.List;

import com.SR.data.FeedReaderContract.FeedCategory;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class SearchResultsListFragment extends ListFragment {

	static String[] columns;
	static int[] textviews;
	static SimpleCursorAdapter simpleCursorAdapter;
	
	static TextView cost;
	
	static String total_cost;
	
	public SearchResultsListFragment() {
		
	}
	
	/*public SearchResultsListFragment(Cursor c) {
		cursor = c;
	}*/
	
	public static SearchResultsListFragment newInstance(Cursor cursor, String cost, Context context, ViewGroup container, String group_by){

		//List<String> temp = new ArrayList<String>();
		SearchResultsListFragment listFragment = new SearchResultsListFragment();
		
		total_cost = cost;
		//if the user has selected group by for his search the group by column will not be visible in the row
		//but only in the tab title
		/*if (group_by!=null) {
			for (int i=0; i<7; i++)
				if (!cursor.getColumnName(i).equals(group_by) && !group_by.equals("username"))
					temp.add(cursor.getColumnName(i));
				//columns.setValue(cursor.getColumnName(i), i);
			temp.toArray(columns);
			temp.clear();
		}
		else*/
		
		columns = cursor.getColumnNames();
	
		/*if (group_by.equals("product_category"))
			textviews = new int[]{R.id.id,R.id.name,R.id.price,R.id.purchase_date,R.id.store};
		else if (group_by.equals("store_name"))
			textviews = new int[]{R.id.id,R.id.name,R.id.price,R.id.category,R.id.purchase_date};
		else*/
		textviews = new int[]{R.id.id,R.id.name,R.id.price,R.id.category,R.id.purchase_date,R.id.store};
    	
		simpleCursorAdapter = new SimpleCursorAdapter(context, R.layout.fragment_search_results_row, cursor, columns, textviews, 0);
        simpleCursorAdapter.bindView(container, context, cursor);
        
        listFragment.setListAdapter(simpleCursorAdapter);
        
        return listFragment;
    }
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {

    	/*columns = cursor.getColumnNames();
    	textviews = new int[]{R.id.id,R.id.name,R.id.price,R.id.category,R.id.purchase_date,R.id.store,R.id.username};

    	simpleCursorAdapter = new SimpleCursorAdapter(inflater.getContext(), R.layout.fragment_search_results_row, cursor, columns, textviews, 0);
        simpleCursorAdapter.bindView(container, inflater.getContext(), cursor);
        
        setListAdapter(simpleCursorAdapter);*/

        // Inflate the layout for this fragment
    	View view = inflater.inflate(R.layout.fragment_search_results_list, container, false);
    	
    	cost = (TextView)view.findViewById(R.id.cost);
    	cost.setText(total_cost);
    	
        return view;
  
    }
    
}
