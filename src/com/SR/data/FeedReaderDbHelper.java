package com.SR.data;

import com.SR.data.FeedReaderContract.FeedBudget;
import com.SR.data.FeedReaderContract.FeedCategory;
import com.SR.data.FeedReaderContract.FeedFamily;
import com.SR.data.FeedReaderContract.FeedOffer;
import com.SR.data.FeedReaderContract.FeedProduct;
import com.SR.data.FeedReaderContract.FeedStore;
import com.SR.data.FeedReaderContract.FeedUser;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FeedReaderDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SmartReceipt.db";
	
    
    // create tables queries
    
	private static final String SQL_CREATE_CATEGORIES =
	    "CREATE TABLE IF NOT EXISTS " + FeedCategory.TABLE_NAME + " (" +
		FeedCategory._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
		FeedCategory.NAME + " TEXT NOT NULL)";
	
	private static final String SQL_CREATE_USER =
		"CREATE TABLE IF NOT EXISTS " + FeedUser.TABLE_NAME + " (" +
		FeedUser._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
		FeedUser.USERNAME + " TEXT NOT NULL," +
		FeedUser.PASSWORD + " TEXT NOT NULL," +
		FeedUser.EMAIL + " TEXT NOT NULL)";

	private static final String SQL_CREATE_BUDGET =
		"CREATE TABLE IF NOT EXISTS " + FeedBudget.TABLE_NAME + " (" +
		FeedBudget._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
		FeedBudget.EXPENSE_CATEGORY + " TEXT," +
		FeedBudget.SPEND_LIMIT + " REAL NOT NULL," +
		FeedBudget.START_DATE + " TEXT NOT NULL," +
		FeedBudget.END_DATE + " TEXT NOT NULL," +
		FeedBudget.USER + " INTEGER NOT NULL," +
		FeedBudget.FAMILY_USER + " INTEGER," + 
		FeedBudget.FOR_DELETION + " BOOLEAN NOT NULL, " +
		FeedBudget.ON_SERVER + " BOOLEAN NOT NULL, " +
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
		FeedOffer.STORE + " INTEGER NOT NULL," +
		" FOREIGN KEY (" + FeedOffer.CATEGORY + ") REFERENCES " + FeedCategory.TABLE_NAME + " (" + FeedCategory.NAME + ")," +
		" FOREIGN KEY (" + FeedOffer.STORE + ") REFERENCES " + FeedStore.TABLE_NAME + " (" + FeedStore._ID + "))";
	
	private static final String SQL_CREATE_FAMILY =
		"CREATE TABLE IF NOT EXISTS " + FeedFamily.TABLE_NAME + " (" +
		FeedFamily._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
		FeedFamily.MEMBER1 + " INTEGER NOT NULL," +
		FeedFamily.MEMBER2 + " INTEGER NOT NULL," +
		FeedFamily.CONFIRMED + " BOOLEAN NOT NULL, " +
		FeedFamily.FOR_DELETION + " BOOLEAN NOT NULL, " +
		FeedFamily.ON_SERVER + " BOOLEAN NOT NULL, " +
		" FOREIGN KEY (" + FeedFamily.MEMBER1 + ") REFERENCES " + FeedUser.TABLE_NAME + " (" + FeedUser._ID + ")," +
		" FOREIGN KEY (" + FeedFamily.MEMBER2 + ") REFERENCES " + FeedUser.TABLE_NAME + " (" + FeedUser._ID + "))";
	
	private static final String SQL_CREATE_STORE =
		"CREATE TABLE IF NOT EXISTS " + FeedStore.TABLE_NAME + " (" +
		FeedStore._ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
		FeedStore.NAME + " TEXT NOT NULL," +
		FeedStore.ADDRESS + " TEXT NOT NULL," +
		FeedStore.VAT_NUMBER + " TEXT NOT NULL)";
	
	
	//insert data into tables
	
	private static final String SQL_ADD_CATEGORIES =
		"INSERT INTO '" + FeedCategory.TABLE_NAME + "'" +
	    " SELECT NULL AS '" + FeedCategory._ID + "', '" + "Food" + "' AS '" + FeedCategory.NAME + "'" +
	    " UNION SELECT NULL, '" + "Entertainment" + "'" +
	    " UNION SELECT NULL, '" + "Clothes" + "'";
	
	/*private static final String SQL_ADD_USER =
	    "INSERT INTO '" + FeedUser.TABLE_NAME + "'" +
	    " SELECT NULL AS '" + FeedUser._ID + "', '" + "Vag" + "' AS '" + FeedUser.USERNAME + "', '" + "Vag" + "' AS '" + FeedUser.PASSWORD + "', '" + "Vag" + "' AS '" + FeedUser.EMAIL + "'" +
		" UNION SELECT NULL, '" + "Panos" + "', '" + "Panos" + "', '" + "Panos" + "'";*/

	private static final String SQL_ADD_USER1 =
	    "INSERT INTO '" + FeedUser.TABLE_NAME + "'" +
        " SELECT NULL AS '" + FeedUser._ID + "', '" + 
			    "Vag" + "' AS '" + FeedUser.USERNAME + "', '" + 
		        "Vag" + "' AS '" + FeedUser.PASSWORD + "', '" + 
			    "Vag" + "' AS '" + FeedUser.EMAIL + "'";
	
	private static final String SQL_ADD_USER2 =
	    "INSERT INTO '" + FeedUser.TABLE_NAME + "'" +
        " SELECT NULL AS '" + FeedUser._ID + "', '" + 
			    "Panos" + "' AS '" + FeedUser.USERNAME + "', '" + 
		        "Panos" + "' AS '" + FeedUser.PASSWORD + "', '" + 
			    "Panos" + "' AS '" + FeedUser.EMAIL + "'";
	
	private static final String SQL_ADD_USER3 =
		    "INSERT INTO '" + FeedUser.TABLE_NAME + "'" +
	        " SELECT NULL AS '" + FeedUser._ID + "', '" + 
				    "Giannis" + "' AS '" + FeedUser.USERNAME + "', '" + 
			        "Giannis" + "' AS '" + FeedUser.PASSWORD + "', '" + 
				    "Giannis" + "' AS '" + FeedUser.EMAIL + "'";
	
	private static final String SQL_ADD_PRODUCTS =
		    "INSERT INTO '" + FeedProduct.TABLE_NAME + "'" +
		    " SELECT NULL AS '" + FeedProduct._ID + "', 'Food' AS '" + FeedProduct.PRODUCT_CATEGORY + "', 'Coffee' AS '" + FeedProduct.NAME + 
		    			"', '3' AS '" + FeedProduct.PRICE + "', '2013-09-16' AS '" + FeedProduct.PURCHASE_DATE + 
		    			"', '2' AS '" + FeedProduct.STORE + "', '1' AS '" + FeedProduct.USER + "', '0' AS '" + FeedProduct.ON_SERVER + "'" +
			" UNION SELECT NULL, 'Food', 'Tost', '2', '2013-09-13', '1', '1', '0'" +
			" UNION SELECT NULL, 'Entertainment', 'Movie', '5', '2013-09-14', null, '1', '0'" +
			" UNION SELECT NULL, 'Clothes', 'Jean', '25', '2013-09-20', '1', '1', '0'" +
			" UNION SELECT NULL, 'Entertainment', 'Theater', '10', '2013-09-19', '1', '2', '0'" +
			" UNION SELECT NULL, 'Food', 'Tost', '3', '2013-09-16', '2', '2', '0'";
	
	private static final String SQL_ADD_BUDGET =
		    "INSERT INTO '" + FeedBudget.TABLE_NAME + "'" +
		    " SELECT NULL AS '" + FeedBudget._ID + "', '" + "Food" + "' AS '" + FeedBudget.EXPENSE_CATEGORY + "', '" + "8" + "' AS '" + FeedBudget.SPEND_LIMIT + "', '" + 
		    			"2013-09-10" + "' AS '" + FeedBudget.START_DATE + "', '" + "2013-09-20" + "' AS '" + FeedBudget.END_DATE + "', '" + 
		    			"1" + "' AS '" + FeedBudget.USER + "', '" + "null" + "' AS '" + FeedBudget.FAMILY_USER + "', " +
		    			"'0' AS '" + FeedBudget.FOR_DELETION + "', '0' AS '" + FeedBudget.ON_SERVER + "'" +
			" UNION SELECT NULL, '" + "Food" + "', '" + "6" + "', '" + "2013-09-10" + "', '" + "2013-09-20" + "', '" + "2" + "', '" + "null" + "', '0', '0'";
	
	private static final String SQL_ADD_STORE =
	    "INSERT INTO '" + FeedStore.TABLE_NAME + "'" +
        " SELECT NULL AS '" + FeedStore._ID + "', 'Nike' AS '" + FeedStore.NAME + 
        				"', 'Syntagma' AS '" + FeedStore.ADDRESS + "', '1' AS '" + FeedStore.VAT_NUMBER + "'" +
		" UNION SELECT NULL, 'Everest', 'Victoria', '2'";
		
	private static final String SQL_ADD_OFFER =
	    "INSERT INTO '" + FeedOffer.TABLE_NAME + "'" +
        " SELECT NULL AS '" + FeedOffer._ID + "', '" + 
			    "Total 90" + "' AS '" + FeedOffer.PRODUCT_NAME + "', '" + 
		        "Clothes" + "' AS '" + FeedOffer.CATEGORY + "', '" + 
			    "100" + "' AS '" + FeedOffer.PRICE + "', '" + 
		        "90" + "' AS '" + FeedOffer.DISCOUNT + "', '" + 
			    "2/10/2013" + "' AS '" + FeedOffer.UNTIL_DATE + "', '" + 
		        1 + "' AS '" + FeedOffer.STORE + "'";
		
	private static final String SQL_ADD_FAMILY =
	    "INSERT INTO '" + FeedFamily.TABLE_NAME + "'" +
        " SELECT NULL AS '" + FeedFamily._ID + "', '" + 
	    		1 + "' AS '" + FeedFamily.MEMBER1 + "', '" + 
	    		2 + "' AS '" + FeedFamily.MEMBER2 + "', " + 
	    		"'1' AS '" + FeedFamily.CONFIRMED + "', " + 
	    		"'0' AS '" + FeedFamily.FOR_DELETION + "', " + 
	    		"'0' AS '" + FeedFamily.ON_SERVER + "'" +
	    " UNION SELECT NULL, '1', '3', '1', '0', '0'";
		

	
    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_CATEGORIES);
		db.execSQL(SQL_CREATE_USER);
		db.execSQL(SQL_CREATE_BUDGET);
		db.execSQL(SQL_CREATE_PRODUCT);
		db.execSQL(SQL_CREATE_OFFER);
		db.execSQL(SQL_CREATE_FAMILY);
		db.execSQL(SQL_CREATE_STORE);
		
		db.execSQL(SQL_ADD_CATEGORIES);
		db.execSQL(SQL_ADD_USER1);
		db.execSQL(SQL_ADD_USER2);
		db.execSQL(SQL_ADD_USER3);
		db.execSQL(SQL_ADD_STORE);
		db.execSQL(SQL_ADD_OFFER);
		db.execSQL(SQL_ADD_FAMILY);
		db.execSQL(SQL_ADD_PRODUCTS);
		db.execSQL(SQL_ADD_BUDGET);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/*switch (oldVersion) {
		case 1:
			db.execSQL(SQL_ADD_USER3);
		}*/
	}

}
