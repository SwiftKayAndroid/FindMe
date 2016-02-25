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

import android.database.sqlite.SQLiteDatabase;

/**
 * Base gateway class to help common operations between gateways
 */
public abstract class BaseSQLiteGateway {

    protected DatabaseHelper mSqLiteModule;

    protected BaseSQLiteGateway(DatabaseHelper sqLiteModule) {
        this.mSqLiteModule = sqLiteModule;
    }

    protected SQLiteDatabase getReadableDatabase() {
        return mSqLiteModule.getReadableDatabase();
    }

    protected SQLiteDatabase getWritableDatabase() {
        return mSqLiteModule.getWritableDatabase();
    }
}