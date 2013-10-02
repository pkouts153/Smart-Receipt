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

import com.SR.data.Budget;
import com.SR.data.FeedReaderContract.FeedBudget;

/**
 * 
 * @author Ιωάννης Διαμαντίδης 8100039
 * 
 * this AsyncTask is used to call a web service which inserts a budget into server database
 *
 */

public class UploadBudgetTask extends AsyncTask<SQLiteDatabase, Void, Void> {
	
    SQLiteDatabase db;

    @Override
	protected Void doInBackground(SQLiteDatabase... arg0) {
      
    	String URL = "http://10.0.2.2/php/rest/budget.php";//the URL of the web service
		String id, startDate, finishDate, category, spentLimit, user, familyUser, created, forUpdate, forDeletion, result;
    	HttpEntity entity;//contains the response Entity
    	InputStream instream;//used to retrieve the content of the response entity
		JSONArray json, jsonArray;//contain the record that will be sent and the record that will be retrieved with the new id accordingly
		Cursor queryResult;//contains the budgets that are stored locally
		
		db = arg0[0];
/*		
		db.execSQL("INSERT INTO "+FeedBudget.TABLE_NAME+" ("+FeedBudget._ID+","+FeedBudget.START_DATE+","+FeedBudget.END_DATE+","+
				FeedBudget.EXPENSE_CATEGORY+","+FeedBudget.SPEND_LIMIT+","+FeedBudget.USER+","+
				FeedBudget.BUDGET_CREATED+","+FeedBudget.FOR_UPDATE+","+FeedBudget.FOR_DELETION+","+
				FeedBudget.ON_SERVER+") " +
		"VALUES ('13','2013-09-01','2013-09-30','1','245','4','2013-09-24 00:00:00','0','0','0')");	
*/		
		//call fetchLocalBudget method to retrieve the budgets that are stored locally
		queryResult = new Budget().fetchLocalBudget(db);
		//as long as there are budgets to be uploaded
		while(queryResult.moveToNext()){
			//get the details of the budget		
			id = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedBudget._ID));
			startDate = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedBudget.START_DATE));
			finishDate = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedBudget.END_DATE));
			category = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedBudget.EXPENSE_CATEGORY));
			spentLimit = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedBudget.SPEND_LIMIT));
			user = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedBudget.USER));
			familyUser = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedBudget.FAMILY_USER));
			created = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedBudget.BUDGET_CREATED));
			forUpdate = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedBudget.FOR_UPDATE));
			forDeletion = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedBudget.FOR_DELETION));
			
			try{
    			//convert string values to JSONArray
    			json = new Budget().convertBudgetObjectToJson(id,startDate,finishDate, category, spentLimit, user, familyUser, created, forUpdate, forDeletion);
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
	                
	                Log.i("UploadBudget", jsonArray.toString());
	        		//call handleBudgetJSONArrayForUpload method
	                new Budget().handleBudgetJSONArrayForUpload(jsonArray, db);
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