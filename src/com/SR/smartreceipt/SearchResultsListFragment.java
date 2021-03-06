package com.SR.smartreceipt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
* Fragment that displays the search results product list
* 
* @author Παναγιώτης Κουτσαυτίκης 8100062
*/
@SuppressLint("ValidFragment")
public class SearchResultsListFragment extends ListFragment {

	// the columns of the cursor
	static String[] columns;
	// the ui components to display each of the columns
	static int[] textviews;
	
	/**
	 * An easy adapter to map columns from the cursor to TextViews 
	 */
	static SimpleCursorAdapter simpleCursorAdapter;
	
	TextView cost;
	
	/**
	 * Class consructor
	 */
	public SearchResultsListFragment() {
		
	}
	
	/**
	 * Creates an instance of the class
	 * 
	 * @param cursor  the cursor to be displayed
	 * @param cost  the total cost of the products of the search results
	 * @param context
	 * @param container  the layout of the SearchResultsActivity
	 * @param group_by  
	 * @return the new instance of the list fragment
	 */
	public static SearchResultsListFragment newInstance(Cursor cursor, String cost, Context context, ViewGroup container, String group_by){

		//List<String> temp = new ArrayList<String>();
		SearchResultsListFragment listFragment = new SearchResultsListFragment();
		
		//add the cost to the arguments for the handling of the TextView in the onCreate method
		Bundle args = new Bundle(1);
		args.putString("cost", cost);
		listFragment.setArguments(args);
		
		
		
		//total_cost = cost;
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
		
		
		// create a list of budgets and bind the cursor to the list, 
				// leading the list's rows to display the data from the corresponding cursor lines
		columns = cursor.getColumnNames();
		
		cursor.moveToFirst();
		
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

        // Inflate the layout for this fragment
    	View view = inflater.inflate(R.layout.fragment_search_results_list, container, false);
    	
    	// set the text of the total cost TextView
    	String total_cost = getArguments().getString("cost");
    	cost = (TextView)view.findViewById(R.id.cost);
    	cost.setText(total_cost);
    	
        return view;
  
    }
    
}
