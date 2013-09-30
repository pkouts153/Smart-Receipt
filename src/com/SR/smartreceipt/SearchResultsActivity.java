package com.SR.smartreceipt;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.SR.data.FeedReaderDbHelper;
import com.SR.data.SearchHandler;
import com.SR.data.User;
import com.SR.processes.MyApplication;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

/**
* Activity that displays the search results screen
* 
* @author Panagiotis Koutsaftikis
*/
public class SearchResultsActivity extends FragmentActivity {

	/**
	 * The PagerAdapter that will provide fragments for each of the sections. 
	 * We use a FragmentPagerAdapter derivative, which will keep every loaded fragment in memory
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The ViewPager that will host the section contents.
	 */
	ViewPager mViewPager;
	
	//Search selection input
	
	Float total_cost;
	String product;
	String category;
	String min_cost;
	String max_cost;
	String start_date;
	String end_date;
	String store;
	String family;
	String group_by;

	//data variables
	User user;
	SearchHandler searchHandler;
	
	FeedReaderDbHelper mDbHelper;
	SQLiteDatabase db;
	
	Bundle extras;

	Cursor c;
	Cursor sums;
	
	
	DatePickerFragment dateFragment;
	
	ArrayList<String> group_names;
	ArrayList<String> group_cost;
	
	SimpleCursorAdapter simpleCursorAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set up the UP button in ActionBar and Overflow menu
		setupActionBar();
		getOverflowMenu();
		
		//get the data from SearchActivity
		extras = getIntent().getExtras();
		
		product = extras.getString("product");
		category = extras.getString("category");
		min_cost = extras.getString("mn_cost");
		max_cost = extras.getString("mx_cost");
		start_date = extras.getString("start_date");
		end_date = extras.getString("end_date");
		store = extras.getString("store");
		family = extras.getString("family");
		group_by = extras.getString("group_by");
		
		mDbHelper = new FeedReaderDbHelper(this);
		db = mDbHelper.getWritableDatabase();
		
		//get the product results for these data
		searchHandler = new SearchHandler(db);
		c = searchHandler.getSearchResults(product, category, min_cost, max_cost, start_date, end_date, store, family, group_by, null);
		sums = searchHandler.getSums();
		
		group_names = new ArrayList<String>();
		group_cost = new ArrayList<String>();
		
		// if the user has selected a group_by, then get the group_names for this group_by
		if (!group_by.equals("")){
			
			c.moveToFirst();
			
			String group_name = c.getString(c.getColumnIndexOrThrow(group_by));
			String group_name1 = c.getString(c.getColumnIndexOrThrow(group_by));
			
			group_names.add(group_name);
			
			if (c.moveToNext()){
			
				while (!c.isAfterLast()){
					group_name1 = c.getString(c.getColumnIndexOrThrow(group_by));
					if (!group_name1.equals(group_name)) {
						group_name = c.getString(c.getColumnIndexOrThrow(group_by));
						group_names.add(group_name);
					}
					c.moveToNext();
				}
			}
		}
		
		// get the total cost for each group or the total cost if there is no group_by
		if (sums.moveToFirst()){
			
			while (!sums.isAfterLast()){
				group_cost.add(sums.getString(sums.getColumnIndexOrThrow("sum")));
				sums.moveToNext();
			}
		}
		
		group_names.trimToSize();
		group_cost.trimToSize();
		
		// if there is no group_by
		if (group_by.equals("") || group_names.size()==0) {
			
			// set the content view to "no tabs"
			setContentView(R.layout.activity_search_results_no_tabs);

			// set up the fragment manager and add a SearchResultsListFragment
		    FragmentManager fragmentManager = getSupportFragmentManager();
		    FragmentTransaction ft = fragmentManager.beginTransaction();

		    // the last variable (group_name) is null because the user hasn't selected a group_by and
		    // we display all the product results
		    SearchResultsListFragment listFragment = SearchResultsListFragment.newInstance(c, group_cost.get(0), this, 
		    		(ViewGroup) findViewById(R.id.search_results_no_tabs), null);
		    
		    ft.add(R.id.fragment_frame, listFragment);
		    ft.commit();
		    
		    //mDbHelper.close();
        } 
		else{
			
			setContentView(R.layout.activity_search_results);

			// the list of fragments to be displayed
			List<Fragment> fragments =  getFragments();
			
			// Create the adapter that will return a fragment for each section of the searhc results.
			mSectionsPagerAdapter = new SectionsPagerAdapter(
					getSupportFragmentManager(), fragments);
	
			// Set up the ViewPager with the sections adapter.
			mViewPager = (ViewPager) findViewById(R.id.pager);

			mViewPager.setAdapter(mSectionsPagerAdapter);
			
			//mDbHelper.close();
		}
	}

	/**
	 * Creates the fragments for the PagerAdapter
	 * each fragment represents a group's products in the results screen
	 * 
	 * @return a list of fragments to be displayed in the sections
	 */
	private List<Fragment> getFragments(){
		
		List<Fragment> fList = new ArrayList<Fragment>();
		
		Bundle args = new Bundle();

		args.putString("product", product);
		args.putString("category", category);
		args.putString("mn_cost", min_cost);
		args.putString("mx_cost", max_cost);
		args.putString("start_date", start_date);
		args.putString("end_date", end_date);
		args.putString("store", store);
		args.putString("family", family);
		args.putString("group_by", group_by);
		
		// for each of the search groups create a SearchResultsListFragment
		for (int i=0; i<group_names.size(); i++){
			args.putString("group_name", group_names.get(i));
			args.putString("group_cost", group_cost.get(i));
			
			Cursor cursor = searchHandler.getSearchResults(product, category, min_cost, max_cost, 
						start_date, end_date, store, family, group_by, group_names.get(i));

			fList.add(SearchResultsListFragment.newInstance(cursor, group_cost.get(i), this, 
					(ViewGroup) findViewById(R.id.search_results), group_by));
			
		}
		
		return fList;
	}
	
	/**
	 * Display an error dialog
	 * 
	 * @param message  the message to be displayed in the dialog
	 */
	public void displayError(String message) {
		InputErrorDialogFragment errorDialog = new InputErrorDialogFragment();
		errorDialog.setMessage(message);
		errorDialog.show(getFragmentManager(), "errorDialog");
	}
	
	/**
	 * Set up the ActionBar, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	/**
	 * Inflate the menu; this adds items to the action bar if it is present.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.search, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * Handle presses on the action bar items
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.action_logout:
	        	mDbHelper = new FeedReaderDbHelper(this);
	    		db = mDbHelper.getWritableDatabase();
	    		
	        	user = new User(db);
	        	user.userLogout();
	        	
	        	mDbHelper.close();
	        	
	        	Intent intent = new Intent(this, LoginActivity.class);
	    		startActivity(intent);
	            return true;
			case android.R.id.home:
				// This action represents the Home or Up button which leads the user to the previous screen
				NavUtils.navigateUpFromSameTask(this);
				return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
    
	/**
	 * Set up the Overflow menu.
	 */
	private void getOverflowMenu() {

	     try {
	        ViewConfiguration config = ViewConfiguration.get(this);
	        Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
	        if(menuKeyField != null) {
	            menuKeyField.setAccessible(true);
	            menuKeyField.setBoolean(config, false);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
    @Override
    protected void onResume() {
    	super.onResume();
    	MyApplication.activityResumed();
    	
    	/*if (mDbHelper == null) {
    		new FeedReaderDbHelper(this);
    		db = mDbHelper.getWritableDatabase();
    	}*/
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	MyApplication.activityPaused();
    	
    	if (mDbHelper != null)
    		mDbHelper.close();
    }	
    
	@Override
	public void onDestroy(){
		super.onDestroy();
		group_names.clear();
		group_cost.clear();
		group_names.trimToSize();
		group_cost.trimToSize();
	}
	
	
	/**
	 * A FragmentPagerAdapter that returns a fragment corresponding to
	 * one of the sections
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		
		private List<Fragment> fragments;
		
		public SectionsPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
			super(fm);
			this.fragments = fragments;
		}

		/**
		 * Instantiates the fragment for the given section
		 * 
		 * @param position  the section number 
		 * @return the fragment of the list that corresponds to the given section
		 */
		@Override
		public Fragment getItem(int position) {

			return this.fragments.get(position);
			
		}
		
		/**
		 * Sets the number of sections to be displayed
		 * 
		 * @return the number of sections
		 */
		@Override
		public int getCount() {
			
			return group_names.size();
		}

		/**
		 * Creates the title of each section
		 * 
		 * @param position  the section number 
		 * @return the title
		 */
		@Override
		public CharSequence getPageTitle(int position) {
			
			Locale l = Locale.getDefault();
			
			String title = group_names.get(position).toUpperCase(l);
			return title;
		}
	}

}
