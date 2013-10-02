package com.SR.processes;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.SR.data.Family;

/**
 * 
 * @author Ιωάννης Διαμαντίδης 8100039
 * 
 * this AsyncTask is used to retrieve family records which are deleted from other users or from this user from the site
 *
 */

public class RetrieveDeletedFamilyTask extends AsyncTask<SQLiteDatabase, Void, Void> {
	
    SQLiteDatabase db;
	
    @Override
	protected Void doInBackground(SQLiteDatabase... arg0) {
      
    	String result;//contains the String value of the response content
    	String URL = "http://10.0.2.2/php/rest/family.php/deleted";//the web service URL
    	HttpEntity entity;//contains the response Entity
    	InputStream instream;//used to retrieve the content of the response entity
    	JSONArray jsonArray = null;//contains the response content in JSONArray format

    	db = arg0[0];
		
    	//call handleGetRequest function to make a GET Request and retrieve the response entity
		entity = new Functions().handleGetRequest( URL);

        if(entity!=null){
			try{
				//get he content of the response entity
        		instream = entity.getContent();
        		//call the convertStreamToString method
        		result = new Functions().convertStreamToString(instream);
        		//create a JSONArray with the response content
                jsonArray = new JSONArray(result);
                //close InputStream
                instream.close();
                
        		Log.i("DeletedFamily", jsonArray.toString());
        		//call handleFamilyJSONArrayForDeletion method
        		new Family().handleFamilyJSONArrayForDeletion(jsonArray, db);

			} catch (ClientProtocolException e) {
		        e.printStackTrace();
		    } catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
        
    	//call DeleteFamilyTask AsyncTask
    	new DeleteFamilyTask().execute(db);
    	
		return null;
 	}
}