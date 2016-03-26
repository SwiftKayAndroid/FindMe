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

package com.swiftkaydevelopment.findme.data;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.squareup.picasso.Picasso;
import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.activity.FriendsActivity;
import com.swiftkaydevelopment.findme.data.datainterfaces.Notifiable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by Kevin Haines on 2/24/16.
 * Class Overview:
 */
public class FriendRequest implements Serializable, Notifiable {
    private static final String TAG = "friendRequest";

    User user;

    @Override
    public PushData getPushData(Bundle data, Context context) {
        PushData pushData = new PushData();
        pushData.title = "New Friend Request";
        try {
            JSONObject object = new JSONObject(data.getString("user"));
            User user = SimpleUser.createUserFromJson(object);
            if (user != null) {
                if (!TextUtils.isEmpty(user.getPropicloc())) {
                    pushData.icon = Picasso.with(context)
                            .load(user.getPropicloc())
                            .get();
                }
                pushData.message = user.getFirstname() + " sent you a friend request";
            }
        } catch (JSONException e) {
            e.printStackTrace();
            pushData.message = "Someone sent you a friend request";
        } catch (IOException e) {
            e.printStackTrace();
        }
        pushData.resId = R.mipmap.ic_person_add_white_24dp;
        pushData.notificationId = getNotificationId();
        pushData.intent = PushData.createPendingIntent(FriendsActivity.createIntent(context), context);
        return pushData;
    }

    @Override
    public int getNotificationId() {
        return 7545;
    }

    @Override
    public int getNotificationTypeCount() {
        return 0;
    }
}
