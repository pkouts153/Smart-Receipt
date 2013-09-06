package com.SR.smartreceipt;

import com.SR.data.FeedReaderConract.FeedCategory;
import com.SR.data.FeedReaderDbHelper;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.support.v4.app.NavUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

public class BudgetActivity extends Activity implements OnItemSelectedListener {

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_budget);
		// Show the Up button in the action bar.
		setupActionBar();
		
		FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(this);
		
		// Gets the data repository in write mode
		SQLiteDatabase db = mDbHelper.getWritableDatabase();

		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection = {
			FeedCategory._ID,
		    FeedCategory.NAME
		    };

		// How you want the results sorted in the resulting Cursor
		//String sortOrder = FeedCategory.COLUMN_NAME_UPDATED + " DESC";

		Cursor c = db.query(
			FeedCategory.TABLE_NAME,  				  // The table to query
		    projection,                               // The columns to return
		    null,                                	  // The columns for the WHERE clause
		    null,                            		  // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    null                                 	  // The sort order
		    );
		
		c.moveToFirst();
		String category_name = c.getString(c.getColumnIndexOrThrow(FeedCategory.NAME));
		
		ArrayAdapter <CharSequence> adapter = new ArrayAdapter <CharSequence> (this, android.R.layout.simple_spinner_item );
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapter.add(category_name);
		Spinner s = (Spinner) findViewById(R.id.category_spinner);
		s.setAdapter(adapter);
		
		/*ArrayAdapter <CharSequence> adapter = new ArrayAdapter <CharSequence> (this, android.R.layout.simple_spinner_item );
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		adapter.add(category_name);
		
		while (!c.isLast ()) {
			c.moveToNext ();
			if (c.isAfterLast ()) {
				c.close();
				return;
			}
			category_name = c.getString(c.getColumnIndexOrThrow(FeedCategory.NAME));
			adapter.add(category_name);
			c.close();
		}
		
		Spinner category_spinner = (Spinner) findViewById(R.id.category_spinner);
		category_spinner.setOnItemSelectedListener(this);
		
		category_spinner.setAdapter(adapter);*/
		

		
		/*//configure category spinner
		Spinner category_spinner = (Spinner) findViewById(R.id.category_spinner);
		category_spinner.setOnItemSelectedListener(this);
		
		ArrayAdapter<CharSequence> category_adapter = ArrayAdapter.createFromResource(this, R.array.categories_array, android.R.layout.simple_spinner_item);
		category_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		category_spinner.setAdapter(category_adapter);*/

		//configure family spinner
		Spinner family_spinner = (Spinner) findViewById(R.id.family_spinner);
		family_spinner.setOnItemSelectedListener(this);
		
		ArrayAdapter<CharSequence> family_adapter = ArrayAdapter.createFromResource(this, R.array.family_array, android.R.layout.simple_spinner_item);
		family_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		family_spinner.setAdapter(family_adapter);
	}
	
    public void onItemSelected(AdapterView<?> parent, View view, 
            int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
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
		getMenuInflater().inflate(R.menu.budget, menu);
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
	
	/** Call database and save preferences */
	public void submitBudgetPreferences(View view) {
		
	}
}
