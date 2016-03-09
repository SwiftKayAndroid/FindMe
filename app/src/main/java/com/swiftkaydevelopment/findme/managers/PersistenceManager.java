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

package com.swiftkaydevelopment.findme.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PersistenceManager {
    private static final String TAG = "PreferencesManager";
    private static final String EMAIL = "email";
    private static final String UID = "uid";
    private static final String PASSWORD = "password";
    private static final String ZIP = "zip";
    private static final String FIRSTNAME = "firstname";
    private static final String LASTNAME = "lastname";
    private static final String GENDER = "gender";
    private static final String LOOKING_FOR_GENDER = "looking_for_gender";
    private static final String PROPICLOC = "propicloc";

    private static String mUid;

    private static PersistenceManager sInstance = null;
    private static Context mContext;
    private static SharedPreferences prefs;

    public static PersistenceManager getInstance(Context context) {
        if (sInstance == null) {
            synchronized (PersistenceManager.class) {
                sInstance = new PersistenceManager();
            }
        }
        sInstance.mContext = context;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return sInstance;
    }

    private SharedPreferences.Editor getEditor() {
        return prefs.edit();
    }

    public void updateEmail(String email) {
        getEditor().putString(EMAIL, email).apply();
    }

    public void updatePropicloc(String picloc) {
        getEditor().putString(PROPICLOC, picloc).apply();
        invalidateMe();
    }

    private void invalidateMe() {
        UserManager.getInstance(null).invalidateMe();
    }

}
