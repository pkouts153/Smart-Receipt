package com.SR.smartreceipt;

import java.lang.reflect.Field;

import com.SR.data.User;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;

public class MainActivity extends Activity {

	User user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupActionBar();
		getOverflowMenu();
		if (User.USER_ID != 0) {
			setContentView(R.layout.activity_main);
		}
		else {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
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
	        	user = new User(this);
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
	
	/** Called when the user clicks the Save link */
	public void goToSave(View view) {
		Intent intent = new Intent(this, SaveActivity.class);
		startActivity(intent);
	}
	
	/** Called when the user clicks the Budget link */
	public void goToBudget(View view) {
		Intent intent = new Intent(this, BudgetActivity.class);
		startActivity(intent);
	}
	
	/** Called when the user clicks the Search link */
	public void goToSearch(View view) {
		Intent intent = new Intent(this, SearchActivity.class);
		startActivity(intent);
	}
	
	/** Called when the user clicks the Offers link */
	public void goToOffers(View view) {
		Intent intent = new Intent(this, BudgetActivity.class);
		startActivity(intent);
	}
	
	/** Called when the user clicks the Family link */
	public void goToFamily(View view) {
		Intent intent = new Intent(this, AddFamilyMemberActivity.class);
		startActivity(intent);
	}
	
	/** Called when the user clicks the List link */
	public void goToList(View view) {
		Intent intent = new Intent(this, BudgetActivity.class);
		startActivity(intent);
	}
	
	/** Called when the user clicks the Stores link */
	public void goToStores(View view) {
		Intent intent = new Intent(this, BudgetActivity.class);
		startActivity(intent);
	}
}
