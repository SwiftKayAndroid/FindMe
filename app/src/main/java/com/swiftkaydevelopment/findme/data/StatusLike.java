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

import com.swiftkaydevelopment.findme.R;
import com.swiftkaydevelopment.findme.activity.NotificationActivity;
import com.swiftkaydevelopment.findme.data.datainterfaces.Notifiable;

import java.io.Serializable;

/**
 * Created by Kevin Haines on 2/24/16.
 * Class Overview:
 */
public class StatusLike implements Serializable, Notifiable {

    @Override
    public PushData getPushData(Bundle data, Context context) {

//        JSONObject postData = new JSONObject(data.getString("post"));
//        JSONObject userData = new JSONObject(data.getString("user"));

        PushData pushData = new PushData();
        pushData.title = "New like";
        pushData.message = "Someone liked your status";
        pushData.intent = PushData.createPendingIntent(NotificationActivity.createIntent(context), context);
        pushData.notificationId = getNotificationId();
        pushData.resId = R.mipmap.ic_heart_black_24dp;
        return pushData;
    }

    @Override
    public int getNotificationId() {
        return 4569;
    }

    @Override
    public int getNotificationTypeCount() {
        return 0;
    }
}
