package com.SR.data;

import android.provider.BaseColumns;

public final class FeedReaderConract {
	
    public FeedReaderConract() {}

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
}
