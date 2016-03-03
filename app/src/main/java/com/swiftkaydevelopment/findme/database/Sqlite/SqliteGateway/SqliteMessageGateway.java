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

package com.swiftkaydevelopment.findme.database.Sqlite.SqliteGateway;

import com.swiftkaydevelopment.findme.data.Message;
import com.swiftkaydevelopment.findme.database.BaseSQLiteGateway;
import com.swiftkaydevelopment.findme.database.Sqlite.modules.SQLiteModule;
import com.swiftkaydevelopment.findme.database.gatewayInterfaces.MessageGateway;

import java.util.ArrayList;

/**
 * Created by Kevin Haines on 3/3/16.
 * Class Overview:
 */
public class SqliteMessageGateway extends BaseSQLiteGateway implements MessageGateway {

    public SqliteMessageGateway(SQLiteModule sqLiteModule) {
        super(sqLiteModule);
    }

    @Override
    public boolean insert(Message message) {
        return false;
    }

    @Override
    public boolean update(Message message) {
        return false;
    }

    @Override
    public boolean delete(Message message) {
        return false;
    }

    @Override
    public Message find(String messageId) {
        return null;
    }

    @Override
    public ArrayList<Message> findAll(String uid, String ouid) {
        return null;
    }
}
