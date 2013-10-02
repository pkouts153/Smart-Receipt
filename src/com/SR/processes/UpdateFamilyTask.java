package com.SR.processes;

import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.json.JSONArray;
import org.json.JSONException;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.SR.data.Family;
import com.SR.data.FeedReaderContract.FeedFamily;

/**
 * 
 * @author Ιωάννης Διαμαντίδης 8100039
 * 
 * this AsyncTask is used to call a web service which updates a family
 *
 */

public class UpdateFamilyTask extends AsyncTask<SQLiteDatabase, Void, Void> {
	
    SQLiteDatabase db;

    @Override
	protected Void doInBackground(SQLiteDatabase... arg0) {
    	
    	String URL = "http://10.0.2.2/php/rest/family.php";
    	String id, member1, member2, confirmed, created, forDeletion, forUpdate;
		JSONArray json;
		Cursor familyDetails;
		
		db = arg0[0];
/*
		db.execSQL("UPDATE "+FeedFamily.TABLE_NAME+" SET "+FeedFamily.CONFIRMED+" = '1' WHERE "+FeedFamily._ID+" ='5'" );
		db.execSQL("UPDATE "+FeedFamily.TABLE_NAME+" SET "+FeedFamily.FOR_UPDATE+" = '1' WHERE "+FeedFamily._ID+" ='5'" );
*/	
		//call fetchFamilyForUpdate method to retrieve the families that will be updated
		familyDetails = new Family().fetchFamilyForUpdate(db);
		//as long as there are families to be updated
		while(familyDetails.moveToNext()){
			//get the details of the family		
			id = familyDetails.getString(familyDetails.getColumnIndexOrThrow(FeedFamily._ID));
			member1 = familyDetails.getString(familyDetails.getColumnIndexOrThrow(FeedFamily.MEMBER1));
			member2 = familyDetails.getString(familyDetails.getColumnIndexOrThrow(FeedFamily.MEMBER2));
			confirmed = familyDetails.getString(familyDetails.getColumnIndexOrThrow(FeedFamily.CONFIRMED));
			created = familyDetails.getString(familyDetails.getColumnIndexOrThrow(FeedFamily.FAMILY_CREATED));
			forDeletion = familyDetails.getString(familyDetails.getColumnIndexOrThrow(FeedFamily.FOR_DELETION));	
			forUpdate = familyDetails.getString(familyDetails.getColumnIndexOrThrow(FeedFamily.FOR_UPDATE));	
				
    		try{
    			//convert string values to JSONArray
    			json = new Family().convertStringToJson(id, member1, member2, confirmed, created, forDeletion, forUpdate);
    			//call handlePutRequest method to make a PUT request and get the response status
				StatusLine statusLine = new Functions().handlePutRequest(json, URL);
		       
				Log.i("UpdateFamStatus", statusLine.toString());
    			Log.i("UpdateFamily", json.toString()); 
    			//if status is Ok, or code is 200
    	        if(statusLine.getStatusCode() == HttpStatus.SC_OK){
    	    		//set this record to not for Update
    				db.execSQL("UPDATE "+FeedFamily.TABLE_NAME+" SET "+FeedFamily.FOR_UPDATE+" = '0' WHERE "+FeedFamily._ID+" = '"+id+"'");
    	        } 
    	        
    		} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
    }
}