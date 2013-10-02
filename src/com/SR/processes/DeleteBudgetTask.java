package com.SR.processes;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.SR.data.Budget;
import com.SR.data.FeedReaderContract.FeedBudget;

/**
 * 
 * @author Ιωάννης Διαμαντίδης 8100039
 * 
 * This AsyncTask is used to call a web service which deletes a budget
 *
 */

public class DeleteBudgetTask extends AsyncTask<SQLiteDatabase, Void, Void> {
	
    @Override
	protected Void doInBackground(SQLiteDatabase... arg0) {
    	
		String BudgetId;//contains the id of the budget which will be deleted
		SQLiteDatabase db = arg0[0];
		
//		db.execSQL("UPDATE "+FeedBudget.TABLE_NAME+" SET "+FeedBudget.FOR_DELETION+" = '1' WHERE "+FeedBudget._ID+" ='13'");
		
		//call fetchBudgetsForDeletion method to retrieve the budgets that will be deleted
		Cursor deletedBudgetsQuery = new Budget().fetchBudgetsForDeletion(db);
		//as long as there are budgets to be deleted
		while (deletedBudgetsQuery.moveToNext()){
			//get the id of the budget		
			BudgetId = deletedBudgetsQuery.getString(deletedBudgetsQuery.getColumnIndexOrThrow(FeedBudget._ID));
			//the URL of the web service
			String URL = "http://10.0.2.2/php/rest/budget.php";
			//add the id to the URL
			URL = URL+"/"+BudgetId;
			//call handleDeleteRequest method to make a DELETE request and get the response status
			StatusLine statusLine = new Functions().handleDeleteRequest(URL);
			//if status is Ok, or code is 200
	        if(statusLine.getStatusCode() == HttpStatus.SC_OK){
				//call deleteBudget function		
	        	new Budget().deleteBudget(BudgetId, db);
	         }
		}
		return null;
    }
}