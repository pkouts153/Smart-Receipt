package com.SR.processes;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.SR.data.Budget;
import com.SR.data.FeedReaderContract.FeedBudget;

public class DeleteBudgetTask extends AsyncTask<SQLiteDatabase, Void, String> {
	
    @Override
	protected String doInBackground(SQLiteDatabase... arg0) {
    	
		String BudgetId;
		SQLiteDatabase db = arg0[0];
/*		
		db.execSQL("UPDATE "+FeedBudget.TABLE_NAME+" SET "+FeedBudget.FOR_DELETION+" = '1' " +
				   "WHERE "+FeedBudget._ID+" ='13' and "+FeedBudget.USER+" ="+ User.USER_ID+"");
*/		
		Cursor deletedBudgetsQuery = new Budget().fetchBudgetsForDeletion(db);

		while (deletedBudgetsQuery.moveToNext()){
					
			BudgetId = deletedBudgetsQuery.getString(deletedBudgetsQuery.getColumnIndexOrThrow(FeedBudget._ID));
			
			String URL = "http://10.0.2.2/php/rest/budget.php";		
			URL = URL+"/"+BudgetId;
			
			StatusLine statusLine = new Functions().handleDeleteRequest(URL);
					
	        if(statusLine.getStatusCode() == HttpStatus.SC_OK){
				 		
	        	new Budget().deleteBudget(BudgetId, db);
	         }
		}
		return "ok";
    }
    
    @Override
	protected void onPostExecute(String result) {

    }
}