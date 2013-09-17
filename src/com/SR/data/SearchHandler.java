package com.SR.data;

import com.SR.data.FeedReaderContract.FeedProduct;
import com.SR.data.FeedReaderContract.FeedStore;
import com.SR.data.FeedReaderContract.FeedUser;

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

	public Cursor getSearchResults(String product, String category, Float min_cost, Float max_cost, String start_date, String end_date, 
									String store, String family, String group_by){
		
		mDbHelper = new FeedReaderDbHelper(context);
		
		// Gets the data repository in write mode
		db = mDbHelper.getWritableDatabase();
		
		String query = "SELECT *, SUM(" + FeedProduct.PRICE + ") as sum " +
					   "FROM " + FeedProduct.TABLE_NAME;
		
		if (product!=null) 
			query = query + " WHERE " + FeedProduct.NAME + "=" + product;
		
		if (category!=null) {
			if (product!=null)
				query = query + " AND ";
			else
				query = query + " WHERE ";
			query = query + FeedProduct.PRODUCT_CATEGORY + "=" + category;
		}
		
		if (min_cost!=0){
			if (product!=null || category!=null)
				query = query + " AND ";
			else
				query = query + " WHERE ";
			query = query + FeedProduct.PRICE + ">=" + min_cost;
		}
		
		if (max_cost!=0){
			if (product!=null || category!=null || min_cost!=0)
				query = query + " AND ";
			else
				query = query + " WHERE ";
			query = query + FeedProduct.PRICE + "<=" + max_cost;
		}
		
		if (start_date!=null) {
			if (product!=null || category!=null || min_cost!=0 || max_cost!=0)
				query = query + " AND ";
			else
				query = query + " WHERE ";
			query = query + FeedProduct.PURCHASE_DATE + ">=Date('" + start_date + "')";
		}
		
		if (end_date!=null) {
			if (product!=null || category!=null || min_cost!=0 || max_cost!=0 || start_date!=null)
				query = query + " AND ";
			else
				query = query + " WHERE ";
			query = query + FeedProduct.PURCHASE_DATE + "<=Date('" + end_date + "')";
		}
		
		if (store!=null) {
			if (product!=null || category!=null || min_cost!=0 || max_cost!=0 || start_date!=null || end_date!=null)
				query = query + " AND ";
			else
				query = query + " WHERE ";
			query = query + FeedProduct.STORE + "= (SELECT " + FeedStore._ID +
													" FROM " + FeedStore.TABLE_NAME +
													" WHERE " + FeedStore.VAT_NUMBER + "='" + store + "')";
		}
		
		if (family!=null) {
			if (product!=null || category!=null || min_cost!=0 || max_cost!=0 || start_date!=null || end_date!=null || store!=null)
				query = query + " AND ";
			else
				query = query + " WHERE ";
			query = query + FeedProduct.USER + "= (SELECT " + FeedUser._ID +
												  " FROM " + FeedUser.TABLE_NAME +
												  " WHERE "  + FeedUser.USERNAME + "='" + family + "')";
		}
		
		if (group_by!=null) {
			if (group_by.equals("Category"))
				query = query + " GROUP BY product_category";
			else if (group_by.equals("Store"))
				query = query + " GROUP BY store";
			else
				query = query + " GROUP BY user";
		}
		
		c = db.rawQuery(query, null);
		
		return c;
	}
	
    public FeedReaderDbHelper getSearchFeedReaderDbHelper(){
    	return mDbHelper;
    }
	
}