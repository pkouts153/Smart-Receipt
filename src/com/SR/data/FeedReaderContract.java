package com.SR.data;

import android.provider.BaseColumns;

/**
 * Class containing classes representing the tables columns
 * 
* @authors Παναγιώτης Κουτσαυτίκης 8100062, Βαγγέλης Μαράκης 8100069, Γιάννης Διαμαντίδης 8100039
*/

public final class FeedReaderContract {
	
    public FeedReaderContract() {}

    /** Inner class that defines the Category table contents */
    public static abstract class FeedCategory implements BaseColumns {
        public static final String TABLE_NAME = "category";
        public static final String NAME = "category_name";
        public static final String CATEGORY_CREATED = "category_created";
    }
    
    /** Inner class that defines the User table contents */
    public static abstract class FeedUser implements BaseColumns {
        public static final String TABLE_NAME = "user";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String EMAIL = "email";
        // 1 if the record has to be updated in the server, else 0
        public static final String FOR_UPDATE= "for_update";
        public static final String USER_CREATED = "user_created";
        public static final String FROM_SERVER = "from_server";
    }
    
    /** Inner class that defines the User table contents */
    public static abstract class FeedList implements BaseColumns {
        public static final String TABLE_NAME = "list";
        public static final String USER = "user";
        public static final String PRODUCT = "product";
        public static final String IS_CHECKED = "is_checked";
    }
    
    /** Inner class that defines the Budget table contents */
    public static abstract class FeedBudget implements BaseColumns {
        public static final String TABLE_NAME = "budget";
        public static final String EXPENSE_CATEGORY = "expense_category";
        public static final String SPEND_LIMIT = "spend_limit";
        public static final String START_DATE = "start_date";
        public static final String END_DATE = "end_date";
        public static final String USER = "user";
        // if the user saves the same budget to the family members, 
        // this column saves the user that created the budget
        public static final String FAMILY_USER = "family_user";
        // 1 if the record has to be deleted in the server, else 0
        public static final String FOR_DELETION = "for_deletion";
        // 1 if the record has to be updated in the server, else 0
        public static final String FOR_UPDATE= "for_update";
        // 1 if the record is saved in the server, else 0
        public static final String ON_SERVER = "on_server";
        public static final String BUDGET_CREATED = "budget_created";
        // takes values 0,1,2. 1 if the budget is not surpassed, 1 if it is surpassed by 80%
        // and 2 if it is fully surpassed
        public static final String IS_SURPASSED = "is_surpassed";
    }
    
    /** Inner class that defines the Product table contents */
    public static abstract class FeedProduct implements BaseColumns {
        public static final String TABLE_NAME = "product";
        public static final String PRODUCT_CATEGORY = "product_category";
        public static final String NAME = "product_name";
        public static final String PRICE = "price";
        public static final String PURCHASE_DATE = "purchase_date";
        public static final String STORE = "store";
        public static final String USER = "user";
        // 1 if the record is saved in the server, else 0
        public static final String ON_SERVER = "on_server";
        public static final String PRODUCT_CREATED = "product_created";
    }
    
    /** Inner class that defines the Offer table contents */
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
    
    /** Inner class that defines the Family table contents */
    public static abstract class FeedFamily implements BaseColumns {
        public static final String TABLE_NAME = "family";
        public static final String MEMBER1 = "member1";
        public static final String MEMBER2 = "member2";
        public static final String CONFIRMED = "confirmed";
        public static final String FOR_DELETION = "for_deletion";
        // 1 if the record is saved in the server, else 0
        public static final String ON_SERVER = "on_server";
        public static final String FAMILY_CREATED = "family_created";
        // 1 if the record has to be updated in the server, else 0
        public static final String FOR_UPDATE= "for_update";
    }
    
    /** Inner class that defines the Store table contents */
    public static abstract class FeedStore implements BaseColumns {
        public static final String TABLE_NAME = "store";
        public static final String NAME = "store_name";
        public static final String ADDRESS = "address";
        public static final String VAT_NUMBER = "vat_number";
        public static final String STORE_CREATED = "user_created";
    } 
}