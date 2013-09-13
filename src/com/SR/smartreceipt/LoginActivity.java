package com.SR.smartreceipt;

import com.SR.data.User;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends Activity implements OnClickListener {

	EditText email;
	EditText password;
	Button login;
    Button reset;

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
				User user = new User(this);
	
				if (user.userLogin(mail, pass)) {
					Intent intent = new Intent(this, MainActivity.class);
					startActivity(intent);
				}
				else {
					InputErrorDialogFragment errorDialog = new InputErrorDialogFragment();
					errorDialog.setMessage(this.getString(R.string.no_user));
					errorDialog.show(getFragmentManager(), "Dialog");
				}
	
				user.getUserFeedReaderDbHelper().close();
			}
			else {
				InputErrorDialogFragment errorDialog = new InputErrorDialogFragment();
				errorDialog.setMessage(this.getString(R.string.no_input));
				errorDialog.show(getFragmentManager(), "errorDialog");
			}
		}
		else {
			clearFields();
		}
	}

	public void clearFields() {
		email.getText().clear();
		password.getText().clear();
	}
}
