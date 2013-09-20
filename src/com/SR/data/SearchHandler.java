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
    Cursor sums;
    
    Context context;
    
	public SearchHandler(Context c) {
		context = c;
	}

	public Cursor getSearchResults(String product, String category, String min_cost, String max_cost, String start_date, String end_date, 
									String store, String family, String group_by){
		
		mDbHelper = new FeedReaderDbHelper(context);
		
		// Gets the data repository in write mode
		db = mDbHelper.getWritableDatabase();
		
		String query = "SELECT DISTINCT " + FeedProduct.TABLE_NAME + "." + FeedProduct._ID + ", " + FeedProduct.NAME + ", " + FeedProduct.PRICE + 
							", " + FeedProduct.PRODUCT_CATEGORY + ", " + FeedProduct.PURCHASE_DATE + //", " + FeedStore.NAME + 
							//", " + FeedUser.USERNAME +
					   " FROM " + FeedProduct.TABLE_NAME;// + ", " + FeedStore.TABLE_NAME + ", " + FeedUser.TABLE_NAME;
		
		String sum_query = "SELECT SUM(" + FeedProduct.PRICE + ") as sum, " + FeedProduct.PRODUCT_CATEGORY + ", " + FeedStore.NAME + ", " + FeedUser.USERNAME +
							" FROM " + FeedProduct.TABLE_NAME + ", " + FeedStore.TABLE_NAME + ", " + FeedUser.TABLE_NAME;
		
		if (!(product.equals("")))
		{
			query = query + " WHERE " + FeedProduct.NAME + "='" + product + "'";
			sum_query = sum_query + " WHERE " + FeedProduct.NAME + "='" + product + "'";
		}
		
		if (!(category.equals(""))) {
			if (!(product.equals(""))) {
				query = query + " AND ";
				sum_query = sum_query + " AND ";
			}
			else {
				query = query + " WHERE ";
				sum_query = sum_query + " WHERE ";
			}
			query = query + FeedProduct.PRODUCT_CATEGORY + "='" + category + "'";
			sum_query = sum_query + FeedProduct.PRODUCT_CATEGORY + "='" + category + "'";
		}
		
		if (!(min_cost.equals(""))){
			if (!(product.equals("")) || !(category.equals(""))) {
				query = query + " AND ";
				sum_query = sum_query + " AND ";
			}
			else{
				query = query + " WHERE ";
				sum_query = sum_query + " WHERE ";
			}
			query = query + FeedProduct.PRICE + ">=" + Float.parseFloat(min_cost);
			sum_query = sum_query + FeedProduct.PRICE + ">=" + Float.parseFloat(min_cost);
		}
		
		if (!(max_cost.equals(""))){
			if (!(product.equals("")) || !(category.equals("")) || !(min_cost.equals(""))) {
				query = query + " AND ";
				sum_query = sum_query + " AND ";
			}
			else{
				query = query + " WHERE ";
				sum_query = sum_query + " WHERE ";
			}
			query = query + FeedProduct.PRICE + "<=" + Float.parseFloat(max_cost);
			sum_query = sum_query + FeedProduct.PRICE + "<=" + Float.parseFloat(max_cost);
		}
		
		if (!(start_date.equals(""))) {
			if (!(product.equals("")) || !(category.equals("")) || !(min_cost.equals("")) || !(max_cost.equals(""))) {
				query = query + " AND ";
				sum_query = sum_query + " AND ";
			}
			else{
				query = query + " WHERE ";
				sum_query = sum_query + " WHERE ";
			}
			query = query + FeedProduct.PURCHASE_DATE + ">=Date('" + start_date + "')";
			sum_query = sum_query + FeedProduct.PURCHASE_DATE + ">=Date('" + start_date + "')";
		}
		
		if (!(end_date.equals(""))) {
			if (!(product.equals("")) || !(category.equals("")) || !(min_cost.equals("")) || !(max_cost.equals("")) || !(start_date.equals(""))) {
				query = query + " AND ";
				sum_query = sum_query + " AND ";
			}
			else{
				query = query + " WHERE ";
				sum_query = sum_query + " WHERE ";
			}
			query = query + FeedProduct.PURCHASE_DATE + "<=Date('" + end_date + "')";
			sum_query = sum_query + FeedProduct.PURCHASE_DATE + "<=Date('" + end_date + "')";
		}
		
		
		
		if (family.equals("")) {
			if (!(product.equals("")) || !(category.equals("")) || !(min_cost.equals("")) || !(max_cost.equals("")) || !(start_date.equals("")) || !(end_date.equals(""))) {
				query = query + " AND ";
			    sum_query = sum_query + " AND ";
			}
			else{
				query = query + " WHERE ";
				sum_query = sum_query + " WHERE ";
			}
			query = query + FeedProduct.USER + "= " + User.USER_ID + "";
			sum_query = sum_query + FeedProduct.USER + "= " + User.USER_ID + "";

		}
		else if (family.equals("All")){
			if (!(product.equals("")) || !(category.equals("")) || !(min_cost.equals("")) || !(max_cost.equals("")) || !(start_date.equals("")) || !(end_date.equals(""))) {
				query = query + " AND ";
				sum_query = sum_query + " AND ";
			}
			else{
				query = query + " WHERE ";
				sum_query = sum_query + " WHERE ";
			}
			query = query + FeedProduct.USER + "=" + FeedUser.TABLE_NAME + "." + FeedUser._ID + "";
			sum_query = sum_query + FeedProduct.USER + "= " + FeedUser.TABLE_NAME + "." + FeedUser._ID + "";
		}
		else {
			if (!(product.equals("")) || !(category.equals("")) || !(min_cost.equals("")) || !(max_cost.equals("")) || !(start_date.equals("")) || !(end_date.equals(""))) {
				query = query + " AND ";
				sum_query = sum_query + " AND ";
			}
			else{
				query = query + " WHERE ";
				sum_query = sum_query + " WHERE ";
			}
			query = query + FeedProduct.USER + "= (SELECT " + FeedUser._ID +
												  " FROM " + FeedUser.TABLE_NAME +
												  " WHERE "  + FeedUser.USERNAME + "='" + family + "')";
			
			sum_query = sum_query + FeedProduct.USER + "= (SELECT " + FeedUser._ID +
					  									  " FROM " + FeedUser.TABLE_NAME +
					  									  " WHERE "  + FeedUser.USERNAME + "='" + family + "')";
		}
		
		
		if (!(store.equals(""))) {
			query = query + " AND " + FeedProduct.STORE + "= (SELECT " + FeedStore._ID +
															 " FROM " + FeedStore.TABLE_NAME +
															 " WHERE " + FeedStore.VAT_NUMBER + "='" + store + "')";// AND " + FeedStore.VAT_NUMBER + "=" + store;
			
			sum_query = sum_query + " AND " + FeedProduct.STORE + "= (SELECT " + FeedStore._ID +
																     " FROM " + FeedStore.TABLE_NAME +
																     " WHERE " + FeedStore.VAT_NUMBER + "='" + store + "') AND " + FeedStore.VAT_NUMBER + "=" + store;
		}
		/*else {
			
			query = query + " AND (" + FeedProduct.STORE + "=" + FeedStore.TABLE_NAME + "." + FeedStore._ID + " OR " + FeedProduct.STORE + " is null)";
			sum_query = sum_query + " AND (" + FeedProduct.STORE + "=" + FeedStore.TABLE_NAME + "." + FeedStore._ID + " OR " + FeedProduct.STORE + " is null)";
		}*/
		
		
		if (!(group_by.equals(""))) {
			if (group_by.equals("product_category")) {
				query = query + " ORDER BY " + FeedProduct.PRODUCT_CATEGORY;// + ", " + FeedProduct.PURCHASE_DATE + ", " + FeedProduct.NAME;
				sum_query = sum_query + " GROUP BY " + FeedProduct.PRODUCT_CATEGORY;
			}
			else if (group_by.equals("store_name")) {
				query = query + " ORDER BY " + FeedStore.NAME;// + ", " + FeedProduct.PURCHASE_DATE + ", " + FeedProduct.NAME;
				sum_query = sum_query + " GROUP BY " + FeedStore.NAME;
			}
			else {
				query = query + " ORDER BY " + FeedUser.USERNAME;// + ", " + FeedProduct.PURCHASE_DATE + ", " + FeedProduct.NAME;
				sum_query = sum_query + " GROUP BY " + FeedUser.USERNAME;
			}
		}
		else
			query = query + " ORDER BY " + FeedProduct.PURCHASE_DATE;// + ", " + FeedProduct.NAME + ", " + FeedProduct.PRODUCT_CATEGORY;
		
		Log.w("", query);
		Log.w("", sum_query);
		
		c = db.rawQuery(query, null);
		sums = db.rawQuery(sum_query, null);
		
		return c;
	}
	
	public Cursor getSums(){
		return sums;
	}
	
    public FeedReaderDbHelper getSearchFeedReaderDbHelper(){
    	return mDbHelper;
    }
	
}