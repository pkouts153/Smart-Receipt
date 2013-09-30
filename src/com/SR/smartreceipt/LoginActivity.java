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

/**
* Activity that displays the login screen
* 
* @author Panagiotis Koutsaftikis
*/
public class LoginActivity extends FragmentActivity implements OnClickListener {

	// UI components
	
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

		// set up UI components
		
		email = (EditText)findViewById(R.id.email);
		password = (EditText)findViewById(R.id.password);

		login = (Button)findViewById(R.id.login_button);
		login.setOnClickListener(this);

		reset = (Button)findViewById(R.id.reset_button);
		reset.setOnClickListener(this);

	}
	
	@Override
	public void onClick(View v) {
		
		// if user clicked login
		if (login.getId() == ((Button)v).getId()) {

			// get user input
			mail = email.getText().toString();
			pass = password.getText().toString();
			
			mDbHelper = new FeedReaderDbHelper(this);
			db = mDbHelper.getWritableDatabase();
			
			//mDbHelper.onUpgrade(db, 1, 1);

			// if the user has typed an email and a password
			if (!(mail.equals("") || pass.equals(""))) {
				//if (isEmailValid(mail)) {
					
					user = new User(db);
					
					/*if(user.isDatabaseEmpty()){
						Boolean connected = true;
						if(connected){
							new RetrieveUserDataTask(this).execute(db);
						} else {
							displayError(this.getString(R.string.no_connection));
						}
					} else {*/
						// if user exists in the mobile database call MainActivity
						if (user.userLogin(mail, pass, this)) {
							Intent intent = new Intent(this, MainActivity.class);
							startActivity(intent);
						}
						//else check the server online
						else {
							/*Boolean connected = true;
							if(connected){
								new RetrieveUserDataTask(this).execute(db);
							} else {*/
								displayError(this.getString(R.string.no_connection));
							//}
						}
					
				/*}
				else {
					displayError(this.getString(R.string.not_a_mail));
				}*/
			}
			//else if email or password is empty
			else {
				displayError(this.getString(R.string.no_input));
			}
			
			mDbHelper.close();
		}
		//else if the user clicked reset
		else {
			clearFields();
		}
	}
	
	
	/**
	 * Check if the mail format is valid
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
	
	/**
	 * Display an error dialog
	 * 
	 * @param message  the message to be displayed in the dialog
	 */
	public void displayError(String message) {
		InputErrorDialogFragment errorDialog = new InputErrorDialogFragment();
		errorDialog.setMessage(message);
		errorDialog.show(getFragmentManager(), "errorDialog");
	}
	
	/**
	 * Clear the fields from user's input
	 */
	public void clearFields() {
		email.getText().clear();
		password.getText().clear();
	}
	
    @Override
    protected void onResume() {
    	super.onResume();
    	MyApplication.activityResumed();

    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    	MyApplication.activityPaused();
    	
    	if (mDbHelper != null)
    		mDbHelper.close();
    }	

}
