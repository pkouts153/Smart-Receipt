package com.SR.smartreceipt;

import java.lang.reflect.Field;

import com.SR.data.Budget;
import com.SR.data.FeedReaderDbHelper;
import com.SR.data.FeedReaderContract.FeedBudget;
import com.SR.data.User;
import com.SR.processes.MyApplication;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SimpleCursorAdapter;

/**
* Activity that displays the budget list screen
* 
* @author Παναγιώτης Κουτσαυτίκης 8100062
*/
public class BudgetListActivity extends ListActivity implements OnClickListener{

	//data variables
	Budget budget;
	User user;
	
	FeedReaderDbHelper mDbHelper;
	SQLiteDatabase db;
	
	/** A custom adapter to map columns from the cursor to TextViews */
	CustomListAdapter customListAdapter;
	
	// user's budgets cursor
	Cursor c;
	
	// the columns of the cursor
	String[] columns;
	// the ui components to display each of the columns
	int[] textviews;
	
	// UI components
	
	ImageButton delete;
	Button add_budget;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_budget_list);
		
		// set up the UP button in ActionBar and Overflow menu
		setupActionBar();
		getOverflowMenu();
		
		// if the user deletes budget(s) display a success dialog
		if (getIntent()!=null)
        	if (getIntent().getStringExtra("Success")!=null)
        		displaySuccess(this.getString(R.string.delete_success));
		
		mDbHelper = new FeedReaderDbHelper(this);
		db = mDbHelper.getWritableDatabase();
		
		budget = new Budget(db);
		
		// check and update the surpassed budgets
		budget.BudgetsSurpassed();
		
		c = budget.getBudget(User.USER_ID);

		//set up UI components
		
		delete = (ImageButton)findViewById(R.id.delete_button);
		delete.setOnClickListener((android.view.View.OnClickListener) this);
		
		add_budget = (Button)findViewById(R.id.add_budget);
		add_budget.setOnClickListener((android.view.View.OnClickListener) this);
		
		// create a list of budgets and bind the cursor to the list, 
		// leading the list's rows to display the data from the corresponding cursor lines
		columns = c.getColumnNames();
		
		//budget id is displayed in an invisible TextView
		//cursorAdapter requires an id
		textviews = new int[]{R.id.budget_id, R.id.exp_category,R.id.limit,R.id.start_date,R.id.end_date};
    	
		c.moveToFirst();
		
		customListAdapter= new CustomListAdapter(this, R.layout.activity_budget_list_row, c, columns, textviews);
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

	@Override
	public void onClick(View v) {
		// if the user clicks the "add budget" button
		if (v instanceof Button){
			Intent intent = new Intent(this, AddBudgetActivity.class);
			intent.putExtra("Activity", "Budget");
			startActivity(intent);
		}
		// else if the delete ImageButton is clicked
		else if (v instanceof ImageButton){
			View listViewRow;
			ListView listView = getListView();
			
			mDbHelper = new FeedReaderDbHelper(this);
    		db = mDbHelper.getWritableDatabase();
    		
			budget = new Budget(db);

			// if an error occurs during the deletion of a budget it becomes true
			boolean deletion_error = false;
			
			// counts the deleted budgets
			int deleted = 0;
			
			// for each budget
			c.moveToFirst();
			for (int i=0; i<c.getCount(); i++) {
				if (listView.getChildAt(i)!=null){
					// get the corresponding row of the list
					listViewRow = listView.getChildAt(i);
					CheckBox delete_budget = (CheckBox)listViewRow.findViewById(R.id.delete_budget);
					
					// delete the budget if the checkbox is checked
					if (delete_budget.isChecked()){
						if (!(budget.deleteBudget(c.getInt(c.getColumnIndexOrThrow(FeedBudget._ID)))))
							deletion_error = true;
						
						deleted++;
					}
					if (!c.isLast())
						c.moveToNext();
				}
			}
			
			if (deletion_error)
				displayError(this.getString(R.string.budget_delete_error));
			else {
				// if the user deleted a budget and didn't simply clicked delete with unckecked checkboxes
				if (deleted>0){
					// if there was only one budget before the deletion, then call the AddBudgetActivity
					if (c.getCount()==1){
						Intent intent = new Intent(this, AddBudgetActivity.class);
						intent.putExtra("Activity", "Main");
						intent.putExtra("Success", "Success");
						startActivity(intent);
					}
					// else refresh the BudgetListActivity
					else {
						finish();
						getIntent().putExtra("Success", "Success");
						startActivity(getIntent());
					}
				}
				//else if no checkbox was checked
				else {
					displayError(this.getString(R.string.unckecked_budgets));
				}
			}

			mDbHelper.close();
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
    
    /**
     * Custom adapter to bind list rows to cursor and 
     * set text color depending on the budget's spend balance
     * 
     * @author Παναγιώτης Κουτσαυτίκης 8100062
     *
     */
    private class CustomListAdapter extends SimpleCursorAdapter {

		private Context context;
        private int id;
        private Cursor cursor;
        
        /**
         * class constructor
         */
        public CustomListAdapter(Context con, int layout, Cursor c,
				String[] from, int[] to) {
			super(con, layout, c, from, to, 0);
			cursor = c;
			context = con;
			id = layout;
		}
        
        /**
         * Gets each row of the list, checks the budget spend balance 
         * and sets the TextView color accordingly
         */
        public View getView(int position, View v, ViewGroup parent)
        {
        	View mView = v ;
        	if(mView == null){
                LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mView = vi.inflate(id, null);
            }
        	
        	TextView budget_category = (TextView)mView.findViewById(R.id.exp_category);
        	
    		if (cursor.moveToPosition(position)){
    			if (cursor.getInt(cursor.getColumnIndexOrThrow(FeedBudget.IS_SURPASSED))==1)
    				budget_category.setTextColor(getResources().getColor(R.color.e_yellow));
    			
    			else if (cursor.getInt(cursor.getColumnIndexOrThrow(FeedBudget.IS_SURPASSED))==2)
    				budget_category.setTextColor(Color.RED);
    			
    		}
    		bindView(mView, context, cursor);
			return mView;
        }
    }
}
