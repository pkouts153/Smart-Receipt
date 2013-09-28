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

public class Product {
    
    SQLiteDatabase db;
    Cursor c;
    
	public Product(SQLiteDatabase database) {
		db = database;
	}
	
		
    public boolean saveProduct(ArrayList<String> products_list, String date, int store_id){

		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		
		boolean successful_save = true;
		
		for (int i = 0; i<products_list.size(); i += 3) {
			
			values.put(FeedProduct.PRODUCT_CATEGORY, products_list.get(i));
			values.put(FeedProduct.NAME, products_list.get(i+1));
			Float price = Float.parseFloat(products_list.get(i+2));
			values.put(FeedProduct.PRICE, price);
			values.put(FeedProduct.PURCHASE_DATE, date);
			values.put(FeedProduct.STORE, store_id);
			values.put(FeedProduct.USER, User.USER_ID);
			values.put(FeedProduct.ON_SERVER, 0);
			
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
    
    

    public Product() {
    	
	}
    
	public String fetchProductMaxId(SQLiteDatabase db){

		String[] params ={"1", String.valueOf(User.USER_ID)};
		Cursor result = db.rawQuery("SELECT max("+FeedProduct._ID+") AS max FROM "+FeedProduct.TABLE_NAME +
									" WHERE "+FeedProduct.ON_SERVER+"=? and "+FeedProduct.USER+" =?", params);
		result.moveToNext();
		String lastId = result.getString(result.getColumnIndexOrThrow("max"));
		
		return lastId;
	}
	
	public Cursor fetchLocalProduct(SQLiteDatabase db){
		
		String[] params = {"0", String.valueOf(User.USER_ID)};	
		Cursor result = db.rawQuery("SELECT * FROM "+FeedProduct.TABLE_NAME +
									" WHERE "+FeedProduct.ON_SERVER+"=? and "+FeedProduct.USER+" =?", params);
		return result;
	}
	
	private void makeIdAvailable(String id, SQLiteDatabase db){
		
        Cursor result = fetchProductById(id, db);
		if(result.moveToNext()){

			String prevName = result.getString(result.getColumnIndexOrThrow(FeedProduct.NAME));
			String prevCategory = result.getString(result.getColumnIndexOrThrow(FeedProduct.PRODUCT_CATEGORY));
			String prevPrice = result.getString(result.getColumnIndexOrThrow(FeedProduct.PRICE));
			String prevDate = result.getString(result.getColumnIndexOrThrow(FeedProduct.PURCHASE_DATE));
			String prevUser = result.getString(result.getColumnIndexOrThrow(FeedProduct.USER));				
			String prevStore = result.getString(result.getColumnIndexOrThrow(FeedProduct.PURCHASE_DATE));
			String prevCreated = result.getString(result.getColumnIndexOrThrow(FeedProduct.PRODUCT_CREATED));
			String prevOnServer = result.getString(result.getColumnIndexOrThrow(FeedProduct.ON_SERVER));

			reInsertProduct(id, prevName, prevCategory, prevPrice, prevDate, prevUser, prevStore, prevCreated, prevOnServer, db);
		}
	}
/*	
	private Cursor fetchProductById(String id, SQLiteDatabase db){
		String[] params = {id, String.valueOf(User.USER_ID)};
		Cursor result = db.rawQuery("SELECT * FROM "+FeedProduct.TABLE_NAME +
									" WHERE "+FeedProduct._ID +" = ? and "+FeedProduct.USER +" = ?", params);
		return result;
	}
*/	
	private Cursor fetchProductById(String id, SQLiteDatabase db){
		String[] params = {id, String.valueOf(User.USER_ID), String.valueOf(User.USER_ID), "1", String.valueOf(User.USER_ID), "1"};
		Cursor result = db.rawQuery("SELECT * FROM "+FeedProduct.TABLE_NAME +
									" WHERE "+FeedProduct._ID +" = ? and " +
											"("+FeedProduct.USER +" = ? or "
											   +FeedProduct.USER+" IN (SELECT "+FeedFamily.MEMBER1+" FROM "+FeedFamily.TABLE_NAME+" WHERE "+FeedFamily.MEMBER2+"=? and "+FeedFamily.CONFIRMED+"=?) or "
											   +FeedProduct.USER+" IN (SELECT "+FeedFamily.MEMBER2+" FROM "+FeedFamily.TABLE_NAME+" WHERE "+FeedFamily.MEMBER1+"=? and "+FeedFamily.CONFIRMED+"=?))", params);
		return result;
	}
	private void reInsertProduct(String id, String prevName, String prevCategory, String prevPrice, String prevDate,
						String prevUser, String prevStore, String prevCreated, String prevOnServer, SQLiteDatabase db){

		db.execSQL("INSERT INTO "+FeedProduct.TABLE_NAME +" ("+FeedProduct.NAME +", "+FeedProduct.PRODUCT_CATEGORY +", "
											+FeedProduct.PRICE +", "+FeedProduct.PURCHASE_DATE +", "+FeedProduct.USER +", "
											+FeedProduct.STORE +", "+FeedProduct.PRODUCT_CREATED +", "+FeedProduct.ON_SERVER +") " +
					"VALUES ('"+prevName+"','"+prevCategory+"','"+prevPrice+"','"+prevDate+"','"
							   +prevUser+"','"+prevStore+"','"+prevCreated+"', '"+prevOnServer+"')");			
	
		db.execSQL("DELETE FROM "+FeedProduct.TABLE_NAME +
					" WHERE "+FeedProduct._ID +" = '"+id+"' and "+FeedProduct.USER +" = '"+prevUser+"'");
	}
	
	private void insertProduct(String id, String name, String category, String price, String date, String user, 
			String store, String created, SQLiteDatabase db){

		db.execSQL("INSERT INTO "+FeedProduct.TABLE_NAME +" ("+FeedProduct._ID +","+FeedProduct.NAME +", "+FeedProduct.PRODUCT_CATEGORY +
												", "+FeedProduct.PRICE +", "+FeedProduct.PURCHASE_DATE +", "+FeedProduct.USER +
												", "+FeedProduct.STORE +", "+FeedProduct.PRODUCT_CREATED +", "+FeedProduct.ON_SERVER +") " +
				"VALUES ('"+id+"','"+name+"','"+category+"','"+price+"','"+date+"','"+user+"','"+store+"','"+created+"', '1')");	
	}
    
	private void updateProduct(String id, String name, String category, String price, String date, String user,
			String store, String created, SQLiteDatabase db){

		db.execSQL("UPDATE "+FeedProduct.TABLE_NAME +" SET "+FeedProduct._ID +" = '"+id+"'" +
				" WHERE "+FeedProduct.NAME +" = '"+name+"' and "+FeedProduct.PRODUCT_CATEGORY +" = '"+category+"' " +"and "+
						FeedProduct.PRICE +" = '"+price+"'" +" and "+FeedProduct.PURCHASE_DATE +" = '"+date+"' " +"and "+
						FeedProduct.USER +" = '"+user+"' and "+FeedProduct.STORE +" = '"+store+"' and "+FeedProduct.PRODUCT_CREATED +" = '"+created+"'" );
	
		db.execSQL("UPDATE "+FeedProduct.TABLE_NAME +" SET "+FeedProduct.ON_SERVER +" =1 WHERE "+FeedProduct._ID +" ='"+id+"'" );
	}
	
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
			
			insertProduct(id, name, category, price, date, user, store, created, db);
		}
	}
	    
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
/*    
	public Cursor fetchProducts(SQLiteDatabase db){
		
		Cursor result = db.rawQuery("SELECT * FROM "+FeedProduct.TABLE_NAME +"", null);
		return result;
	}
*/
}