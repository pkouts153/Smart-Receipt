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

import com.SR.data.Family;
import com.SR.data.User;
import com.SR.data.FeedReaderContract.FeedFamily;

/**
 * 
 * @author Ιωάννης Διαμαντίδης 8100039
 * 
 * this AsyncTask is used to retrieve user records for a family member from the server database.
 *  This record will be found by the id
 *
 */

public class RetrieveFamilyUserDataTask extends AsyncTask<SQLiteDatabase, Void, Void> {
    
	SQLiteDatabase db;
	
    @Override
	protected Void doInBackground(SQLiteDatabase... arg0) {
     	
    	String result, member1, member2, FamilyMemberId;
    	HttpEntity entity;//contains the response Entity
    	InputStream instream;//used to retrieve the content of the response entity
    	JSONArray jsonArray = null;//contains the response content in JSONArray format

    	db = arg0[0];
    	
    	//call fetchFamilyMembersNotInDB to get user data of family members
    	Cursor queryResult = new Family().fetchFamilyMembersNotInDB(db);
		
    	//as long as there are family members
    	while(queryResult.moveToNext()){
			
    		//get the details of the family		
			member1 = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedFamily.MEMBER1));
			member2 = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedFamily.MEMBER2));
			
			//if member1 is the current user of the app, member2 is the user without data and visa versa
			if(String.valueOf(User.USER_ID).equals(member1))
				FamilyMemberId = member2;
			else
				FamilyMemberId = member1;
			
			//the web service URL
			String URL = "http://10.0.2.2/php/rest/user.php";
			//add the FamilyMemberId to the URL
			URL = URL + "/" +FamilyMemberId;

			//call handleGetRequest function to make a GET Request and retrieve the response entity
			entity = new Functions().handleGetRequest(URL);
	        
	        if(entity!=null){
				try{
					//get he content of the response entity
	        		instream = entity.getContent();
	        		//call the convertStreamToString method
	        		result = new Functions().convertStreamToString(instream);
	        		//create a JSONArray with the response content
	                jsonArray = new JSONArray(result);
	                //close InputStream
	                instream.close();
	                
	                Log.i("RetrieveFamilyUser", jsonArray.toString());
	        		//call handleNewFamilyMemberJSONArray method
		        	new User().handleNewFamilyMemberJSONArray(jsonArray,db);
		        		        	
				} catch (ClientProtocolException e) {
			        e.printStackTrace();
			    } catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
    	}
    	return null;
	}	
}