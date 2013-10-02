package com.SR.processes;

import com.SR.data.Budget;
import com.SR.data.FeedReaderDbHelper;
import com.SR.data.User;
import com.SR.data.FeedReaderContract.FeedBudget;
import com.SR.smartreceipt.BudgetListActivity;
import com.SR.smartreceipt.R;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

/**
* This class is responsible for displaying notification for surpassed budgets in the notification bar
* 
* @author Παναγιώτης Κουτσαυτίκης 8100062
*/
@SuppressLint("NewApi")
public class BudgetNotificationIntentService extends IntentService {
	
	FeedReaderDbHelper mDbHelper;
	SQLiteDatabase db;
	
	// mId allows you to update the notification later on.
	//if mId is the same as an existing notification, then the existing one will be replaced by the new one
	int mId;
	
	public BudgetNotificationIntentService() {
		super("BudgetNotificationIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// mId allows you to update the notification
		// e.g. if a budget is 80% surpassed and becomes fully surpassed 
		// while there is an 80% notification, then it while be replaced by a surpassed notification
		mId = 0;
		
		mDbHelper = new FeedReaderDbHelper(this);
		db = mDbHelper.getWritableDatabase();
		
		boolean budgetSurpassed = false;
		
		Budget budget = new Budget(db);
		budgetSurpassed = budget.BudgetsSurpassed();
		
		// if at least one budget is surpassed display notification
		if (budgetSurpassed) {
			
			Cursor c = budget.getBudget(User.USER_ID);

			
			NotificationCompat.Builder mBuilder =
			        new NotificationCompat.Builder(this)
					.setSmallIcon(R.drawable.ic_launcher)
					.setContentTitle("Budget Control");
			
			// display one notification for each surpassed budget
			c.moveToFirst();
			while (!c.isAfterLast()) {
     
				if (c.getInt(c.getColumnIndexOrThrow(FeedBudget.IS_SURPASSED))==1) 
					mBuilder.setContentText("Budget 80% surpassed: " + c.getString(c.getColumnIndexOrThrow(FeedBudget.EXPENSE_CATEGORY))
											+ ", " + c.getFloat(c.getColumnIndexOrThrow(FeedBudget.SPEND_LIMIT)) + "€");
				
				else if (c.getInt(c.getColumnIndexOrThrow(FeedBudget.IS_SURPASSED))==2)
					mBuilder.setContentText("Budget surpassed: " + c.getString(c.getColumnIndexOrThrow(FeedBudget.EXPENSE_CATEGORY))
											+ ", " + c.getFloat(c.getColumnIndexOrThrow(FeedBudget.SPEND_LIMIT)) + "€");
				
					// Creates an explicit intent for an Activity in your app
					Intent resultIntent = new Intent(this, BudgetListActivity.class);
			
					// The stack builder object will contain an artificial back stack for the
					// started Activity.
					// This ensures that navigating backward from the Activity leads out of
					// your application to the Home screen.
					TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
					// Adds the back stack for the Intent (but not the Intent itself)
					stackBuilder.addParentStack(BudgetListActivity.class);
					// Adds the Intent that starts the Activity to the top of the stack
					stackBuilder.addNextIntent(resultIntent);
					PendingIntent resultPendingIntent =
					        stackBuilder.getPendingIntent(
					            0,
					            PendingIntent.FLAG_UPDATE_CURRENT
					        );
					
					mBuilder.setContentIntent(resultPendingIntent);
					NotificationManager mNotificationManager =
					    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
					
					mNotificationManager.notify(++mId, mBuilder.build());
				
		        c.moveToNext();
			}
		}
		
		mDbHelper.close();
	}
	
}
