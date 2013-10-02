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

/**
 * 
 * @author Ιωάννης Διαμαντίδης 8100039
 * 
 * this AsyncTask is used to call a web service which updates a budget
 *
 */

public class UpdateBudgetTask extends AsyncTask<SQLiteDatabase, Void, Void> {
	
    SQLiteDatabase db;

    @Override
	protected Void doInBackground(SQLiteDatabase... arg0) {
    	
    	String URL = "http://10.0.2.2/php/rest/budget.php";//the URL of the web service
    	String id, startDate, finishDate, category, spentLimit;
		JSONArray json;
		Cursor budgetDetails;
		
		db = arg0[0];
/*				
		db.execSQL("UPDATE "+FeedBudget.TABLE_NAME+" SET "+FeedBudget.SPEND_LIMIT+" = 263 WHERE "+FeedBudget._ID+" ='5'" );
		db.execSQL("UPDATE "+FeedBudget.TABLE_NAME+" SET "+FeedBudget.FOR_UPDATE+" = '1' WHERE "+FeedBudget._ID+" ='5'" );
*/
		//call fetchBudgetForUpdate method to retrieve the budgets that will be updated
		budgetDetails = new Budget().fetchBudgetForUpdate(db);
		//as long as there are budgets to be updated
		while(budgetDetails.moveToNext()){
			//get the details of the budget		
			id = budgetDetails.getString(budgetDetails.getColumnIndexOrThrow(FeedBudget._ID));
			startDate = budgetDetails.getString(budgetDetails.getColumnIndexOrThrow(FeedBudget.START_DATE));
			finishDate = budgetDetails.getString(budgetDetails.getColumnIndexOrThrow(FeedBudget.END_DATE));
			category = budgetDetails.getString(budgetDetails.getColumnIndexOrThrow(FeedBudget.EXPENSE_CATEGORY));
			spentLimit = budgetDetails.getString(budgetDetails.getColumnIndexOrThrow(FeedBudget.SPEND_LIMIT));
				
    		try{
    			//convert string values to JSONArray
    			json = new Budget().convertStringDataToJson(id,startDate, finishDate, category,spentLimit);
    			//call handlePutRequest method to make a PUT request and get the response status
				StatusLine statusLine = new Functions().handlePutRequest(json, URL);
		       
				Log.i("UpdateBudStatus", statusLine.toString());
    			Log.i("UpdateBudget", json.toString()); 
    			//if status is Ok, or code is 200
    	        if(statusLine.getStatusCode() == HttpStatus.SC_OK){
    	    		//set this record to not for Update
    				db.execSQL("UPDATE "+FeedBudget.TABLE_NAME+" SET "+FeedBudget.FOR_UPDATE+" = '0' WHERE "+FeedBudget._ID+" = '"+id+"'");
    	         } 
    	        
    		} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
    }
}