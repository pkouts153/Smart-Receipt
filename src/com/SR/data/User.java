package com.SR.data;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.SR.data.FeedReaderContract.FeedFamily;
import com.SR.data.FeedReaderContract.FeedUser;

public class User {

	public static int USER_ID;

    SQLiteDatabase db;
    Cursor c;
   
	public User(SQLiteDatabase database) {
		db = database;
	}
	
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
    

    public boolean userLogin(String email, String password){
    	
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
		
    	return found;
    }
    
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

    
    public void userLogout() {
    	USER_ID = 0;
    }
    
    
    
    
    public User() {
    	
	}
    
    public boolean isDatabaseEmpty(){
    	
		//mDbHelper = new FeedReaderDbHelper(context);
		//db = mDbHelper.getWritableDatabase();
		
		Boolean empty = true;
		Cursor result = db.rawQuery("SELECT count("+FeedUser._ID +") AS count FROM "+FeedUser.TABLE_NAME, null);
		if(result.moveToNext()){
			
			if( result.getInt(result.getColumnIndexOrThrow("count")) > 0){
				empty=false;
			}
		}
		return empty;
    }
    
	public Cursor fetchUserForUpdate(SQLiteDatabase db){

		String[] params = {"1", String.valueOf(User.USER_ID)};
		Cursor result = db.rawQuery("SELECT * FROM "+FeedUser.TABLE_NAME+
								   " WHERE "+FeedUser.FOR_UPDATE+" = ? and "+FeedUser._ID+"= ? ", params);
		return result;
	}
	
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
	
	public void handleUserJSONArrayForUpdate(JSONArray json, SQLiteDatabase db) throws JSONException{
		
		if(json.length()==1){
			JSONObject json_data = json.getJSONObject(0);
			
			String id = json_data.get("id").toString();
			String newPassword = json_data.get("password").toString();
			String newEmail = json_data.get("email").toString();
			
			updateUserDetails(id, newPassword, newEmail);
		}
	}
	
	private void updateUserDetails(String id, String newPassword, String newEmail){
		
		//mDbHelper = new FeedReaderDbHelper(context);
		//db = mDbHelper.getWritableDatabase();
		
		Cursor result = fetchUserById(id);
		
		result.moveToFirst();
		String prevPassword = result.getString(result.getColumnIndexOrThrow(FeedUser.PASSWORD));
		String prevEmail = result.getString(result.getColumnIndexOrThrow(FeedUser.EMAIL));
				
		if(!(newPassword.equals(prevPassword)))
			db.execSQL("UPDATE "+FeedUser.TABLE_NAME+" SET "+FeedUser.PASSWORD+" = '"+newPassword+"' WHERE "+FeedUser._ID+" = '"+id+"'");
		if(!(newEmail.equals(prevEmail)))
			db.execSQL("UPDATE "+FeedUser.TABLE_NAME+" SET "+FeedUser.EMAIL+" = '"+newEmail+"' WHERE "+FeedUser._ID+" = '"+id+"'");
		
		db.execSQL("UPDATE "+FeedUser.TABLE_NAME+" SET "+FeedUser.FOR_UPDATE+" = 1 WHERE "+FeedUser._ID+" = '"+id+"'");

	}
	
	private Cursor fetchUserById(String id){
		
		//mDbHelper = new FeedReaderDbHelper(context);
		//db = mDbHelper.getWritableDatabase();
		
		String[] param ={id};
		Cursor result = db.rawQuery("SELECT * FROM "+FeedUser.TABLE_NAME+" WHERE "+FeedUser._ID+" = ?", param);
		
		return result;
	}
	
	public void handleUserJSONArrayForRetrieve(JSONArray json, SQLiteDatabase database) throws JSONException{
		
		for(int i=0; i<json.length(); i++){
			
			JSONObject json_data =json.getJSONObject(i);
		
			String id = json_data.get("id").toString();
			String username = json_data.get("username").toString();
			String password = json_data.get("password").toString();
			String email = json_data.get("email").toString();
			String created = json_data.get("created").toString();
			String forUpdate = json_data.get("forUpdate").toString();
		
			database.execSQL("INSERT INTO "+FeedUser.TABLE_NAME+" ("+FeedUser._ID+", "+FeedUser.USERNAME+", "+FeedUser.PASSWORD+", "+FeedUser.EMAIL+", "+FeedUser.USER_CREATED+", "+FeedUser.FOR_UPDATE+", "+FeedUser.FROM_SERVER+") " +
					   "VALUES ('"+id+"','"+username+"','"+password+"','"+email+"','"+created+"','"+forUpdate+"','1')");
			if(USER_ID == 0)
				USER_ID = Integer.valueOf(id);
		}
	}
	
	public Cursor fetchUserNotOnServer(SQLiteDatabase db){

		String[] params = {"0"};
		Cursor result = db.rawQuery("SELECT * FROM "+FeedUser.TABLE_NAME+" WHERE "+FeedUser.FROM_SERVER+" = ? ", params);
		return result;
	}

	public JSONArray convertUserToJson(String id, String email) throws JSONException {
		
		Map<String,String> user = new HashMap<String,String>();
		user.put("id", id);
		user.put("email", email);

		JSONObject temp = new JSONObject(user);
		JSONArray json = new JSONArray();
		
		json.put(0, temp);		
		
		return json;	
	}

	public void handleNewFamilyMemberJSONArray(JSONArray json, SQLiteDatabase db) throws JSONException {
		
		JSONObject json_data = json.getJSONObject(0);
		
		String id = json_data.get("id").toString();
		String username = json_data.get("username").toString();
		String password = json_data.get("password").toString();
		String email = json_data.get("email").toString();
		String created = json_data.get("created").toString();
		String forUpdate = json_data.get("forUpdate").toString();
		
		db.execSQL("UPDATE "+FeedFamily.TABLE_NAME+" SET "+FeedFamily.MEMBER2+" = '"+id+"' " +
				"WHERE "+FeedFamily.MEMBER2+" = (SELECT "+FeedUser._ID+" FROM "+FeedUser.TABLE_NAME+" WHERE "+FeedUser.EMAIL+"='"+email+"')" );
		
		db.execSQL("DELETE FROM "+FeedUser.TABLE_NAME+" WHERE "+FeedUser.EMAIL+" = '"+email+"' and "+FeedUser.FROM_SERVER+" = 0 ");
		
		db.execSQL("INSERT INTO "+FeedUser.TABLE_NAME+" ("+FeedUser._ID+", "+FeedUser.USERNAME+", "+FeedUser.PASSWORD+", "+FeedUser.EMAIL+", "+FeedUser.USER_CREATED+", "+FeedUser.FOR_UPDATE+", "+FeedUser.FROM_SERVER+") " +
				   "VALUES ('"+id+"','"+username+"','"+password+"','"+email+"','"+created+"','"+forUpdate+"','1')");
		

	}

}

