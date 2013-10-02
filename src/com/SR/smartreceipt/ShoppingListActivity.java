package com.SR.smartreceipt;

import java.lang.reflect.Field;

import com.SR.data.FeedReaderContract.FeedList;
import com.SR.data.FeedReaderDbHelper;
import com.SR.data.List;
import com.SR.data.User;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.app.NavUtils;

/**
* Activity that displays the user's shopping list
* 
* @author Παναγιώτης Κουτσαυτίκης 8100062
*/
public class ShoppingListActivity extends ListActivity implements OnClickListener{
    
	// data variables
	User user;
	List list;
    
    FeedReaderDbHelper mDbHelper;
	SQLiteDatabase db;
	
	Cursor products_list;
	
	// UI components
	
	EditText product;
	Button add;
	ImageButton delete;
	
	/**
	 * A custom adapter to map columns from the cursor to TextViews 
	 */
	CustomListAdapter customListAdapter;
	
	// the columns of the cursor
	String[] columns;
	// the UI components to display each of the columns
	int[] textviews;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		// set up the UP button in ActionBar and Overflow menu
		setupActionBar();
		getOverflowMenu();

        mDbHelper = new FeedReaderDbHelper(this);
		db = mDbHelper.getWritableDatabase();
		
    	list = new List(db);
    	
    	products_list = list.getList();
    	
		// set up UI components
		product = (EditText)findViewById(R.id.add_product);
        
		add = (Button)findViewById(R.id.add_button);
		add.setOnClickListener(this);

		delete = (ImageButton)findViewById(R.id.delete_products);
		delete.setOnClickListener(this);
		
		// create a list of products and bind the cursor to the list, 
		// leading the list's rows to display the data from the corresponding cursor lines
		columns = products_list.getColumnNames();

		//list id is displayed in an invisible TextView
		//cursorAdapter requires an id
		textviews = new int[]{R.id.list_id, R.id.product_name};
    	
		products_list.moveToFirst();
		
		customListAdapter= new CustomListAdapter(this, R.layout.activity_list_row, products_list, columns, textviews);
		customListAdapter.bindView((View)findViewById(R.id.activity_shopping_list), this, products_list);
        
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
				// when the user returns to the MainActivity 
				// update the list table and check or uncheck the products
				View listViewRow;
				ListView listView = getListView();
				
				mDbHelper = new FeedReaderDbHelper(this);
	    		db = mDbHelper.getWritableDatabase();
	    		
				list = new List(db);
				// for each product of the list
				products_list.moveToFirst();
				for (int i=0; i<products_list.getCount(); i++) {
					if (listView.getChildAt(i)!=null){
						// get the corresponding row of the list
						listViewRow = listView.getChildAt(i);
						CheckBox check_product = (CheckBox)listViewRow.findViewById(R.id.delete_product);
						
						if (check_product.isChecked()) {
							list.checkProductOfList(products_list.getInt(products_list.getColumnIndexOrThrow(FeedList._ID)));
						}
						else
							list.uncheckProductOfList(products_list.getInt(products_list.getColumnIndexOrThrow(FeedList._ID)));
						
						if (!products_list.isLast())
							products_list.moveToNext();
					}
				}
				mDbHelper.close();
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
		// if the user clicks add
		if (v instanceof Button) {
			if (add.getId() == ((Button)v).getId()) {
				String product_name = product.getText().toString();
				
				// if the user has typed a product, save it to the table
				if (!product_name.equals("")){
					mDbHelper = new FeedReaderDbHelper(this);
					db = mDbHelper.getWritableDatabase();
					
					list = new List(db);
			    	if (list.addProductToList(product_name)) {
			    		// refresh the activity
			    		finish();
						startActivity(getIntent());
			    	}
			    	
			    	mDbHelper.close();
				}
			}
		}
		// if the user clicks the delete button
		else if (v instanceof ImageButton){
			mDbHelper = new FeedReaderDbHelper(this);
			db = mDbHelper.getWritableDatabase();
			
			// if at least one list product is deleted, it becomes true
			boolean deleted = false;
			
			View listViewRow;
			ListView listView = getListView();
			
			list = new List(db);
			
			// for each product of the list
			products_list.moveToFirst();
			for (int i=0; i<products_list.getCount(); i++) {
				if (listView.getChildAt(i)!=null){
					// get the corresponding row of the list
					listViewRow = listView.getChildAt(i);

					CheckBox check_product = (CheckBox)listViewRow.findViewById(R.id.delete_product);
					
					// delete the product
					if (check_product.isChecked())
						if (list.deleteProductFromList(products_list.getInt(products_list.getColumnIndexOrThrow(FeedList._ID))))
							deleted=true;
					
					if (!products_list.isLast())
						products_list.moveToNext();
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
     * Custom adapter to bind list rows to cursor and 
     * set CheckBoxes depending on the product's check condition
     * 
     * @author Panagiotis Koutsaftikis
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
         * Gets each row of the list, checks the product's check condition 
         * and sets the CheckBox accordingly
         */
        public View getView(int position, View v, ViewGroup parent)
        {
        	View mView = v ;
        	if(mView == null){
                LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mView = vi.inflate(id, null);
            }
        	
        	CheckBox checkbox = (CheckBox)mView.findViewById(R.id.delete_product);
        	
    		if (cursor.moveToPosition(position))
    			if (cursor.getInt(cursor.getColumnIndexOrThrow(FeedList.IS_CHECKED))==1)
    				checkbox.setChecked(true);
    			
    		bindView(mView, context, cursor);
			return mView;
        }
    }
}
