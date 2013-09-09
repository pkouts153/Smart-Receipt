package com.SR.smartreceipt;

import com.SR.data.FeedReaderContract.FeedProduct;
import com.SR.data.FeedReaderDbHelper;
import com.SR.data.FeedReaderContract.FeedBudget;
import com.SR.data.FeedReaderContract.FeedCategory;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class SaveActivity extends Activity implements OnClickListener {
	
	Spinner category_spinner;
	EditText product_name;
	EditText price;
	EditText purchase_date;
    Button save;
    Button scan;
    
    FeedReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    
    DatePickerFragment dateFragment = new DatePickerFragment();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_save);
		// Show the Up button in the action bar.
		setupActionBar();
		
		product_name = (EditText)findViewById(R.id.product_name);
		price = (EditText)findViewById(R.id.price);
		
		purchase_date = (EditText)findViewById(R.id.purchase_date);
		purchase_date.setOnClickListener(this);
        
		save = (Button)findViewById(R.id.save_button);
		save.setOnClickListener(this);
        
		scan = (Button)findViewById(R.id.scan_button);
		scan.setOnClickListener(this);
		
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
		getMenuInflater().inflate(R.menu.save, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v instanceof Button) {
			if (save.getId() == ((Button)v).getId()) {
				try{
					mDbHelper = new FeedReaderDbHelper(this);
			    	
					// Gets the data repository in write mode
					db = mDbHelper.getWritableDatabase();
					
					String cat_spinner = category_spinner.getSelectedItem().toString();
					
					String p_name = product_name.getText().toString();
					
					String p_price = price.getText().toString();
					Float p = Float.parseFloat(p_price);
					
					String pd = purchase_date.getText().toString();
					
					if ((p_name.equals("")) || (pd.equals(""))) {
						InputErrorDialogFragment errorDialog = new InputErrorDialogFragment();
						errorDialog.show(getFragmentManager(), "Dialog");
					}
					else {
						
						// Create a new map of values, where column names are the keys
						ContentValues values = new ContentValues();
						values.put(FeedProduct.PRODUCT_CATEGORY, cat_spinner);
						values.put(FeedProduct.NAME, p_name);
						values.put(FeedProduct.PRICE, p);
						values.put(FeedProduct.PURCHASE_DATE, pd);
						
						db.insert(FeedProduct.TABLE_NAME, "null", values);
					}
				
				} catch (NumberFormatException e) {
					InputErrorDialogFragment errorDialog = new InputErrorDialogFragment();
					errorDialog.show(getFragmentManager(), "errorDialog");
				}
			}
			else {
				product_name.getText().clear();
				price.getText().clear();
				purchase_date.getText().clear();
			}
		}
		else {
			dateFragment.setView(v);
	    	dateFragment.show(getFragmentManager(), "datePicker");
		}

	}
}
