package com.SR.smartreceipt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.SR.data.FeedReaderDbHelper;
import com.SR.data.User;
import com.SR.processes.MyApplication;
import com.SR.processes.RetrieveUserDataTask;

public class LoginActivity extends FragmentActivity implements OnClickListener {

	EditText email;
	EditText password;
	Button login;
    Button reset;
    
    public static String mail;
	public static String pass;
	
	FeedReaderDbHelper mDbHelper;
	SQLiteDatabase db;
	
    User user;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		email = (EditText)findViewById(R.id.email);
		password = (EditText)findViewById(R.id.password);

		login = (Button)findViewById(R.id.login_button);
		login.setOnClickListener(this);

		reset = (Button)findViewById(R.id.reset_button);
		reset.setOnClickListener(this);

	}
	
	@Override
	public void onClick(View v) {

		if (login.getId() == ((Button)v).getId()) {

			mail = email.getText().toString();
			pass = password.getText().toString();
			
			mDbHelper = new FeedReaderDbHelper(this);
			db = mDbHelper.getWritableDatabase();
			
			//mDbHelper.onUpgrade(db, 1, 1);

			if (!(mail.equals("") || pass.equals(""))) {
				//if (isEmailValid(mail)) {
					
					user = new User(db);
					
					/*if (user.userLogin(mail, pass)) {
						Intent intent = new Intent(this, MainActivity.class);
						startActivity(intent);
					}
					else {
						an einai sundedemenos
							psa3e sto server
							
							an uparxei
								new RetrieveUserDataTask(this).execute();
							alliws
								displayError(this.getString(R.string.no_user));
						alliws
							displayError(this.getString(R.string.no_user));
					}*/
					
					/*if(user.isDatabaseEmpty()){
						Boolean connected = true;
						if(connected){
							new RetrieveUserDataTask(this).execute(db);
						} else {
							displayError("You must connect to the Internet1");
						}
					} else {*/
						if (user.userLogin(mail, pass)) {
							Intent intent = new Intent(this, MainActivity.class);
							startActivity(intent);
						}
						else {
							/*Boolean connected = true;
							if(connected){
								new RetrieveUserDataTask(this).execute(db);
							} else {*/
								displayError("You must connect to the Internet");
							//}
						}
					
				/*}
				else {
					displayError(this.getString(R.string.not_a_mail));
				}*/
			}
			else {
				displayError(this.getString(R.string.no_input));
			}
			
			mDbHelper.close();
		}
		else {
			clearFields();
		}
	}
	
	
	/**
	 * method is used for checking valid email id format.
	 * 
	 * @param email
	 * @return boolean true for valid false for invalid
	 */
	public static boolean isEmailValid(String email) {
	    boolean isValid = false;

	    String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
	    CharSequence inputStr = email;

	    Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
	    Matcher matcher = pattern.matcher(inputStr);
	    if (matcher.matches()) {
	        isValid = true;
	    }
	    return isValid;
	}
	
	public void displayError(String message) {
		InputErrorDialogFragment errorDialog = new InputErrorDialogFragment();
		errorDialog.setMessage(message);
		errorDialog.show(getFragmentManager(), "errorDialog");
	}
	
	public void clearFields() {
		email.getText().clear();
		password.getText().clear();
	}
	
    @Override
    protected void onResume() {
    	super.onResume();
    	MyApplication.activityResumed();
    	
    	/*if (mDbHelper == null) {
    		new FeedReaderDbHelper(this);
    		db = mDbHelper.getWritableDatabase();
    	}*/
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	MyApplication.activityPaused();
    	
    	if (mDbHelper != null)
    		mDbHelper.close();
    }	

	
	/*@Override
	protected void onStop() {
	    super.onStop();
	    
	    if (user.getUserFeedReaderDbHelper() != null)
	    	user.getUserFeedReaderDbHelper().close();
	}
	
	@Override
	protected void onPause() {
	    super.onPause();
	    
	    if (user.getUserFeedReaderDbHelper() != null)
	    	user.getUserFeedReaderDbHelper().close();
	}*/
}
