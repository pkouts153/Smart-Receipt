package com.SR.smartreceipt;

import java.lang.reflect.Field;

import com.SR.data.Budget;
import com.SR.data.User;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SimpleCursorAdapter;

public class BudgetListActivity extends ListActivity implements OnClickListener{

	Budget budget;
	User user;
	
	SimpleCursorAdapter simpleCursorAdapter;
	
	Cursor c;
	
	String[] columns;
	int[] textviews;
	
	Button delete;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_budget_list);
		// Show the Up button in the action bar.
		setupActionBar();
		getOverflowMenu();
		
		delete = (Button)findViewById(R.id.delete);
		delete.setOnClickListener((android.view.View.OnClickListener) this);
		
		budget = new Budget(this);
		
		c = budget.getBudget(User.USER_ID);
		
		columns = c.getColumnNames();
		
		textviews = new int[]{R.id.budget_id, R.id.exp_category,R.id.limit,R.id.start_date,R.id.end_date};
    	
		simpleCursorAdapter = new SimpleCursorAdapter(this, R.layout.activity_budget_list_row, c, columns, textviews, 0);
        simpleCursorAdapter.bindView((View)findViewById(R.id.activity_list), this, c);
        
        setListAdapter(simpleCursorAdapter);
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
	        case R.id.action_logout:
	        	user = new User(this);
	        	user.userLogout();
	        	Intent intent = new Intent(this, LoginActivity.class);
	    		startActivity(intent);
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
		// TODO Auto-generated method stub
		
	}

}
