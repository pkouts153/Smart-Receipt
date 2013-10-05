package com.SR.smartreceipt;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.SR.data.Category;
import com.SR.data.FeedReaderDbHelper;
import com.SR.data.SearchHandler;
import com.SR.data.User;
import com.SR.data.FeedReaderContract.FeedCategory;
import com.SR.data.FeedReaderContract.FeedUser;
import com.SR.processes.MyApplication;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
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

/**
* Activity that displays the search screen
* 
* @author Παναγιώτης Κουτσαυτίκης 8100062
*/
public class SearchActivity extends FragmentActivity implements OnClickListener{
	
	//User's input
	
	String product = "";
	String category = "";
	String mn_cost = "";
	String mx_cost = "";
	String start_date = "";
	String end_date = "";
	String store = "";
	String family = "";
	String group_by = "";
	
	//UI components
	
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
	
	//data variables
	
	User user;
	Category cat;
	SearchHandler searchHandler;
	
	FeedReaderDbHelper mDbHelper;
	SQLiteDatabase db;

	Bundle extras;

    /**
     *  Fragment for date selection
     */
	DatePickerFragment dateFragment;

	// search results without separation of the groups
	Cursor general_results;
	
	// the costs of the groups
	Cursor costs;
	
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
		setContentView(R.layout.activity_search);
		// set up the UP button in ActionBar and Overflow menu
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
		
		mDbHelper = new FeedReaderDbHelper(this);
		db = mDbHelper.getWritableDatabase();
		
		cat = new Category(db);
		Cursor c = cat.getCategories();
		
		category_spinner = (Spinner) findViewById(R.id.category_spinner);
		ArrayAdapter <CharSequence> cat_adapter = new ArrayAdapter <CharSequence> (this, android.R.layout.simple_spinner_item );
		category_spinner.setAdapter(cat_adapter);
		cat_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		cat_adapter.add(this.getString(R.string.category_prompt));
		
        try{
			c.moveToFirst();
			String category_name;;
			
			while (!c.isAfterLast ()) {
				category_name = c.getString(c.getColumnIndexOrThrow(FeedCategory.NAME));
				cat_adapter.add(category_name);
				c.moveToNext ();
			}
			c.close();
        } catch (CursorIndexOutOfBoundsException e){
        	c.close();
        }
		
        //set up family spinner
        
        ArrayAdapter <CharSequence> fam_adapter = new ArrayAdapter <CharSequence> (this, android.R.layout.simple_spinner_item );
		fam_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		family_spinner = (Spinner)findViewById(R.id.family_spinner);
		family_spinner.setAdapter(fam_adapter);
		
		fam_adapter.add(this.getString(R.string.family_prompt));
		
		user = new User(db);
		Cursor c1 = user.getFamilyMembers(User.USER_ID);
		
		// if there are at least one family members, add the choice "All" to the spinner, 
		// which will include the user and the family members
		if(c1.getCount()>0)
			fam_adapter.add("All");
		
        try{
        	c1.moveToFirst();
			String family_member;

			while (!c1.isAfterLast ()) {
				family_member = c1.getString(c1.getColumnIndexOrThrow(FeedUser.USERNAME));
				fam_adapter.add(family_member);
				c1.moveToNext ();
			}
			c1.close();
		} catch (CursorIndexOutOfBoundsException e){
			fam_adapter.add("No family");
			c1.close();
	    }
        
        mDbHelper.close();
        
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
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.search, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * Handle presses on the action bar items
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_logout:
	        	user.userLogout(this);
	        	Intent intent2 = new Intent(this, LoginActivity.class);
	    		startActivity(intent2);
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
	public void onClick(View v) {
		if (v instanceof Button) {
			try {
				// if the clicked button is submit
				if (submit.getId() == ((Button)v).getId()) {

					// if all the fields are empty, display an error dialog
					if (product_name.getText().toString().equals("") && search_store.getText().toString().equals("") && 
						min_cost.getText().toString().equals("") && max_cost.getText().toString().equals("") &&
						search_start_date.getText().toString().equals("") && search_end_date.getText().toString().equals("") && 
						category_spinner.getSelectedItem().toString().equals(this.getString(R.string.category_prompt)) &&
						family_spinner.getSelectedItem().toString().equals(this.getString(R.string.family_prompt))) {
					
						displayError(this.getString(R.string.missing_input));
					}
					else {
						
						// get the input of the user where there is one
						if (!(product_name.getText().toString().equals(""))){
							product = product_name.getText().toString();
						}
						
						if (!(category_spinner.getSelectedItem().toString().equals(this.getString(R.string.category_prompt)))){
							category = category_spinner.getSelectedItem().toString();
						}
						
						if (!(min_cost.getText().toString().equals(""))) {
							mn_cost =  min_cost.getText().toString();
							// parses float to see if there is an error
							Float m_cost = Float.parseFloat(mn_cost);
						}
						
						if (!(max_cost.getText().toString().equals(""))) {
							mx_cost =  max_cost.getText().toString();
							// parses float to see if there is an error
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
						
						// depending on the selection of the user, change the selection to a form readable by the database
						if (!(group_by_spinner.getSelectedItem().toString().equals(this.getString(R.string.group_by_prompt)))){
							group_by = group_by_spinner.getSelectedItem().toString();
							
							if (group_by.equals("Category"))
								group_by = "product_category";
							else if (group_by.equals("Store"))
								group_by = "store_name";
							else
								group_by = "username";
						}
						
						
						mDbHelper = new FeedReaderDbHelper(this);
						db = mDbHelper.getWritableDatabase();
						
						//get the product results for these data
						searchHandler = new SearchHandler(db);
						general_results = searchHandler.getSearchResults(product, category, mn_cost, mx_cost, start_date, end_date, store, family, group_by, null);
						costs = searchHandler.getSums();
						
						group_names = new ArrayList<String>();
						group_cost = new ArrayList<String>();
						
						// if the user has selected a group_by, then get the group_names for this group_by
						if (!group_by.equals("") && general_results.getCount()>0){
							
							general_results.moveToFirst();
							
							String group_name = general_results.getString(general_results.getColumnIndexOrThrow(group_by));
							String group_name1 = general_results.getString(general_results.getColumnIndexOrThrow(group_by));
							
							group_names.add(group_name);
							
							if (general_results.moveToNext()){
							
								while (!general_results.isAfterLast()){
									group_name1 = general_results.getString(general_results.getColumnIndexOrThrow(group_by));
									if (!group_name1.equals(group_name)) {
										group_name = general_results.getString(general_results.getColumnIndexOrThrow(group_by));
										group_names.add(group_name);
									}
									general_results.moveToNext();
								}
							}
						}
						
						// get the total cost for each group or the total cost if there is no group_by
						if (costs.moveToFirst()){
							
							while (!costs.isAfterLast()){
								group_cost.add(costs.getString(costs.getColumnIndexOrThrow("sum")));
								costs.moveToNext();
							}
						}
						
						group_names.trimToSize();
						group_cost.trimToSize();

						// add the input data into a Bundle and call SearchResultsActivity
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
						extras.putStringArrayList("group_names", group_names);
						extras.putStringArrayList("group_cost", group_cost);
						
						if (group_by.equals("") || group_names.size()==0){
							Intent intent = new Intent(this, SearchResultsActivity.class);
							extras.putString("activity", "search");
							intent.putExtras(extras);
							startActivity(intent);
						}
						else {
							Intent intent = new Intent(this, ChartActivity.class);
							intent.putExtras(extras);
							startActivity(intent);
						}
						
					}
				}
				//else if the button that was clicked was reset
				else{
					clearFields();
				}
			// if an error occurs during the parsing of the price to float
			} catch (NumberFormatException e) {
				
				displayError(this.getString(R.string.not_a_number));
			}
		}
		//else if the view that was clicked was an EditText (start date, end date), display the DatePickerDialog
		else {
			dateFragment = new DatePickerFragment();
			dateFragment.setView(v);
	    	dateFragment.show(getSupportFragmentManager(), "datePicker");
		}
		
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
	 * Clear the fields from user's input
	 */
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
    
}
