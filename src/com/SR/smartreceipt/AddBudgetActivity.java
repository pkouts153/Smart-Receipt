package com.SR.smartreceipt;

import java.lang.reflect.Field;

import com.SR.data.Budget;
import com.SR.data.Category;
import com.SR.data.FeedReaderDbHelper;
import com.SR.data.FeedReaderContract.FeedUser;
import com.SR.data.User;
import com.SR.data.FeedReaderContract.FeedCategory;
import com.SR.processes.MyApplication;

import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

/**
* Activity that displays the budget addition screen
* 
* @author Παναγιώτης Κουτσαυτίκης 8100062
*/
public class AddBudgetActivity extends FragmentActivity implements OnClickListener {

	// UI components
	
	Spinner category_spinner;
	EditText spend_limit;
	EditText from_date;
	EditText until_date;
    CheckBox same_on;
    Spinner family_spinner;
    Button submit;
    Button reset;
    
    // data variables
    
    Category category;
    Budget budget;
    User user;
    
    FeedReaderDbHelper mDbHelper;
	SQLiteDatabase db;
	
    Cursor category_cursor;
    Cursor family_cursor;
    
    /**
     *  Fragment for date selection
     */
    DatePickerFragment dateFragment = new DatePickerFragment();
    
    // shows which Activity called AddBudgetActivity (MainActivity or BudgetListActivity)
    String parent_activity;
    
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_budget);
		
		mDbHelper = new FeedReaderDbHelper(this);
		db = mDbHelper.getWritableDatabase();
		
		Intent intent = getIntent();
        parent_activity = intent.getStringExtra("Activity");
        
        // set up the UP button in ActionBar and Overflow menu
		setupActionBar();
		getOverflowMenu();
		
		// if AddBudgetActivity is called from BudgetListActivity and the user deleted all the budgets
		if (intent.getStringExtra("Success")!=null)
			displaySuccess(this.getString(R.string.delete_success));
		
		//set up UI components
		
		spend_limit = (EditText)findViewById(R.id.spend_limit);
		from_date = (EditText)findViewById(R.id.from_date);
		from_date.setOnClickListener(this);
		
		until_date = (EditText)findViewById(R.id.until_date);
		until_date.setOnClickListener(this);
		
        same_on = (CheckBox)findViewById(R.id.same_on);
        
        submit = (Button)findViewById(R.id.submit);
        submit.setOnClickListener(this);
        
        reset = (Button)findViewById(R.id.reset);
        reset.setOnClickListener(this);
        
        //set up category spinner
        
        category = new Category(db);
        category_cursor = category.getCategories();
		
		category_spinner = (Spinner) findViewById(R.id.category_spinner);
		ArrayAdapter <CharSequence> cat_adapter = new ArrayAdapter <CharSequence> (this, android.R.layout.simple_spinner_item );
		category_spinner.setAdapter(cat_adapter);
		cat_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		cat_adapter.add(this.getString(R.string.category_prompt));
		
        try{
        	category_cursor.moveToFirst();
			String category_name;
			
			while (!category_cursor.isAfterLast ()) {
				category_name = category_cursor.getString(category_cursor.getColumnIndexOrThrow(FeedCategory.NAME));
				cat_adapter.add(category_name);
				category_cursor.moveToNext ();
			}
			category_cursor.close();
        } catch (CursorIndexOutOfBoundsException e){
        	category_cursor.close();
        }
        
        //set up family spinner
        
        ArrayAdapter <CharSequence> fam_adapter = new ArrayAdapter <CharSequence> (this, android.R.layout.simple_spinner_item );
		fam_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		family_spinner = (Spinner) findViewById(R.id.family_spinner);
		family_spinner.setAdapter(fam_adapter);
		
		fam_adapter.add(this.getString(R.string.family_prompt));
		
		user = new User(db);
		family_cursor = user.getFamilyMembers(User.USER_ID);
		
		// if there are more than one family members, add the choice "All" to the spinner
		if(family_cursor.getCount()>1)
			fam_adapter.add("All");
		
        try{
        	family_cursor.moveToFirst();
			String family_member;

			while (!family_cursor.isAfterLast ()) {
				family_member = family_cursor.getString(family_cursor.getColumnIndexOrThrow(FeedUser.USERNAME));
				fam_adapter.add(family_member);
				family_cursor.moveToNext ();
			}
		} catch (CursorIndexOutOfBoundsException e){
			fam_adapter.add("No family");
	    }
        family_spinner.setSelection(0);
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
		getMenuInflater().inflate(R.menu.activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * Handle presses on the action bar items
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.action_search:
	    		Intent intent = new Intent(this, SearchActivity.class);
	    		startActivity(intent);
	            return true;
	        case R.id.action_logout:
	        	user.userLogout(this);
	        	mDbHelper.close();
	        	Intent intent2 = new Intent(this, LoginActivity.class);
	    		startActivity(intent2);
	    	
	            return true;
			case android.R.id.home:
				// This action represents the Home or Up button which leads the user to the previous screen
				Intent upIntent;
				if (parent_activity.equals("Main"))
					upIntent = new Intent(this, MainActivity.class);
				else
					upIntent = new Intent(this, BudgetListActivity.class);
				NavUtils.navigateUpTo(this, upIntent);
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
			// if the clicked button is submit
			if (submit.getId() == ((Button)v).getId()) {
				try{
					
					// get user input
					String cat_spinner = category_spinner.getSelectedItem().toString();
					String s_limit = spend_limit.getText().toString();
					String fd = from_date.getText().toString();
					String ud = until_date.getText().toString();
					
					// if any field is empty display an error dialog
					if (cat_spinner.equals(this.getString(R.string.category_prompt)) || s_limit.equals("") || fd.equals("") || ud.equals("")) {
						displayError(this.getString(R.string.no_input));
					}
					else {
						Float limit= Float.parseFloat(s_limit);
						
						mDbHelper = new FeedReaderDbHelper(this);
						db = mDbHelper.getWritableDatabase();
						
						budget = new Budget(db);
						
						// if a budget is not saved successfully it will be true
						boolean save_error = false;
						
						// if the user checked the "same on" checkbox 
						// then get the selected family member(s) and save the same budget to them
						if (same_on.isChecked()) {
							String fam_spinner = family_spinner.getSelectedItem().toString();
						
							// if the selection is "All" save the budget to all the user's family members
							if (fam_spinner.equals("All")) {
								family_cursor.moveToFirst();
								while (!family_cursor.isAfterLast()) {
									//get the id for each family member's username
									int id = user.getId(family_cursor.getString(family_cursor.getColumnIndexOrThrow(FeedUser.USERNAME)));
									
									//save the budget to each family member
									if (id!=0) 
										if (!budget.saveBudget(cat_spinner, limit, fd, ud, id, User.USER_ID))
											save_error=true;
								
									family_cursor.moveToNext();
								}
							}
							//else if the user selects a certain family member
							else {
								int id = user.getId(fam_spinner);
								
								//save the budget to the family member
								if (id!=0)
									if (!budget.saveBudget(cat_spinner, limit, fd, ud, id, User.USER_ID))
											save_error=true;
							}
						}
						
						//save the budget to the user
						if (!budget.saveBudget(cat_spinner, limit, fd, ud, User.USER_ID, 0))
							save_error=true;
						
						mDbHelper.close();
						clearFields();
						
						if (save_error)
							displayError(this.getString(R.string.budget_save_error));
						else
							displaySuccess(this.getString(R.string.success));
					}
				// if an error occurs during the parsing of the price to float
				} catch (NumberFormatException e) {
					displayError(this.getString(R.string.not_a_number));
					
				} catch (NullPointerException e) {
					displayError(this.getString(R.string.no_family));
				}
			}
			//else if the button that was clicked was reset
			else {
				clearFields();
			}
				
		}
		//else if the view that was clicked was an EditText (start date, end date), display the DatePickerDialog
		else {
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
	 * Display a success dialog
	 * 
	 * @param message  the message to be displayed in the dialog
	 */
	public void displaySuccess(String message) {
		SuccessDialogFragment successDialog = new SuccessDialogFragment();
		successDialog.setMessage(message);
		successDialog.show(getFragmentManager(), "successDialog");
		// dismiss dialog after 1,5 seconds
		timerDelayRemoveDialog(1500, successDialog);
	}
	
	/**
	 * Set the time delay of the dialog
	 */
	public void timerDelayRemoveDialog(long time, final SuccessDialogFragment d){
	    new Handler().postDelayed(new Runnable() {
	        public void run() {                
	            d.dismiss();         
	        }
	    }, time); 
	}
	
	/**
	 * Clear the fields from user's input
	 */
	public void clearFields() {
		category_spinner.setSelection(0);
		spend_limit.getText().clear();
		from_date.getText().clear();
		until_date.getText().clear();
		if (same_on.isChecked()) {
			same_on.setChecked(false);
	    }
		family_spinner.setSelection(0);
		
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
