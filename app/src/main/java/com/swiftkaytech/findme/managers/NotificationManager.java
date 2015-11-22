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

package com.swiftkaytech.findme.managers;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.swiftkaytech.findme.R;
import com.swiftkaytech.findme.activity.FriendsActivity;
import com.swiftkaytech.findme.data.User;

import org.json.JSONException;
import org.json.JSONObject;

public class NotificationManager {
    private static final String TAG = "NotificationManager";

    private static String mUid;
    private static NotificationManager manager = null;
    private static Context mContext;

    public static NotificationManager getInstance(Context context) {
        if (manager == null) {
            manager = new NotificationManager();
        }
        manager.mContext = context;
        return manager;
    }

    public void notifyNewPushNotification(Bundle data) {
        if (data != null) {
            String type = data.getString("type");

            if (type != null && type.equals("friend_request")) {
                try {
                    JSONObject jsonObject = new JSONObject(data.getString("user"));
                    User user = User.createUser("", mContext).createUserFromJson(jsonObject);
                    sendNotificationForFriendRequest(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
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
}
