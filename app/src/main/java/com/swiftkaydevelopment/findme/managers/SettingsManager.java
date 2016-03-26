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

import android.os.AsyncTask;

public class SettingsManager {
    public static final String TAG = "SettingsManager";

    private static SettingsManager sInstance = null;

    public static SettingsManager instance() {
        synchronized (SettingsManager.class) {
            if (sInstance == null) {
                sInstance = new SettingsManager();
            }
            return sInstance;
        }
    }

    /**
     * Updates the mediate photos setting on the server
     *
     * @param mediatePhotos yes or no
     * @param uid User's unique id
     */
    public void updateMediationSettings(String mediatePhotos, String uid) {
        new UpdateMediationSettings(uid, mediatePhotos).execute();
    }

    private class UpdateMediationSettings extends AsyncTask<Void, Void, Void> {
        String uid;
        String mediateSettings;

        public UpdateMediationSettings(String uid, String mediateSettings) {
            this.uid = uid;
            this.mediateSettings = mediateSettings;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.addParam("uid", uid);
            connectionManager.addParam("mediate", mediateSettings);
            connectionManager.setUri("updatemediatephotos.php");
            connectionManager.sendHttpRequest();

            return null;
        }
    }
}
