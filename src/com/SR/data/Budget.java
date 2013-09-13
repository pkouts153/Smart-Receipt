package com.SR.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.SR.data.FeedReaderContract.FeedBudget;

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
    
    
    public FeedReaderDbHelper getBudgetFeedReaderDbHelper(){
    	return mDbHelper;
    }
    
}