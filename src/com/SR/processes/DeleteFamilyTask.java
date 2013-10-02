package com.SR.processes;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.SR.data.Family;
import com.SR.data.FeedReaderContract.FeedFamily;

/**
 * 
 * @author Ιωάννης Διαμαντίδης 8100039
 * 
 * This AsyncTask is used to call a web service which deletes a budget
 *
 */

public class DeleteFamilyTask extends AsyncTask<SQLiteDatabase, Void, Void> {
	
    @Override
	protected Void doInBackground(SQLiteDatabase... arg0) {
    	
		String FamilyId;//contains the id of the family which will be deleted
		SQLiteDatabase db = arg0[0];

//		db.execSQL("UPDATE "+FeedFamily.TABLE_NAME+" SET "+FeedFamily.FOR_DELETION+" = '1' WHERE "+FeedFamily._ID+" ='8'");
			
		//call fetchFamilyForDeletion method to retrieve the families that will be deleted
		Cursor deletedFamilyQuery = new Family().fetchFamilyForDeletion(db);
		//as long as there are families to be deleted
		while (deletedFamilyQuery.moveToNext()){
			//get the id of the family		
			FamilyId = deletedFamilyQuery.getString(deletedFamilyQuery.getColumnIndexOrThrow(FeedFamily._ID));
			//the URL of the web service
			String URL = "http://10.0.2.2/php/rest/family.php";
			//add the id to the URL
			URL = URL+"/"+FamilyId;
			//call handleDeleteRequest method to make a DELETE request and get the response status
			StatusLine statusLine = new Functions().handleDeleteRequest(URL);
			//if status is Ok, or code is 200
	        if(statusLine.getStatusCode() == HttpStatus.SC_OK){
				//call deleteFamily function		
	        	new Family().deleteFamily(FamilyId, db);
	         }
		}
		return null;
    }
}