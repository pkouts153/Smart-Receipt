package com.SR.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.SR.data.FeedReaderContract.FeedFamily;
import com.SR.data.FeedReaderContract.FeedUser;

public class User {

	public static int USER_ID;
	
	/*String USERNAME;
    String PASSWORD;
    String EMAIL;*/
    
    FeedReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    Cursor c;
    
    Context context;
    
	public User(Context c) {
		context = c;
	}
	
    public Cursor getUsers(){
    	
    	mDbHelper = new FeedReaderDbHelper(context);
		
		// Gets the data repository in write mode
		db = mDbHelper.getWritableDatabase();
    	
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
    	
    	mDbHelper = new FeedReaderDbHelper(context);
		
		// Gets the data repository in write mode
		db = mDbHelper.getWritableDatabase();
		
		
    	String query = "SELECT DISTINCT " + FeedUser.USERNAME +
    				   " FROM " + FeedUser.TABLE_NAME +
    				   " WHERE " + FeedUser._ID + "=(SELECT DISTINCT " + FeedFamily.MEMBER2 +
    				   								" FROM " + FeedFamily.TABLE_NAME +
    				   								" WHERE " + FeedFamily.MEMBER1 + "=" + user + " AND " + FeedFamily.CONFIRMED + "=1" +
    				   								" UNION ALL " +
    				   								"SELECT DISTINCT " + FeedFamily.MEMBER1 +
    				   								" FROM " + FeedFamily.TABLE_NAME +
    				   								" WHERE " + FeedFamily.MEMBER2 + "=" + user + " AND " + FeedFamily.CONFIRMED + "=1)";
    	
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
    	
    	while (!c.isLast ()){
    		if (user.equals(c.getString(c.getColumnIndexOrThrow(FeedUser.USERNAME)))) {
    			id = c.getInt(c.getColumnIndexOrThrow(FeedUser._ID));
    		}
    		c.moveToNext ();
    	}
    	
		if (user.equals(c.getString(c.getColumnIndexOrThrow(FeedUser.USERNAME)))) {
			id = c.getInt(c.getColumnIndexOrThrow(FeedUser._ID));
		}
		
    	return id;
    }

    
    public void userLogout() {
    	USER_ID = 0;
    }
    
    public FeedReaderDbHelper getUserFeedReaderDbHelper(){
    	return mDbHelper;
    }
    
}
