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

import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.swiftkaydevelopment.findme.data.Comment;
import com.swiftkaydevelopment.findme.data.FriendRequest;
import com.swiftkaydevelopment.findme.data.Message;
import com.swiftkaydevelopment.findme.data.datainterfaces.Notifiable;
import com.swiftkaydevelopment.findme.data.PushData;
import com.swiftkaydevelopment.findme.data.StatusLike;

public class PushNotificationManager {
    private static final String TAG = "PushNotificationManager";

    private static final String TYPE_MESSAGE = "message";
    private static final String TYPE_FRIEND_REQUEST = "friend_request";
    private static final String TYPE_LIKE = "like";
    private static final String TYPE_COMMENT = "comment";


    private static PushNotificationManager sInstance = null;
    private Context mContext;

    public static PushNotificationManager getInstance(Context context) {
        synchronized (PushNotificationManager.class) {
            if (sInstance == null) {
                sInstance = new PushNotificationManager(context);
            }
            return sInstance;
        }
    }

    /**
     * Private constructor for Singleton instance
     *
     * @param context context
     */
    private PushNotificationManager(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * Called when a Push Notification is received
     *
     * @param data Bundle Push notification data
     */
    public void onReceivedPushNotification(Bundle data) {
        //Note: data will never be null at this point
        Notifiable notify = getNotifiable(data);
        if (notify != null) {
            sendNotification(notify.getPushData(data, mContext));
        }
    }

    /**
     * Gets the encapsulated object for the Notifiable interface
     *
     * @param data Bundle push notification data
     * @return encapsulated Notifiable object
     */
    private Notifiable getNotifiable(Bundle data) {
        String type = data.getString("type");
        if (TextUtils.isEmpty(type)) {
            return null;
        }

        if (type.equals(TYPE_MESSAGE)) {
            return Message.instance();
        } else if (type.equals(TYPE_FRIEND_REQUEST)) {
            return new FriendRequest();
        } else if (type.equals(TYPE_LIKE)) {
            return new StatusLike();
        } else if (type.equals(TYPE_COMMENT)) {
            return new Comment();
        }

        return null;
    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param data GCM message received.
     */
    public void sendNotification(PushData data) {

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

        notificationManager.notify(data.notificationId, notificationBuilder.build());
    }
}
