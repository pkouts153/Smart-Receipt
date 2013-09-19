package com.SR.data;

import com.SR.data.FeedReaderContract.FeedProduct;
import com.SR.data.FeedReaderContract.FeedStore;
import com.SR.data.FeedReaderContract.FeedUser;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SearchHandler {
	
    FeedReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    Cursor c;
    
    Context context;
    
	public SearchHandler(Context c) {
		context = c;
	}

	public Cursor getSearchResults(String product, String category, String min_cost, String max_cost, String start_date, String end_date, 
									String store, String family, String group_by){
		
		mDbHelper = new FeedReaderDbHelper(context);
		
		// Gets the data repository in write mode
		db = mDbHelper.getWritableDatabase();
		
		String query = "SELECT " + FeedProduct.TABLE_NAME + "." + FeedProduct._ID + ", " + FeedProduct.NAME + ", " + FeedProduct.PRICE + 
							", " + FeedProduct.PRODUCT_CATEGORY + ", " + FeedProduct.PURCHASE_DATE + ", " + FeedStore.NAME +
					   " FROM " + FeedProduct.TABLE_NAME + ", " + FeedStore.TABLE_NAME;
		
		if (!(product.equals("")))
			query = query + " WHERE " + FeedProduct.NAME + "='" + product + "'";
		
		if (!(category.equals(""))) {
			if (!(product.equals("")))
				query = query + " AND ";
			else
				query = query + " WHERE ";
			query = query + FeedProduct.PRODUCT_CATEGORY + "='" + category + "'";
		}
		
		if (!(min_cost.equals(""))){
			if (!(product.equals("")) || !(category.equals("")))
				query = query + " AND ";
			else
				query = query + " WHERE ";
			query = query + FeedProduct.PRICE + ">=" + min_cost;
		}
		
		if (!(max_cost.equals(""))){
			if (!(product.equals("")) || !(category.equals("")) || !(min_cost.equals("")))
				query = query + " AND ";
			else
				query = query + " WHERE ";
			query = query + FeedProduct.PRICE + "<=" + max_cost;
		}
		
		if (!(start_date.equals(""))) {
			if (!(product.equals("")) || !(category.equals("")) || !(min_cost.equals("")) || !(max_cost.equals("")))
				query = query + " AND ";
			else
				query = query + " WHERE ";
			query = query + FeedProduct.PURCHASE_DATE + ">=Date('" + start_date + "')";
		}
		
		if (!(end_date.equals(""))) {
			if (!(product.equals("")) || !(category.equals("")) || !(min_cost.equals("")) || !(max_cost.equals("")) || !(start_date.equals("")))
				query = query + " AND ";
			else
				query = query + " WHERE ";
			query = query + FeedProduct.PURCHASE_DATE + "<=Date('" + end_date + "')";
		}
		
		
		
		
		if (!(store.equals(""))) {
			if (!(product.equals("")) || !(category.equals("")) || !(min_cost.equals("")) || !(max_cost.equals("")) || !(start_date.equals("")) || !(end_date.equals("")))
				query = query + " AND ";
			else
				query = query + " WHERE ";
			query = query + FeedProduct.STORE + "= (SELECT " + FeedStore._ID +
													" FROM " + FeedStore.TABLE_NAME +
													" WHERE " + FeedStore.VAT_NUMBER + "='" + store + "') AND " + FeedStore.VAT_NUMBER + "=" + store;
		}
		else {
			if (!(product.equals("")) || !(category.equals("")) || !(min_cost.equals("")) || !(max_cost.equals("")) || !(start_date.equals("")) || !(end_date.equals("")))
				query = query + " AND ";
			else
				query = query + " WHERE ";
			query = query + FeedProduct.STORE + "=" + FeedStore.TABLE_NAME + "." + FeedStore._ID + " OR " + FeedProduct.STORE + " is null";
		}
		
		
		
		
		if (!(family.equals("")) && !family.equals("All")) {
			if (!(product.equals("")) || !(category.equals("")) || !(min_cost.equals("")) || !(max_cost.equals("")) || !(start_date.equals("")) || !(end_date.equals("")) || !(store.equals("")))
				query = query + " AND ";
			else
				query = query + " WHERE ";
			query = query + FeedProduct.USER + "= (SELECT " + FeedUser._ID +
												  " FROM " + FeedUser.TABLE_NAME +
												  " WHERE "  + FeedUser.USERNAME + "='" + family + "')";
		}
		else if (family.equals("All")){
			//do nothing
		}
		else {
			if (!(product.equals("")) || !(category.equals("")) || !(min_cost.equals("")) || !(max_cost.equals("")) || !(start_date.equals("")) || !(end_date.equals("")) || !(store.equals("")))
				query = query + " AND ";
			else
				query = query + " WHERE ";
			query = query + FeedProduct.USER + "= " + User.USER_ID + "";
		}
		
		
		
		
		if (!(group_by.equals(""))) {
			if (group_by.equals("Category"))
				query = query + " GROUP BY " + FeedProduct.PRODUCT_CATEGORY;
			else if (group_by.equals("Store"))
				query = query + " GROUP BY " + FeedProduct.STORE;
			else
				query = query + " GROUP BY " + FeedProduct.USER;
		}
		Log.w("", query);
		c = db.rawQuery(query, null);
		
		return c;
	}
	
    public FeedReaderDbHelper getSearchFeedReaderDbHelper(){
    	return mDbHelper;
    }
	
}