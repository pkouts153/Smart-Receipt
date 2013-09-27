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

import android.util.Log;

import com.SR.smartreceipt.LoginActivity;

public class Functions {
    
	public HttpEntity handleGetRequest(String URL){

    	HttpGet request;
		HttpClient httpclient;
		HttpResponse response;
		UsernamePasswordCredentials credentials;
		StatusLine statusLine;
		HttpEntity responseEntity = null;

		try{
			//android.os.Debug.waitForDebugger();
			httpclient = new DefaultHttpClient();
			request = new HttpGet(URL);

			credentials = new UsernamePasswordCredentials(LoginActivity.mail, LoginActivity.pass);
	        Header authorizationHeader = (new BasicScheme()).authenticate(credentials, request);
	      
	        request.setHeader(authorizationHeader); 
			request.setHeader("Content-Type", "application/json");

	        response = httpclient.execute(request); 
	        statusLine = response.getStatusLine();
	         
			Log.i("status", statusLine.toString());

	        if(statusLine.getStatusCode() == HttpStatus.SC_OK){

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
	
    public HttpEntity handlePostRequest(JSONArray json, String URL){

    	StringEntity entity;
    	HttpPost httpPost;
		HttpClient httpclient;
		HttpResponse response;
		UsernamePasswordCredentials credentials;
		StatusLine statusLine;
		HttpEntity responseEntity = null;

		try{
			//android.os.Debug.waitForDebugger();
			httpclient = new DefaultHttpClient();
			httpPost = new HttpPost(URL);

			entity = new StringEntity(json.toString());

			credentials = new UsernamePasswordCredentials(LoginActivity.mail, LoginActivity.pass);
			Header authorizationHeader = (new BasicScheme()).authenticate(credentials, httpPost);

			httpPost.setHeader(authorizationHeader);
			httpPost.setHeader("Content-Type", "application/json");

			httpPost.setEntity(entity);
			response = httpclient.execute(httpPost);

	        statusLine = response.getStatusLine();
	
	        Log.i("status", statusLine.toString());
	         
	        if(statusLine.getStatusCode() == HttpStatus.SC_OK){

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
	
    public StatusLine handlePutRequest(JSONArray json, String URL){
		
    	StringEntity entity;
    	HttpPut httpPut;
		HttpClient httpclient;
		HttpResponse response;
		UsernamePasswordCredentials credentials;
		StatusLine statusLine = null;
		
		try{
			//android.os.Debug.waitForDebugger();
			httpclient = new DefaultHttpClient();
			httpPut = new HttpPut(URL);

			entity = new StringEntity(json.toString());

			credentials = new UsernamePasswordCredentials(LoginActivity.mail, LoginActivity.pass);
			Header authorizationHeader = (new BasicScheme()).authenticate(credentials, httpPut);

			httpPut.setHeader(authorizationHeader);
			httpPut.setHeader("Content-Type", "application/json");

			httpPut.setEntity(entity);
			response = httpclient.execute(httpPut);
	        
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
    
    public StatusLine handleDeleteRequest(String URL){
    	
		StatusLine statusLine = null;
		HttpDelete httpDelete;
		HttpClient httpclient;
		HttpResponse response;
		UsernamePasswordCredentials credentials;
		
		try{
			//android.os.Debug.waitForDebugger();

			httpclient = new DefaultHttpClient();
			httpDelete = new HttpDelete(URL);
			    			
			credentials = new UsernamePasswordCredentials(LoginActivity.mail, LoginActivity.pass);
	        Header authorizationHeader = (new BasicScheme()).authenticate(credentials, httpDelete);
	        
	        httpDelete.setHeader(authorizationHeader); 
	        httpDelete.setHeader("Content-Type", "application/json");
			
        	response = httpclient.execute(httpDelete); 
        	
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
    
    public String convertTimestampToDate(String timestamp){
    	
		Timestamp time = Timestamp.valueOf(timestamp);
		String date = new SimpleDateFormat("yyyy-MM-dd").format(time);
		
		return date;
    }
}
