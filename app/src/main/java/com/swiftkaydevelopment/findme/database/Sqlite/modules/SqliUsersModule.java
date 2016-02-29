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
import android.database.sqlite.SQLiteOpenHelper;

import com.swiftkaydevelopment.findme.data.User;
import com.swiftkaydevelopment.findme.database.DatabaseContract;
import com.swiftkaydevelopment.findme.database.Sqlite.SqliteGateway.SqliteUsersGateway;
import com.swiftkaydevelopment.findme.database.gatewayInterfaces.UsersGateway;
import com.swiftkaydevelopment.findme.database.modules.UsersModule;

/**
 * Created by Kevin Haines on 2/29/16.
 * Class Overview:
 */
public class SqliUsersModule implements SQLiteModule, UsersModule {
    private final SQLiteOpenHelper mSQLiteOpenHelper;

    private UsersGateway mUsersGateway;

    public SqliUsersModule(SQLiteOpenHelper sQLiteOpenHelper) {
        this.mSQLiteOpenHelper = sQLiteOpenHelper;
        this.mUsersGateway = new SqliteUsersGateway(this);
    }

    @Override
    public void create(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.UserEntry.CREATE_TABLE);
    }

    @Override
    public void upgrade(SQLiteDatabase db, int upgradeTo) {

    }

    @Override
    public void clearTables() {

    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return mSQLiteOpenHelper.getWritableDatabase();
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return mSQLiteOpenHelper.getReadableDatabase();
    }

    @Override
    public boolean createUser(User user) {
        return mUsersGateway.insert(user);
    }

    @Override
    public boolean updateUser(User user) {
        return mUsersGateway.update(user);
    }

    @Override
    public User getUser(String uid) {
        return mUsersGateway.find(uid);
    }
}
