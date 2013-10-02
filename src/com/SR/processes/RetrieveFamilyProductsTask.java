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
import com.SR.data.FeedReaderContract.FeedUser;
import com.SR.data.Product;

/**
 * 
 * @author Ιωάννης Διαμαντίδης 8100039
 * 
 * this AsyncTask is used to retrieve product records for a family member from the server database.
 *  This record will be found by the id
 *
 */

public class RetrieveFamilyProductsTask extends AsyncTask<SQLiteDatabase, Void, Void> {

    SQLiteDatabase db;
   
    @Override
 	protected Void doInBackground(SQLiteDatabase... arg0) {
    	
       	String result, FamilyMemberId;
    	HttpEntity entity;//contains the response Entity
    	InputStream instream;//used to retrieve the content of the response entity
    	JSONArray jsonArray = null;//contains the response content in JSONArray format

    	db = arg0[0];
    	
    	//call fetchUsersWithoutProductsInDB to get family members without products in database
    	Cursor queryResult = new Family().fetchUsersWithoutProductsInDB(db);
    	
    	//as long as there are family members
    	while(queryResult.moveToNext()){
    		
    		//get the details of the family		
    		FamilyMemberId = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedUser._ID));
			
    		//the web service URL
			String URL = "http://10.0.2.2/php/rest/product.php";
			//add the FamilyMemberId to the URL
			URL = URL + "/" +FamilyMemberId;

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
	                             
	                Log.i("RetrieveFamilyProduct", jsonArray.toString());
	        		//call handleProductJSONArrayForRetrieve method
	                new Product().handleProductJSONArrayForRetrieve(jsonArray, db);
	
				} catch (ClientProtocolException e) {
			        e.printStackTrace();
			    } catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
    	}
		return null;
 	}
}