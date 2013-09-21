package com.SR.smartreceipt;

import java.lang.reflect.Field;
import java.util.ArrayList;

import com.SR.data.Category;
import com.SR.data.Product;
import com.SR.data.User;
import com.SR.data.FeedReaderContract.FeedCategory;
import com.SR.processes.BudgetNotificationIntentService;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
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

public class SaveActivity extends FragmentActivity implements OnClickListener {
	
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
    
    DatePickerFragment dateFragment = new DatePickerFragment();
    
    ArrayList<String> product_list = new ArrayList<String>();
    
    Product product;
    Category category;
    User user;
    
    String cat_spinner;
	String p_name;
	String p_price;
	Float p;
	String pd;
	String VAT;
	int products = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_save);
		// Show the Up button in the action bar.
		setupActionBar();
		getOverflowMenu();
		//set up category spinner
		
        category = new Category(this);
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
				cat_adapter.add(category_name);
				c.moveToNext ();
			}
			c.close();
			category.getCatFeedReaderDbHelper().close();
			
        } catch (CursorIndexOutOfBoundsException e){
        	c.close();
			category.getCatFeedReaderDbHelper().close();
        }
		
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
	
	@Override
	public void onClick(View v) {
		if (v instanceof Button) {
			try{
				if (save.getId() == ((Button)v).getId()) {
					
					cat_spinner = category_spinner.getSelectedItem().toString();
					p_name = product_name.getText().toString();
					p_price = price.getText().toString();
					pd = purchase_date.getText().toString();
					VAT = store_VAT.getText().toString();
	
					if ((product_list.size()==0 && (p_name.equals("") || pd.equals("") || p_price.equals("") || cat_spinner.equals(this.getString(R.string.category_prompt)))) 
							|| (product_list.size()!=0 && (pd.equals("")))) {
							
						displayError(this.getString(R.string.no_input));
					}
					else {
						if (!(p_name.equals("") || pd.equals("") || p_price.equals("") || cat_spinner.equals(this.getString(R.string.category_prompt))))
							addToArrayList();
						
						product = new Product(this);
						product.saveProduct(product_list, pd, VAT);
						
						product.getProductFeedReaderDbHelper().close();
						
						clearFields();
						purchase_date.getText().clear();
						store_VAT.getText().clear();
						
						product_list.clear();
						product_list.trimToSize();
						
						products = 0;
						number_of_products.setText(""+products+"");
						
						SuccessDialogFragment successDialog = new SuccessDialogFragment();
						successDialog.setMessage(this.getString(R.string.success));
						successDialog.show(getSupportFragmentManager(), "successDialog");
						timerDelayRemoveDialog(1500, successDialog);
					
					    Intent serviceIntent = new Intent(SaveActivity.this, BudgetNotificationIntentService.class);
					    SaveActivity.this.startService(serviceIntent);
					}
				}
				else if (reset.getId() == ((Button)v).getId()){
					clearFields();
					purchase_date.getText().clear();
					store_VAT.getText().clear();
				}
				else if (add.getId() == ((Button)v).getId()){
					
					cat_spinner = category_spinner.getSelectedItem().toString();
					p_name = product_name.getText().toString();
					p_price = price.getText().toString();
					
					
					if ((p_name.equals("")) || (p_price.equals("")) || (cat_spinner.equals(this.getString(R.string.category_prompt)))) {
						
						displayError(this.getString(R.string.no_input));
					}
					else {
						//to evala edw giati 8elw na pianei prwta to no_input error an einai keno
						p = Float.parseFloat(p_price);
						
						addToArrayList();
						clearFields();
						products++;
						number_of_products.setText(""+products+"");
						
						displaySuccess(this.getString(R.string.added));
					}
				}
				else {
					Intent intent = new Intent(this, MainActivity.class);
					startActivity(intent);
				}
			
			} catch (NumberFormatException e) {
				
				displayError(this.getString(R.string.not_a_price));
			}
		}
		else {
			dateFragment.setView(v);
	    	dateFragment.show(getSupportFragmentManager(), "datePicker");
		}
	}
	
	public void clearFields() {
		category_spinner.setSelection(0);
		product_name.getText().clear();
		price.getText().clear();
	}
	
	public void addToArrayList() {
		product_list.add(cat_spinner);
		product_list.add(p_name);
		product_list.add(p_price);
	}
	
	public void displayError(String message) {
		InputErrorDialogFragment errorDialog = new InputErrorDialogFragment();
		errorDialog.setMessage(message);
		errorDialog.show(getSupportFragmentManager(), "errorDialog");
	}
	
	public void displaySuccess(String message) {
		SuccessDialogFragment successDialog = new SuccessDialogFragment();
		successDialog.setMessage(message);
		successDialog.show(getSupportFragmentManager(), "successDialog");
		timerDelayRemoveDialog(1500, successDialog);
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
	    
	    product_list.clear();
		product_list.trimToSize();
		
	    /*if (product.getProductFeedReaderDbHelper() != null)
	    	product.getProductFeedReaderDbHelper().close();*/
	}
	
	@Override
	protected void onPause() {
	    super.onPause();
	    
	    /*if (product.getProductFeedReaderDbHelper() != null)
	    	product.getProductFeedReaderDbHelper().close();*/
	}

}
