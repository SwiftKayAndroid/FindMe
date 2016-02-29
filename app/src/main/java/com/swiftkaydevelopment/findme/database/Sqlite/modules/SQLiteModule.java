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

package com.swiftkaydevelopment.findme.database.Sqlite.modules;

import android.database.sqlite.SQLiteDatabase;

/**
 * Required implementation for SQLite modules to retrieve the read/write db
 */
public interface SQLiteModule {
    void create(SQLiteDatabase db);
    void upgrade(SQLiteDatabase db, int upgradeTo);
    void clearTables();
    SQLiteDatabase getWritableDatabase();
    SQLiteDatabase getReadableDatabase();
}
