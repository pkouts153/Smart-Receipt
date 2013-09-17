package com.SR.smartreceipt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.SR.data.User;

import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends FragmentActivity implements OnClickListener {

	EditText email;
	EditText password;
	Button login;
    Button reset;
    
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

			String mail = email.getText().toString();
			String pass = password.getText().toString();
			
			if (!(mail.equals("") || pass.equals(""))) {
				//if (isEmailValid(mail)) {
					
					user = new User(this);
					
					if (user.userLogin(mail, pass)) {
						Intent intent = new Intent(this, MainActivity.class);
						startActivity(intent);
					}
					else {
						displayError(this.getString(R.string.no_user));
					}
		
					user.getUserFeedReaderDbHelper().close();
				/*}
				else {
					InputErrorDialogFragment errorDialog = new InputErrorDialogFragment();
					errorDialog.setMessage(this.getString(R.string.input_error));
					errorDialog.show(getFragmentManager(), "errorDialog");
				}*/
			}
			else {
				displayError(this.getString(R.string.no_input));
			}
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
		errorDialog.show(getSupportFragmentManager(), "errorDialog");
	}
	
	public void clearFields() {
		email.getText().clear();
		password.getText().clear();
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
