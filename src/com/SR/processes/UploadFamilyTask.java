package com.SR.processes;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.SR.data.Family;
import com.SR.data.FeedReaderContract.FeedFamily;

public class UploadFamilyTask extends AsyncTask<SQLiteDatabase, Void, String> {
   
	SQLiteDatabase db;
	
    @Override
	protected String doInBackground(SQLiteDatabase... arg0) {
        
    	String URL = "http://10.0.2.2/php/rest/family.php";
    	String id, member1, member2, confirmed, created, forDeletion, result;
    	HttpEntity entity;
    	InputStream instream;
    	JSONArray jsonArray,json;
    	Cursor queryResult;
    	
    	db = arg0[0];
/*   	
		db.execSQL("INSERT INTO "+FeedFamily.TABLE_NAME+" ("+FeedFamily._ID+", "+FeedFamily.MEMBER1+", "+FeedFamily.MEMBER2+", "
				+FeedFamily.CONFIRMED+", "+FeedFamily.FAMILY_CREATED+", "
				+FeedFamily.FOR_DELETION+", "+FeedFamily.ON_SERVER+") " +
				"VALUES ('7','4','3','0','2013-09-24 00:00:00','0', '0')");
*/		
		queryResult = new Family().fetchLocalFamily(db);
				
		while(queryResult.moveToNext()){
				
			id = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedFamily._ID));
			member1 = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedFamily.MEMBER1));
			member2 = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedFamily.MEMBER2));
			confirmed = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedFamily.CONFIRMED));
			created = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedFamily.FAMILY_CREATED));
			forDeletion = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedFamily.FOR_DELETION));	
			
    		try{
    			//android.os.Debug.waitForDebugger();
    			json = new Family().convertStringToJson(id, member1, member2, confirmed, created, forDeletion);
    			
    			entity = new Functions().handlePostRequest(json, URL);

    	        if(entity!=null){
	
	        		instream = entity.getContent();
	        		result = new Functions().convertStreamToString(instream);
	
	                jsonArray = new JSONArray(result);
	                instream.close();
	        		
	                Log.i("UploadFamily", jsonArray.toString());
	        		
	    	        new Family().handleFamilyJSONArrayForUpload(jsonArray, db);
    			}
    	        
			} catch (ClientProtocolException e) {
		        e.printStackTrace();
		    } catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
    	return "OK";
    }
    
    @Override
	protected void onPostExecute(String result) {

    }
}