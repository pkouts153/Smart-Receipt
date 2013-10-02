package com.SR.smartreceipt;

import java.lang.reflect.Field;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;

import com.SR.data.Budget;
import com.SR.data.Family;
import com.SR.data.FeedReaderContract.FeedFamily;
import com.SR.data.FeedReaderContract.FeedUser;
import com.SR.data.FeedReaderDbHelper;
import com.SR.data.User;
import com.SR.processes.MyApplication;
import com.SR.processes.RetrieveBudgetsAfterIdTask;
import com.SR.processes.RetrieveCategoriesAfterIdTask;
import com.SR.processes.RetrieveDeletedBudgetsTask;
import com.SR.processes.RetrieveDeletedFamilyTask;
import com.SR.processes.RetrieveFamilyAfterIdTask;
import com.SR.processes.RetrieveNewFamilyMemberUserDataTask;
import com.SR.processes.RetrieveOffersAfterIdTask;
import com.SR.processes.RetrieveProductsAfterIdTask;
import com.SR.processes.RetrieveStoresAfterIdTask;
import com.SR.processes.RetrieveUpdatedBudgetsTask;
import com.SR.processes.RetrieveUpdatedUserDataTask;
import com.SR.processes.UploadBudgetTask;
import com.SR.processes.UploadFamilyTask;
import com.SR.processes.UploadProductTask;

/**
* Activity that displays the budget addition screen
* 
* @author Panagiotis Koutsaftikis, Vaggelis Marakis
*/
public class MainActivity extends Activity {

	// data variables
	User user;
	Budget budget;
	
	FeedReaderDbHelper mDbHelper;
	SQLiteDatabase db;
	
	/**
	 * An object for accessing preference data
	 */
	SharedPreferences sharedPref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// set up the UP button in ActionBar and Overflow menu
		setupActionBar();
		getOverflowMenu();
		
		// get the id from the last logged in user
        sharedPref = getSharedPreferences(getString(R.string.preference_user_id), MODE_PRIVATE);
        int id = sharedPref.getInt("USER_ID", 0);
	    
        if (id!=0 && User.USER_ID != id)
        	User.USER_ID = id;
        
		// if a user is logged in display the MainActivity
		if (User.USER_ID != 0) {
			setContentView(R.layout.activity_main);
		}
		//else display LoginActivity
		else {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		}
	}
	
	/**
	 * Set up the ActionBar, if the API is available.
	 * 
	 * the Home button is not enabled in MainActivity because there is no previous screen
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(false);
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
				
	        	user = new User(db);
	        	user.userLogout(this);
	        	
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
	
	/** Called when the user clicks the Save link */
	public void goToSave(View view) {
		Intent intent = new Intent(this, SaveActivity.class);
		startActivity(intent);
	}
	
	/** Called when the user clicks the Budget link */
	public void goToBudget(View view) {
		mDbHelper = new FeedReaderDbHelper(this);
		db = mDbHelper.getWritableDatabase();
		
		budget = new Budget(db);
		Cursor c = budget.getBudget(User.USER_ID);
		
		// if the user has budgets, call the BudgetListActivity
		if (c!=null && c.getCount()>0){
			mDbHelper.close();
			Intent intent = new Intent(this, BudgetListActivity.class);
			startActivity(intent);
		}
		// else call the AddBudgetActivity
		else{
			mDbHelper.close();
			Intent intent = new Intent(this, AddBudgetActivity.class);
			intent.putExtra("Activity", "Main");
			startActivity(intent);
		}
	}
	
	/** Called when the user clicks the Search link */
	public void goToSearch(View view) {
		Intent intent = new Intent(this, SearchActivity.class);
		startActivity(intent);
	}
	
	/** Called when the user clicks the Offers link */
	public void goToOffers(View view) {
		Intent intent = new Intent(this, AddBudgetActivity.class);
		startActivity(intent);
	}
	
	/** Called when the user clicks the Family link */
	public void goToFamily(View view) {
		Intent intent = new Intent(this, AddFamilyMemberActivity.class);
		startActivity(intent);
	}
	
	/** Called when the user clicks the List link */
	public void goToList(View view) {
		Intent intent = new Intent(this, ShoppingListActivity.class);
		startActivity(intent);
	}
	
	/** Called when the user clicks the Stores link */
	public void goToStores(View view) {
		Intent intent = new Intent(this, AddBudgetActivity.class);
		startActivity(intent);
	}
	
	@Override
    protected void onResume() {
    	super.onResume();
    	MyApplication.activityResumed();
    	
    	/*if (mDbHelper == null) {
    		new FeedReaderDbHelper(this);
    		db = mDbHelper.getWritableDatabase();
    	}*/
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	MyApplication.activityPaused();
    
    	/*FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(this);
		SQLiteDatabase db = mDbHelper.getWritableDatabase();*/
 /*    	Cursor queryResult = new Product().fetchProducts(db);
    	
		while(queryResult.moveToNext()){
			
			String id = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedProduct._ID));
			String name = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedProduct.NAME));
		
			Log.i("id", id);
			Log.i("name", name);
		}
	*/	
    	/*Cursor queryResult1 = new User(this).getFamilyMembers(User.USER_ID);
    	
		while(queryResult1.moveToNext()){
			
			String username = queryResult1.getString(queryResult1.getColumnIndexOrThrow(FeedUser.USERNAME));

			Log.i("username", username);

		}
		
		Cursor queryResult2 = new Family().fetchFamilies(db);
		while(queryResult2.moveToNext()){
			String id = queryResult2.getString(queryResult2.getColumnIndexOrThrow(FeedFamily._ID));
			
			String member2 = queryResult2.getString(queryResult2.getColumnIndexOrThrow(FeedFamily.MEMBER2));
			String member1 = queryResult2.getString(queryResult2.getColumnIndexOrThrow(FeedFamily.MEMBER1));

			Log.i("id", id);
			Log.i("member1", member1);
			Log.i("member2", member2);
		}*/
    	if (mDbHelper != null)
    		mDbHelper.close();
    }
    
    /*@Override
    protected void onStop() {
        super.onStop();
	      
        if(!(MyApplication.isActivityVisible())){
			
         	FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(this);
			SQLiteDatabase db = mDbHelper.getWritableDatabase();
		
			new RetrieveNewFamilyMemberUserDataTask().execute(db);
			
            new RetrieveBudgetsAfterIdTask().execute(db);
  			new RetrieveFamilyAfterIdTask().execute(db);
            new RetrieveProductsAfterIdTask().execute(db);
            new RetrieveCategoriesAfterIdTask().execute(db);
            new RetrieveStoresAfterIdTask().execute(db);
    	    new RetrieveOffersAfterIdTask().execute(db);
    	    
            new UploadBudgetTask().execute(db);
	  		new UploadFamilyTask().execute(db);  
     		new UploadProductTask().execute(db);
  
    		new RetrieveDeletedBudgetsTask().execute(db);
    		new RetrieveDeletedFamilyTask().execute(db);	

      		new RetrieveUpdatedBudgetsTask().execute(db);
      		new RetrieveUpdatedUserDataTask().execute(db);

        }
    }*/
	
}
