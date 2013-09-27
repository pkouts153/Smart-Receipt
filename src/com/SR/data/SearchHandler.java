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
    Cursor c32;
    Context context;
    
	public SearchHandler(Context c) {
		context = c;
	}

	public Cursor getSearchResults(String product, String category, String min_cost, String max_cost, String start_date, String end_date, 
									String store, String family, String group_by, String group_name){
		
		mDbHelper = new FeedReaderDbHelper(context);
		
		// Gets the data repository in write mode
		db = mDbHelper.getWritableDatabase();
		   
		   
		String query = "SELECT DISTINCT " + FeedProduct.TABLE_NAME + "." + FeedProduct._ID + ", " + FeedProduct.NAME + ", " + FeedProduct.PRICE + 
							", " + FeedProduct.PRODUCT_CATEGORY + ", " + FeedProduct.PURCHASE_DATE + 
							", " + FeedStore.NAME +
							", " + FeedUser.USERNAME +
					   " FROM " + FeedProduct.TABLE_NAME + ", " + FeedStore.TABLE_NAME + ", " + FeedUser.TABLE_NAME +
					   " WHERE " + FeedProduct.USER + "=" + FeedUser.TABLE_NAME + "." + FeedUser._ID + 
					       " AND " + FeedProduct.STORE + "=" + FeedStore.TABLE_NAME + "." + FeedStore._ID;// + " OR " + FeedProduct.STORE + " IS null)";
		
		String sum_query = "SELECT DISTINCT SUM(" + FeedProduct.PRICE + ") as sum, " + FeedProduct.PRODUCT_CATEGORY + 
								", " + FeedStore.NAME +
								", " + FeedUser.USERNAME +
							" FROM " + FeedProduct.TABLE_NAME + ", " + FeedStore.TABLE_NAME + ", " + FeedUser.TABLE_NAME +
							" WHERE " + FeedProduct.USER + "=" + FeedUser.TABLE_NAME + "." + FeedUser._ID + 
								" AND " + FeedProduct.STORE + "=" + FeedStore.TABLE_NAME + "." + FeedStore._ID;// + " OR " + FeedProduct.STORE + " IS null)";
		
		if (!(product.equals("")))
		{
			query = query + " AND " + FeedProduct.NAME + " LIKE '%" + product + "%'";
			sum_query = sum_query + " AND " + FeedProduct.NAME + " LIKE '%" + product + "%'";
		}
		
		if (!(category.equals("")) && !(category.equals("All"))) {
			query = query + " AND " + FeedProduct.PRODUCT_CATEGORY + "='" + category + "'";
			sum_query = sum_query + " AND " + FeedProduct.PRODUCT_CATEGORY + "='" + category + "'";
		}
		
		if (!(min_cost.equals(""))){
			query = query + " AND " + FeedProduct.PRICE + ">=" + Float.parseFloat(min_cost);
			sum_query = sum_query + " AND " + FeedProduct.PRICE + ">=" + Float.parseFloat(min_cost);
		}
		
		if (!(max_cost.equals(""))){
			query = query + " AND " + FeedProduct.PRICE + "<=" + Float.parseFloat(max_cost);
			sum_query = sum_query + " AND " + FeedProduct.PRICE + "<=" + Float.parseFloat(max_cost);
		}
		
		if (!(start_date.equals(""))) {
			query = query + " AND " + FeedProduct.PURCHASE_DATE + ">=Date('" + start_date + "')";
			sum_query = sum_query + " AND " + FeedProduct.PURCHASE_DATE + ">=Date('" + start_date + "')";
		}
		
		if (!(end_date.equals(""))) {
			query = query + " AND " + FeedProduct.PURCHASE_DATE + "<=Date('" + end_date + "')";
			sum_query = sum_query + " AND " + FeedProduct.PURCHASE_DATE + "<=Date('" + end_date + "')";
		}
		
		if (family.equals("")) {
			query = query + " AND " + FeedProduct.USER + "= " + User.USER_ID + "";
			sum_query = sum_query + " AND " + FeedProduct.USER + "= " + User.USER_ID + "";

		}
		else if (family.equals("All")){
			
		}
		else {
			query = query + " AND " + FeedProduct.USER + "= (SELECT " + FeedUser._ID +
												  " FROM " + FeedUser.TABLE_NAME +
												  " WHERE "  + FeedUser.USERNAME + "='" + family + "')";
			
			sum_query = sum_query + " AND " + FeedProduct.USER + "= (SELECT " + FeedUser._ID +
					  									  " FROM " + FeedUser.TABLE_NAME +
					  									  " WHERE "  + FeedUser.USERNAME + "='" + family + "')";
		}
		
		if (!(store.equals(""))) {
			query = query + //" AND " + FeedProduct.STORE + " IS NOT NULL " + 
							" AND " + FeedProduct.STORE + "= (SELECT " + FeedStore._ID +
															 " FROM " + FeedStore.TABLE_NAME +
															 " WHERE " + FeedStore.VAT_NUMBER + "='" + store + "')";// AND " + FeedStore.VAT_NUMBER + "=" + store;
			
			sum_query = sum_query + //" AND " + FeedProduct.STORE + " IS NOT NULL " + 
									" AND " + FeedProduct.STORE + "= (SELECT " + FeedStore._ID +
																     " FROM " + FeedStore.TABLE_NAME +
																     " WHERE " + FeedStore.VAT_NUMBER + "='" + store + "')";// AND " + FeedStore.VAT_NUMBER + "='" + store + "'";
		}
		
		
		if (group_name!=null) {
			
			//if (!group_by.equals("store_name")){
				query = query + " AND " + group_by + "='" + group_name +"'";
				sum_query = sum_query + " AND " + group_by + "='" + group_name +"'";
			/*}
			
			else {
				if (!group_name.equals("Unknown store")){
					query = query + " AND " + FeedProduct.STORE +" IS NOT NULL AND " + group_by + "='" + group_name +"'";
					sum_query = sum_query + " AND " + FeedProduct.STORE +" IS NOT NULL AND " + group_by + "='" + group_name +"'";
				}
				else{
					query = query + " AND " + FeedProduct.STORE +" IS NULL ";
					sum_query = sum_query + " AND " + FeedProduct.STORE +" IS NULL ";
				}
			}*/
			
			
				
		}
		
		if (!(group_by.equals(""))) {
			query = query + " ORDER BY " + group_by + ", " + FeedProduct.PURCHASE_DATE + " DESC, " + FeedProduct.NAME;
			sum_query = sum_query + " GROUP BY " + group_by;
		}
		else
			query = query + " ORDER BY " + FeedProduct.PURCHASE_DATE + " DESC, " + FeedProduct.NAME + ", " + FeedProduct.PRODUCT_CATEGORY;
		
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