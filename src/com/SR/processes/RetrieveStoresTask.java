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

public class RetrieveStoresTask extends AsyncTask<SQLiteDatabase, Void, String> {

    SQLiteDatabase db;
	
    @Override
 	protected String doInBackground(SQLiteDatabase... arg0) {
    	
     	String result;      
     	String URL = "http://10.0.2.2/php/rest/store.php";
    	HttpEntity entity;
    	InputStream instream;
    	JSONArray jsonArray = null;
    	
		db = arg0[0];
		
		entity = new Functions().handleGetRequest(URL);
        
        if(entity!=null){
			try{
        		instream = entity.getContent();
        		result = new Functions().convertStreamToString(instream);

                jsonArray = new JSONArray(result);
                instream.close();
                
                Log.i("RetrieveStores", jsonArray.toString());

                new Store().handleStoreJSONArray(jsonArray, db);
                
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
    	 	
    protected void onPostExecute(String result) {

    }
}