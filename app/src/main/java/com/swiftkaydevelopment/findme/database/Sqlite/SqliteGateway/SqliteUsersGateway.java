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
import android.util.Log;

import com.swiftkaydevelopment.findme.data.User;
import com.swiftkaydevelopment.findme.database.BaseSQLiteGateway;
import com.swiftkaydevelopment.findme.database.DatabaseContract;
import com.swiftkaydevelopment.findme.database.Sqlite.modules.SQLiteModule;
import com.swiftkaydevelopment.findme.database.gatewayInterfaces.UsersGateway;

/**
 * Created by Kevin Haines on 2/29/16.
 * Class Overview:
 */
public class SqliteUsersGateway extends BaseSQLiteGateway implements UsersGateway {

    public SqliteUsersGateway(SQLiteModule module) {
        super(module);
    }

    @Override
    public boolean insert(User user) {
        SQLiteDatabase db = getWritableDatabase();

        User existingUser = find(user.getOuid());
        if (existingUser == null) {
            long rows = db.insert(DatabaseContract.UserEntry.TABLE_NAME, null, userToContentValues(user));
            return rows != -1;
        } else {
            return update(user);
        }
    }

    @Override
    public boolean update(User user) {
        return false;
    }

    @Override
    public User find(String uid) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.query(DatabaseContract.UserEntry.TABLE_NAME, null, DatabaseContract.UserEntry.COLUMN_NAME_UID + " = ?",
                new String[]{uid}, null, null, null);
        return cursorToUser(c);
    }

    /**
     * Turns a User into ContentValues
     *
     * @param user User to convert
     * @return User as ContentValues
     */
    private ContentValues userToContentValues(User user) {
        ContentValues values = new ContentValues();

        values.put(DatabaseContract.UserEntry.COLUMN_NAME_UID, user.getOuid());
        values.put(DatabaseContract.UserEntry.COLUMN_NAME_ABOUT, user.getAboutMe());
        values.put(DatabaseContract.UserEntry.COLUMN_NAME_AGE, user.getAge());
        values.put(DatabaseContract.UserEntry.COLUMN_NAME_FIRST, user.getFirstname());
        values.put(DatabaseContract.UserEntry.COLUMN_NAME_LAST, user.getLastname());
        values.put(DatabaseContract.UserEntry.COLUMN_NAME_GENDER, user.getGender().toString());
        values.put(DatabaseContract.UserEntry.COLUMN_NAME_HASKIDS, user.hasKids);
        values.put(DatabaseContract.UserEntry.COLUMN_NAME_WANTSKIDS, user.wantsKids);
        values.put(DatabaseContract.UserEntry.COLUMN_NAME_PROFESSION, user.profession);
        values.put(DatabaseContract.UserEntry.COLUMN_NAME_ORIENTATION, user.getOrientation().toString());
        values.put(DatabaseContract.UserEntry.COLUMN_NAME_PROPICLOC, user.getPropicloc());
        values.put(DatabaseContract.UserEntry.COLUMN_NAME_WEED, user.weed);
        values.put(DatabaseContract.UserEntry.COLUMN_NAME_SCHOOL, user.school);
        values.put(DatabaseContract.UserEntry.COLUMN_NAME_RELATIONSHIP, user.mRelationshipStatus);
        return values;
    }

    /**
     * Creates a User based on the Cursor values
     *
     * @param c Cursor containing user
     * @return User
     */
    private User cursorToUser(Cursor c) {
        try {
            if (c != null && c.moveToFirst()) {
                User user = User.createUser();

                user.setFirstname(c.getString(c.getColumnIndexOrThrow(DatabaseContract.UserEntry.COLUMN_NAME_FIRST)));
                user.setLastname(c.getString(c.getColumnIndexOrThrow(DatabaseContract.UserEntry.COLUMN_NAME_LAST)));

                Log.e("usermodule", "firstname" + user.getFirstname());
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
