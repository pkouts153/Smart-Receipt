package com.SR.processes;

import android.app.Application;

/**
 * 
 *   @author Ιωάννης Διαμαντίδης 8100039
 *  
 *  this class is used to describe whether the whole application is visible or not.
 * the logic is that when an activity is onPause, it is about to get invisible and when an activity is onResume then it gets visible
 * also, when an activity1 starts activity2, then the sequence is the following: activity1.onPause then activity2.onResume and
 * then activity1.onStop. For this reason, in onStop() method it can be checked if application is visible or not.
 */

public class MyApplication extends Application {
	  
	private static boolean activityVisible;
	
		/*this method is used to return the activityVisible variable. It is called from onStop() method of each activity*/
		public static boolean isActivityVisible() {
			return activityVisible;
		}  
		
		/*this method assign true value to activityVisible variable. It is called from onResume() method of each activity*/
		public static void activityResumed() {
			activityVisible = true;
		}

		/*this method assign false value to activityVisible variable. It is called from onPause() method of each activity*/
		public static void activityPaused() {
			activityVisible = false;
		}
	}