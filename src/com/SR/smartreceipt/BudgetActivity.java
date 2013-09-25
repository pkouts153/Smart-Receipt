package com.SR.smartreceipt;

import java.lang.reflect.Field;

import com.SR.data.Budget;
import com.SR.data.Category;
import com.SR.data.FeedReaderContract.FeedUser;
import com.SR.data.User;
import com.SR.data.FeedReaderContract.FeedCategory;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import android.os.Build;

public class BudgetActivity extends FragmentActivity implements OnClickListener {

	Spinner category_spinner;
	EditText spend_limit;
	EditText from_date;
	EditText until_date;
    CheckBox same_on;
    Spinner family_spinner;
    Button submit;
    Button reset;
    
    Category category;
    Budget budget;
    User user;
    
    Cursor cat;
    Cursor fam;
    
    DatePickerFragment dateFragment = new DatePickerFragment();
    
    String parent_activity;
    
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_budget);
		// Show the Up button in the action bar.
		
		Intent intent = getIntent();
        parent_activity = intent.getStringExtra("Activity");
        
		setupActionBar();
		getOverflowMenu();
		
		if (intent.getStringExtra("Success")!=null)
			displaySuccess(this.getString(R.string.delete_success));
		
		//set up ui components
		
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
        
        category = new Category(this);
		cat = category.getCategories();
		
		category_spinner = (Spinner) findViewById(R.id.category_spinner);
		ArrayAdapter <CharSequence> cat_adapter = new ArrayAdapter <CharSequence> (this, android.R.layout.simple_spinner_item );
		category_spinner.setAdapter(cat_adapter);
		cat_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		cat_adapter.add(this.getString(R.string.category_prompt));
		
        try{
        	
        	cat.moveToFirst();
			String category_name;
			
			while (!cat.isAfterLast ()) {
				category_name = cat.getString(cat.getColumnIndexOrThrow(FeedCategory.NAME));
				cat_adapter.add(category_name);
				cat.moveToNext ();
			}
			cat.close();
			category.getCatFeedReaderDbHelper().close();
			
        } catch (CursorIndexOutOfBoundsException e){
        	cat.close();
			category.getCatFeedReaderDbHelper().close();
        }
        
        
        //set up family spinner
        
        ArrayAdapter <CharSequence> fam_adapter = new ArrayAdapter <CharSequence> (this, android.R.layout.simple_spinner_item );
		fam_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		family_spinner = (Spinner) findViewById(R.id.family_spinner);
		family_spinner.setAdapter(fam_adapter);
		
		fam_adapter.add(this.getString(R.string.family_prompt));
		fam_adapter.add("All");
		
		user = new User(this);
		fam = user.getFamilyMembers(User.USER_ID);
		
        try{
        	fam.moveToFirst();
			String family_member;

			while (!fam.isAfterLast ()) {
				Log.w("", fam.getString(fam.getColumnIndexOrThrow(FeedUser.USERNAME)));
				family_member = fam.getString(fam.getColumnIndexOrThrow(FeedUser.USERNAME));
				fam_adapter.add(family_member);
				fam.moveToNext ();
			}
			//fam.close();
			//user.getUserFeedReaderDbHelper().close();
			
		} catch (CursorIndexOutOfBoundsException e){
			fam_adapter.add("No family");
			//fam.close();
			//user.getUserFeedReaderDbHelper().close();
	    }
        family_spinner.setSelection(0);
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
		getMenuInflater().inflate(R.menu.activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_search:
	    		Intent intent = new Intent(this, SearchActivity.class);
	    		startActivity(intent);
	            return true;
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
			if (submit.getId() == ((Button)v).getId()) {
				try{
					
					String cat_spinner = category_spinner.getSelectedItem().toString();
					String s_limit = spend_limit.getText().toString();
					String fd = from_date.getText().toString();
					String ud = until_date.getText().toString();
					
					if (cat_spinner.equals(this.getString(R.string.category_prompt)) || s_limit.equals("") || fd.equals("") || ud.equals("")) {
						displayError(this.getString(R.string.no_input));
					}
					else {
						//to evala edw giati 8elw na pianei prwta to no_input error an einai keno
						Float limit= Float.parseFloat(s_limit);
						
						budget = new Budget(this);
						
						if (same_on.isChecked()) {
							String fam_spinner = family_spinner.getSelectedItem().toString();
						
							//User user = new User(this);
							if (fam_spinner.equals("All")) {
								fam.moveToFirst();
								while (!fam.isAfterLast()) {
									int id = user.getId(fam.getString(fam.getColumnIndexOrThrow(FeedUser.USERNAME)));
									
									if (id!=0) {
										
										//budget.saveBudget(cat_spinner, limit, fd, ud, User.USER_ID, 0);
										budget.saveBudget(cat_spinner, limit, fd, ud, id, User.USER_ID);
									}
									fam.moveToNext();
								}
							}
							else {
								int id = user.getId(fam_spinner);
								
								if (id!=0) {
									
									//budget.saveBudget(cat_spinner, limit, fd, ud, User.USER_ID, 0);
									budget.saveBudget(cat_spinner, limit, fd, ud, id, User.USER_ID);
								}
							}
							
							//user.getUserFeedReaderDbHelper().close();
						
						}
						//else {
							budget.saveBudget(cat_spinner, limit, fd, ud, User.USER_ID, 0);
						//}
						
						budget.getBudgetFeedReaderDbHelper().close();
						
						user.getUserFeedReaderDbHelper().close();
						
						clearFields();
						
						displaySuccess(this.getString(R.string.success));
					}
				
				} catch (NumberFormatException e) {
					displayError(this.getString(R.string.not_a_number));
					
				} catch (NullPointerException e) {
					displayError(this.getString(R.string.no_family));
				}
			}
			else {
				clearFields();
			}
				
		}
		else {
			dateFragment.setView(v);
	    	dateFragment.show(getSupportFragmentManager(), "datePicker");
		}

	}
	
	public void displayError(String message) {
		InputErrorDialogFragment errorDialog = new InputErrorDialogFragment();
		errorDialog.setMessage(message);
		errorDialog.show(getFragmentManager(), "errorDialog");
	}
	
	public void displaySuccess(String message) {
		SuccessDialogFragment successDialog = new SuccessDialogFragment();
		successDialog.setMessage(message);
		successDialog.show(getFragmentManager(), "successDialog");
		timerDelayRemoveDialog(1500, successDialog);
	}
	
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
	
	public void timerDelayRemoveDialog(long time, final SuccessDialogFragment d){
	    new Handler().postDelayed(new Runnable() {
	        public void run() {                
	            d.dismiss();         
	        }
	    }, time); 
	}
	

	/*@Override
	protected void onStop() {
	    super.onStop();
	    
	    if (budget.getBudgetFeedReaderDbHelper() != null)
	    	budget.getBudgetFeedReaderDbHelper().close();
	    
	    if (user.getUserFeedReaderDbHelper() != null)
	    	user.getUserFeedReaderDbHelper().close();
	    
	    if (category.getCatFeedReaderDbHelper() != null)
	    	category.getCatFeedReaderDbHelper().close();
	}
	
	@Override
	protected void onPause() {
	    super.onPause();
	    
	    if (budget.getBudgetFeedReaderDbHelper() != null)
	    	budget.getBudgetFeedReaderDbHelper().close();
	    
	    if (user.getUserFeedReaderDbHelper() != null)
	    	user.getUserFeedReaderDbHelper().close();
	    
	    if (category.getCatFeedReaderDbHelper() != null)
	    	category.getCatFeedReaderDbHelper().close();
	}*/
}
