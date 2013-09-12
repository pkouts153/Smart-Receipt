package com.SR.smartreceipt;

import com.SR.data.User;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
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
		// Show the Up button in the action bar.
		setupActionBar();

		email = (EditText)findViewById(R.id.email);
		password = (EditText)findViewById(R.id.password);

		login = (Button)findViewById(R.id.login_button);
		login.setOnClickListener(this);

		reset = (Button)findViewById(R.id.reset_button);
		reset.setOnClickListener(this);

	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
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
