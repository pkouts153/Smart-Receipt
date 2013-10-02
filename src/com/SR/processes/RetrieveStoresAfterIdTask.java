package com.SR.processes;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;

import com.SR.data.Store;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

/**
 * 
 * @author Ιωάννης Διαμαντίδης 8100039
 * 
 * this AsyncTask is used to retrieve Store records from the server database.
 *  These records will be after the maximum id stored in this table 
 *
 */

public class RetrieveStoresAfterIdTask extends AsyncTask<SQLiteDatabase, Void, Void> {

    SQLiteDatabase db;
	
    @Override
	protected Void doInBackground(SQLiteDatabase... arg0) {
    	
    	String result;//contains the String value of the response content
        String URL = "http://10.0.2.2/php/rest/store.php";//the web service URL
    	HttpEntity entity;//contains the response Entity
    	InputStream instream;//used to retrieve the content of the response entity
    	JSONArray jsonArray = null;//contains the response content in JSONArray format
    	
		db = arg0[0];
		
		//call fetchStoreMaxId method to get the maximum saved id and add this id to the URL
		URL = URL+"/after/"+new Store().fetchStoreMaxId(db);	
		
		//call handleGetRequest function to make a GET Request and retrieve the response entity
		entity = new Functions().handleGetRequest(URL);
        
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
             
                Log.i("RetrStoAfterId", jsonArray.toString());
        		//call handleStoreJSONArray method
                new Store().handleStoreJSONArray(jsonArray, db);

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