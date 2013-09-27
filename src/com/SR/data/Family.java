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
import com.SR.data.FeedReaderContract.FeedUser;

public class Family {

	public String fetchFamilyMaxId(SQLiteDatabase db){

		String[] param ={"1"};
		Cursor result = db.rawQuery("SELECT max("+FeedFamily._ID+") AS max FROM "+FeedFamily.TABLE_NAME+" WHERE "+FeedFamily.ON_SERVER+"=?", param);
		result.moveToNext();
		String lastId = result.getString(result.getColumnIndexOrThrow("max"));
		
		return lastId;
	}	
	
	private void insertFamily(String id, String member1, String member2, String confirmed, String created,
																						String forDeletion, SQLiteDatabase db){
		 
		db.execSQL("INSERT INTO "+FeedFamily.TABLE_NAME+" ("+FeedFamily._ID+", "+FeedFamily.MEMBER1+", "+FeedFamily.MEMBER2+", "
															+FeedFamily.CONFIRMED+", "+FeedFamily.FAMILY_CREATED+", "
															+FeedFamily.FOR_DELETION+", "+FeedFamily.ON_SERVER+") " +
			    "VALUES ('"+id+"','"+member1+"','"+member2+"','"+confirmed+"','"+created+"','"+forDeletion+"', '1')");
	}
	
	private void makeIdAvailable(String id, SQLiteDatabase db){
		
		Cursor result = fetchFamilyById(id, db);
		if(result.moveToNext()){

			String prevMember1 = result.getString(result.getColumnIndexOrThrow(FeedFamily.MEMBER1));
			String prevMember2 = result.getString(result.getColumnIndexOrThrow(FeedFamily.MEMBER2));
			String prevConfirmed = result.getString(result.getColumnIndexOrThrow(FeedFamily.CONFIRMED));
			String prevCreated = result.getString(result.getColumnIndexOrThrow(FeedFamily.FAMILY_CREATED));
			String prevForDeletion = result.getString(result.getColumnIndexOrThrow(FeedFamily.FOR_DELETION));				
			String prevOnServer = result.getString(result.getColumnIndexOrThrow(FeedFamily.ON_SERVER));
		
			reInsertFamily(id, prevMember1, prevMember2, prevConfirmed, prevCreated, prevForDeletion, prevOnServer, db);
		}
	}
	
	private Cursor fetchFamilyById(String id, SQLiteDatabase db){
		String[] params = {id, String.valueOf(User.USER_ID), String.valueOf(User.USER_ID)};
		Cursor result = db.rawQuery("SELECT * FROM "+FeedFamily.TABLE_NAME+
								" WHERE "+FeedFamily._ID+"=? and ("+FeedFamily.MEMBER1+" = ? or "+FeedFamily.MEMBER2+" = ?)", params);
		return result;
	}
	
	private void reInsertFamily(String id, String prevMember1, String prevMember2, String prevConfirmed, String prevCreated,
																	String prevForDeletion, String prevOnServer, SQLiteDatabase db){
		
		db.execSQL("INSERT INTO "+FeedFamily.TABLE_NAME+" ("+FeedFamily.MEMBER1+", "+FeedFamily.MEMBER2+", "+FeedFamily.CONFIRMED+", "
											+FeedFamily.FAMILY_CREATED+", "+FeedFamily.FOR_DELETION+", "+FeedFamily.ON_SERVER+") " +
			    "VALUES ('"+prevMember1+"','"+prevMember2+"','"+prevConfirmed+"','"+prevCreated+"','"+prevForDeletion+"'," +
			    		"'"+prevOnServer+"')");				
	
		db.execSQL("DELETE FROM "+FeedFamily.TABLE_NAME+" WHERE "+FeedFamily._ID+" = '"+id+"'");
	}
	
	public void deleteFamily(String id, SQLiteDatabase db) {
		
		db.execSQL("DELETE FROM "+FeedFamily.TABLE_NAME+" WHERE "+FeedFamily._ID+" = '"+id+"' and "+FeedFamily.FOR_DELETION+" = 1 ");
		
	}
	
	public Cursor fetchFamilyForDeletion(SQLiteDatabase db){
		
		String[] params = {String.valueOf(User.USER_ID), String.valueOf(User.USER_ID)};	
		Cursor result = db.rawQuery("SELECT "+FeedFamily._ID+" FROM "+FeedFamily.TABLE_NAME+
						" WHERE "+FeedFamily.FOR_DELETION+" = '1' and ("+FeedFamily.MEMBER1+"=? or "+FeedFamily.MEMBER2+"=? )", params);
		return result;
	}
	
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
	
	private void updateFamily(String id, String member1, String member2, String confirmed, String created, 
																				String forDeletion, SQLiteDatabase db){
		db.execSQL("UPDATE "+FeedFamily.TABLE_NAME+" SET "+FeedFamily._ID+" = '"+id+"' " +
					"WHERE "+FeedFamily.MEMBER1+" = '"+member1+"' and "+FeedFamily.MEMBER2+" = '"+member2+"' and " +
				 	""+FeedFamily.CONFIRMED+" = '"+confirmed+"' and "+FeedFamily.FAMILY_CREATED+" = '"+created+"' and "+
					""+FeedFamily.FOR_DELETION+" = '"+forDeletion+"'" );
			
		db.execSQL("UPDATE "+FeedFamily.TABLE_NAME+" SET "+FeedFamily.ON_SERVER+" = '1' WHERE "+FeedFamily._ID+" ='"+id+"'" );
	}	
	
	private void updateFamilyForDeletion(String id, SQLiteDatabase db) {
		
		db.execSQL("UPDATE "+FeedFamily.TABLE_NAME+" SET "+FeedFamily.FOR_DELETION+" = '1' WHERE "+FeedFamily._ID+" ='"+id+"'");
	}
	
    public JSONArray convertStringToJson(String id, String member1, String member2, String confirmed,
    																		String created, String forDeletion)  throws JSONException {
		Map<String,String> family = new HashMap<String,String>();
		family.put("id", id);
		family.put("member1", member1);
		family.put("member2", member2);
		family.put("confirmed", confirmed);
		family.put("created", created);
		family.put("forDeletion", forDeletion);
		
		JSONObject temp = new JSONObject(family);
		JSONArray json = new JSONArray();
		
		json.put(0, temp);		
		
		return json;
	}
	
	public void handleFamilyJSONArrayForRetrieve(JSONArray json, SQLiteDatabase db) throws JSONException{
		
		for(int i=0; i<json.length(); i++){
			
			JSONObject json_data =json.getJSONObject(i);
	
			String id = json_data.get("id").toString();
			String member1 = json_data.get("member1").toString();
			String member2 = json_data.get("member2").toString();
			String confirmed = json_data.get("confirmed").toString();
			String created = json_data.get("created").toString();
			String forDeletion = json_data.get("forDeletion").toString();
	
			makeIdAvailable(id, db);
			
			insertFamily(id, member1, member2, confirmed, created, forDeletion, db);
		}	
	}
	
    public void handleFamilyJSONArrayForUpload(JSONArray json, SQLiteDatabase db) throws JSONException{
		
		JSONObject json_data = json.getJSONObject(0);

		String id = json_data.get("id").toString();
		String member1 = json_data.get("member1").toString();
		String member2 = json_data.get("member2").toString();
		String confirmed = json_data.get("confirmed").toString();
		String created = json_data.get("created").toString();
		String forDeletion = json_data.get("forDeletion").toString();
		
		makeIdAvailable(id, db);
		
		updateFamily(id, member1, member2, confirmed, created, forDeletion, db);
	}

	public void handleFamilyJSONArrayForDeletion(JSONArray json, SQLiteDatabase db) throws JSONException{
		
		for(int i=0; i<json.length(); i++){
			
			JSONObject json_data = json.getJSONObject(i);

			String id = json_data.get("id").toString();
			
			updateFamilyForDeletion(id, db);
		}	
	}
	
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
		String onServer = "0";
				
		db.execSQL("INSERT INTO "+FeedFamily.TABLE_NAME+" ("+FeedFamily._ID+", "+FeedFamily.MEMBER1+", "+FeedFamily.MEMBER2+", "
				+FeedFamily.CONFIRMED+", "+FeedFamily.FAMILY_CREATED+", "
				+FeedFamily.FOR_DELETION+", "+FeedFamily.ON_SERVER+") " +
				"VALUES (NULL,'"+member1+"','"+member2+"','"+confirmed+"','"+created+"','"+forDeletion+"', '"+onServer+"')");
		
	}
	public Cursor fetchFamilies(SQLiteDatabase db){
		
		Cursor result = db.rawQuery("SELECT * FROM "+FeedFamily.TABLE_NAME +"", null);
		return result;
	}
	
}