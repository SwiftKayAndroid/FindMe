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
import com.swiftkaydevelopment.findme.activity.ProfileViewsActivity;
import com.swiftkaydevelopment.findme.data.datainterfaces.Notifiable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ProfileView implements Notifiable {
    public static final String TAG = "ProfileView";

    @Override
    public PushData getPushData(Bundle data, Context context) {
        PushData pushData = new PushData();
        try {
            JSONObject object = new JSONObject(data.getString("user"));
            User user = SimpleUser.createUserFromJson(object);
            if (user != null) {
                if (!TextUtils.isEmpty(user.getPropicloc())) {
                    pushData.icon = Picasso.with(context)
                            .load(user.getPropicloc())
                            .get();
                }
                pushData.message = user.getFirstname() + " just viewed your profile";
            }
        } catch (JSONException e) {
            e.printStackTrace();
            pushData.message = "Someone just viewed your profile";
        } catch (IOException e) {
            e.printStackTrace();
        }

        pushData.title = "New profile view";
        pushData.resId = R.mipmap.ic_eye_white_24dp;
        pushData.intent = PushData.createPendingIntent(ProfileViewsActivity.createIntent(context), context);
        pushData.notificationId = getNotificationId();
        return pushData;
    }

    @Override
    public int getNotificationId() {
        return 5002;
    }

    @Override
    public int getNotificationTypeCount() {
        return 0;
    }
}
