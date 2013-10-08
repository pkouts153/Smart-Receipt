package com.SR.smartreceipt;

import java.lang.reflect.Field;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;

import com.SR.data.Family;
import com.SR.data.FeedReaderContract.FeedFamily;
import com.SR.data.FeedReaderDbHelper;
import com.SR.data.List;
import com.SR.data.User;
import com.SR.data.FeedReaderContract.FeedList;
import com.SR.processes.MyApplication;

public class FamilyListActivity extends ListActivity implements OnClickListener{

	FeedReaderDbHelper mDbHelper;
	SQLiteDatabase db;
	
	SimpleCursorAdapter customListAdapter;
	Cursor c;
	String[] columns;
	int[] textviews;
	Button add_family;
	ImageButton delete_family;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_family_list);
		
		// set up the UP button in ActionBar and Overflow menu
		setupActionBar();
		getOverflowMenu();
		
		mDbHelper = new FeedReaderDbHelper(this);
		db = mDbHelper.getWritableDatabase();

		c = new Family().fetchFamily(db);
		
		add_family = (Button)findViewById(R.id.add_family);
		add_family.setOnClickListener((android.view.View.OnClickListener) this);

		delete_family = (ImageButton)findViewById(R.id.delete_family);
		delete_family.setOnClickListener((android.view.View.OnClickListener) this);
		
		columns = c.getColumnNames();
		textviews = new int[]{R.id.family_id, R.id.member};
		c.moveToFirst();
		
		customListAdapter= new SimpleCursorAdapter(this, R.layout.activity_family_list_row, c, columns, textviews,0);
		customListAdapter.bindView((View)findViewById(R.id.activity_list), this, c);
        
        setListAdapter(customListAdapter);

        mDbHelper.close();
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
	        	mDbHelper = new FeedReaderDbHelper(this);
	    		db = mDbHelper.getWritableDatabase();
	    		
	        	new User(db).userLogout(this);
	        	
	        	mDbHelper.close();
	        	
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
		// if the user clicks the "add budget" button
		if (v instanceof Button){
		//if (add_family.getId() == ((Button)v).getId()) {
			Intent intent = new Intent(this, AddFamilyActivity.class);
			intent.putExtra("Activity", "Budget");
			startActivity(intent);
		}
		else {
			mDbHelper = new FeedReaderDbHelper(this);
			db = mDbHelper.getWritableDatabase();
			
			// if at least one list product is deleted, it becomes true
			boolean deleted = false;
			
			View listViewRow;
			ListView listView = getListView();
			
			Family family = new Family(db);
			
			// for each product of the list
			c.moveToFirst();
			for (int i=0; i<c.getCount(); i++) {
				if (listView.getChildAt(i)!=null){
					// get the corresponding row of the list
					listViewRow = listView.getChildAt(i);

					CheckBox check_product = (CheckBox)listViewRow.findViewById(R.id.check_family);
					
					// delete the product
					if (check_product.isChecked())
						if (family.deleteFamilyMember(c.getInt(c.getColumnIndexOrThrow(FeedFamily._ID))))
							deleted=true;
					
					if (!c.isLast())
						c.moveToNext();
				}
			}
			mDbHelper.close();
			
			// if any product was deleted, refresh the activity
			if (deleted){
				finish();
				startActivity(getIntent());
			}
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
