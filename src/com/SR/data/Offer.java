package com.SR.data;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.SR.data.FeedReaderContract.FeedOffer;
import com.SR.processes.Functions;

public class Offer {
	
    SQLiteDatabase db;
    Cursor c;
    
    public Offer(SQLiteDatabase database){
    	db = database;
    }
    
    public Cursor getOffers(){

		// Specifies which columns are needed from the database
		String[] projection = {
			FeedOffer._ID,	
			FeedOffer.PRODUCT_NAME,
			FeedOffer.CATEGORY,
			FeedOffer.PRICE,
			FeedOffer.DISCOUNT,
			FeedOffer.UNTIL_DATE,
			FeedOffer.STORE
		    };
		
		c = db.query(
			FeedOffer.TABLE_NAME,  				  // The table to query
		    projection,                               // The columns to return
		    null,                                	  // The columns for the WHERE clause
		    null,                            		  // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    null                                 	  // The sort order
		    );
		
		return c;
    }
    

    
    
    public Offer() {
    	
	}
    
    public String fetchOfferMaxId(SQLiteDatabase db){

		Cursor result = db.rawQuery("SELECT max("+FeedOffer._ID+") AS max FROM "+FeedOffer.TABLE_NAME+"", null);
		result.moveToNext();
		String lastId = result.getString(result.getColumnIndexOrThrow("max"));
		
		return lastId;
	}
	
	public void handleOfferJSONArray(JSONArray json, SQLiteDatabase db) throws JSONException{
		
		for(int i=0; i<json.length(); i++){
			
			JSONObject json_data =json.getJSONObject(i);

			String id = json_data.get("id").toString();
			String name = json_data.get("name").toString();
			String category = json_data.get("category").toString();
			String price = json_data.get("price").toString();
			String discount = json_data.get("discount").toString();
			String date = json_data.get("date").toString();
			String store = json_data.get("store").toString();
	        String created = json_data.get("created").toString();
			
	        date = new Functions().convertTimestampToDate(date);
			
	        insertOffer(id, name, category, price, discount, date, store, created, db);
		}	
	}

	private void insertOffer(String id, String name, String category, String price, String discount, String date, String store, String created, SQLiteDatabase db){
			 
		db.execSQL("INSERT INTO "+FeedOffer.TABLE_NAME+" ("+FeedOffer._ID+", "+FeedOffer.PRODUCT_NAME+", "+FeedOffer.CATEGORY+", "+FeedOffer.PRICE+", "+FeedOffer.DISCOUNT+", "+FeedOffer.UNTIL_DATE+", "+FeedOffer.STORE+", "+FeedOffer.OFFER_CREATED+" )" +
					" VALUES ('"+id+"','"+name+"','"+category+"','"+price+"','"+discount+"','"+date+"','"+store+"','"+created+"')");
	}
	
	public void deleteEndedOffers(SQLiteDatabase db){
		Date date= new Date();
		Timestamp timestampToday = new Timestamp(date.getTime());
		String today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestampToday);
				
		db.execSQL("DELETE FROM "+FeedOffer.TABLE_NAME+" WHERE "+FeedOffer.UNTIL_DATE+"< '"+today+"'");
	}
}
