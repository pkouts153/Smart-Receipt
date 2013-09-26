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
import android.view.ViewGroup;

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
	
	Cursor c;
	Cursor sums;
	
	ArrayList<String> group_names;
	ArrayList<String> group_cost;
	
	Bundle extras;
	
	SimpleCursorAdapter simpleCursorAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Show the Up button in the action bar.
		setupActionBar();
		getOverflowMenu();
		
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
		
		searchHandler = new SearchHandler(this);
		c = searchHandler.getSearchResults(product, category, min_cost, max_cost, start_date, end_date, store, family, group_by, null);
		sums = searchHandler.getSums();
		//searchHandler.getSearchFeedReaderDbHelper().close();
		
		group_names = new ArrayList<String>();
		group_cost = new ArrayList<String>();
		
		if (!group_by.equals("")){
			
			c.moveToFirst();
			
			String group_name = c.getString(c.getColumnIndexOrThrow(group_by));
			String group_name1 = c.getString(c.getColumnIndexOrThrow(group_by));
			
			group_names.add(group_name);
			
			c.moveToNext();
			
			while (!c.isAfterLast()){
				group_name1 = c.getString(c.getColumnIndexOrThrow(group_by));
				if (!group_name1.equals(group_name)) {
					group_name = c.getString(c.getColumnIndexOrThrow(group_by));
					group_names.add(group_name);
				}
				c.moveToNext();
			}
		}
		
		sums.moveToFirst();
		
		while (!sums.isAfterLast()){
			group_cost.add(sums.getString(sums.getColumnIndexOrThrow("sum")));
			sums.moveToNext();
		}
		
		group_names.trimToSize();
		group_cost.trimToSize();
		
		if (group_by.equals("") || group_names.size()==0) {
		
			setContentView(R.layout.activity_search_results_no_tabs);

		    FragmentManager fragmentManager = getSupportFragmentManager();
		    FragmentTransaction ft = fragmentManager.beginTransaction();
		    
		    Log.w("ResultsActivity", group_cost.get(0));

		    SearchResultsListFragment listFragment = SearchResultsListFragment.newInstance(c, group_cost.get(0), this, 
		    		(ViewGroup) findViewById(R.id.search_results_no_tabs), null);
		    
		    ft.add(R.id.fragment_frame, listFragment);
		    ft.commit();

        } 
		else{
			
			setContentView(R.layout.activity_search_results);

			// Create the adapter that will return a fragment for each of the three
			// primary sections of the app.
			mSectionsPagerAdapter = new SectionsPagerAdapter(
					getSupportFragmentManager());
	
			// Set up the ViewPager with the sections adapter.
			mViewPager = (ViewPager) findViewById(R.id.pager);
			//mViewPager.setCurrentItem(1);

			mViewPager.setAdapter(mSectionsPagerAdapter);
		}
		
	}

	public void displayError(String message) {
		InputErrorDialogFragment errorDialog = new InputErrorDialogFragment();
		errorDialog.setMessage(message);
		errorDialog.show(getFragmentManager(), "errorDialog");
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
		group_names.clear();
		group_cost.clear();
		group_names.trimToSize();
		group_cost.trimToSize();
		searchHandler.getSearchFeedReaderDbHelper().close();
	}
	
	
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
			// Return a SearchResultsFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			
			Fragment fragment = new SearchResultsFragment();
			Bundle args = new Bundle();
			args.putInt(SearchResultsFragment.ARG_SECTION_NUMBER, position + 1);
			args.putString("product", product);
			args.putString("category", category);
			args.putString("mn_cost", min_cost);
			args.putString("mx_cost", max_cost);
			args.putString("start_date", start_date);
			args.putString("end_date", end_date);
			args.putString("store", store);
			args.putString("family", family);
			args.putString("group_by", group_by);
			args.putStringArrayList("group_names", group_names);
			args.putStringArrayList("group_cost", group_cost);
			
			
			//Fragment fragment = SearchResultsFragment.newInstance(context, (ViewGroup) findViewById(R.id.search_results), args);
			
			fragment.setArguments(args);
			
			return fragment;
			
		}
		
		@Override
		public int getCount() {
			
			return group_names.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			
			Locale l = Locale.getDefault();
			
			String title = group_names.get(position).toUpperCase(l);
			//title = title + "Total Cost: " + group_cost.get(position).toUpperCase(l);
			return title;
		}
	}

}
