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
import android.os.Bundle;
import android.util.Log;

import com.swiftkaydevelopment.findme.data.Notification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationManager {

    public interface NotificationsListener {
        void onNotificationsFetched(ArrayList<Notification> notifications);
    }
    private static final String TAG = "NotificationManager";

    private static NotificationManager manager = null;

    private CopyOnWriteArrayList<NotificationsListener> mListeners = new CopyOnWriteArrayList<>();

    public static NotificationManager getInstance(Context context) {
        synchronized (NotificationManager.class) {
            if (manager == null) {
                manager = new NotificationManager();
            }
            return manager;
        }
    }

    public void notifyNewPushNotification(Bundle data) {
        Log.e(TAG, data.toString());
            String type = data.getString("type");

            if (type != null) {
//                if (type.equals("view")) {
//                    PushData pd = new PushData();
//                    pd.title = "New Profile View";
//                    pd.message = "Someone viewed your profile";
//                    pd.resId = R.drawable.redfsmall;
//
//                    Intent intent = ProfileViewsActivity.createIntent(mContext);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//                    pd.intent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);
//                    sendNotification(pd);
//                }
            }
    }

    public void getNotifications(String uid, String lastpost) {
        new GetNotificationTask(uid, lastpost).execute();
    }

    public void addListener(NotificationsListener listener) {
        mListeners.addIfAbsent(listener);
    }

    public void removeListener(NotificationsListener listener) {
        mListeners.remove(listener);
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
            connectionManager.setUri("getnotifications.php");
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

            for (NotificationsListener l : mListeners) {
                if (l != null) {
                    l.onNotificationsFetched(notifications);
                }
            }
        }
    }
}
