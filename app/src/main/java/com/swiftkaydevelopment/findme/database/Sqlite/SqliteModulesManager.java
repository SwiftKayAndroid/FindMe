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

package com.swiftkaydevelopment.findme.database.Sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.swiftkaydevelopment.findme.database.DatabaseContract;
import com.swiftkaydevelopment.findme.database.ModuleEventListener;
import com.swiftkaydevelopment.findme.database.Sqlite.modules.ModuleManager;
import com.swiftkaydevelopment.findme.database.Sqlite.modules.SqliUsersModule;
import com.swiftkaydevelopment.findme.database.modules.UsersModule;

/**
 * Created by Kevin Haines on 2/29/16.
 * Class Overview:
 */
public class SqliteModulesManager extends SQLiteOpenHelper implements ModuleManager {

    private SqliUsersModule mUsersModule;
    private ModuleEventListener mModuleEventListener;

    public SqliteModulesManager(ModuleEventListener listener, Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
        this.mModuleEventListener = listener;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        mUsersModule.create(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mUsersModule.upgrade(db, newVersion);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        mUsersModule = new SqliUsersModule(this);
    }

    @Override
    public UsersModule getUsersModule() {
        return mUsersModule;
    }

    @Override
    public void initialize() {
        getWritableDatabase();
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        if (null != mModuleEventListener) {
            mModuleEventListener.onInit();
        }
    }
}
