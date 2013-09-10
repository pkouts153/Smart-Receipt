package com.SR.smartreceipt;

import com.SR.data.FeedReaderDbHelper;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AddFamilyMemberActivity extends Activity{
	
	
	Button add;
    Button cancel;
    EditText give_member_name;
	
	
	FeedReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addfamilymember);
		// Show the Up button in the action bar.
		setupActionBar();
		
		//cancel = (Button)findViewById(R.id.cancel);
        cancel.setOnClickListener((OnClickListener) this);
        add =(Button)findViewById(R.id.add);
        add.setOnClickListener((OnClickListener) this);
       
        
        mDbHelper = new FeedReaderDbHelper(this);
        db = mDbHelper.getWritableDatabase();
        Button add=(Button)findViewById(R.id.add);
        add.setVisibility(View.INVISIBLE);
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
}
