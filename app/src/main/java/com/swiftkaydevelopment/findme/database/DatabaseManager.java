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

import android.content.Context;

import com.swiftkaydevelopment.findme.data.User;
import com.swiftkaydevelopment.findme.database.Sqlite.SqliteModulesManager;
import com.swiftkaydevelopment.findme.database.Sqlite.modules.ModuleManager;
import com.swiftkaydevelopment.findme.database.modules.UsersModule;

import java.util.ArrayList;

/**
 * Created by Kevin Haines on 2/29/16.
 * Class Overview:
 */
public class DatabaseManager implements UsersModule, ModuleEventListener {

    private static DatabaseManager sInstance = null;
    private static ModuleManager sModuleManager = null;
    private UsersModule mUsersModule;

    /**
     * Gets the singleton instance of the DatabaseManager
     *
     * @param context Current calling context
     * @return singleton instance of DatabaseManager
     */
    public static DatabaseManager instance(Context context) {
        if (sInstance == null) {
            synchronized (DatabaseManager.class) {
                sInstance = new DatabaseManager(context.getApplicationContext());
                sModuleManager.initialize();
            }
        }

        return sInstance;
    }

    private DatabaseManager(Context context) {
        sModuleManager = new SqliteModulesManager(this, context);
    }

    @Override
    public void onInit() {
        mUsersModule = sModuleManager.getUsersModule();
    }

    @Override
    public boolean createUser(User user) {
        return mUsersModule.createUser(user);
    }

    @Override
    public boolean updateUser(User user) {
        return mUsersModule.updateUser(user);
    }

    @Override
    public User getUser(String uid) {
        return mUsersModule.getUser(uid);
    }

    @Override
    public ArrayList<User> getUsers() {
        return mUsersModule.getUsers();
    }
}
