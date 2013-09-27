package com.SR.processes;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.json.JSONArray;
import org.json.JSONException;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.SR.data.Budget;
import com.SR.data.FeedReaderContract.FeedBudget;

public class UpdateBudgetTask extends AsyncTask<SQLiteDatabase, Void, String> {
	
    SQLiteDatabase db;

    @Override
	protected String doInBackground(SQLiteDatabase... arg0) {
    	
    	String URL = "http://10.0.2.2/php/rest/budget.php";
    	String id, startDate, finishDate, category, spentLimit;
		JSONArray json;
		Cursor budgetDetails;
		
		db = arg0[0];
/*				
db.execSQL("UPDATE "+FeedBudget.TABLE_NAME+" SET "+FeedBudget.SPEND_LIMIT+" = 263 WHERE "+FeedBudget._ID+" ='5'" );
db.execSQL("UPDATE "+FeedBudget.TABLE_NAME+" SET "+FeedBudget.FOR_UPDATE+" = '1' WHERE "+FeedBudget._ID+" ='5'" );
*/
		budgetDetails = new Budget().fetchBudgetForUpdate(db);
		
		while(budgetDetails.moveToNext()){
			
			id = budgetDetails.getString(budgetDetails.getColumnIndexOrThrow(FeedBudget._ID));
			startDate = budgetDetails.getString(budgetDetails.getColumnIndexOrThrow(FeedBudget.START_DATE));
			finishDate = budgetDetails.getString(budgetDetails.getColumnIndexOrThrow(FeedBudget.END_DATE));
			category = budgetDetails.getString(budgetDetails.getColumnIndexOrThrow(FeedBudget.EXPENSE_CATEGORY));
			spentLimit = budgetDetails.getString(budgetDetails.getColumnIndexOrThrow(FeedBudget.SPEND_LIMIT));
				
    		try{
    			//android.os.Debug.waitForDebugger();
    			json = new Budget().convertStringDataToJson(id,startDate, finishDate, category,spentLimit);
    		
				StatusLine statusLine = new Functions().handlePutRequest(json, URL);
		       
				Log.i("UpdateBudStatus", statusLine.toString());
    			Log.i("UpdateBudget", json.toString()); 

    	        if(statusLine.getStatusCode() == HttpStatus.SC_OK){
    	        	    	        	
    				db.execSQL("UPDATE "+FeedBudget.TABLE_NAME+" SET "+FeedBudget.FOR_UPDATE+" = '0' WHERE "+FeedBudget._ID+" = '"+id+"'");
    	         } 
    	        
    		} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return "ok";
    }
    
    @Override
	protected void onPostExecute(String result) {

    }
}