package com.SR.smartreceipt;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/** Called when the user clicks the Save link */
	public void goToSave(View view) {
	    // Do something in response to button
	}
	
	/** Called when the user clicks the Budget link */
	public void goToBudget(View view) {
	    // Do something in response to button
	}
	
	/** Called when the user clicks the Search link */
	public void goToSearch(View view) {
	    // Do something in response to button
	}
	
	/** Called when the user clicks the Offers link */
	public void goToOffers(View view) {
	    // Do something in response to button
	}
	
	/** Called when the user clicks the Family link */
	public void goToFamily(View view) {
	    // Do something in response to button
	}
	
	/** Called when the user clicks the List link */
	public void goToList(View view) {
	    // Do something in response to button
	}
	
	/** Called when the user clicks the Stores link */
	public void goToStores(View view) {
	    // Do something in response to button
	}

}
