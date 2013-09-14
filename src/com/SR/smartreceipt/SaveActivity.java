package com.SR.smartreceipt;

import java.util.ArrayList;

import com.SR.data.FeedReaderDbHelper;
import com.SR.data.Product;
import com.SR.processes.BudgetNotificationIntentService;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class SaveActivity extends Activity implements OnClickListener {
	
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
    
    FeedReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    
    DatePickerFragment dateFragment = new DatePickerFragment();
    
    ArrayList<String> product_list = new ArrayList<String>();
    
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
		
		category_spinner = (Spinner) findViewById(R.id.category_spinner);
		
		ArrayAdapter<CharSequence> category_adapter = ArrayAdapter.createFromResource(this, R.array.categories_array, android.R.layout.simple_spinner_item);
		category_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		category_spinner.setAdapter(category_adapter);
		
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
		getMenuInflater().inflate(R.menu.save, menu);
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
			try{
				if (save.getId() == ((Button)v).getId()) {
					
					cat_spinner = category_spinner.getSelectedItem().toString();
					p_name = product_name.getText().toString();
					p_price = price.getText().toString();
					pd = purchase_date.getText().toString();
					VAT = store_VAT.getText().toString();
	
					if ((product_list.size()==0 && (p_name.equals("") || pd.equals("") || p_price.equals("") || cat_spinner.equals(this.getString(R.string.category_prompt)))) 
							|| (product_list.size()!=0 && (pd.equals("")))) {
							
						InputErrorDialogFragment errorDialog = new InputErrorDialogFragment();
						errorDialog.setMessage(this.getString(R.string.no_input));
						errorDialog.show(getFragmentManager(), "Dialog");
					}
					else {
						if (!(p_name.equals("") || pd.equals("") || p_price.equals("") || cat_spinner.equals(this.getString(R.string.category_prompt))))
							addToArrayList();
						
						Product product = new Product(this);
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
						successDialog.show(getFragmentManager(), "successDialog");
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
					p = Float.parseFloat(p_price);
					
					if ((p_name.equals("")) || (p_price.equals("")) || (cat_spinner.equals(this.getString(R.string.category_prompt)))) {
						
						InputErrorDialogFragment errorDialog = new InputErrorDialogFragment();
						errorDialog.setMessage(this.getString(R.string.no_input));
						errorDialog.show(getFragmentManager(), "Dialog");
					}
					else {
						addToArrayList();
						clearFields();
						products++;
						number_of_products.setText(""+products+"");
						
						SuccessDialogFragment successDialog = new SuccessDialogFragment();
						successDialog.setMessage(this.getString(R.string.added));
						successDialog.show(getFragmentManager(), "addedDialog");
						timerDelayRemoveDialog(1500, successDialog);
					}
				}
				else {
					Intent intent = new Intent(this, MainActivity.class);
					startActivity(intent);
				}
			
			} catch (NumberFormatException e) {
				
				InputErrorDialogFragment errorDialog = new InputErrorDialogFragment();
				errorDialog.setMessage(this.getString(R.string.input_error));
				errorDialog.show(getFragmentManager(), "errorDialog");
			}
		}
		else {
			dateFragment.setView(v);
	    	dateFragment.show(getFragmentManager(), "datePicker");
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
