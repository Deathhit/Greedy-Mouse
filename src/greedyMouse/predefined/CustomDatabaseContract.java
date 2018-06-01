package greedyMouse.predefined;

import android.provider.BaseColumns;

/***Custom database contract for the Android application Greedy Mouse.***/
public class CustomDatabaseContract{
    // To prevent someone from accidentally instantiating the contract class,
    private CustomDatabaseContract(){}

    /***Inner class that defines the table contents.***/
    public static abstract class Entry implements BaseColumns {
        public static final String 
        	TABLE_NAME = "ranking",
        	COLUMN_NAME_ENTRY_NAME  = "name",
        	COLUMN_NAME_ENTRY_SCORE = "score"; 
    }
}
