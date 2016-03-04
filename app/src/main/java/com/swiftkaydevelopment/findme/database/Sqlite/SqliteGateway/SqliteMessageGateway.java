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

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.swiftkaydevelopment.findme.data.Message;
import com.swiftkaydevelopment.findme.database.BaseSQLiteGateway;
import com.swiftkaydevelopment.findme.database.DatabaseContract;
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
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = messageToContentValues(message);

        Message msg = find(message.getMessageId());
        if (msg != null) {
           return update(message);
        } else {
            return db.insert(DatabaseContract.MessageEntry.TABLE_NAME, null, values) != -1;
        }
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
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(DatabaseContract.MessageEntry.TABLE_NAME, null,
                DatabaseContract.MessageEntry.COLUMN_NAME_MESSAGE_ID + " = ?",
                new String[]{messageId}, null, null, null);
        Message message = null;
            if (c != null && c.moveToFirst()) {
                message = cursorToMessage(c);
            }

        if (c != null) {
            c.close();
        }
        return message;
    }

    @Override
    public ArrayList<Message> findAll(String uid, String ouid) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.query(DatabaseContract.MessageEntry.TABLE_NAME, null,
                DatabaseContract.MessageEntry.COLUMN_NAME_OUID + " = ?",
                new String[]{ouid}, null, null, null);
        return null;
    }

    /**
     * Turns a message into ContentValues
     *
     * @param message Message to convert
     * @return Content values from message
     */
    private ContentValues messageToContentValues(Message message) {
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.MessageEntry.COLUMN_NAME_MESSAGE_ID, message.getMessageId());
        values.put(DatabaseContract.MessageEntry.COLUMN_NAME_MESSAGE, message.getMessage());
        values.put(DatabaseContract.MessageEntry.COLUMN_NAME_DELETED_STATUS, message.getDeletedStatus());
        //todo:
        values.put(DatabaseContract.MessageEntry.COLUMN_NAME_IMAGE_LOC, "");
        values.put(DatabaseContract.MessageEntry.COLUMN_NAME_OUID, message.getOuid());
        values.put(DatabaseContract.MessageEntry.COLUMN_NAME_SEEN_STATUS, message.getSeenStatus());
        values.put(DatabaseContract.MessageEntry.COLUMN_NAME_SENDER_ID, message.getSenderId());
        values.put(DatabaseContract.MessageEntry.COLUMN_NAME_TAG, message.getTag());
        values.put(DatabaseContract.MessageEntry.COLUMN_NAME_THREAD_ID, message.getThreadId());
        values.put(DatabaseContract.MessageEntry.COLUMN_NAME_TIME, message.getTime());

        return values;
    }

    /**
     * Turns a cursor into a message
     *
     * @param c Cursor to convert
     * @return message from cursor
     */
    private Message cursorToMessage(Cursor c) {
        Message message = Message.instance();
        message.setMessageId(c.getString(c.getColumnIndexOrThrow(DatabaseContract.MessageEntry.COLUMN_NAME_MESSAGE_ID)));
        message.setMessage(c.getString(c.getColumnIndexOrThrow(DatabaseContract.MessageEntry.COLUMN_NAME_MESSAGE)));
        message.setDeletedStatus(c.getInt(c.getColumnIndexOrThrow(DatabaseContract.MessageEntry.COLUMN_NAME_DELETED_STATUS)));
        message.setOuid(c.getString(c.getColumnIndexOrThrow(DatabaseContract.MessageEntry.COLUMN_NAME_OUID)));
        message.setSeenStatus(c.getInt(c.getColumnIndexOrThrow(DatabaseContract.MessageEntry.COLUMN_NAME_SEEN_STATUS)));
        message.setSenderId(c.getString(c.getColumnIndexOrThrow(DatabaseContract.MessageEntry.COLUMN_NAME_SENDER_ID)));
        message.setTag(c.getString(c.getColumnIndexOrThrow(DatabaseContract.MessageEntry.COLUMN_NAME_TAG)));
        message.setThreadId(c.getString(c.getColumnIndexOrThrow(DatabaseContract.MessageEntry.COLUMN_NAME_THREAD_ID)));
        message.setTime(c.getString(c.getColumnIndexOrThrow(DatabaseContract.MessageEntry.COLUMN_NAME_TIME)));
        return message;
    }
}
