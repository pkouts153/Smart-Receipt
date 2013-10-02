package com.SR.data;

import com.SR.data.FeedReaderContract.FeedBudget;
import com.SR.data.FeedReaderContract.FeedCategory;
import com.SR.data.FeedReaderContract.FeedFamily;
import com.SR.data.FeedReaderContract.FeedList;
import com.SR.data.FeedReaderContract.FeedOffer;
import com.SR.data.FeedReaderContract.FeedProduct;
import com.SR.data.FeedReaderContract.FeedStore;
import com.SR.data.FeedReaderContract.FeedUser;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
* This class is responsible for initializing the database
* 
* @author Panagiotis Koutsaftikis
*/
public class FeedReaderDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SmartReceipt.db";
	
    
    // create tables queries
    
	private static final String SQL_CREATE_CATEGORIES =
	    "CREATE TABLE IF NOT EXISTS " + FeedCategory.TABLE_NAME + " (" +
		FeedCategory._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
		FeedCategory.NAME + " TEXT NOT NULL," +
		FeedCategory.CATEGORY_CREATED + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL)";
	
	private static final String SQL_CREATE_USER =
		"CREATE TABLE IF NOT EXISTS " + FeedUser.TABLE_NAME + " (" +
		FeedUser._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
		FeedUser.USERNAME + " TEXT," +
		FeedUser.PASSWORD + " TEXT," +
		FeedUser.EMAIL + " TEXT NOT NULL," +
		FeedUser.FOR_UPDATE + " BOOLEAN NOT NULL," +
		FeedUser.FROM_SERVER + " BOOLEAN NOT NULL," +
		FeedUser.USER_CREATED + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL)";
	
	private static final String SQL_CREATE_LIST =
		"CREATE TABLE IF NOT EXISTS " + FeedList.TABLE_NAME + " (" +
		FeedList._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
		FeedList.USER + " INTEGER NOT NULL," +
		FeedList.PRODUCT + " TEXT NOT NULL," +
		FeedList.IS_CHECKED + " BOOLEAN NOT NULL, " +
		" FOREIGN KEY (" + FeedList.USER + ") REFERENCES " + FeedUser.TABLE_NAME + " (" + FeedUser._ID + "))";
	
	private static final String SQL_CREATE_BUDGET =
		"CREATE TABLE IF NOT EXISTS " + FeedBudget.TABLE_NAME + " (" +
		FeedBudget._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
		FeedBudget.EXPENSE_CATEGORY + " TEXT," +
		FeedBudget.SPEND_LIMIT + " REAL NOT NULL," +
		FeedBudget.START_DATE + " TEXT NOT NULL," +
		FeedBudget.END_DATE + " TEXT NOT NULL," +
		FeedBudget.USER + " INTEGER NOT NULL," +
		FeedBudget.FAMILY_USER + " INTEGER," + 
		FeedBudget.FOR_UPDATE + " BOOLEAN NOT NULL, " +
		FeedBudget.FOR_DELETION + " BOOLEAN NOT NULL, " +
		FeedBudget.ON_SERVER + " BOOLEAN NOT NULL, " +
		FeedBudget.IS_SURPASSED + " INTEGER NOT NULL, " +
		FeedBudget.BUDGET_CREATED + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, "+
		" FOREIGN KEY (" + FeedBudget.USER + ") REFERENCES " + FeedUser.TABLE_NAME + " (" + FeedUser._ID + ")," +
		" FOREIGN KEY (" + FeedBudget.EXPENSE_CATEGORY + ") REFERENCES " + FeedCategory.TABLE_NAME + " (" + FeedCategory.NAME + ")," +
		" FOREIGN KEY (" + FeedBudget.FAMILY_USER + ") REFERENCES " + FeedUser.TABLE_NAME + " (" + FeedUser._ID + "))";
	
	private static final String SQL_CREATE_PRODUCT =
		"CREATE TABLE IF NOT EXISTS " + FeedProduct.TABLE_NAME + " (" +
		FeedProduct._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
		FeedProduct.PRODUCT_CATEGORY + " TEXT NOT NULL," +
		FeedProduct.NAME + " TEXT NOT NULL," +
		FeedProduct.PRICE + " REAL NOT NULL," +
		FeedProduct.PURCHASE_DATE + " TEXT NOT NULL," +
		FeedProduct.STORE + " INTEGER," +
		FeedProduct.USER + " INTEGER NOT NULL," +
		FeedProduct.ON_SERVER + " BOOLEAN NOT NULL, " +
		FeedProduct.PRODUCT_CREATED + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "+
		" FOREIGN KEY (" + FeedProduct.USER + ") REFERENCES " + FeedUser.TABLE_NAME + " (" + FeedUser._ID + ")," +
		" FOREIGN KEY (" + FeedProduct.PRODUCT_CATEGORY + ") REFERENCES " + FeedCategory.TABLE_NAME + " (" + FeedCategory.NAME + ")," +
		" FOREIGN KEY (" + FeedProduct.STORE + ") REFERENCES " + FeedStore.TABLE_NAME + " (" + FeedStore._ID + "))";
	
	private static final String SQL_CREATE_OFFER =
		"CREATE TABLE IF NOT EXISTS " + FeedOffer.TABLE_NAME + " (" +
		FeedOffer._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
		FeedOffer.PRODUCT_NAME + " TEXT NOT NULL," +
		FeedOffer.CATEGORY + " TEXT NOT NULL," +
		FeedOffer.PRICE + " REAL NOT NULL," +
		FeedOffer.DISCOUNT + " REAL NOT NULL," +
		FeedOffer.UNTIL_DATE + " TEXT," +
		FeedOffer.STORE + " INTEGER NOT NULL, " +
		FeedOffer.OFFER_CREATED+ " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, "+
		" FOREIGN KEY (" + FeedOffer.CATEGORY + ") REFERENCES " + FeedCategory.TABLE_NAME + " (" + FeedCategory.NAME + ")," +
		" FOREIGN KEY (" + FeedOffer.STORE + ") REFERENCES " + FeedStore.TABLE_NAME + " (" + FeedStore._ID + "))";
	
	private static final String SQL_CREATE_FAMILY =
		"CREATE TABLE IF NOT EXISTS " + FeedFamily.TABLE_NAME + " (" +
		FeedFamily._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
		FeedFamily.MEMBER1 + " INTEGER NOT NULL," +
		FeedFamily.MEMBER2 + " INTEGER NOT NULL," +
		FeedFamily.CONFIRMED + " BOOLEAN NOT NULL, " +
		FeedFamily.FOR_DELETION + " BOOLEAN NOT NULL, " +
		FeedFamily.ON_SERVER + " BOOLEAN NOT NULL," +
		FeedUser.FOR_UPDATE + " BOOLEAN NOT NULL," +
		FeedFamily.FAMILY_CREATED + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, "+
		" FOREIGN KEY (" + FeedFamily.MEMBER1 + ") REFERENCES " + FeedUser.TABLE_NAME + " (" + FeedUser._ID + ")," +
		" FOREIGN KEY (" + FeedFamily.MEMBER2 + ") REFERENCES " + FeedUser.TABLE_NAME + " (" + FeedUser._ID + "))";
	
	private static final String SQL_CREATE_STORE =
		"CREATE TABLE IF NOT EXISTS " + FeedStore.TABLE_NAME + " (" +
		FeedStore._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
		FeedStore.NAME + " TEXT NOT NULL," +
		FeedStore.ADDRESS + " TEXT NOT NULL," +
		FeedStore.VAT_NUMBER + " TEXT NOT NULL," +
		FeedStore.STORE_CREATED+ " TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL)";
	
	
	//insert data into tables
	
	private static final String SQL_ADD_CATEGORIES =
		"INSERT INTO '" + FeedCategory.TABLE_NAME + "'" +
	    " SELECT NULL AS '" + FeedCategory._ID + "', '" + "All" + "' AS '" + FeedCategory.NAME + "', '2013-09-26 00:00:00' AS '" + FeedCategory.CATEGORY_CREATED + "'" +
	    " UNION SELECT NULL, '" + "Food" + "', '2013-09-26 00:00:00'" +
	    " UNION SELECT NULL, '" + "Entertainment" + "', '2013-09-26 00:00:00'" +
	    " UNION SELECT NULL, '" + "Clothes" + "', '2013-09-26 00:00:00'";
	
	private static final String SQL_ADD_USER =
	    "INSERT INTO '" + FeedUser.TABLE_NAME + "'" +
	    " SELECT NULL AS '" + FeedUser._ID + "', 'Vag' AS '" + FeedUser.USERNAME + "', " + 
	    		"'Vag' AS '" + FeedUser.PASSWORD + "', 'Vag@a.com' AS '" + FeedUser.EMAIL + "', " +
	    		"'0' AS '" + FeedUser.FOR_UPDATE + "', '1' AS '" + FeedUser.FROM_SERVER + "', " + "" +
	    		"'2013-09-26 00:00:00' AS '" + FeedUser.USER_CREATED + "'" +
		" UNION SELECT NULL, 'Panos', 'Panos', 'Panos@a.com', '0', '1', '2013-09-26 00:00:00'" +
		" UNION SELECT NULL, 'Giannis', 'Giannis', 'Giannis@a.com', '0', '1', '2013-09-26 00:00:00'" +
		" UNION SELECT NULL, 'user', 'user', 'user@a.com', '0', '1', '2013-09-26 00:00:00'";

	private static final String SQL_ADD_STORE =
	    "INSERT INTO '" + FeedStore.TABLE_NAME + "'" +
        " SELECT '1' AS '" + FeedStore._ID + "', 'Unknown store' AS '" + FeedStore.NAME + 
        				"', 'Unknown' AS '" + FeedStore.ADDRESS + "', 'Unknown' AS '" + FeedStore.VAT_NUMBER + "', '2013-09-26 00:00:00' AS '" + FeedStore.STORE_CREATED + "'" +
		" UNION SELECT '2', 'Nike', 'Syntagma', '222', '2013-09-26 00:00:00'" +
		" UNION SELECT '3', 'Everest', 'Victoria', '333', '2013-09-26 00:00:00'" +
		" UNION SELECT '4', 'Public', 'Mall', '444', '2013-09-26 00:00:00'" +
		" UNION SELECT '5', 'Zara', 'Mall', '555', '2013-09-26 00:00:00'";

	
	private static final String SQL_ADD_FAMILY =
	    "INSERT INTO '" + FeedFamily.TABLE_NAME + "'" +
        " SELECT NULL AS '" + FeedFamily._ID + "', '" + 
	    		1 + "' AS '" + FeedFamily.MEMBER1 + "', '" + 
	    		2 + "' AS '" + FeedFamily.MEMBER2 + "', " + 
	    		"'1' AS '" + FeedFamily.CONFIRMED + "', " + 
	    		"'0' AS '" + FeedFamily.FOR_DELETION + "', " + 
	    		"'1' AS '" + FeedFamily.ON_SERVER + "', " +
	    		"'0' AS '" + FeedFamily.FOR_UPDATE + "', " +
	    		"'2013-09-26 00:00:00' AS '" + FeedFamily.FAMILY_CREATED + "'" +
	    " UNION SELECT NULL, '3', '2', '1', '0', '0', '0', '2013-09-26 00:00:00'" +
	    " UNION SELECT NULL, '4', '1', '1', '0', '0', '0', '2013-09-26 00:00:00'" +
	    " UNION SELECT NULL, '4', '3', '1', '0', '0', '0', '2013-09-26 00:00:00'";
		

	
    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // on create is called the first time the user installs the application
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_CATEGORIES);
		db.execSQL(SQL_CREATE_USER);
		db.execSQL(SQL_CREATE_BUDGET);
		db.execSQL(SQL_CREATE_PRODUCT);
		db.execSQL(SQL_CREATE_OFFER);
		db.execSQL(SQL_CREATE_FAMILY);
		db.execSQL(SQL_CREATE_STORE);
		db.execSQL(SQL_CREATE_LIST);
		
		db.execSQL(SQL_ADD_CATEGORIES);
		db.execSQL(SQL_ADD_USER);
		db.execSQL(SQL_ADD_STORE);
		db.execSQL(SQL_ADD_FAMILY);
	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		android.util.Log.w("LunchList", "Upgrading database, which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS '"+FeedCategory.TABLE_NAME+"'");
	    db.execSQL("DROP TABLE IF EXISTS '"+FeedUser.TABLE_NAME+"'");
	    db.execSQL("DROP TABLE IF EXISTS '"+FeedStore.TABLE_NAME+"'");
	    db.execSQL("DROP TABLE IF EXISTS '"+FeedFamily.TABLE_NAME+"'");
	    db.execSQL("DROP TABLE IF EXISTS '"+FeedBudget.TABLE_NAME+"'");
	    db.execSQL("DROP TABLE IF EXISTS '"+FeedProduct.TABLE_NAME+"'");
	    db.execSQL("DROP TABLE IF EXISTS '"+FeedOffer.TABLE_NAME+"'");

		onCreate(db);		
	}

}
