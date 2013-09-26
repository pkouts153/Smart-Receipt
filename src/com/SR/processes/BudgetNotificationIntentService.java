package com.SR.processes;

import com.SR.data.Budget;
import com.SR.data.User;
import com.SR.data.FeedReaderContract.FeedBudget;
import com.SR.smartreceipt.R;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;

@SuppressLint("NewApi")
public class BudgetNotificationIntentService extends IntentService {
	
	public BudgetNotificationIntentService() {
		super("BudgetNotificationIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		boolean budgetSurpassed = false;
		
		Budget budget = new Budget(this);
		budgetSurpassed = budget.BudgetsSurpassed();
		
		if (budgetSurpassed) {
			
			Cursor c = budget.getBudget(User.USER_ID);
			String budgets = "";
			
			int b = 0;
			
			c.moveToFirst();
			while (!c.isAfterLast()) {
				if (c.getInt(c.getColumnIndexOrThrow(FeedBudget.IS_SURPASSED))>0) {
					if (b==1)
						budgets = budgets + ", ";
					budgets = budgets + c.getString(c.getColumnIndexOrThrow(FeedBudget.EXPENSE_CATEGORY));
					b=1;
				}
		        c.moveToNext();
			}
			
			NotificationCompat.Builder mBuilder =
			        new NotificationCompat.Builder(this)
			        .setSmallIcon(R.drawable.budget)
			        .setContentTitle("Budget Control")
			        .setContentText("Budgets surpassed: " + budgets);
			// Creates an explicit intent for an Activity in your app
			//Intent resultIntent = new Intent(this, BudgetActivity.class);
	
			// The stack builder object will contain an artificial back stack for the
			// started Activity.
			// This ensures that navigating backward from the Activity leads out of
			// your application to the Home screen.
			//TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
			// Adds the back stack for the Intent (but not the Intent itself)
			/*stackBuilder.addParentStack(BudgetActivity.class);
			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(arg0);
			PendingIntent resultPendingIntent =
			        stackBuilder.getPendingIntent(
			            0,
			            PendingIntent.FLAG_UPDATE_CURRENT
			        );*/
			PendingIntent pendingIntent
			  = PendingIntent.getActivity(getBaseContext(),
			    0, intent,
			    Intent.FLAG_ACTIVITY_NEW_TASK);
			mBuilder.setContentIntent(pendingIntent);
			NotificationManager mNotificationManager =
			    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
			mNotificationManager.notify(1, mBuilder.build());
			
		}
	}
	
}
