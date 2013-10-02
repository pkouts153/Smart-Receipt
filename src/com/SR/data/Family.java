package com.SR.data;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.SR.data.FeedReaderContract.FeedFamily;
import com.SR.data.FeedReaderContract.FeedProduct;
import com.SR.data.FeedReaderContract.FeedUser;

/**
 * 
 * @author Ιωάννης Διαμαντίδης 8100039
 *
 */

public class Family {

	/*this method returns maximum value of id in the family table for the family records of this user.
	 * The records created from an other user of this phone are excluded
	 */
	public String fetchFamilyMaxId(SQLiteDatabase db){

		String[] param ={"1"};
		Cursor result = db.rawQuery("SELECT CASE" +
												" WHEN max("+FeedFamily._ID+") IS NULL THEN 0" +
											    " ELSE max("+FeedFamily._ID+")" +
										  " END AS max FROM "+FeedFamily.TABLE_NAME+" " +
									"WHERE "+FeedFamily.ON_SERVER+"=? and " +
										"(("+FeedFamily.MEMBER1 +"= ? and "+FeedFamily.MEMBER2+" NOT IN (SELECT "+FeedUser._ID+" FROM "+FeedUser.TABLE_NAME+"" +
																										" WHERE "+FeedUser.PASSWORD+" IS NOT NULL)) or " +
										"("+FeedFamily.MEMBER2 +"= ? and "+FeedFamily.MEMBER1+" NOT IN (SELECT "+FeedUser._ID+" FROM "+FeedUser.TABLE_NAME+"" +
																										" WHERE "+FeedUser.PASSWORD+" IS NOT NULL)))", param);
		result.moveToNext();
		String lastId = result.getString(result.getColumnIndexOrThrow("max"));
		
		return lastId;
	}	
	
	//this method gets family details and insert them to family table
	private void insertFamily(String id, String member1, String member2, String confirmed, String created,
															String forDeletion, String forUpdate, String onServer, SQLiteDatabase db){
		 
		db.execSQL("INSERT INTO "+FeedFamily.TABLE_NAME+" ("+FeedFamily._ID+", "+FeedFamily.MEMBER1+", "+FeedFamily.MEMBER2+", "
															+FeedFamily.CONFIRMED+", "+FeedFamily.FAMILY_CREATED+", "
															+FeedFamily.FOR_DELETION+", "+FeedFamily.FOR_UPDATE+", "+FeedFamily.ON_SERVER+") " +
			    "VALUES ("+id+",'"+member1+"','"+member2+"','"+confirmed+"','"+created+"','"+forDeletion+"','"+forUpdate+"', '"+onServer+"')");
	}
	
	/*this method gets an id as a parameter and deletes the record with this id.
	 * At first, it searches if there is a record with this id.
	 * If so, check if this record is on Server database. If not, insert this record using the first available id.
	 * Then , no matter if the record was on Server or not, delete the entry.
	 */
	private void makeIdAvailable(String id, SQLiteDatabase db){
		
		Cursor result = fetchFamilyById(id, db);
		if(result.moveToNext()){

			String prevMember1 = result.getString(result.getColumnIndexOrThrow(FeedFamily.MEMBER1));
			String prevMember2 = result.getString(result.getColumnIndexOrThrow(FeedFamily.MEMBER2));
			String prevConfirmed = result.getString(result.getColumnIndexOrThrow(FeedFamily.CONFIRMED));
			String prevCreated = result.getString(result.getColumnIndexOrThrow(FeedFamily.FAMILY_CREATED));
			String prevForDeletion = result.getString(result.getColumnIndexOrThrow(FeedFamily.FOR_DELETION));				
			String prevForUpdate = result.getString(result.getColumnIndexOrThrow(FeedFamily.FOR_UPDATE));				
			String prevOnServer = result.getString(result.getColumnIndexOrThrow(FeedFamily.ON_SERVER));
			
			if("0".equals(prevOnServer))
				insertFamily(null, prevMember1, prevMember2, prevConfirmed, prevCreated, prevForDeletion, prevForUpdate, prevOnServer, db);
			
			db.execSQL("DELETE FROM "+FeedFamily.TABLE_NAME+" WHERE "+FeedFamily._ID+" = '"+id+"'");
		}
	}
	
	//this method gets an id as parameter and returns the record with this id.
	private Cursor fetchFamilyById(String id, SQLiteDatabase db){
		String[] params = {id};
		Cursor result = db.rawQuery("SELECT * FROM "+FeedFamily.TABLE_NAME+" WHERE "+FeedFamily._ID+"=?", params);
		return result;
	}
	
	//this method gets as parameter an id and deletes the record with this id.
	public void deleteFamily(String id, SQLiteDatabase db) {
		
		db.execSQL("DELETE FROM "+FeedFamily.TABLE_NAME+" WHERE "+FeedFamily._ID+" = '"+id+"' and "+FeedFamily.FOR_DELETION+" = 1 ");
		
	}
	
	//this method returns the records from family table that are for Deletion.
	public Cursor fetchFamilyForDeletion(SQLiteDatabase db){
		
		String[] params = {String.valueOf(User.USER_ID), String.valueOf(User.USER_ID)};	
		Cursor result = db.rawQuery("SELECT "+FeedFamily._ID+" FROM "+FeedFamily.TABLE_NAME+
						" WHERE "+FeedFamily.FOR_DELETION+" = '1' and ("+FeedFamily.MEMBER1+"=? or "+FeedFamily.MEMBER2+"=? )", params);
		return result;
	}
	
	/*this method returns the records of this user from family table, that are stored only in the local database
	 * the other member of the family(not the current user of the app), must have his details downloaded. 
	 * Otherwise, we could not be sure if his id is the same as in the server database.
	 */
	public Cursor fetchLocalFamily(SQLiteDatabase db){
		
		String[] params = {"0", String.valueOf(User.USER_ID), "1", String.valueOf(User.USER_ID), "1"};	
		Cursor result = db.rawQuery("SELECT * FROM "+FeedFamily.TABLE_NAME+
							" WHERE "+FeedFamily.ON_SERVER+"=? and " +
							"(("+FeedFamily.MEMBER1+" =? and "+FeedFamily.MEMBER2+" IN (SELECT "+FeedUser._ID+" " +
																						"FROM "+FeedUser.TABLE_NAME+
																						" WHERE "+FeedUser.FROM_SERVER+"=?)) or " +
							"("+FeedFamily.MEMBER2+" =? and "+FeedFamily.MEMBER1+" IN (SELECT "+FeedUser._ID+" " +
																						"FROM "+FeedUser.TABLE_NAME+
																						" WHERE "+FeedUser.FROM_SERVER+"=?)) )", params);
		return result;
	}
	
    /*this method updates the id column of a specific entry in the family table. It is used because when a family is created 
     * on this phone and it is uploaded to the server database.The id that was locally saved would be different compared to the
     * id used in the server database. To ensure that these two ids will be the same, the family record at local database must be updated 
     */
	private void updateFamily(String id, String member1, String member2, String confirmed, String created, 
																			String forDeletion, String forUpdate, SQLiteDatabase db){
		db.execSQL("UPDATE "+FeedFamily.TABLE_NAME+" SET "+FeedFamily._ID+" = '"+id+"' " +
					"WHERE "+FeedFamily.MEMBER1+" = '"+member1+"' and "+FeedFamily.MEMBER2+" = '"+member2+"' and " +
				 	""+FeedFamily.CONFIRMED+" = '"+confirmed+"' and "+FeedFamily.FAMILY_CREATED+" = '"+created+"' and "+
					""+FeedFamily.FOR_DELETION+" = '"+forDeletion+"' and "+FeedFamily.FOR_UPDATE+" = '"+forUpdate+"'" );
			
		db.execSQL("UPDATE "+FeedFamily.TABLE_NAME+" SET "+FeedFamily.ON_SERVER+" = '1' WHERE "+FeedFamily._ID+" ='"+id+"'" );
	}	
	
	//this method gets an id and updates the record from family table with this id so as to be considered deleted
	private void updateFamilyForDeletion(String id, SQLiteDatabase db) {
		
		db.execSQL("UPDATE "+FeedFamily.TABLE_NAME+" SET "+FeedFamily.FOR_DELETION+" = '1' WHERE "+FeedFamily._ID+" ='"+id+"'");
	}
	
	//this method gets as parameters the details of a family and converts them into json format
    public JSONArray convertStringToJson(String id, String member1, String member2, String confirmed,
    													String created, String forDeletion, String forUpdate)  throws JSONException {
		Map<String,String> family = new HashMap<String,String>();
		family.put("id", id);
		family.put("member1", member1);
		family.put("member2", member2);
		family.put("confirmed", confirmed);
		family.put("created", created);
		family.put("forDeletion", forDeletion);
		family.put("forUpdate", forUpdate);

		JSONObject temp = new JSONObject(family);
		JSONArray json = new JSONArray();
		
		json.put(0, temp);		
		
		return json;
	}
	
	/*this method handles the jsonArray retrieved from a GET request. At first, it converts the values of this jsonArray
	 * into String values, then it calls makeIdAvailable function and finally it inserts the values into family table.
	 */
    public void handleFamilyJSONArrayForRetrieve(JSONArray json, SQLiteDatabase db) throws JSONException{
		
		for(int i=0; i<json.length(); i++){
			
			JSONObject json_data =json.getJSONObject(i);
	
			String id = json_data.get("id").toString();
			String member1 = json_data.get("member1").toString();
			String member2 = json_data.get("member2").toString();
			String confirmed = json_data.get("confirmed").toString();
			String created = json_data.get("created").toString();
			String forDeletion = json_data.get("forDeletion").toString();
			String forUpdate = json_data.get("forUpdate").toString();

			makeIdAvailable(id, db);
			
			String onServer = "1";
			insertFamily(id, member1, member2, confirmed, created, forDeletion, forUpdate, onServer, db);
		}	
	}
	
    /*this method handles the jsonArray retrieved from the response of a POST request. At first, it converts the values of this jsonArray
	 * into String values, then it calls makeIdAvailable function and finally it calls updateFamily method.
	 */   
    public void handleFamilyJSONArrayForUpload(JSONArray json, SQLiteDatabase db) throws JSONException{
		
		JSONObject json_data = json.getJSONObject(0);

		String id = json_data.get("id").toString();
		String member1 = json_data.get("member1").toString();
		String member2 = json_data.get("member2").toString();
		String confirmed = json_data.get("confirmed").toString();
		String created = json_data.get("created").toString();
		String forDeletion = json_data.get("forDeletion").toString();
		String forUpdate = json_data.get("forUpdate").toString();

		makeIdAvailable(id, db);
		
		updateFamily(id, member1, member2, confirmed, created, forDeletion, forUpdate, db);
	}

	/*this method handles the jsonArray retrieved from a GET request.It converts the id of this jsonArray
	 * into String values, and calls updateFamilyForDeletion(String, SQLiteDatabase) method
	 */
    public void handleFamilyJSONArrayForDeletion(JSONArray json, SQLiteDatabase db) throws JSONException{
		
		for(int i=0; i<json.length(); i++){
			
			JSONObject json_data = json.getJSONObject(i);

			String id = json_data.get("id").toString();
			
			updateFamilyForDeletion(id, db);
		}	
	}
	
    //this method returns the records from family table that are updated
	public Cursor fetchFamilyForUpdate(SQLiteDatabase db){
		
		String[] params = {"1", "1", String.valueOf(User.USER_ID), String.valueOf(User.USER_ID)};
		Cursor result = db.rawQuery("SELECT * FROM "+FeedFamily.TABLE_NAME+" " +
									"WHERE "+FeedFamily.FOR_UPDATE+" = ? and "+FeedFamily.ON_SERVER+" = ? and "+
									"("+FeedFamily.MEMBER1+"=? or "+FeedFamily.MEMBER2+"=? )", params);
		return result;
	}
	
	/*this method handles the jsonArray retrieved from a GET request. At first, it converts the values of this jsonArray
	 * into String values and then it gets the record with the same id from database. After that it compares the values of this
	 *  two records, and when these values are different, it updates the family table with the value from jsonArray. 
	 *  Finally, it sets forUpdate column of this record into 1, so as to be consider for update.
	 */
	public void handleFamilyJSONArrayForUpdate(JSONArray json, SQLiteDatabase db) throws JSONException{
		
		for(int i=0; i<json.length(); i++){
			
			JSONObject json_data = json.getJSONObject(i);
		
			String id = json_data.get("id").toString();
			String confirmed = json_data.get("confirmed").toString();			
			
			Cursor queryResult = fetchFamilyById(id, db);
			
			queryResult.moveToFirst();
			String prevConfirmed = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedFamily.CONFIRMED));

			
			if(!(prevConfirmed.equals(confirmed)))
				db.execSQL("UPDATE "+FeedFamily.TABLE_NAME+" SET "+FeedFamily.CONFIRMED+"='"+confirmed+"' WHERE "+FeedFamily._ID+"='"+id+"'");
			
			db.execSQL("UPDATE "+FeedFamily.TABLE_NAME+" SET "+FeedFamily.FOR_UPDATE+"=1 WHERE "+FeedFamily._ID+"='"+id+"'");
		}	
	}
	
	/*this method returns the records of this user from family table, that the family members do not have their details
	 *  downloaded from server. These family members include those who made a family request to the current user and his has not accept 
	 *  the request yet, and those, whose family request is confirmed
	 */
	public Cursor fetchFamilyMembersNotInDB(SQLiteDatabase db){

		String[] params = {String.valueOf(User.USER_ID), "1", "1", String.valueOf(User.USER_ID), "1"};
		Cursor result = db.rawQuery("SELECT * FROM "+FeedFamily.TABLE_NAME+" " +
									"WHERE (("+FeedFamily.MEMBER1+" =? and "+FeedFamily.CONFIRMED+" = ? " +
											"and "+FeedFamily.MEMBER2+" NOT IN (SELECT "+FeedUser._ID+" FROM "+FeedUser.TABLE_NAME+
																				" WHERE "+FeedUser.FROM_SERVER+"=?))" +
								" or ("+FeedFamily.MEMBER2+" =? and "+FeedFamily.MEMBER1+" NOT IN (SELECT "+FeedUser._ID+
																							" FROM "+FeedUser.TABLE_NAME+
																						" WHERE "+FeedUser.FROM_SERVER+"=?)) )", params);
		return result;
	}
	
	/*this method returns the id of the family members without records in the product table. 
	 * These family members must have their details stored in user table. 
	 */
	public Cursor fetchUsersWithoutProductsInDB(SQLiteDatabase db){
	
		String[] param ={"1", String.valueOf(User.USER_ID), "1", "0", String.valueOf(User.USER_ID), "0"};
		Cursor result = db.rawQuery("SELECT "+FeedUser._ID+" FROM "+FeedUser.TABLE_NAME+" WHERE ("+FeedUser._ID+" IN (SELECT "+FeedUser._ID+" FROM "+FeedUser.TABLE_NAME+" WHERE "+FeedUser.FROM_SERVER+"=?)) and" +
																				   " (("+FeedUser._ID+" IN (SELECT "+FeedFamily.MEMBER2+" FROM "+FeedFamily.TABLE_NAME+" WHERE "+FeedFamily.MEMBER1+"=?  AND " + FeedFamily.CONFIRMED + "=? AND " + FeedFamily.FOR_DELETION + "=?)) or"+
																					" ("+FeedUser._ID+" IN (SELECT "+FeedFamily.MEMBER1+" FROM "+FeedFamily.TABLE_NAME+" WHERE "+FeedFamily.MEMBER2+"=?  AND " + FeedFamily.FOR_DELETION + "=?))) and"+
																					" ("+FeedUser._ID+" NOT IN (SELECT "+FeedProduct.USER+" FROM "+FeedProduct.TABLE_NAME+")) ", param);
		return result;
	}	
	
	/*this method is used to insert a new family record using as parameter the email of member2.
	 * At first, it calculates the current date and time. Then, it searches if there is a record in the local database with this
	 * email. If there is not, it inserts a new record to user table with only email and created.
	 * Then it retrieves the id of the record of the user with this email, and it assign the proper value to each variable
	 * of a family record. Finally, it inserts these variable to family table.
	 */
	public void insertFamilyFromActivity(String member2Email, SQLiteDatabase db){
		
		Date date = new Date();
		Timestamp timestampToday = new Timestamp(date.getTime());
		String created = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestampToday);
				
		String[] params = {member2Email};
		Cursor result = db.rawQuery("SELECT count("+FeedUser._ID +") AS count FROM "+FeedUser.TABLE_NAME+" WHERE "+FeedUser.EMAIL+"=?", params);
		if(result.moveToNext()){
			if( result.getInt(result.getColumnIndexOrThrow("count")) == 0){
								
				db.execSQL("INSERT INTO "+FeedUser.TABLE_NAME+" ("+FeedUser._ID+", "+FeedUser.USERNAME+", "+FeedUser.PASSWORD+", "+FeedUser.EMAIL+", "+FeedUser.USER_CREATED+", "+FeedUser.FOR_UPDATE+", "+FeedUser.FROM_SERVER+") " +
						   "VALUES (NULL,NULL,NULL,'"+member2Email+"','"+created+"','0','0')");
			}
		}
		Cursor userIdResult = db.rawQuery("SELECT "+FeedUser._ID +" FROM "+FeedUser.TABLE_NAME+" WHERE "+FeedUser.EMAIL+" = ?", params);
		
		String member1 = String.valueOf(User.USER_ID);
		
		userIdResult.moveToNext();
		String member2 = userIdResult.getString(userIdResult.getColumnIndexOrThrow(FeedUser._ID ));
		
		String confirmed = "0";
		String forDeletion = "0";
		String forUpdate = "0";
		String onServer = "0";
				
		db.execSQL("INSERT INTO "+FeedFamily.TABLE_NAME+" ("+FeedFamily._ID+", "+FeedFamily.MEMBER1+", "+FeedFamily.MEMBER2+", "
				+FeedFamily.CONFIRMED+", "+FeedFamily.FAMILY_CREATED+", "
				+FeedFamily.FOR_DELETION+", "+FeedFamily.FOR_UPDATE+", "+FeedFamily.ON_SERVER+") " +
				"VALUES (NULL,'"+member1+"','"+member2+"','"+confirmed+"','"+created+"','"+forDeletion+"', '"+forUpdate+"', '"+onServer+"')");
		
	}
}