package com.SR.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.SR.data.FeedReaderContract.FeedOffer;

public class Offer {
	
	//String NAME;

    FeedReaderDbHelper mDbHelper;
    SQLiteDatabase db;
    Cursor c;
    
    Context context;
    
    public Offer(Context c){
    	context = c;
    }
    
    public Cursor getOffers(){
    	
    	mDbHelper = new FeedReaderDbHelper(context);
		
		// Gets the data repository in write mode
		db = mDbHelper.getWritableDatabase();
    	
		// Specifies which columns are needed from the database
		String[] projection = {
			FeedOffer._ID,	
			FeedOffer.PRODUCT_NAME,
			FeedOffer.CATEGORY,
			FeedOffer.PRICE,
			FeedOffer.DISCOUNT,
			FeedOffer.UNTIL_DATE,
			FeedOffer.STORE
		    };
		
		c = db.query(
			FeedOffer.TABLE_NAME,  				  // The table to query
		    projection,                               // The columns to return
		    null,                                	  // The columns for the WHERE clause
		    null,                            		  // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    null                                 	  // The sort order
		    );
		
		return c;
    }
    
    public FeedReaderDbHelper getOfferFeedReaderDbHelper(){
    	return mDbHelper;
    }
}
