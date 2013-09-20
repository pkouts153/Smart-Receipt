package com.SR.smartreceipt;

import com.SR.data.SearchHandler;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
	
	ViewGroup container;
	LayoutInflater inflater;
	
	/*
	//Search results fragment components
	
	TextView result_cost;
	TextView result_name;
	TextView result_category;
	TextView result_price;
	TextView result_date;
	TextView result_store;
	TextView result_user;
	Button diagrams;*/
	
	//SearchResultsActivity searchResultsActivity = new SearchResultsActivity();
	
	Cursor cursor = SearchResultsActivity.c;
	
	SearchHandler searchHandler;
	
	public SearchResultsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater infl, ViewGroup cont,
			Bundle savedInstanceState) {
		
		container = cont;
		inflater = infl;

		
		//If the screen the user sees is the search selection screen
		if (getArguments().getInt(ARG_SECTION_NUMBER)==1) {
			
			FragmentManager fragmentManager = getFragmentManager();
		    FragmentTransaction ft = fragmentManager.beginTransaction();
		    
		    SearchResultsListFragment listFragment = new SearchResultsListFragment();
		    //listFragment.setCursorAndPosition(SearchResultsActivity.c, SearchResultsActivity.group_change_positions.get(0));
		    ft.add(R.id.pager_fragment_frame, listFragment);
		    ft.commit();
		    
		    rootView = inflater.inflate(R.layout.fragment_search_results_list, container, false);
		    		//listFragment.getView();
		}
		//If the screen the user sees is the search results screen
		else if (getArguments().getInt(ARG_SECTION_NUMBER)==2) {
			rootView = inflater.inflate(R.layout.activity_budget, container, false);

		}
		//If the screen the user sees is the search diagrams screen
		else {
			rootView = inflater.inflate(R.layout.activity_save, container, false);
			//dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
			//dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
		}
		
		return rootView;
	}


	@Override
	public void onClick(View v) {

	}
	
	public void setSearchHandler(SearchHandler handler){
		searchHandler = handler;
	}
}