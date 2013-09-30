package com.SR.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.SR.data.FeedReaderContract.FeedStore;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
* This class represents store and is responsible for the necessary processes
* 
* @author Panagiotis Koutsaftikis
*/
public class Store {
	
    SQLiteDatabase db;
    Cursor c;
    
    /**
    * Store constructor 
    * 
    * @param database   saves the database object, that was passed from the Activity, 
    * 					in the database object of the class for use in the methods
    */
    public Store(SQLiteDatabase database){
    	db = database;
    }   
	
	/**
	 * Gets stores from the database
	 * 
	 * @return a cursor with the stores
	 */
	public Cursor getStores(){

		// Specifies which columns are needed from the database
		String[] projection = {
			FeedStore._ID,
			FeedStore.VAT_NUMBER
		    };
		
		c = db.query(
			FeedStore.TABLE_NAME,  				  // The table to query
		    projection,                               // The columns to return
		    null,                                	  // The columns for the WHERE clause
		    null,                            		  // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    null                                 	  // The sort order
		    );
		
		return c;
    }
    
	/**
	 * Finds the id of a store whose VAT number is been given
	 * 
	 * @param VAT  the VAT number of the store
	 * @return the id of the store
	 */
    public int getId(String VAT){
    	
    	int id = 1;
    	
    	// if the user doesn't specify a VAT number in the save product screen 
    	//	then the store's id is 1, which is the id of the "Unknown store"
    	if (!VAT.equals("")) {
	    	c = getStores();
	    	
	    	c.moveToFirst();
	    	
	    	while (!c.isAfterLast ()){
	    		if (VAT.equals(c.getString(c.getColumnIndexOrThrow(FeedStore.VAT_NUMBER)))) {
	    			id = c.getInt(c.getColumnIndexOrThrow(FeedStore._ID));
	    		}
	    		c.moveToNext ();
	    	}
    	}
    	
    	return id;
    }

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
    public Store(){

    }   
	
	
	
	public String fetchStoreMaxId(SQLiteDatabase db){

		Cursor result = db.rawQuery("SELECT max("+FeedStore._ID+") AS max FROM "+FeedStore.TABLE_NAME+"", null);
		result.moveToNext();
		String lastId = result.getString(result.getColumnIndexOrThrow("max"));
		
		return lastId;
	}

	public void handleStoreJSONArray(JSONArray json, SQLiteDatabase db) throws JSONException{
		
		for(int i=0; i<json.length(); i++){
			
			JSONObject json_data =json.getJSONObject(i);

			String id = json_data.get("id").toString();
			String name = json_data.get("name").toString();
			String address = json_data.get("address").toString();
			String vat = json_data.get("afm").toString();
			String created = json_data.get("created").toString();
						
			insertStore(id, name, address, vat, created, db);
		}	
	}
	
	private void insertStore(String id, String name, String address, String vat, String created, SQLiteDatabase db){
		 
		db.execSQL("INSERT INTO "+FeedStore.TABLE_NAME+" ("+FeedStore._ID+", "+FeedStore.NAME+", "+FeedStore.ADDRESS+", "
															+FeedStore.VAT_NUMBER+", "+FeedStore.STORE_CREATED+")" +
				" VALUES ('"+id+"','"+name+"','"+address+"','"+vat+"','"+created+"')");
	}	
}