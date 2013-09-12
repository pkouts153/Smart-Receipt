package com.SR.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.SR.data.FeedReaderContract.FeedUser;

public class User {

	public static int USER_ID;
	
	String USERNAME;
    String PASSWORD;
    String EMAIL;
    
    FeedReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    Cursor c;
    
    Context context;
    
	public User() {
		// TODO Auto-generated constructor stub
	}
	
    public Cursor getUsers(){
    	
    	mDbHelper = new FeedReaderDbHelper(context);
		
		// Gets the data repository in write mode
		db = mDbHelper.getWritableDatabase();
    	
		// Specifies which columns are needed from the database
		String[] projection = {
			FeedUser.USERNAME
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
    
    public FeedReaderDbHelper getFeedReaderDbHelper(){
    	return mDbHelper;
    }
}
