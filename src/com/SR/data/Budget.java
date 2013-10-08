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
import com.SR.data.FeedReaderContract.FeedUser;
import com.SR.processes.Functions;

/**
* This class represents budget and is responsible for the necessary processes
* 
* @author Παναγιώτης Κουτσαυτίκης 8100062
*/

public class Budget {
    
    SQLiteDatabase db;
    Cursor c1;
    Cursor c2;
    
    /**
    * Budget constructor 
    * 
    * @param database   saves the database object, that was passed from the Activity, 
    * 					in the database object of the class for use in the methods
    */
	public Budget(SQLiteDatabase database) {
		db = database;
	}
	
	/**
	 * Gets budgets of a user from the database
	 * 
	 * @param user_id  specifies which user's budgets the method will return
	 * @return a cursor with the budgets of the user
	 */
	
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
    
    /**
     * Gets data from the Activity and creates a new line in the Budget table with these data
     * 
     * @return whether the insertion was made successfully or not
     */
    public boolean saveBudget(String category, Float limit, String from_date, String until_date, int user_id, int family_id){
    	
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
		
		// BUDGET_CREATED takes the date and time of the creation
		Date date = new Date();
		Timestamp timestampToday = new Timestamp(date.getTime());
		String today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestampToday);
		
		values.put(FeedBudget.BUDGET_CREATED, today);
		
		// if the insertion was made successfully then it will return 1 (>0)
		return db.insert(FeedBudget.TABLE_NAME, "null", values) > 0;
		
    }
    
    /**
     * Updates a line of the budget table making FOR_DELETION column 1 
     * (the actual deletion will be made after the database in the server updates it's own tables)
     * 
     * @param id  the id of the budget to be deleted
     * @return whether the update was made successfully
     */
    public boolean deleteBudget(int id){
		
		ContentValues values = new ContentValues();
		values.put(FeedBudget.FOR_DELETION, 1);
		return db.update(FeedBudget.TABLE_NAME, values, FeedBudget._ID + "=" + id, null) > 0;
		
    }
    
    /**
     * Checks if budgets are surpassed
     * 
     * @return true if at least one budget is surpassed 
     */
    public boolean BudgetsSurpassed() {
    	
    	// if at least one budget is surpassed it will be true
    	boolean surpassed = false;
		
		c2 = getBudget(User.USER_ID);
		
		c2.moveToFirst();
		
		// for its budget of the user compare the sum of the products' price with the SPEND_LIMIT
		
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
	        
	        // if sum > SPEND_LIMIT then IS_SURPASSED is 2
	        //	else IS_SURPASSED is 1 which means > 80% of SPEND_LIMIT
	        if (c1.getFloat(c1.getColumnIndexOrThrow("sum")) > (c2.getFloat(c2.getColumnIndexOrThrow(FeedBudget.SPEND_LIMIT)))) {
	        	
	        	ContentValues values = new ContentValues();
	        	values.put(FeedBudget.IS_SURPASSED, 2);
	        	db.update(FeedBudget.TABLE_NAME, values, FeedBudget._ID+"="+c2.getInt(c2.getColumnIndexOrThrow(FeedBudget._ID)), null);
	        	surpassed = true;
			}
	        else if (c1.getFloat(c1.getColumnIndexOrThrow("sum")) > (c2.getFloat(c2.getColumnIndexOrThrow(FeedBudget.SPEND_LIMIT))*0.8)) {
	        	
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

    public float getRemainingOfBudget(String start_date, String end_date, String category){
    	
    	String productQuery = "SELECT SUM(" + FeedProduct.PRICE + ") AS sum" + 
			   	   " FROM " + FeedProduct.TABLE_NAME +
				   " WHERE " + FeedProduct.USER + "=" + User.USER_ID +
				   " AND " + FeedProduct.PURCHASE_DATE + " BETWEEN Date('" + start_date + "')" +
				   " AND Date('" + end_date + "')";

		if (!category.equals("All"))
		productQuery = productQuery +" AND " + FeedProduct.PRODUCT_CATEGORY + "='" + category + "'";
		
		
		c1 = db.rawQuery(productQuery, null);
		
		c1.moveToFirst();
		return c1.getFloat(c1.getColumnIndexOrThrow("sum"));
    }
    
    
    /**
     * @author Ιωάννης Διαμαντίδης 8100039
     * 
     */
        
        
        public Budget() {
        	
    	}
    	
        //this method updates the budget table when a budget is deleted. This is done by setting forDeletion column equals 1
    	private void updateForDeletion(String id, SQLiteDatabase db) {
    		
    		db.execSQL("UPDATE "+FeedBudget.TABLE_NAME+" SET "+FeedBudget.FOR_DELETION+" = 1 " +
    				   "WHERE "+FeedBudget._ID+" ='"+id+"' and "+FeedBudget.USER+" ="+ User.USER_ID+"");
    	}
    	
    	//this method returns the records from budget table, which are deleted
    	public Cursor fetchBudgetsForDeletion(SQLiteDatabase db){
    		
    		String[] params = {String.valueOf(User.USER_ID)};
    		Cursor result = db.rawQuery("SELECT "+FeedBudget._ID+" FROM "+FeedBudget.TABLE_NAME+" WHERE "+FeedBudget.FOR_DELETION+"= '1' and "+
    															FeedBudget.USER+" =?", params);
    		return result;
    	}
    	
    	//this method deletes a record from budget table
    	public void deleteBudget(String id, SQLiteDatabase db) {
    		
    		db.execSQL("DELETE FROM "+FeedBudget.TABLE_NAME+" WHERE "+FeedBudget._ID+" = '"+id+"' and "+FeedBudget.FOR_DELETION+" = '1' and "+
    															""+FeedBudget.USER+" ="+ User.USER_ID+"");	
    	}
    	
    	/*this method returns the max value of id in budget table for budget of this user
    	 * Budgets created by other users of this phone, for this user are excluded
    	 */
    	public String fetchBudgetMaxId(SQLiteDatabase db){
    		
    		String[] params ={"1", String.valueOf(User.USER_ID)};
    		Cursor result = db.rawQuery("SELECT CASE" +
    												" WHEN max("+FeedBudget._ID+") IS NULL THEN 0" +
    											    " ELSE max("+FeedBudget._ID+")" +
    										  " END AS max FROM "+FeedBudget.TABLE_NAME+" " +
    									"WHERE "+FeedBudget.ON_SERVER+" = ? and "+FeedBudget.USER+" = ? " +
    											"and "+FeedBudget.FAMILY_USER+" NOT IN (SELECT "+FeedUser._ID+" FROM "+FeedUser.TABLE_NAME+
    																				" WHERE "+FeedUser.PASSWORD+" IS NOT NULL)", params);
    		result.moveToNext();
    		String lastId = result.getString(result.getColumnIndexOrThrow("max"));
    		
    		return lastId;
    	}
    	
    	/*this method make an id available, in order to insert a new record.
    	 * it checks if there is a record with a specific id, and if it exists and it is not downloaded from server,
    	 * it inserts it again with the first available id.
    	 * After that, no matter if it is downloaded from server or not, the record with this id is deleted.
    	 */
    	private void makeIdAvailable(String id, SQLiteDatabase db){
    		
    		Cursor result = fetchBudgetById(id, db);
    		if(result.moveToNext()){

    			String prevStartDate = result.getString(result.getColumnIndexOrThrow(FeedBudget.START_DATE));
    			String prevFinishDate = result.getString(result.getColumnIndexOrThrow(FeedBudget.END_DATE));
    			String prevCategory = result.getString(result.getColumnIndexOrThrow(FeedBudget.EXPENSE_CATEGORY));
    			String prevSpentLimit = result.getString(result.getColumnIndexOrThrow(FeedBudget.SPEND_LIMIT));
    			String prevUser = result.getString(result.getColumnIndexOrThrow(FeedBudget.USER));
    			String prevFamilyUser = result.getString(result.getColumnIndexOrThrow(FeedBudget.FAMILY_USER));
    			String prevCreated = result.getString(result.getColumnIndexOrThrow(FeedBudget.BUDGET_CREATED));
    			String prevForUpdate = result.getString(result.getColumnIndexOrThrow(FeedBudget.FOR_UPDATE));
    			String prevForDeletion = result.getString(result.getColumnIndexOrThrow(FeedBudget.FOR_DELETION));				
    			String prevOnServer = result.getString(result.getColumnIndexOrThrow(FeedBudget.ON_SERVER));
    			String prevOnSurpass = result.getString(result.getColumnIndexOrThrow(FeedBudget.IS_SURPASSED));
    			
    			if("0".equals(prevOnServer))
    				insertBudget(null, prevStartDate, prevFinishDate, prevCategory,	prevSpentLimit, prevUser, prevFamilyUser, prevCreated,
    																				prevForUpdate, prevForDeletion, prevOnServer, prevOnSurpass, db);
    			db.execSQL("DELETE FROM "+FeedBudget.TABLE_NAME+" WHERE "+FeedBudget._ID+" = '"+id+"'");	
    		}
    	}
    	
    	//this method returns a record from  budget table fetched by its id
    	private Cursor fetchBudgetById(String id, SQLiteDatabase db){
    		
    		String[] params = {id};
    		Cursor result = db.rawQuery("SELECT * FROM "+FeedBudget.TABLE_NAME+" " +
    									"WHERE "+FeedBudget._ID+" = ?", params);
    		return result;
    	}

    	//this method gets as parameters the details of a budget object and inserts them to budget table
    	private void insertBudget(String id, String startDate, String finishDate, String category, String spentLimit, String user, 
    			String familyUser, String created, String forUpdate, String forDeletion, String onServer, String isSurpassed, SQLiteDatabase db){
    		
    		db.execSQL("INSERT INTO "+FeedBudget.TABLE_NAME+" ("+FeedBudget._ID+","+FeedBudget.START_DATE+","+FeedBudget.END_DATE+","+
    															FeedBudget.EXPENSE_CATEGORY+","+FeedBudget.SPEND_LIMIT+","+FeedBudget.USER+","+
    															FeedBudget.FAMILY_USER+","+FeedBudget.BUDGET_CREATED+","+FeedBudget.FOR_UPDATE+","
    															+FeedBudget.FOR_DELETION+","+FeedBudget.ON_SERVER+","+FeedBudget.IS_SURPASSED+") " +
    				"VALUES ("+id+",'"+startDate+"','"+finishDate+"','"+category+"','"+spentLimit+"','"+user+"','"+familyUser+"','"+created+"'," +
    				   		"'"+forUpdate+"','"+forDeletion+"', '"+onServer+"', '"+isSurpassed+"')");	
    	}
    	
    	/*this method handles the jsonArray retrieved from a GET request. At first, it converts the values of this jsonArray
    	 * into String values, then it converts them into the proper format, it calls makeIdAvailable function and finally
    	 * it inserts the values into budget table.
    	 */
    	public void handleBudgetJSONArrayForRetrieve(JSONArray json, SQLiteDatabase db) throws JSONException{
    		
    		for(int i=0; i<json.length(); i++){
    			
    			JSONObject json_data = json.getJSONObject(i);

    			String id = json_data.get("id").toString();
    			String startDate = json_data.get("startDate").toString();
    			String finishDate = json_data.get("finishDate").toString();
    			String category = json_data.get("category").toString();
    			String spentLimit = json_data.get("spentLimit").toString();
    			String user = json_data.get("user").toString();
    			String familyUser = json_data.get("familyUser").toString();
    			String created = json_data.get("created").toString();
    			String forUpdate = json_data.get("forUpdate").toString();
    			String forDeletion = json_data.get("forDeletion").toString();
    			
    			startDate = new Functions().convertTimestampToDate(startDate);
    			finishDate = new Functions().convertTimestampToDate(finishDate);

    			makeIdAvailable(id, db);
    			
    			String onServer = "1";
    			String isSurpassed = "0";
    			insertBudget(id, startDate, finishDate, category, spentLimit, user, familyUser, created, forUpdate, forDeletion, onServer, isSurpassed, db);
    		}	
    	}
    	
    	/*this method handles the jsonArray retrieved from a GET request. At first, it converts the values of this jsonArray
    	 * into String values, then it converts them into the proper format, and gets the record with the same id from database.
    	 * After that it compares the values of this two records, and when these values are different, it updates the budget table
    	 * with the value from jsonArray. Finally, it sets forUpdate column of this record into 1, so as to be consider for update.
    	 */
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
    			
    			Cursor queryResult = fetchBudgetById(id, db);
    			
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
    	
    	/*this method handles the jsonArray retrieved from a GET request.It converts the id of this jsonArray
    	 * into String values, and calls updateForDeletion(String, SQLiteDatabase) method
    	 */
    	public void handleBudgetJSONArrayForDeletion(JSONArray json, SQLiteDatabase db) throws JSONException{
    		
    		for(int i=0; i<json.length(); i++){
    			
    			JSONObject json_data = json.getJSONObject(i);

    			String id = json_data.get("id").toString();
    			
    			updateForDeletion(id, db);			
    		}	
    	}

    	//this method returns the records from budget table that are updated
    	public Cursor fetchBudgetForUpdate(SQLiteDatabase db){
    		
    		String[] params = {"1", "1", String.valueOf(User.USER_ID)};
    		Cursor result = db.rawQuery("SELECT * FROM "+FeedBudget.TABLE_NAME+" " +
    									"WHERE "+FeedBudget.FOR_UPDATE+" = ? and "+FeedBudget.ON_SERVER+" = ? and "+
    											FeedBudget.USER+" = ? ", params);
    		return result;
    	}
    	
    	//this method gets as parameters the values of a budget that are able to be updated and converts them into json format
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
    	
    	//this method gets as parameters the details of a budget and converts them into json format
        public JSONArray convertBudgetObjectToJson(String id, String startDate, String finishDate, String category,String spentLimit,
    			String user, String familyUser, String created,String forUpdate, String forDeletion) throws JSONException {

    		Map<String,String> budget = new HashMap<String,String>();
    		budget.put("id", id);
    		budget.put("startDate", startDate);
    		budget.put("finishDate", finishDate);
    		budget.put("category", category);
    		budget.put("spentLimit", spentLimit);
    		budget.put("familyUser", familyUser);
    		budget.put("user", user);
    		budget.put("created", created);
    		budget.put("forUpdate", forUpdate);
    		budget.put("forDeletion", forDeletion);
    		
    		JSONObject temp = new JSONObject(budget);
    		JSONArray json = new JSONArray();
    		
    		json.put(0, temp);		
    		
    		return json;
    	}
        
    	/*this method handles the jsonArray retrieved from the response of a POST request. At first, it converts the values of this jsonArray
    	 * into String values, then it converts them into the proper format, it calls makeIdAvailable function and finally
    	 * it calls updateBudget method.
    	 */   
        public void handleBudgetJSONArrayForUpload(JSONArray json, SQLiteDatabase db) throws JSONException{
    							
    		JSONObject json_data =json.getJSONObject(0);

    		String id = json_data.get("id").toString();
    		String startDate = json_data.get("startDate").toString();
    		String finishDate = json_data.get("finishDate").toString();
    		String category = json_data.get("category").toString();
    		String spentLimit = json_data.get("spentLimit").toString();
    		String user = json_data.get("user").toString();
    		String familyUser = json_data.get("familyUser").toString();
    		String created = json_data.get("created").toString();
    		String forUpdate = json_data.get("forUpdate").toString();
    		String forDeletion = json_data.get("forDeletion").toString();
    	
    		float limit = Float.parseFloat(spentLimit);
    		String format = " %.2f ";
    		spentLimit = String.format(format, limit);
    		
    		startDate = new Functions().convertTimestampToDate(startDate);
    		finishDate = new Functions().convertTimestampToDate(finishDate);
    		
    		makeIdAvailable(id, db);
    		
    		updateBudget(id, startDate, finishDate, category, spentLimit, user, familyUser, created, forUpdate, forDeletion, db);		
    	}

        /*this method updates the id column of a specific entry in the budget table. It is used because when a budget is created 
         * on this phone and it is uploaded to the server database.The id that was locally saved would be different compared to the
         * id used in the server database. To ensure that these two ids will be the same, the budget record at local database must be updated 
         */
    	private void updateBudget(String id, String startDate, String finishDate, String category, String spentLimit, 
    			 					String user, String familyUser, String created, String forUpdate, String forDeletion, SQLiteDatabase db){
    		
    		db.execSQL("UPDATE "+FeedBudget.TABLE_NAME+" SET "+FeedBudget._ID+" = '"+id+"' " +
    					"WHERE "+FeedBudget.START_DATE+" = '"+startDate+"' and "+FeedBudget.END_DATE+" = '"+finishDate+"' and " +
    							 FeedBudget.EXPENSE_CATEGORY+" = '"+category+"' and "+FeedBudget.SPEND_LIMIT+" = '"+spentLimit+"' and " +
    							 FeedBudget.USER+" = '"+user+"' and "+FeedBudget.FAMILY_USER+" = '"+familyUser+"' and "+FeedBudget.BUDGET_CREATED+" = '"+created+"' and " +
    							 FeedBudget.FOR_UPDATE+" = '"+forUpdate+"' and "+FeedBudget.FOR_DELETION+" = '"+forDeletion+"'" );
    		
    		db.execSQL("UPDATE "+FeedBudget.TABLE_NAME+" SET "+FeedBudget.ON_SERVER+" = 1 WHERE "+FeedBudget._ID+" ='"+id+"'" );
    	}	
    	
    	//this method returns the records from budget table that are created from this user on the phone and are stored only locally
    	public Cursor fetchLocalBudget(SQLiteDatabase db){
    		
    		String[] params = {"0", String.valueOf(User.USER_ID), String.valueOf(User.USER_ID)};	
    		Cursor result = db.rawQuery("SELECT * FROM "+FeedBudget.TABLE_NAME+
    									" WHERE "+FeedBudget.ON_SERVER+"=? and ("+FeedBudget.USER+" =? or "+FeedBudget.FAMILY_USER+"= ?)", params);
    		return result;
    	}
    }