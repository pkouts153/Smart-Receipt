package com.SR.processes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.SR.data.User;

/**
 * 
 * @author Ιωάννης Διαμαντίδης 8100039
 * 
 * This class contains general methods that are used regularly in other classes
 */

public class Functions {
    
	/*this method is used when a GET request is made. It gets as a parameter a URL and returns the response body */
	public HttpEntity handleGetRequest(String URL){

    	HttpGet request;
		HttpClient httpclient;
		HttpResponse response;
		UsernamePasswordCredentials credentials;
		StatusLine statusLine;
		HttpEntity responseEntity = null;

		try{
			//create a new Http client
			httpclient = new DefaultHttpClient();
			//prepare a request object
			request = new HttpGet(URL);
			//set credentials
			credentials = new UsernamePasswordCredentials(User.EMAIL, User.PASSWORD);
			//put credentials to header
	        Header authorizationHeader = (new BasicScheme()).authenticate(credentials, request);
	        //set request Header
	        request.setHeader(authorizationHeader); 
			request.setHeader("Content-Type", "application/json");
			//execute the request
	        response = httpclient.execute(request); 
	        //get the response status
	        statusLine = response.getStatusLine();
	         
			Log.i("status", statusLine.toString());
			//if status is ok, or code 200
	        if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	        	//assign response entity to responseEntity variable
	        	responseEntity = response.getEntity();       	
			}

		} catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } catch (AuthenticationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return responseEntity;
    }
	
	/*this method is used when a Post request is made. It gets as a parameter a JSONArray and a URL and returns the response body */
   public HttpEntity handlePostRequest(JSONArray json, String URL){

    	StringEntity entity;
    	HttpPost httpPost;
		HttpClient httpclient;
		HttpResponse response;
		UsernamePasswordCredentials credentials;
		StatusLine statusLine;
		HttpEntity responseEntity = null;

		try{
			//create a new Http client
			httpclient = new DefaultHttpClient();
			//prepare a request object			
			httpPost = new HttpPost(URL);
			//assign as request entity the JSONArray as string
			entity = new StringEntity(json.toString());
			//set credentials
			credentials = new UsernamePasswordCredentials(User.EMAIL, User.PASSWORD);
			//put credentials to header
			Header authorizationHeader = (new BasicScheme()).authenticate(credentials, httpPost);
	        //set request Header
			httpPost.setHeader(authorizationHeader);
			httpPost.setHeader("Content-Type", "application/json");
	        //set request Entity
			httpPost.setEntity(entity);
			//execute the request
			response = httpclient.execute(httpPost);
	        //get the response status
	        statusLine = response.getStatusLine();
	
	        Log.i("status", statusLine.toString());
			//if status is ok, or code 200
	        if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	        	//assign response entity to responseEntity variable
	        	responseEntity = response.getEntity();       	
			}

		} catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } catch (AuthenticationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return responseEntity;
    }
	
	/*this method is used when a PUT request is made. It gets as a parameter a JSONArray and a URL and returns the status */
    public StatusLine handlePutRequest(JSONArray json, String URL){
		
    	StringEntity entity;
    	HttpPut httpPut;
		HttpClient httpclient;
		HttpResponse response;
		UsernamePasswordCredentials credentials;
		StatusLine statusLine = null;
		
		try{
			//create a new Http client
			httpclient = new DefaultHttpClient();
			//prepare a request object			
			httpPut = new HttpPut(URL);
			//assign as request entity the JSONArray as string
			entity = new StringEntity(json.toString());
			//set credentials
			credentials = new UsernamePasswordCredentials(User.EMAIL, User.PASSWORD);
			//put credentials to header
			Header authorizationHeader = (new BasicScheme()).authenticate(credentials, httpPut);
	        //set request Header
			httpPut.setHeader(authorizationHeader);
			httpPut.setHeader("Content-Type", "application/json");
	        //set request Entity
			httpPut.setEntity(entity);
			//execute the request
			response = httpclient.execute(httpPut);
	        //get the response status
			statusLine = response.getStatusLine();
			
		} catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } catch (AuthenticationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return statusLine; 
    }
    
	/*this method is used when a DELETE request is made. It gets as a parameter a URL and returns the status */
    public StatusLine handleDeleteRequest(String URL){
    	
		StatusLine statusLine = null;
		HttpDelete httpDelete;
		HttpClient httpclient;
		HttpResponse response;
		UsernamePasswordCredentials credentials;
		
		try{
			//create a new Http client

			httpclient = new DefaultHttpClient();
			//prepare a request object			
			httpDelete = new HttpDelete(URL);
			//set credentials
			credentials = new UsernamePasswordCredentials(User.EMAIL, User.PASSWORD);
			//put credentials to header
	        Header authorizationHeader = (new BasicScheme()).authenticate(credentials, httpDelete);
	        //set request Header
	        httpDelete.setHeader(authorizationHeader); 
	        httpDelete.setHeader("Content-Type", "application/json");
			//execute the request
        	response = httpclient.execute(httpDelete); 
	        //get the response status
        	statusLine = response.getStatusLine();
        	
		} catch (ClientProtocolException e) {
	        e.printStackTrace();
	    } catch (AuthenticationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		return statusLine;	
    }
    
    /*this method is used to convert the response entity of a request into String*/
    public String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	} 
    
    /*this method is used to convert date from format "yyyy-MM-dd HH:mm:SS" to "yyyy-MM-dd"*/
    public String convertTimestampToDate(String timestamp){
    	
		Timestamp time = Timestamp.valueOf(timestamp);
		String date = new SimpleDateFormat("yyyy-MM-dd").format(time);
		
		return date;
    }
    
    /*this method is used to check is a wifi connection exists or not and returns true or false accordingly*/
    public Boolean isWifiConnected(Context context){
    	
    	//create an object of ConnectivityManager class, in order to monitor network connections
    	ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	//call getNetworkInfo function to get connection status information about wifi connection 
    	NetworkInfo nWIFI = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    	//return a boolean that indicates whether a wifi connection exists or not
    	return nWIFI.isConnected();

    }
    
    /*this method is used to check is a mobile connection exists or not and returns true or false accordingly*/
    public Boolean isMobileConnected(Context context){
    	
    	//create an object of ConnectivityManager class, in order to monitor network connections
    	ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    	//call getNetworkInfo function to get connection status information about mobile connection 
    	NetworkInfo nMobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    	//return a boolean that indicates whether a mobile connection exists or not
    	return nMobile.isConnected();
    }
}
