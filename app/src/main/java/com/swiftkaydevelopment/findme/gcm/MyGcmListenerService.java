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

package com.swiftkaydevelopment.findme.gcm;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.swiftkaydevelopment.findme.managers.NotificationManager;
import com.swiftkaydevelopment.findme.managers.MessagesManager;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        if (data != null) {
            Log.e(TAG, "Push Data: " + data.toString());
            String type = data.getString("type");
            Log.w(TAG, "incoming push notification");
            if (type != null) {
                if (type.equals("message")) {
                    MessagesManager.messageNotificationReceived(data, getApplicationContext());
                } else {
                    NotificationManager.getInstance(getApplication()).notifyNewPushNotification(data);
                }
            }
        }
    }
}
