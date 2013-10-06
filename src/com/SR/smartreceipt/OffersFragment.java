package com.SR.smartreceipt;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.SR.data.FeedReaderContract.FeedOffer;
import com.SR.data.FeedReaderDbHelper;
import com.SR.data.Offer;
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
import android.util.Log;
import android.view.LayoutInflater;
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
public class OffersFragment extends Fragment{

	public OffersFragment() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * The PagerAdapter that will provide fragments for each of the sections. 
	 * We use a FragmentPagerAdapter derivative, which will keep every loaded fragment in memory
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The ViewPager that will host the section contents.
	 */
	ViewPager mViewPager;
	
	//data variables
	User user;
	Offer offer;
	
	FeedReaderDbHelper mDbHelper;
	SQLiteDatabase db;
	
	// object for accessing data passed from previous activity 
	Bundle extras;

	Cursor c;
	
	public static final String ARG_SECTION_NUMBER = "section_number";
	
	int section_number;
	
	ArrayList<String> group_names;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_offers, container, false);
		
	}
	
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
	    super.onViewCreated(view, savedInstanceState);
	    
	    mDbHelper = new FeedReaderDbHelper(getActivity());
		db = mDbHelper.getWritableDatabase();
		
		offer = new Offer(db);
		
		extras = getArguments();
		
		section_number = extras.getInt(ARG_SECTION_NUMBER);

		if (section_number==1)
			c = offer.getOffersByCategory(null);
		else
			c = offer.getOffersByStore(null);
		
		while (c.moveToNext())
			Log.w("", "" +c.getInt(c.getColumnIndexOrThrow(FeedOffer._ID)));
		
		List<Fragment> fragments =  getFragments(c, section_number);
		
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getChildFragmentManager(), fragments);

		mViewPager = (ViewPager) view.findViewById(R.id.list_pager);

		mViewPager.setAdapter(mSectionsPagerAdapter);
    }
    
	/**
	 * Creates the fragments for the PagerAdapter
	 * each fragment represents a group's products in the results screen
	 * 
	 * @return a list of fragments to be displayed in the sections
	 */
	private List<Fragment> getFragments(Cursor cursor, int section){
		
		List<Fragment> fList = new ArrayList<Fragment>();
		
		//Bundle args = new Bundle();
		
		group_names = new ArrayList<String>();
		
		String group_by;
		
		if (section==1)
			group_by = "category";
		else
			group_by = "store_name";
		
		cursor.moveToFirst();
		
		String group_name = cursor.getString(cursor.getColumnIndexOrThrow(group_by));
		String group_name1 = cursor.getString(cursor.getColumnIndexOrThrow(group_by));
		
		group_names.add(group_name);
		
		if (cursor.moveToNext()){
				
			while (!cursor.isAfterLast()){
				group_name1 = cursor.getString(cursor.getColumnIndexOrThrow(group_by));
				if (!group_name1.equals(group_name)) {
					group_name = cursor.getString(cursor.getColumnIndexOrThrow(group_by));
					group_names.add(group_name);
				}
				cursor.moveToNext();
			}
		}
		
		Cursor c;
		// for each of the search groups create a SearchResultsListFragment
		for (int i=0; i<group_names.size(); i++){
			//args.putString("group_name", group_names.get(i));
			
			if (section==1)
				c = offer.getOffersByCategory(group_names.get(i));
			else
				c = offer.getOffersByStore(group_names.get(i));
			
			fList.add(OffersListFragment.newInstance(section_number, c, getActivity(), (ViewGroup) getActivity().findViewById(R.id.offers_results)));
		}
		
		return fList;
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

}
