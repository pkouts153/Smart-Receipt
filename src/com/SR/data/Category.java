package com.SR.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.SR.data.FeedReaderContract.FeedCategory;

/**
* This class represents category and is responsible for the necessary processes
* 
* @author Panagiotis Koutsaftikis
*/
public class Category {
	
    SQLiteDatabase db;
    Cursor c;
    
    /**
    * Category constructor 
    * 
    * @param database   saves the database object, that was passed from the Activity, 
    * 					in the database object of the class for use in the methods
    */
    public Category(SQLiteDatabase database){
    	db = database;
    }
	
	/**
	 * Gets categories from the database
	 * 
	 * @return a cursor with the categories
	 */
    public Cursor getCategories(){

		// Specifies which columns are needed from the database
		String[] projection = {
			FeedCategory.NAME
		    };
		
		c = db.query(
			FeedCategory.TABLE_NAME,  				  // The table to query
		    projection,                               // The columns to return
		    null,                                	  // The columns for the WHERE clause
		    null,                            		  // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    null                                 	  // The sort order
		    );
		
		return c;
    }
    

    
    
    public Category() {
    	
	}
    
    public String fetchCategoryMaxId(SQLiteDatabase db){

		Cursor result = db.rawQuery("SELECT max("+FeedCategory._ID+") AS max FROM "+FeedCategory.TABLE_NAME+"", null);
		result.moveToNext();
		String lastId = result.getString(result.getColumnIndexOrThrow("max"));
		
		return lastId;
	}
	
	public void handleCategoryJSONArray(JSONArray json, SQLiteDatabase db) throws JSONException{
		
		for(int i=0; i<json.length(); i++){
			
			JSONObject json_data =json.getJSONObject(i);

			String id = json_data.get("id").toString();
			String name = json_data.get("name").toString();
			String created = json_data.get("created").toString();
						
			insertCategory(id, name, created, db);
		}	
	}

	private void insertCategory(String id, String name, String created, SQLiteDatabase db){
			 
		db.execSQL("INSERT INTO "+FeedCategory.TABLE_NAME+" ("+FeedCategory._ID+", "+FeedCategory.NAME+" ,"+FeedCategory.CATEGORY_CREATED+")" +
					" VALUES ('"+id+"','"+name+"','"+created+"')");
	}

}
