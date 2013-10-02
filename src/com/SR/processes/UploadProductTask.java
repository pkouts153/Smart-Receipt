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

import com.SR.data.FeedReaderContract.FeedProduct;
import com.SR.data.Product;

/**
 * 
 * @author Ιωάννης Διαμαντίδης 8100039
 * 
 * this AsyncTask is used to call a web service which inserts a product into server database
 *
 */

public class UploadProductTask extends AsyncTask<SQLiteDatabase, Void, Void> {
	
    SQLiteDatabase db;

    @Override
	protected Void doInBackground(SQLiteDatabase... arg0) {
        
    	String URL = "http://10.0.2.2/php/rest/product.php";//the URL of the web service
		String id, name, category, price, date, user, store, created ,result;
    	HttpEntity entity;//contains the response Entity
    	InputStream instream;//used to retrieve the content of the response entity
		JSONArray json, jsonArray;//contain the record that will be sent and the record that will be retrieved with the new id accordingly
		Cursor queryResult;//contains the products that are stored locally
		
		db = arg0[0];
/*		
		db.execSQL("INSERT INTO "+FeedProduct.TABLE_NAME +" ("+FeedProduct._ID +","+FeedProduct.NAME +", "+FeedProduct.PRODUCT_CATEGORY +
				", "+FeedProduct.PRICE +", "+FeedProduct.PURCHASE_DATE +", "+FeedProduct.USER +
				", "+FeedProduct.STORE +", "+FeedProduct.PRODUCT_CREATED +", "+FeedProduct.ON_SERVER +") " +
				"VALUES ('10','coca-cola','2','1.2','2013-09-30','4','3','2013-09-24 00:00:00', '0')");
*/	
		//call fetchLocalProduct method to retrieve the products that are stored locally
		queryResult = new Product().fetchLocalProduct(db);
		//as long as there are products to be uploaded
		while(queryResult.moveToNext()){
			//get the details of the product		
			id = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedProduct._ID));
			name = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedProduct.NAME));
			category = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedProduct.PRODUCT_CATEGORY));
			price = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedProduct.PRICE));
			date = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedProduct.PURCHASE_DATE));
			user = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedProduct.USER));
			store = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedProduct.STORE));
			created = queryResult.getString(queryResult.getColumnIndexOrThrow(FeedProduct.PRODUCT_CREATED));
			
    		try{
    			//convert string values to JSONArray
    			json = new Product().convertStringToJson(id, name, category, price, date, user, store, created);
    			//call handlePostRequest method to make a POST request and get the response entity
    			entity =  new Functions().handlePostRequest(json, URL);

    	        if(entity!=null){
    				//get he content of the response entity
            		instream = entity.getContent();
            		//call the convertStreamToString method
            		result = new Functions().convertStreamToString(instream);
            		//create a JSONArray with the response content
                    jsonArray = new JSONArray(result);
                    //close InputStream
                    instream.close();
	        		
	                Log.i("UploadProduct", jsonArray.toString());
	        		//call handleProductJSONArrayForUpload method
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
    	return null;
    }
}