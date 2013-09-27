package com.SR.processes;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.SR.data.Product;
import com.SR.data.FeedReaderContract.FeedBudget;
import com.SR.data.FeedReaderContract.FeedProduct;

public class UploadProductTask extends AsyncTask<SQLiteDatabase, Void, String> {
	
    SQLiteDatabase db;

    @Override
	protected String doInBackground(SQLiteDatabase... arg0) {
        
    	String URL = "http://10.0.2.2/php/rest/product.php";
		String id, name, category, price, date, user, store, created ,result;
		HttpEntity entity;
		InputStream instream;
		JSONArray jsonArray, json;
		Cursor queryResult;
		
		db = arg0[0];
		
	/*	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date datePurchaseDate=null;
		
		try {
			datePurchaseDate = dateFormat.parse("2013-09-23");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		Timestamp timestampPurchaseDate = new Timestamp(datePurchaseDate.getTime());
		String purchaseDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestampPurchaseDate);
		
		Date date1= new Date();
		Timestamp timestampToday = new Timestamp(date1.getTime());
		String today = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestampToday);
		
		Log.i("today", today);
		
		db.execSQL("INSERT INTO "+FeedProduct.TABLE_NAME +" ("+FeedProduct._ID +","+FeedProduct.NAME +", "+FeedProduct.PRODUCT_CATEGORY +
				", "+FeedProduct.PRICE +", "+FeedProduct.PURCHASE_DATE +", "+FeedProduct.USER +
				", "+FeedProduct.STORE +", "+FeedProduct.PRODUCT_CREATED +", "+FeedProduct.ON_SERVER +") " +
				"VALUES ('10','coca-cola','2','1.2','"+purchaseDate+"','4','3','"+today+"', '0')");
	*/
		queryResult = new Product().fetchLocalProduct(db);
		
		while(queryResult.moveToNext()){
						
			id = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedProduct._ID));
			name = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedProduct.NAME));
			category = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedProduct.PRODUCT_CATEGORY));
			price = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedProduct.PRICE));
			date = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedProduct.PURCHASE_DATE));
			user = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedProduct.USER));
			store = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedProduct.STORE));
			created = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedProduct.PRODUCT_CREATED));
			
    		try{
    			//android.os.Debug.waitForDebugger();
    			json = new Product().convertStringToJson(id, name, category, price, date, user, store, created);
                    			
    			entity =  new Functions().handlePostRequest(json, URL);

    	        if(entity!=null){
	
	        		instream = entity.getContent();
	        		result = new Functions().convertStreamToString(instream);
	
	                jsonArray = new JSONArray(result);
	                instream.close();
	        		
	                Log.i("UploadProduct", jsonArray.toString());

	                new Product().handleProductJSONArrayForUpload(jsonArray, db);
    			}
    	        
			} catch (ClientProtocolException e) {
		        e.printStackTrace();
		    } catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
    	return "OK";
    }
    
    @Override
	protected void onPostExecute(String result) {

    }
}