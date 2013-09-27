package com.SR.processes;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;

import com.SR.data.Family;
import com.SR.data.Store;
import com.SR.data.User;
import com.SR.data.FeedReaderContract.FeedUser;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

public class RetrieveNewFamilyMemberUserDataTask extends AsyncTask<SQLiteDatabase, Void, String> {

    SQLiteDatabase db;
	
    @Override
	protected String doInBackground(SQLiteDatabase... arg0) {
    	
		String email= null, result;
        String URL = "http://10.0.2.2/php/rest/user.php";
    	HttpEntity entity;
    	InputStream instream;
    	JSONArray jsonArray = null;
		Cursor queryResult;

		db = arg0[0];

		queryResult = new User().fetchUserNotOnServer(db);
		
		if(queryResult.moveToNext()){
			
			email = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedUser.EMAIL));
		
			URL = URL+"/family/"+email;	
			
			entity = new Functions().handleGetRequest(URL);
	        
	        if(entity!=null){
				try{
	        		instream = entity.getContent();
	        		result = new Functions().convertStreamToString(instream);
	
	                jsonArray = new JSONArray(result);
	                instream.close();
	             
	                Log.i("RetrNewFamMember", jsonArray.toString());
	                
	            	new User().handleNewFamilyMemberJSONArray(jsonArray,db); 
	
				} catch (ClientProtocolException e) {
			        e.printStackTrace();
			    } catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		return "OK";
 	}
    	 	
    protected void onPostExecute(String result) {

    }
}