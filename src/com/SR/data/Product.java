package com.SR.data;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.SR.data.FeedReaderContract.FeedFamily;
import com.SR.data.FeedReaderContract.FeedProduct;
import com.SR.processes.Functions;

/**
* This class represents product and is responsible for the necessary processes
* 
* @author Παναγιώτης Κουτσαυτίκης 8100062
*/
public class Product {
    
    SQLiteDatabase db;
    Cursor c;
    
    /**
    * Product constructor 
    * 
    * @param database   saves the database object, that was passed from the Activity, 
    * 					in the database object of the class for use in the methods
    */
	public Product(SQLiteDatabase database) {
		db = database;
	}
	
	 /**
     * Gets data from the Activity and creates a new line in the Product table with these data
     * 
     * @return whether the insertion was made successfully or not
     */	
    public boolean saveProduct(ArrayList<String> products_list, String date, int store_id){

		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		
		// if even one product was not saved successfully it will be false
		boolean successful_save = true;
		
		// products_list represent one product per 3 lines (PRODUCT_CATEGORY, NAME, PRICE)
		for (int i = 0; i<products_list.size(); i += 3) {
			
			values.put(FeedProduct.PRODUCT_CATEGORY, products_list.get(i));
			values.put(FeedProduct.NAME, products_list.get(i+1));
			Float price = Float.parseFloat(products_list.get(i+2));
			values.put(FeedProduct.PRICE, price);
			values.put(FeedProduct.PURCHASE_DATE, date);
			values.put(FeedProduct.STORE, store_id);
			values.put(FeedProduct.USER, User.USER_ID);
			values.put(FeedProduct.ON_SERVER, 0);
			
			// PRODUCT_CREATED takes the date and time of the creation
			Date date1 = new Date();
			Timestamp timestampToday = new Timestamp(date1.getTime());
			String today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestampToday);
			
			values.put(FeedProduct.PRODUCT_CREATED, today);
			
			if (!(db.insert(FeedProduct.TABLE_NAME, "null", values)>0) && successful_save == true)
				successful_save = false;
				
			values.clear();
		}
		
		return successful_save;
    }
    
    /**
     * @author Ιωάννης Διαμαντίδης 8100039
     * 
     */
        
    public Product() {
    	
	}
   
    //this method returns the maximum id that exists in product table in local database	   
	public String fetchProductMaxId(SQLiteDatabase db){

		String[] params ={"1", String.valueOf(User.USER_ID)};
		Cursor result = db.rawQuery("SELECT CASE" +
												" WHEN max("+FeedProduct._ID+") IS NULL THEN 0" +
											    " ELSE max("+FeedProduct._ID+")" +
										  " END AS max FROM "+FeedProduct.TABLE_NAME +
									" WHERE "+FeedProduct.ON_SERVER+"=? and "+FeedProduct.USER+" =?", params);
		result.moveToNext();
		String lastId = result.getString(result.getColumnIndexOrThrow("max"));
		
		return lastId;
	}
	
	//this method returns the products that are created from this app and are not saved on server
	public Cursor fetchLocalProduct(SQLiteDatabase db){
		
		String[] params = {"0", String.valueOf(User.USER_ID)};	
		Cursor result = db.rawQuery("SELECT * FROM "+FeedProduct.TABLE_NAME +
									" WHERE "+FeedProduct.ON_SERVER+"=? and "+FeedProduct.USER+" =?", params);
		return result;
	}
	
	/*this method make a search whether there is a product is saved with a certain id or not, and if it does, 
		then read the index of this record and re insert it in the first available id*/
	private void makeIdAvailable(String id, SQLiteDatabase db){
		
        Cursor result = fetchProductById(id, db);
		if(result.moveToNext()){

			String prevName = result.getString(result.getColumnIndexOrThrow(FeedProduct.NAME));
			String prevCategory = result.getString(result.getColumnIndexOrThrow(FeedProduct.PRODUCT_CATEGORY));
			String prevPrice = result.getString(result.getColumnIndexOrThrow(FeedProduct.PRICE));
			String prevDate = result.getString(result.getColumnIndexOrThrow(FeedProduct.PURCHASE_DATE));
			String prevUser = result.getString(result.getColumnIndexOrThrow(FeedProduct.USER));				
			String prevStore = result.getString(result.getColumnIndexOrThrow(FeedProduct.STORE));
			String prevCreated = result.getString(result.getColumnIndexOrThrow(FeedProduct.PRODUCT_CREATED));
			String prevOnServer = result.getString(result.getColumnIndexOrThrow(FeedProduct.ON_SERVER));
			
			if("0".equals(prevOnServer))
				insertProduct(null, prevName, prevCategory, prevPrice, prevDate, prevUser, prevStore, prevCreated, prevOnServer, db);
			
			db.execSQL("DELETE FROM "+FeedProduct.TABLE_NAME+" WHERE "+FeedProduct._ID +" = '"+id+"'");
		}
	}
	
	/*this method returns a record from product table using its id*/
	private Cursor fetchProductById(String id, SQLiteDatabase db){
		
		String[] params = {id};
		Cursor result = db.rawQuery("SELECT * FROM "+FeedProduct.TABLE_NAME +
									" WHERE "+FeedProduct._ID +" = ?", params);
		return result;
	}
	
	
	/*this method insert a product into the database using a specific id(the one received from the server database)*/
	private void insertProduct(String id, String name, String category, String price, String date, String user, 
			String store, String created, String onServer, SQLiteDatabase db){

		db.execSQL("INSERT INTO "+FeedProduct.TABLE_NAME +" ("+FeedProduct._ID +","+FeedProduct.NAME +", "+FeedProduct.PRODUCT_CATEGORY +
												", "+FeedProduct.PRICE +", "+FeedProduct.PURCHASE_DATE +", "+FeedProduct.USER +
												", "+FeedProduct.STORE +", "+FeedProduct.PRODUCT_CREATED +", "+FeedProduct.ON_SERVER +") " +
				"VALUES ("+id+",'"+name+"','"+category+"','"+price+"','"+date+"','"+user+"','"+store+"','"+created+"', '"+onServer+"')");	
	}
    
	/*this method updates a database record. It is used when a product created in this app, is sent to the server database.
	 * There, this record is saved with the first available id in the server database and not the initial id with which
	 * it was first saved in the app database. So, because ids should be the same in both databases, the local record is updated 
	 * with the id from server database*/
	private void updateProduct(String id, String name, String category, String price, String date, String user,
			String store, String created, SQLiteDatabase db){

		db.execSQL("UPDATE "+FeedProduct.TABLE_NAME +" SET "+FeedProduct._ID +" = '"+id+"'" +
				" WHERE "+FeedProduct.NAME +" = '"+name+"' and "+FeedProduct.PRODUCT_CATEGORY +" = '"+category+"' " +"and "+
						FeedProduct.PRICE +" = '"+price+"'" +" and "+FeedProduct.PURCHASE_DATE +" = '"+date+"' " +"and "+
						FeedProduct.USER +" = '"+user+"' and "+FeedProduct.STORE +" = '"+store+"' and "+FeedProduct.PRODUCT_CREATED +" = '"+created+"'" );
	
		db.execSQL("UPDATE "+FeedProduct.TABLE_NAME +" SET "+FeedProduct.ON_SERVER +" =1 WHERE "+FeedProduct._ID +" ='"+id+"'" );
	}
	
	/*this method creates a jsonArray that contains a product.*/
    public JSONArray convertStringToJson(String id, String name, String category, String price, 
			String date, String user, String store, String created) throws JSONException {

		Map<String,String> product = new HashMap<String,String>();
		product.put("id", id);
		product.put("name", name);
		product.put("category", category);
		product.put("price", price);
		product.put("date", date);
		product.put("user", user);
		product.put("store", store);
		product.put("created", created);
		
		JSONObject temp = new JSONObject(product);
		JSONArray json = new JSONArray();
		
		json.put(0, temp);		
		
		return json;
    }
    
    /*this method handles the response JSONArray object from a GET request. It converts its data into String variables 
     * and format them properly. Then it calls mekeIdAvailable method and insert the product with the proper id*/
	public void handleProductJSONArrayForRetrieve(JSONArray json, SQLiteDatabase db) throws JSONException{
				
        for(int i=0; i<json.length(); i++){
			
			JSONObject json_data =json.getJSONObject(i);
			
			String id = json_data.get("id").toString();
			String name = json_data.get("name").toString();
			String category = json_data.get("category").toString();
			String price = json_data.get("price").toString();
			String date = json_data.get("date").toString();
			String user = json_data.get("user").toString();
			String store = json_data.get("store").toString();
	        String created = json_data.get("created").toString();
			
	        float floatPrice = Float.parseFloat(price);
			String format = " %.2f ";
			price = String.format(format, floatPrice);

			date = new Functions().convertTimestampToDate(date);

			makeIdAvailable(id, db);
			
			String onServer = "1";
			insertProduct(id, name, category, price, date, user, store, created, onServer, db);
		}
	}
	    
	/*this method handles the response JSONArray object from a POST Request. It converts its data into String variables and 
	 * format them properly. Then it calls mekeIdAvailable method and update the product with the proper id*/
    public void handleProductJSONArrayForUpload(JSONArray json, SQLiteDatabase db) throws JSONException{
		
		JSONObject json_data = json.getJSONObject(0);

		String id = json_data.get("id").toString();
		String name = json_data.get("name").toString();
		String category = json_data.get("category").toString();
		String price = json_data.get("price").toString();
		String date = json_data.get("date").toString();
		String user = json_data.get("user").toString();
		String store = json_data.get("store").toString();
        String created = json_data.get("created").toString();

		float floatPrice = Float.parseFloat(price);
		String format = " %.2f ";
		price = String.format(format, floatPrice);

		date = new Functions().convertTimestampToDate(date);
		
		makeIdAvailable(id, db);

		updateProduct(id, name, category, price, date, user, store, created, db);
	}
}
