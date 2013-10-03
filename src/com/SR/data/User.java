package com.SR.data;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.SR.data.FeedReaderContract.FeedFamily;
import com.SR.data.FeedReaderContract.FeedUser;
import com.SR.smartreceipt.R;

/**
* This class represents user and is responsible for the necessary processes
* 
* @author Παναγιώτης Κουτσαυτίκης 8100062
*/
public class User {

	// saves the details of the user that is logged in
	public static int USER_ID;
		
    SQLiteDatabase db;
    Cursor c;
   
    /**
     * An object for accessing preference data
     */
    SharedPreferences sharedPref;
    
    /**
    * User constructor 
    * 
    * @param database   saves the database object, that was passed from the Activity, 
    * 					in the database object of the class for use in the methods
    */
	public User(SQLiteDatabase database) {
		db = database;
	}
	
	/**
	 * Gets users from the database
	 * 
	 * @return a cursor with the users
	 */
    public Cursor getUsers(){

		// Specifies which columns are needed from the database
		String[] projection = {
			FeedUser._ID,
			FeedUser.USERNAME,
			FeedUser.PASSWORD,
			FeedUser.EMAIL
		    };
		
		c = db.query(
			FeedUser.TABLE_NAME,  				  // The table to query
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
     * Gets the names of the family members of a given user
     * 
     * @param user  the id of the user
     * @return a cursor with the family names
     */
    public Cursor getFamilyMembers(int user){
    	
    	String query = "SELECT DISTINCT " + FeedUser.USERNAME +
    				   " FROM " + FeedUser.TABLE_NAME +
    				   " WHERE " + FeedUser._ID + " IN (SELECT DISTINCT " + FeedFamily.MEMBER2 +
    				   								" FROM " + FeedFamily.TABLE_NAME +
    				   								" WHERE " + FeedFamily.MEMBER1 + "=" + user + " AND " + FeedFamily.CONFIRMED + "=1 AND " + FeedFamily.FOR_DELETION + "=0" +
    				   								" UNION ALL " +
    				   								"SELECT DISTINCT " + FeedFamily.MEMBER1 +
    				   								" FROM " + FeedFamily.TABLE_NAME +
    				   								" WHERE " + FeedFamily.MEMBER2 + "=" + user + " AND " + FeedFamily.CONFIRMED + "=1 AND " + FeedFamily.FOR_DELETION + "=0)";
    	
    	c = db.rawQuery(query, null);
    	
		return c;
    }  
    
    /**
     * Checks if the user that attempts to log in exists in the database and if he does save his id
     * 
     * @param email the typed email of the user
     * @param password the typed password of the user
     * @return whether the user exists
     */
    public boolean userLogin(String email, String password, Context context){
    	
    	boolean found = false;
    	c = getUsers();
    	
    	c.moveToFirst();
    	
    	while (!c.isAfterLast ()){
    		if (password.equals(c.getString(c.getColumnIndexOrThrow(FeedUser.PASSWORD))) && email.equals(c.getString(c.getColumnIndexOrThrow(FeedUser.EMAIL)))) {
    			found = true;
    			USER_ID = c.getInt(c.getColumnIndexOrThrow(FeedUser._ID));
    		}
    		c.moveToNext ();
    	}
		
        // get an object to access the sharedPreferences
        sharedPref = context.getSharedPreferences(context.getString(R.string.preference_user_id), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        
        // add the user details to the shared preferences, in order not to ask the user
        // to login every time he closes and opens the application
        editor.putInt("USER_ID", USER_ID);
        editor.commit();
        
    	return found;
    }
    
	/**
	 * Finds the id of a user whose username is been given
	 * 
	 * @param user  the username
	 * @return the id of the user
	 */
    public int getId(String user){
    	
    	int id = 0;
    	c = getUsers();
    	
    	c.moveToFirst();
    	
    	while (!c.isAfterLast ()){
    		if (user.equals(c.getString(c.getColumnIndexOrThrow(FeedUser.USERNAME)))) {
    			id = c.getInt(c.getColumnIndexOrThrow(FeedUser._ID));
    		}
    		c.moveToNext ();
    	}
		
    	return id;
    }

    /**
     * This method is called when the user wants to logout and 
     * replaced the details with the default value
     */
    public void userLogout(Context context) {
    	USER_ID = 0;
        sharedPref = context.getSharedPreferences(context.getString(R.string.preference_user_id), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("USER_ID", USER_ID);
        editor.commit();
    }
    
    
    
    
    
  /**
   * @author Ιωάννης Διαμαντίδης 8100039  
   */
    
    public User() {
    	
	}
    
    //this method checks if any value is stored in the user table
    public boolean isDatabaseEmpty(SQLiteDatabase db){
    	
		Boolean empty = true;
		Cursor result = db.rawQuery("SELECT count("+FeedUser._ID +") AS count FROM "+FeedUser.TABLE_NAME, null);
		if(result.moveToNext()){
			
			if( result.getInt(result.getColumnIndexOrThrow("count")) > 0){
				empty=false;
			}
		}
		return empty;
    }
    
    //this method returns the user details if they are for update
	public Cursor fetchUserForUpdate(SQLiteDatabase db){

		String[] params = {"1", String.valueOf(User.USER_ID)};
		Cursor result = db.rawQuery("SELECT * FROM "+FeedUser.TABLE_NAME+
								   " WHERE "+FeedUser.FOR_UPDATE+" = ? and "+FeedUser._ID+"= ? ", params);
		return result;
	}
	
	/*this method creates a jsonArray that contains the id, the password and the email of a user.*/
    public JSONArray convertStringToJson(String id, String password, String email) throws JSONException {
		
    	Map<String,String> user = new HashMap<String,String>();
    	user.put("id", id);
    	user.put("password", password);
    	user.put("email", email);

		JSONObject temp = new JSONObject(user);
		JSONArray json = new JSONArray();
		
		json.put(0, temp);		
		
		return json;
	}
   
    /*this method handles the response JSONArray object from a PUT request. It converts its data into String variables.
     *Then it updates the user record*/
	public void handleUserJSONArrayForUpdate(JSONArray json, SQLiteDatabase db) throws JSONException{
		
		if(json.length()==1){
			JSONObject json_data = json.getJSONObject(0);
			
			String id = json_data.get("id").toString();
			String newPassword = json_data.get("password").toString();
			String newEmail = json_data.get("email").toString();
			
			updateUserDetails(id, newPassword, newEmail, db);
		}
	}
	
	/*this method gets the details of a user, gets the user record from database and updates the database record.
	 * It is used when an update happens
	 */
	private void updateUserDetails(String id, String newPassword, String newEmail, SQLiteDatabase db){
		
		Cursor result = fetchUserById(id, db);
		
		result.moveToFirst();
		String prevPassword = result.getString(result.getColumnIndexOrThrow(FeedUser.PASSWORD));
		String prevEmail = result.getString(result.getColumnIndexOrThrow(FeedUser.EMAIL));
				
		if(!(newPassword.equals(prevPassword)))
			db.execSQL("UPDATE "+FeedUser.TABLE_NAME+" SET "+FeedUser.PASSWORD+" = '"+newPassword+"' WHERE "+FeedUser._ID+" = '"+id+"'");
		if(!(newEmail.equals(prevEmail)))
			db.execSQL("UPDATE "+FeedUser.TABLE_NAME+" SET "+FeedUser.EMAIL+" = '"+newEmail+"' WHERE "+FeedUser._ID+" = '"+id+"'");
		
		db.execSQL("UPDATE "+FeedUser.TABLE_NAME+" SET "+FeedUser.FOR_UPDATE+" = 1 WHERE "+FeedUser._ID+" = '"+id+"'");

	}
	
	//this method return a user record with a specific id
	private Cursor fetchUserById(String id, SQLiteDatabase db){
		
		String[] param ={id};
		Cursor result = db.rawQuery("SELECT * FROM "+FeedUser.TABLE_NAME+" WHERE "+FeedUser._ID+" = ?", param);
		
		return result;
	}
   
	/*this method handles the response JSONArray object from a POST request. It converts its data into String variables.
	 * Then, check if a record with this id exists in database. 
	 * If it exists, check if this record was taken from server
	 *  	or if it was created in the phone. If it was taken from server, update the password column, else get the value of the record,
	 *  	insert them with a new id, and then delete the record with the previous id.
	 * Else, if there is no record with this id, set that this record was taken from the server and insert it into the database
	 */
	public void handleUserJSONArrayForRetrieve(JSONArray json, SQLiteDatabase db) throws JSONException{
		
		for(int i=0; i<json.length(); i++){
			
			JSONObject json_data =json.getJSONObject(i);
		
			String id = json_data.get("id").toString();
			String username = json_data.get("username").toString();
			String password = json_data.get("password").toString();
			String email = json_data.get("email").toString();
			String created = json_data.get("created").toString();
			String forUpdate = json_data.get("forUpdate").toString();

			Cursor result = fetchUserById(id,db);
			
			if(result.moveToNext()){
				
				String fromServer = result.getString(result.getColumnIndexOrThrow(FeedUser.FROM_SERVER));
				if("1".equals(fromServer)){
					db.execSQL("UPDATE "+FeedUser.TABLE_NAME+" SET "+FeedUser.PASSWORD+" = '"+password+"' WHERE "+FeedUser._ID+" = '"+id+"'");
				} else {
					String prevId = result.getString(result.getColumnIndexOrThrow(FeedUser._ID));
					String prevUsername = result.getString(result.getColumnIndexOrThrow(FeedUser.USERNAME));
					String prevPassword = result.getString(result.getColumnIndexOrThrow(FeedUser.PASSWORD));
					String prevEmail = result.getString(result.getColumnIndexOrThrow(FeedUser.EMAIL));
					String prevCreated = result.getString(result.getColumnIndexOrThrow(FeedUser.USER_CREATED));
					String prevForUpdate = result.getString(result.getColumnIndexOrThrow(FeedUser.FOR_UPDATE));
					String prevfromServer = result.getString(result.getColumnIndexOrThrow(FeedUser.FROM_SERVER));
					
					insertUser(null, prevUsername, prevPassword, prevEmail, prevCreated, prevForUpdate, prevfromServer, db);
					
					db.execSQL("DELETE FROM "+FeedUser.TABLE_NAME+" WHERE "+FeedUser._ID+" = '"+prevId+"'");	
				}
			} else {
				String fromServer = "1";
				insertUser(id, username, password, email, created, forUpdate, fromServer , db);
			}
			if(USER_ID == 0)
				USER_ID = Integer.valueOf(id);
		}
	}
	
	/*
	 * this method gets the values of a user object and insert them into the database
	 */
	public void insertUser(String id, String username, String password, String email, String created, String forUpdate,
																								String fromServer, SQLiteDatabase db){
		db.execSQL("INSERT INTO "+FeedUser.TABLE_NAME+" ("+FeedUser._ID+", "+FeedUser.USERNAME+", "+FeedUser.PASSWORD+", " +
								""+FeedUser.EMAIL+", "+FeedUser.USER_CREATED+", "+FeedUser.FOR_UPDATE+", "+FeedUser.FROM_SERVER+") " +
				   "VALUES ('"+id+"','"+username+"','"+password+"','"+email+"','"+created+"','"+forUpdate+"','"+fromServer+"')");
	}
	
	//this method returns the entries in user table that are created locally
	public Cursor fetchUserNotFromServer(SQLiteDatabase db){

		String[] params = {"0"};
		Cursor result = db.rawQuery("SELECT * FROM "+FeedUser.TABLE_NAME+" WHERE "+FeedUser.FROM_SERVER+" = ? ", params);
		return result;
	}

	//this method convert the id and the email of a user into json format. it is used when a new family member is made by his email.
	public JSONArray convertUserToJson(String id, String email) throws JSONException {
		
		Map<String,String> user = new HashMap<String,String>();
		user.put("id", id);
		user.put("email", email);

		JSONObject temp = new JSONObject(user);
		JSONArray json = new JSONArray();
		
		json.put(0, temp);		
		
		return json;	
	}
	
	/*this method handles the user data of a new family member, that exists in local database, 
	 * but his details are not download from server.This method gets this details from a jsonArray, updates the record from
	 * family table in order to correspond to the new id of the member2 user, delete the record of member2 from user table, 
	 * and insert his data in a new record with the right id.
	 */
	public void handleNewFamilyMemberJSONArray(JSONArray json, SQLiteDatabase db) throws JSONException {
		
		if(json.length()>0){
			
			JSONObject json_data = json.getJSONObject(0);
			
			String id = json_data.get("id").toString();
			String username = json_data.get("username").toString();
			String email = json_data.get("email").toString();
			String created = json_data.get("created").toString();
			String forUpdate = json_data.get("forUpdate").toString();
			
			db.execSQL("UPDATE "+FeedFamily.TABLE_NAME+" SET "+FeedFamily.MEMBER2+" = '"+id+"' " +
					"WHERE "+FeedFamily.MEMBER2+" = (SELECT "+FeedUser._ID+" FROM "+FeedUser.TABLE_NAME+" WHERE "+FeedUser.EMAIL+"='"+email+"')" );
						
			db.execSQL("DELETE FROM "+FeedUser.TABLE_NAME+" WHERE "+FeedUser.EMAIL+" = '"+email+"' and "+FeedUser.FROM_SERVER+" = 0 ");
      		
			insertUser(id, username, null, email, created, forUpdate, "1" , db);
		}
	}
}