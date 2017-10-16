/*
Copyright 2017 YANG-TUN-HUNG

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package greedyMouse.predefined;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import greedyMouse.predefined.CustomDatabaseContract.Entry;

/***The custom SQLiteOpenHelper for the Android application Greedy Mouse.***/
public class CustomDatabaseHelper extends SQLiteOpenHelper{
	private static final String TEXT_TYPE    = " TEXT",
								INTEGER_TYPE = " INTEGER",
								COMMA_SEP    = ",",
								
								SQL_CREATE_ENTRIES =
									"CREATE TABLE " + Entry.TABLE_NAME + " (" +
									Entry._ID + " INTEGER PRIMARY KEY," +
									Entry.COLUMN_NAME_ENTRY_NAME + TEXT_TYPE + COMMA_SEP +
									Entry.COLUMN_NAME_ENTRY_SCORE + INTEGER_TYPE +
									" )",
									
								SQL_DELETE_ENTRIES = 
									"DROP TABLE IF EXISTS " + Entry.TABLE_NAME,
									
								DATABASE_NAME = "GreedyMouseRanking.db";

    public static final int     DATABASE_VERSION = 6;
    
    public CustomDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
    	ContentValues values = new ContentValues();
    	
        db.execSQL(SQL_CREATE_ENTRIES);
 
        //Put some default records.
        values.put(Entry.COLUMN_NAME_ENTRY_NAME, "King Rat");
        values.put(Entry.COLUMN_NAME_ENTRY_SCORE, 50);
        
        db.insert(Entry.TABLE_NAME, null, values);
        
        values.put(Entry.COLUMN_NAME_ENTRY_NAME, "White Rat");
        values.put(Entry.COLUMN_NAME_ENTRY_SCORE, 40);
        
        db.insert(Entry.TABLE_NAME, null, values);
        
        values.put(Entry.COLUMN_NAME_ENTRY_NAME, "Greedy Rat");
        values.put(Entry.COLUMN_NAME_ENTRY_SCORE, 30);
        
        db.insert(Entry.TABLE_NAME, null, values);
        
        values.put(Entry.COLUMN_NAME_ENTRY_NAME, "Fat Rat");
        values.put(Entry.COLUMN_NAME_ENTRY_SCORE, 20);
        
        db.insert(Entry.TABLE_NAME, null, values);
        
        values.put(Entry.COLUMN_NAME_ENTRY_NAME, "Tiny Rat");
        values.put(Entry.COLUMN_NAME_ENTRY_SCORE, 10);
        
        db.insert(Entry.TABLE_NAME, null, values);
  
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}
