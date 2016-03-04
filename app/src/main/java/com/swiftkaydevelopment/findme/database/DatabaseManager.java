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

import com.swiftkaydevelopment.findme.data.Message;
import com.swiftkaydevelopment.findme.data.User;
import com.swiftkaydevelopment.findme.database.Sqlite.SqliteModulesManager;
import com.swiftkaydevelopment.findme.database.Sqlite.modules.ModuleManager;
import com.swiftkaydevelopment.findme.database.modules.MessagesModule;
import com.swiftkaydevelopment.findme.database.modules.UsersModule;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Kevin Haines on 2/29/16.
 * Class Overview:
 */
public class DatabaseManager implements UsersModule, MessagesModule, ModuleEventListener {

    private static DatabaseManager sInstance = null;
    private static ModuleManager sModuleManager = null;
    private UsersModule mUsersModule;
    private MessagesModule mMessageModule;

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
        mMessageModule = sModuleManager.getMessagesModule();
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

    @Override
    public void clearUsers() {
        mUsersModule.clearUsers();
    }

    @Override
    public boolean createMessage(Message message) {
        return mMessageModule.createMessage(message);
    }

    @Override
    public boolean updateMessage(Message message) {
        return mMessageModule.updateMessage(message);
    }

    @Override
    public boolean deleteMessage(Message message) {
        return mMessageModule.deleteMessage(message);
    }

    @Override
    public Message getMessage(String messageId) {
        Message message = mMessageModule.getMessage(messageId);
        User user = mUsersModule.getUser(message.getOuid());
        if (user == null) {
            return null;
        } else {
            message.setUser(user);
        }
        return message;
    }

    @Override
    public ArrayList<Message> getMessages(String uid, String ouid) {
        ArrayList<Message> messages = mMessageModule.getMessages(uid, ouid);
        Iterator<Message> iterator = messages.iterator();

        while (iterator.hasNext()) {
            Message message = iterator.next();
            User user = mUsersModule.getUser(message.getOuid());
            if (user != null) {
                message.setUser(user);
            } else {
                iterator.remove();
            }
        }

        return messages;
    }
}
