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

import com.swiftkaydevelopment.findme.data.Message;
import com.swiftkaydevelopment.findme.database.Sqlite.SqliteGateway.SqliteMessageGateway;
import com.swiftkaydevelopment.findme.database.Sqlite.SqliteGateway.SqliteUsersGateway;
import com.swiftkaydevelopment.findme.database.gatewayInterfaces.MessageGateway;
import com.swiftkaydevelopment.findme.database.modules.MessagesModule;

import java.util.ArrayList;

/**
 * Created by Kevin Haines on 3/3/16.
 * Class Overview:
 */
public class SqliteMessagesModule implements SQLiteModule, MessagesModule {
    private final SQLiteOpenHelper mSQLiteOpenHelper;
    private MessageGateway mMessageGateway;

    public SqliteMessagesModule(SQLiteOpenHelper sQLiteOpenHelper) {
        this.mSQLiteOpenHelper = sQLiteOpenHelper;
        this.mMessageGateway = new SqliteMessageGateway(this);
    }

    @Override
    public void create(SQLiteDatabase db) {

    }

    @Override
    public void upgrade(SQLiteDatabase db, int upgradeTo) {

    }

    @Override
    public void clearTables() {

    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return null;
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return null;
    }

    @Override
    public boolean createMessage(Message message) {
        return false;
    }

    @Override
    public boolean updateMessage(Message message) {
        return false;
    }

    @Override
    public boolean deleteMessage(Message message) {
        return false;
    }

    @Override
    public Message getMessage(String messageId) {
        return null;
    }

    @Override
    public ArrayList<Message> getMessages(String uid, String ouid) {
        return null;
    }
}
