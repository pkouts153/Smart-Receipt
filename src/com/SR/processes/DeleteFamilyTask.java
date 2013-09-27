package com.SR.processes;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.SR.data.Family;
import com.SR.data.FeedReaderContract.FeedFamily;

public class DeleteFamilyTask extends AsyncTask<SQLiteDatabase, Void, String> {
	
    @Override
	protected String doInBackground(SQLiteDatabase... arg0) {
    	
		String FamilyId;
		SQLiteDatabase db = arg0[0];
	
//		db.execSQL("UPDATE "+FeedFamily.TABLE_NAME+" SET "+FeedFamily.FOR_DELETION+" = '1' WHERE "+FeedFamily._ID+" ='7'");
				
		Cursor deletedFamilyQuery = new Family().fetchFamilyForDeletion(db);
		
		while (deletedFamilyQuery.moveToNext()){
				
			FamilyId = deletedFamilyQuery.getString(deletedFamilyQuery.getColumnIndexOrThrow(FeedFamily._ID));
		    
			String URL = "http://10.0.2.2/php/rest/family.php";
			URL = URL+"/"+FamilyId;
						
			StatusLine statusLine = new Functions().handleDeleteRequest(URL);
			
	        if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	        		        	
	        	new Family().deleteFamily(FamilyId, db);
	         }
		}
		return "ok";
    }

    @Override
	protected void onPostExecute(String result) {

    }
}