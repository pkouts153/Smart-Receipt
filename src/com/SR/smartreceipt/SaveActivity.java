package com.SR.smartreceipt;

import java.util.ArrayList;

import com.SR.data.FeedReaderContract.FeedProduct;
import com.SR.data.FeedReaderDbHelper;
import com.SR.processes.BudgetNotificationIntentService;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class SaveActivity extends Activity implements OnClickListener {
	
	Spinner category_spinner;
	EditText product_name;
	EditText price;
	Button add;
	EditText purchase_date;
    Button save;
    Button reset;
    Button scan;
    
    FeedReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    
    DatePickerFragment dateFragment = new DatePickerFragment();
    
    ArrayList<String> product_list = new ArrayList<String>();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_save);
		// Show the Up button in the action bar.
		setupActionBar();
		
		category_spinner = (Spinner) findViewById(R.id.category_spinner);
		//family_spinner.setOnItemSelectedListener(this);
		
		ArrayAdapter<CharSequence> category_adapter = ArrayAdapter.createFromResource(this, R.array.categories_array, android.R.layout.simple_spinner_item);
		category_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		category_spinner.setAdapter(category_adapter);
		
		product_name = (EditText)findViewById(R.id.product_name);
		price = (EditText)findViewById(R.id.price);
		
		add = (Button)findViewById(R.id.add_button);
		add.setOnClickListener(this);
		
		purchase_date = (EditText)findViewById(R.id.purchase_date);
		purchase_date.setOnClickListener(this);
        
		save = (Button)findViewById(R.id.save_button);
		save.setOnClickListener(this);
		
		reset = (Button)findViewById(R.id.reset_button);
		reset.setOnClickListener(this);
        
		scan = (Button)findViewById(R.id.scan_button);
		scan.setOnClickListener(this);
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

					String cat_spinner = category_spinner.getSelectedItem().toString();
					
					String p_name = product_name.getText().toString();
					
					String p_price = price.getText().toString();
					Float p = Float.parseFloat(p_price);
					
					String pd = purchase_date.getText().toString();
					
					if ((p_name.equals("")) || (pd.equals("")) || (cat_spinner.equals(this.getString(R.string.category_prompt)))) {
						InputErrorDialogFragment errorDialog = new InputErrorDialogFragment();
						errorDialog.show(getFragmentManager(), "Dialog");
					}
					else {
						mDbHelper = new FeedReaderDbHelper(this);
				    	
						// Gets the data repository in write mode
						db = mDbHelper.getWritableDatabase();
						
						// Create a new map of values, where column names are the keys
						ContentValues values = new ContentValues();
						values.put(FeedProduct.PRODUCT_CATEGORY, cat_spinner);
						values.put(FeedProduct.NAME, p_name);
						values.put(FeedProduct.PRICE, p);
						values.put(FeedProduct.PURCHASE_DATE, pd);
						
						db.insert(FeedProduct.TABLE_NAME, "null", values);
						
						mDbHelper.close();
						
						SuccessDialogFragment successDialog = new SuccessDialogFragment();
						successDialog.show(getFragmentManager(), "successDialog");
						timerDelayRemoveDialog(1500, successDialog);
					
					    Intent serviceIntent = new Intent(SaveActivity.this, BudgetNotificationIntentService.class);
					    SaveActivity.this.startService(serviceIntent);
					}
				
				} catch (NumberFormatException e) {
					InputErrorDialogFragment errorDialog = new InputErrorDialogFragment();
					errorDialog.show(getFragmentManager(), "errorDialog");
				}
			}
			else if (reset.getId() == ((Button)v).getId()){
				product_name.getText().clear();
				price.getText().clear();
				purchase_date.getText().clear();
			}
			else if (add.getId() == ((Button)v).getId()){
				String cat_spinner = category_spinner.getSelectedItem().toString();
				
				String p_name = product_name.getText().toString();
				
				String p_price = price.getText().toString();
				
				if ((p_name.equals("")) || (p_price.equals("")) || (cat_spinner.equals(this.getString(R.string.category_prompt)))) {
					InputErrorDialogFragment errorDialog = new InputErrorDialogFragment();
					errorDialog.show(getFragmentManager(), "Dialog");
				}
				else {
					product_list.add(cat_spinner);
					product_list.add(p_name);
					product_list.add(p_price);
				}
				
			}
			else {
				Intent intent = new Intent(this, MainActivity.class);
				startActivity(intent);
			}
		}
		else {
			dateFragment.setView(v);
	    	dateFragment.show(getFragmentManager(), "datePicker");
		}

	}
	
	public void timerDelayRemoveDialog(long time, final SuccessDialogFragment d){
	    new Handler().postDelayed(new Runnable() {
	        public void run() {                
	            d.dismiss();         
	        }
	    }, time); 
	}
	

	@Override
	protected void onStop() {
	    super.onStop();
	    
	    if (mDbHelper != null)
	    	mDbHelper.close();
	}
	
	@Override
	protected void onPause() {
	    super.onPause();
	    
	    if (mDbHelper != null)
	    	mDbHelper.close();
	}

}
