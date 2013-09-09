package com.SR.data;

import com.SR.data.FeedReaderContract.FeedBudget;
import com.SR.data.FeedReaderContract.FeedCategory;
import com.SR.data.FeedReaderContract.FeedProduct;
import com.SR.data.FeedReaderContract.FeedUser;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class FeedReaderDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
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
	
	private static final String SQL_CREATE_PRODUCT =
			"CREATE TABLE IF NOT EXISTS " + FeedProduct.TABLE_NAME + " (" +
			FeedProduct._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
			FeedProduct.PRODUCT_CATEGORY + " TEXT" + "," +
			FeedProduct.NAME + " TEXT" + "," +
			FeedProduct.PRICE + " REAL" + "," +
			FeedProduct.PURCHASE_DATE + " TEXT" + " )";
	
	private static final String SQL_ADD_CATEGORIES =
			 "INSERT INTO '" + FeedCategory.TABLE_NAME + "'" +
		     " SELECT NULL AS '" + FeedCategory._ID + "', '" + "Food" + "' AS '" + FeedCategory.NAME + "'" +
		     " UNION SELECT NULL, '" + "Entertainment" + "'" +
		     " UNION SELECT NULL, '" + "Clothes" + "'" +
		     " UNION SELECT NULL, '" + "All" + "'";
	
	private static final String SQL_ADD_USER =
			 "INSERT INTO '" + FeedUser.TABLE_NAME + "'" +
		     " SELECT NULL AS '" + FeedUser._ID + "', '" + "Vag" + "' AS '" + FeedUser.USERNAME + "', '" + "Vag" + "' AS '" + FeedUser.PASSWORD + "', '" + "Vag" + "' AS '" + FeedUser.EMAIL + "'";
	
    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_CATEGORIES);
		db.execSQL(SQL_CREATE_USER);
		db.execSQL(SQL_CREATE_BUDGET);
		db.execSQL(SQL_CREATE_PRODUCT);
		db.execSQL(SQL_ADD_CATEGORIES);
		db.execSQL(SQL_ADD_USER);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/*switch (oldVersion) {
		case 1:
			db.execSQL(SQL_CREATE_PRODUCT);
		}*/
	}

}
