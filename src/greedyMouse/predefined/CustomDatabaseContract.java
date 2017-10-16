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
