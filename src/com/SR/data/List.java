package com.SR.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.SR.data.FeedReaderContract.FeedList;

/**
* This class represents a user's shopping list and is responsible for the necessary processes
* 
* @author Παναγιώτης Κουτσαυτίκης 8100062
*/
public class List {
	
    SQLiteDatabase db;
    Cursor c;
   
    
    /**
    * List constructor 
    * 
    * @param database   saves the database object, that was passed from the Activity, 
    * 					in the database object of the class for use in the methods
    */
	public List(SQLiteDatabase database) {
		db = database;
	}


	/**
	 * Gets user's shopping list from the database
	 * 
	 * @return a cursor with the list's products
	 */
    public Cursor getList(){

		// Specifies which columns are needed from the database
		String[] projection = {
			FeedList._ID,
			FeedList.PRODUCT,
			FeedList.IS_CHECKED
		    };
		
		String where = "" + FeedList.USER +"=" + User.USER_ID;
		
		c = db.query(
			FeedList.TABLE_NAME,  				  // The table to query
		    projection,                               // The columns to return
		    where,                                	  // The columns for the WHERE clause
		    null,                            		  // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    null                                 	  // The sort order
		    );
		
		return c;
    }
    
	/**
	 * Saves a new product to the user's shopping list
	 * 
	 * @param product  the product to be saved
     * @return whether the save was made successfully
	 */
    public boolean addProductToList(String product) {

		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		
		// if even one product was not saved successfully it will be false
		boolean successful_save = true;
		
		values.put(FeedList.USER, User.USER_ID);
		values.put(FeedList.PRODUCT, product);
		values.put(FeedList.IS_CHECKED, 0);
		
		if (!(db.insert(FeedList.TABLE_NAME, null, values)>0))
			successful_save = false;
		
		return successful_save;
    }
    
    /**
     * Deletes a product from the user's list
     * 
     * @param id  the id of the product to be deleted
     * @return whether the deletion was made successfully
     */
    public boolean deleteProductFromList(int id){
		
		return db.delete(FeedList.TABLE_NAME, 
						FeedList._ID + "=" + id,
						null) > 0;
		
    }
    
    /**
     * Update a product's check value
     * 
     * @param id  the id of the product to be checked
     */
    public void checkProductOfList(int id) {
		ContentValues values = new ContentValues();
		values.put(FeedList.IS_CHECKED, 1);
		db.update(FeedList.TABLE_NAME, 
				  values, 
				  FeedList._ID + "=" + id,
				  null);
    }
    
    /**
     * Update a product's check value
     * 
     * @param id  the id of the product to be unchecked
     */
    public void uncheckProductOfList(int id) {
		ContentValues values = new ContentValues();
		values.put(FeedList.IS_CHECKED, 0);
		
		db.update(FeedList.TABLE_NAME, 
				  values, 
				  FeedList._ID + "=" + id,
				  null);
    }
}
