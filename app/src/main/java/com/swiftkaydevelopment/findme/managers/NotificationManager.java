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

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.activity.FriendsActivity;
import com.swiftkaydevelopment.findme.activity.ProfileActivity;
import com.swiftkaydevelopment.findme.data.Notification;
import com.swiftkaydevelopment.findme.data.PushData;
import com.swiftkaydevelopment.findme.data.User;

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
    private static final String TYPE_LIKE = "like";
    private static final String TYPE_COMMENT = "comment";

    private static String mUid;
    private static NotificationManager manager = null;
    private static Context mContext;

    private CopyOnWriteArrayList<NotificationsListener> mListeners = new CopyOnWriteArrayList<>();

    public static NotificationManager getInstance(Context context) {
        if (manager == null) {
            manager = new NotificationManager();
        }
        manager.mContext = context;
        return manager;
    }

    public void notifyNewPushNotification(Bundle data) {
        Log.e(TAG, data.toString());
        if (data != null) {
            String type = data.getString("type");

            if (type != null) {
                if (type.equals("friend_request")) {
                    try {
                        JSONObject jsonObject = new JSONObject(data.getString("user"));
                        User user = User.createUser("", mContext).createUserFromJson(jsonObject);
                        sendNotificationForFriendRequest(user);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (type.equals(TYPE_LIKE)) {
                    try {
                        JSONObject postData = new JSONObject(data.getString("post"));
                        JSONObject userData = new JSONObject(data.getString("user"));

                        PushData d = new PushData();
                        d.title = "New Like";
                        d.message = "Someone Liked your status";
                        d.resId = R.drawable.redfsmall;

                        Intent intent = ProfileActivity.createIntent(mContext, User.createUser(null, mContext).createUserFromJson(userData));
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        d.intent = PendingIntent.getActivity(mContext, 0, intent,
                                PendingIntent.FLAG_ONE_SHOT);
                        sendNotification(d);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (type.equals(TYPE_COMMENT)) {
                    try {
                        JSONObject postData = new JSONObject(data.getString("post"));
                        JSONObject userData = new JSONObject(data.getString("user"));

                        PushData d = new PushData();
                        d.title = "New Comment";
                        d.message = "Someone commented your status";
                        d.resId = R.drawable.redfsmall;

                        Intent intent = ProfileActivity.createIntent(mContext, User.createUser(null, mContext).createUserFromJson(userData));
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        d.intent = PendingIntent.getActivity(mContext, 0, intent,
                                PendingIntent.FLAG_ONE_SHOT);
                        sendNotification(d);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param data GCM message received.
     */
    public static void sendNotification(PushData data) {

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(data.resId)
                .setContentTitle(data.title)
                .setContentText(data.message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(data.intent);

        android.app.NotificationManager notificationManager =
                (android.app.NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param user User
     */
    public void sendNotificationForFriendRequest(User user) {
        Intent intent = FriendsActivity.createIntent(mContext);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.redfsmall)
                .setContentTitle("Friend Request")
                .setContentText("From: " + user.getFirstname() + " " + user.getLastname())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        android.app.NotificationManager notificationManager =
                (android.app.NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
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
