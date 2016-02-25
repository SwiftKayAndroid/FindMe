/*
 *      Copyright (C) 2015 Kevin Haines
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */

package com.swiftkaydevelopment.findme.database;

/**
 * Created by Kevin Haines on 2/25/16.
 * Class Overview:
 */
public class DatabaseContract {
    public static final  int    DATABASE_VERSION    = 1;
    public static final  String DATABASE_NAME       = "database.db";
    private static final String TEXT_TYPE           = " TEXT";
    private static final String INTEGER_TYPE        = " INTEGER";
    private static final String COMMA_SEP           = ", ";
    private static final String REFERENCES          = " REFERENCES ";
    private static final String DEFAULT             = " DEFAULT ";
    private static final String PRIMARY_KEY         = " PRIMARY KEY ";
    private static final String FOREIGN_KEY         = " FOREIGN KEY ";
    private static final String AUTO_INCREMENT      = " AUTOINCREMENT";
    private static final String CREATE_TABLE        = "CREATE TABLE IF NOT EXISTS ";
    private static final String LEFT                = " left ";
    private static final String JOIN                = " join ";
    private static final String ON                  = " on ";
    private static final String ALTER_TABLE         = " ALTER TABLE ";
    private static final String ADD_COLUMN          = " ADD COLUMN ";
    private static final String UPDATE              = " UPDATE ";
    private static final String SET                 = " SET ";

//    public static abstract class UserProfileEntry {
//        public static final String TABLE_NAME = "user_profile";
//        public static final String COLUMN_NAME_ENTRY_ID = "id";
//        public static final String COLUMN_NAME_FIRST = "first";
//        public static final String COLUMN_NAME_LAST = "last";
//        public static final String COLUMN_NAME_MIDDLE = "middle";
//        public static final String COLUMN_NAME_TITLE = "title";
//        public static final String COLUMN_NAME_LAST_MODIFIED = "last_modified";
//        public static final String COLUMN_NAME_RESTRICTIONS = "restrictions";
//        public static final String COLUMN_NAME_USER = "user_id";
//
//        public static final String CREATE_TABLE = DatabaseContract.CREATE_TABLE +
//                TABLE_NAME + " (" +
//                COLUMN_NAME_ENTRY_ID +  INTEGER_TYPE + PRIMARY_KEY + AUTO_INCREMENT + COMMA_SEP +
//                COLUMN_NAME_FIRST + TEXT_TYPE + COMMA_SEP +
//                COLUMN_NAME_LAST + TEXT_TYPE + COMMA_SEP +
//                COLUMN_NAME_MIDDLE + TEXT_TYPE + COMMA_SEP +
//                COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
//                COLUMN_NAME_LAST_MODIFIED + TEXT_TYPE + COMMA_SEP +
//                COLUMN_NAME_RESTRICTIONS + INTEGER_TYPE + COMMA_SEP +
//                COLUMN_NAME_USER + TEXT_TYPE + COMMA_SEP +
//                FOREIGN_KEY + " (" + COLUMN_NAME_USER + ")" + REFERENCES + UserEntry.TABLE_NAME + "(" + UserEntry.COLUMN_NAME_USERNAME + ")" + " )";
//
//        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
//    }
}
