package com.SR.processes;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.SR.data.Offer;
import com.SR.data.User;
import com.SR.smartreceipt.MainActivity;

public class RetrieveUserDataTask extends AsyncTask<SQLiteDatabase, Void, String> {
    
	SQLiteDatabase db;
	private Context context;
	
	public RetrieveUserDataTask(Context ctx){
	    super();
	    this.context=ctx;
	}
	
    @Override
	protected String doInBackground(SQLiteDatabase... arg0) {
    	
    	String result;
    	String URL = "http://10.0.2.2/php/rest/user.php";
    	HttpEntity entity;
    	InputStream instream;
    	JSONArray jsonArray = null;
    	String status="";
    	
    	db = arg0[0];
	
		entity = new Functions().handleGetRequest( URL);
        
        if(entity!=null){
			try{
        		instream = entity.getContent();
        		result = new Functions().convertStreamToString(instream);

                jsonArray = new JSONArray(result);
                instream.close();
                
                Log.i("RetrieveUser", jsonArray.toString());
                
	        	new User().handleUserJSONArrayForRetrieve(jsonArray,db);
	        	
	        	status = "ok";
	        	
			} catch (ClientProtocolException e) {
		        e.printStackTrace();
		    } catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
   		return status;
	}
    	 	
    protected void onPostExecute(String result) {
    	if(result.equals("ok")){
    		Intent intent = new Intent(context, MainActivity.class);
    		context.startActivity(intent);
			
    		new RetrieveCategoriesTask().execute(db);  
			new RetrieveOffersTask().execute(db);  
			new RetrieveStoresTask().execute(db);	
			new RetrieveBudgetsTask().execute(db);  
			new RetrieveFamilyTask().execute(db);  
			new RetrieveProductsTask().execute(db);
			new RetrieveFamilyUserDataTask().execute(db);
			new RetrieveFamilyProductsTask().execute(db);
			
			new Offer().deleteEndedOffers(db);
    	} else{
    		//show message that there are no smartReceipt user with this data
    	}
    }	
}