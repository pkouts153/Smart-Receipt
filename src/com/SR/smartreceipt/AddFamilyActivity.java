package com.SR.smartreceipt;

import java.lang.reflect.Field;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.EditText;

import com.SR.data.Family;
import com.SR.data.FeedReaderDbHelper;
import com.SR.data.User;
import com.SR.processes.MyApplication;

public class AddFamilyActivity extends FragmentActivity implements OnClickListener {
	
	EditText email;
    Button submit;
    Button reset;
   
    FeedReaderDbHelper mDbHelper;
	SQLiteDatabase db;
    
    String parent_activity;
    
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_family_member);
		
		mDbHelper = new FeedReaderDbHelper(this);
		db = mDbHelper.getWritableDatabase();
		
		Intent intent = getIntent();
        parent_activity = intent.getStringExtra("Activity");
        
		setupActionBar();
		getOverflowMenu();
				
		email = (EditText)findViewById(R.id.member2_email);
        
        submit = (Button)findViewById(R.id.submit);
        submit.setOnClickListener(this);
        
        reset = (Button)findViewById(R.id.reset);
        reset.setOnClickListener(this);
	}
    
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case R.id.action_search:
	    		Intent intent = new Intent(this, SearchActivity.class);
	    		startActivity(intent);
	            return true;
	        case R.id.action_logout:
	        	new User().userLogout(this);
	        	mDbHelper.close();
	        	Intent intent2 = new Intent(this, LoginActivity.class);
	    		startActivity(intent2);
	    	
	            return true;
			case android.R.id.home:
				Intent upIntent;
				if (parent_activity.equals("Main"))
					upIntent = new Intent(this, MainActivity.class);
				else
					upIntent = new Intent(this, FamilyListActivity.class);
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
			// if the clicked button is submit
			if (submit.getId() == ((Button)v).getId()) {
								
				boolean save_error = true;
				String mail = email.getText().toString();
				
				if (mail.equals("")) {
					displayError(this.getString(R.string.no_input));
				}
				else {
					if (isEmailValid(mail)) {
						new Family().insertFamilyFromActivity(mail, db);
						save_error=false;
						mDbHelper.close();
						clearFields();
					} else
						displayError(this.getString(R.string.not_a_mail));
				}
				if (save_error)
					displayError(this.getString(R.string.budget_save_error));
				else
					displaySuccess(this.getString(R.string.success));	
			}
			else 
				clearFields();
		}

	}
	
	public static boolean isEmailValid(String email) {
	    boolean isValid = false;

	    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
	    CharSequence inputStr = email;

	    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(inputStr);
	    if (matcher.matches()) {
	        isValid = true;
	    }
	    return isValid;
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
		email.getText().clear();
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
