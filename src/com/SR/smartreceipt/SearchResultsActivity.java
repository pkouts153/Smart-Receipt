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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
* Activity that displays the search results screen
* 
* @author Παναγιώτης Κουτσαυτίκης 8100062
*/
public class SearchResultsActivity extends FragmentActivity implements OnClickListener{

	/**
	 * The PagerAdapter that will provide fragments for each of the sections. 
	 * We use a FragmentPagerAdapter derivative, which will keep every loaded fragment in memory
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The ViewPager that will host the section contents.
	 */
	ViewPager mViewPager;
	
	ImageButton graph;
	
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
	String activity;
	
	//data variables
	User user;
	SearchHandler searchHandler;
	
	FeedReaderDbHelper mDbHelper;
	SQLiteDatabase db;
	
	// object for accessing data passed from previous activity 
	Bundle extras;

	// search results without separation of the groups
	Cursor general_results;
	
	// the costs of the groups
	Cursor costs;
	
	// the product results of each group
	Cursor group_results;
	
	/**
	 * Saves the group names depending on the group_by selection of the user for further manipulation
	 */
	ArrayList<String> group_names;
	
	/**
	 * Saves the cost for each group or the total cost if there are no groups for further manipulation
	 */
	ArrayList<String> group_cost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// set up the UP button in ActionBar and Overflow menu
		setupActionBar();
		getOverflowMenu();
		
		try{
			//get the data from SearchActivity or ChartActivity
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
			group_names = extras.getStringArrayList("group_names");
			group_cost = extras.getStringArrayList("group_cost");
			activity = extras.getString("activity");
			
			mDbHelper = new FeedReaderDbHelper(this);
			db = mDbHelper.getWritableDatabase();
			
			searchHandler = new SearchHandler(db);
			
			// if there is no group_by
			if (activity.equals("search")) {
				
				// set the content view to "no tabs"
				setContentView(R.layout.activity_search_results_no_tabs);
	
				// set up the fragment manager and add a SearchResultsListFragment
			    FragmentManager fragmentManager = getSupportFragmentManager();
			    FragmentTransaction ft = fragmentManager.beginTransaction();

				//get the product results for these data
			    general_results = searchHandler.getSearchResults(product, category, min_cost, max_cost, start_date, end_date, store, family, group_by, null);
				
			    // the last variable (group_name) is null because the user hasn't selected a group_by and
			    // we display all the product results
			    SearchResultsListFragment listFragment = SearchResultsListFragment.newInstance(general_results, group_cost.get(0), this, 
			    		(ViewGroup) findViewById(R.id.search_results_no_tabs), null);
			    
			    ft.add(R.id.fragment_frame, listFragment);
			    ft.commit();
			    
			    //mDbHelper.close();
	        } 
			else{
				
				setContentView(R.layout.activity_search_results);
	
				graph = (ImageButton)findViewById(R.id.graph_button);
				graph.setOnClickListener(this);
				
				// the list of fragments to be displayed
				List<Fragment> fragments =  getFragments();
				
				// Create the adapter that will return a fragment for each section of the search results.
				mSectionsPagerAdapter = new SectionsPagerAdapter(
						getSupportFragmentManager(), fragments);
		
				// Set up the ViewPager with the sections adapter.
				mViewPager = (ViewPager) findViewById(R.id.pager);
	
				mViewPager.setAdapter(mSectionsPagerAdapter);
				
				//mDbHelper.close();
			}
		} catch (Exception e){
			setContentView(R.layout.activity_search_results_no_tabs);
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
			
			group_results = searchHandler.getSearchResults(product, category, min_cost, max_cost, 
						start_date, end_date, store, family, group_by, group_names.get(i));

			fList.add(SearchResultsListFragment.newInstance(group_results, group_cost.get(i), this, 
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
	        	user.userLogout(this);
	        	
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
	 * 
	 * @author Παναγιώτης Κουτσαυτίκης 8100062
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


	@Override
	public void onClick(View arg0) {
		Bundle args = getIntent().getExtras();
		
		Intent intent = new Intent(this, ChartActivity.class);
		args.putStringArrayList("groups names", group_names);
		args.putStringArrayList("groups costs", group_cost);
		
		intent.putExtras(args);
		
		startActivity(intent);
		
	}

}
