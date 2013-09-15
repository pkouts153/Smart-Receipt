package com.SR.data;

import java.util.Calendar;
import java.util.StringTokenizer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.SR.data.FeedReaderContract.FeedBudget;
import com.SR.data.FeedReaderContract.FeedProduct;
import com.SR.data.FeedReaderContract.FeedUser;

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
    Cursor c;
    
    Context context;
    
	public Budget(Context c) {
		context = c;
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
    	
    	mDbHelper = new FeedReaderDbHelper(context);
		
		// Gets the data repository in write mode
		db = mDbHelper.getWritableDatabase();
    	
		String query = "SELECT SUM(" + FeedProduct.PRICE + ") as sum" + //", " + FeedBudget.START_DATE + ", " + FeedBudget.END_DATE +
				   	   " FROM " + FeedProduct.TABLE_NAME +// ", " + FeedBudget.TABLE_NAME +
					   " WHERE " + FeedProduct.USER + "=" + User.USER_ID + " AND " + 
					   	   "" + " AND " +
						   
						   "sum>(SELECT " + FeedBudget.SPEND_LIMIT + 
								 " FROM " + FeedBudget.TABLE_NAME +
								 " WHERE " + FeedBudget.USER + "=" + User.USER_ID + ")"; 
	

		c = db.rawQuery(query, null);
		
		/*Calendar calendar = Calendar.getInstance();
        int c_year = calendar.get(Calendar.YEAR);
        int c_month = calendar.get(Calendar.MONTH);
        int c_day = calendar.get(Calendar.DAY_OF_MONTH);
		
        String start_date = c.getString(c.getColumnIndexOrThrow(FeedBudget.START_DATE));
        
        StringTokenizer tokens = new StringTokenizer(start_date, "/");
        int s_day = Integer.parseInt(tokens.nextToken());
        int s_month = Integer.parseInt(tokens.nextToken());
        int s_year = Integer.parseInt(tokens.nextToken());
        
        String end_date = c.getString(c.getColumnIndexOrThrow(FeedBudget.END_DATE));
        
        StringTokenizer tokens1 = new StringTokenizer(end_date, "/");
        int e_day = Integer.parseInt(tokens.nextToken());
        int e_month = Integer.parseInt(tokens.nextToken());
        int e_year = Integer.parseInt(tokens.nextToken());*/
        
        
		if (c.moveToFirst()){ //&&  && 
			//c.getFloat(c.getColumnIndexOrThrow("sum")) > c.getFloat(c.getColumnIndexOrThrow(FeedBudget.SPEND_LIMIT))) {
		    
			surpassed = true;
		}

    	return surpassed;
    }
    
    public FeedReaderDbHelper getBudgetFeedReaderDbHelper(){
    	return mDbHelper;
    }
    
}