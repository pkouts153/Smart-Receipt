package com.SR.data;

import com.SR.data.FeedReaderConract.FeedBudget;
import com.SR.data.FeedReaderConract.FeedCategory;
import com.SR.data.FeedReaderConract.FeedUser;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class FeedReaderDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "SmartReceipt.db";
	
	private static final String SQL_CREATE_CATEGORIES =
	    "CREATE TABLE IF NOT EXISTS " + FeedCategory.TABLE_NAME + " (" +
		FeedCategory._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
		FeedCategory.NAME + " TEXT" + " )";
	
	private static final String SQL_CREATE_USER =
		"CREATE TABLE IF NOT EXISTS " + FeedUser.TABLE_NAME + " (" +
		FeedUser._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
		FeedUser.USERNAME + " TEXT" + "," +
		FeedUser.PASSWORD + " TEXT" + "," +
		FeedUser.EMAIL + " TEXT" + " )";
    
	private static final String SQL_CREATE_BUDGET =
			"CREATE TABLE IF NOT EXISTS " + FeedBudget.TABLE_NAME + " (" +
			FeedBudget._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
			FeedBudget.EXPENSE_CATEGORY + " TEXT" + "," +
			FeedBudget.SPEND_LIMIT + " REAL" + "," +
			FeedBudget.START_DATE + " TEXT" + "," +
			FeedBudget.END_DATE + " TEXT" + "," +
			FeedBudget.NOTIFICATION + " INTEGER" + "," +
			FeedBudget.USER + " INTEGER" + " )";
	
    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_CATEGORIES);
		db.execSQL(SQL_CREATE_USER);
		//db.execSQL(SQL_CREATE_BUDGET);
		
		// Gets the data repository in write mode
		//db = getWritableDatabase();

		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		
		values.put(FeedCategory.NAME, "Food");
		db.insert(FeedCategory.TABLE_NAME, "null", values);
		
		ContentValues values2 = new ContentValues();
		values2.put(FeedCategory.NAME, "Entertainment");
		db.insert(FeedCategory.TABLE_NAME, "null", values2);
		
		ContentValues values3 = new ContentValues();
		values3.put(FeedCategory.NAME, "Clothes");
		db.insert(FeedCategory.TABLE_NAME, "null", values3);
		
		ContentValues values4 = new ContentValues();
		
		values4.put(FeedCategory.NAME, "All");
		db.insert(FeedCategory.TABLE_NAME, "null", values4);
		
		ContentValues values1 = new ContentValues();
		
		values1.put(FeedUser.USERNAME, "Vag");
		values1.put(FeedUser.PASSWORD, "Vag");
		values1.put(FeedUser.EMAIL, "Vag@gmail.com");
		db.insert(FeedUser.TABLE_NAME, "null", values1);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		switch (oldVersion) {
		case 1:
			db.execSQL(SQL_CREATE_BUDGET);
		}
		/*case 1:
			db.execSQL(SQL_CREATE_CATEGORIES);
			ContentValues values = new ContentValues();
		
			values.put(FeedCategory.NAME, "Food");
			db.insert(FeedCategory.TABLE_NAME, "null", values);
		case 2:
			ContentValues values2 = new ContentValues();
			
			values2.put(FeedCategory.NAME, "Entertainment");
			db.insert(FeedCategory.TABLE_NAME, "null", values2);
		case 3:
			ContentValues values3 = new ContentValues();
			
			values3.put(FeedCategory.NAME, "Clothes");
			db.insert(FeedCategory.TABLE_NAME, "null", values3);
			
			ContentValues values4 = new ContentValues();
			
			values4.put(FeedCategory.NAME, "All");
			db.insert(FeedCategory.TABLE_NAME, "null", values4);
		}*/
		
		//onCreate(db);

	}

}
