package com.SR.data;

import com.SR.data.FeedReaderContract.FeedProduct;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SearchHandler {
	
    FeedReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    Cursor c;
    
    Context context;
    
	public SearchHandler(Context c) {
		context = c;
	}

	public Cursor getSearchResults(String product, String category, Float p_cost, String start_date, String end_date, 
									String store, String family, String group_by){
		
		mDbHelper = new FeedReaderDbHelper(context);
		
		// Gets the data repository in write mode
		db = mDbHelper.getWritableDatabase();
		
		String query = "SELECT * " +
					   "FROM " + FeedProduct.TABLE_NAME;
		
		
		c = db.rawQuery(query, null);
		
		return c;
	}
	
    public FeedReaderDbHelper getSearchFeedReaderDbHelper(){
    	return mDbHelper;
    }
	
}