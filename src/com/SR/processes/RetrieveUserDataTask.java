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
import android.app.FragmentManager;
import android.util.Log;

import com.SR.data.User;
import com.SR.smartreceipt.InputErrorDialogFragment;
import com.SR.smartreceipt.MainActivity;
import com.SR.smartreceipt.R;

/**
 * 
 * @author Ιωάννης Διαμαντίδης 8100039
 * 
 * this AsyncTask is used to retrieve User records from the server database using the credentials in the header of the request
 *
 */

public class RetrieveUserDataTask extends AsyncTask<SQLiteDatabase, Void, String> {
    
	SQLiteDatabase db;
	private Context context;
	private FragmentManager fragment;
	
	/*constructor with parameter a context used to start MainActivity and a FragmentManager to display Error*/
	public RetrieveUserDataTask(Context ctx, FragmentManager frag){
	    super();
	    this.context=ctx;
	    this.fragment = frag;
	}
	
    @Override
	protected String doInBackground(SQLiteDatabase... arg0) {
    	
    	String result;//contains the String value of the response content
    	String URL = "http://10.0.2.2/php/rest/user.php";//the web service URL
    	HttpEntity entity;//contains the response Entity
    	InputStream instream;//used to retrieve the content of the response entity
    	JSONArray jsonArray = null;//contains the response content in JSONArray format
    	String status="";
    	
    	db = arg0[0];
		
    	//call handleGetRequest function to make a GET Request and retrieve the response entity
		entity = new Functions().handleGetRequest( URL);
        
        if(entity!=null){
			try{
				//get the content of the response entity
        		instream = entity.getContent();
        		//call the convertStreamToString method
        		result = new Functions().convertStreamToString(instream);
        		//create a JSONArray with the response content
                jsonArray = new JSONArray(result);
                //close InputStream
                instream.close();
                
                Log.i("RetrieveUser", jsonArray.toString());
        		//call handleUserJSONArrayForRetrieve method
	        	new User().handleUserJSONArrayForRetrieve(jsonArray,db);
	        	//assign ok to status
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
    	//if parameter from doInBackground is ok
    	if(result.equals("ok")){
    		//start Activity MainActivity
    		Intent intent = new Intent(context, MainActivity.class);
    		context.startActivity(intent);

			new RetrieveCategoriesAfterIdTask().execute(db);
            new RetrieveStoresAfterIdTask().execute(db);			
    	    new RetrieveOffersAfterIdTask().execute(db);
            new RetrieveBudgetsAfterIdTask().execute(db);
            new RetrieveProductsAfterIdTask().execute(db);
  			new RetrieveFamilyAfterIdTask().execute(db);
  			
			new RetrieveFamilyUserDataTask().execute(db);
			new RetrieveFamilyProductsTask().execute(db);
    	} else{
    		displayError(this.context.getString(R.string.no_user));
    	}
    }
    
	public void displayError(String message) {
		InputErrorDialogFragment errorDialog = new InputErrorDialogFragment();
		errorDialog.setMessage(message);
		errorDialog.show(this.fragment, "errorDialog");
	}
}