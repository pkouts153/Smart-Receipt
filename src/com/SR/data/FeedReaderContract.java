package com.SR.data;

import android.provider.BaseColumns;

public final class FeedReaderContract {
	
    public void FeedReaderContract() {}

    /* Inner class that defines the table contents */
    public static abstract class FeedCategory implements BaseColumns {
        public static final String TABLE_NAME = "category";
        public static final String NAME = "name";
    }
    
    /* Inner class that defines the table contents */
    public static abstract class FeedUser implements BaseColumns {
        public static final String TABLE_NAME = "user";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String EMAIL = "email";
    }
    
    /* Inner class that defines the table contents */
    public static abstract class FeedBudget implements BaseColumns {
        public static final String TABLE_NAME = "budget";
        public static final String EXPENSE_CATEGORY = "expense_category";
        public static final String SPEND_LIMIT = "spend_limit";
        public static final String START_DATE = "start_date";
        public static final String END_DATE = "end_date";
        public static final String NOTIFICATION = "notification";
        public static final String USER = "user";
    }    
}
