package com.SR.smartreceipt;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Locale;

import com.SR.data.SearchHandler;
import com.SR.data.User;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;

public class SearchResultsActivity extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	
	Context context = this;
	
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

	User user;
	
	DatePickerFragment dateFragment;
	
	SearchHandler searchHandler;
	
	SearchResultsFragment searchResultsFragment;
	
	static int tabs;
	
	static Cursor c;
	static Cursor sums;
	
	static ArrayList<String> groups_names;
	static ArrayList<String> group_cost;
	//static ArrayList<Integer> group_change_positions;
	
	String[] columns;
	int[] textviews;
	SimpleCursorAdapter simpleCursorAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Show the Up button in the action bar.
		setupActionBar();
		getOverflowMenu();
		
		Bundle extras = getIntent().getExtras();
		
		product = extras.getString("product");
		category = extras.getString("category");
		min_cost = extras.getString("mn_cost");
		max_cost = extras.getString("mx_cost");
		start_date = extras.getString("start_date");
		end_date = extras.getString("end_date");
		store = extras.getString("store");
		family = extras.getString("family");
		group_by = extras.getString("group_by");
		
		searchHandler = new SearchHandler(this);
		c = searchHandler.getSearchResults(product, category, min_cost, max_cost, start_date, end_date, store, family, group_by);
		sums = searchHandler.getSums();
		//searchHandler.getSearchFeedReaderDbHelper().close();
		
		groups_names = new ArrayList<String>();
		group_cost = new ArrayList<String>();
		//group_change_positions = new ArrayList<Integer>();
		
		if (!group_by.equals("")){
			
			c.moveToFirst();
			
			String group_name = c.getString(c.getColumnIndexOrThrow(group_by));
			String group_name1 = c.getString(c.getColumnIndexOrThrow(group_by));
			
			groups_names.add(group_name);
			
			c.moveToNext();
			
			while (!c.isAfterLast()){
				group_name1 = c.getString(c.getColumnIndexOrThrow(group_by));
				if (!group_name1.equals(group_name)) {
					group_name = c.getString(c.getColumnIndexOrThrow(group_by));
					groups_names.add(group_name);
					//group_change_positions.add(c.getPosition());
				}
				c.moveToNext();
			}
			
			
			sums.moveToFirst();
			
			while (!sums.isAfterLast()){
				group_cost.add(sums.getString(sums.getColumnIndexOrThrow("sum")));
				sums.moveToNext();
			}
			
		}
		groups_names.trimToSize();
		group_cost.trimToSize();
		
		Log.w("groups", "" + groups_names.size() + "");
		Log.w("groups", "" + group_cost.size() + "");
		
		if (group_by.equals("") || groups_names.size()==0) {
		
			setContentView(R.layout.activity_search_results_no_tabs);

		    FragmentManager fragmentManager = getSupportFragmentManager();
		    FragmentTransaction ft = fragmentManager.beginTransaction();
		    
		    SearchResultsListFragment listFragment = new SearchResultsListFragment();
		    //listFragment.setCursor(c);
		    ft.add(R.id.fragment_frame, listFragment);
		    ft.commit();

        } 
		else{
			
			tabs = groups_names.size();
			setContentView(R.layout.activity_search_results);

			// Create the adapter that will return a fragment for each of the three
			// primary sections of the app.
			mSectionsPagerAdapter = new SectionsPagerAdapter(
					getSupportFragmentManager());
	
			// Set up the ViewPager with the sections adapter.
			mViewPager = (ViewPager) findViewById(R.id.pager);
			mViewPager.setAdapter(mSectionsPagerAdapter);
		}
		
	}
    
	public void displayError(String message) {
		InputErrorDialogFragment errorDialog = new InputErrorDialogFragment();
		errorDialog.setMessage(message);
		errorDialog.show(getSupportFragmentManager(), "errorDialog");
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_logout:
	        	user = new User(this);
	        	user.userLogout();
	        	Intent intent = new Intent(this, LoginActivity.class);
	    		startActivity(intent);
	            return true;
			case android.R.id.home:
				// This ID represents the Home or Up button. In the case of this
				// activity, the Up button is shown. Use NavUtils to allow users
				// to navigate up one level in the application structure. For
				// more details, see the Navigation pattern on Android Design:
				//
				// http://developer.android.com/design/patterns/navigation.html#up-vs-back
				//
				NavUtils.navigateUpFromSameTask(this);
				return true;
	        default:
	        	//NavUtils.navigateUpFromSameTask(this);
	            return super.onOptionsItemSelected(item);
	    }
	}
	
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
	public void onDestroy(){
		super.onDestroy();
		searchHandler.getSearchFeedReaderDbHelper().close();
	}
	
	/*private ArrayList<String> getGroupsNames() {
		return groups_names;
	}*/
	
	
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new SearchResultsFragment();
			Bundle args = new Bundle();
			args.putInt(SearchResultsFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			
			return SearchResultsActivity.tabs;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			/*switch (position) {
			case 0:
				return getString(R.string.title_activity_search).toUpperCase(l);
			case 1:*/
				//SearchResultsActivity searchResultsActivity= new SearchResultsActivity();
			String title = SearchResultsActivity.groups_names.get(position).toUpperCase(l);
			title = title + "Total Cost: " + SearchResultsActivity.group_cost.get(position).toUpperCase(l);
			return title;
				//return getString(R.string.title_search_results).toUpperCase(l);
			/*case 2:
				return getString(R.string.title_search_diagrams).toUpperCase(l);
			case 3:
				return getString(R.string.title_activity_search).toUpperCase(l);
			}
			
			return null;*/
		}
	}

}
