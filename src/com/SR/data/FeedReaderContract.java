package com.SR.data;

import android.provider.BaseColumns;

public final class FeedReaderContract {
	
    public FeedReaderContract() {}

    /* Inner class that defines the table contents */
    public static abstract class FeedCategory implements BaseColumns {
        public static final String TABLE_NAME = "category";
        public static final String NAME = "category_name";
        public static final String CATEGORY_CREATED = "category_created";
    }
    
    /* Inner class that defines the table contents */
    public static abstract class FeedUser implements BaseColumns {
        public static final String TABLE_NAME = "user";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String EMAIL = "email";
        public static final String FOR_UPDATE= "for_update";
        public static final String USER_CREATED = "user_created";
    }
    
    /* Inner class that defines the table contents */
    public static abstract class FeedBudget implements BaseColumns {
        public static final String TABLE_NAME = "budget";
        public static final String EXPENSE_CATEGORY = "expense_category";
        public static final String SPEND_LIMIT = "spend_limit";
        public static final String START_DATE = "start_date";
        public static final String END_DATE = "end_date";
        public static final String USER = "user";
        public static final String FAMILY_USER = "family_user";
        public static final String FOR_DELETION = "for_deletion";
        public static final String FOR_UPDATE= "for_update";
        public static final String ON_SERVER = "on_server";
        public static final String BUDGET_CREATED = "budget_created";
        public static final String IS_SURPASSED = "is_surpassed";
    }
    
    /* Inner class that defines the table contents */
    public static abstract class FeedProduct implements BaseColumns {
        public static final String TABLE_NAME = "product";
        public static final String PRODUCT_CATEGORY = "product_category";
        public static final String NAME = "product_name";
        public static final String PRICE = "price";
        public static final String PURCHASE_DATE = "purchase_date";
        public static final String STORE = "store";
        public static final String USER = "user";
        public static final String ON_SERVER = "on_server";
        public static final String PRODUCT_CREATED = "product_created";
    }
    
    /* Inner class that defines the table contents */
    public static abstract class FeedOffer implements BaseColumns {
        public static final String TABLE_NAME = "offer";
        public static final String PRODUCT_NAME = "offer_product_name";
        public static final String CATEGORY = "category";
        public static final String PRICE = "offer_price";
        public static final String DISCOUNT = "discount";
        public static final String UNTIL_DATE = "until_date";
        public static final String STORE = "store";
        public static final String OFFER_CREATED = "offer_created";
    }
    
    /* Inner class that defines the table contents */
    public static abstract class FeedFamily implements BaseColumns {
        public static final String TABLE_NAME = "family";
        public static final String MEMBER1 = "member1";
        public static final String MEMBER2 = "member2";
        public static final String CONFIRMED = "confirmed";
        public static final String FOR_DELETION = "for_deletion";
        public static final String ON_SERVER = "on_server";
        public static final String FAMILY_CREATED = "family_created";
    }
    
    /* Inner class that defines the table contents */
    public static abstract class FeedStore implements BaseColumns {
        public static final String TABLE_NAME = "store";
        public static final String NAME = "store_name";
        public static final String ADDRESS = "address";
        public static final String VAT_NUMBER = "vat_number";
        public static final String STORE_CREATED = "user_created";
    } 
}






