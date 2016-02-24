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

/**
 * Created by Kevin Haines on 2/24/16.
 * Class Overview:
 */
public interface Notifiable {

    /**
     * This will cause the data object to create and show a
     * notification.
     *
     * @param data push notification data
     */
    PushData getPushData(Bundle data, Context context);

    /**
     * Gets the notification type id
     *
     * @return int id of the notification type
     */
    int getNotificationId();

    /**
     * Gets the total number of existing notifications for
     * the notification type.
     *
     * @return number of current notifications for the type
     */
    int getNotificationTypeCount();
}
