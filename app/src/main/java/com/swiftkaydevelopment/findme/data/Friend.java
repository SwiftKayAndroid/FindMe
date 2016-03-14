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
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.data.datainterfaces.Notifiable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Friend implements Notifiable {
    public static final String TAG = "Friend";

    @Override
    public PushData getPushData(Bundle data, Context context) {
        PushData pushData = new PushData();
        pushData.title = "New Friend";
        try {
            JSONObject object = new JSONObject(data.getString("user"));
            User user = SimpleUser.createUserFromJson(object);
            if (user != null) {
                pushData.icon = Picasso.with(context).load(user.getPropicloc())
                        .get();
                pushData.message = user.getFirstname() + " accepted your friend request";
            }
        } catch (JSONException e) {
            e.printStackTrace();
            pushData.message = "New Friend";
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(TAG, data.getString("user"));
        pushData.resId = R.mipmap.ic_person_add_white_24dp;

        return pushData;
    }

    @Override
    public int getNotificationId() {
        return 2267;
    }

    @Override
    public int getNotificationTypeCount() {
        return 0;
    }
}
