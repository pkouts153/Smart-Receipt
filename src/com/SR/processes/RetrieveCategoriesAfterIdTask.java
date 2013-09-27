package com.SR.processes;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;

import com.SR.data.Category;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

public class RetrieveCategoriesAfterIdTask extends AsyncTask<SQLiteDatabase, Void, String> {
	
    SQLiteDatabase db;
	
    @Override
	protected String doInBackground(SQLiteDatabase... arg0) {

    	String result;
    	String URL = "http://10.0.2.2/php/rest/category.php";
    	HttpEntity entity;
    	InputStream instream;
    	JSONArray jsonArray = null;
    	
    	db = arg0[0];
		URL = URL+"/after/"+new Category().fetchCategoryMaxId(db);	
		
		entity = new Functions().handleGetRequest(URL);
        
        if(entity!=null){
			try{
        		instream = entity.getContent();
        		result = new Functions().convertStreamToString(instream);

                jsonArray = new JSONArray(result);
                instream.close();
              
                Log.i("RetrCatAfterId", jsonArray.toString());

	        	new Category().handleCategoryJSONArray(jsonArray, db);

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