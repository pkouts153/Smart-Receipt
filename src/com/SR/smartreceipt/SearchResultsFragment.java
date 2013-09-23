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
	
	Bundle args;
	
	SearchResultsListFragment listFragment;
	
	public SearchResultsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		args = getArguments();
		
		//Position means the screen the user sees
		int position = args.getInt(ARG_SECTION_NUMBER);
		Log.w("position", "" + position + "");
	
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
		
		
		searchHandler = new SearchHandler(getActivity());
		
		cursor = searchHandler.getSearchResults(product, category, min_cost, max_cost, 
					start_date, end_date, store, family, group_by, groups_names.get(position-1));
		
		Log.w("group name", groups_names.get(position-1));

		listFragment = SearchResultsListFragment.newInstance(cursor, getActivity(), 
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