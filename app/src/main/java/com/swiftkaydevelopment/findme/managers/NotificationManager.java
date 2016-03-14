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
import android.os.AsyncTask;

import com.swiftkaydevelopment.findme.data.Notification;
import com.swiftkaydevelopment.findme.events.OnNotificationsRecieved;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class NotificationManager {

    private static final String TAG = "NotificationManager";

    private static NotificationManager manager = null;

    public static NotificationManager getInstance(Context context) {
        synchronized (NotificationManager.class) {
            if (manager == null) {
                manager = new NotificationManager();
            }
            return manager;
        }
    }

    /**
     * Gets a list of the user's notifications from the server
     *
     * @param uid User's id
     * @param lastpost Last notification recieved
     */
    public void getNotifications(String uid, String lastpost) {
        new GetNotificationTask(uid, lastpost).execute();
    }

    private class GetNotificationTask extends AsyncTask<Void, Void, ArrayList<Notification>> {
        String uid;
        String lastpost;

        public GetNotificationTask(String uid, String lastpost) {
            this.uid = uid;
            this.lastpost = lastpost;
        }

        @Override
        protected ArrayList<Notification> doInBackground(Void... params) {
            ConnectionManager connectionManager = new ConnectionManager();
            connectionManager.setMethod(ConnectionManager.POST);
            connectionManager.setUri("getnotificationsv_1_6_1.php");
            connectionManager.addParam("uid", uid);
            connectionManager.addParam("lastpost", lastpost);
            String result = connectionManager.sendHttpRequest();
            ArrayList<Notification> notifications = new ArrayList<>();

            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("notes");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        Notification n = Notification.instance().createNotificationFromJson(jsonArray.getJSONObject(i));
                        notifications.add(n);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return notifications;
        }

        @Override
        protected void onPostExecute(ArrayList<Notification> notifications) {
            super.onPostExecute(notifications);
            EventBus.getDefault().postSticky(new OnNotificationsRecieved(notifications));
        }
    }
}
