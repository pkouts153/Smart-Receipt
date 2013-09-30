package com.SR.data;

import com.SR.data.FeedReaderContract.FeedProduct;
import com.SR.data.FeedReaderContract.FeedStore;
import com.SR.data.FeedReaderContract.FeedUser;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
* This class is responsible for searching the database and returning the search results
* 
* @author Panagiotis Koutsaftikis
*/
public class SearchHandler {
	
    SQLiteDatabase db;
    
    // extracts the products details that match the user's typed data
    Cursor c;
    
    // extracts the total cost of the products returned
    Cursor sums;
    
    /**
    * SearchHandler constructor 
    * 
    * @param database   saves the database object, that was passed from the Activity, 
    * 					in the database object of the class for use in the methods
    */
	public SearchHandler(SQLiteDatabase database) {
		db = database;
	}
	
	/**
	 * Searches the database with the search data given by the user and returns the search results
	 * 
	 * @param group_name  if the user selects group_by then getSearchResults() is called as many times as the groups existing
	 * 						e.g. group_by="family_member" group_name="user"
	 * 						(if the user doesn't select a group_by, the group_name is null)
	 * 
	 * @return a cursor with the search results
	 */
	public Cursor getSearchResults(String product, String category, String min_cost, String max_cost, String start_date, String end_date, 
									String store, String family, String group_by, String group_name){
		   
		// represents the products of the search
		String query = "SELECT DISTINCT " + FeedProduct.TABLE_NAME + "." + FeedProduct._ID + ", " + FeedProduct.NAME + ", " + FeedProduct.PRICE + 
							", " + FeedProduct.PRODUCT_CATEGORY + ", " + FeedProduct.PURCHASE_DATE + 
							", " + FeedStore.NAME +
							", " + FeedUser.USERNAME +
					   " FROM " + FeedProduct.TABLE_NAME + ", " + FeedStore.TABLE_NAME + ", " + FeedUser.TABLE_NAME +
					   " WHERE " + FeedProduct.USER + "=" + FeedUser.TABLE_NAME + "." + FeedUser._ID + 
					       " AND " + FeedProduct.STORE + "=" + FeedStore.TABLE_NAME + "." + FeedStore._ID;
		
		// represents the cost of the group_by category of the search
		String sum_query = "SELECT DISTINCT SUM(" + FeedProduct.PRICE + ") as sum, " + FeedProduct.PRODUCT_CATEGORY + 
								", " + FeedStore.NAME +
								", " + FeedUser.USERNAME +
							" FROM " + FeedProduct.TABLE_NAME + ", " + FeedStore.TABLE_NAME + ", " + FeedUser.TABLE_NAME +
							" WHERE " + FeedProduct.USER + "=" + FeedUser.TABLE_NAME + "." + FeedUser._ID + 
								" AND " + FeedProduct.STORE + "=" + FeedStore.TABLE_NAME + "." + FeedStore._ID;
		
		// if the user typed a product name or keyword
		if (!(product.equals("")))
		{
			query = query + " AND " + FeedProduct.NAME + " LIKE '%" + product + "%'";
			sum_query = sum_query + " AND " + FeedProduct.NAME + " LIKE '%" + product + "%'";
		}
		
		// if the user selected a category (if he selects all don't search certain categories, which means do nothing)
		if (!(category.equals("")) && !(category.equals("All"))) {
			query = query + " AND " + FeedProduct.PRODUCT_CATEGORY + "='" + category + "'";
			sum_query = sum_query + " AND " + FeedProduct.PRODUCT_CATEGORY + "='" + category + "'";
		}
		
		// if the user typed a minimum product cost
		if (!(min_cost.equals(""))){
			query = query + " AND " + FeedProduct.PRICE + ">=" + Float.parseFloat(min_cost);
			sum_query = sum_query + " AND " + FeedProduct.PRICE + ">=" + Float.parseFloat(min_cost);
		}
		
		// if the user typed a maximum product cost
		if (!(max_cost.equals(""))){
			query = query + " AND " + FeedProduct.PRICE + "<=" + Float.parseFloat(max_cost);
			sum_query = sum_query + " AND " + FeedProduct.PRICE + "<=" + Float.parseFloat(max_cost);
		}
		
		// if the user typed a start date
		if (!(start_date.equals(""))) {
			query = query + " AND " + FeedProduct.PURCHASE_DATE + ">=Date('" + start_date + "')";
			sum_query = sum_query + " AND " + FeedProduct.PURCHASE_DATE + ">=Date('" + start_date + "')";
		}
		
		// if the user typed an end date
		if (!(end_date.equals(""))) {
			query = query + " AND " + FeedProduct.PURCHASE_DATE + "<=Date('" + end_date + "')";
			sum_query = sum_query + " AND " + FeedProduct.PURCHASE_DATE + "<=Date('" + end_date + "')";
		}
		
		
		// if the user doesn't selected a family member then search for the user that is logged in 
		if (family.equals("")) {
			query = query + " AND " + FeedProduct.USER + "= " + User.USER_ID + "";
			sum_query = sum_query + " AND " + FeedProduct.USER + "= " + User.USER_ID + "";

		}
		// else if the user selects all, do nothing (same as all categories)
		else if (family.equals("All")){
			
		}
		// else if the user selects a certain family member
		else {
			query = query + " AND " + FeedProduct.USER + "= (SELECT " + FeedUser._ID +
												  			" FROM " + FeedUser.TABLE_NAME +
												  			" WHERE "  + FeedUser.USERNAME + "='" + family + "')";
			
			sum_query = sum_query + " AND " + FeedProduct.USER + "= (SELECT " + FeedUser._ID +
					  									  			" FROM " + FeedUser.TABLE_NAME +
					  									  			" WHERE "  + FeedUser.USERNAME + "='" + family + "')";
		}
		
		// if the user typed a VAT number
		if (!(store.equals(""))) {
			query = query + " AND " + FeedProduct.STORE + "= (SELECT " + FeedStore._ID +
															 " FROM " + FeedStore.TABLE_NAME +
															 " WHERE " + FeedStore.VAT_NUMBER + "='" + store + "')";
			
			sum_query = sum_query + " AND " + FeedProduct.STORE + "= (SELECT " + FeedStore._ID +
																     " FROM " + FeedStore.TABLE_NAME +
																     " WHERE " + FeedStore.VAT_NUMBER + "='" + store + "')";
		}
		
		
		// if search results is contacted for a certain group
		// 		e.g. category=clothes
		if (group_name!=null) {
				query = query + " AND " + group_by + "='" + group_name +"'";
				sum_query = sum_query + " AND " + group_by + "='" + group_name +"'";
		}
		
		// if the user selects a group_by then the products (query) will be ordered by 
		//									and the costs (sum_query) will be grouped by that specific group_by selection
		if (!(group_by.equals(""))) {
			query = query + " ORDER BY " + group_by + ", " + FeedProduct.PURCHASE_DATE + " DESC, " + FeedProduct.NAME;
			sum_query = sum_query + " GROUP BY " + group_by;
		}
		// else order products by PURCHASE_DATE
		else
			query = query + " ORDER BY " + FeedProduct.PURCHASE_DATE + " DESC, " + FeedProduct.NAME + ", " + FeedProduct.PRODUCT_CATEGORY;
		
		c = db.rawQuery(query, null);
		sums = db.rawQuery(sum_query, null);
		
		return c;
	}
	
	/**
	 * 
	 * @return a cursor with the products total cost which was created in getSearchResults()
	 */
	public Cursor getSums(){
		return sums;
	}
	
}