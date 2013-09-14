package com.SR.smartreceipt;

import com.SR.data.Budget;
import com.SR.data.Category;
import com.SR.data.FeedReaderContract.FeedUser;
import com.SR.data.User;
import com.SR.data.FeedReaderContract.FeedCategory;
import com.SR.data.FeedReaderDbHelper;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.support.v4.app.NavUtils;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

public class BudgetActivity extends Activity implements OnClickListener {

	Spinner category_spinner;
	EditText spend_limit;
	EditText from_date;
	EditText until_date;
    CheckBox same_on;
    Spinner family_spinner;
    CheckBox notify;
    Button submit;
    Button reset;
    
    FeedReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    
    DatePickerFragment dateFragment = new DatePickerFragment();
    
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
        
        reset = (Button)findViewById(R.id.reset);
        reset.setOnClickListener(this);
        
        
        Category category = new Category(this);
		Cursor c = category.getCategories();
		
		ArrayAdapter <CharSequence> cat_adapter = new ArrayAdapter <CharSequence> (this, android.R.layout.simple_spinner_item );
		cat_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		category_spinner = (Spinner) findViewById(R.id.category_spinner);
		category_spinner.setAdapter(cat_adapter);
			
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
			category.getCatFeedReaderDbHelper().close();
			
        } catch (CursorIndexOutOfBoundsException e){
        	c.close();
			category.getCatFeedReaderDbHelper().close();
        }
        
        
        
        
        ArrayAdapter <CharSequence> fam_adapter = new ArrayAdapter <CharSequence> (this, android.R.layout.simple_spinner_item );
		fam_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		family_spinner = (Spinner) findViewById(R.id.family_spinner);
		family_spinner.setAdapter(fam_adapter);
		
		User user = new User(this);
		Cursor c2 = user.getFamilyMembers(User.USER_ID);
		
        try{
	        
			c2.moveToFirst();
			String family_member = c2.getString(c2.getColumnIndexOrThrow(FeedUser.USERNAME));

			fam_adapter.add(family_member);

			while (!c2.isLast ()) {
				c2.moveToNext ();
				family_member = c2.getString(c2.getColumnIndexOrThrow(FeedUser.USERNAME));
				fam_adapter.add(family_member);
			}
			c2.close();
			user.getUserFeedReaderDbHelper().close();
			
		} catch (CursorIndexOutOfBoundsException e){
			fam_adapter.add("No family");
			c2.close();
			user.getUserFeedReaderDbHelper().close();
	    }
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
						errorDialog.setMessage(this.getString(R.string.no_input));
						errorDialog.show(getFragmentManager(), "Dialog");
					}
					else {
						
						Budget budget = new Budget(this);
						
						if (same_on.isChecked()) {
							String fam_spinner = family_spinner.getSelectedItem().toString();
							
							User user = new User(this);
							int id = user.getId(fam_spinner);
							
							if (id!=0) {
								
								budget.saveBudget(cat_spinner, limit, fd, ud, n, User.USER_ID, id);
								budget.saveBudget(cat_spinner, limit, fd, ud, n, id, 0);
							}
							
							user.getUserFeedReaderDbHelper().close();
						}
						else {
							budget.saveBudget(cat_spinner, limit, fd, ud, n, User.USER_ID, 0);
						}
						
						budget.getBudgetFeedReaderDbHelper().close();
						
						clearFields();
						
						SuccessDialogFragment successDialog = new SuccessDialogFragment();
						successDialog.setMessage(this.getString(R.string.success));
						successDialog.show(getFragmentManager(), "successDialog");
						timerDelayRemoveDialog(1500, successDialog);
					}
				
				} catch (NumberFormatException e) {
					InputErrorDialogFragment errorDialog = new InputErrorDialogFragment();
					errorDialog.setMessage(this.getString(R.string.input_error));
					errorDialog.show(getFragmentManager(), "errorDialog");
				}
			}
			else {
				clearFields();
			}
				
		}
		else {
			dateFragment.setView(v);
	    	dateFragment.show(getFragmentManager(), "datePicker");
		}

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
		if (notify.isChecked()) {
			notify.setChecked(false);
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
