package com.swiftkaytech.findme.managers;

import android.content.Context;

import com.swiftkaytech.findme.data.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Haines on 10/21/15.
 */
public class UserManager {
    private static final String TAG = "UserManager";
    private static String mUid;
    private static Context mContext;
    private static User me;

    private static UserManager manager = null;


    public static UserManager getInstance(String uid, Context context){
        if (manager == null) {
            manager = new UserManager();
        }
        manager.mUid = uid;
        manager.mContext = context;

        return manager;
    }

    public User me() {
        if (me == null) {
            me = User.createUser(mUid, mContext).fetchUser(mUid, mContext);
        }
        return me;
    }
}
