package com.SR.processes;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.json.JSONArray;
import org.json.JSONException;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.SR.data.FeedReaderContract.FeedUser;
import com.SR.data.User;

/**
 * 
 * @author Ιωάννης Διαμαντίδης 8100039
 * 
 * this AsyncTask is used to call a web service which updates a user
 *
 */

public class UpdateUserTask extends AsyncTask<SQLiteDatabase, Void, Void> {
	
    SQLiteDatabase db;

    @Override
	protected Void doInBackground(SQLiteDatabase... arg0) {
       
    	String URL = "http://10.0.2.2/php/rest/user.php";//the URL of the web service
		String id, password, email;
		JSONArray json;
		Cursor userDetails;	
		
		db = arg0[0];
/*				
		db.execSQL("UPDATE "+FeedUser.TABLE_NAME+" SET "+FeedUser.EMAIL+" = 'giannis@mail.com' WHERE "+FeedUser._ID+" = "+ User.USER_ID+"");
		db.execSQL("UPDATE "+FeedUser.TABLE_NAME+" SET "+FeedUser.FOR_UPDATE+" = '1' WHERE "+FeedUser._ID+" = "+ User.USER_ID+"");
*/		
		//call fetchUserForUpdate method to retrieve the user that will be updated
		userDetails =new User().fetchUserForUpdate(db);
		//if this user is for update
		if(userDetails.moveToNext()){
			//get the details of the user		
			id = userDetails.getString(userDetails.getColumnIndexOrThrow(FeedUser._ID));
			password = userDetails.getString(userDetails.getColumnIndexOrThrow(FeedUser.PASSWORD));
			email = userDetails.getString(userDetails.getColumnIndexOrThrow(FeedUser.EMAIL));
		
    		try{
    			//convert string values to JSONArray
    			json = new User().convertStringToJson( id, password, email);
    			//call handlePutRequest method to make a PUT request and get the response status
    			StatusLine statusLine = new Functions().handlePutRequest(json, URL);    			
    	        
    			Log.i("UpdateUserStatus", statusLine.toString());
    			Log.i("UpdateUser", json.toString()); 
    			//if status is Ok, or code is 200
    	        if(statusLine.getStatusCode() == HttpStatus.SC_OK){
    	    		//set this record to not for Update
    				db.execSQL("UPDATE "+FeedUser.TABLE_NAME+" SET "+FeedUser.FOR_UPDATE+" = '0' WHERE "+FeedUser._ID+" = '"+id+"'");    				
    	         } 
    			
    		} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
    }
}