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

/**
 * 
 * @author Ιωάννης Διαμαντίδης 8100039
 * 
 * this AsyncTask is used to call a web service which inserts a family into server database
 *
 */

public class UploadFamilyTask extends AsyncTask<SQLiteDatabase, Void, Void> {
   
	SQLiteDatabase db;
	
    @Override
	protected Void doInBackground(SQLiteDatabase... arg0) {
        
    	String URL = "http://10.0.2.2/php/rest/family.php";//the URL of the web service
    	String id, member1, member2, confirmed, created, forDeletion, forUpdate, result;
    	HttpEntity entity;//contains the response Entity
    	InputStream instream;//used to retrieve the content of the response entity
		JSONArray json, jsonArray;//contain the record that will be sent and the record that will be retrieved with the new id accordingly
    	Cursor queryResult;//contains the families that are stored locally
    	
    	db = arg0[0];
/*
    	db.execSQL("INSERT INTO "+FeedFamily.TABLE_NAME+" ("+FeedFamily._ID+", "+FeedFamily.MEMBER1+", "+FeedFamily.MEMBER2+", "
				+FeedFamily.CONFIRMED+", "+FeedFamily.FAMILY_CREATED+", "
				+FeedFamily.FOR_DELETION+", "+FeedFamily.FOR_UPDATE+", "+FeedFamily.ON_SERVER+") " +
				"VALUES ('7','4','3','0','2013-09-24 00:00:00','0','0', '0')");
*/
		//call fetchLocalFamily method to retrieve the families that are stored locally
		queryResult = new Family().fetchLocalFamily(db);
		//as long as there are families to be uploaded
		while(queryResult.moveToNext()){
			//get the details of the family		
			id = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedFamily._ID));
			member1 = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedFamily.MEMBER1));
			member2 = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedFamily.MEMBER2));
			confirmed = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedFamily.CONFIRMED));
			created = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedFamily.FAMILY_CREATED));
			forDeletion = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedFamily.FOR_DELETION));	
			forUpdate = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedFamily.FOR_UPDATE));	
			
    		try{
    			//convert string values to JSONArray
    			json = new Family().convertStringToJson(id, member1, member2, confirmed, created, forDeletion, forUpdate);
    			//call handlePostRequest method to make a POST request and get the response entity
    			entity = new Functions().handlePostRequest(json, URL);

    	        if(entity!=null){
    				//get he content of the response entity
            		instream = entity.getContent();
            		//call the convertStreamToString method
            		result = new Functions().convertStreamToString(instream);
            		//create a JSONArray with the response content
                    jsonArray = new JSONArray(result);
                    //close InputStream
                    instream.close();
	        		
	                Log.i("UploadFamily", jsonArray.toString());
	        		//call handleFamilyJSONArrayForUpload method
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
    	return null;
    }
}