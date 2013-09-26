package com.SR.smartreceipt;

import java.util.ArrayList;

import com.SR.data.SearchHandler;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * A dummy fragment representing a section of the app, but that simply
 * displays dummy text.
 */
public class SearchResultsFragment extends Fragment implements OnClickListener{
	/**
	 * The fragment argument representing the section number for this
	 * fragment.
	 */
	public static final String ARG_SECTION_NUMBER = "section_number";
	View rootView = null;
	
	Cursor cursor;
	Cursor sums;
	
	SearchHandler searchHandler;
	
	String product;
	String category;
	String min_cost;
	String max_cost;
	String start_date;
	String end_date;
	String store;
	String family;
	String group_by;
	ArrayList<String> groups_names;
	ArrayList<String> group_cost;
	
	Bundle args;
	
	SearchResultsListFragment listFragment;

	TextView cost;
	
	public SearchResultsFragment() {
	}
	
	/*public static SearchResultsFragment newInstance(Context context, ViewGroup container, Bundle args){
		
		SearchResultsFragment fragment = new SearchResultsFragment();
		
		//Position means the screen the user sees
		int position = args.getInt(ARG_SECTION_NUMBER);
		Log.w("position", "" + position + "");
		
		group_cost = new ArrayList<String>();
		
	    product = args.getString("product");
		category = args.getString("category");
		min_cost = args.getString("mn_cost");
		max_cost = args.getString("mx_cost");
		start_date = args.getString("start_date");
		end_date = args.getString("end_date");
		store = args.getString("store");
		family = args.getString("family");
		group_by = args.getString("group_by");
		groups_names = args.getStringArrayList("group_names");
		group_cost = args.getStringArrayList("group_cost");
		
		cost = (TextView) container.findViewById(R.id.cost);
		
		try{
			if (group_cost.size()!=0)
				cost.setText(group_cost.get(position-1));
			else
				cost.setText("0");
		} catch (NullPointerException e){
			//cost.setText("0");
		}
			
		
		searchHandler = new SearchHandler(context);
		
		cursor = searchHandler.getSearchResults(product, category, min_cost, max_cost, 
					start_date, end_date, store, family, group_by, groups_names.get(position-1));

		listFragment = SearchResultsListFragment.newInstance(cursor, context, 
				container, group_by);

	    /*FragmentTransaction ft = getChildFragmentManager().beginTransaction();
	    ft.add(R.id.results_fragment, listFragment);
	    ft.commit();
		return fragment;
	}*/
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		args = getArguments();

		//Position means the screen the user sees
		int position = args.getInt(ARG_SECTION_NUMBER);
		Log.w("position", "" + position + "");
		
		group_cost = new ArrayList<String>();
		
	    product = args.getString("product");
		category = args.getString("category");
		min_cost = args.getString("mn_cost");
		max_cost = args.getString("mx_cost");
		start_date = args.getString("start_date");
		end_date = args.getString("end_date");
		store = args.getString("store");
		family = args.getString("family");
		group_by = args.getString("group_by");
		groups_names = args.getStringArrayList("group_names");
		group_cost = args.getStringArrayList("group_cost");
		
		cost = (TextView) container.findViewById(R.id.cost);
		
		try{
			if (group_cost.size()!=0)
				cost.setText(group_cost.get(position-1));
			else
				cost.setText("0");
		} catch (NullPointerException e){
		}
			
		
		searchHandler = new SearchHandler(getActivity());
		
		cursor = searchHandler.getSearchResults(product, category, min_cost, max_cost, 
					start_date, end_date, store, family, group_by, groups_names.get(position-1));
		sums = searchHandler.getSums();
		
		listFragment = SearchResultsListFragment.newInstance(cursor, group_cost.get(position-1), getActivity(), 
				container, group_by);

	    FragmentTransaction ft = getChildFragmentManager().beginTransaction();
	    ft.add(R.id.results_fragment, listFragment);
	    ft.commit();
	    
	    rootView = inflater.inflate(R.layout.fragment_search_results, container, false);
	    
		return rootView;
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		groups_names.clear();
		groups_names.trimToSize();
		searchHandler.getSearchFeedReaderDbHelper().close();
	}
	
	@Override
	public void onClick(View v) {

	}
}