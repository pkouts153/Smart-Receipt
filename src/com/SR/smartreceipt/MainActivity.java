package com.SR.smartreceipt;

import com.SR.data.User;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (User.USER_ID != 0) {
			setContentView(R.layout.activity_main);
		}
		else {
			Intent intent = new Intent(this, LoginActivity.class);
			startActivity(intent);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/** Called when the user clicks the Save link */
	public void goToSave(View view) {
		Intent intent = new Intent(this, SaveActivity.class);
		startActivity(intent);
	}
	
	/** Called when the user clicks the Budget link */
	public void goToBudget(View view) {
		Intent intent = new Intent(this, BudgetActivity.class);
		startActivity(intent);
	}
	
	/** Called when the user clicks the Search link */
	public void goToSearch(View view) {
		Intent intent = new Intent(this, BudgetActivity.class);
		startActivity(intent);
	}
	
	/** Called when the user clicks the Offers link */
	public void goToOffers(View view) {
		Intent intent = new Intent(this, BudgetActivity.class);
		startActivity(intent);
	}
	
	/** Called when the user clicks the Family link */
	public void goToFamily(View view) {
		Intent intent = new Intent(this, AddFamilyMemberActivity.class);
		startActivity(intent);
	}
	
	/** Called when the user clicks the List link */
	public void goToList(View view) {
		Intent intent = new Intent(this, BudgetActivity.class);
		startActivity(intent);
	}
	
	/** Called when the user clicks the Stores link */
	public void goToStores(View view) {
		Intent intent = new Intent(this, BudgetActivity.class);
		startActivity(intent);
	}
}
