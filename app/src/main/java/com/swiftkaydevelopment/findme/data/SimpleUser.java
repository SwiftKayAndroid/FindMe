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

import org.json.JSONException;
import org.json.JSONObject;

public class SimpleUser extends User {
    public static final String TAG = "SimpleUser";


    public static SimpleUser createUserFromJson(JSONObject object) {
        SimpleUser user = new SimpleUser();
        try {
            user.mOuid = object.getString("uid");
            user.mPropicloc = object.getString("pic");
            user.setFirstname(object.getString("fn"));
            user.mLastname = object.getString("ln");
            user.mGender = setGenderFromString(object.getString("ge"));
            user.mOrientation = setOrientationFromString(object.getString("ori"));
            user.mAge = Integer.parseInt(object.getString("age"));
            return user;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
