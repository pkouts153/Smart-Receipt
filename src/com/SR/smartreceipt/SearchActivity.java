package com.SR.smartreceipt;

import java.lang.reflect.Field;

import com.SR.data.Category;
import com.SR.data.SearchHandler;
import com.SR.data.User;
import com.SR.data.FeedReaderContract.FeedCategory;
import com.SR.data.FeedReaderContract.FeedUser;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;

public class SearchActivity extends FragmentActivity implements OnClickListener{
	
	//Search selection input
	
	String product = "";
	String category = "";
	String mn_cost = "";
	String mx_cost = "";
	String start_date = "";
	String end_date = "";
	String store = "";
	String family = "";
	String group_by = "";
	
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
	Category cat;
	
	DatePickerFragment dateFragment;
	
	SearchHandler searchHandler;
	
	Bundle extras;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		// Show the Up button in the action bar.
		setupActionBar();
		getOverflowMenu();
		//set up EditTexts
		
		product_name = (EditText)findViewById(R.id.product_name);
		
		min_cost = (EditText)findViewById(R.id.min_cost);
		max_cost = (EditText)findViewById(R.id.max_cost);
		
		search_start_date = (EditText)findViewById(R.id.search_start_date);
		search_start_date.setOnClickListener(this);
		
		search_end_date = (EditText)findViewById(R.id.search_end_date);
		search_end_date.setOnClickListener(this);
		
		search_store = (EditText)findViewById(R.id.search_store);
		
		//set up category spinner
		
		cat = new Category(this);
		Cursor c = cat.getCategories();
		
		category_spinner = (Spinner) findViewById(R.id.category_spinner);
		ArrayAdapter <CharSequence> cat_adapter = new ArrayAdapter <CharSequence> (this, android.R.layout.simple_spinner_item );
		category_spinner.setAdapter(cat_adapter);
		cat_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		cat_adapter.add(this.getString(R.string.category_prompt));
		
        try{
        	
			c.moveToFirst();
			String category_name = c.getString(c.getColumnIndexOrThrow(FeedCategory.NAME));
			
			cat_adapter.add(category_name);
			
			while (!c.isLast ()) {
				c.moveToNext ();
				category_name = c.getString(c.getColumnIndexOrThrow(FeedCategory.NAME));
				cat_adapter.add(category_name);
			}
			c.close();
			cat.getCatFeedReaderDbHelper().close();
			
        } catch (CursorIndexOutOfBoundsException e){
        	c.close();
        	cat.getCatFeedReaderDbHelper().close();
        }
		
        //set up family spinner
        
        ArrayAdapter <CharSequence> fam_adapter = new ArrayAdapter <CharSequence> (this, android.R.layout.simple_spinner_item );
		fam_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		family_spinner = (Spinner)findViewById(R.id.family_spinner);
		family_spinner.setAdapter(fam_adapter);
		
		fam_adapter.add(this.getString(R.string.family_prompt));
		fam_adapter.add("All");
		
		user = new User(this);
		Cursor c1 = user.getFamilyMembers(User.USER_ID);
		
        try{
	        
        	c1.moveToFirst();
			String family_member = c1.getString(c1.getColumnIndexOrThrow(FeedUser.USERNAME));

			fam_adapter.add(family_member);

			while (!c1.isLast ()) {
				c1.moveToNext ();
				family_member = c1.getString(c1.getColumnIndexOrThrow(FeedUser.USERNAME));
				fam_adapter.add(family_member);
			}
			c1.close();
			user.getUserFeedReaderDbHelper().close();
			
		} catch (CursorIndexOutOfBoundsException e){
			fam_adapter.add("No family");
			c1.close();
			user.getUserFeedReaderDbHelper().close();
	    }
        
        //set up group_by spinner
        
        group_by_spinner = (Spinner)findViewById(R.id.group_by_spinner);
		
		ArrayAdapter<CharSequence> group_by_adapter = ArrayAdapter.createFromResource(this, R.array.group_by_array, android.R.layout.simple_spinner_item);
		group_by_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		group_by_spinner.setAdapter(group_by_adapter);
		
		//set up buttons
		
        submit = (Button)findViewById(R.id.submit);
        submit.setOnClickListener(this);
        
        reset = (Button)findViewById(R.id.reset);
        reset.setOnClickListener(this);
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
	        	user.userLogout();
	        	Intent intent2 = new Intent(this, LoginActivity.class);
	    		startActivity(intent2);
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
	public void onClick(View v) {
		if (v instanceof Button) {
			try {
				if (submit.getId() == ((Button)v).getId()) {

					if (product_name.getText().toString().equals("") && search_store.getText().toString().equals("") && 
						min_cost.getText().toString().equals("") && max_cost.getText().toString().equals("") &&
						search_start_date.getText().toString().equals("") && search_end_date.getText().toString().equals("") && 
						category_spinner.getSelectedItem().toString().equals(this.getString(R.string.category_prompt)) &&
						family_spinner.getSelectedItem().toString().equals(this.getString(R.string.family_prompt))) {
					
						displayError(this.getString(R.string.missing_input));
					}
					else {
						
						if (!(product_name.getText().toString().equals(""))){
							product = product_name.getText().toString();
						}
						
						if (!(category_spinner.getSelectedItem().toString().equals(this.getString(R.string.category_prompt)))){
							category = category_spinner.getSelectedItem().toString();
						}
						
						if (!(min_cost.getText().toString().equals(""))) {
							mn_cost =  min_cost.getText().toString();
							Float m_cost = Float.parseFloat(mn_cost);
						}
						
						if (!(max_cost.getText().toString().equals(""))) {
							mx_cost =  max_cost.getText().toString();
							Float x_cost = Float.parseFloat(mx_cost);
						}
						
						if (!(search_start_date.getText().toString().equals(""))){
							start_date = search_start_date.getText().toString();
						}
						
						if (!(search_end_date.getText().toString().equals(""))){
							end_date = search_end_date.getText().toString();
						}
						
						if (!(search_store.getText().toString().equals(""))){
							store = search_store.getText().toString();
						}
						
						if (!(family_spinner.getSelectedItem().toString().equals(this.getString(R.string.family_prompt)))){
							family = family_spinner.getSelectedItem().toString();
						}
						
						if (!(group_by_spinner.getSelectedItem().toString().equals(this.getString(R.string.group_by_prompt)))){
							group_by = group_by_spinner.getSelectedItem().toString();
							
							if (group_by.equals("Category"))
								group_by = "product_category";
							else if (group_by.equals("Store"))
								group_by = "store_name";
							else
								group_by = "username";
						}
						
						extras = new Bundle();
						extras.putString("product", product);
						extras.putString("category", category);
						extras.putString("mn_cost", mn_cost);
						extras.putString("mx_cost", mx_cost);
						extras.putString("start_date", start_date);
						extras.putString("end_date", end_date);
						extras.putString("store", store);
						extras.putString("family", family);
						extras.putString("group_by", group_by);
						
						Intent intent = new Intent(this, SearchResultsActivity.class);
						intent.putExtras(extras);
						startActivity(intent);
					}
				}
				else{
					clearFields();
				}
			} catch (NumberFormatException e) {
				
				displayError(this.getString(R.string.not_a_number));
			}
		}
		else {
			dateFragment = new DatePickerFragment();
			dateFragment.setView(v);
	    	dateFragment.show(getSupportFragmentManager(), "datePicker");
		}
		
	}
	
	public void displayError(String message) {
		InputErrorDialogFragment errorDialog = new InputErrorDialogFragment();
		errorDialog.setMessage(message);
		errorDialog.show(getSupportFragmentManager(), "errorDialog");
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
