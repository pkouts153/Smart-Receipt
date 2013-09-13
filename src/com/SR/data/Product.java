package com.SR.data;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.SR.data.FeedReaderContract.FeedProduct;

public class Product {

	/*String PRODUCT_CATEGORY;
    String NAME;
    Float PRICE;
    String PURCHASE_DATE;
    int STORE;
    int USER;*/
    
    FeedReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    Cursor c;
    
    Context context;
    
	public Product(Context c) {
		context = c;
	}
	
    public Cursor getProducts(){
    	
    	mDbHelper = new FeedReaderDbHelper(context);
		
		// Gets the data repository in write mode
		db = mDbHelper.getWritableDatabase();
    	
		// Specifies which columns are needed from the database
		String[] projection = {
			FeedProduct._ID,
			FeedProduct.PRODUCT_CATEGORY,
			FeedProduct.NAME,
			FeedProduct.PRICE,
			FeedProduct.PURCHASE_DATE,
			FeedProduct.STORE,
			FeedProduct.USER
		    };
		
		c = db.query(
			FeedProduct.TABLE_NAME,  				  // The table to query
		    projection,                               // The columns to return
		    null,                                	  // The columns for the WHERE clause
		    null,                            		  // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    null                                 	  // The sort order
		    );
		
		return c;
    }
    
    public void saveProduct(ArrayList<String> products_list, String date, String VAT){
		mDbHelper = new FeedReaderDbHelper(context);
		
		// Gets the data repository in write mode
		db = mDbHelper.getWritableDatabase();
		
		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
	
		for (int i = 0; i<products_list.size(); i += 3) {
			
			values.put(FeedProduct.PRODUCT_CATEGORY, products_list.get(i));
			values.put(FeedProduct.NAME, products_list.get(i+1));
			Float price = Float.parseFloat(products_list.get(i+2));
			values.put(FeedProduct.PRICE, price);
			values.put(FeedProduct.PURCHASE_DATE, date);
			values.put(FeedProduct.STORE, VAT);
			values.put(FeedProduct.USER, User.USER_ID);
			
			db.insert(FeedProduct.TABLE_NAME, "null", values);
			values.clear();
		}
    }
    
    
    public FeedReaderDbHelper getProductFeedReaderDbHelper(){
    	return mDbHelper;
    }
    
}