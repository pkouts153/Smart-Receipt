package com.SR.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.SR.data.FeedReaderContract.FeedCategory;
import com.SR.data.FeedReaderContract.FeedStore;
import com.SR.data.FeedReaderContract.FeedUser;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class Store {
	
    FeedReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    Cursor c;
    
    Context context;
    
    public Store(Context c){
    	context = c;
    }   
	
	public Cursor getStores(){
    	
    	mDbHelper = new FeedReaderDbHelper(context);
		
		// Gets the data repository in write mode
		db = mDbHelper.getWritableDatabase();
    	
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
    
    public int getId(String VAT){
    	
    	int id = 1;
    	
    	if (!VAT.equals("")) {
	    	c = getStores();
	    	
	    	c.moveToFirst();
	    	
	    	while (!c.isAfterLast ()){
	    		if (VAT.equals(c.getString(c.getColumnIndexOrThrow(FeedStore.VAT_NUMBER)))) {
	    			id = c.getInt(c.getColumnIndexOrThrow(FeedStore._ID));
	    		}
	    		c.moveToNext ();
	    	}
	    	
	    	getStoreFeedReaderDbHelper().close();
    	}
    	
    	return id;
    }
	
    public FeedReaderDbHelper getStoreFeedReaderDbHelper(){
    	return mDbHelper;
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*public String fetchStoreMaxId(SQLiteDatabase db){

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
	}	*/
}