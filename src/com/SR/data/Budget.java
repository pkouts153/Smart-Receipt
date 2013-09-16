package com.SR.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.SR.data.FeedReaderContract.FeedBudget;
import com.SR.data.FeedReaderContract.FeedProduct;

public class Budget {

	/*String EXPENSE_CATEGORY;
	Float SPEND_LIMIT;
    String START_DATE;
    String END_DATE;
    int NOTIFICATION;
    int USER;
    int FAMILY_USER;*/
    
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
			FeedBudget.NOTIFICATION,
			FeedBudget.USER,
			FeedBudget.FAMILY_USER,
		    };
		
		String where = "" + FeedBudget.USER + "=" + user_id;
		
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
    
    public void saveBudget(String category, Float limit, String from_date, String until_date, int n, int user_id, int family_id){
    	
    	mDbHelper = new FeedReaderDbHelper(context);
		
		// Gets the data repository in write mode
		db = mDbHelper.getWritableDatabase();
    	
		ContentValues values = new ContentValues();
		values.put(FeedBudget.EXPENSE_CATEGORY, category);
		values.put(FeedBudget.SPEND_LIMIT, limit);
		values.put(FeedBudget.START_DATE, from_date);
		values.put(FeedBudget.END_DATE, until_date);
		values.put(FeedBudget.NOTIFICATION, n);
		values.put(FeedBudget.USER, user_id);
		if (family_id != 0)
			values.put(FeedBudget.FAMILY_USER, family_id);
		
		db.insert(FeedBudget.TABLE_NAME, "null", values);
		values.clear();
    }
    
    public boolean isBudgetSurpassed() {
    	boolean surpassed = false;
    	
		/*String query = "SELECT " + FeedProduct.USER + ", SUM(" + FeedProduct.PRICE + ") as sum" + ", " + FeedBudget.START_DATE + ", " + FeedBudget.END_DATE +
				   	   " FROM " + FeedProduct.TABLE_NAME + ", " + FeedBudget.TABLE_NAME +
					   " WHERE " + FeedProduct.USER + "=" + User.USER_ID + " AND " + FeedBudget.USER + "=" + User.USER_ID +
					   " AND " + FeedProduct.PURCHASE_DATE + " BETWEEN Date('" + FeedBudget.START_DATE + "') AND Date('" + FeedBudget.END_DATE + "')" +
					   " GROUP BY " + FeedProduct.USER + 
					   " HAVING sum>(SELECT " + FeedBudget.SPEND_LIMIT + 
								 " FROM " + FeedBudget.TABLE_NAME +
								 " WHERE " + FeedBudget.USER + "=" + User.USER_ID + ")";*/
		
		c2 = getBudget(User.USER_ID);
		c2.moveToFirst();
		
		String productQuery = "SELECT SUM(" + FeedProduct.PRICE + ") AS sum" + 
						   	   " FROM " + FeedProduct.TABLE_NAME +
							   " WHERE " + FeedProduct.USER + "=" + User.USER_ID +
						   	   		" AND " + FeedProduct.PURCHASE_DATE + " BETWEEN Date('" + c2.getString(c2.getColumnIndexOrThrow(FeedBudget.START_DATE)) + "')" +
						   													" AND Date('" + c2.getString(c2.getColumnIndexOrThrow(FeedBudget.END_DATE)) + "')";
			
		c1 = db.rawQuery(productQuery, null);
        
        c1.moveToFirst();
        c2.moveToFirst();
		
        if (c1.getFloat(c1.getColumnIndexOrThrow("sum")) > c2.getFloat(c2.getColumnIndexOrThrow(FeedBudget.SPEND_LIMIT))){// && (p_day>=s_day && p_day<=e_day) && (p_month>=s_month && p_month<=e_month) && (p_year>=s_year && p_year<=e_year)) {
			
			surpassed = true;
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