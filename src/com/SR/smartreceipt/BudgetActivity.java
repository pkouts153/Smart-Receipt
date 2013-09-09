package com.SR.smartreceipt;

import com.SR.data.FeedReaderContract.FeedBudget;
import com.SR.data.FeedReaderContract.FeedCategory;
import com.SR.data.FeedReaderDbHelper;

import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.support.v4.app.NavUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

public class BudgetActivity extends Activity implements OnItemSelectedListener,  OnClickListener {

	Spinner category_spinner;
	EditText spend_limit;
	EditText from_date;
	EditText until_date;
    CheckBox same_on;
    Spinner family_spinner;
    CheckBox notify;
    Button submit;
    Button cancel;
    
    FeedReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    
    DatePickerFragment dateFragment = new DatePickerFragment();
    
    String dateClicked;
    
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_budget);
		// Show the Up button in the action bar.
		setupActionBar();
		
		spend_limit = (EditText)findViewById(R.id.spend_limit);
		from_date = (EditText)findViewById(R.id.from_date);
		from_date.setOnClickListener(this);
		
		until_date = (EditText)findViewById(R.id.until_date);
		until_date.setOnClickListener(this);
		
        same_on = (CheckBox)findViewById(R.id.same_on);
        notify = (CheckBox)findViewById(R.id.notify);
        
        submit = (Button)findViewById(R.id.submit);
        submit.setOnClickListener(this);
        
        cancel = (Button)findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        
		mDbHelper = new FeedReaderDbHelper(this);
		
		// Gets the data repository in write mode
		db = mDbHelper.getWritableDatabase();

		// Specifies which columns are needed from the database
		String[] projection = {
			FeedCategory.NAME
		    };
		
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
		category_spinner = (Spinner) findViewById(R.id.category_spinner);
		category_spinner.setAdapter(adapter);
		
		while (!c.isLast ()) {
			c.moveToNext ();
			category_name = c.getString(c.getColumnIndexOrThrow(FeedCategory.NAME));
			adapter.add(category_name);
		}
		c.close();

		//configure family spinner
		Spinner family_spinner = (Spinner) findViewById(R.id.family_spinner);
		family_spinner.setOnItemSelectedListener(this);
		
		ArrayAdapter<CharSequence> family_adapter = ArrayAdapter.createFromResource(this, R.array.family_array, android.R.layout.simple_spinner_item);
		family_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		family_spinner.setAdapter(family_adapter);
	}
	
	
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        parent.getItemAtPosition(pos);
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


	@Override
	public void onClick(View v) {
		if (v instanceof Button) {
			if (submit.getId() == ((Button)v).getId()) {
				try{
					mDbHelper = new FeedReaderDbHelper(this);
			    	
					// Gets the data repository in write mode
					db = mDbHelper.getWritableDatabase();
					
					String cat_spinner = category_spinner.getSelectedItem().toString();
					
					String s_limit = spend_limit.getText().toString();
					Float limit= Float.parseFloat(s_limit);
					
					String fd = from_date.getText().toString();
					String ud = until_date.getText().toString();
					
					int n;
					if (notify.isChecked())
						n = 1;
					else
						n = 0;
					
					if ((fd.equals("")) || (ud.equals(""))) {
						InputErrorDialogFragment errorDialog = new InputErrorDialogFragment();
						errorDialog.show(getFragmentManager(), "Dialog");
					}
					else {
						
						// Create a new map of values, where column names are the keys
						ContentValues values = new ContentValues();
						values.put(FeedBudget.EXPENSE_CATEGORY, cat_spinner);
						values.put(FeedBudget.SPEND_LIMIT, limit);
						values.put(FeedBudget.START_DATE, fd);
						values.put(FeedBudget.END_DATE, ud);
						values.put(FeedBudget.NOTIFICATION, n);
						//values.put(FeedBudget.USER, 0);
						
						db.insert(FeedBudget.TABLE_NAME, "null", values);
					}
				
				} catch (NumberFormatException e) {
					InputErrorDialogFragment errorDialog = new InputErrorDialogFragment();
					errorDialog.show(getFragmentManager(), "errorDialog");
				}
			}
			else {
				spend_limit.getText().clear();
				from_date.getText().clear();
				until_date.getText().clear();
				if (notify.isChecked()) {
					notify.setChecked(false);
			     }
			}
				
		}
		else {
			dateFragment.setView(v);
	    	dateFragment.show(getFragmentManager(), "datePicker");
		}

	}

}
