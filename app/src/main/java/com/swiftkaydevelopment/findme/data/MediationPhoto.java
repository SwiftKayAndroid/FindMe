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

public class MediationPhoto {
    public static final String TAG = "MediationPhoto";

    public String imgLocation;
    public String id;
    public String dataId;
    public String type;
    public String decision;


    public static MediationPhoto createFromJson(JSONObject object) {
        try {
            MediationPhoto photo = new MediationPhoto();
            photo.imgLocation = object.getString("pic");
            photo.id = object.getString("id");
            photo.dataId = object.getString("data_id");
            photo.type = object.getString("type");
            return photo;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
}
