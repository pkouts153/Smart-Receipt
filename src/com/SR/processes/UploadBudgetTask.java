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

public class UploadBudgetTask extends AsyncTask<SQLiteDatabase, Void, String> {
	
    SQLiteDatabase db;

    @Override
	protected String doInBackground(SQLiteDatabase... arg0) {
      
    	String URL = "http://10.0.2.2/php/rest/budget.php";
		String id, startDate, finishDate, category, spentLimit, user, created, forUpdate, forDeletion, result;
		HttpEntity entity;
		InputStream instream;
		JSONArray jsonArray, json;
		Cursor queryResult;
		
		db = arg0[0];
/*		
		db.execSQL("INSERT INTO "+FeedBudget.TABLE_NAME+" ("+FeedBudget._ID+","+FeedBudget.START_DATE+","+FeedBudget.END_DATE+","+
				FeedBudget.EXPENSE_CATEGORY+","+FeedBudget.SPEND_LIMIT+","+FeedBudget.USER+","+
				FeedBudget.BUDGET_CREATED+","+FeedBudget.FOR_UPDATE+","+FeedBudget.FOR_DELETION+","+
				FeedBudget.ON_SERVER+") " +
		"VALUES ('13','2013-09-01','2013-09-30','1','245','4','2013-09-24 00:00:00','0','0','0')");	
*/			
		queryResult = new Budget().fetchLocalBudget(db);
		
		while(queryResult.moveToNext()){
						
			id = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedBudget._ID));
			startDate = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedBudget.START_DATE));
			finishDate = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedBudget.END_DATE));
			category = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedBudget.EXPENSE_CATEGORY));
			spentLimit = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedBudget.SPEND_LIMIT));
			user = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedBudget.USER));
			created = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedBudget.BUDGET_CREATED));
			forUpdate = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedBudget.FOR_UPDATE));
			forDeletion = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedBudget.FOR_DELETION));
			
			try{
    			//android.os.Debug.waitForDebugger();
    			json = new Budget().convertBudgetObjectToJson(id,startDate,finishDate, category, spentLimit, user, created, forUpdate, forDeletion);
    		        			
    			entity = new Functions().handlePostRequest(json, URL);

    	        if(entity!=null){
	
	        		instream = entity.getContent();
	        		result = new Functions().convertStreamToString(instream);
	
	                jsonArray = new JSONArray(result);
	                instream.close();
	                
	                Log.i("UploadBudget", jsonArray.toString());
	                
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
    	return "OK";
    }
    
    @Override
	protected void onPostExecute(String result) {

    }
}