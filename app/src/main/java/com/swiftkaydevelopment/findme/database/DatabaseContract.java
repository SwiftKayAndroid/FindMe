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

    public static abstract class UserEntry {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_NAME_UID = "uid";
        public static final String COLUMN_NAME_FIRST = "firstname";
        public static final String COLUMN_NAME_LAST = "lastname";
        public static final String COLUMN_NAME_PROPICLOC = "propicloc";
        public static final String COLUMN_NAME_GENDER = "gender";
        public static final String COLUMN_NAME_AGE = "age";
        public static final String COLUMN_NAME_ORIENTATION = "orientation";
        public static final String COLUMN_NAME_RELATIONSHIP = "relationship_status";
        public static final String COLUMN_NAME_ABOUT = "about_me";
        public static final String COLUMN_NAME_HASKIDS = "has_kids";
        public static final String COLUMN_NAME_WANTSKIDS = "wants_kids";
        public static final String COLUMN_NAME_PROFESSION = "profession";
        public static final String COLUMN_NAME_SCHOOL = "school";
        public static final String COLUMN_NAME_WEED = "weed";
        public static final String COLUMN_NAME_LOCATION = "location";

        public static final String CREATE_TABLE = DatabaseContract.CREATE_TABLE +
                TABLE_NAME + " (" +
                COLUMN_NAME_UID +  TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_FIRST + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_LAST + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_PROPICLOC + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_GENDER + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_AGE + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_ORIENTATION + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_RELATIONSHIP + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_ABOUT + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_HASKIDS + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_WANTSKIDS + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_PROFESSION + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_SCHOOL + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_WEED + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_LOCATION + TEXT_TYPE +
                " )";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
