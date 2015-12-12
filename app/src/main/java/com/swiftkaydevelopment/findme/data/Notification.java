package com.swiftkaydevelopment.findme.data;

import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by Kevin Haines on 10/21/15.
 */
public class Notification implements Serializable {
    /**
     * this class will be responsible for storing data about a notification including type and where to direct user when
     * clicked.
     */
    public static final String TYPE_LIKE = "like";
    public static final String TYPE_COMMENT = "comment";
    public static final String TYPE_NEW_FRIEND = "new_friend";
    public static final String TYPE_NEW_MATCH = "match";

    public String type;
    public String description;
    public User user;
    public int resId;
    public Intent intent;

    public static Notification instance() {
        return new Notification();
    }


    /**
     * Creates a new notification from JSONObject
     * @param object JsonObject for the notification
     * @return This instance of a notification
     */
    public Notification createNotificationFromJson(JSONObject object) {
        try {
            type = object.getString("type");
            description = object.getString("description");
            //todo: we will have to create an individual post thing for this
            user = User.createUser(null, null).createUserFromJson(object.getJSONObject("data"));

            return this;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
