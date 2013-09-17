package com.SR.smartreceipt;

import java.util.Locale;

import com.SR.data.SearchHandler;
import com.SR.data.User;
import com.SR.data.FeedReaderContract.FeedUser;

import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class SearchActivity extends FragmentActivity {

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
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
		}
		return super.onOptionsItemSelected(item);
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
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			Fragment fragment = new SearchSectionFragment();
			Bundle args = new Bundle();
			args.putInt(SearchSectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_search_selection).toUpperCase(l);
			case 1:
				return getString(R.string.title_search_results).toUpperCase(l);
			case 2:
				return getString(R.string.title_search_diagrams).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class SearchSectionFragment extends Fragment implements OnClickListener{
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";
		View rootView = null;
		
		//Search selection input
		
		Float total_cost;
		String product;
		String category;
		Float mn_cost;
		Float mx_cost;
		String start_date;
		String end_date;
		String store;
		String family;
		String group_by;
		
		//Search selection fragment components
		
		EditText product_name;
		Spinner category_spinner;
		EditText min_cost;
		EditText max_cost;
		EditText search_start_date;
		EditText search_end_date;
		EditText search_store;
		Spinner family_spinner;
		Spinner group_by_spinner;
		Button submit;
		Button reset;
		
		User user;
		
		DatePickerFragment dateFragment;
		
		SearchHandler searchHandler;
		
		Cursor c;
		
		ViewGroup container;
		LayoutInflater inflater;
		
		//Search results fragment components
		
		TextView result_cost;
		TextView result_name;
		TextView result_category;
		TextView result_price;
		TextView result_date;
		TextView result_store;
		TextView result_user;
		Button diagrams;
		
		public SearchSectionFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater infl, ViewGroup cont,
				Bundle savedInstanceState) {
			
			container = cont;
			inflater = infl;
			
			//If the screen the user sees is the search selection screen
			if (getArguments().getInt(ARG_SECTION_NUMBER)==1) {
				rootView = inflater.inflate(R.layout.fragment_search_selection, container, false);
				
				//set up EditTexts
				
				product_name = (EditText)rootView.findViewById(R.id.product_name);
				
				min_cost = (EditText)rootView.findViewById(R.id.min_cost);
				max_cost = (EditText)rootView.findViewById(R.id.max_cost);
				
				search_start_date = (EditText)rootView.findViewById(R.id.search_start_date);
				search_start_date.setOnClickListener(this);
				
				search_end_date = (EditText)rootView.findViewById(R.id.search_end_date);
				search_end_date.setOnClickListener(this);
				
				search_store = (EditText)rootView.findViewById(R.id.search_store);
				
				//set up category spinner
				
				category_spinner = (Spinner) rootView.findViewById(R.id.category_spinner);
				
				ArrayAdapter<CharSequence> category_adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.categories_array, android.R.layout.simple_spinner_item);
				category_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				category_spinner.setAdapter(category_adapter);
				
		        //set up family spinner
		        
		        ArrayAdapter <CharSequence> fam_adapter = new ArrayAdapter <CharSequence> (this.getActivity(), android.R.layout.simple_spinner_item );
				fam_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				
				family_spinner = (Spinner) rootView.findViewById(R.id.family_spinner);
				family_spinner.setAdapter(fam_adapter);
				
				fam_adapter.add(this.getString(R.string.family_prompt));
				
				user = new User(this.getActivity());
				Cursor c = user.getFamilyMembers(User.USER_ID);
				
		        try{
			        
					c.moveToFirst();
					String family_member = c.getString(c.getColumnIndexOrThrow(FeedUser.USERNAME));

					fam_adapter.add(family_member);

					while (!c.isLast ()) {
						c.moveToNext ();
						family_member = c.getString(c.getColumnIndexOrThrow(FeedUser.USERNAME));
						fam_adapter.add(family_member);
					}
					c.close();
					user.getUserFeedReaderDbHelper().close();
					
				} catch (CursorIndexOutOfBoundsException e){
					fam_adapter.add("No family");
					c.close();
					user.getUserFeedReaderDbHelper().close();
			    }
		        
		        //set up group_by spinner
		        
		        group_by_spinner = (Spinner) rootView.findViewById(R.id.group_by_spinner);
				
				ArrayAdapter<CharSequence> group_by_adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.group_by_array, android.R.layout.simple_spinner_item);
				group_by_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				group_by_spinner.setAdapter(group_by_adapter);
				
				//set up buttons
				
		        submit = (Button)rootView.findViewById(R.id.submit);
		        submit.setOnClickListener(this);
		        
		        reset = (Button)rootView.findViewById(R.id.reset);
		        reset.setOnClickListener(this);
		        
			}
			//If the screen the user sees is the search results screen
			else if (getArguments().getInt(ARG_SECTION_NUMBER)==2) {
				rootView = inflater.inflate(R.layout.fragment_search_results, container, false);
				
				
				if (c!=null){
					result_cost = (TextView)rootView.findViewById(R.id.search_store);
					result_name = (TextView)rootView.findViewById(R.id.search_store);
					result_category = (TextView)rootView.findViewById(R.id.search_store);
					result_price = (TextView)rootView.findViewById(R.id.search_store);
					result_date = (TextView)rootView.findViewById(R.id.search_store);
					result_store = (TextView)rootView.findViewById(R.id.search_store);
					result_user = (TextView)rootView.findViewById(R.id.search_store);
					diagrams = (Button)rootView.findViewById(R.id.reset);;
					diagrams.setOnClickListener(this);
				}
				else {
					
				}
			}
			//If the screen the user sees is the search diagrams screen
			else {
				rootView = inflater.inflate(R.layout.fragment_search_diagrams, container, false);
				//dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
				//dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
			}
			
			return rootView;
		}

		@Override
		public void onClick(View v) {
			if (v instanceof Button) {
				try {
					if (submit.getId() == ((Button)v).getId()) {

						if (product_name.getText().toString().equals("") && search_store.getText().toString().equals("") && 
							min_cost.getText().toString().equals("") && max_cost.getText().toString().equals("") &&
							search_start_date.getText().toString().equals("") && search_end_date.getText().toString().equals("") && 
							category_spinner.getSelectedItem().toString().equals(this.getString(R.string.category_prompt)) &&
							family_spinner.getSelectedItem().toString().equals(this.getString(R.string.family_prompt))) {
						
							displayError(this.getString(R.string.no_input));
						}
						else {
							if (!(product_name.getText().toString().equals("")))
								product = product_name.getText().toString();
							
							if (!(category_spinner.getSelectedItem().toString().equals(this.getString(R.string.category_prompt))))
								category = category_spinner.getSelectedItem().toString();
							
							if (!(min_cost.getText().toString().equals(""))) {
								String product_min_cost =  min_cost.getText().toString();
								mn_cost = Float.parseFloat(product_min_cost);
							}
							
							if (!(max_cost.getText().toString().equals(""))) {
								String product_max_cost =  max_cost.getText().toString();
								mx_cost = Float.parseFloat(product_max_cost);
							}
							
							if (!(search_start_date.getText().toString().equals("")))
								start_date = search_start_date.getText().toString();
							
							if (!(search_end_date.getText().toString().equals("")))
								end_date = search_end_date.getText().toString();
							
							if (!(search_store.getText().toString().equals("")))
								store = search_store.getText().toString();
							
							if (!(family_spinner.getSelectedItem().toString().equals(this.getString(R.string.family_prompt))))
								family = family_spinner.getSelectedItem().toString();
							
							if (!(group_by_spinner.getSelectedItem().toString().equals(this.getString(R.string.group_by_prompt))))
								group_by = group_by_spinner.getSelectedItem().toString();
							
							searchHandler = new SearchHandler(this.getActivity());
							c = searchHandler.getSearchResults(product, category, mn_cost, mx_cost, start_date, end_date, store, family, group_by);
							
							searchHandler.getSearchFeedReaderDbHelper().close();
							
							rootView = inflater.inflate(R.layout.fragment_search_results, container, false);
						}
					}
					else if (reset.getId() == ((Button)v).getId()){
						clearFields();
					}
					else {
						rootView = inflater.inflate(R.layout.fragment_search_diagrams, container, false);
					}
				} catch (NumberFormatException e) {
					
					displayError(this.getString(R.string.input_error));
				}
			}
			else {
				dateFragment = new DatePickerFragment();
				dateFragment.setView(v);
		    	dateFragment.show(getFragmentManager(), "datePicker");
			}
			
		}
		
		public void displayError(String message) {
			InputErrorDialogFragment errorDialog = new InputErrorDialogFragment();
			errorDialog.setMessage(message);
			errorDialog.show(getFragmentManager(), "errorDialog");
		}
		
		public void clearFields() {
			category_spinner.setSelection(0);
			family_spinner.setSelection(0);
			group_by_spinner.setSelection(0);
			product_name.getText().clear();
			min_cost.getText().clear();
			max_cost.getText().clear();
			search_start_date.getText().clear();
			search_end_date.getText().clear();
			search_store.getText().clear();
		}
	}

}
