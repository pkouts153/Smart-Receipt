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

public class UpdateUserTask extends AsyncTask<SQLiteDatabase, Void, String> {
	
    SQLiteDatabase db;

    @Override
	protected String doInBackground(SQLiteDatabase... arg0) {
       
    	String URL = "http://10.0.2.2/php/rest/user.php";
		String id, password, email;

		JSONArray json;
		Cursor userDetails;	
		
		db = arg0[0];
/*				
		db.execSQL("UPDATE "+FeedUser.TABLE_NAME+" SET "+FeedUser.EMAIL+" = 'giannis@mail.com' WHERE "+FeedUser._ID+" = "+ User.USER_ID+"");
		db.execSQL("UPDATE "+FeedUser.TABLE_NAME+" SET "+FeedUser.FOR_UPDATE+" = '1' WHERE "+FeedUser._ID+" = "+ User.USER_ID+"");
*/		
		userDetails =new User().fetchUserForUpdate(db);
		
		if(userDetails.moveToNext()){
			
			id = userDetails.getString(userDetails.getColumnIndexOrThrow(FeedUser._ID));
			password = userDetails.getString(userDetails.getColumnIndexOrThrow(FeedUser.PASSWORD));
			email = userDetails.getString(userDetails.getColumnIndexOrThrow(FeedUser.EMAIL));
		
    		try{
    			//android.os.Debug.waitForDebugger();
    			json = new User().convertStringToJson( id, password, email);
    		
    			StatusLine statusLine = new Functions().handlePutRequest(json, URL);    			
    	        
    			Log.i("UpdateUserStatus", statusLine.toString());
    			Log.i("UpdateUser", json.toString()); 
 
    	        if(statusLine.getStatusCode() == HttpStatus.SC_OK){
    	    		
    				db.execSQL("UPDATE "+FeedUser.TABLE_NAME+" SET "+FeedUser.FOR_UPDATE+" = '0' WHERE "+FeedUser._ID+" = '"+id+"'");    				
    	         } 
    			
    		} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return "ok";
    }
    
    @Override
	protected void onPostExecute(String result) {

    }
}