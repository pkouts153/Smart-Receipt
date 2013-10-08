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
import com.SR.data.FeedReaderContract.FeedStore;
import com.SR.processes.Functions;

public class Offer {
	
    SQLiteDatabase db;
    Cursor c;
    
    public Offer(SQLiteDatabase database){
    	db = database;
    }
    
    public Cursor getOffersByCategory(String category){

		// Specifies which columns are needed from the database
		String[] projection = {
			FeedOffer.TABLE_NAME + "." + FeedOffer._ID,	
			FeedOffer.PRODUCT_NAME,
			FeedOffer.PRICE,
			FeedOffer.DISCOUNT,
			FeedOffer.UNTIL_DATE,
			FeedStore.NAME,
			FeedOffer.CATEGORY
		    };
		
		String where = FeedOffer.STORE + "=" + FeedStore.TABLE_NAME + "." + FeedStore._ID;
				
		if (category!=null)
			where = where + " AND " + FeedOffer.CATEGORY + "='" + category +"'";
		
		c = db.query(
			FeedOffer.TABLE_NAME + ", " + FeedStore.TABLE_NAME,  				      // The table to query
		    projection,                               // The columns to return
		    where,                                	  // The columns for the WHERE clause
		    null,                            		  // The values for the WHERE clause
		    null,                                 // don't group the rows
		    null,                                     // don't filter by row groups
		    null                                 	  // The sort order
		    );
		
		return c;
    }
    
    public Cursor getOffersByStore(String store){
    	
		// Specifies which columns are needed from the database
		String[] projection = {
			FeedOffer.TABLE_NAME + "." + FeedOffer._ID,	
			FeedOffer.PRODUCT_NAME,
			FeedOffer.PRICE,
			FeedOffer.DISCOUNT,
			FeedOffer.UNTIL_DATE,
			FeedOffer.CATEGORY,
			FeedStore.NAME
		    };
		
		String where = FeedOffer.STORE + "=" + FeedStore.TABLE_NAME + "." + FeedStore._ID;
		
		if (store!=null)
			where = where + " AND " + FeedStore.NAME + "='" + store + "'";
		
		c = db.query(
			FeedOffer.TABLE_NAME + ", " + FeedStore.TABLE_NAME,  // The table to query
		    projection,                               // The columns to return
		    where,                                	  // The columns for the WHERE clause
		    null,                            		  // The values for the WHERE clause
		    null,                                 // don't group the rows
		    null,                                     // don't filter by row groups
		    null                                 	  // The sort order
		    );
    	
		return c;
    }
    
    
    
/**
 * @author Ιωάννης Διαμαντίδης 8100039
 * 
 */
    
    public Offer() {
    	
	}
    
    //this method returns the maximum id that exists in offer table in local database
   public String fetchOfferMaxId(SQLiteDatabase db){

		Cursor result = db.rawQuery("SELECT CASE" +
												" WHEN max("+FeedOffer._ID+") IS NULL THEN 0" +
											    " ELSE max("+FeedOffer._ID+")" +
										  " END AS max FROM "+FeedOffer.TABLE_NAME+"", null);
		result.moveToNext();
		String lastId = result.getString(result.getColumnIndexOrThrow("max"));
		
		return lastId;
	}
	
   /*this method gets a jsonArray variable, converts its data to Java variables, converts this variable in to the proper format 
     and call insertOffer method passing as parameters the retrieved data*/
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

	/*this method gets as parameters the variables of an offer and insert them to local database*/
	private void insertOffer(String id, String name, String category, String price, String discount, String date, String store, String created, SQLiteDatabase db){
			 
		db.execSQL("INSERT INTO "+FeedOffer.TABLE_NAME+" ("+FeedOffer._ID+", "+FeedOffer.PRODUCT_NAME+", "+FeedOffer.CATEGORY+", "+FeedOffer.PRICE+", "+FeedOffer.DISCOUNT+", "+FeedOffer.UNTIL_DATE+", "+FeedOffer.STORE+", "+FeedOffer.OFFER_CREATED+" )" +
					" VALUES ('"+id+"','"+name+"','"+category+"','"+price+"','"+discount+"','"+date+"','"+store+"','"+created+"')");
	}
	
	/*this method calculate current date and time and delete every ended offer */
	public void deleteEndedOffers(SQLiteDatabase db){
		Date date= new Date();
		Timestamp timestampToday = new Timestamp(date.getTime());
		String today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestampToday);
				
		db.execSQL("DELETE FROM "+FeedOffer.TABLE_NAME+" WHERE "+FeedOffer.UNTIL_DATE+"< '"+today+"'");
	}
}
