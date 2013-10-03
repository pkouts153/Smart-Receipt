package com.SR.smartreceipt;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.SR.data.Category;
import com.SR.data.FeedReaderDbHelper;
import com.SR.data.Product;
import com.SR.data.Store;
import com.SR.data.User;
import com.SR.data.FeedReaderContract.FeedCategory;
import com.SR.processes.BudgetNotificationIntentService;
import com.SR.processes.MyApplication;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
* Activity that displays the product save screen
* 
* @author Παναγιώτης Κουτσαυτίκης 8100062
*/
public class SaveActivity extends FragmentActivity implements OnClickListener {
	
	// UI components
	
	Spinner category_spinner;
	EditText product_name;
	EditText price;
	TextView number_of_products;
	Button add;
	EditText purchase_date;
	EditText store_VAT;
    Button save;
    Button reset;
    Button scan;
    
    /**
     *  Fragment for date selection
     */
    DatePickerFragment dateFragment = new DatePickerFragment();
    
    /**
     * It saves the products' category, date and price. A product per 3 positions
     */
    ArrayList<String> product_list = new ArrayList<String>();
    
    // data variables
    
    Product product;
    Category category;
    User user;
    Store store;
    
    FeedReaderDbHelper mDbHelper;
	SQLiteDatabase db;
	
	//user input variables
	
    String cat_spinner;
	String p_name;
	String p_price;
	Float p;
	String pd;
	String VAT;
	
	// the number of products added to the list
	int products = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_save);
		
		// set up the UP button in ActionBar and Overflow menu
		setupActionBar();
		getOverflowMenu();
		
		//set up category spinner
		
		mDbHelper = new FeedReaderDbHelper(this);
		db = mDbHelper.getWritableDatabase();
		
        category = new Category(db);
		Cursor c = category.getCategories();
		
		category_spinner = (Spinner) findViewById(R.id.category_spinner);
		ArrayAdapter <CharSequence> cat_adapter = new ArrayAdapter <CharSequence> (this, android.R.layout.simple_spinner_item );
		category_spinner.setAdapter(cat_adapter);
		cat_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		cat_adapter.add(this.getString(R.string.category_prompt));
		
        try{
        	
			c.moveToFirst();
			String category_name;
			
			while (!c.isAfterLast ()) {
				category_name = c.getString(c.getColumnIndexOrThrow(FeedCategory.NAME));
				if (!c.getString(c.getColumnIndexOrThrow(FeedCategory.NAME)).equals("All"))
					cat_adapter.add(category_name);
				c.moveToNext ();
			}
			c.close();
        } catch (CursorIndexOutOfBoundsException e){
        	c.close();
        }
		
        mDbHelper.close();
        
        //set up other ui components
        
		product_name = (EditText)findViewById(R.id.product_name);
		price = (EditText)findViewById(R.id.price);
		number_of_products = (TextView)findViewById(R.id.number_of_products);
		number_of_products.setText(""+products+"");
		
		add = (Button)findViewById(R.id.add_button);
		add.setOnClickListener(this);
		
		purchase_date = (EditText)findViewById(R.id.purchase_date);
		purchase_date.setOnClickListener(this);
		
		store_VAT = (EditText)findViewById(R.id.store);
        
		save = (Button)findViewById(R.id.save_button);
		save.setOnClickListener(this);
		
		reset = (Button)findViewById(R.id.reset_button);
		reset.setOnClickListener(this);
        
		scan = (Button)findViewById(R.id.scan_button);
		scan.setOnClickListener(this);
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
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	/**
	 * Handle presses on the action bar items
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
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
		if (v instanceof Button) {
			try{
				// if the clicked button is add
				if (add.getId() == ((Button)v).getId()) {
					
					//get user's input
					cat_spinner = category_spinner.getSelectedItem().toString();
					p_name = product_name.getText().toString();
					p_price = price.getText().toString();
					
					//if any field is empty display error dialog
					if ((p_name.equals("")) || (p_price.equals("")) || (cat_spinner.equals(this.getString(R.string.category_prompt)))) {
						
						displayError(this.getString(R.string.no_input));
					}
					else {
						p = Float.parseFloat(p_price);
						
						//add the product data to the product_list
						addToArrayList();
						
						//clear the fields
						clearFields();
						
						//update the number of products
						products++;
						number_of_products.setText(""+products+"");
						
						displaySuccess(this.getString(R.string.added));
					}
				}
				// else if the clicked button is save
				else if (save.getId() == ((Button)v).getId()){
					
					// get user's input
					cat_spinner = category_spinner.getSelectedItem().toString();
					p_name = product_name.getText().toString();
					p_price = price.getText().toString();
					pd = purchase_date.getText().toString();
					VAT = store_VAT.getText().toString();
	
					// display an error dialog when 
					//		no product is added and the fields are empty 
					//		and product was added, but the date is empty
					if ((product_list.size()==0 && (p_name.equals("") || pd.equals("") || p_price.equals("") || cat_spinner.equals(this.getString(R.string.category_prompt)))) 
							|| (product_list.size()!=0 && (pd.equals("")))) {
							
						displayError(this.getString(R.string.no_input));
					}
					else {
						// if the fields are not empty, add the new product to the list 
						//(if the list is not empty then there is no need for the fields to be full and a new product to be added)
						if (!(p_name.equals("") || pd.equals("") || p_price.equals("") || cat_spinner.equals(this.getString(R.string.category_prompt))))
							addToArrayList();
						
						mDbHelper = new FeedReaderDbHelper(this);
			    		db = mDbHelper.getWritableDatabase();
			    		
						product = new Product(db);
						store = new Store(db);
						
						// get the store id from the VAT number typed by the user
						int id = store.getId(VAT);
						
						// if a product is not saved successfully it will be true
						boolean save_error = false;
						
						if (!product.saveProduct(product_list, pd, id))
							save_error = true;
						
						mDbHelper.close();
						
						//clear user's input
						clearFields();
						purchase_date.getText().clear();
						store_VAT.getText().clear();
						
						// set products to default
						product_list.clear();
						product_list.trimToSize();
						
						products = 0;
						number_of_products.setText(""+products+"");
						
						// if an error occurred during the saving display error dialog
						if (save_error)
							displayError(this.getString(R.string.product_save_error));
						else{
							SuccessDialogFragment successDialog = new SuccessDialogFragment();
							successDialog.setMessage(this.getString(R.string.success));
							successDialog.show(getFragmentManager(), "successDialog");
							timerDelayRemoveDialog(1500, successDialog);
						
							// if the save was successful call the notification service to check if any budget was surpassed
						    Intent serviceIntent = new Intent(SaveActivity.this, BudgetNotificationIntentService.class);
						    SaveActivity.this.startService(serviceIntent);
						}
					}
				}
				//else if the clicked button is reset clear the fields
				else if (reset.getId() == ((Button)v).getId()){
					clearFields();
					purchase_date.getText().clear();
					store_VAT.getText().clear();
				}
				//else if the clicked button is scan return to the Main (scan is not implemented yet)
				else {
					Intent intent = new Intent(this, MainActivity.class);
					startActivity(intent);
				}
			// if an error occurs during the parsing of the price to float
			} catch (NumberFormatException e) {
				
				displayError(this.getString(R.string.not_a_price));
			}
		}
		// if the view clicked was an EditView (date) call the date picker dialog
		else {
			dateFragment.setView(v);
	    	dateFragment.show(getSupportFragmentManager(), "datePicker");
		}
	}
	
	/**
	 * Clear the fields from user's input
	 */
	public void clearFields() {
		category_spinner.setSelection(0);
		product_name.getText().clear();
		price.getText().clear();
	}
	
	/**
	 * Adds product category, name and price to an ArrayList
	 */
	public void addToArrayList() {
		product_list.add(cat_spinner);
		product_list.add(p_name);
		product_list.add(p_price);
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
    
	@Override
	protected void onStop() {
	    super.onStop();
	    
	    product_list.clear();
		product_list.trimToSize();
	}
}
