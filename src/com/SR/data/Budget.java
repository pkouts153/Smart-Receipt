package com.SR.data;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.SR.data.FeedReaderContract.FeedBudget;
import com.SR.data.FeedReaderContract.FeedProduct;
import com.SR.processes.Functions;

public class Budget {
    
    SQLiteDatabase db;
    Cursor c1;
    Cursor c2;
    
	public Budget(SQLiteDatabase database) {
		db = database;
	}
	
    public Cursor getBudget(int user_id){
    	
		// Specifies which columns are needed from the database
		String[] projection = {
			FeedBudget._ID,
			FeedBudget.EXPENSE_CATEGORY,
			FeedBudget.SPEND_LIMIT,
			FeedBudget.START_DATE,
			FeedBudget.END_DATE,
			FeedBudget.USER,
			FeedBudget.FAMILY_USER,
			FeedBudget.IS_SURPASSED
		    };
		
		String where = "" + FeedBudget.USER + "=" + user_id + " AND " + FeedBudget.FOR_DELETION + "=0";
		
		c1 = db.query(
			FeedBudget.TABLE_NAME,  				  // The table to query
		    projection,                               // The columns to return
		    where,                                	  // The columns for the WHERE clause
		    null,                            		  // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    null                                 	  // The sort order
		    );
		
		return c1;
    }
    
    public void saveBudget(String category, Float limit, String from_date, String until_date, int user_id, int family_id){
    	
		ContentValues values = new ContentValues();
		values.put(FeedBudget.EXPENSE_CATEGORY, category);
		values.put(FeedBudget.SPEND_LIMIT, limit);
		values.put(FeedBudget.START_DATE, from_date);
		values.put(FeedBudget.END_DATE, until_date);
		values.put(FeedBudget.USER, user_id);
		if (family_id != 0)
			values.put(FeedBudget.FAMILY_USER, family_id);
		values.put(FeedBudget.FOR_DELETION, 0);
		values.put(FeedBudget.FOR_UPDATE, 0);
		values.put(FeedBudget.ON_SERVER, 0);
		values.put(FeedBudget.IS_SURPASSED, 0);
		
		Date date = new Date();
		Timestamp timestampToday = new Timestamp(date.getTime());
		String today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestampToday);
		
		values.put(FeedBudget.BUDGET_CREATED, today);
		
		db.insert(FeedBudget.TABLE_NAME, "null", values);
		values.clear();
		
    }
    
    public boolean deleteBudget(int id){
		
		ContentValues values = new ContentValues();
		values.put(FeedBudget.FOR_DELETION, 1);
		return db.update(FeedBudget.TABLE_NAME, values, FeedBudget._ID + "=" + id, null) > 0;
		
    }
    
    public Boolean BudgetsSurpassed() {
    	boolean surpassed = false;
		
		c2 = getBudget(User.USER_ID);
		
		c2.moveToFirst();
		
		while (!c2.isAfterLast()) {
			String productQuery = "SELECT SUM(" + FeedProduct.PRICE + ") AS sum" + 
							   	   " FROM " + FeedProduct.TABLE_NAME +
								   " WHERE " + FeedProduct.USER + "=" + User.USER_ID +
								   " AND " + FeedProduct.PURCHASE_DATE + " BETWEEN Date('" + c2.getString(c2.getColumnIndexOrThrow(FeedBudget.START_DATE)) + "')" +
								   " AND Date('" + c2.getString(c2.getColumnIndexOrThrow(FeedBudget.END_DATE)) + "')";
			
			if (!c2.getString(c2.getColumnIndexOrThrow(FeedBudget.EXPENSE_CATEGORY)).equals("All"))
				productQuery = productQuery +" AND " + FeedProduct.PRODUCT_CATEGORY + "='" + c2.getString(c2.getColumnIndexOrThrow(FeedBudget.EXPENSE_CATEGORY)) + "'";
			
			
			c1 = db.rawQuery(productQuery, null);
			
	        c1.moveToFirst();
	        
	        if (c1.getFloat(c1.getColumnIndexOrThrow("sum")) > c2.getFloat(c2.getColumnIndexOrThrow(FeedBudget.SPEND_LIMIT))) {
	        	
	        	ContentValues values = new ContentValues();
	        	values.put(FeedBudget.IS_SURPASSED, 1);
	        	db.update(FeedBudget.TABLE_NAME, values, FeedBudget._ID+"="+c2.getInt(c2.getColumnIndexOrThrow(FeedBudget._ID)), null);
	        	surpassed = true;
			}
	        c2.moveToNext();
		}

		c1.close();
		c2.close();

    	return surpassed;
    }

    
    
    
    public Budget() {
    	
	}
	
	private void updateForDeletion(String id, SQLiteDatabase db) {
		
		db.execSQL("UPDATE "+FeedBudget.TABLE_NAME+" SET "+FeedBudget.FOR_DELETION+" = 1 " +
				   "WHERE "+FeedBudget._ID+" ='"+id+"' and "+FeedBudget.USER+" ="+ User.USER_ID+"");
	}
	
	public Cursor fetchBudgetsForDeletion(SQLiteDatabase db){
		
		String[] params = {String.valueOf(User.USER_ID)};
		Cursor result = db.rawQuery("SELECT "+FeedBudget._ID+" FROM "+FeedBudget.TABLE_NAME+" WHERE "+FeedBudget.FOR_DELETION+"= '1' and "+
															FeedBudget.USER+" =?", params);
		return result;
	}
	
	public void deleteBudget(String id, SQLiteDatabase db) {
		
		db.execSQL("DELETE FROM "+FeedBudget.TABLE_NAME+" WHERE "+FeedBudget._ID+" = '"+id+"' and "+FeedBudget.FOR_DELETION+" = '1' and "+
															""+FeedBudget.USER+" ="+ User.USER_ID+"");	
	}
	
	public String fetchBudgetMaxId(SQLiteDatabase db){
		
		String[] params ={"1", String.valueOf(User.USER_ID)};
		Cursor result = db.rawQuery("SELECT max("+FeedBudget._ID+") AS max FROM "+FeedBudget.TABLE_NAME+" " +
									"WHERE "+FeedBudget.ON_SERVER+" = ? and "+FeedBudget.USER+" = ?", params);
		result.moveToNext();
		String lastId = result.getString(result.getColumnIndexOrThrow("max"));
		
		return lastId;
	}
	
	private void makeIdAvailable(String id, SQLiteDatabase db){
		
		Cursor result = fetchBudgetById(id, db);
		if(result.moveToNext()){

			String prevStartDate = result.getString(result.getColumnIndexOrThrow(FeedBudget.START_DATE));
			String prevFinishDate = result.getString(result.getColumnIndexOrThrow(FeedBudget.END_DATE));
			String prevCategory = result.getString(result.getColumnIndexOrThrow(FeedBudget.EXPENSE_CATEGORY));
			String prevSpentLimit = result.getString(result.getColumnIndexOrThrow(FeedBudget.SPEND_LIMIT));
			String prevUser = result.getString(result.getColumnIndexOrThrow(FeedBudget.USER));
			String prevCreated = result.getString(result.getColumnIndexOrThrow(FeedBudget.BUDGET_CREATED));
			String prevForUpdate = result.getString(result.getColumnIndexOrThrow(FeedBudget.FOR_UPDATE));
			String prevForDeletion = result.getString(result.getColumnIndexOrThrow(FeedBudget.FOR_DELETION));				
			String prevOnServer = result.getString(result.getColumnIndexOrThrow(FeedBudget.ON_SERVER));
			String prevOnSurpass = result.getString(result.getColumnIndexOrThrow(FeedBudget.IS_SURPASSED));

			reInsertBudget(id, prevStartDate, prevFinishDate, prevCategory,	prevSpentLimit, prevUser, prevCreated,
																				prevForUpdate, prevForDeletion, prevOnServer, prevOnSurpass, db);
		}
	}
	
	private Cursor fetchBudgetById(String id, SQLiteDatabase db){
		
		String[] params = {id, String.valueOf(User.USER_ID)};
		Cursor result = db.rawQuery("SELECT * FROM "+FeedBudget.TABLE_NAME+" " +
									"WHERE "+FeedBudget._ID+" = ? and "+FeedBudget.USER+" = ?", params);
		return result;
	}
	
	private void reInsertBudget(String id, String prevStartDate, String prevFinishDate, String prevCategory,
									String prevSpentLimit, String prevUser, String prevCreated, String prevForUpdate,
																String prevForDeletion, String prevOnServer, String prevOnSurpass, SQLiteDatabase db){

		db.execSQL("INSERT INTO "+FeedBudget.TABLE_NAME+" ("+FeedBudget.START_DATE+","+FeedBudget.END_DATE+","+FeedBudget.EXPENSE_CATEGORY+"," +
															FeedBudget.SPEND_LIMIT+","+FeedBudget.USER+","+FeedBudget.BUDGET_CREATED+"," +
															FeedBudget.FOR_UPDATE+","+FeedBudget.FOR_DELETION+","+FeedBudget.ON_SERVER+","+FeedBudget.IS_SURPASSED+") " +
			   "VALUES ('"+prevStartDate+"','"+prevFinishDate+"','"+prevCategory+"','"+prevSpentLimit+"','"+prevUser+"'," +
			   		"'"+prevCreated+"','"+prevForUpdate+"','"+prevForDeletion+"', '"+prevOnServer+"', '"+prevOnSurpass+"')");				

		db.execSQL("DELETE FROM "+FeedBudget.TABLE_NAME+" WHERE "+FeedBudget._ID+" = '"+id+"'and " +
																FeedBudget.USER+" = "+ User.USER_ID+" ");	
	}
	
	private void insertBudget(String id, String startDate, String finishDate, String category, String spentLimit, String user, 
			String created, String forUpdate, String forDeletion, SQLiteDatabase db){
		
		db.execSQL("INSERT INTO "+FeedBudget.TABLE_NAME+" ("+FeedBudget._ID+","+FeedBudget.START_DATE+","+FeedBudget.END_DATE+","+
															FeedBudget.EXPENSE_CATEGORY+","+FeedBudget.SPEND_LIMIT+","+FeedBudget.USER+","+
															FeedBudget.BUDGET_CREATED+","+FeedBudget.FOR_UPDATE+","+FeedBudget.FOR_DELETION+","+
															FeedBudget.ON_SERVER+","+FeedBudget.IS_SURPASSED+") " +
				"VALUES ('"+id+"','"+startDate+"','"+finishDate+"','"+category+"','"+spentLimit+"','"+user+"','"+created+"'," +
				   		"'"+forUpdate+"','"+forDeletion+"', '1', '1')");	
	}
	
	public void handleBudgetJSONArrayForRetrieve(JSONArray json, SQLiteDatabase db) throws JSONException{
		
		for(int i=0; i<json.length(); i++){
			
			JSONObject json_data = json.getJSONObject(i);

			String id = json_data.get("id").toString();
			String startDate = json_data.get("startDate").toString();
			String finishDate = json_data.get("finishDate").toString();
			String category = json_data.get("category").toString();
			String spentLimit = json_data.get("spentLimit").toString();
			String user = json_data.get("user").toString();
			String created = json_data.get("created").toString();
			String forUpdate = json_data.get("forUpdate").toString();
			String forDeletion = json_data.get("forDeletion").toString();
			
			startDate = new Functions().convertTimestampToDate(startDate);
			finishDate = new Functions().convertTimestampToDate(finishDate);

			makeIdAvailable(id, db);
			
			insertBudget(id, startDate, finishDate, category, spentLimit, user, created, forUpdate, forDeletion, db);
		}	
	}
	
	public void handleBudgetJSONArrayForUpdate(JSONArray json, SQLiteDatabase db) throws JSONException{
		
		for(int i=0; i<json.length(); i++){
			
			JSONObject json_data = json.getJSONObject(i);
		
			String id = json_data.get("id").toString();
			String newStartDate = json_data.get("startDate").toString();
			String newFinishDate = json_data.get("finishDate").toString();
			String newCategory = json_data.get("category").toString();
			String newSpentLimit = json_data.get("spentLimit").toString();
			
			newStartDate = new Functions().convertTimestampToDate(newStartDate);
			newFinishDate = new Functions().convertTimestampToDate(newFinishDate);
			
			String[] param ={id};
			Cursor queryResult = db.rawQuery("SELECT * FROM "+FeedBudget.TABLE_NAME+" WHERE "+FeedBudget._ID+" = ?", param);
			
			queryResult.moveToFirst();
			String prevStartDate = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedBudget.START_DATE));
			String prevFinishDate = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedBudget.END_DATE));
			String prevCategory = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedBudget.EXPENSE_CATEGORY));
			String prevSpentLimit = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedBudget.SPEND_LIMIT));
			
			if(!(prevStartDate.equals(newStartDate)))
				db.execSQL("UPDATE "+FeedBudget.TABLE_NAME+" SET "+FeedBudget.START_DATE+"='"+newStartDate+"' WHERE "+FeedBudget._ID+"='"+id+"'");
			if(!(prevFinishDate.equals(newFinishDate)))
				db.execSQL("UPDATE "+FeedBudget.TABLE_NAME+" SET "+FeedBudget.END_DATE+"='"+newFinishDate+"' WHERE "+FeedBudget._ID+"='"+id+"'");
			if(!(prevCategory.equals(newCategory)))
				db.execSQL("UPDATE "+FeedBudget.TABLE_NAME+" SET "+FeedBudget.EXPENSE_CATEGORY+"='"+newCategory+"' WHERE "+FeedBudget._ID+"='"+id+"'");
			if(!(prevSpentLimit.equals(newSpentLimit)))
				db.execSQL("UPDATE "+FeedBudget.TABLE_NAME+" SET "+FeedBudget.SPEND_LIMIT+"='"+newSpentLimit+"' WHERE "+FeedBudget._ID+"='"+id+"'");
			
			db.execSQL("UPDATE "+FeedBudget.TABLE_NAME+" SET "+FeedBudget.FOR_UPDATE+"=1 WHERE "+FeedBudget._ID+"='"+id+"'");

		}	
	}
	
	public void handleBudgetJSONArrayForDeletion(JSONArray json, SQLiteDatabase db) throws JSONException{
		
		for(int i=0; i<json.length(); i++){
			
			JSONObject json_data = json.getJSONObject(i);

			String id = json_data.get("id").toString();
			
			updateForDeletion(id, db);			
		}	
	}

	public Cursor fetchBudgetForUpdate(SQLiteDatabase db){
		
		String[] params = {"1", "1", String.valueOf(User.USER_ID)};
		Cursor result = db.rawQuery("SELECT * FROM "+FeedBudget.TABLE_NAME+" " +
									"WHERE "+FeedBudget.FOR_UPDATE+" = ? and "+FeedBudget.ON_SERVER+" = ? and "+
											FeedBudget.USER+" = ? ", params);
		return result;
	}
	
	public JSONArray convertStringDataToJson(String id, String startDate, String finishDate, String category, String spentLimit)
																									throws JSONException {

		Map<String,String> budget = new HashMap<String,String>();
		budget.put("id", id);
		budget.put("startDate", startDate);
		budget.put("finishDate", finishDate);
		budget.put("category", category);
		budget.put("spentLimit", spentLimit);
		
		JSONObject temp = new JSONObject(budget);
		JSONArray json = new JSONArray();
		
		json.put(0, temp);		
		
		return json;
	}
	
    public JSONArray convertBudgetObjectToJson(String id, String startDate, String finishDate, String category,String spentLimit,
			String user, String created,String forUpdate, String forDeletion) throws JSONException {

		Map<String,String> budget = new HashMap<String,String>();
		budget.put("id", id);
		budget.put("startDate", startDate);
		budget.put("finishDate", finishDate);
		budget.put("category", category);
		budget.put("spentLimit", spentLimit);
		budget.put("user", user);
		budget.put("created", created);
		budget.put("forUpdate", forUpdate);
		budget.put("forDeletion", forDeletion);
		
		JSONObject temp = new JSONObject(budget);
		JSONArray json = new JSONArray();
		
		json.put(0, temp);		
		
		return json;
	}
    
    public void handleBudgetJSONArrayForUpload(JSONArray json, SQLiteDatabase db) throws JSONException{
							
		JSONObject json_data =json.getJSONObject(0);

		String id = json_data.get("id").toString();
		String startDate = json_data.get("startDate").toString();
		String finishDate = json_data.get("finishDate").toString();
		String category = json_data.get("category").toString();
		String spentLimit = json_data.get("spentLimit").toString();
		String user = json_data.get("user").toString();
		String created = json_data.get("created").toString();
		String forUpdate = json_data.get("forUpdate").toString();
		String forDeletion = json_data.get("forDeletion").toString();
	
		float limit = Float.parseFloat(spentLimit);
		String format = " %.2f ";
		spentLimit = String.format(format, limit);
		
		startDate = new Functions().convertTimestampToDate(startDate);
		finishDate = new Functions().convertTimestampToDate(finishDate);
		
		makeIdAvailable(id, db);
		
		updateBudget(id, startDate, finishDate, category, spentLimit, user, created, forUpdate, forDeletion, db);		
	}

	private void updateBudget(String id, String startDate, String finishDate, String category, String spentLimit, 
			 					String user, String created, String forUpdate, String forDeletion, SQLiteDatabase db){
		
		db.execSQL("UPDATE "+FeedBudget.TABLE_NAME+" SET "+FeedBudget._ID+" = '"+id+"' " +
					"WHERE "+FeedBudget.START_DATE+" = '"+startDate+"' and "+FeedBudget.END_DATE+" = '"+finishDate+"' and " +
							 FeedBudget.EXPENSE_CATEGORY+" = '"+category+"' and "+FeedBudget.SPEND_LIMIT+" = '"+spentLimit+"' and " +
							 FeedBudget.USER+" = '"+user+"' and "+FeedBudget.BUDGET_CREATED+" = '"+created+"' and " +
							 FeedBudget.FOR_UPDATE+" = '"+forUpdate+"' and "+FeedBudget.FOR_DELETION+" = '"+forDeletion+"'" );
		
		db.execSQL("UPDATE "+FeedBudget.TABLE_NAME+" SET "+FeedBudget.ON_SERVER+" = 1 WHERE "+FeedBudget._ID+" ='"+id+"'" );
	}	
	
	public Cursor fetchLocalBudget(SQLiteDatabase db){
		
		String[] params = {"0", String.valueOf(User.USER_ID)};	
		Cursor result = db.rawQuery("SELECT * FROM "+FeedBudget.TABLE_NAME+
									" WHERE "+FeedBudget.ON_SERVER+"=? and "+FeedBudget.USER+" =?", params);
		return result;
	}
}