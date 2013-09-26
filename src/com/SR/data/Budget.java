package com.SR.data;

import java.sql.Timestamp;
import java.util.Date;
import java.text.SimpleDateFormat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.SR.data.FeedReaderContract.FeedBudget;
import com.SR.data.FeedReaderContract.FeedProduct;

public class Budget {
    
    FeedReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    Cursor c1;
    Cursor c2;
    
    Context context;
    
	public Budget(Context c) {
		context = c;
	}
	
    public Cursor getBudget(int user_id){
    	
    	mDbHelper = new FeedReaderDbHelper(context);
		
		// Gets the data repository in write mode
		db = mDbHelper.getWritableDatabase();
    	
		// Specifies which columns are needed from the database
		String[] projection = {
			FeedBudget._ID,
			FeedBudget.EXPENSE_CATEGORY,
			FeedBudget.SPEND_LIMIT,
			FeedBudget.START_DATE,
			FeedBudget.END_DATE,
			FeedBudget.USER,
			FeedBudget.FAMILY_USER,
			FeedBudget.IS_SURPASSED
		    };
		
		String where = "" + FeedBudget.USER + "=" + user_id + " AND " + FeedBudget.FOR_DELETION + "=0";
		
		c1 = db.query(
			FeedBudget.TABLE_NAME,  				  // The table to query
		    projection,                               // The columns to return
		    where,                                	  // The columns for the WHERE clause
		    null,                            		  // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    null                                 	  // The sort order
		    );
		
		return c1;
    }
    
    public void saveBudget(String category, Float limit, String from_date, String until_date, int user_id, int family_id){
    	
    	mDbHelper = new FeedReaderDbHelper(context);
		
		// Gets the data repository in write mode
		db = mDbHelper.getWritableDatabase();
    	
		ContentValues values = new ContentValues();
		values.put(FeedBudget.EXPENSE_CATEGORY, category);
		values.put(FeedBudget.SPEND_LIMIT, limit);
		values.put(FeedBudget.START_DATE, from_date);
		values.put(FeedBudget.END_DATE, until_date);
		values.put(FeedBudget.USER, user_id);
		if (family_id != 0)
			values.put(FeedBudget.FAMILY_USER, family_id);
		values.put(FeedBudget.FOR_DELETION, 0);
		values.put(FeedBudget.FOR_UPDATE, 0);
		values.put(FeedBudget.ON_SERVER, 0);
		values.put(FeedBudget.IS_SURPASSED, 0);
		
		Date date = new Date();
		Timestamp timestampToday = new Timestamp(date.getTime());
		String today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestampToday);
		
		values.put(FeedBudget.BUDGET_CREATED, today);
		
		db.insert(FeedBudget.TABLE_NAME, "null", values);
		values.clear();
		
		getBudgetFeedReaderDbHelper().close();
    }
    
    public boolean deleteBudget(int id){
    	
    	mDbHelper = new FeedReaderDbHelper(context);
		
		// Gets the data repository in write mode
		db = mDbHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		values.put(FeedBudget.FOR_DELETION, 1);
		return db.update(FeedBudget.TABLE_NAME, values, FeedBudget._ID + "=" + id, null) > 0;
		
    }
    
    public Boolean BudgetsSurpassed() {
    	boolean surpassed = false;
		
		c2 = getBudget(User.USER_ID);
		
		c2.moveToFirst();
		
		while (!c2.isAfterLast()) {
			String productQuery = "SELECT SUM(" + FeedProduct.PRICE + ") AS sum" + 
							   	   " FROM " + FeedProduct.TABLE_NAME +
								   " WHERE " + FeedProduct.USER + "=" + User.USER_ID +
								   " AND " + FeedProduct.PURCHASE_DATE + " BETWEEN Date('" + c2.getString(c2.getColumnIndexOrThrow(FeedBudget.START_DATE)) + "')" +
								   " AND Date('" + c2.getString(c2.getColumnIndexOrThrow(FeedBudget.END_DATE)) + "')";
			
			if (!c2.getString(c2.getColumnIndexOrThrow(FeedBudget.EXPENSE_CATEGORY)).equals("All"))
				productQuery = productQuery +" AND " + FeedProduct.PRODUCT_CATEGORY + "='" + c2.getString(c2.getColumnIndexOrThrow(FeedBudget.EXPENSE_CATEGORY)) + "'";
			
			
			c1 = db.rawQuery(productQuery, null);
			
	        c1.moveToFirst();
	        
	        if (c1.getFloat(c1.getColumnIndexOrThrow("sum")) > c2.getFloat(c2.getColumnIndexOrThrow(FeedBudget.SPEND_LIMIT))) {
	        	
	        	ContentValues values = new ContentValues();
	        	values.put(FeedBudget.IS_SURPASSED, 1);
	        	db.update(FeedBudget.TABLE_NAME, values, FeedBudget._ID+"="+c2.getInt(c2.getColumnIndexOrThrow(FeedBudget._ID)), null);
	        	surpassed = true;
			}
	        c2.moveToNext();
		}

		c1.close();
		c2.close();
			
		getBudgetFeedReaderDbHelper().close();
		
    	return surpassed;
    }

    public FeedReaderDbHelper getBudgetFeedReaderDbHelper(){
    	return mDbHelper;
    }
    
}